# CustomEnchantment Conditions Reference

Complete reference for all conditions available in the CustomEnchantment plugin. Conditions determine when an enchantment activates.

## Table of Contents

1. [Condition Format](#condition-format)
2. [Condition Prefixes](#condition-prefixes)
3. [Operators](#operators)
4. [Equipment Conditions](#equipment-conditions)
5. [Entity Conditions](#entity-conditions)
6. [Health & Stats Conditions](#health--stats-conditions)
7. [State Conditions](#state-conditions)
8. [Combat Conditions](#combat-conditions)
9. [Storage Conditions](#storage-conditions)
10. [Environment Conditions](#environment-conditions)
11. [Integration Conditions](#integration-conditions)

---

## Condition Format

Conditions use the format:
```
[PREFIX |] CONDITION_TYPE:arg1:arg2:...
```

Multiple prefixes can be combined with commas.

### Empty Conditions
If no conditions are needed, use an empty string:
```yaml
conditions:
  - ""
```

---

## Condition Prefixes

| Prefix | Description | Example |
|--------|-------------|---------|
| `TARGET=ENEMY` | Check condition on enemy | `TARGET=ENEMY \| ENTITY_TYPE:PLAYER` |
| `TARGET=PLAYER` | Check condition on player (default) | `TARGET=PLAYER \| HEALTH:<=:50` |
| `NEGATIVE=true` | Invert the condition result | `NEGATIVE=true \| ON_FIRE` (true if NOT on fire) |

**Combining Prefixes:**
```yaml
- "TARGET=ENEMY,NEGATIVE=true | ON_FIRE"  # True if enemy is NOT on fire
```

---

## Operators

Conditions that compare values use these operators:

| Operator | Symbol | Description |
|----------|--------|-------------|
| `EQUALS` | `=` | Equal to |
| `EQUALSIGNORECASE` | `==` | Equal (case-insensitive for strings) |
| `NOT_EQUALS` | `!=` | Not equal to |
| `BIGGER` | `>` | Greater than |
| `BIGGEREQUALS` | `>=` | Greater than or equal |
| `SMALLER` | `<` | Less than |
| `SMALLEREQUALS` | `<=` | Less than or equal |

**Source:** `api/CompareOperation.java:3-6`

---

## Equipment Conditions

### EQUIP_SLOT
Checks if the enchanted item is in a specific slot.

**Format:** `EQUIP_SLOT:slot`

| Parameter | Type | Description |
|-----------|------|-------------|
| `slot` | EquipSlot | Equipment slot to check |

**Available Slots:**

| Slot | Description |
|------|-------------|
| `MAINHAND` | Main hand |
| `OFFHAND` | Off hand |
| `HAND` | Either hand (MAINHAND or OFFHAND) |
| `HELMET` | Helmet slot |
| `CHESTPLATE` | Chestplate slot |
| `LEGGINGS` | Leggings slot |
| `BOOTS` | Boots slot |
| `ARMOR` | Any armor slot |
| `HOTBAR` | Any hotbar slot |
| `HOTBAR_1` - `HOTBAR_9` | Specific hotbar slot |
| `EXTRA_SLOT` | Any extra slot |
| `EXTRA_SLOT_1` - `EXTRA_SLOT_9` | Specific extra slot |

**Examples:**
```yaml
conditions:
  - "EQUIP_SLOT:MAINHAND"          # Must be in main hand
  - "EQUIP_SLOT:ARMOR"             # Must be in any armor slot
  - "EQUIP_SLOT:HAND"              # Must be in either hand
```

**Source:** `enchant/condition/ConditionEquipSlot.java:14-46`

---

### ACTIVE_EQUIP_SLOT
Checks if a specific slot is currently active.

**Format:** `ACTIVE_EQUIP_SLOT:slot`

Works the same as EQUIP_SLOT but checks the active slot from the event data.

**Source:** `enchant/condition/ConditionActiveEquipSlot.java:14-46`

---

### ONLY_ACTIVE_EQUIP
Checks if only the active equipment triggered the event.

**Format:** `ONLY_ACTIVE_EQUIP`

No parameters. Use when you want to ensure only the actively held item triggers the enchantment.

**Source:** `enchant/condition/ConditionOnlyActiveEquip.java`

---

### HOLD
Checks if the player is holding a specific item type.

**Format:** `HOLD:materials`

| Parameter | Type | Description |
|-----------|------|-------------|
| `materials` | String | Comma-separated list of materials or material list reference |

**Examples:**
```yaml
conditions:
  - "HOLD:DIAMOND_SWORD"
  - "HOLD:DIAMOND_SWORD,NETHERITE_SWORD"
  - "HOLD:ALL_SWORD"
```

**Source:** `enchant/condition/ConditionHold.java:19-21`

---

## Entity Conditions

### ENTITY_TYPE
Checks the entity type.

**Format:** `ENTITY_TYPE:types`

| Parameter | Type | Description |
|-----------|------|-------------|
| `types` | String | Comma-separated entity types |

**Common Entity Types:**
- `PLAYER` - Human players
- `ZOMBIE`, `SKELETON`, `CREEPER` - Hostile mobs
- `VILLAGER`, `IRON_GOLEM` - Passive/utility mobs
- `PIG`, `COW`, `SHEEP` - Animals

**Examples:**
```yaml
conditions:
  - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"           # Target is a player
  - "TARGET=ENEMY | ENTITY_TYPE:ZOMBIE,SKELETON"  # Target is zombie or skeleton
```

**Source:** `enchant/condition/ConditionEntityType.java:17-19`

---

### HAS_ENEMY
Checks if there is an enemy target in the event.

**Format:** `HAS_ENEMY`

No parameters. Returns true if `data.getEnemyLivingEntity() != null`.

**Source:** `enchant/condition/ConditionHasEnemy.java:15-17`

---

### HAS_NEARBY_ENEMY
Checks for enemy players within a radius.

**Format:** `HAS_NEARBY_ENEMY:distance`

| Parameter | Type | Description |
|-----------|------|-------------|
| `distance` | Double | Search radius in blocks |

**Notes:**
- Excludes operator players
- Respects faction relations (same faction = not enemy)

**Example:**
```yaml
conditions:
  - "HAS_NEARBY_ENEMY:10"  # Enemy within 10 blocks
```

**Source:** `enchant/condition/ConditionHasNearbyEnemy.java:19-21`

---

### OUT_OF_SIGHT
Checks if the entity is out of the enemy's line of sight.

**Format:** `OUT_OF_SIGHT`

No parameters. Calculates based on yaw angles (approximately 141.5 degree field of view).

**Source:** `enchant/condition/ConditionOutOfSight.java:25-32`

---

## Health & Stats Conditions

### HEALTH
Checks the entity's current health.

**Format:** `HEALTH:operator:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `operator` | String | Comparison operator (<, >, =, etc.) |
| `value` | Double | Health value to compare |

**Example:**
```yaml
conditions:
  - "HEALTH:<=:10"                    # Health at or below 10 HP
  - "TARGET=ENEMY | HEALTH:>=:20"     # Enemy health at or above 20 HP
```

**Source:** `enchant/condition/ConditionHealth.java:17-20`

---

### HEALTH_PERCENT
Checks the entity's health as a percentage of max health.

**Format:** `HEALTH_PERCENT:operator:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `operator` | String | Comparison operator |
| `value` | Double | Percentage (0-100) |

**Example:**
```yaml
conditions:
  - "HEALTH_PERCENT:<=:50"     # Health at or below 50%
  - "HEALTH_PERCENT:>=:90"     # Health at or above 90%
```

**Source:** `enchant/condition/ConditionHealthPercent.java:17-20`

---

### FOOD
Checks the player's food level.

**Format:** `FOOD:operator:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `operator` | String | Comparison operator |
| `value` | Double | Food level (0-20) |

**Source:** `enchant/condition/ConditionFood.java:17-20`

---

### FOOD_PERCENT
Checks the player's food level as a percentage.

**Format:** `FOOD_PERCENT:operator:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `operator` | String | Comparison operator |
| `value` | Double | Percentage (0-100) |

**Source:** `enchant/condition/ConditionFoodPercent.java:17-20`

---

### OXYGEN
Checks the player's remaining air.

**Format:** `OXYGEN:operator:value`

**Source:** `enchant/condition/ConditionOxygen.java`

---

### OXYGEN_PERCENT
Checks the player's oxygen as a percentage.

**Format:** `OXYGEN_PERCENT:operator:value`

**Source:** `enchant/condition/ConditionOxygenPercent.java`

---

### EXP
Checks the player's total experience.

**Format:** `EXP:operator:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `operator` | String | Comparison operator |
| `value` | Double | Experience value |

**Source:** `enchant/condition/ConditionExp.java:17-20`

---

### LEVEL
Checks the player's experience level.

**Format:** `LEVEL:operator:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `operator` | String | Comparison operator |
| `value` | Integer | Level value |

**Source:** `enchant/condition/ConditionLevel.java`

---

## State Conditions

### ON_FIRE
Checks if the entity is on fire.

**Format:** `ON_FIRE`

No parameters. Returns true if `entity.getFireTicks() > 0`.

**Example:**
```yaml
conditions:
  - "ON_FIRE"                              # Player is on fire
  - "TARGET=ENEMY | ON_FIRE"               # Enemy is on fire
  - "NEGATIVE=true | ON_FIRE"              # Player is NOT on fire
```

**Source:** `enchant/condition/ConditionOnFire.java:17-24`

---

### ON_GROUND
Checks if the entity is on the ground.

**Format:** `ON_GROUND`

No parameters. Returns true if `entity.isOnGround()`.

**Source:** `enchant/condition/ConditionOnGround.java:17-26`

---

### ALLOW_FLIGHT
Checks if the player has flight enabled.

**Format:** `ALLOW_FLIGHT`

No parameters. Returns true if `player.getAllowFlight()`.

**Source:** `enchant/condition/ConditionAllowFlight.java:13-20`

---

### IN_COMBAT
Checks if the player is in combat.

**Format:** `IN_COMBAT`

No parameters. Uses combat logging integration (CombatLogX if available).

**Source:** `enchant/condition/ConditionInCombat.java:22-24`

---

## Combat Conditions

### CAN_ATTACK
Checks if the player can attack the target.

**Format:** `CAN_ATTACK`

No parameters. Checks:
- Faction relations (if Factions plugin installed)
- Target's gamemode (must be SURVIVAL)
- Target's operator status (can't attack ops)

**Source:** `enchant/condition/ConditionCanAttack.java:25-40`

---

### DAMAGE_CAUSE
Checks if the damage cause matches.

**Format:** `DAMAGE_CAUSE:causes`

| Parameter | Type | Description |
|-----------|------|-------------|
| `causes` | String | Comma-separated damage causes |

**Damage Causes:**
- `ENTITY_ATTACK` - Melee attack
- `PROJECTILE` - Arrow/projectile
- `FALL` - Fall damage
- `FIRE`, `FIRE_TICK` - Fire damage
- `LAVA` - Lava damage
- `POISON` - Poison damage
- `MAGIC` - Magic damage
- `VOID` - Void damage
- `LIGHTNING` - Lightning strike
- And many more...

**Example:**
```yaml
conditions:
  - "DAMAGE_CAUSE:ENTITY_ATTACK,ENTITY_SWEEP_ATTACK"
```

**Source:** `enchant/condition/ConditionDamageCause.java:17-20`

---

### HAS_DAMAGE_CAUSE
Checks if the event has a specific damage cause.

**Format:** `HAS_DAMAGE_CAUSE:causes`

Similar to DAMAGE_CAUSE but checks if the event has the cause at all.

**Source:** `enchant/condition/ConditionHasDamageCause.java`

---

### FAKE_SOURCE
Checks if the damage source is fake (internal damage).

**Format:** `FAKE_SOURCE`

No parameters. Used to detect plugin-generated damage events.

**Source:** `enchant/condition/ConditionFakeSource.java`

---

## Storage Conditions

### NUMBER_STORAGE
Checks a stored number value.

**Format:** `NUMBER_STORAGE:key` or `NUMBER_STORAGE:key:operator:value`

**One Parameter (existence check):**
```yaml
conditions:
  - "NUMBER_STORAGE:%combo%"       # True if 'combo' key exists
```

**Three Parameters (comparison):**
| Parameter | Type | Description |
|-----------|------|-------------|
| `key` | String | Storage key or placeholder |
| `operator` | String | Comparison operator |
| `value` | String | Value to compare (supports placeholders and math) |

**Examples:**
```yaml
conditions:
  - "NUMBER_STORAGE:%combo%:>=:5"          # Combo counter at least 5
  - "NUMBER_STORAGE:%damage%:>=:100"       # Stored damage at least 100
  - "NUMBER_STORAGE:%rage%:=:1"            # Rage mode is active
```

**Mathematical Expressions:**
```yaml
conditions:
  - "NUMBER_STORAGE:%damage%:>=:%player_max_value%*0.1"
```

**Source:** `enchant/condition/ConditionNumberStorage.java:22-88`

---

### TEXT_STORAGE
Checks a stored text value.

**Format:** `TEXT_STORAGE:key:operator:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `key` | String | Storage key |
| `operator` | String | Comparison operator (=, !=, ==) |
| `value` | String | Value to compare |

**Operators for Text:**
- `=` - Exact match (case-sensitive)
- `!=` - Not equal
- `==` - Case-insensitive match

**Example:**
```yaml
conditions:
  - "TEXT_STORAGE:%mode%:=:rage"        # Mode is exactly "rage"
  - "TEXT_STORAGE:%state%:!=:disabled"  # State is not "disabled"
```

**Source:** `enchant/condition/ConditionTextStorage.java:22-55`

---

## Environment Conditions

### WORLD_TIME
Checks the world time.

**Format:** `WORLD_TIME:operator:value`

| Parameter | Type | Description |
|-----------|------|-------------|
| `operator` | String | Comparison operator |
| `value` | Long | Time in ticks (0-24000) |

**Time Values:**
- `0` - Sunrise (6:00 AM)
- `6000` - Noon (12:00 PM)
- `12000` - Sunset (6:00 PM)
- `13000` - Night starts
- `18000` - Midnight (12:00 AM)
- `23000` - Night ends

**Example:**
```yaml
conditions:
  - "WORLD_TIME:>=:13000"              # Night time
  - "WORLD_TIME:<:13000"               # Day time
```

**Source:** `enchant/condition/ConditionWorldTime.java:17-20`

---

### ITEM_CONSUME
Checks if the consumed item matches.

**Format:** `ITEM_CONSUME:materials`

| Parameter | Type | Description |
|-----------|------|-------------|
| `materials` | String | Material to check |

Use with `ITEM_CONSUME` trigger type.

**Source:** `enchant/condition/ConditionItemConsume.java`

---

## Integration Conditions

### FACTION_RELATION
Checks the faction relation between player and target.

**Format:** `FACTION_RELATION:relation`

| Parameter | Type | Description |
|-----------|------|-------------|
| `relation` | String | Faction relation type |

**Relation Types:**
- `MEMBER` - Same faction member
- `ALLY` - Allied faction
- `TRUCE` - Faction in truce
- `NEUTRAL` - No special relation
- `ENEMY` - Enemy faction

**Example:**
```yaml
conditions:
  - "FACTION_RELATION:MEMBER"           # Same faction
  - "NEGATIVE=true | FACTION_RELATION:MEMBER"  # Different faction
```

**Source:** `enchant/condition/ConditionFactionRelation.java`

---

### IN_FACTION_TERRIORITY
Checks if player is in faction territory.

**Format:** `IN_FACTION_TERRIORITY`

No parameters. Requires Factions plugin.

**Source:** `enchant/condition/ConditionInFactionTerriority.java`

---

## Complete Condition List

| Category | Conditions |
|----------|------------|
| Equipment | EQUIP_SLOT, ACTIVE_EQUIP_SLOT, ONLY_ACTIVE_EQUIP, HOLD |
| Entity | ENTITY_TYPE, HAS_ENEMY, HAS_NEARBY_ENEMY, OUT_OF_SIGHT |
| Health/Stats | HEALTH, HEALTH_PERCENT, FOOD, FOOD_PERCENT, OXYGEN, OXYGEN_PERCENT, EXP, LEVEL |
| State | ON_FIRE, ON_GROUND, ALLOW_FLIGHT, IN_COMBAT |
| Combat | CAN_ATTACK, DAMAGE_CAUSE, HAS_DAMAGE_CAUSE, FAKE_SOURCE |
| Storage | NUMBER_STORAGE, TEXT_STORAGE |
| Environment | WORLD_TIME, ITEM_CONSUME |
| Integration | FACTION_RELATION, IN_FACTION_TERRIORITY |

---

## Examples

### Basic Combat Enchantment
```yaml
conditions:
  - "EQUIP_SLOT:MAINHAND"
  - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
```

### Low Health Trigger
```yaml
conditions:
  - "HEALTH_PERCENT:<=:25"
```

### Night-Only Effect
```yaml
conditions:
  - "WORLD_TIME:>=:13000"
  - "WORLD_TIME:<=:23000"
```

### Combo Counter Check
```yaml
conditions:
  - "NUMBER_STORAGE:%combo%:>=:10"
```

### Faction PvP Only
```yaml
conditions:
  - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
  - "NEGATIVE=true | FACTION_RELATION:MEMBER"
  - "NEGATIVE=true | FACTION_RELATION:ALLY"
```

### Back Stab (Out of Sight)
```yaml
conditions:
  - "EQUIP_SLOT:MAINHAND"
  - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
  - "OUT_OF_SIGHT"
```

### Conditional Flow (Damage Thresholds)
```yaml
# First function: High damage (>= 1000)
high_damage:
  conditions:
    - "NUMBER_STORAGE:%damage%:>=:1000"
  true-condition-break: true

# Second function: Medium damage (>= 500)
medium_damage:
  conditions:
    - "NUMBER_STORAGE:%damage%:>=:500"
  true-condition-break: true

# Third function: Low damage (default)
low_damage:
  conditions:
    - ""
```
