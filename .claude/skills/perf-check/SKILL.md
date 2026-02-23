---
name: perf-check
description: Check for common Minecraft server performance anti-patterns. Use when writing tick tasks, event handlers, or optimizing hot paths. Covers main thread blocking, GC pressure, allocation in loops, and memory leaks.
---

# Game Server Performance

## Context: This Is a Real-Time Game Server

Your plugin runs on **Minecraft servers** — a real-time game loop running at **20 ticks per second (TPS)**. Each tick has a **50ms budget** to complete ALL server-side logic: entity AI, block updates, player movement, combat, redstone, and **all plugin code**.

If plugin code takes 30ms in a single tick, only 20ms remains for everything else. Below 18 TPS, players experience visible lag (rubber-banding, delayed interactions, unresponsive GUIs).

**The main thread is single-threaded.** There is no parallelism — every plugin, every event handler, every scheduled task on the main thread runs sequentially within the same 50ms window.

## Optimization Priority

Always prioritize by **execution frequency**, not code aesthetics:

| Priority | Code Path | Frequency | Budget Impact |
|----------|-----------|-----------|---------------|
| **Critical** | Tick tasks (`AbstractPerTickTask`) | 20x/sec | Direct TPS impact |
| **Critical** | High-frequency events (`PlayerMoveEvent`, `EntityDamageEvent`) | 100s-1000s/sec | Major TPS impact |
| **High** | Per-player operations (menu render, condition checks) | On interaction | Noticeable lag spikes |
| **Medium** | Commands, config reload, plugin enable | Rare | Acceptable if <100ms |
| **Low** | One-time init, shutdown, migration | Once | Not performance-sensitive |

**Rule: Don't optimize code that runs once per minute. Focus on code that runs 20+ times per second.**

## Profile First, Optimize Later

Never guess where the bottleneck is. Measure it.

**Tools:**
- **Spark** (`/spark profiler`) — best for identifying CPU hotspots and GC pressure
- **Paper Timings** (`/timings report`) — shows per-plugin tick time breakdown
- **JProfiler / VisualVM** — for deep allocation analysis

**Workflow:**
1. Reproduce the performance issue (low TPS, lag spike)
2. Profile with Spark to identify the actual hotspot
3. Fix only the identified bottleneck
4. Measure again to confirm improvement

## Main Thread Anti-Patterns

### Blocking I/O on Main Thread (CRITICAL)

A 20ms database query freezes the **entire server** for 20ms — ALL players, ALL entities, ALL plugins stop. This is 40% of the tick budget gone.

```java
// CATASTROPHIC: Blocks ALL players for the duration of the query
@EventHandler
public void onJoin(PlayerJoinEvent e) {
    ResultSet rs = database.query("SELECT * FROM players WHERE uuid = ?", uuid);
    // Server frozen for entire query duration
}

// CORRECT: Query async, apply result on main thread
@EventHandler
public void onJoin(PlayerJoinEvent e) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        PlayerData data = database.loadPlayer(uuid);  // Off main thread
        Bukkit.getScheduler().runTask(plugin, () -> {
            applyData(player, data);  // Back to main thread for Bukkit API
        });
    });
}
```

### GC Pressure in Hot Paths (HIGH)

Object allocation in tick tasks or high-frequency events increases garbage collection pauses. Young generation GC can freeze the server for 5-50ms — an entire tick.

```java
// BAD: String concatenation + unnecessary allocation every tick per player
@Override
public void run() {
    for (Player p : Bukkit.getOnlinePlayers()) {
        String msg = "Score: " + score + " | " + p.getName();  // New String objects each tick
        updateScoreboard(p, msg);
    }
}

// BETTER: Pre-build what you can, avoid per-tick allocations
private String scorePrefix;  // Rebuilt only when score changes

public void onScoreChange(int newScore) {
    scorePrefix = "Score: " + newScore + " | ";  // Allocate once on change
}

@Override
public void run() {
    for (Player p : Bukkit.getOnlinePlayers()) {
        updateScoreboard(p, scorePrefix, p.getName());  // Pass components, concat inside if needed
    }
}
```

### Autoboxing in Hot Paths (MEDIUM)

`Double.valueOf()` / `Integer.valueOf()` create wrapper objects. In tick tasks or frequent conditions, use primitives:

```java
// BAD: Autoboxing creates Double objects on every check
double amount = Double.valueOf(value);  // Allocates Double, then unboxes

// GOOD: Direct primitive parsing, no allocation
double amount = Double.parseDouble(value);
```

This matters in conditions/executes that fire on every player interaction — reducing allocation rate in hot paths directly reduces GC pressure.

## Specific Anti-Patterns for Minecraft

### String Operations in Event Handlers

```java
// BAD: .split() allocates new array + strings on EVERY event fire
@EventHandler
public void onMove(PlayerMoveEvent e) {
    String[] parts = config.getString("path").split(":");  // New array every event
}

// GOOD: Parse once, cache result
private String[] cachedParts;

@Override
public void onReload() {
    cachedParts = config.getString("path").split(":");  // Once on reload
}
```

### String Concatenation in Loops

```java
// BAD: O(n^2) — creates new String on every iteration
String result = "";
for (String s : list) {
    result += " " + s;  // New String object each iteration
}

// GOOD: O(n) — single allocation
String result = String.join(" ", list);
// Or use StringBuilder for complex cases
```

### Distance Calculations

```java
// BAD: Math.sqrt() is expensive — avoid in hot paths
if (loc1.distance(loc2) < 10.0) { ... }

// GOOD: Compare squared distances — skip sqrt entirely
if (loc1.distanceSquared(loc2) < 100.0) { ... }  // 10^2 = 100
```

### Stream API in Tick Tasks

```java
// AVOID in tick tasks: Stream creates iterator + lambda objects
long count = players.stream().filter(p -> p.isOnline()).count();

// PREFER: Simple for-loop, zero allocation
int count = 0;
for (Player p : players) {
    if (p.isOnline()) count++;
}
```

Streams are fine in commands, config loading, and other infrequent code paths.

### Bukkit.getOnlinePlayers() on Paper/Leaf Returns a View

On Paper/Leaf, `getOnlinePlayers()` returns an **unmodifiable view** of the internal player list, NOT a copy. Iterating it directly on the main thread is safe and efficient (no allocation). However, on async threads the list can change if a player joins/leaves during iteration.

```java
// On Paper/Leaf: getOnlinePlayers() is a view — no copy overhead
// Safe to iterate directly on the main thread
for (Player p : Bukkit.getOnlinePlayers()) { ... }

// If you need a snapshot (e.g., for async processing or removal during iteration):
List<Player> snapshot = new ArrayList<>(Bukkit.getOnlinePlayers());
```

## When Refactoring: Prioritize by Impact

When refactoring, always prioritize by runtime impact:

1. **Threading/concurrency bugs** — can cause data corruption, server crashes
2. **Resource leaks** — memory grows until OOM after hours of uptime
3. **Input validation** — malformed config = exception = broken feature
4. **Hot path optimizations** — directly impacts TPS
5. **Code duplication** — maintenance burden, not runtime impact
6. **Code quality** — cosmetic, zero runtime impact

**Never prioritize diamond operator cleanup over a ConcurrentHashMap fix.** The former is cosmetic; the latter prevents data corruption under load.

### Chunk Loading in Tick Tasks

Calling `World.getBlockAt()` or `Location.getBlock()` on unloaded chunks triggers **synchronous chunk loading** — potentially freezing the main thread for hundreds of milliseconds.

```java
// BAD: May trigger chunk load if location is in unloaded chunk
@Override
public void run() {
    Block block = location.getBlock();  // Synchronous chunk load if unloaded
}

// GOOD: Check if chunk is loaded first
@Override
public void run() {
    if (location.isChunkLoaded()) {
        Block block = location.getBlock();  // Safe — chunk already in memory
    }
}
```

### Leaked Scheduled Tasks

Tasks that are never cancelled keep running after a feature is disabled or on `/reload`, wasting tick budget.

```java
// BAD: Task runs forever — no way to cancel
public void onEnable() {
    Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 0L, 20L);
}

// GOOD: Store reference, cancel in onDisable()
private BukkitTask tickTask;

public void onEnable() {
    tickTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 0L, 20L);
}

public void onDisable() {
    if (tickTask != null) tickTask.cancel();
}
```

## Memory Rules for Long-Running Servers

Minecraft servers run for **days or weeks** continuously. Issues invisible in testing become critical at scale:

- **Unbounded maps** without eviction = memory leak (e.g., `PlayerStorage.playerDataMap` growing forever)
- **Unclosed resources** (streams, connections) = file handle exhaustion after hours
- **Cancelled task references** — always null out or remove references to stopped tasks
- **Singleton cleanup** — null instances in `onDisable()` to prevent stale references on `/reload`

## Rules

1. **Profile before optimizing** — use Spark or Timings to find actual bottlenecks
2. **Protect the tick loop** — never block main thread with I/O
3. **Minimize allocations in hot paths** — tick tasks, high-frequency events
4. **Use primitives over wrappers** in frequently-called conditions/executes
5. **Cache expensive computations** — don't reparse config strings every tick
6. **Plan for long uptime** — evict stale data, close resources, clean up on disable
7. **Measure improvement** — profile before AND after optimization
