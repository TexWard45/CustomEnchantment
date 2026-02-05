# Guard System Reference

This document provides comprehensive reference for the Guard system in CustomEnchantment, which allows players to summon companion mobs that follow and fight for them.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Guard Class](#guard-class)
- [PlayerGuard Class](#playerguard-class)
- [GuardManager Class](#guardmanager-class)
- [Guard Lifecycle](#guard-lifecycle)
- [Combat Behavior](#combat-behavior)
- [Related Effects](#related-effects)

---

## Overview

The Guard system enables players to summon temporary mob companions through enchantment effects. Guards automatically:
- Follow the player within a specified range
- Attack enemies the player fights
- Expire after a set duration
- Die when the player disconnects or dies

**Source Location:** `src/main/java/com/bafmc/customenchantment/guard/`

---

## Architecture

```
GuardManager (Global)
    └── PlayerGuard (Per-Player)
            └── Guard (Individual Mob)
```

| Component | Responsibility | Scope |
|-----------|----------------|-------|
| `GuardManager` | Global tracking of all guards and player-guard mappings | Singleton |
| `PlayerGuard` | Per-player guard management, enemy tracking | Per-Player |
| `Guard` | Individual guard behavior, movement, combat | Per-Entity |

---

## Guard Class

**Source:** `guard/Guard.java`

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `playerGuard` | `PlayerGuard` | Owner's guard manager |
| `name` | `String` | Unique guard identifier |
| `team` | `String` | Team name for friendly fire prevention |
| `entityType` | `EntityType` | Type of mob summoned |
| `playerRange` | `double` | Max distance from player before teleporting |
| `attackRange` | `double` | Distance to teleport to enemy |
| `aliveTime` | `long` | Duration in milliseconds |
| `spawnTime` | `long` | Timestamp when spawned |
| `damage` | `double` | Custom damage value |
| `suicide` | `boolean` | Whether to die after attacking |
| `lastEnemy` | `EntityLivingNMS` | Last attacked enemy |
| `nowTarget` | `boolean` | Currently targeting flag |

### Constructor

```java
Guard(PlayerGuard playerGuard, String name, String team, EntityType entityType,
      double playerRange, double attackRange, long aliveTime)
```

| Parameter | Description |
|-----------|-------------|
| `playerGuard` | The player's guard manager |
| `name` | Unique identifier for this guard |
| `team` | Team name for friendly fire |
| `entityType` | Bukkit EntityType to spawn |
| `playerRange` | Distance before teleporting to player |
| `attackRange` | Distance before teleporting to enemy |
| `aliveTime` | Lifetime in milliseconds |

### Methods

#### summon(Location, double)
```java
Entity summon(Location location, double speed)
```
Spawns the guard at the specified location with movement speed.
- Sets `GuardListener.guardSpawning = true` to bypass spawn listeners
- Disables CustomMobDrop for the guard
- Sets custom name to `{PlayerName}'s {EntityName}`
- Returns the spawned Entity

#### remove()
```java
void remove()
```
Removes the guard entity from the world if not already dead.

#### tick()
```java
void tick()
```
Main tick method called every game tick:
1. Check if still alive (`tickAlive`)
2. Try to attack (`tickAttack`)
3. If not attacking, follow player (`tickMove`)

#### tickAlive()
```java
boolean tickAlive()
```
Checks guard validity:
- Returns `false` and removes if entity is dead
- Returns `false` and removes if player offline/dead
- Returns `false` and removes if lifetime expired
- Returns `true` if valid

#### tickMove()
```java
boolean tickMove()
```
Follows the player:
- Moves toward player if distance > 2 blocks
- Uses NMS pathfinding

#### tickAttack()
```java
boolean tickAttack()
```
Handles combat logic:
1. Teleport to player if too far (`playerRange`)
2. Try to attack guard's `lastEnemy`
3. Try to attack player's `lastTarget`
4. Try to attack player's `lastEnemy`

#### attack(EntityLivingNMS)
```java
boolean attack(EntityLivingNMS enemyInsentientNMS)
```
Attacks a specific enemy:
- Validates enemy can be attacked
- Sets goal target via NMS
- Teleports to enemy if within `playerRange` but outside `attackRange`

#### canAttack(EntityLivingNMS)
```java
boolean canAttack(EntityLivingNMS enemyInsentientNMS)
```
Validates attack target:
- Enemy must not be dead
- Enemy must be in same world

### Lifetime Management

```java
boolean isExpireAliveTime()
```
Returns `true` if `currentTime - spawnTime > aliveTime`

```java
void setAliveTime(long aliveTime)
```
Updates the remaining lifetime (can extend duration)

---

## PlayerGuard Class

**Source:** `guard/PlayerGuard.java`

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `guards` | `ConcurrentHashMap<UUID, Guard>` | Guards by entity UUID |
| `guardByNameList` | `ConcurrentHashMap<String, Guard>` | Guards by name |
| `name` | `String` | Player name |
| `uuid` | `UUID` | Player UUID |
| `lastTarget` | `EntityLivingNMS` | Last entity player attacked |
| `lastEnemy` | `EntityLivingNMS` | Last entity that attacked player |

### Methods

#### addGuard(Guard)
```java
void addGuard(Guard guard)
```
Registers a new guard:
- Skips if guard name already exists
- Adds to GuardManager's global tracking
- Adds to local maps

#### removeGuard(Guard)
```java
void removeGuard(Guard guard)
```
Unregisters a guard:
- Removes from GuardManager
- Removes from local maps
- Calls `guard.remove()`

#### removeGuardByName(String)
```java
void removeGuardByName(String name)
```
Removes guard(s) by name:
- Supports wildcard `*` for prefix matching
- Example: `"wolf*"` removes all guards starting with "wolf"

#### clearGuards()
```java
void clearGuards()
```
Removes all guards for this player.

#### tickGuards()
```java
void tickGuards()
```
Calls `tick()` on all guards.

#### Target Tracking

```java
void setTarget(Entity entity)      // Set player's attack target
void setLastEnemy(Entity entity)   // Set player's attacker
EntityLivingNMS getLastTarget()
EntityLivingNMS getLastEnemy()
```

Guards use these to know what enemies to attack.

---

## GuardManager Class

**Source:** `guard/GuardManager.java`

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `guardMap` | `ConcurrentHashMap<UUID, Guard>` | All guards by entity UUID |
| `playerGuardMap` | `ConcurrentHashMap<UUID, PlayerGuard>` | Player guard managers |

### Methods

#### getPlayerGuard(Player)
```java
PlayerGuard getPlayerGuard(Player player)
```
Gets or creates the PlayerGuard for a player.

#### removePlayerGuard(Player)
```java
void removePlayerGuard(Player player)
```
Removes the PlayerGuard mapping (called on quit).

#### isGuard(Entity)
```java
boolean isGuard(Entity entity)
```
Checks if an entity is a guard.

#### getGuard(Entity)
```java
Guard getGuard(Entity entity)
```
Gets the Guard object for an entity.

#### Global Tracking

```java
void addEntityGuard(Guard guard)
void removeEntityGuard(Guard guard)
List<Guard> getGuards()
List<PlayerGuard> getPlayerGuards()
```

---

## Guard Lifecycle

### 1. Creation
```
Effect triggers (GUARD_SUMMON)
    └── Creates Guard object with parameters
        └── guard.summon(location, speed)
            └── Spawns entity in world
            └── Registers with GuardManager
```

### 2. Active State
```
Every tick (GuardTask/scheduler):
    └── playerGuard.tickGuards()
        └── guard.tick()
            └── tickAlive() - check validity
            └── tickAttack() - try combat
            └── tickMove() - follow player
```

### 3. Removal Conditions
Guard is removed when:
- Entity dies (killed by mob/player/environment)
- Player disconnects
- Player dies
- Lifetime (`aliveTime`) expires
- Explicitly removed by effect (GUARD_REMOVE)

### 4. Cleanup
```
Guard removal:
    └── playerGuard.removeGuard(guard)
        └── GuardManager.removeEntityGuard(guard)
        └── Remove from player's maps
        └── guard.remove() - despawn entity
```

---

## Combat Behavior

### Target Priority
Guards attack in this order:
1. Guard's own `lastEnemy` (attacked the guard)
2. Player's `lastTarget` (player attacked)
3. Player's `lastEnemy` (attacked the player)

### Attack Flow
```
tickAttack()
    └── Check distance from player (teleport if > playerRange)
    └── Try attack(lastEnemy)
    └── Try attack(player.lastTarget)
    └── Try attack(player.lastEnemy)

attack(enemy)
    └── Validate canAttack()
    └── Set NMS goal target
    └── Teleport if needed (within playerRange, outside attackRange)
```

### Distance Handling

| Distance | Behavior |
|----------|----------|
| Guard > `playerRange` from player | Teleport to player |
| Guard > `attackRange` from enemy | Teleport to enemy |
| Guard within `attackRange` of enemy | Attack normally |

### Team System
Guards can have a `team` property to prevent friendly fire between:
- Guards of the same player
- Guards on the same team

---

## Related Effects

### GUARD_SUMMON
Summons a new guard with specified parameters.

**Format:** `GUARD_SUMMON;name;team;entityType;playerRange;attackRange;aliveTime;speed;damage;suicide`

| Parameter | Description |
|-----------|-------------|
| `name` | Unique identifier |
| `team` | Team name |
| `entityType` | Mob type to spawn |
| `playerRange` | Max distance from player |
| `attackRange` | Distance to teleport to enemy |
| `aliveTime` | Duration in ticks |
| `speed` | Movement speed modifier |
| `damage` | Custom damage value |
| `suicide` | Die after first attack |

### GUARD_REMOVE
Removes guards by name pattern.

**Format:** `GUARD_REMOVE;namePattern`

| Parameter | Description |
|-----------|-------------|
| `namePattern` | Name or prefix with `*` wildcard |

**Examples:**
- `GUARD_REMOVE;wolf1` - Remove specific guard
- `GUARD_REMOVE;wolf*` - Remove all guards starting with "wolf"

### HAS_GUARD Condition
Checks if player has a guard with specific name.

**Format:** `HAS_GUARD;name`

---

## Integration with CustomMobDrop

When a guard is summoned, it sets metadata to disable CustomMobDrop:
```java
if (Bukkit.getPluginManager().isPluginEnabled("CustomMobDrop")) {
    entity.setMetadata(CustomMobDropTag.DISABLE,
        new FixedMetadataValue(CustomEnchantment.instance(), true));
}
```

This prevents guards from dropping loot when killed.

---

## NMS Integration

Guards use NMS (Net Minecraft Server) classes for:
- `EntityInsentientNMS` - Pathfinding and goal targeting
- `EntityLivingNMS` - Living entity wrapper for targets

These provide access to vanilla mob AI systems for:
- Movement/pathfinding
- Goal-based targeting
- Attack animations
