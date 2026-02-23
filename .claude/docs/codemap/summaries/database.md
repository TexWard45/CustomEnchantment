# Database Module Summary

**Package:** `com.bafmc.customenchantment.database` | **Classes:** 2 | **Init Phase:** 3
**Purpose:** SQLite persistence for player data

## Execution Flow
- **Player Data**: PlayerListener (join) -> PlayerModule (create CEPlayer) -> PlayerExpansions (14 systems) -> TaskModule (periodic updates) -> DatabaseModule (persist via SaveTask)

## Key Classes

### Database (99 lines)
**Type:** class
**Purpose:** Database operations
**Fields:** `File file`, `Connection connection`, `static SimpleDateFormat format`
**Key Methods:**
- `connect()`: 
- `disconnect()`: 
- `init()`: 
- `insertLogs(Player player, String itemType1, String itemType2, String result, Map<String, Object> map)`: 
- `getConnection()` -> `Connection`: 
- `isConnected()` -> `boolean`: 

### DatabaseModule (30 lines)
**Type:** class
**Purpose:** PluginModule — SQLite persistence
**Fields:** `Database database`
**Key Methods:**
- `onEnable()`: 
- `onDisable()`: 
**Annotations:** @Getter
