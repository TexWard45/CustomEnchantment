# CustomEnchantment Developer API Reference

This document provides comprehensive reference for developers integrating with or extending the CustomEnchantment plugin.

## Table of Contents

- [Overview](#overview)
- [Core API (CEAPI)](#core-api-ceapi)
- [Player System](#player-system)
- [Item System](#item-system)
- [Enchantment System](#enchantment-system)
- [Extension Points](#extension-points)
- [Utility Classes](#utility-classes)

---

## Overview

The CustomEnchantment plugin provides several public APIs for integration:

| Package | Purpose |
|---------|---------|
| `com.bafmc.customenchantment.api` | Core API classes |
| `com.bafmc.customenchantment.player` | Player data and expansions |
| `com.bafmc.customenchantment.item` | Item management |
| `com.bafmc.customenchantment.enchant` | Enchantment system |

### Getting the Plugin Instance

```java
import com.bafmc.customenchantment.CustomEnchantment;

CustomEnchantment plugin = CustomEnchantment.instance();
```

---

## Core API (CEAPI)

The `CEAPI` class (`com.bafmc.customenchantment.api.CEAPI`) is the primary entry point for accessing plugin functionality.

### Player Access

```java
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;

// Get CEPlayer for a Bukkit Player
CEPlayer cePlayer = CEAPI.getCEPlayer(player);

// Get all online CEPlayers
List<CEPlayer> allPlayers = CEAPI.getCEPlayers();
```

### Combat Status

```java
// Check if player is in combat (integrates with CombatLogX if available)
boolean inCombat = CEAPI.isInCombat(player);
```

### Enchantment Access

```java
import com.bafmc.customenchantment.enchant.CEEnchant;
import com.bafmc.customenchantment.enchant.CEGroup;

// Get enchantment by name
CEEnchant enchant = CEAPI.getCEEnchant("lifesteal");

// Get enchantment group by name
CEGroup group = CEAPI.getCEGroup("legendary");
```

### Item Operations

```java
import org.bukkit.inventory.ItemStack;
import com.bafmc.customenchantment.item.*;

// Check if ItemStack is a CE item
boolean isCEItem = CEAPI.isCEItem(itemStack);

// Check if ItemStack is a specific CE item type
boolean isWeapon = CEAPI.isCEItem(itemStack, CEItemType.WEAPON);

// Get CEItem from ItemStack
CEItem ceItem = CEAPI.getCEItem(itemStack);
CEItem weapon = CEAPI.getCEItem(itemStack, CEItemType.WEAPON);

// Get item type
String itemType = CEAPI.getCEItemType(itemStack);

// Get CEItemSimple (lightweight item reference)
CEItemSimple simple = CEAPI.getCEItemSimple(itemStack);
```

### Item Storage Access

```java
// Get item storage by type
CEItemStorage storage = CEAPI.getCEItemStorage(CEItemType.WEAPON);

// Get item from storage by pattern
CEItem item = CEAPI.getCEItemByStorage(CEItemType.WEAPON, "excalibur");

// Get vanilla item from storage
ItemStack vanilla = CEAPI.getVanillaItemStack("diamond_sword");
List<ItemStack> vanillaList = CEAPI.getVanillaItemStacks("diamond_*");

// Get gem item with level
ItemStack gem = CEAPI.getGemItemStack("ruby", 3);
```

### Enchantment Book Generation

```java
import com.bafmc.customenchantment.enchant.CEEnchantSimple;

// Create enchantment book
CEEnchantSimple enchantData = new CEEnchantSimple("lifesteal", 3, 100, 0, 0);
ItemStack book = CEAPI.getCEBookItemStack(enchantData);

// Create book with specific type
ItemStack specialBook = CEAPI.getCEBookItemStack("legendary_book", enchantData);
```

### Equipment Access

```java
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.item.CEWeaponAbstract;

// Get all equipped CE weapons
Map<EquipSlot, CEWeaponAbstract> allEquipment = CEAPI.getCEWeaponMap(player);

// Get specific slots
Map<EquipSlot, CEWeaponAbstract> handEquipment = CEAPI.getCEWeaponMap(player,
    EquipSlot.MAINHAND, EquipSlot.OFFHAND);
```

### Player Guard Access

```java
import com.bafmc.customenchantment.guard.PlayerGuard;

// Get player's guard (summoned entity protectors)
PlayerGuard guard = CEAPI.getPlayerGuard(player);
```

---

## Player System

### CEPlayer

The `CEPlayer` class wraps Bukkit's Player with additional functionality.

```java
CEPlayer cePlayer = CEAPI.getCEPlayer(player);

// Get bukkit player
Player bukkitPlayer = cePlayer.getPlayer();

// Access player expansions
PlayerEquipment equipment = cePlayer.getEquipment();
PlayerCustomAttribute attributes = cePlayer.getCustomAttribute();
PlayerStorage storage = cePlayer.getStorage();
PlayerPotion potions = cePlayer.getPotion();
```

### Available Expansions

| Expansion | Description |
|-----------|-------------|
| `PlayerEquipment` | Manages equipped CE items across all slots |
| `PlayerCustomAttribute` | Custom attribute modifiers (dodge, crit, etc.) |
| `PlayerVanillaAttribute` | Vanilla Minecraft attribute management |
| `PlayerStorage` | Persistent player data storage |
| `PlayerTemporaryStorage` | Session-based temporary data |
| `PlayerPotion` | Permanent potion effect management |
| `PlayerGem` | Gem effects on player |
| `PlayerCECooldown` | Enchantment cooldown tracking |
| `PlayerAbility` | Active abilities (dash, flash, etc.) |
| `PlayerMobBonus` | Mob drop modifiers |
| `PlayerBlockBonus` | Block drop modifiers |
| `PlayerSpecialMining` | Mining mode management (vein, explosion) |
| `PlayerExtraSlot` | Extra equipment slot management |
| `PlayerSet` | Set bonus tracking |
| `PlayerNameTag` | Custom name tag display |

### Accessing Expansions

```java
// Get specific expansion by class
PlayerCustomAttribute attr = cePlayer.getExpansion(PlayerCustomAttribute.class);

// Common expansion shortcuts
PlayerEquipment equipment = cePlayer.getEquipment();
```

---

## Item System

### CEItem Hierarchy

```
CEItem<T extends CEItemData>
├── CEBook           - Enchantment books
├── CEGem            - Socket gems
├── CEWeaponAbstract - Equipable items
│   ├── CEWeapon     - Weapons
│   ├── CEArmor      - Armor pieces
│   ├── CEArtifact   - Artifact accessories
│   ├── CESigil      - Sigil accessories
│   └── CEOutfit     - Outfit accessories
├── CEMask           - Cosmetic masks
├── CEBanner         - Display banners
└── CESkin           - Weapon skins
```

### Item Types

```java
import com.bafmc.customenchantment.item.CEItemType;

// Available types
CEItemType.WEAPON        // Custom weapons
CEItemType.BOOK          // Enchantment books
CEItemType.GEM           // Socket gems
CEItemType.ARTIFACT      // Artifact items
CEItemType.SIGIL         // Sigil items
CEItemType.OUTFIT        // Outfit items
CEItemType.MASK          // Cosmetic masks
CEItemType.STORAGE       // Stored vanilla items
CEItemType.BANNER        // Display banners
CEItemType.SKIN          // Weapon skins
// ... and more
```

### Working with CEWeaponAbstract

```java
CEWeaponAbstract weapon = (CEWeaponAbstract) CEAPI.getCEItem(itemStack);

// Access components
WeaponEnchant enchants = weapon.getWeaponEnchant();
WeaponGem gems = weapon.getWeaponGem();
WeaponDisplay display = weapon.getWeaponDisplay();
WeaponData data = weapon.getWeaponData();
WeaponAttribute attributes = weapon.getWeaponAttribute();

// Get enchantment level
int lifeStealLevel = enchants.getEnchantLevel("lifesteal");

// Check if has enchantment
boolean hasLifeSteal = enchants.hasEnchant("lifesteal");

// Get all enchantments
Map<String, Integer> allEnchants = enchants.getEnchantMap();
```

### ITrade Interface

All CE items implement `ITrade<ItemStack>` for serialization:

```java
// Import from ItemStack
ceItem.importFrom(itemStack);

// Export to ItemStack
ItemStack exported = ceItem.exportTo();
```

---

## Enchantment System

### Creating Custom Conditions

Extend `ConditionHook` to create custom conditions:

```java
import com.bafmc.customenchantment.enchant.ConditionHook;
import com.bafmc.customenchantment.enchant.CEFunctionData;

public class ConditionMyCustom extends ConditionHook {
    private double threshold;

    @Override
    public String getIdentify() {
        return "MY_CUSTOM_CONDITION";
    }

    @Override
    public void setup(String[] args) {
        // Parse configuration arguments
        // Example config: MY_CUSTOM_CONDITION:50
        this.threshold = Double.parseDouble(args[0]);
    }

    @Override
    public boolean match(CEFunctionData data) {
        // Return true if condition is met
        Player player = data.getPlayer();
        if (player == null) return false;

        return player.getHealth() > threshold;
    }
}
```

### Registering Conditions

```java
import com.bafmc.customenchantment.enchant.Condition;

// Register the condition
Condition.register(new ConditionMyCustom());

// Check if registered
boolean isRegistered = Condition.isRegister("MY_CUSTOM_CONDITION");

// Unregister
Condition.unregister(conditionHook);
```

### Creating Custom Effects

Extend `EffectHook` to create custom effects:

```java
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.enchant.CEFunctionData;

public class EffectMyCustom extends EffectHook {
    private double amount;

    @Override
    public String getIdentify() {
        return "MY_CUSTOM_EFFECT";
    }

    @Override
    public void setup(String[] args) {
        // Parse configuration arguments
        // Example config: MY_CUSTOM_EFFECT:10
        this.amount = Double.parseDouble(args[0]);
    }

    @Override
    public void execute(CEFunctionData data) {
        // Execute the effect
        LivingEntity entity = data.getLivingEntity();
        if (entity == null) return;

        // Apply effect logic
        entity.setHealth(Math.min(entity.getHealth() + amount,
                                   entity.getMaxHealth()));
    }

    @Override
    public boolean isAsync() {
        // Return false if this effect modifies Bukkit API
        // that must run on main thread
        return false;
    }
}
```

### Registering Effects

```java
import com.bafmc.customenchantment.enchant.Effect;

// Register the effect
Effect.register(new EffectMyCustom());

// Check if registered
boolean isRegistered = Effect.isRegister("MY_CUSTOM_EFFECT");

// Unregister
Effect.unregister(effectHook);
```

### CEFunctionData

The `CEFunctionData` class contains context for condition/effect execution:

```java
// Player who owns the enchanted item
Player player = data.getPlayer();

// Enemy player (for combat effects)
Player enemy = data.getEnemyPlayer();

// Living entity (target based on settings)
LivingEntity entity = data.getLivingEntity();

// Current target type
Target target = data.getTarget();

// Equipment slot that triggered
EquipSlot slot = data.getSlot();

// Enchantment level
int level = data.getLevel();
```

### Target Types

```java
import com.bafmc.customenchantment.enchant.Target;

Target.PLAYER  // Effect targets the item owner
Target.ENEMY   // Effect targets the enemy
```

---

## Extension Points

### Creating Player Expansions

Extend `CEPlayerExpansion` to add custom player data:

```java
import com.bafmc.customenchantment.player.CEPlayerExpansion;
import com.bafmc.customenchantment.player.CEPlayer;

public class MyCustomExpansion extends CEPlayerExpansion {
    private int customValue = 0;

    public MyCustomExpansion(CEPlayer cePlayer) {
        super(cePlayer);
    }

    @Override
    public void onJoin() {
        // Called when player joins
        loadData();
    }

    @Override
    public void onQuit() {
        // Called when player quits
        saveData();
    }

    public int getCustomValue() {
        return customValue;
    }

    public void setCustomValue(int value) {
        this.customValue = value;
    }

    private void loadData() {
        // Load from database or config
    }

    private void saveData() {
        // Save to database or config
    }
}
```

### Registering Player Expansions

```java
import com.bafmc.customenchantment.player.CEPlayerExpansionRegister;

// Register expansion class
CEPlayerExpansionRegister.register(MyCustomExpansion.class);

// Access expansion from CEPlayer
MyCustomExpansion expansion = cePlayer.getExpansion(MyCustomExpansion.class);
```

### Creating Item Expansions

Extend `CEItemExpansion` for custom item components:

```java
import com.bafmc.customenchantment.item.CEItemExpansion;
import com.bafmc.customenchantment.item.CEWeaponAbstract;

public class MyItemExpansion extends CEItemExpansion {
    public MyItemExpansion(CEWeaponAbstract ceItem) {
        super(ceItem);
    }

    public void customMethod() {
        CEWeaponAbstract item = getCEItem();
        // Custom logic
    }
}
```

---

## Utility Classes

### Parameter

Utility for parsing colon-separated configuration values:

```java
import com.bafmc.customenchantment.api.Parameter;

// Parse from string
Parameter param = new Parameter("value1:100:3.5");

// Access values with type conversion
String str = param.getString(0);        // "value1"
Integer num = param.getInteger(1);      // 100
Double dbl = param.getDouble(2);        // 3.5

// With default values
String strOrDefault = param.getString(5, "default");
Integer numOrDefault = param.getInteger(5, 0);

// Check if index exists
boolean hasIndex = param.isSet(2);      // true
boolean hasIndex5 = param.isSet(5);     // false
```

### CompareOperation

Utility for comparison operations:

```java
import com.bafmc.customenchantment.api.CompareOperation;

// Parse operation from string
CompareOperation op = CompareOperation.getOperation(">=");

// Compare values
boolean result = CompareOperation.compare(50.0, 30.0, op);  // true (50 >= 30)

// Available operations
CompareOperation.SMALLER         // "<"
CompareOperation.BIGGER          // ">"
CompareOperation.EQUALS          // "="
CompareOperation.EQUALSIGNORECASE // "=="
CompareOperation.NOT_EQUALS      // "!="
CompareOperation.SMALLEREQUALS   // "<="
CompareOperation.BIGGEREQUALS    // ">="
```

### Pair

Simple key-value pair utility:

```java
import com.bafmc.customenchantment.api.Pair;

Pair<String, Integer> pair = new Pair<>("key", 100);
String key = pair.getKey();      // "key"
Integer value = pair.getValue(); // 100
```

### ITrade Interface

Interface for import/export operations:

```java
import com.bafmc.customenchantment.api.ITrade;

public class MyItem implements ITrade<ItemStack> {
    @Override
    public void importFrom(ItemStack source) {
        // Parse item data from ItemStack
    }

    @Override
    public ItemStack exportTo() {
        // Create ItemStack from item data
        return itemStack;
    }
}
```

### ILine Interface

Interface for string serialization:

```java
import com.bafmc.customenchantment.api.ILine;

public class MyData implements ILine {
    private String name;
    private int level;

    @Override
    public String toLine() {
        return name + ":" + level;
    }

    @Override
    public void fromLine(String line) {
        String[] parts = line.split(":");
        this.name = parts[0];
        this.level = Integer.parseInt(parts[1]);
    }
}
```

### MaterialList

Pre-defined material groups for filtering:

```java
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.api.MaterialData;

// Define custom material list
MaterialList swords = new MaterialList();
swords.add(MaterialData.getMaterialNMSByString("DIAMOND_SWORD"));
swords.add(MaterialData.getMaterialNMSByString("NETHERITE_SWORD"));
MaterialList.defineMaterialList("SWORDS", swords);

// Get material list by name
MaterialList list = MaterialList.getMaterialList("SWORDS");

// Parse from config list
List<String> configList = Arrays.asList("DIAMOND_SWORD", "IRON_SWORD");
MaterialList parsed = MaterialList.getMaterialList(configList);
```

### EntityTypeList

Pre-defined entity type groups for filtering:

```java
import com.bafmc.customenchantment.api.EntityTypeList;
import org.bukkit.entity.EntityType;

// Define custom entity type list
EntityTypeList hostiles = new EntityTypeList();
hostiles.add(EntityType.ZOMBIE);
hostiles.add(EntityType.SKELETON);
EntityTypeList.defineEntityTypeList("HOSTILES", hostiles);

// Get entity type list by name
EntityTypeList list = EntityTypeList.getEntityTypeList("HOSTILES");

// Parse from config list
List<String> configList = Arrays.asList("ZOMBIE", "SKELETON", "SPIDER");
EntityTypeList parsed = EntityTypeList.getEntityTypeList(configList);
```

### ParticleAPI

Particle effect utilities:

```java
import com.bafmc.customenchantment.api.ParticleAPI;
import org.bukkit.Location;

// Send particle at location
ParticleAPI.sendParticle(location, "FLAME");

// Send versioned particle (1.8 or 1.13 format)
ParticleAPI.sendParticle(location, "1_13:SOUL_FIRE_FLAME");

// Get colored dust particles
List<ParticleOptions> dust = ParticleAPI.getDustColor("#FF0000", 1.0f);
```

### ItemPacketAPI

Packet-based item display:

```java
import com.bafmc.customenchantment.api.ItemPacketAPI;

// Send item to specific slot via packet (client-side only)
ItemPacketAPI.sendItem(player, 0, slotIndex, itemStack);
```

### EquipSlotAPI

Equipment slot utilities:

```java
import com.bafmc.customenchantment.api.EquipSlotAPI;
import com.bafmc.bukkit.utils.EquipSlot;

// Get the slot holding a bow (for arrow shooting)
EquipSlot bowSlot = EquipSlotAPI.getBowShoowSlot(player);
```

---

## Best Practices

### Thread Safety

1. Effects with `isAsync() = true` run on async thread
2. Use `isAsync() = false` for Bukkit API calls that require main thread
3. Player expansion data should use `ConcurrentHashMap` for thread-safe storage

### Performance

1. Cache CEPlayer references when processing multiple players
2. Use `CEItemSimple` for quick item type checks
3. Avoid repeated `getCEItem()` calls in loops

### Error Handling

```java
CEPlayer cePlayer = CEAPI.getCEPlayer(player);
if (cePlayer == null) {
    // Player not yet initialized
    return;
}

CEItem item = CEAPI.getCEItem(itemStack);
if (item == null) {
    // Not a CE item
    return;
}
```

---

## See Also

- [ARCHITECTURE.md](ARCHITECTURE.md) - System architecture overview
- [ENCHANTMENT-CONFIG.md](ENCHANTMENT-CONFIG.md) - Enchantment configuration
- [ITEM-SYSTEM.md](ITEM-SYSTEM.md) - Item system details
- [EVENTS.md](EVENTS.md) - Custom events documentation
