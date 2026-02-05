# CustomEnchantment Architecture

This document provides a comprehensive overview of the CustomEnchantment plugin architecture, including module organization, data flow, and system interactions.

## Table of Contents

- [Overview](#overview)
- [Module System](#module-system)
- [Core Components](#core-components)
- [Data Flow](#data-flow)
- [Player System](#player-system)
- [Enchantment Execution Flow](#enchantment-execution-flow)
- [Task System](#task-system)
- [Configuration Loading](#configuration-loading)
- [Integration Points](#integration-points)
- [Package Structure](#package-structure)

---

## Overview

CustomEnchantment is a Minecraft Bukkit/Spigot plugin that provides a highly configurable custom enchantment system. The plugin follows a modular architecture where each major feature is encapsulated in its own module.

### Key Design Principles

1. **Modular Architecture** - Features are organized into independent modules
2. **Expansion Pattern** - Player data uses an expansion system for extensibility
3. **Registration Pattern** - Effects, conditions, and items use registration for extensibility
4. **Task-Based Execution** - Async and sync task queues for performance
5. **Configuration-Driven** - All enchantments defined via YAML configuration

---

## Module System

The plugin uses `PluginModule<CustomEnchantment>` as the base class for all modules. Modules are registered in a specific order during plugin enable.

### Module Registration Order

```java
// In CustomEnchantment.registerModules()
1.  FeatureModule      // Core features
2.  CustomMenuModule   // Custom menu integration
3.  AttributeModule    // Custom attribute types
4.  FilterModule       // Item/entity filters
5.  CommandModule      // Commands registration
6.  ItemModule         // Item factories
7.  PlayerModule       // Player expansions
8.  ExecuteModule      // Item executors
9.  EnchantModule      // Effects and conditions
10. ConfigModule       // Configuration loading
11. TaskModule         // Background tasks
12. GuardModule        // Guard mob system
13. DatabaseModule     // SQLite database
14. MenuModule         // Menu views
15. PlaceholderModule  // PlaceholderAPI integration
16. ListenerModule     // Event listeners
```

### Module Lifecycle

```
Plugin Enable
    └── registerModules()
        └── For each module:
            └── module.onEnable()

Plugin Disable
    └── For each module (reverse order):
        └── module.onDisable()
```

---

## Core Components

### Main Plugin Class

**Source:** `CustomEnchantment.java`

The main plugin class holds references to all modules and global data maps:

| Property | Type | Description |
|----------|------|-------------|
| `ceEnchantMap` | `CEEnchantMap` | All loaded enchantments |
| `ceGroupMap` | `CEGroupMap` | Rarity group definitions |
| `ceItemStorageMap` | `CEItemStorageMap` | Stored item templates |
| `mainConfig` | `MainConfig` | Main configuration |

### Data Maps

```
CustomEnchantment (singleton)
    ├── CEEnchantMap          # Map<String, CEEnchant>
    ├── CEGroupMap            # Map<String, CEGroup>
    ├── CEItemStorageMap      # Map<String, ItemStack>
    ├── CEArtifactGroupMap    # Artifact group definitions
    ├── CESigilGroupMap       # Sigil group definitions
    └── CEOutfitGroupMap      # Outfit group definitions
```

---

## Data Flow

### Enchantment Loading Flow

```
Plugin Enable
    └── ConfigModule.onEnable()
        ├── Load groups.yml → CEGroupMap
        ├── Load enchantment/*.yml
        │   └── For each enchantment:
        │       ├── Parse CEDisplay (name, description, lore)
        │       ├── Parse CELevelMap
        │       │   └── For each level:
        │       │       └── Parse CEFunction list
        │       │           ├── Parse Condition
        │       │           ├── Parse Effect
        │       │           └── Parse Option
        │       └── Create CEEnchant → CEEnchantMap
        └── Load items.yml → CEItemStorageMap
```

### Enchantment Execution Flow

```
Game Event (Attack, Defense, etc.)
    └── Listener (PlayerListener, EntityListener, etc.)
        └── CECallerBuilder.build()
            └── CECaller.call()
                └── For each EquipSlot:
                    └── callEquipSlot()
                        └── For each CEEnchantSimple:
                            └── callCE()
                                └── For each CEFunction:
                                    └── callCEFunction()
                                        ├── Check CEType match
                                        ├── Check active slot
                                        ├── Check chance
                                        ├── Check cooldown
                                        ├── Check conditions
                                        ├── Execute effects
                                        └── Apply options
```

---

## Player System

### CEPlayer Architecture

The `CEPlayer` class uses an expansion pattern for extensibility. Each expansion handles a specific aspect of player data.

```
CEPlayer
    ├── PlayerStorage           # Persistent data (file-based)
    ├── PlayerEquipment         # Equipment tracking
    ├── PlayerTemporaryStorage  # Session data (NUMBER_STORAGE, TEXT_STORAGE)
    ├── PlayerVanillaAttribute  # Bukkit attribute modifiers
    ├── PlayerCustomAttribute   # Custom attribute modifiers
    ├── PlayerPotion            # Permanent potion effects
    ├── PlayerSet               # Equipment set tracking
    ├── PlayerCECooldown        # Enchantment cooldowns
    ├── PlayerCEManager         # Cancel/lock management
    ├── PlayerAbility           # Abilities (dash, flash, double jump)
    ├── PlayerExtraSlot         # Extra equipment slots (artifacts)
    ├── PlayerMobBonus          # Mob kill bonuses
    ├── PlayerBlockBonus        # Block break bonuses
    ├── PlayerSpecialMining     # Mining abilities (telepathy, vein, etc.)
    ├── PlayerNameTag           # Name tag customization
    └── PlayerGem               # Gem socket management
```

### Player Lifecycle

```
Player Join
    └── CEPlayer created
        └── For each registered expansion:
            └── expansion.onJoin()
        └── CEPlayer.register() → CEPlayerMap

Player Quit
    └── CEPlayer.onQuit()
        └── For each expansion (reverse order):
            └── expansion.onQuit()
        └── CEPlayer.unregister()
```

### Expansion Registration

**Source:** `PlayerModule.java`

```java
// Register expansion classes
CEPlayerExpansionRegister.register(PlayerStorage.class);
CEPlayerExpansionRegister.register(PlayerEquipment.class);
// ... more expansions
```

---

## Enchantment Execution Flow

### CECaller Chain

The `CECaller` class orchestrates enchantment execution using a fluent builder pattern:

```java
CECaller.instance()
    .setCEType(CEType.ATTACK)
    .setCaller(player)
    .setData(functionData)
    .setEquipSlotList(equipSlots)
    .setWeaponMap(weapons)
    .resetResult()
    .call();
```

### Execution Steps

1. **Type Check** - Function's CEType must match event type
2. **Active Slot Check** - Equipment must be in active slot
3. **Lock Check** - Enchantment must not be locked/cancelled
4. **Chance Check** - Random chance must succeed
5. **Cooldown Check** - Cooldown must have expired
6. **Condition Check** - All conditions must pass
7. **Effect Execution** - Execute all effects
8. **Option Application** - Apply damage/defense modifiers

### Break Flags

Functions can control execution flow with break flags:

| Flag | Description |
|------|-------------|
| `true-condition-break` | Stop if conditions pass |
| `false-condition-break` | Stop if conditions fail |
| `true-chance-break` | Stop if chance succeeds |
| `false-chance-break` | Stop if chance fails |
| `timeout-cooldown-break` | Stop when cooldown expires |
| `in-cooldown-break` | Stop if still in cooldown |

---

## Task System

### Background Tasks

**Source:** `TaskModule.java`

The plugin runs multiple background tasks for various systems:

| Task | Interval | Async | Purpose |
|------|----------|-------|---------|
| `EffectExecuteTask` | 1 tick | No | Execute queued effects (sync) |
| `EffectExecuteTask` | 1 tick | Yes | Execute queued effects (async) |
| `CEPlayerTask` | 20 ticks | No | Player tick updates |
| `CECallerTask` | 1 tick | No | Process caller queue |
| `CEExtraSlotTask` | 4 ticks | No | Extra slot updates |
| `RecalculateAttributeTask` | 1 tick | Yes | Attribute recalculation |
| `RegenerationTask` | 4 ticks | No | Health regeneration |
| `SlowResistanceTask` | 4 ticks | No | Slow resistance checks |
| `SpecialMiningTask` | 1 tick | No | Mining abilities |
| `BlockTask` | 1 tick | No | Block break processing |
| `ArrowTask` | 20 ticks | No | Arrow tracking |
| `SaveTask` | 15 min | No | Auto-save player data |
| `ExpTask` | 10 sec | No | Experience updates |
| `PowerAsyncTask` | 20 ticks | Yes | Power calculations |

### Effect Task Separation

Effects are separated into sync and async queues:

```java
// In CustomEnchantment
public void addEffectTask(EffectTaskSeparate effectTask) {
    getTaskModule().getAsyncEffectExecuteTask()
        .addEffectDataList(effectTask.getEffectAsyncList());
    getTaskModule().getEffectExecuteTask()
        .addEffectDataList(effectTask.getEffectList());
}
```

---

## Configuration Loading

### File Structure

```
plugins/CustomEnchantment/
├── config.yml              # Main configuration
├── messages.yml            # Message templates
├── groups.yml              # Rarity group definitions
├── items.yml               # Stored item templates
├── tinkerer.yml            # Tinkerer configuration
├── book-craft.yml          # Book crafting recipes
├── book-upgrade.yml        # Book upgrade configuration
├── artifact-upgrade.yml    # Artifact upgrade configuration
├── artifact-groups.yml     # Artifact group definitions
├── sigil-groups.yml        # Sigil group definitions
├── outfit-groups.yml       # Outfit group definitions
├── data.db                 # SQLite database
├── enchantment/            # Enchantment configurations
│   ├── sword.yml
│   ├── bow.yml
│   ├── helmet.yml
│   └── ...
├── weapon/                 # Weapon configurations
├── skin/                   # Skin configurations
├── outfit/                 # Outfit configurations
├── artifact/               # Artifact configurations
├── menu/                   # Custom menu configurations
├── storage/                # Stored items
└── data/
    └── player/             # Player data files
```

### Configuration Classes

| Class | File | Purpose |
|-------|------|---------|
| `MainConfig` | config.yml | Main settings |
| `CEEnchantConfig` | enchantment/*.yml | Enchantment definitions |
| `CEEnchantGroupConfig` | groups.yml | Rarity groups |
| `BookCraftConfig` | book-craft.yml | Book crafting |
| `BookUpgradeConfig` | book-upgrade.yml | Book upgrading |
| `TinkererConfig` | tinkerer.yml | Tinkerer system |
| `VanillaItemConfig` | - | Vanilla item support |

---

## Integration Points

### External Plugin Support

**Source:** `ListenerModule.java`

| Plugin | Integration |
|--------|-------------|
| StackMob | Mob stacking death handling |
| CustomMenu | GUI menu system |
| mcMMO | mcMMO skill integration |
| CustomFarm | Farm system integration |
| PlaceholderAPI | Placeholder support |
| Factions | Faction relation checks |
| CombatLogX | Combat logging |
| Citizens | NPC support |

### API Access

```java
// Get CEPlayer
CEPlayer cePlayer = CEAPI.getCEPlayer(player);

// Get enchantment
CEEnchant enchant = CustomEnchantment.instance()
    .getCeEnchantMap().get("enchantName");

// Get group
CEGroup group = CustomEnchantment.instance()
    .getCeGroupMap().get("groupName");
```

---

## Package Structure

```
com.bafmc.customenchantment/
├── CustomEnchantment.java      # Main plugin class
├── CEEnchantMap.java           # Enchantment registry
├── CEGroupMap.java             # Group registry
├── CEPlayerMap.java            # Player registry
├── CEItemStorageMap.java       # Item storage
│
├── api/                        # Public API
│   ├── CEAPI.java              # Main API class
│   ├── MaterialData.java       # Material utilities
│   ├── MaterialList.java       # Material list handling
│   ├── EquipSlotAPI.java       # Equipment slot utilities
│   ├── ParticleAPI.java        # Particle effects
│   └── ...
│
├── attribute/                  # Attribute system
│   ├── AttributeModule.java
│   ├── AttributeCalculate.java
│   ├── CustomAttributeType.java
│   └── RangeAttribute.java
│
├── command/                    # Commands
│   ├── CommandModule.java
│   ├── CustomEnchantmentCommand.java
│   └── Command*.java
│
├── config/                     # Configuration
│   ├── ConfigModule.java
│   ├── AbstractConfig.java
│   ├── MainConfig.java
│   └── *Config.java
│
├── database/                   # Database
│   ├── DatabaseModule.java
│   └── Database.java
│
├── enchant/                    # Core enchantment system
│   ├── EnchantModule.java
│   ├── CEEnchant.java          # Enchantment data
│   ├── CELevel.java            # Level data
│   ├── CELevelMap.java         # Level mapping
│   ├── CEFunction.java         # Function (trigger)
│   ├── CEFunctionData.java     # Execution context
│   ├── CECaller.java           # Execution orchestrator
│   ├── CECallerBuilder.java    # Builder pattern
│   ├── CEType.java             # Trigger types enum
│   ├── Condition.java          # Condition container
│   ├── Effect.java             # Effect container
│   ├── Option.java             # Option container
│   ├── condition/              # Condition implementations
│   │   └── Condition*.java
│   └── effect/                 # Effect implementations
│       └── Effect*.java
│
├── event/                      # Custom events
│   └── CEPlayerStatsModifyEvent.java
│
├── execute/                    # Item executors
│   ├── ExecuteModule.java
│   └── *Execute.java
│
├── feature/                    # Feature modules
│   ├── FeatureModule.java
│   ├── item/                   # Item features
│   └── other/                  # Other features
│
├── filter/                     # Filtering system
│   ├── FilterModule.java
│   ├── FilterRegister.java
│   └── CEWeaponFilter.java
│
├── guard/                      # Guard mob system
│   ├── GuardModule.java
│   ├── GuardManager.java
│   ├── GuardSetting.java
│   └── ...
│
├── item/                       # Item system
│   ├── ItemModule.java
│   ├── CEItemType.java         # Item type enum
│   ├── CEWeaponAbstract.java   # Base weapon class
│   ├── CEWeaponFactory.java    # Weapon factory
│   ├── artifact/               # Artifact items
│   ├── book/                   # Enchantment books
│   ├── gem/                    # Socket gems
│   ├── mask/                   # Masks
│   ├── outfit/                 # Outfits
│   ├── sigil/                  # Sigils
│   └── ...                     # Other item types
│
├── listener/                   # Event listeners
│   ├── ListenerModule.java
│   ├── PlayerListener.java
│   ├── EntityListener.java
│   ├── BlockListener.java
│   └── ...
│
├── menu/                       # GUI menus
│   ├── MenuModule.java
│   ├── anvil/                  # CE Anvil menu
│   ├── bookcraft/              # Book crafting menu
│   ├── bookupgrade/            # Book upgrade menu
│   ├── equipment/              # Equipment menu
│   ├── tinkerer/               # Tinkerer menu
│   └── artifactupgrade/        # Artifact upgrade menu
│
├── placeholder/                # PlaceholderAPI
│   └── PlaceholderModule.java
│
├── player/                     # Player system
│   ├── PlayerModule.java
│   ├── CEPlayer.java           # Main player class
│   ├── CEPlayerExpansion.java  # Expansion interface
│   ├── CEPlayerExpansionRegister.java
│   ├── Player*.java            # Expansion implementations
│   └── mining/                 # Mining abilities
│       └── *SpecialMine.java
│
└── task/                       # Background tasks
    ├── TaskModule.java
    └── *Task.java
```

---

## Thread Safety

### Main Thread Operations

The following must run on the main thread:
- Player inventory modifications
- Entity spawning/modifications
- Block modifications
- Most Bukkit API calls

### Async Operations

The following can run asynchronously:
- Attribute calculations
- Effect queue processing (async queue)
- Power calculations
- Configuration loading
- Database operations

### Synchronization Points

```java
// Task queues use synchronized access
EffectExecuteTask.addEffectDataList(list);  // Thread-safe

// Player maps use ConcurrentHashMap
CEPlayerMap.registerCEPlayer(player);  // Thread-safe
```

---

## Error Handling

### Exception Logging

```java
// In CECaller.callCE()
try {
    if (setCEFunction(function).callCEFunction().isBreak()) {
        break;
    }
} catch (Exception e) {
    e.printStackTrace();  // Log and continue
}
```

### Debug Mode

```java
// Enable per-player debug
if (CommandDebugCE.getTogglePlayers().contains(player.getName())) {
    System.out.println(caller.getName() + " is calling " + enchant.getName());
}
```

---

## Performance Considerations

1. **Task Queuing** - Effects are queued rather than executed immediately
2. **Async Calculations** - Attribute recalculation runs asynchronously
3. **Lazy Loading** - Player expansions are created on demand
4. **Cooldown Caching** - Cooldown checks use cached timestamps
5. **Material Lists** - Pre-parsed material lists for fast matching

---

## See Also

- [ENCHANTMENT-CONFIG.md](ENCHANTMENT-CONFIG.md) - Enchantment configuration guide
- [EFFECTS.md](EFFECTS.md) - Complete effect reference
- [CONDITIONS.md](CONDITIONS.md) - Complete condition reference
- [ATTRIBUTE-SYSTEM.md](ATTRIBUTE-SYSTEM.md) - Custom attribute system
- [GUARD-SYSTEM.md](GUARD-SYSTEM.md) - Guard mob system
