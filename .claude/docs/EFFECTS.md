# CustomEnchantment Effects Reference

Complete reference for all effects available in the CustomEnchantment plugin. Effects define what happens when an enchantment activates.

## Table of Contents

1. [Effect Format](#effect-format)
2. [Effect Prefixes](#effect-prefixes)
3. [Potion Effects](#potion-effects)
4. [Attribute Effects](#attribute-effects)
5. [Stats Effects](#stats-effects)
6. [Damage Effects](#damage-effects)
7. [Ability Effects](#ability-effects)
8. [Mining Effects](#mining-effects)
9. [Bonus Effects](#bonus-effects)
10. [Combat Effects](#combat-effects)
11. [Summon Effects](#summon-effects)
12. [Utility Effects](#utility-effects)
13. [Storage Effects](#storage-effects)
14. [Visual Effects](#visual-effects)
15. [Slot Effects](#slot-effects)
16. [Placeholders](#placeholders)

---

## Effect Format

Effects use the format:
```
[PREFIX |] EFFECT_TYPE:arg1:arg2:...
```

Multiple prefixes can be combined with commas.

---

## Effect Prefixes

| Prefix | Description | Example |
|--------|-------------|---------|
| `TARGET=ENEMY` | Apply effect to enemy | `TARGET=ENEMY \| ADD_POTION:POISON:0:40` |
| `TARGET=PLAYER` | Apply effect to player (default) | `TARGET=PLAYER \| HEALTH:ADD:10` |
| `DELAY=ticks` | Delay effect (20 ticks = 1 second) | `DELAY=20 \| MESSAGE:&a1 second!` |
| `NAME=name` | Name delayed task for cancellation | `NAME=speed_boost \| ADD_POTION:SPEED:1:100` |
| `EFFECT_AFTER_DEAD=true` | Execute even if target died | `EFFECT_AFTER_DEAD=true \| MESSAGE:&cEnemy killed!` |
| `EFFECT_ON_FAKE_SOURCE=true` | Execute on fake damage sources | Used for internal mechanics |

---

## Potion Effects

### ADD_POTION
Adds a temporary potion effect.

**Format:** `ADD_POTION:type:level:duration`

| Parameter | Type | Description |
|-----------|------|-------------|
| `type` | PotionEffectType | Potion type (SPEED, STRENGTH, etc.) or comma-separated list |
| `level` | Integer/Range | Amplifier (0 = level I, 1 = level II) |
| `duration` | Integer/Range | Duration in ticks (20 = 1 second) |

**Examples:**
```yaml
- "ADD_POTION:SPEED:1:100"                    # Speed II for 5 seconds
- "ADD_POTION:SPEED,STRENGTH:0:200"           # Speed I and Strength I for 10 seconds
- "TARGET=ENEMY | ADD_POTION:POISON:0:40"     # Poison enemy for 2 seconds
```

**Source:** `enchant/effect/EffectAddPotion.java:26-30`

---

### REMOVE_POTION
Removes an active potion effect.

**Format:** `REMOVE_POTION:type`

| Parameter | Type | Description |
|-----------|------|-------------|
| `type` | PotionEffectType | Potion type to remove |

---

### ADD_RANDOM_POTION
Adds random potion effects from a pool.

**Format:** `ADD_RANDOM_POTION:mode:amount:types:level:duration`

| Parameter | Type | Description |
|-----------|------|-------------|
| `mode` | String | `DUPLICATE` (can repeat) or `UNIQUE` (no repeats) |
| `amount` | Integer/Range | Number of potions to apply |
| `types` | String | Comma-separated list of potion types |
| `level` | Integer/Range | Amplifier level |
| `duration` | Integer/Range | Duration in ticks |

**Example:**
```yaml
- "ADD_RANDOM_POTION:UNIQUE:2:SPEED,STRENGTH,RESISTANCE,REGENERATION:1:100"
```

**Source:** `enchant/effect/EffectAddRandomPotion.java:32-38`

---

### ADD_FOREVER_POTION
Adds a permanent potion effect (persists until removed).

**Format:** `ADD_FOREVER_POTION:id:type:level`

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | String | Unique identifier for this effect |
| `type` | PotionEffectType | Potion type |
| `level` | Integer | Amplifier level |

**Example:**
```yaml
- "ADD_FOREVER_POTION:glowing_vision:NIGHT_VISION:0"
```

**Source:** `enchant/effect/EffectAddForeverPotion.java:24-28`

---

### REMOVE_FOREVER_POTION
Removes a permanent potion effect by ID.

**Format:** `REMOVE_FOREVER_POTION:id`

**Example:**
```yaml
- "REMOVE_FOREVER_POTION:glowing_vision"
```

---

### BLOCK_FOREVER_POTION
Blocks a potion type from being applied permanently.

**Format:** `BLOCK_FOREVER_POTION:id:type`

---

### UNBLOCK_FOREVER_POTION
Unblocks a previously blocked potion type.

**Format:** `UNBLOCK_FOREVER_POTION:id`

---

## Attribute Effects

### ADD_ATTRIBUTE
Adds a vanilla Minecraft attribute modifier.

**Format:** `ADD_ATTRIBUTE:attribute:id:amount:operation`

| Parameter | Type | Description |
|-----------|------|-------------|
| `attribute` | Attribute | Vanilla attribute type |
| `id` | String | Unique modifier ID |
| `amount` | Double | Modifier amount |
| `operation` | Integer | 0=Add, 1=AddPercent, 2=Multiply |

**Available Attributes:**
- `GENERIC_MAX_HEALTH` - Maximum health
- `GENERIC_MOVEMENT_SPEED` - Movement speed
- `GENERIC_ATTACK_DAMAGE` - Attack damage
- `GENERIC_ATTACK_SPEED` - Attack speed
- `GENERIC_ARMOR` - Armor points
- `GENERIC_ARMOR_TOUGHNESS` - Armor toughness
- `GENERIC_KNOCKBACK_RESISTANCE` - Knockback resistance
- `GENERIC_LUCK` - Luck

**Example:**
```yaml
- "ADD_ATTRIBUTE:GENERIC_MOVEMENT_SPEED:speed_boost:0.1:1"  # +10% speed
```

**Source:** `enchant/effect/EffectAddAttribute.java:23-28`

---

### REMOVE_ATTRIBUTE
Removes an attribute modifier by ID.

**Format:** `REMOVE_ATTRIBUTE:attribute:id`

**Example:**
```yaml
- "REMOVE_ATTRIBUTE:GENERIC_MOVEMENT_SPEED:speed_boost"
```

---

### ADD_AUTO_ATTRIBUTE
Adds an auto-indexed attribute (for stackable effects).

**Format:** `ADD_AUTO_ATTRIBUTE:attribute:id:index:amount:operation`

| Parameter | Type | Description |
|-----------|------|-------------|
| `attribute` | Attribute | Attribute type |
| `id` | String | Base modifier ID |
| `index` | Integer | Index for auto-naming |
| `amount` | Double | Modifier amount |
| `operation` | Integer | Operation type |

**Source:** `enchant/effect/EffectAddAutoAttribute.java:24-30`

---

### ADD_CUSTOM_ATTRIBUTE
Adds a custom plugin attribute.

**Format:** `ADD_CUSTOM_ATTRIBUTE:id:attribute_type:amount:operation`

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | String | Modifier ID |
| `attribute_type` | CustomAttributeType | Custom attribute type |
| `amount` | Double/Range | Amount |
| `operation` | Integer | Operation (default: 0) |

**Custom Attribute Types:**
- `STAT_HEALTH` - Health stats
- `STAT_FOOD` - Food stats
- `STAT_OXYGEN` - Oxygen stats
- `STAT_EXP` - Experience stats
- `STAT_ABSORPTION_HEART` - Absorption hearts

**Source:** `enchant/effect/EffectAddCustomAttribute.java:21-24`

---

## Stats Effects

### HEALTH
Modifies player health.

**Format:** `HEALTH:operation:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `operation` | ModifyType | ADD, REMOVE, or SET |
| `value` | String | Amount (supports placeholders and math) |

**Examples:**
```yaml
- "HEALTH:ADD:10"                                        # Heal 10 HP
- "HEALTH:REMOVE:5"                                      # Damage 5 HP
- "HEALTH:SET:20"                                        # Set to 20 HP
- "HEALTH:ADD:%player_max_value%*0.1"                    # Heal 10% max HP
```

**Placeholders:**
- `%player_value%` - Current health
- `%player_max_value%` - Max health
- `%enemy_value%` - Enemy's current health
- `%enemy_max_value%` - Enemy's max health

**Source:** `enchant/effect/EffectHealth.java:27-30`

---

### FOOD
Modifies player food level.

**Format:** `FOOD:operation:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `operation` | ModifyType | ADD, REMOVE, or SET |
| `value` | String | Amount |

**Example:**
```yaml
- "FOOD:ADD:2"   # Restore 1 hunger bar
- "FOOD:SET:-1"  # Restore to full if below max
```

**Source:** `enchant/effect/EffectFood.java:26-30`

---

### OXYGEN
Modifies player oxygen/air level.

**Format:** `OXYGEN:operation:value`

**Source:** `enchant/effect/EffectOxygen.java:26-30`

---

### EXP
Modifies player experience.

**Format:** `EXP:operation:value`

**Example:**
```yaml
- "EXP:ADD:100"     # Add 100 experience points
- "EXP:REMOVE:50"   # Remove 50 experience points
```

**Source:** `enchant/effect/EffectExp.java:27-30`

---

### ABSORPTION_HEART
Adds absorption (golden) hearts.

**Format:** `ABSORPTION_HEART:operation:value:limit`

| Parameter | Type | Description |
|-----------|------|-------------|
| `operation` | ModifyType | ADD, REMOVE, or SET |
| `value` | String | Amount of hearts |
| `limit` | Float | Optional max limit |

**Source:** `enchant/effect/EffectAbsorptionHeart.java:28-32`

---

## Damage Effects

### DEAL_DAMAGE
Deals damage with full calculation (armor, potions, etc.).

**Format:** `DEAL_DAMAGE:value:potion_attacker:potion_defender:armor:defense:toughness`

| Parameter | Type | Description |
|-----------|------|-------------|
| `value` | String | Damage amount |
| `potion_attacker` | Boolean | Include attacker potion effects |
| `potion_defender` | Boolean | Include defender potion effects |
| `armor` | Boolean | Include armor reduction |
| `defense` | Boolean | Include defense points |
| `toughness` | Boolean | Include armor toughness |

**Example:**
```yaml
- "TARGET=ENEMY | DEAL_DAMAGE:10:true:true:true:true:true"
```

**Source:** `enchant/effect/EffectDealDamage.java:29-36`

---

### TRUE_DAMAGE
Deals true damage (ignores all defenses).

**Format:** `TRUE_DAMAGE:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `value` | String | Damage amount (supports placeholders) |

**Example:**
```yaml
- "TARGET=ENEMY | TRUE_DAMAGE:5"
- "TARGET=ENEMY | TRUE_DAMAGE:%player_max_value%*0.05"
```

**Source:** `enchant/effect/EffectTrueDamage.java:22-24`

---

## Ability Effects

### ACTIVE_ABILITY / DEACTIVE_ABILITY
Enables/disables player abilities.

**Format:** `ACTIVE_ABILITY:type:id` / `DEACTIVE_ABILITY:type:id`

| Parameter | Type | Description |
|-----------|------|-------------|
| `type` | PlayerAbility.Type | Ability type |
| `id` | String | Unique ability ID |

**Source:** `enchant/effect/EffectActiveAbility.java:19-22`

---

### ACTIVE_DOUBLE_JUMP
Enables double jump ability.

**Format:** `ACTIVE_DOUBLE_JUMP:power:cooldown:particle_version:particle:cooldown_message`

| Parameter | Type | Description |
|-----------|------|-------------|
| `power` | Double | Jump power |
| `cooldown` | Long | Cooldown in milliseconds |
| `particle_version` | String | Particle version |
| `particle` | String | Particle name |
| `cooldown_message` | String | Message shown during cooldown |

**Source:** `enchant/effect/EffectActiveDoubleJump.java:26-36`

---

### DEACTIVE_DOUBLE_JUMP
Disables double jump ability.

**Format:** `DEACTIVE_DOUBLE_JUMP`

---

### ACTIVE_DASH
Enables dash ability.

**Format:** `ACTIVE_DASH:power:cooldown:particle_version:forward_particle:backward_particle:cooldown_message`

| Parameter | Type | Description |
|-----------|------|-------------|
| `power` | Double | Dash power |
| `cooldown` | Long | Cooldown in milliseconds |
| `particle_version` | String | Particle version |
| `forward_particle` | String | Forward particle name |
| `backward_particle` | String | Backward particle name |
| `cooldown_message` | String | Cooldown message |

**Source:** `enchant/effect/EffectActiveDash.java:27-38`

---

### ACTIVE_FLASH
Enables flash/teleport ability.

**Format:** `ACTIVE_FLASH:power:smart:cooldown:particle_version:particle:cooldown_message`

| Parameter | Type | Description |
|-----------|------|-------------|
| `power` | Double | Flash distance |
| `smart` | Boolean | Use smart positioning |
| `cooldown` | Long | Cooldown in milliseconds |
| `particle_version` | String | Particle version |
| `particle` | String | Particle name |
| `cooldown_message` | String | Cooldown message |

**Source:** `enchant/effect/EffectActiveFlash.java:27-35`

---

### SET_FLIGHT
Enables or disables player flight.

**Format:** `SET_FLIGHT:allow`

| Parameter | Type | Description |
|-----------|------|-------------|
| `allow` | Boolean | true/false |

**Source:** `enchant/effect/EffectSetFlight.java:19-21`

---

## Mining Effects

### ENABLE_TELEPATHY / DISABLE_TELEPATHY
Enables/disables automatic item pickup when mining.

**Format:** `ENABLE_TELEPATHY` / `DISABLE_TELEPATHY`

---

### ENABLE_AUTO_SELL / DISABLE_AUTO_SELL
Enables/disables automatic selling of mined items.

**Format:** `ENABLE_AUTO_SELL` / `DISABLE_AUTO_SELL`

---

### ADD_FURNACE_MINING
Enables auto-smelt for mined blocks.

**Format:** `ADD_FURNACE_MINING:id:chance`

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | String | Unique ID |
| `chance` | Double | Smelt chance (0-100) |

**Source:** `enchant/effect/EffectAddFurnaceMining.java:17-20`

---

### ADD_VEIN_MINING
Enables vein mining (mine connected blocks).

**Format:** `ADD_VEIN_MINING:id:max_blocks:chance:materials`

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | String | Unique ID |
| `max_blocks` | Integer | Maximum blocks to mine |
| `chance` | Double | Activation chance |
| `materials` | String | Comma-separated material list |

**Source:** `enchant/effect/EffectAddVeinMining.java:20-23`

---

### ADD_EXPLOSION_MINING
Enables explosion mining.

**Format:** `ADD_EXPLOSION_MINING:id:radius:chance:fire:replace:materials`

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | String | Unique ID |
| `radius` | Integer | Explosion radius |
| `chance` | Double | Activation chance |
| `fire` | Boolean | Create fire |
| `replace` | Boolean | Replace blocks |
| `materials` | String | Optional material filter |

**Source:** `enchant/effect/EffectAddExplosionMining.java:20-29`

---

## Bonus Effects

### ADD_BLOCK_BONUS
Adds bonus rewards when breaking blocks.

**Format:** `ADD_BLOCK_BONUS:type:id:materials:amount:operation:chance`

| Parameter | Type | Description |
|-----------|------|-------------|
| `type` | String | EXP or MONEY |
| `id` | String | Unique ID |
| `materials` | String | Material list reference |
| `amount` | Range | Bonus amount |
| `operation` | Integer | Operation type (default: 0) |
| `chance` | Integer | Chance (default: 100) |

**Source:** `enchant/effect/EffectAddBlockBonus.java:25-32`

---

### ADD_MOB_BONUS
Adds bonus rewards when killing mobs.

**Format:** `ADD_MOB_BONUS:type:id:entities:amount:operation:chance`

| Parameter | Type | Description |
|-----------|------|-------------|
| `type` | String | EXP, MONEY, or MOB_SLAYER_EXP |
| `id` | String | Unique ID |
| `entities` | String | Entity type list |
| `amount` | Range | Bonus amount |
| `operation` | Integer | Operation type |
| `chance` | Integer | Chance percentage |

**Source:** `enchant/effect/EffectAddMobBonus.java:26-33`

---

### ADD_BLOCK_DROP_BONUS_MINING
Adds bonus drops when mining specific blocks.

**Format:** `ADD_BLOCK_DROP_BONUS_MINING:id:type:require:reward:amounts:remove:chance`

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | String | Unique ID |
| `type` | BonusType | Bonus type |
| `require` | String | Required materials (comma-separated) |
| `reward` | String | Reward material |
| `amounts` | String | Amount mapping (fortune level -> range) |
| `remove` | Boolean | Remove original drop |
| `chance` | String | Chance percentage |

**Source:** `enchant/effect/EffectAddBlockDropBonusMining.java:32-40`

---

## Combat Effects

### LIGHTNING
Strikes lightning at a location.

**Format:** `LIGHTNING:location_format`

| Parameter | Type | Description |
|-----------|------|-------------|
| `location_format` | String | Location format (PLAYER, ENEMY, etc.) |

**Example:**
```yaml
- "LIGHTNING:ENEMY"
```

**Source:** `enchant/effect/EffectLightning.java:21-22`

---

### EXPLOSION
Creates an explosion.

**Format:** `EXPLOSION:location:power:fire:break_blocks`

| Parameter | Type | Description |
|-----------|------|-------------|
| `location` | String | Location format |
| `power` | Range | Explosion power |
| `fire` | Boolean | Create fire |
| `break_blocks` | Boolean | Break blocks |

**Source:** `enchant/effect/EffectExplosion.java:26-31`

---

### ON_FIRE
Sets target on fire.

**Format:** `ON_FIRE:ticks`

| Parameter | Type | Description |
|-----------|------|-------------|
| `ticks` | Range | Fire duration in ticks |

**Source:** `enchant/effect/EffectOnFire.java:20-22`

---

### PULL
Applies velocity to target.

**Format:** `PULL:vector_format`

| Parameter | Type | Description |
|-----------|------|-------------|
| `vector_format` | String | Vector format string |

**Source:** `enchant/effect/EffectPull.java:17-19`

---

### FIXED_PULL
Applies velocity with fixed distance.

**Format:** `FIXED_PULL:vector_format`

**Source:** `enchant/effect/EffectFixedPull.java`

---

### SHOOT_ARROW
Shoots arrows.

**Format:** `SHOOT_ARROW:spawn_location:destination:speed:spread:active_ce`

| Parameter | Type | Description |
|-----------|------|-------------|
| `spawn_location` | String | Arrow spawn location |
| `destination` | String | Target location |
| `speed` | Range | Arrow speed |
| `spread` | Range | Arrow spread |
| `active_ce` | Boolean | Enable enchantments on arrow |

**Source:** `enchant/effect/EffectShootArrow.java:30-36`

---

### WIND_CHARGE
Launches a wind charge projectile.

**Format:** `WIND_CHARGE:spawn_location:destination:speed:spread`

**Source:** `enchant/effect/EffectWindCharge.java:29-34`

---

### ENABLE_MULTIPLE_ARROW / DISABLE_MULTIPLE_ARROW
Enables/disables multi-shot for bows.

**Format:** `ENABLE_MULTIPLE_ARROW` / `DISABLE_MULTIPLE_ARROW`

---

## Summon Effects

### SUMMON_GUARD
Summons a guard mob.

**Format:** `SUMMON_GUARD:id:entity_type:location:speed:player_range:attack_range:alive_time`

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | String | Guard name (supports %player% placeholder) |
| `entity_type` | EntityType | Mob type (ZOMBIE, SKELETON, etc.) |
| `location` | String | Spawn location format |
| `speed` | Double | Movement speed |
| `player_range` | Double | Follow range |
| `attack_range` | Double | Attack range |
| `alive_time` | Long | Duration in milliseconds |

**Example:**
```yaml
- "SUMMON_GUARD:%player%_guard_1:ZOMBIE:PLAYER+0,2,0:0.3:10:4:30000"
```

**Source:** `enchant/effect/EffectSummonGuard.java:32-40`

---

### SUMMON_CUSTOM_GUARD
Summons a custom configured guard.

**Source:** `enchant/effect/EffectSummonCustomGuard.java`

---

### REMOVE_GUARD
Removes guards.

**Format:** `REMOVE_GUARD:type`

---

## Utility Effects

### MESSAGE
Sends a message to the player.

**Format:** `MESSAGE:message`

| Parameter | Type | Description |
|-----------|------|-------------|
| `message` | String | Message with color codes and placeholders |

**Source:** `enchant/effect/EffectMessage.java:17-19`

---

### ADVANCED_MESSAGE
Sends a message with full placeholder support.

**Format:** `ADVANCED_MESSAGE:message`

Supports all CEPlaceholder and TemporaryStorage placeholders.

**Source:** `enchant/effect/EffectAdvancedMessage.java:20-22`

---

### PLAY_SOUND
Plays a sound.

**Format:** `PLAY_SOUND:sound:volume:pitch:distance`

| Parameter | Type | Description |
|-----------|------|-------------|
| `sound` | String | Sound name(s) comma-separated |
| `volume` | Range | Volume (0.0-1.0) |
| `pitch` | Range | Pitch (0.5-2.0) |
| `distance` | Double | Optional max distance for nearby players |

**Source:** `enchant/effect/EffectPlaySound.java:25-32`

---

### TELEPORT
Teleports the player.

**Format:** `TELEPORT:location_format`

| Parameter | Type | Description |
|-----------|------|-------------|
| `location_format` | String | Location format |

**Source:** `enchant/effect/EffectTeleport.java:17-19`

---

### DURABILITY
Modifies item durability.

**Format:** `DURABILITY:operation:slots:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `operation` | ModifyType | ADD, REMOVE, or SET |
| `slots` | String | Comma-separated slots (MAINHAND, AUTO, etc.) |
| `value` | Range | Durability amount |

**Source:** `enchant/effect/EffectDurability.java:27-31`

---

### SET_BLOCK
Sets a block at a location.

**Format:** `SET_BLOCK:location:material`

**Source:** `enchant/effect/EffectSetBlock.java`

---

## Storage Effects

### NUMBER_STORAGE
Stores/modifies a number in player's temporary storage.

**Format:** `NUMBER_STORAGE:operation:key:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `operation` | Type | ADD, SUB, REMOVE, SET, CLEAR |
| `key` | String | Storage key |
| `value` | String | Value (supports placeholders and math) |

**Examples:**
```yaml
- "NUMBER_STORAGE:SET:combo:0"           # Initialize counter
- "NUMBER_STORAGE:ADD:combo:1"           # Increment by 1
- "NUMBER_STORAGE:ADD:damage:%damage%"   # Add damage to total
- "NUMBER_STORAGE:REMOVE:combo"          # Clear the key
```

**Source:** `enchant/effect/EffectNumberStorage.java:27-32`

---

### TEXT_STORAGE
Stores/modifies a text value.

**Format:** `TEXT_STORAGE:operation:key:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `operation` | Type | SET or REMOVE |
| `key` | String | Storage key |
| `value` | String | Value (optional for REMOVE) |

**Source:** `enchant/effect/EffectTextStorage.java:27-32`

---

### REMOVE_TASK
Cancels a named delayed task.

**Format:** `REMOVE_TASK:name`

| Parameter | Type | Description |
|-----------|------|-------------|
| `name` | String | Task name set via NAME= prefix |

**Source:** `enchant/effect/EffectRemoveTask.java`

---

### REMOVE_TASK_ASYNC
Cancels a named delayed task asynchronously.

**Format:** `REMOVE_TASK_ASYNC:name`

**Source:** `enchant/effect/EffectRemoveTaskAsync.java`

---

## Visual Effects

### PACKET_PARTICLE
Spawns particles.

**Format:** `PACKET_PARTICLE:particles:distance:last_move_time:location:offset:data:count`

| Parameter | Type | Description |
|-----------|------|-------------|
| `particles` | String | Particle type(s) |
| `distance` | Integer | View distance |
| `last_move_time` | Long | Time since last move (0 = always, negative = only when still) |
| `location` | String | Location format |
| `offset` | String | Offset vector format |
| `data` | Float | Particle speed/data |
| `count` | Range | Particle count |

**Source:** `enchant/effect/EffectPacketParticle.java:36-44`

---

### PACKET_REDSTONE_PARTICLE
Spawns colored redstone dust particles.

**Format:** `PACKET_REDSTONE_PARTICLE:location:r:g:b:size`

**Source:** `enchant/effect/EffectPacketRedstoneParticle.java`

---

### PACKET_CIRCLE_REDSTONE_PARTICLE
Spawns a circle of colored particles.

**Format:** `PACKET_CIRCLE_REDSTONE_PARTICLE:location:radius:count:r:g:b`

**Source:** `enchant/effect/EffectPacketCircleRedstoneParticle.java`

---

### PACKET_SPIRAL_REDSTONE_PARTICLE
Spawns a spiral of colored particles.

**Format:** `PACKET_SPIRAL_REDSTONE_PARTICLE:location:radius:height:r:g:b`

**Source:** `enchant/effect/EffectPacketSpiralRedstoneParticle.java`

---

### SET_STAFF_PARTICLE
Sets a particle trail for staff weapons.

**Format:** `SET_STAFF_PARTICLE:config`

**Source:** `enchant/effect/EffectSetStaffParticle.java`

---

### REMOVE_STAFF_PARTICLE
Removes staff particle trail.

**Format:** `REMOVE_STAFF_PARTICLE`

**Source:** `enchant/effect/EffectRemoveStaffParticle.java`

---

## Slot Effects

### ACTIVE_EQUIP_SLOT
Activates an equipment slot.

**Format:** `ACTIVE_EQUIP_SLOT:slot:id`

| Parameter | Type | Description |
|-----------|------|-------------|
| `slot` | EquipSlot | Equipment slot |
| `id` | String | Unique ID |

**Source:** `enchant/effect/EffectActiveEquipSlot.java:19-22`

---

### DEACTIVE_EQUIP_SLOT
Deactivates an equipment slot.

**Format:** `DEACTIVE_EQUIP_SLOT:slot:id`

**Source:** `enchant/effect/EffectDeactiveEquipSlot.java`

---

## Placeholders

Effects support various placeholders in value fields:

### Combat Placeholders
| Placeholder | Description |
|-------------|-------------|
| `%damage%` | Current damage amount |
| `%time%` | Current timestamp |
| `%player_value%` | Player's current stat value |
| `%player_max_value%` | Player's max stat value |
| `%enemy_value%` | Enemy's current stat value |
| `%enemy_max_value%` | Enemy's max stat value |

### Storage Placeholders
| Placeholder | Description |
|-------------|-------------|
| `%storage_KEY%` | Value from NUMBER_STORAGE with key "KEY" |
| `%text_KEY%` | Value from TEXT_STORAGE with key "KEY" |

### Entity Placeholders
| Placeholder | Description |
|-------------|-------------|
| `%player%` | Player name |
| `%enemy%` | Enemy name |

### Mathematical Expressions
Effects support basic math in value fields:
```yaml
- "HEALTH:ADD:1+(%player_max_value%-%player_value%)*10/100"
- "NUMBER_STORAGE:ADD:total:%damage%*0.5"
```

---

## Complete Effect List Summary

| Category | Effects |
|----------|---------|
| Potion | ADD_POTION, REMOVE_POTION, ADD_RANDOM_POTION, REMOVE_RANDOM_POTION, ADD_FOREVER_POTION, REMOVE_FOREVER_POTION, BLOCK_FOREVER_POTION, UNBLOCK_FOREVER_POTION |
| Attribute | ADD_ATTRIBUTE, REMOVE_ATTRIBUTE, ADD_AUTO_ATTRIBUTE, REMOVE_AUTO_ATTRIBUTE, ADD_CUSTOM_ATTRIBUTE, REMOVE_CUSTOM_ATTRIBUTE |
| Stats | HEALTH, FOOD, OXYGEN, EXP, ABSORPTION_HEART |
| Damage | DEAL_DAMAGE, TRUE_DAMAGE |
| Ability | ACTIVE_ABILITY, DEACTIVE_ABILITY, ACTIVE_DOUBLE_JUMP, DEACTIVE_DOUBLE_JUMP, ACTIVE_DASH, DEACTIVE_DASH, ACTIVE_FLASH, DEACTIVE_FLASH, SET_FLIGHT |
| Mining | ENABLE_TELEPATHY, DISABLE_TELEPATHY, ENABLE_AUTO_SELL, DISABLE_AUTO_SELL, ADD_FURNACE_MINING, REMOVE_FURNACE_MINING, ADD_VEIN_MINING, REMOVE_VEIN_MINING, ADD_EXPLOSION_MINING, REMOVE_EXPLOSION_MINING |
| Bonus | ADD_BLOCK_BONUS, REMOVE_BLOCK_BONUS, ADD_MOB_BONUS, REMOVE_MOB_BONUS, ADD_BLOCK_DROP_BONUS_MINING, REMOVE_BLOCK_DROP_BONUS_MINING |
| Combat | LIGHTNING, EXPLOSION, ON_FIRE, PULL, FIXED_PULL, SHOOT_ARROW, WIND_CHARGE, ENABLE_MULTIPLE_ARROW, DISABLE_MULTIPLE_ARROW |
| Summon | SUMMON_GUARD, SUMMON_CUSTOM_GUARD, SUMMON_BABY_ZOMBIE_GUARD, REMOVE_GUARD |
| Utility | MESSAGE, ADVANCED_MESSAGE, PLAY_SOUND, TELEPORT, DURABILITY, SET_BLOCK |
| Storage | NUMBER_STORAGE, TEXT_STORAGE, REMOVE_TASK, REMOVE_TASK_ASYNC |
| Visual | PACKET_PARTICLE, PACKET_REDSTONE_PARTICLE, PACKET_CIRCLE_REDSTONE_PARTICLE, PACKET_SPIRAL_REDSTONE_PARTICLE, SET_STAFF_PARTICLE, REMOVE_STAFF_PARTICLE |
| Slot | ACTIVE_EQUIP_SLOT, DEACTIVE_EQUIP_SLOT |
