# Attribute System Reference

This document provides comprehensive reference for the Attribute system in CustomEnchantment, which includes custom attributes and vanilla attribute modifier management.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Custom Attributes](#custom-attributes)
- [Vanilla Attributes](#vanilla-attributes)
- [PlayerCustomAttribute](#playercustomattribute)
- [PlayerVanillaAttribute](#playervanillaattribute)
- [Attribute Calculation](#attribute-calculation)
- [Related Effects](#related-effects)
- [Usage Examples](#usage-examples)

---

## Overview

The Attribute system provides two types of attributes:

1. **Custom Attributes** - Plugin-defined stats (dodge chance, critical damage, etc.)
2. **Vanilla Attributes** - Bukkit attribute modifiers (health, speed, damage, etc.)

**Source Location:** `src/main/java/com/bafmc/customenchantment/attribute/`, `src/main/java/com/bafmc/customenchantment/player/`

---

## Architecture

```
CEPlayer
    ├── PlayerCustomAttribute
    │       ├── attributeMap (named custom attributes)
    │       ├── valueMap (calculated values)
    │       └── recalculateAttributeMap (equipment-based)
    │
    └── PlayerVanillaAttribute
            └── attributeMap (Bukkit AttributeModifiers)
```

---

## Custom Attributes

**Source:** `attribute/CustomAttributeType.java`

### Overview

CustomEnchantment defines 26 custom attribute types for advanced combat and stat mechanics.

### Combat Attributes

| Attribute | Default | Percent | Description |
|-----------|---------|---------|-------------|
| `DODGE_CHANCE` | 0 | Yes | Chance to completely avoid an attack (0-100) |
| `CRITICAL_CHANCE` | 0 | Yes | Chance to deal critical damage (0-100) |
| `CRITICAL_DAMAGE` | 2 | No | Critical hit damage multiplier |
| `ACCURACY_CHANCE` | 0 | Yes | Counters target's dodge chance (0-100) |
| `LIFE_STEAL` | 0 | Yes | Percentage of damage returned as health (0-100) |
| `ARMOR_PENETRATION` | 0 | Yes | Percentage of enemy armor ignored (0-100) |
| `DAMAGE_REDUCTION` | 0 | Yes | Percentage of incoming damage reduced (0-100) |
| `VULNERABILITY` | 0 | Yes | Bonus damage taken from other players (0-100) |
| `GRIEVOUS_WOUNDS` | 0 | Yes | Reduces healing effectiveness on target (0-100) |

### Regeneration Attributes

| Attribute | Default | Percent | Description |
|-----------|---------|---------|-------------|
| `HEALTH_REGENERATION` | 0 | No | Flat health regenerated per second |
| `HEALTH_REGENERATION_PERCENT` | 0 | Yes | Percentage of max health regenerated per second |

### Resistance Attributes

| Attribute | Default | Percent | Description |
|-----------|---------|---------|-------------|
| `SLOW_RESISTANCE` | 0 | Yes | Chance to resist slowness effects (0-100) |
| `MAGIC_RESISTANCE` | 0 | Yes | Chance to resist magic/stun effects (0-100) |

### Area of Effect Attributes

| Attribute | Default | Percent | Description |
|-----------|---------|---------|-------------|
| `AOE_RANGE` | 1 | No | Multiplier for area of effect range |
| `AOE_DAMAGE_RATIO` | 0 | Yes | Percentage of damage dealt to AoE targets |

### Option Attributes

| Attribute | Default | Percent | Description |
|-----------|---------|---------|-------------|
| `OPTION_ATTACK` | 0 | No | Attack power from equipment options |
| `OPTION_DEFENSE` | 0 | No | Defense power from equipment options |
| `OPTION_POWER` | 0 | No | General power from equipment options |
| `OPTION_ARMOR_PENETRATION` | 0 | No | Armor penetration from equipment options |

### Stat Attributes

| Attribute | Default | Percent | Description |
|-----------|---------|---------|-------------|
| `STAT_EXP` | 0 | No | Bonus EXP modifier |
| `STAT_FOOD` | 0 | No | Food/hunger modifier |
| `STAT_HEALTH` | 0 | No | Health modifier |
| `STAT_OXYGEN` | 0 | No | Oxygen/air modifier |
| `STAT_ABSORPTION_HEART` | 0 | No | Absorption hearts modifier |

### Power Attributes

| Attribute | Default | Percent | Description |
|-----------|---------|---------|-------------|
| `TOTAL_POWER` | 0 | No | Overall power level for balancing |
| `ATK_POWER` | 0 | No | Attack power rating |
| `DEF_POWER` | 0 | No | Defense power rating |
| `MINING_POWER` | 0 | No | Tool effectiveness modifier |

### Attribute Definition

```java
public static final CustomAttributeType DODGE_CHANCE =
    (CustomAttributeType) (new CustomAttributeType(
        "DODGE_CHANCE",              // Type name
        "custom:player.dodge_chance", // Minecraft ID
        0,                            // Base value
        true                          // Is percentage
    )).register();
```

### Percentage Attributes

Attributes marked as `percent: true` are expected to have values between 0 and 100:
- `DODGE_CHANCE` - 50 means 50% dodge chance
- `LIFE_STEAL` - 25 means 25% life steal
- `DAMAGE_REDUCTION` - 30 means 30% damage reduction

---

## Vanilla Attributes

**Source:** `player/PlayerVanillaAttribute.java`

### Supported Attributes

| Bukkit Attribute | Description |
|------------------|-------------|
| `GENERIC_ARMOR` | Armor points |
| `GENERIC_ARMOR_TOUGHNESS` | Armor toughness |
| `GENERIC_ATTACK_DAMAGE` | Base attack damage |
| `GENERIC_ATTACK_SPEED` | Attack speed |
| `GENERIC_KNOCKBACK_RESISTANCE` | Knockback resistance |
| `GENERIC_LUCK` | Luck for loot tables |
| `GENERIC_MAX_HEALTH` | Maximum health |
| `GENERIC_MOVEMENT_SPEED` | Movement speed |

### Modifier Prefix

All plugin-managed modifiers use the prefix `ce-`:
- Prevents conflicts with other plugins
- Easy identification and cleanup
- Auto-prefixed by helper methods

---

## PlayerCustomAttribute

**Source:** `player/PlayerCustomAttribute.java`

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `attributeMap` | `Map<String, NMSAttribute>` | Named custom attributes |
| `valueMap` | `Map<NMSAttributeType, Double>` | Current calculated values |
| `recalculateAttributeMap` | `Multimap<CustomAttributeType, NMSAttribute>` | Equipment-based attributes |

### Methods

#### getValue(CustomAttributeType, double)

Calculates attribute value with base:

```java
double getValue(CustomAttributeType type, double baseValue)
```

Returns the calculated value using `AttributeCalculate.calculate()`.

#### getValue(CustomAttributeType, double, List)

Calculates with additional modifiers:

```java
double getValue(CustomAttributeType type, double baseValue, List<NMSAttribute> otherList)
```

Combines player's attributes with additional modifiers.

#### getValue(NMSAttributeType)

Gets current cached value:

```java
double getValue(NMSAttributeType type)
```

Returns value from `valueMap`, defaulting to 0.0.

#### addCustomAttribute(String, RangeAttribute)

Registers a named custom attribute:

```java
void addCustomAttribute(String name, RangeAttribute attributeData)
```

#### removeCustomAttribute(String)

Unregisters a custom attribute:

```java
void removeCustomAttribute(String name)
```

#### recalculateAttribute()

Recalculates all attributes from equipment:

```java
void recalculateAttribute()
```

This method:
1. Gets all equipped items from CEPlayer
2. Loads attributes from all registered `AbstractAttributeMap` implementations
3. Stores in `recalculateAttributeMap`
4. Calculates final values for each attribute type
5. Stores in `valueMap`

### Recalculation Flow

```
Player equips/unequips item
    └── CEPlayer.recalculate()
        └── PlayerCustomAttribute.recalculateAttribute()
            └── For each AttributeMapRegister:
                └── loadAttributeMap(data)
            └── For each CustomAttributeType:
                └── Calculate final value
                └── Store in valueMap
```

---

## PlayerVanillaAttribute

**Source:** `player/PlayerVanillaAttribute.java`

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `PREFIX` | `String` | `"ce-"` - Prefix for all modifiers |
| `ATTRIBUTE_LIST` | `List<Attribute>` | List of managed vanilla attributes |
| `attributeMap` | `Multimap<Attribute, NMSAttribute>` | Attribute modifiers |

### Lifecycle Methods

```java
void onJoin()  // Clears all attributes on join
void onQuit()  // Clears all attributes on quit
```

### addAttribute(Attribute, String, double, Operation)

Adds an attribute modifier to the player:

```java
boolean addAttribute(Attribute attribute, String name, double amount, Operation operation)
```

| Parameter | Description |
|-----------|-------------|
| `attribute` | Bukkit Attribute type |
| `name` | Unique name (auto-prefixed with "ce-") |
| `amount` | Modifier value |
| `operation` | ADD_NUMBER, ADD_SCALAR, MULTIPLY_SCALAR_1 |

Returns `true` if added, `false` if duplicate name exists.

### removeAttribute(Attribute, String)

Removes an attribute modifier:

```java
boolean removeAttribute(Attribute attribute, String name)
```

Returns `true` if removed, `false` if not found.

### clearAttribute(Attribute)

Clears all plugin modifiers for an attribute type:

```java
void clearAttribute(Attribute attribute)
```

### clearAllAttribute()

Clears all plugin modifiers:

```java
void clearAllAttribute()
```

### getAttributeModifier(Attribute, String)

Gets a specific modifier:

```java
AttributeModifier getAttributeModifier(Attribute attribute, String name)
```

### hasAttributeModifier(Attribute, String)

Checks if modifier exists:

```java
boolean hasAttributeModifier(Attribute attribute, String name)
```

### getAttributeModifiers(Attribute)

Gets all modifiers for an attribute:

```java
List<AttributeModifier> getAttributeModifiers(Attribute attribute)
```

### getAttributeModifierNameList(Attribute)

Gets modifier names with prefix:

```java
List<String> getAttributeModifierNameList(Attribute attribute)
List<String> getAttributeModifierNameList(Attribute attribute, String prefix)
```

### Static Helper

```java
static String getPrefix(String name)
```

Ensures name has "ce-" prefix:
- `"speed"` → `"ce-speed"`
- `"ce-speed"` → `"ce-speed"` (unchanged)

---

## Attribute Calculation

### Operations

| Operation | ID | Calculation |
|-----------|-----|-------------|
| ADD | 0 | `base + value` |
| MULTIPLY_BASE | 1 | `base * (1 + value)` |
| MULTIPLY_TOTAL | 2 | `total * (1 + value)` |
| SET | 3 | `value` (overrides) |

### Calculation Order

1. Start with base value
2. Apply all ADD (operation 0) modifiers
3. Apply all MULTIPLY_BASE (operation 1) modifiers
4. Apply all MULTIPLY_TOTAL (operation 2) modifiers
5. Apply SET (operation 3) if present

### Example Calculation

Base: 100, Modifiers: +10 (ADD), +20% (MULTIPLY_BASE), +10% (MULTIPLY_TOTAL)

```
Step 1: 100 + 10 = 110 (ADD)
Step 2: 110 * (1 + 0.20) = 132 (MULTIPLY_BASE)
Step 3: 132 * (1 + 0.10) = 145.2 (MULTIPLY_TOTAL)
```

---

## Related Effects

### Custom Attribute Effects

#### ATTRIBUTE

Sets a custom attribute value:

**Format:** `ATTRIBUTE;type;name;value;operation`

| Parameter | Description |
|-----------|-------------|
| `type` | CustomAttributeType name |
| `name` | Unique modifier name |
| `value` | Attribute value |
| `operation` | 0-3 |

#### ATTRIBUTE_REMOVE

Removes a custom attribute:

**Format:** `ATTRIBUTE_REMOVE;name`

### Vanilla Attribute Effects

#### VANILLA_ATTRIBUTE

Sets a vanilla attribute modifier:

**Format:** `VANILLA_ATTRIBUTE;attribute;name;value;operation`

| Parameter | Description |
|-----------|-------------|
| `attribute` | Bukkit Attribute name |
| `name` | Unique modifier name |
| `value` | Modifier value |
| `operation` | Bukkit Operation |

#### VANILLA_ATTRIBUTE_REMOVE

Removes a vanilla attribute modifier:

**Format:** `VANILLA_ATTRIBUTE_REMOVE;attribute;name`

---

## Usage Examples

### Critical Strike Build

```yaml
# Weapon enchantment
effects:
  - ATTRIBUTE;CRITICAL_CHANCE;crit_strike;25;0      # +25% crit chance
  - ATTRIBUTE;CRITICAL_DAMAGE;crit_power;0.5;0      # +0.5x crit multiplier
```

### Tank Build

```yaml
# Armor enchantment
effects:
  - ATTRIBUTE;DAMAGE_REDUCTION;tank_armor;15;0      # +15% damage reduction
  - ATTRIBUTE;HEALTH_REGENERATION;regen;2;0         # +2 HP/sec
  - VANILLA_ATTRIBUTE;GENERIC_MAX_HEALTH;bonus_hp;4;0  # +4 max health
```

### Lifesteal Weapon

```yaml
effects:
  - ATTRIBUTE;LIFE_STEAL;vampire;10;0               # +10% life steal
  - ATTRIBUTE;GRIEVOUS_WOUNDS;anti_heal;20;0        # Apply 20% healing reduction
```

### Speed Boots

```yaml
effects:
  - VANILLA_ATTRIBUTE;GENERIC_MOVEMENT_SPEED;swift;0.1;1  # +10% speed
  - ATTRIBUTE;DODGE_CHANCE;nimble;5;0                      # +5% dodge
```

### Removing on Unequip

```yaml
# UNEQUIP trigger
effects:
  - ATTRIBUTE_REMOVE;crit_strike
  - ATTRIBUTE_REMOVE;crit_power
  - VANILLA_ATTRIBUTE_REMOVE;GENERIC_MOVEMENT_SPEED;swift
```

---

## Combat Mechanics

### Dodge Calculation

```java
double dodgeChance = defender.getValue(DODGE_CHANCE);
double accuracyChance = attacker.getValue(ACCURACY_CHANCE);
double effectiveDodge = dodgeChance - accuracyChance;

if (random.nextDouble() * 100 < effectiveDodge) {
    // Attack dodged
}
```

### Critical Hit Calculation

```java
double critChance = attacker.getValue(CRITICAL_CHANCE);

if (random.nextDouble() * 100 < critChance) {
    double critMultiplier = attacker.getValue(CRITICAL_DAMAGE);
    damage *= critMultiplier;
}
```

### Life Steal Calculation

```java
double lifeStealPercent = attacker.getValue(LIFE_STEAL);
double healAmount = damage * (lifeStealPercent / 100);
attacker.heal(healAmount);
```

### Damage Reduction Calculation

```java
double damageReduction = defender.getValue(DAMAGE_REDUCTION);
damage *= (1 - damageReduction / 100);
```

### Armor Penetration Calculation

```java
double armorPen = attacker.getValue(ARMOR_PENETRATION);
double effectiveArmor = targetArmor * (1 - armorPen / 100);
```

---

## Integration Points

### Getting Player Attributes

```java
CEPlayer cePlayer = CustomEnchantment.instance()
    .getCEPlayerManager()
    .get(player);

// Custom attributes
PlayerCustomAttribute customAttr = cePlayer.getCustomAttribute();
double dodgeChance = customAttr.getValue(CustomAttributeType.DODGE_CHANCE);
double critDamage = customAttr.getValue(CustomAttributeType.CRITICAL_DAMAGE);

// Vanilla attributes
PlayerVanillaAttribute vanillaAttr = cePlayer.getVanillaAttribute();
boolean hasSpeedBoost = vanillaAttr.hasAttributeModifier(
    Attribute.GENERIC_MOVEMENT_SPEED,
    "swift"
);
```

### Triggering Recalculation

```java
// After equipment change
cePlayer.recalculate();

// This calls PlayerCustomAttribute.recalculateAttribute()
// which updates valueMap with new calculated values
```

---

## Thread Safety

- `PlayerCustomAttribute.attributeMap` uses `ConcurrentHashMap`
- `PlayerCustomAttribute.valueMap` uses `ConcurrentHashMap`
- Vanilla attribute operations use Bukkit's thread-safe methods
- Recalculation should be called from main thread
