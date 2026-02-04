# CustomEnchantment Configuration Guide

This document explains how to configure custom enchantments in the CustomEnchantment plugin.

## Table of Contents

1. [File Structure](#file-structure)
2. [Groups Configuration](#groups-configuration)
3. [Enchantment Configuration](#enchantment-configuration)
4. [Levels Configuration](#levels-configuration)
5. [Functions Configuration](#functions-configuration)
6. [Conditions Reference](#conditions-reference)
7. [Effects Reference](#effects-reference)
8. [Options Reference](#options-reference)
9. [Advanced Features](#advanced-features)
10. [Examples](#examples)

---

## File Structure

```
src/main/resources/
├── groups.yml                 # Group definitions (rarity tiers)
├── enchantment/              # Enchantment configurations
│   ├── sword.yml             # Sword enchantments
│   ├── bow.yml               # Bow enchantments
│   ├── helmet.yml            # Helmet enchantments
│   ├── chestplate.yml        # Chestplate enchantments
│   ├── leggings.yml          # Leggings enchantments
│   ├── boots.yml             # Boots enchantments
│   ├── tool.yml              # Tool set enchantments
│   ├── mix.yml               # Multi-item type enchantments
│   └── ...
```

---

## Groups Configuration

Groups define rarity tiers for enchantments. Configured in `groups.yml`.

### Group Properties

| Property | Type | Description |
|----------|------|-------------|
| `display` | String | Display name of the group |
| `enchant-display` | String | Format for enchantment display on items |
| `book-display` | String | Format for enchantment book display |
| `prefix` | String | Color prefix for the group |
| `success` | String/Number | Success rate range (e.g., `0->50` or `100`) |
| `success-sigma` | Number | Gaussian distribution sigma for success rate |
| `destroy` | String/Number | Destroy rate range |
| `destroy-sigma` | Number | Gaussian distribution sigma for destroy rate |
| `valuable` | Number | Base value for tinkerer/trading |
| `craft` | Boolean | Whether books can be crafted |
| `filter` | Boolean | Whether to show in filter menus |
| `priority` | String | Processing priority (LOWEST, LOW, NORMAL, HIGH, HIGHEST, MORNITOR) |

### Available Groups

| Group | Display (Vietnamese) | Color | Description |
|-------|---------------------|-------|-------------|
| `common` | Thuong | Gray (#94a3b8) | Common tier |
| `rare` | Hiem | Green (#22c55e) | Rare tier |
| `epic` | Su thi | Cyan (#0dcaf0) | Epic tier |
| `legendary` | Huyen thoai | Yellow (#ffc107) | Legendary tier |
| `supreme` | Toi thuong | Orange (#ff7b00) | Supreme tier |
| `ultimate` | Chien than | Red (#ef4444) | Ultimate tier |
| `event` | Su kien | Pink (#ec4899) | Event-exclusive |
| `limit` | Gioi han | Purple (#a855f7) | Limited edition |
| `mask` | Mat na | Dark Purple (&5) | Mask items |
| `artifact` | Co vat | White (&f) | Artifact items |
| `set` | Bo trang bi | White (&f) | Set bonuses |
| `tool` | Bo cong cu | White (&f) | Tool sets |
| `disable` | Khong hoat dong | Gray (&8) | Disabled enchants |
| `test` | Thu nghiem | Gray (&8) | Testing only |
| `primecommon` - `primeultimate` | (Dot pha) | Gradient | Breakthrough variants |

### Display Placeholders

| Placeholder | Description |
|-------------|-------------|
| `%enchant_display%` | Enchantment display name |
| `%enchant_level%` | Enchantment level (Roman numerals) |
| `%enchant_display_half_1%` | First half of display name (for gradients) |
| `%enchant_display_half_2%` | Second half of display name (for gradients) |

### Example Group

```yaml
rare:
  display: Hiem
  enchant-display: "&#22c55e%enchant_display% %enchant_level%"
  book-display: "&#22c55e&l&n%enchant_display% %enchant_level%"
  prefix: "&#22c55e"
  success: 0->75
  success-sigma: 0.5
  destroy: 5->100
  destroy-sigma: 0.5
  valuable: 1000
  craft: true
  filter: true
```

---

## Enchantment Configuration

Each enchantment is defined as a YAML key in an enchantment file.

### Enchantment Properties

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `group` | String | Yes | Group/rarity tier reference |
| `display` | String | Yes | Display name shown in-game |
| `description` | List<String> | Yes | Short description lines |
| `detail-description` | List<String> | No | Detailed description for library |
| `applies-description` | List<String> | Yes | Human-readable item types |
| `applies` | List<String> | Yes | Applicable item types |
| `max-level` | Number | Yes | Maximum enchantment level |
| `enchant-point` | Number | Yes | Enchantment points consumed |
| `valuable` | Number | Yes | Value for tinkerer (0 = use group default) |
| `levels` | Map | Yes | Level-specific configurations |
| `enable` | Boolean | No | Whether enchantment is enabled (default: true) |
| `custom-display-lore` | String | No | Override the group's enchant-display |

### Applicable Item Types

| Type | Description |
|------|-------------|
| `ALL_SWORD` | All sword types |
| `ALL_AXE` | All axe types |
| `ALL_BOW` | All bow types |
| `ALL_PICKAXE` | All pickaxe types |
| `ALL_SHOVEL` | All shovel types |
| `ALL_HOE` | All hoe types |
| `ALL_HELMET` | All helmet types |
| `ALL_CHESTPLATE` | All chestplate types |
| `ALL_LEGGINGS` | All leggings types |
| `ALL_BOOTS` | All boots types |
| `ALL_TRIDENT` | All trident types |

### Example Enchantment

```yaml
glowing:
  group: rare
  display: Glowing
  description:
    - "&eGrants night vision effect"
    - "&ewhen equipped"
  detail-description:
    - "&eGrants night vision effect"
    - "&ewhen equipped"
    - ""
    - "&ePotion Effect: Night Vision"
    - "&eDuration: Permanent"
  applies-description:
    - Helmet
  applies:
    - ALL_HELMET
  max-level: 1
  enchant-point: 1
  valuable: 0
  levels:
    "1":
      # ... level configuration
```

---

## Levels Configuration

Each level is defined under the `levels` key with the level number as the key.

```yaml
levels:
  "1":
    function_name:
      # function configuration
  "2":
    function_name:
      # function configuration
  # ... up to max-level
```

**Note:** Level keys can be strings (`"1"`) or integers (`1`).

---

## Functions Configuration

Each level can have multiple functions (triggers). Function names are arbitrary but should be descriptive.

### Function Properties

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `type` | String | Yes | Trigger type (CEType) |
| `chance` | Number | Yes | Activation chance (0-100) |
| `cooldown` | Number | Yes | Cooldown in milliseconds |
| `conditions` | List<String> | Yes | List of conditions to check |
| `effects` | List<String> | No | List of effects to execute |
| `options` | List<String> | No | List of damage/defense modifiers |
| `effect-now` | Boolean | No | Execute effects immediately |
| `true-condition-break` | Boolean | No | Stop processing if conditions pass |
| `false-condition-break` | Boolean | No | Stop processing if conditions fail |
| `timeout-cooldown-break` | Boolean | No | Stop if cooldown just expired |
| `in-cooldown-break` | Boolean | No | Stop if still in cooldown |
| `true-chance-break` | Boolean | No | Stop if chance succeeded |
| `false-chance-break` | Boolean | No | Stop if chance failed |
| `target-filter` | Map | No | Filter for area effects |
| `target-conditions` | List<String> | No | Conditions for filtered targets |
| `target-effects` | List<String> | No | Effects for filtered targets |
| `chance-slot` | List<String> | No | Equipment slots for chance check |
| `cooldown-slot` | List<String> | No | Equipment slots for cooldown |
| `active-slot` | List<String> | No | Equipment slots that must be active |

### CEType (Trigger Types)

| Type | Description |
|------|-------------|
| `ATTACK` | When player attacks |
| `FINAL_ATTACK` | After attack damage calculation |
| `DEFENSE` | When player takes damage |
| `UNKNOWN_DEFENSE` | When player takes unknown damage |
| `HURT` | When player is hurt |
| `ARROW_HIT` | When player's arrow hits target |
| `ARROW_DEFENSE` | When player is hit by arrow |
| `BOW_SHOOT` | When player shoots bow |
| `SNEAK` | When player sneaks |
| `MINING` | When player mines a block |
| `HOLD` | When player holds item |
| `CHANGE_HAND` | When player changes held item |
| `HOTBAR_HOLD` | When item is in hotbar |
| `HOTBAR_CHANGE` | When hotbar selection changes |
| `EXTRA_SLOT_EQUIP` | When item equipped to extra slot |
| `EXTRA_SLOT_UNEQUIP` | When item unequipped from extra slot |
| `KILL_PLAYER` | When player kills another player |
| `DEATH` | When player dies |
| `ARMOR_EQUIP` | When armor is equipped |
| `ARMOR_UNDRESS` | When armor is removed |
| `QUIT` | When player quits |
| `JOIN` | When player joins |
| `MOVE` | When player moves |
| `STATS_CHANGE` | When player stats change |
| `ITEM_CONSUME` | When player consumes item |

### Example Function

```yaml
arrow_hit:
  type: ARROW_HIT
  chance: 50
  cooldown: 2000
  conditions:
    - "EQUIP_SLOT:MAINHAND"
    - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
  effects:
    - "TARGET=ENEMY | ADD_POTION:POISON:0:40"
```

---

## Conditions Reference

Conditions determine when an enchantment activates.

### Condition Format

```
[TARGET=target,NEGATIVE=true |] CONDITION_TYPE:arg1:arg2:...
```

### Condition Prefixes

| Prefix | Description |
|--------|-------------|
| `TARGET=ENEMY` | Check condition on enemy instead of player |
| `TARGET=PLAYER` | Check condition on player (default) |
| `NEGATIVE=true` | Invert the condition result |

### Available Conditions

| Condition | Arguments | Description |
|-----------|-----------|-------------|
| `EQUIP_SLOT` | slot | Check if item is in specific slot |
| `ACTIVE_EQUIP_SLOT` | slot | Check if slot is active |
| `ONLY_ACTIVE_EQUIP` | - | Only trigger if equip is active |
| `ENTITY_TYPE` | type | Check entity type (PLAYER, etc.) |
| `HEALTH` | operator:value | Check health (e.g., `>=:10`) |
| `HEALTH_PERCENT` | operator:value | Check health percentage |
| `FOOD` | operator:value | Check food level |
| `FOOD_PERCENT` | operator:value | Check food percentage |
| `OXYGEN` | operator:value | Check oxygen level |
| `OXYGEN_PERCENT` | operator:value | Check oxygen percentage |
| `EXP` | operator:value | Check experience |
| `LEVEL` | operator:value | Check player level |
| `ON_FIRE` | - | Check if on fire |
| `ON_GROUND` | - | Check if on ground |
| `ALLOW_FLIGHT` | - | Check if flight allowed |
| `IN_COMBAT` | - | Check if in combat |
| `HAS_ENEMY` | - | Check if has enemy target |
| `HAS_NEARBY_ENEMY` | radius | Check for nearby enemies |
| `OUT_OF_SIGHT` | - | Check if out of enemy's sight |
| `CAN_ATTACK` | - | Check if can attack |
| `FACTION_RELATION` | relation | Check faction relation (MEMBER, ALLY, etc.) |
| `IN_FACTION_TERRIORITY` | - | Check if in faction territory |
| `WORLD_TIME` | operator:value | Check world time |
| `DAMAGE_CAUSE` | cause | Check damage cause |
| `HAS_DAMAGE_CAUSE` | cause | Check if damage has specific cause |
| `NUMBER_STORAGE` | key:operator:value | Check stored number value |
| `TEXT_STORAGE` | key:operator:value | Check stored text value |
| `ITEM_CONSUME` | material | Check consumed item type |
| `HOLD` | material | Check held item type |
| `FAKE_SOURCE` | - | Check if damage source is fake |

### Equipment Slots

| Slot | Description |
|------|-------------|
| `MAINHAND` | Main hand |
| `OFFHAND` | Off hand |
| `HAND` | Either hand |
| `HELMET` | Helmet slot |
| `CHESTPLATE` | Chestplate slot |
| `LEGGINGS` | Leggings slot |
| `BOOTS` | Boots slot |
| `ARMOR` | Any armor slot |
| `HOTBAR` | Any hotbar slot |
| `HOTBAR_1` - `HOTBAR_9` | Specific hotbar slot |
| `EXTRA_SLOT` | Any extra slot |
| `EXTRA_SLOT_1` - `EXTRA_SLOT_9` | Specific extra slot |

### Operators

| Operator | Description |
|----------|-------------|
| `=` or `==` | Equal to |
| `!=` | Not equal to |
| `>` | Greater than |
| `>=` | Greater than or equal |
| `<` | Less than |
| `<=` | Less than or equal |

### Example Conditions

```yaml
conditions:
  - "EQUIP_SLOT:MAINHAND"                    # Item in main hand
  - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"      # Target is a player
  - "HEALTH_PERCENT:<=:50"                   # Player health <= 50%
  - "NUMBER_STORAGE:%damage%:>=:100"         # Damage >= 100
  - "FACTION_RELATION:MEMBER"                # Target is faction member
```

---

## Effects Reference

Effects define what happens when the enchantment activates.

### Effect Format

```
[TARGET=target,DELAY=ticks,NAME=name,EFFECT_AFTER_DEAD=true |] EFFECT_TYPE:arg1:arg2:...
```

### Effect Prefixes

| Prefix | Description |
|--------|-------------|
| `TARGET=ENEMY` | Apply effect to enemy |
| `TARGET=PLAYER` | Apply effect to player (default) |
| `DELAY=ticks` | Delay effect by game ticks (20 ticks = 1 second) |
| `NAME=name` | Name the delayed task (for cancellation) |
| `EFFECT_AFTER_DEAD=true` | Execute even if target died |
| `EFFECT_ON_FAKE_SOURCE=true` | Execute on fake damage sources |

### Available Effects

#### Potion Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `ADD_POTION` | type:level:duration | Add potion effect |
| `REMOVE_POTION` | type | Remove potion effect |
| `ADD_RANDOM_POTION` | mode:count:types:level:duration | Add random potion effects |
| `REMOVE_RANDOM_POTION` | mode:count:types | Remove random potion effects |
| `ADD_FOREVER_POTION` | id:type:level | Add permanent potion effect |
| `REMOVE_FOREVER_POTION` | id | Remove permanent potion effect |
| `BLOCK_FOREVER_POTION` | id:type | Block a potion type permanently |
| `UNBLOCK_FOREVER_POTION` | id | Unblock a potion type |

#### Attribute Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `ADD_ATTRIBUTE` | attribute:id:amount:operation | Add attribute modifier |
| `REMOVE_ATTRIBUTE` | attribute:id | Remove attribute modifier |
| `ADD_AUTO_ATTRIBUTE` | attribute:amount:operation | Add auto-calculated attribute |
| `REMOVE_AUTO_ATTRIBUTE` | attribute | Remove auto attribute |
| `ADD_CUSTOM_ATTRIBUTE` | attribute:id:amount:operation | Add custom attribute |
| `REMOVE_CUSTOM_ATTRIBUTE` | attribute:id | Remove custom attribute |

#### Health/Stats Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `HEALTH` | operation:value | Modify health (ADD/REMOVE/SET) |
| `FOOD` | operation:value | Modify food level |
| `OXYGEN` | operation:value | Modify oxygen level |
| `EXP` | operation:value | Modify experience |
| `ABSORPTION_HEART` | amount:duration | Add absorption hearts |

#### Damage Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `DEAL_DAMAGE` | amount | Deal damage to target |
| `TRUE_DAMAGE` | amount | Deal true damage (ignores armor) |

#### Ability Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `ACTIVE_ABILITY` | ability:id:duration | Activate ability |
| `DEACTIVE_ABILITY` | ability:id:duration | Deactivate ability |
| `ACTIVE_DOUBLE_JUMP` | id:power:cooldown | Enable double jump |
| `DEACTIVE_DOUBLE_JUMP` | id | Disable double jump |
| `ACTIVE_DASH` | id:power:cooldown | Enable dash |
| `DEACTIVE_DASH` | id | Disable dash |
| `ACTIVE_FLASH` | id:distance:cooldown | Enable flash |
| `DEACTIVE_FLASH` | id | Disable flash |
| `SET_FLIGHT` | true/false | Set flight ability |

#### Mining Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `ENABLE_TELEPATHY` | - | Enable telepathy (auto-pickup) |
| `DISABLE_TELEPATHY` | - | Disable telepathy |
| `ENABLE_AUTO_SELL` | - | Enable auto-sell |
| `DISABLE_AUTO_SELL` | - | Disable auto-sell |
| `ADD_FURNACE_MINING` | id | Enable furnace mining (auto-smelt) |
| `REMOVE_FURNACE_MINING` | id | Disable furnace mining |
| `ADD_VEIN_MINING` | id:radius:max | Enable vein mining |
| `REMOVE_VEIN_MINING` | id | Disable vein mining |
| `ADD_EXPLOSION_MINING` | id:radius:max | Enable explosion mining |
| `REMOVE_EXPLOSION_MINING` | id | Disable explosion mining |

#### Bonus Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `ADD_MOB_BONUS` | type:id:target:amount:min:max | Add mob kill bonus |
| `REMOVE_MOB_BONUS` | type:id | Remove mob kill bonus |
| `ADD_BLOCK_BONUS` | type:id:target:amount:min:max | Add block break bonus |
| `REMOVE_BLOCK_BONUS` | type:id | Remove block break bonus |
| `ADD_BLOCK_DROP_BONUS_MINING` | type:id:amount | Add block drop bonus |
| `REMOVE_BLOCK_DROP_BONUS_MINING` | type:id | Remove block drop bonus |

#### Combat Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `PULL` | power | Pull target toward player |
| `FIXED_PULL` | power:fixed_distance | Pull with fixed distance |
| `LIGHTNING` | location | Strike lightning |
| `EXPLOSION` | location:power:fire:break | Create explosion |
| `ON_FIRE` | duration | Set target on fire |
| `SHOOT_ARROW` | power:spread | Shoot arrows |
| `WIND_CHARGE` | power | Launch wind charge |

#### Bow Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `ENABLE_MULTIPLE_ARROW` | count:spread:damage:cooldown | Enable multi-shot |
| `DISABLE_MULTIPLE_ARROW` | - | Disable multi-shot |

#### Summon Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `SUMMON_GUARD` | type:duration | Summon guard mob |
| `SUMMON_CUSTOM_GUARD` | config:duration | Summon custom guard |
| `SUMMON_BABY_ZOMBIE_GUARD` | duration | Summon baby zombie guard |
| `REMOVE_GUARD` | type | Remove guards |

#### Utility Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `MESSAGE` | message | Send message to player |
| `ADVANCED_MESSAGE` | type:message | Send advanced message |
| `PLAY_SOUND` | sound:volume:pitch | Play sound |
| `TELEPORT` | location | Teleport player |
| `DURABILITY` | operation:slot:amount | Modify item durability |
| `SET_BLOCK` | location:material | Set block |

#### Storage Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `NUMBER_STORAGE` | operation:key:value | Store/modify number |
| `TEXT_STORAGE` | operation:key:value | Store/modify text |
| `REMOVE_TASK` | name | Cancel delayed task |
| `REMOVE_TASK_ASYNC` | name | Cancel delayed task (async) |

#### Visual Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `PACKET_PARTICLE` | type:location:count:... | Spawn particles |
| `PACKET_REDSTONE_PARTICLE` | location:r:g:b:size | Spawn redstone particle |
| `PACKET_CIRCLE_REDSTONE_PARTICLE` | location:radius:count:r:g:b | Spawn circle |
| `PACKET_SPIRAL_REDSTONE_PARTICLE` | location:radius:height:r:g:b | Spawn spiral |
| `SET_STAFF_PARTICLE` | config | Set staff particle effect |
| `REMOVE_STAFF_PARTICLE` | - | Remove staff particle |

#### Slot Effects

| Effect | Arguments | Description |
|--------|-----------|-------------|
| `ACTIVE_EQUIP_SLOT` | slot | Activate equipment slot |
| `DEACTIVE_EQUIP_SLOT` | slot | Deactivate equipment slot |

### Example Effects

```yaml
effects:
  - "ADD_POTION:SPEED:1:100"                           # Speed II for 5 seconds
  - "TARGET=ENEMY | ADD_POTION:POISON:0:40"           # Poison enemy for 2 seconds
  - "DELAY=20 | MESSAGE:&a1 second passed!"           # Delayed message
  - "ADD_ATTRIBUTE:MOVEMENT_SPEED:speed_boost:0.1:1"  # +10% movement speed
  - "NAME=remove_speed,DELAY=100 | REMOVE_ATTRIBUTE:MOVEMENT_SPEED:speed_boost"
  - "ADD_FOREVER_POTION:night_vision:NIGHT_VISION:0"  # Permanent night vision
  - "NUMBER_STORAGE:SET:my_counter:0"                 # Set counter to 0
  - "NUMBER_STORAGE:ADD:my_counter:%damage%"          # Add damage to counter
```

---

## Options Reference

Options modify damage/defense calculations.

### Option Format

```
OPTION_TYPE:value:operation
```

### Available Options

| Option | Description |
|--------|-------------|
| `ATTACK` | Modify attack damage |
| `DEFENSE` | Modify defense (damage reduction) |

### Operations

| Operation | Description |
|-----------|-------------|
| `0` | Add flat value |
| `1` | Multiply by (1 + value) |
| `2` | Multiply by value |
| `3` | Set to value |

### Example Options

```yaml
options:
  - "ATTACK:10:0"      # +10 flat damage
  - "ATTACK:0.5:2"     # +50% damage (multiply by 0.5)
  - "DEFENSE:0.25:2"   # -25% damage taken
  - "ATTACK:-0.1:2"    # -10% damage (for balanced effects)
```

---

## Advanced Features

### Target Filter

Filter nearby entities for area effects:

```yaml
target-filter:
  target: ENEMY                # Target type
  except-player: true          # Exclude the caster
  except-enemy: false          # Include initial target
  min-distance: 0              # Minimum distance
  max-distance: 4              # Maximum distance
target-conditions:
  - "FACTION_RELATION:MEMBER"  # Only faction members
target-effects:
  - "HEALTH:ADD:10"            # Heal allies
```

### Number Storage Variables

Use stored values and placeholders in conditions/effects:

| Variable | Description |
|----------|-------------|
| `%damage%` | Current damage amount |
| `%time%` | Current time in milliseconds |
| `%player_value%` | Player's current health |
| `%player_max_value%` | Player's max health |

### Mathematical Expressions

Effects support basic math in value fields:

```yaml
effects:
  - "HEALTH:ADD:1+(%player_max_value%-%player_value%)*5/100"
```

### Conditional Flow Control

Control execution flow with break flags:

```yaml
defense1:
  type: DEFENSE
  conditions:
    - "NUMBER_STORAGE:%damage%:>=:1000"
  options:
    - "DEFENSE:0.50:2"
  true-condition-break: true    # Stop if damage >= 1000

defense2:
  type: DEFENSE
  conditions:
    - "NUMBER_STORAGE:%damage%:>=:500"
  options:
    - "DEFENSE:0.25:2"
  true-condition-break: true    # Stop if damage >= 500
```

---

## Examples

### Simple Potion Enchantment (Glowing)

```yaml
glowing:
  group: rare
  display: Glowing
  description:
    - "&eGrants night vision when equipped"
  applies-description:
    - Helmet
  applies:
    - ALL_HELMET
  max-level: 1
  enchant-point: 1
  valuable: 0
  levels:
    "1":
      equip:
        type: ARMOR_EQUIP
        chance: 100
        cooldown: 0
        conditions:
          - ""
        effects:
          - "ADD_FOREVER_POTION:glowing:NIGHT_VISION:0"
      unequip:
        type: ARMOR_UNDRESS
        chance: 100
        cooldown: 0
        conditions:
          - ""
        effects:
          - "REMOVE_FOREVER_POTION:glowing"
```

### Combat Enchantment (Piercing)

```yaml
piercing:
  group: rare
  display: Piercing
  description:
    - "&eChance to deal bonus damage"
    - "&ewhen hitting with arrows"
  detail-description:
    - "&eChance to deal bonus damage"
    - "&ewhen hitting with arrows"
    - ""
    - "&eDamage: +15% Total ATK x Level"
    - "&eActivation Chance: 50%"
    - "&eCooldown: 1s"
  applies-description:
    - Bow
  applies:
    - ALL_BOW
  max-level: 5
  enchant-point: 1
  valuable: 0
  levels:
    "1":
      arrow_hit:
        type: ARROW_HIT
        chance: 50
        cooldown: 1000
        options:
          - "ATTACK:0.15:2"
        conditions:
          - "EQUIP_SLOT:MAINHAND"
    "2":
      arrow_hit:
        type: ARROW_HIT
        chance: 50
        cooldown: 1000
        options:
          - "ATTACK:0.30:2"
        conditions:
          - "EQUIP_SLOT:MAINHAND"
    # ... levels 3-5
```

### Passive Tool Enchantment (Telepathy)

```yaml
telepathy:
  group: legendary
  display: Telepathy
  description:
    - "&eAutomatically picks up mined blocks"
  applies-description:
    - Tools
  applies:
    - ALL_PICKAXE
    - ALL_SHOVEL
    - ALL_AXE
    - ALL_HOE
  max-level: 1
  enchant-point: 1
  valuable: 0
  levels:
    "1":
      equip:
        type: HOLD
        chance: 100
        cooldown: 0
        conditions:
          - ""
        effects:
          - "ENABLE_TELEPATHY"
      unequip:
        type: CHANGE_HAND
        chance: 100
        cooldown: 0
        conditions:
          - ""
        effects:
          - "DISABLE_TELEPATHY"
```

### Area Effect Enchantment (Doctor)

```yaml
doctor:
  group: epic
  display: Doctor
  description:
    - "&eHeal nearby allies when hitting faction members"
  applies-description:
    - Bow
  applies:
    - ALL_BOW
  max-level: 1
  enchant-point: 1
  valuable: 0
  levels:
    "1":
      arrow_hit:
        type: ARROW_HIT
        chance: 100
        cooldown: 10000
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "FACTION_RELATION:MEMBER"
        options:
          - "ATTACK:-1:2"  # Reduce damage to allies
        target-filter:
          target: ENEMY
          except-player: true
          except-enemy: false
          min-distance: 0
          max-distance: 4
        target-conditions:
          - "FACTION_RELATION:MEMBER"
        target-effects:
          - "TARGET=ENEMY,DELAY=8 | HEALTH:ADD:1+(%player_max_value%-%player_value%)*1/100"
          # ... more delayed heals
```

---

## Tips and Best Practices

1. **Use descriptive function names** - Makes configs easier to maintain
2. **Always include conditions** - Even if empty (`- ""`)
3. **Use cooldowns** - Prevent enchantment spam
4. **Test thoroughly** - Complex enchantments can have unexpected interactions
5. **Document your enchantments** - Use comments (`# comment`) for clarity
6. **Consider balance** - Higher levels should scale appropriately
7. **Use `true-condition-break`** - For tiered effects (damage thresholds)
8. **Clean up on unequip** - Remove any permanent effects when items are unequipped
9. **Use unique IDs** - For effects that need to be removed (potions, attributes)
10. **Consider PvP vs PvE** - Use `ENTITY_TYPE:PLAYER` for PvP-only effects
