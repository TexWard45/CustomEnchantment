# CustomEnchantment Data Storage Reference

This document provides comprehensive reference for the data storage systems in the CustomEnchantment plugin, including SQLite database, YAML configuration, and player data files.

## Table of Contents

- [Overview](#overview)
- [SQLite Database](#sqlite-database)
- [Player Data Files](#player-data-files)
- [Configuration Files](#configuration-files)
- [File Structure](#file-structure)
- [Storage API](#storage-api)

---

## Overview

CustomEnchantment uses a hybrid storage approach:

| Storage Type | Purpose | Location |
|--------------|---------|----------|
| SQLite Database | Action logging, analytics | `plugins/CustomEnchantment/data.db` |
| YAML Files | Player persistent data | `plugins/CustomEnchantment/data/players/` |
| YAML Configs | Plugin configuration | `plugins/CustomEnchantment/` |
| Folder Storage | Item definitions | Various folders |

---

## SQLite Database

### Database Module

The `DatabaseModule` manages the SQLite connection lifecycle.

```java
package com.bafmc.customenchantment.database;

public class DatabaseModule extends PluginModule<CustomEnchantment> {
    private Database database;

    public void onEnable() {
        // Creates database file if not exists
        FileUtils.createFile(getPlugin().getDatabaseFile());

        // Connect and initialize
        this.database = new Database(getPlugin().getDatabaseFile());
        this.database.connect();
        this.database.init();
    }

    public void onDisable() {
        this.database.disconnect();
    }
}
```

### Database Location

```
plugins/CustomEnchantment/data.db
```

### Database Schema

#### item_action_logs Table

Tracks all item-related player actions for audit and analytics.

```sql
CREATE TABLE IF NOT EXISTS item_action_logs (
    id INTEGER PRIMARY KEY,
    date DATETIME,
    player VARCHAR(16),
    item1_type VARCHAR(64),
    item2_type VARCHAR(64),
    result VARCHAR(64),
    data TEXT
)
```

| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER | Auto-increment primary key |
| `date` | DATETIME | Action timestamp (yyyy-MM-dd HH:mm:ss) |
| `player` | VARCHAR(16) | Player name |
| `item1_type` | VARCHAR(64) | First item type involved |
| `item2_type` | VARCHAR(64) | Second item type (if applicable) |
| `result` | VARCHAR(64) | Action result code |
| `data` | TEXT | Additional data (key=value format) |

### Logging Actions

The `CustomEnchantmentLog` class provides the logging interface:

```java
import com.bafmc.customenchantment.CustomEnchantmentLog;
import com.bafmc.customenchantment.item.ApplyReason;

// Log an item action
ApplyReason reason = ApplyReason.builder()
    .player(player)
    .ceItem1(enchantedItem)
    .ceItem2(book)
    .result(ApplyResult.SUCCESS)
    .data(Map.of("enchant", "lifesteal", "level", 3))
    .build();

CustomEnchantmentLog.writeItemActionLogs(reason);
```

### Direct Database Access

```java
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.database.Database;

// Get database instance
Database database = CustomEnchantment.instance().getDatabaseModule().getDatabase();

// Check connection status
boolean connected = database.isConnected();

// Get raw JDBC connection for custom queries
Connection conn = database.getConnection();
```

### Insert Custom Logs

```java
import java.util.HashMap;
import java.util.Map;

Map<String, Object> data = new HashMap<>();
data.put("enchant_name", "lifesteal");
data.put("enchant_level", 3);
data.put("success_rate", 75);

database.insertLogs(player, "WEAPON", "BOOK", "APPLIED", data);
```

---

## Player Data Files

### Storage Location

```
plugins/CustomEnchantment/data/players/<playername>.yml
```

Each player has a dedicated YAML file named after their lowercase username.

### PlayerStorage Expansion

The `PlayerStorage` expansion manages player data persistence:

```java
public class PlayerStorage extends CEPlayerExpansion {
    private AdvancedFileConfiguration config;

    public void onJoin() {
        // Load or create player data file
        File file = getPlayerDataFile();
        FileUtils.createFile(file);
        this.config = new AdvancedFileConfiguration(file);
    }

    public void onQuit() {
        // Save player data on disconnect
        this.config.save();
    }

    public AdvancedFileConfiguration getConfig() {
        return config;
    }
}
```

### Accessing Player Data

```java
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerStorage;

CEPlayer cePlayer = CEAPI.getCEPlayer(player);
PlayerStorage storage = cePlayer.getStorage();
AdvancedFileConfiguration config = storage.getConfig();

// Read data
String value = config.getString("custom.setting");
int level = config.getInt("custom.level", 0);

// Write data
config.set("custom.setting", "new_value");
config.set("custom.level", 5);
config.save();
```

### Example Player Data Structure

```yaml
# plugins/CustomEnchantment/data/players/playername.yml

# Equipment slot data
equipment:
  helmet:
    enabled: true
  extra_slot_1:
    item: "artifact:dragon_heart:5"

# Custom filters
filters:
  enchant_blacklist:
    - "LEGENDARY_ENCHANT"
    - "MYTHIC_ENCHANT"

# Name tag settings
nametag:
  current: "Champion"
  unlocked:
    - "Champion"
    - "Warrior"

# Ability cooldowns (temporary, may reset)
abilities:
  dash:
    last_use: 1704067200000
  flash:
    last_use: 1704067100000
```

---

## Configuration Files

### File Locations

All configuration files are located in `plugins/CustomEnchantment/`:

| File | Purpose |
|------|---------|
| `config.yml` | Main plugin configuration |
| `messages.yml` | All plugin messages |
| `groups.yml` | Enchantment group definitions |
| `artifact-groups.yml` | Artifact group definitions |
| `sigil-groups.yml` | Sigil group definitions |
| `outfit-groups.yml` | Outfit group definitions |
| `items.yml` | Item definitions |
| `tinkerer.yml` | Tinkerer exchange rates |
| `book-craft.yml` | Book crafting recipes |
| `book-upgrade.yml` | Book upgrade configuration |

### Folder Locations

| Folder | Purpose |
|--------|---------|
| `data/` | General data storage |
| `data/players/` | Player data files |
| `enchantment/` | Enchantment definition files |
| `weapon/` | Weapon definition files |
| `artifact/` | Artifact definition files |
| `skin/` | Weapon skin files |
| `outfit/` | Outfit definition files |
| `storage/` | Stored item templates |
| `menu/` | GUI menu configurations |
| `book-upgrade/` | Book upgrade tier files |

### Getting File References

```java
import com.bafmc.customenchantment.CustomEnchantment;

CustomEnchantment plugin = CustomEnchantment.instance();

// Configuration files
File configFile = plugin.getConfigFile();           // config.yml
File messagesFile = plugin.getMessagesFile();       // messages.yml
File groupFile = plugin.getGroupFile();             // groups.yml
File itemFile = plugin.getItemFile();               // items.yml

// Data files
File databaseFile = plugin.getDatabaseFile();       // data.db
File playerDataFile = plugin.getPlayerDataFile(player); // data/players/<name>.yml

// Folders
File enchantFolder = plugin.getEnchantFolder();     // enchantment/
File weaponFolder = plugin.getWeaponFolder();       // weapon/
File artifactFolder = plugin.getArtifactFolder();   // artifact/
File skinFolder = plugin.getSkinFolder();           // skin/
File menuFolder = plugin.getMenuFolder();           // menu/
File storageFolder = plugin.getStorageItemFolder(); // storage/
```

---

## File Structure

### Complete Directory Layout

```
plugins/CustomEnchantment/
├── config.yml                 # Main configuration
├── messages.yml               # Plugin messages
├── groups.yml                 # Enchantment groups
├── artifact-groups.yml        # Artifact groups
├── sigil-groups.yml           # Sigil groups
├── outfit-groups.yml          # Outfit groups
├── items.yml                  # Item definitions
├── tinkerer.yml               # Tinkerer rates
├── book-craft.yml             # Book crafting
├── book-upgrade.yml           # Book upgrade config
├── artifact-upgrade.yml       # Artifact upgrade config
├── data.db                    # SQLite database
│
├── data/
│   └── players/
│       ├── player1.yml
│       ├── player2.yml
│       └── ...
│
├── enchantment/
│   ├── legendary/
│   │   ├── lifesteal.yml
│   │   └── critical.yml
│   ├── epic/
│   └── ...
│
├── weapon/
│   ├── excalibur.yml
│   └── ...
│
├── artifact/
│   ├── dragon_heart.yml
│   └── ...
│
├── skin/
│   ├── fire_sword.yml
│   └── ...
│
├── outfit/
│   ├── warrior_set.yml
│   └── ...
│
├── storage/
│   ├── items.yml
│   └── ...
│
├── menu/
│   ├── ce-anvil.yml
│   ├── equipment.yml
│   └── ...
│
└── book-upgrade/
    ├── tier1.yml
    └── ...
```

---

## Storage API

### Temporary Storage

For session-based data that doesn't persist:

```java
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.TemporaryKey;

CEPlayer cePlayer = CEAPI.getCEPlayer(player);
PlayerTemporaryStorage tempStorage = cePlayer.getTemporaryStorage();

// Store temporary values
tempStorage.setLong(TemporaryKey.LAST_COMBAT_TIME, System.currentTimeMillis());

// Retrieve temporary values
long lastCombat = tempStorage.getLong(TemporaryKey.LAST_COMBAT_TIME);
```

### StorageUtils

Utility class for storage operations:

```java
import com.bafmc.customenchantment.utils.StorageUtils;

// Storage utility methods are available for common operations
```

---

## Best Practices

### Database Operations

1. **Async Logging**: Item action logs are written synchronously; consider batching for high-volume servers
2. **Connection Reuse**: The plugin maintains a single connection; don't close it manually
3. **Query Safety**: Use `PreparedStatement` for any custom queries

### Player Data

1. **Save on Quit**: Player data is automatically saved on disconnect
2. **Manual Save**: Call `config.save()` after important changes
3. **Default Values**: Always provide default values when reading config

```java
// Good - with default
int level = config.getInt("path.to.value", 0);

// Bad - may return null/error
int level = config.getInt("path.to.value");
```

### Configuration Reloads

Handle configuration reloads properly:

```java
// After /customenchantment reload
// All configuration is reloaded automatically
// Player data is NOT reloaded - use PlayerStorage.setup() if needed
```

---

## Database Queries (Advanced)

### Example: Query Action Logs

```java
import java.sql.*;

Connection conn = CustomEnchantment.instance()
    .getDatabaseModule().getDatabase().getConnection();

String sql = "SELECT * FROM item_action_logs WHERE player = ? ORDER BY date DESC LIMIT 10";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setString(1, playerName);
    ResultSet rs = stmt.executeQuery();

    while (rs.next()) {
        String date = rs.getString("date");
        String item1 = rs.getString("item1_type");
        String result = rs.getString("result");
        // Process results...
    }
} catch (SQLException e) {
    e.printStackTrace();
}
```

### Example: Get Statistics

```java
String sql = "SELECT result, COUNT(*) as count FROM item_action_logs " +
             "WHERE player = ? GROUP BY result";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setString(1, playerName);
    ResultSet rs = stmt.executeQuery();

    Map<String, Integer> stats = new HashMap<>();
    while (rs.next()) {
        stats.put(rs.getString("result"), rs.getInt("count"));
    }
} catch (SQLException e) {
    e.printStackTrace();
}
```

---

## See Also

- [ARCHITECTURE.md](ARCHITECTURE.md) - System architecture
- [API.md](API.md) - Developer API reference
- [COMMANDS.md](COMMANDS.md) - Admin commands
