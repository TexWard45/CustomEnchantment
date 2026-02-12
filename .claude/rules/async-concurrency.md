# Async & Concurrency

## Bukkit Main Thread Rule

**Most Bukkit API calls MUST run on the main thread.** This includes:
- Player methods that send packets (teleport, openInventory, closeInventory, sendMessage)
- World modifications (block changes, entity spawning)
- Event handling

### Safe Off Main Thread (Data Preparation)

These are **just object creation/manipulation** and do NOT require the main thread:
- `Bukkit.createInventory()` — creates an Inventory object in memory
- `inventory.setItem()` — sets items in an Inventory object
- `new ItemStack()` / ItemStack manipulation
- Building data structures, config parsing, calculations

**Key distinction:** Preparing data (creating inventories, building ItemStacks) is safe async. Only **sending data to the client** (opening inventory, teleporting, sending messages) requires the main thread.

```java
// CORRECT PATTERN: Prepare async, display on main thread
Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
    // Safe: just object creation
    Inventory inv = Bukkit.createInventory(null, 54, "Title");
    inv.setItem(0, new ItemStack(Material.DIAMOND));

    // Must be on main thread: sends packets to player
    Bukkit.getScheduler().runTask(plugin, () -> {
        player.openInventory(inv);
    });
});
```

## When to Use Async

| Task | Thread | Why |
|------|--------|-----|
| Database queries | Async | I/O blocking |
| Redis pub/sub | Async | Network I/O |
| HTTP requests | Async | Network I/O |
| File I/O (large) | Async | Disk blocking |
| Config reload | Main | Bukkit API access |
| Player actions | Main | Bukkit requirement |
| Event listeners | Main | Bukkit default |

## BukkitScheduler Patterns

```java
// Run async (database, network)
Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
    Object result = database.query(...);

    // Return to main thread for Bukkit API
    Bukkit.getScheduler().runTask(plugin, () -> {
        player.sendMessage("Result: " + result);
    });
});

// Repeating task (main thread)
Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    // Runs every 20 ticks (1 second)
}, 0L, 20L);

// Repeating async task
Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
    // Periodic background work
}, 0L, 100L);
```

## Thread-Safe Collections

When data is accessed from multiple threads, use concurrent collections:

```java
// GOOD: Thread-safe for multi-server player maps
private Map<String, PlayerData> playerMap = new ConcurrentHashMap<>();

// BAD: Not thread-safe if accessed from async tasks
private Map<String, PlayerData> playerMap = new HashMap<>();
```

## Rules

1. **Never block the main thread** with database/network calls
2. **Never call Bukkit API from async threads** without scheduling back
3. **Use ConcurrentHashMap** when data is shared between threads
4. **Cancel tasks in onDisable()** to prevent leaks on reload
5. **Keep tick tasks lightweight** — heavy work goes to async
