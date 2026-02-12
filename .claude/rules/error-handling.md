# Error Handling

## Logging

Use the plugin's logger, NOT `System.out` or `e.printStackTrace()`:

```java
// GOOD: Plugin logger with context
getPlugin().getLogger().warning("Failed to load config for player " + playerName);
getPlugin().getLogger().severe("Database connection failed: " + e.getMessage());

// BAD: Raw stack trace to console
e.printStackTrace();

// BAD: System.out
System.out.println("Something went wrong");
```

When no plugin reference is available, use `Bukkit.getLogger()`:

```java
Bukkit.getLogger().warning("[{PluginName}] " + message);
```

## When to Throw vs Return Null

| Situation | Approach |
|-----------|----------|
| Invalid argument passed by developer | Throw `IllegalArgumentException` |
| Required resource not found | Throw `IllegalStateException` |
| Optional lookup (getPlayer, getMenu) | Return `null` |
| Config parse failure | Log warning + use default value |
| External plugin API failure | Log warning + return gracefully |
| Database connection failure | Log severe + disable feature |

## Null Handling

For public API methods that may return null, document it:

```java
/**
 * @return the player data, or null if not found
 */
public PlayerData getPlayerData(String name) {
    return playerMap.get(name);
}
```

For internal code, validate early:

```java
public void processPlayer(Player player) {
    if (player == null) return;  // Guard clause for Bukkit events
    // ... logic
}
```

## Try-Catch Guidelines

```java
// GOOD: Catch specific, log with context, continue gracefully
try {
    config = fileConfig.get(MainConfig.class);
} catch (Exception e) {
    getPlugin().getLogger().severe("Failed to load MainConfig: " + e.getMessage());
    config = new MainConfig(); // fallback to defaults
}

// BAD: Catch and ignore
try {
    config = fileConfig.get(MainConfig.class);
} catch (Exception e) {
    // silent failure
}

// BAD: Catch and only print stack trace
try {
    config = fileConfig.get(MainConfig.class);
} catch (Exception e) {
    e.printStackTrace();
}
```
