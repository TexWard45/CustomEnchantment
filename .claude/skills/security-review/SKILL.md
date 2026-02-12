---
name: security-review
description: Use this skill when handling player input, working with secrets, creating commands, or implementing sensitive features. Provides comprehensive security checklist for Bukkit plugins.
---

# Security Review Skill

This skill ensures all code follows security best practices for Bukkit plugin development.

## When to Activate

- Handling player input (commands, chat, signs)
- Working with secrets or credentials (database, Redis, API keys)
- Creating new commands with permissions
- Implementing admin features
- Storing or transmitting player data
- Processing configuration files
- Handling file I/O operations

## Security Checklist

### 1. Secrets Management

#### Never Do This
```java
// Hardcoded secret in source code
@Path("database.password")
private String password = "mySecretPass123";

// API key in code
private static final String API_KEY = "sk-xxxxx";
```

#### Always Do This
```java
// Empty defaults - loaded from config file
@Path("database.password")
private String password = "";

@Path("redis.password")
private String redisPassword = "";
```

#### Verification Steps
- [ ] No hardcoded passwords, tokens, or API keys
- [ ] All secrets in config files (not source code)
- [ ] Config files with secrets in .gitignore
- [ ] Default values are empty strings

### 2. Input Validation

#### Validate Command Arguments
```java
@Override
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length < 1) {
        sender.sendMessage("Usage: /mycommand <player>");
        return true;
    }

    // Validate player name (alphanumeric, 3-16 chars)
    String playerName = args[0];
    if (!playerName.matches("^[a-zA-Z0-9_]{3,16}$")) {
        sender.sendMessage("Invalid player name");
        return true;
    }

    // Validate numeric input
    try {
        int amount = Integer.parseInt(args[1]);
        if (amount < 0 || amount > 10000) {
            sender.sendMessage("Amount must be 0-10000");
            return true;
        }
    } catch (NumberFormatException e) {
        sender.sendMessage("Invalid number");
        return true;
    }
}
```

#### Verification Steps
- [ ] All command arguments validated
- [ ] Numeric inputs have range checks
- [ ] String inputs have length limits
- [ ] No direct use of player input in file paths

### 3. Permission Checks

#### Always Check Permissions
```java
// Before any sensitive operation
if (!player.hasPermission("myplugin.admin")) {
    player.sendMessage("No permission");
    return;
}

// Check for specific actions
if (!player.hasPermission("myplugin.command.give")) {
    player.sendMessage("You cannot use this command");
    return;
}
```

#### Verification Steps
- [ ] All commands have permission checks
- [ ] Admin commands require admin permissions
- [ ] Sensitive operations gated by permissions
- [ ] Permission nodes documented in plugin.yml

### 4. SQL Injection Prevention

#### Never Concatenate SQL
```java
// DANGEROUS
String query = "SELECT * FROM players WHERE name = '" + playerName + "'";
statement.execute(query);
```

#### Always Use Prepared Statements
```java
// SAFE
PreparedStatement ps = connection.prepareStatement(
    "SELECT * FROM players WHERE name = ?"
);
ps.setString(1, playerName);
ResultSet rs = ps.executeQuery();
```

#### Verification Steps
- [ ] All database queries use PreparedStatement
- [ ] No string concatenation in SQL
- [ ] Player input never directly in queries

### 5. File Path Traversal Prevention

#### Validate File Paths
```java
// DANGEROUS - player could use "../../../etc/passwd"
File file = new File(dataFolder, playerInput);

// SAFE - validate path stays within data folder
File file = new File(dataFolder, playerInput);
if (!file.getCanonicalPath().startsWith(dataFolder.getCanonicalPath())) {
    throw new SecurityException("Path traversal attempt");
}
```

#### Verification Steps
- [ ] File paths validated against base directory
- [ ] No direct use of player input in file names
- [ ] Canonical path checks for traversal

### 6. Thread Safety

#### Shared Data
```java
// DANGEROUS - Not thread-safe
private Map<String, PlayerData> playerMap = new HashMap<>();

// SAFE - Thread-safe collection
private Map<String, PlayerData> playerMap = new ConcurrentHashMap<>();
```

#### Bukkit API from Async
```java
// DANGEROUS - Bukkit API from async thread
Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
    player.teleport(location); // CRASH RISK
});

// SAFE - Schedule back to main thread
Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
    Object result = database.query(...);
    Bukkit.getScheduler().runTask(plugin, () -> {
        player.sendMessage("Result: " + result);
    });
});
```

#### Verification Steps
- [ ] ConcurrentHashMap for shared data
- [ ] No Bukkit API calls from async threads
- [ ] Proper synchronization where needed

### 7. Error Handling

#### Don't Leak Information
```java
// DANGEROUS - Exposes internal details
catch (Exception e) {
    player.sendMessage("Error: " + e.getMessage() + "\n" + e.getStackTrace());
}

// SAFE - Generic message to player, detailed log
catch (Exception e) {
    player.sendMessage("An error occurred. Please contact an admin.");
    plugin.getLogger().severe("Error processing command: " + e.getMessage());
}
```

#### Verification Steps
- [ ] No stack traces sent to players
- [ ] Error messages don't reveal internals
- [ ] All exceptions logged with context
- [ ] No `printStackTrace()` calls

### 8. Deserialization Safety

#### Validate Deserialized Data
```java
// When loading from config/file
try {
    PlayerData data = gson.fromJson(json, PlayerData.class);
    if (data == null || data.getName() == null) {
        plugin.getLogger().warning("Invalid player data");
        return null;
    }
    return data;
} catch (JsonSyntaxException e) {
    plugin.getLogger().warning("Corrupted data: " + e.getMessage());
    return null;
}
```

#### Verification Steps
- [ ] Null checks after deserialization
- [ ] Type validation on loaded data
- [ ] Graceful handling of corrupted files

## Pre-Commit Security Checklist

Before ANY commit:

- [ ] **Secrets**: No hardcoded credentials
- [ ] **Input**: All player input validated
- [ ] **SQL**: All queries use PreparedStatement
- [ ] **Permissions**: All commands check permissions
- [ ] **Files**: Path traversal prevented
- [ ] **Threads**: Proper thread safety
- [ ] **Errors**: No information leakage
- [ ] **Logging**: No `printStackTrace()` or `System.out`
