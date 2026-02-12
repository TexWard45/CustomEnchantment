# Configuration System

BafFramework provides annotation-based YAML configuration binding.

## Annotations

Package: `com.bafmc.bukkit.config.annotation`

### @Configuration

Marks a class as a config-mappable object. Required on all config classes.

```java
import com.bafmc.bukkit.config.annotation.Configuration;
import com.bafmc.bukkit.config.annotation.Path;
import lombok.Getter;

@Getter
@Configuration
public class MainConfig {
    @Path("prefix")
    private String prefix = "&7[&bMyPlugin&7] ";

    @Path("debug")
    private boolean debug = false;

    @Path("max-players")
    private int maxPlayers = 100;
}
```

### @Path

Maps a field to a YAML path. Applied to fields.

| Usage | Behavior |
|-------|----------|
| `@Path("my.path")` | Explicit YAML path |
| `@Path` (empty) | Auto-converts camelCase field name to kebab-case YAML path |
| `@Path(isRoot = true)` | Maps the entire current section to this field |

```java
@Path("database.host")
private String host = "localhost";     // Maps to: database.host

@Path("database.port")
private int port = 3306;               // Maps to: database.port

@Path(isRoot = true)
private String key;                    // Receives the section key name
```

### @Key

Marks a field to receive the YAML key name when used inside a map.

### Collection Annotations

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@ListType(MyClass.class)` | Element type for `List<T>` | `@ListType(RewardData.class)` |
| `@ValueType(MyClass.class)` | Value type for `Map<K, V>` | `@ValueType(ChannelData.class)` |
| `@KeyType(MyClass.class)` | Key type for `Map<K, V>` | `@KeyType(String.class)` |

```java
@Path("rewards")
@ListType(RewardData.class)
private List<RewardData> rewards = new ArrayList<>();

@Path("channels")
@ValueType(ChannelData.class)
private Map<String, ChannelData> channels = new LinkedHashMap<>();
```

Always initialize collections to empty (not null).

## AdvancedFileConfiguration

Package: `com.bafmc.bukkit.config.AdvancedFileConfiguration`

File-backed config extending Bukkit's `ConfigurationSection` with additional features.

### Loading Config Files

```java
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.utils.ConfigUtils;

// Load config with defaults from JAR resource
AdvancedFileConfiguration config = ConfigUtils.setupResource(
    plugin, "/config.yml",
    new java.io.File(plugin.getDataFolder(), "config.yml")
);
```

`ConfigUtils.setupResource()` copies the default resource from your JAR to the data folder on first run, then loads the file.

### Object Mapping

```java
// Map entire config to a class
MainConfig mainConfig = config.get(MainConfig.class);

// Map a specific path to a class
DatabaseConfig dbConfig = config.get("database", DatabaseConfig.class);
```

### Type-Safe Getters

Standard Bukkit `ConfigurationSection` getters plus:

| Method | Returns |
|--------|---------|
| `getString(path)` | `String` |
| `getInt(path)` | `int` |
| `getBoolean(path)` | `boolean` |
| `getDouble(path)` | `double` |
| `getStringList(path)` | `List<String>` |
| `getStringColor(path)` | `String` with color codes translated |
| `getStringColorList(path)` | `List<String>` with color codes translated |
| `get(Class<T>)` | Mapped config object |
| `get(String, Class<T>)` | Mapped config object at path |

### Saving

```java
config.save();  // Save to the original file
```

## IConfigurationLoader

Package: `com.bafmc.bukkit.config.IConfigurationLoader`

Implement this interface on a `@Configuration` class for post-load initialization.

```java
import com.bafmc.bukkit.config.IConfigurationLoader;
import org.bukkit.configuration.ConfigurationSection;

@Getter
@Configuration
public class ShopConfig implements IConfigurationLoader {
    @Path("shop.name")
    private String name = "Shop";

    // Called after annotation-based fields are populated
    @Override
    public void loadConfig(String path, ConfigurationSection config) {
        // Custom parsing logic here
    }
}
```

## Full Example

### config.yml

```yaml
database:
  host: localhost
  port: 3306
  name: my_database
  username: root
  password: ''
```

### DatabaseConfig.java

```java
import com.bafmc.bukkit.config.annotation.Configuration;
import com.bafmc.bukkit.config.annotation.Path;
import lombok.Getter;

@Getter
@Configuration
public class DatabaseConfig {
    @Path("database.host")
    private String host = "localhost";

    @Path("database.port")
    private int port = 3306;

    @Path("database.name")
    private String name = "my_database";

    @Path("database.username")
    private String username = "root";

    @Path("database.password")
    private String password = "";
}
```

### Loading in a Module

```java
@Override
public void onReload() {
    AdvancedFileConfiguration config = ConfigUtils.setupResource(
        getPlugin(), "/config.yml",
        new java.io.File(getPlugin().getDataFolder(), "config.yml")
    );
    this.databaseConfig = config.get(DatabaseConfig.class);
}
```

## Rules

- Always provide sensible defaults so the plugin works without user config
- Never commit real passwords or API keys as default values
- YAML uses `kebab-case`, Java uses `camelCase`
- Nested `@Configuration` objects are supported
