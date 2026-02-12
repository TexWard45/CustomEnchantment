# CustomEnchantment Task System Reference

This document provides comprehensive reference for the background task system in the CustomEnchantment plugin, including task types, scheduling, and the effect execution queue.

## Table of Contents

- [Overview](#overview)
- [TaskModule](#taskmodule)
- [Task Categories](#task-categories)
- [Effect Execution System](#effect-execution-system)
- [Creating Custom Tasks](#creating-custom-tasks)
- [Task Reference](#task-reference)

---

## Overview

The task system provides background processing for:

| Category | Purpose |
|----------|---------|
| Effect Execution | Async/sync enchantment effect processing |
| Player Processing | Per-player tick-based updates |
| Attribute Calculation | Recalculating player attributes |
| Mining Operations | Vein/explosion mining block processing |
| Power Calculation | Excel-based power score calculation |
| Data Persistence | Periodic data saving |

### Execution Modes

| Mode | Description | Use Case |
|------|-------------|----------|
| Synchronous | Main server thread | Bukkit API calls |
| Asynchronous | Separate thread | Heavy calculations, I/O |

---

## TaskModule

The `TaskModule` manages all background tasks.

### Location

```java
package com.bafmc.customenchantment.task;

public class TaskModule extends PluginModule<CustomEnchantment> {
    // All task instances
}
```

### Task Registration

All tasks are started in `onEnable()`:

```java
public void onEnable() {
    // Async effect execution (every tick)
    this.asyncEffectExecuteTask = new EffectExecuteTask(true);
    this.asyncEffectExecuteTask.runTaskTimerAsynchronously(getPlugin(), 0, 1);

    // Sync effect execution (every tick)
    this.effectExecuteTask = new EffectExecuteTask(false);
    this.effectExecuteTask.runTaskTimer(getPlugin(), 0, 1);

    // Player task (every second)
    this.cePlayerTask = new CEPlayerTask(getPlugin());
    this.cePlayerTask.runTaskTimer(getPlugin(), 0, 20);

    // CE Caller (every tick)
    this.ceCallerTask = new CECallerTask(getPlugin());
    this.ceCallerTask.runTaskTimer(getPlugin(), 0, 1);

    // ... more tasks
}
```

### Accessing Tasks

```java
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.task.TaskModule;

TaskModule taskModule = CustomEnchantment.instance().getTaskModule();

// Access specific tasks
EffectExecuteTask asyncEffects = taskModule.getAsyncEffectExecuteTask();
EffectExecuteTask syncEffects = taskModule.getEffectExecuteTask();
CECallerTask caller = taskModule.getCeCallerTask();
```

---

## Task Categories

### Effect Processing Tasks

| Task | Thread | Interval | Purpose |
|------|--------|----------|---------|
| `EffectExecuteTask` (async) | Async | 1 tick | Process async effects |
| `EffectExecuteTask` (sync) | Main | 1 tick | Process sync effects |

### Player Update Tasks

| Task | Thread | Interval | Purpose |
|------|--------|----------|---------|
| `CECallerTask` | Main | 1 tick | Call AUTO enchantments |
| `CEPlayerTask` | Main | 20 ticks | Player state updates |
| `RegenerationTask` | Main | 4 ticks | Health regeneration |
| `UpdateAttributeTask` | Main | 4 ticks | Vanilla attribute sync |
| `SlowResistanceTask` | Main | 4 ticks | Slowness resistance |
| `UpdateJumpTask` | Main | 4 ticks | Jump boost updates |

### Attribute Tasks

| Task | Thread | Interval | Purpose |
|------|--------|----------|---------|
| `RecalculateAttributeTask` | Async | 1 tick | Recalc custom attributes |
| `PowerAsyncTask` | Async | 20 ticks | Power score calculation |

### Equipment Tasks

| Task | Thread | Interval | Purpose |
|------|--------|----------|---------|
| `CEExtraSlotTask` | Main | 4 ticks | Extra slot management |
| `UnbreakableArmorTask` | Main | Config | Armor durability |
| `OutfitItemTask` | Main | 1 tick | Outfit display |
| `OutfitTopInventoryTask` | Main | 1 tick | Outfit inventory |
| `SigilItemTask` | Async | 1 tick | Sigil display |

### World Tasks

| Task | Thread | Interval | Purpose |
|------|--------|----------|---------|
| `SpecialMiningTask` | Main | 1 tick | Vein/explosion mining |
| `BlockTask` | Main | 1 tick | Block processing |
| `ArrowTask` | Main | 20 ticks | Arrow tracking |

### Data Tasks

| Task | Thread | Interval | Purpose |
|------|--------|----------|---------|
| `SaveTask` | Main | 15 minutes | Periodic data save |
| `ExpTask` | Main | 200 ticks | Experience tracking |

---

## Effect Execution System

### EffectExecuteTask

The core task for processing enchantment effects.

```java
public class EffectExecuteTask extends BukkitRunnable {
    private int maxProcessPerTick = 500;
    private final ConcurrentHashMap<String, EffectData> effectSchedulerMap;
    private List<EffectData> list;
    private boolean async;
}
```

### Effect Processing Flow

1. **Queue Effect**: Add effect to execution queue
2. **Check Delay**: Wait for delay period if configured
3. **Execute Effect**: Run the effect logic
4. **Handle Period**: Re-queue periodic effects
5. **Cleanup**: Remove completed effects

### Adding Effects to Queue

```java
EffectExecuteTask task = CustomEnchantment.instance()
    .getTaskModule().getAsyncEffectExecuteTask();

List<EffectData> effects = new ArrayList<>();
effects.add(effectData);

task.addEffectDataList(effects);
```

### Effect Scheduling

Effects support delay and period configuration:

```yaml
# In enchantment config
effects:
  - type: ADD_POTION
    args: "SPEED:1:100"
    delay: 20          # Wait 20 ticks before first execution
    period: 40         # Execute every 40 ticks after
    scheduler: true    # Enable named scheduling
```

### Named Effect Scheduler

Named effects can be cancelled/replaced:

```java
// Remove a named effect
task.removeEffectData(cePlayer, "my_effect_name");

// Get effect by name
EffectData data = task.getEffectData("playername-my_effect_name");
```

### Sync vs Async Effects

Effects declare their thread requirement:

```java
public class EffectAddPotion extends EffectHook {
    @Override
    public boolean isAsync() {
        return false; // Must run on main thread (Bukkit API)
    }
}

public class EffectMessage extends EffectHook {
    @Override
    public boolean isAsync() {
        return true; // Can run async
    }
}
```

---

## Creating Custom Tasks

### Basic Task Structure

```java
import org.bukkit.scheduler.BukkitRunnable;
import com.bafmc.customenchantment.CustomEnchantment;

public class MyCustomTask extends BukkitRunnable {
    private CustomEnchantment plugin;

    public MyCustomTask(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Task logic here
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            processPlayer(player);
        }
    }

    private void processPlayer(Player player) {
        // Per-player processing
    }
}
```

### Starting Tasks

```java
// Synchronous task (main thread)
MyCustomTask task = new MyCustomTask(plugin);
task.runTaskTimer(plugin, 0, 20); // Every second

// Asynchronous task
MyAsyncTask asyncTask = new MyAsyncTask(plugin);
asyncTask.runTaskTimerAsynchronously(plugin, 0, 20);
```

### Rate Limiting

For performance, limit processing per tick:

```java
public class RateLimitedTask extends BukkitRunnable {
    private static final int MAX_PER_TICK = 50;
    private List<Data> queue = new ArrayList<>();

    @Override
    public void run() {
        int processed = 0;
        ListIterator<Data> iter = queue.listIterator();

        while (iter.hasNext() && processed < MAX_PER_TICK) {
            Data data = iter.next();
            process(data);
            iter.remove();
            processed++;
        }
    }

    public void add(Data data) {
        queue.add(data);
    }
}
```

---

## Task Reference

### CECallerTask

Calls AUTO enchantment triggers every tick.

```java
public class CECallerTask extends BukkitRunnable {
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            CECallerBuilder
                .build(player)
                .setCEType(CEType.AUTO)
                .call();
        }
    }
}
```

**Purpose**: Triggers enchantments with AUTO activation type.

### RegenerationTask

Handles health regeneration based on custom attributes.

```java
public class RegenerationTask extends BukkitRunnable {
    public static final long REGENERATION_SECONDS = 5;

    public void run(Player player) {
        // Get regeneration rate
        double healRegeneration = cePlayer.getCustomAttribute()
            .getValue(CustomAttributeType.HEALTH_REGENERATION);

        // Apply healing
        player.setHealth(Math.min(maxHealth, currentHealth + healAmount));
    }
}
```

**Purpose**: Applies HEALTH_REGENERATION and HEALTH_REGENERATION_PERCENT attributes.

### SpecialMiningTask

Processes vein and explosion mining block breaks.

```java
public class SpecialMiningTask extends BukkitRunnable {
    private static final int maxProcessPerTick = 50;
    private List<Data> list = new ArrayList<>();

    public void run() {
        // Process up to 50 blocks per tick
        for (int i = 0; i < maxProcessPerTick && !list.isEmpty(); i++) {
            Data data = list.remove(0);
            if (data.canBreak(block, factionSupport)) {
                data.breakBlock(block);
            }
        }
    }

    public void add(PlayerSpecialMining mining, Location loc,
                    Player player, SpecialMiningData settings) {
        list.add(new Data(mining, loc, player, settings));
    }
}
```

**Purpose**: Rate-limited block breaking for special mining modes.

### PowerAsyncTask

Calculates player power scores using Excel formulas.

```java
public class PowerAsyncTask extends BukkitRunnable {
    private Workbook workbook; // Excel workbook
    private FormulaEvaluator evaluator;

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Set attribute values in Excel
            for (Map.Entry<Integer, AttributeSetter> entry : ATTRIBUTE_SETTERS.entrySet()) {
                double value = entry.getValue().get(player, vanilla, custom);
                setCellValue(entry.getKey(), 3, value);
            }

            // Evaluate formulas
            evaluator.evaluateAll();

            // Read power scores
            double defensePower = readEvaluatedCell(sheet, evaluator, 24, 11);
            double attackPower = readEvaluatedCell(sheet, evaluator, 25, 11);

            // Store results
            customAttribute.addCustomAttribute("TOTAL_POWER", ...);
            customAttribute.addCustomAttribute("ATK_POWER", ...);
            customAttribute.addCustomAttribute("DEF_POWER", ...);
        }
    }
}
```

**Purpose**: Complex power calculations using Excel spreadsheet as calculation engine.

### SaveTask

Periodic data persistence.

```java
public class SaveTask extends BukkitRunnable {
    public void run() {
        // Save all player data
        for (CEPlayer cePlayer : CEPlayer.getCePlayerMap().getCEPlayers()) {
            cePlayer.getStorage().getConfig().save();
        }
    }
}
```

**Interval**: Every 15 minutes (20 * 60 * 15 ticks)

---

## Best Practices

### Thread Safety

1. **Async tasks**: Use `ConcurrentHashMap` for shared data
2. **Sync tasks**: Safe to use Bukkit API directly
3. **Cross-thread**: Use `runTask()` to schedule main thread work from async

```java
// From async task, schedule main thread work
Bukkit.getScheduler().runTask(plugin, () -> {
    // This runs on main thread
    player.sendMessage("Hello!");
});
```

### Performance

1. **Rate limiting**: Process max N items per tick
2. **Batch processing**: Group similar operations
3. **Lazy computation**: Only calculate when needed
4. **Caching**: Cache expensive calculations

### Error Handling

```java
public void run() {
    for (Player player : Bukkit.getOnlinePlayers()) {
        try {
            processPlayer(player);
        } catch (Exception e) {
            e.printStackTrace();
            // Continue processing other players
        }
    }
}
```

### Cancellation

Always clean up resources when task is cancelled:

```java
@Override
public void cancel() {
    // Cleanup resources
    if (workbook != null) {
        try {
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    super.cancel();
}
```

---

## Task Tick Intervals

| Interval | Ticks | Purpose |
|----------|-------|---------|
| Every tick | 1 | Real-time processing |
| 4 ticks | 4 | Frequent updates |
| 20 ticks | 20 | Once per second |
| 200 ticks | 200 | Every 10 seconds |
| 18000 ticks | 18000 | Every 15 minutes |

---

## See Also

- [ARCHITECTURE.md](ARCHITECTURE.md) - System architecture
- [API.md](API.md) - Developer API reference
- [ENCHANTMENT-CONFIG.md](ENCHANTMENT-CONFIG.md) - Effect configuration
