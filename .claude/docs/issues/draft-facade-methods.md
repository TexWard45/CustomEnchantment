# Draft Issue: refactor: Add facade methods to modules to eliminate deep getter chains

**Labels:** refactor, enhancement

---

## Summary

Add delegation/facade methods directly on `PluginModule` classes (`GuardModule`, `TaskModule`, `DatabaseModule`) so callers access module functionality through a flat public API instead of chaining through internal managers. This eliminates Law of Demeter violations and hides module internals from 60+ call sites across the codebase.

## Problem

### Current Behavior

Modules expose their internal structure via `@Getter`, forcing callers to chain through 3-4 levels:

```java
// 4-level chain — callers know GuardModule has a GuardManager which has PlayerGuard
CustomEnchantment.instance().getGuardModule().getGuardManager().getPlayerGuard(player);

// 4-level chain — callers know TaskModule has a BlockTask with setBlock()
CustomEnchantment.instance().getTaskModule().getBlockTask().setBlock(location, material, duration);

// 3-level chain — callers know DatabaseModule has a Database
CustomEnchantment.instance().getDatabaseModule().getDatabase().insertLogs(...);
```

### Expected Behavior

Modules expose flat facade methods that hide internal structure:

```java
// Callers only know GuardModule can give them a PlayerGuard
CustomEnchantment.instance().getGuardModule().getPlayerGuard(player);

// Callers only know TaskModule can set blocks
CustomEnchantment.instance().getTaskModule().setBlock(location, material, duration);

// Callers only know DatabaseModule can insert logs
CustomEnchantment.instance().getDatabaseModule().insertLogs(...);
```

### Impact

- **60+ call sites** across the codebase use deep getter chains
- **8 callers** chain through `GuardModule.getGuardManager()`
- **9 callers** chain through `TaskModule.getXxxTask()`
- If any module restructures its internals (e.g., renames `GuardManager`), **all callers break**
- Makes unit testing harder — callers need the full module chain mocked

## Solution

### Approach

Add public delegation methods to each module that forward to internal managers/tasks. Then update callers to use the new facade methods. Finally, reduce `@Getter` visibility on internal fields so new code can't bypass the facade.

### Modules to Refactor

#### 1. GuardModule (8 callers)

```java
// ADD these facade methods:
public class GuardModule extends PluginModule<CustomEnchantment> {
    private GuardManager guardManager; // remove @Getter or make package-private

    public PlayerGuard getPlayerGuard(Player player) {
        return guardManager.getPlayerGuard(player);
    }

    public void addEntityGuard(EntityGuard guard) {
        guardManager.addEntityGuard(guard);
    }

    public void removeEntityGuard(EntityGuard guard) {
        guardManager.removeEntityGuard(guard);
    }
}
```

**Callers to update:**
- `CEAPI.java:28`
- `PlayerGuard.java:33, 39`
- `EntityListener.java:67`
- `GuardListener.java:24`
- `EffectRemoveGuard.java:34`
- `EffectSummonGuard.java:53`
- `EffectSummonCustomGuard.java:98`

#### 2. TaskModule (9 callers)

```java
// ADD these facade methods:
public class TaskModule extends PluginModule<CustomEnchantment> {
    // Existing fields stay, but accessed via facade

    public void addEffectData(EffectTaskSeparate effectTask) {
        asyncEffectExecuteTask.addEffectDataList(effectTask.getEffectAsyncList());
        effectExecuteTask.addEffectDataList(effectTask.getEffectList());
    }

    public void removeEffectData(String playerName, String name) {
        asyncEffectExecuteTask.removeEffectData(playerName, name);
        effectExecuteTask.removeEffectData(playerName, name);
    }

    public void setBlock(Location location, Material material, long duration) {
        blockTask.setBlock(location, material, duration);
    }

    public boolean containsBlock(Location location) {
        return blockTask.contains(location);
    }

    public SpecialMiningTask getSpecialMiningTask() { ... } // keep — already flat

    public void setReloading(boolean reloading) {
        powerAsyncTask.setReloading(reloading);
    }
}
```

**Callers to update:**
- `CustomEnchantment.java:97-103` (addEffectTask/removeEffectTask can delegate to TaskModule)
- `CommandReload.java:18`
- `VeinSpecialMine.java:141`
- `ExplosionSpecialMine.java:146`
- `BlockListener.java:39`
- `EffectSetBlock.java:41`

#### 3. DatabaseModule (1 caller)

```java
public class DatabaseModule extends PluginModule<CustomEnchantment> {
    private Database database;

    public void insertLogs(Player player, CEItem item1, CEItem item2, String result, String data) {
        database.insertLogs(player, item1, item2, result, data);
    }
}
```

**Caller to update:**
- `CustomEnchantmentLog.java:9`

#### 4. Update CEAPI to use new facades

```java
// BEFORE
public static PlayerGuard getPlayerGuard(Player player) {
    return CustomEnchantment.instance().getGuardModule().getGuardManager().getPlayerGuard(player);
}

// AFTER
public static PlayerGuard getPlayerGuard(Player player) {
    return CustomEnchantment.instance().getGuardModule().getPlayerGuard(player);
}
```

### What NOT to Change

- **MainConfig access** (`getPlugin().getMainConfig().getXxx()`) — this is a 2-level chain and is standard config access. Not worth wrapping.
- **Static registries** (`CEPlayer.getCePlayerMap()`, `CEItemRegister.getCEItem()`) — already decoupled from modules.
- **ConfigModule.onReload()** — direct module method call, already clean.

### Alternatives Considered

| Alternative | Why Not (Yet) |
|-------------|---------------|
| Constructor injection | Larger refactor, good as Phase 2 |
| Service interfaces | Over-engineering for current scale |
| Event bus | Only useful for config reload, not service calls |

## Tasks

- [ ] Add facade methods to `GuardModule` (getPlayerGuard, addEntityGuard, removeEntityGuard)
- [ ] Update 8 GuardModule callers to use facade methods
- [ ] Add facade methods to `TaskModule` (addEffectData, removeEffectData, setBlock, containsBlock, setReloading)
- [ ] Update 9 TaskModule callers to use facade methods
- [ ] Simplify `CustomEnchantment.addEffectTask()`/`removeEffectTask()` to delegate to `TaskModule` facade
- [ ] Add facade method to `DatabaseModule` (insertLogs)
- [ ] Update 1 DatabaseModule caller to use facade method
- [ ] Update `CEAPI` to use facade methods instead of chaining
- [ ] Restrict `@Getter` on internal fields (remove class-level `@Getter`, add field-level only where needed)
- [ ] Verify build passes: `./gradlew build`
- [ ] Verify tests pass: `./gradlew test`

## Hints

### Key Files

- `src/main/java/com/bafmc/customenchantment/guard/GuardModule.java` — 30 lines, simple module
- `src/main/java/com/bafmc/customenchantment/task/TaskModule.java` — 127 lines, 20 task fields
- `src/main/java/com/bafmc/customenchantment/database/DatabaseModule.java` — 31 lines, simple module
- `src/main/java/com/bafmc/customenchantment/api/CEAPI.java` — public API, should be updated first as reference
- `src/main/java/com/bafmc/customenchantment/CustomEnchantment.java` — plugin class with addEffectTask/removeEffectTask

### Pattern to Follow

`BlockListener.java:39` already caches a reference at construction time — this is the best existing pattern. The facade methods build on this by making the module the entry point instead of the internal task.

### Pitfalls

- Don't remove `@Getter` from fields that other modules within the **same package** need directly
- `TaskModule.getSpecialMiningTask()` is already a flat getter — keep it as-is since callers use the task object directly
- Some `BlockListener`/`EntityListener` cache manager references in constructors — update these to cache the module instead, or just use the facade

---
**Priority:** P3 | **Effort:** M | **Labels:** refactor, enhancement
