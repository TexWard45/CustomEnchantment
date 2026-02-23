# Guard Module Summary

**Package:** `com.bafmc.customenchantment.guard` | **Classes:** 5 | **Init Phase:** 3
**Purpose:** Guard mob system (spawning, targeting, management)

## Key Classes

### GuardManager (53 lines)
**Type:** class
**Purpose:** Guard lifecycle management
**Fields:** `ConcurrentHashMap<UUID, Guard> guardMap`, `ConcurrentHashMap<UUID, PlayerGuard> playerGuardMap`
**Key Methods:**
- `removePlayerGuard(Player player)`: 
- `addEntityGuard(Guard guard)`: 
- `removeEntityGuard(Guard guard)`: 
- `getPlayerGuard(Player player)` -> `PlayerGuard`: 
- `isGuard(Entity entity)` -> `boolean`: 
- `getGuard(Entity entity)` -> `Guard`: 
- `getPlayerGuards()` -> `List<PlayerGuard>`: 
- `getGuards()` -> `List<Guard>`: 
**Notes:** Thread-safe collections

### Guard (215 lines)
**Type:** class
**Purpose:** Guard entity representation
**Fields:** `PlayerGuard playerGuard`, `String name`, `String team`, `EntityInsentientNMS entityInsentient`, `EntityType entityType`, `double playerRange` (+7 more)
**Key Methods:**
- `summon(Location location, double speed)` -> `Entity`: 
- `remove()`: 
- `tick()`: 
- `tickAlive()` -> `boolean`: 
- `tickMove()` -> `boolean`: 
- `tickAttack()` -> `boolean`: 
- `attack(EntityLivingNMS enemyInsentientNMS)` -> `boolean`: 
- `canAttack(EntityLivingNMS enemyInsentientNMS)` -> `boolean`: 

### PlayerGuard (123 lines)
**Type:** class
**Purpose:** Per-player guard holder
**Fields:** `ConcurrentHashMap<UUID, Guard> guards`, `ConcurrentHashMap<String, Guard> guardByNameList`, `String name`, `UUID uuid`, `EntityLivingNMS lastTarget`, `EntityLivingNMS lastEnemy`
**Key Methods:**
- `addGuard(Guard guard)`: 
- `removeGuard(Guard guard)`: 
- `removeGuardByName(String name)`: 
- `clearGuards()`: 
- `containsGuardName(String name)` -> `boolean`: 
- `tickGuards()`: 
- `getGuardByName(String name)` -> `Guard`: 
- `getPlayer()` -> `Player`: 
**Notes:** Thread-safe collections

### GuardModule (30 lines)
**Type:** class
**Purpose:** PluginModule — guard system
**Fields:** `GuardManager guardManager`, `GuardTask guardTask`
**Key Methods:**
- `onEnable()`: 
- `onDisable()`: 
- `setupTask()`: 
**Annotations:** @Getter

### GuardSetting (5 lines)
**Type:** class
**Purpose:** Guard configuration
