# Player Bonuses Reference

This document provides comprehensive reference for the Player Bonus system in CustomEnchantment, which allows enchantments to grant bonus EXP and money when mining blocks or killing mobs.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Block Bonuses](#block-bonuses)
- [Mob Bonuses](#mob-bonuses)
- [Bonus Base Class](#bonus-base-class)
- [Related Effects](#related-effects)
- [Usage Examples](#usage-examples)

---

## Overview

The Player Bonus system provides two types of bonuses:
1. **Block Bonuses** - Extra EXP/money when breaking specific blocks
2. **Mob Bonuses** - Extra EXP/money/Mob Slayer EXP when killing specific mobs

Bonuses are:
- Stackable from multiple enchantments
- Filtered by block/mob type
- Calculated using the attribute system

**Source Location:** `src/main/java/com/bafmc/customenchantment/player/`

---

## Architecture

```
CEPlayer
    ├── PlayerBlockBonus
    │       ├── expBonus (BlockBonus)
    │       └── moneyBonus (BlockBonus)
    │
    └── PlayerMobBonus
            ├── expBonus (EntityTypeBonus)
            ├── moneyBonus (EntityTypeBonus)
            └── msExpBonus (EntityTypeBonus - Mob Slayer)
```

### Class Hierarchy

```
Bonus<K> (Base)
    ├── BlockBonus extends Bonus<MaterialList>
    └── EntityTypeBonus extends Bonus<EntityTypeList>
```

---

## Block Bonuses

**Source:** `player/PlayerBlockBonus.java`, `player/bonus/BlockBonus.java`

### PlayerBlockBonus

The player extension that manages block-related bonuses.

```java
public class PlayerBlockBonus extends CEPlayerExpansion {
    private BlockBonus expBonus;
    private BlockBonus moneyBonus;
}
```

#### Access Methods

```java
BlockBonus getExpBonus()    // EXP bonus for mining
BlockBonus getMoneyBonus()  // Money bonus for mining
```

### BlockBonus Class

Manages bonuses filtered by block material.

```java
public class BlockBonus extends Bonus<MaterialList> {
    double getBonus(MaterialData materialNMS)
}
```

#### getBonus(MaterialData)

Calculates total bonus for a specific block:
1. Iterates through all registered bonuses
2. Checks if bonus `MaterialList` contains the block
3. Collects matching `RangeAttribute` values
4. Calculates final value using `AttributeCalculate`

```java
public double getBonus(MaterialData materialNMS) {
    List<NMSAttribute> list = new ArrayList<>();

    for (Pair<MaterialList, RangeAttribute> bonus : getBonusList()) {
        if (bonus.getKey().contains(materialNMS)) {
            list.add(bonus.getValue());
        }
    }

    return AttributeCalculate.calculate(0, list);
}
```

### Block Bonus Flow

```
Player breaks block
    └── Get PlayerBlockBonus from CEPlayer
        └── getExpBonus().getBonus(blockMaterial)
            └── Sum all matching bonuses
            └── Apply attribute calculation
        └── getMoneyBonus().getBonus(blockMaterial)
            └── Sum all matching bonuses
            └── Apply attribute calculation
    └── Grant bonus EXP and money
```

---

## Mob Bonuses

**Source:** `player/PlayerMobBonus.java`, `player/bonus/EntityTypeBonus.java`

### PlayerMobBonus

The player extension that manages mob-related bonuses.

```java
public class PlayerMobBonus extends CEPlayerExpansion {
    private EntityTypeBonus expBonus;
    private EntityTypeBonus moneyBonus;
    private EntityTypeBonus msExpBonus;  // Mob Slayer EXP
}
```

#### Access Methods

```java
EntityTypeBonus getExpBonus()          // EXP bonus for killing
EntityTypeBonus getMoneyBonus()        // Money bonus for killing
EntityTypeBonus getMobSlayerExpBonus() // Mob Slayer EXP bonus
```

### EntityTypeBonus Class

Manages bonuses filtered by entity type.

```java
public class EntityTypeBonus extends Bonus<EntityTypeList> {
    double getBonus(EntityType entityType, int size)
}
```

#### getBonus(EntityType, int)

Calculates total bonus for a specific mob type:
1. Iterates through all registered bonuses
2. Checks if bonus `EntityTypeList` contains the entity type
3. Collects matching `RangeAttribute` values
4. Calculates and multiplies by `size` (for multi-kills)

```java
public double getBonus(EntityType entityType, int size) {
    List<NMSAttribute> list = new ArrayList<>();

    for (Pair<EntityTypeList, RangeAttribute> bonus : getBonusList()) {
        if (bonus.getKey().contains(entityType)) {
            list.add(bonus.getValue());
        }
    }

    return AttributeCalculate.calculate(0, list) * size;
}
```

### Mob Bonus Flow

```
Player kills mob
    └── Get PlayerMobBonus from CEPlayer
        └── getExpBonus().getBonus(entityType, 1)
        └── getMoneyBonus().getBonus(entityType, 1)
        └── getMobSlayerExpBonus().getBonus(entityType, 1)
    └── Grant bonus EXP, money, and Mob Slayer EXP
```

---

## Bonus Base Class

**Source:** `player/bonus/Bonus.java`

### Generic Structure

```java
public class Bonus<K> {
    private ConcurrentHashMap<String, Pair<K, RangeAttribute>> map;
}
```

The generic type `K` represents the filter type:
- `MaterialList` for blocks
- `EntityTypeList` for mobs

### Methods

#### put(String, K, RangeAttribute)

Registers a bonus with unique name.

```java
void put(String name, K key, RangeAttribute data)
```

| Parameter | Description |
|-----------|-------------|
| `name` | Unique identifier for this bonus |
| `key` | Filter (MaterialList or EntityTypeList) |
| `data` | Bonus value as RangeAttribute |

#### remove(String)

Unregisters a bonus by name.

```java
void remove(String name)
```

#### isEmpty()

Checks if any bonuses are registered.

```java
boolean isEmpty()
```

#### getBonusList()

Gets all registered bonuses.

```java
List<Pair<K, RangeAttribute>> getBonusList()
```

---

## Related Effects

### BLOCK_BONUS_EXP

Adds EXP bonus when breaking matching blocks.

**Format:** `BLOCK_BONUS_EXP;name;blocks;value;operation`

| Parameter | Description |
|-----------|-------------|
| `name` | Unique bonus identifier |
| `blocks` | MaterialList (block types) |
| `value` | Bonus amount |
| `operation` | 0-3 (see Operations) |

### BLOCK_BONUS_MONEY

Adds money bonus when breaking matching blocks.

**Format:** `BLOCK_BONUS_MONEY;name;blocks;value;operation`

### BLOCK_BONUS_REMOVE

Removes a block bonus by name.

**Format:** `BLOCK_BONUS_REMOVE;name;type`

| Parameter | Description |
|-----------|-------------|
| `name` | Bonus identifier to remove |
| `type` | `EXP` or `MONEY` |

### MOB_BONUS_EXP

Adds EXP bonus when killing matching mobs.

**Format:** `MOB_BONUS_EXP;name;mobs;value;operation`

| Parameter | Description |
|-----------|-------------|
| `name` | Unique bonus identifier |
| `mobs` | EntityTypeList (mob types) |
| `value` | Bonus amount |
| `operation` | 0-3 (see Operations) |

### MOB_BONUS_MONEY

Adds money bonus when killing matching mobs.

**Format:** `MOB_BONUS_MONEY;name;mobs;value;operation`

### MOB_BONUS_MOB_SLAYER_EXP

Adds Mob Slayer EXP bonus when killing matching mobs.

**Format:** `MOB_BONUS_MOB_SLAYER_EXP;name;mobs;value;operation`

### MOB_BONUS_REMOVE

Removes a mob bonus by name.

**Format:** `MOB_BONUS_REMOVE;name;type`

| Parameter | Description |
|-----------|-------------|
| `name` | Bonus identifier to remove |
| `type` | `EXP`, `MONEY`, or `MOB_SLAYER_EXP` |

---

## Operations

Bonuses use the attribute operation system:

| Operation | Name | Calculation |
|-----------|------|-------------|
| 0 | ADD | `base + value` |
| 1 | MULTIPLY_BASE | `base * (1 + value)` |
| 2 | MULTIPLY_TOTAL | `total * (1 + value)` |
| 3 | SET | `value` (overrides) |

---

## Usage Examples

### Mining Fortune

Add 50% more EXP when mining ores:

```yaml
effects:
  - BLOCK_BONUS_EXP;mining_fortune;COAL_ORE,IRON_ORE,GOLD_ORE,DIAMOND_ORE;0.5;1
```

### Mob Hunter

Add 10 bonus money when killing undead mobs:

```yaml
effects:
  - MOB_BONUS_MONEY;undead_hunter;ZOMBIE,SKELETON,PHANTOM;10;0
```

### Mob Slayer Bonus

Add 25% more Mob Slayer EXP for all mobs:

```yaml
effects:
  - MOB_BONUS_MOB_SLAYER_EXP;slayer_bonus;ALL;0.25;1
```

### Conditional Bonus

Add bonus only at night:

```yaml
conditions:
  - TIME;12000->24000
effects:
  - MOB_BONUS_EXP;night_bonus;ALL;0.5;1
```

### Removing Bonuses

Remove bonuses when enchantment deactivates:

```yaml
# On equipment removal (UNEQUIP trigger)
effects:
  - BLOCK_BONUS_REMOVE;mining_fortune;EXP
  - MOB_BONUS_REMOVE;undead_hunter;MONEY
```

---

## MaterialList & EntityTypeList

### MaterialList

Specifies which blocks qualify for bonuses:
- Individual materials: `DIAMOND_ORE`
- Multiple materials: `COAL_ORE,IRON_ORE,GOLD_ORE`
- All blocks: `ALL`
- Block tags: `#minecraft:base_stone_overworld`

### EntityTypeList

Specifies which mobs qualify for bonuses:
- Individual types: `ZOMBIE`
- Multiple types: `ZOMBIE,SKELETON,CREEPER`
- All mobs: `ALL`
- Categories: `UNDEAD` (if supported)

---

## Integration Points

### CEPlayer Access

```java
CEPlayer cePlayer = CustomEnchantment.instance()
    .getCEPlayerManager()
    .get(player);

// Block bonuses
PlayerBlockBonus blockBonus = cePlayer.getBlockBonus();
double expBonus = blockBonus.getExpBonus().getBonus(materialData);

// Mob bonuses
PlayerMobBonus mobBonus = cePlayer.getMobBonus();
double moneyBonus = mobBonus.getMoneyBonus().getBonus(entityType, 1);
```

### Event Handling

Block break events:
```java
@EventHandler
void onBlockBreak(BlockBreakEvent event) {
    CEPlayer cePlayer = getCEPlayer(event.getPlayer());
    MaterialData material = new MaterialData(event.getBlock());

    double expBonus = cePlayer.getBlockBonus().getExpBonus().getBonus(material);
    double moneyBonus = cePlayer.getBlockBonus().getMoneyBonus().getBonus(material);

    // Apply bonuses...
}
```

Mob death events:
```java
@EventHandler
void onMobDeath(EntityDeathEvent event) {
    Player killer = event.getEntity().getKiller();
    if (killer == null) return;

    CEPlayer cePlayer = getCEPlayer(killer);
    EntityType type = event.getEntityType();

    double expBonus = cePlayer.getMobBonus().getExpBonus().getBonus(type, 1);
    double moneyBonus = cePlayer.getMobBonus().getMoneyBonus().getBonus(type, 1);
    double msExpBonus = cePlayer.getMobBonus().getMobSlayerExpBonus().getBonus(type, 1);

    // Apply bonuses...
}
```

---

## Thread Safety

Both `BlockBonus` and `EntityTypeBonus` use `ConcurrentHashMap` for thread-safe operations:
- Safe to add/remove bonuses from async tasks
- Safe to calculate bonuses during events
- No explicit synchronization needed
