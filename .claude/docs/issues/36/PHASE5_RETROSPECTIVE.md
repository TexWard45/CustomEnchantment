# Phase 5 Retrospective: Equipment Menu Migration

## Outcome

**Iterations: 1** (target: 1-2)
**Bugs found in testing: 1** (PAPI NPE during initial item setup)
**Time to fix: ~2 minutes** (override `setupItemStack()` to return null)

## What Went Right

### 1. Documentation-First Approach Paid Off
The Phase 5 implementation doc (`issue-36-phase5-equipment.md`) was updated with 22 edge cases, all 7 subsystem analyses, and detailed code-level notes BEFORE any code was written. Every `if` branch in the legacy code was documented with line numbers.

### 2. 1:1 Port Strategy
Instead of refactoring or "improving" the legacy code, we did a direct port:
- `player` → `owner`
- `menuView` → `inventory`
- `getSlots()` → `getSlotsByName()`
- `getItemStack(player, name)` → `getTemplateItemStackForEquipment(name)`
- `updateTemporaryItem(slots, item)` → direct `inventory.setItem()` loops

This eliminated logic bugs entirely — the only bug was a framework-level API difference.

### 3. Code Review Before Testing
The code-reviewer agent caught 3 CRITICAL issues before deployment:
- Memory leak in static `swapSkinCooldowns` map (moved to proper static context)
- Missing `clearAll()` for plugin disable/reload cleanup
- `putMenu()` not marking old menu as removed (leaked BukkitRunnable)

All fixed before the first test, preventing 3 potential bugs.

### 4. Previous Phase Lessons Applied Successfully
| Lesson | Source | Result |
|--------|--------|--------|
| `updateSlots(name, null)` resets to YAML default | Phase 4 Bug 2 | No display bugs |
| Sound at top-level `sound: open:` format | Phase 4 Bug 4 | Sound works correctly |
| Copy Vietnamese text exactly | Phase 3 | All text correct |
| Template items (no-slot YAML) | Phase 3-4 | Skin display works |
| Code review before testing | Phase 4 | 3 CRITICALs caught pre-deploy |

## The One Bug: PAPI NPE

### What Happened
```
NullPointerException: Cannot invoke "OfflinePlayer.getPlayer()" because "player" is null
  at CustomEnchantmentPlaceholder.onRequest(CustomEnchantmentPlaceholder.java:84)
  at ItemData.getItemStack(ItemData.java:75)
  at AbstractItem.setupItemStack(AbstractItem.java:28)
  at AbstractMenu.setupItems(AbstractMenu.java:93)
```

### Root Cause
BafFramework's `AbstractItem.setupItemStack()` calls `ItemData.getItemStack()` **without player context**. When the `player-info` item's YAML lore contains `%customenchantment_...%` PAPI placeholders, `ItemStackBuilder` passes `null` to PlaceholderAPI → NPE.

### Why It Wasn't Caught
- This is a **framework internal behavior** — not visible from reading the API
- No other Phase 1-4 menus had PAPI placeholders in YAML item definitions
- The legacy CustomMenu API's `CItem.getItemStack(Player)` always had player context
- The code review focused on logic parity, not framework-level PAPI resolution

### Fix
Override `setupItemStack()` in `PlayerInfoEquipmentItem` to return `null`. The item is rendered by `updatePlayerInfoSlots()` immediately after, which uses `itemData.getItemStack(owner)` with proper player context.

### Lesson for Future
**NEW PATTERN: Items with PAPI placeholders in YAML must override `setupItemStack()` to return `null`** and be rendered dynamically by the menu's update methods. This applies to any item whose YAML lore/display contains `%placeholder%` patterns.

## Cross-Phase Metrics

| Phase | Menu | LOC (Legacy) | Iterations | Bugs | Fix Time |
|-------|------|-------------|------------|------|----------|
| 1 | Tinkerer | ~200 | 2 | 2 | ~30 min |
| 2 | BookCraft | ~400 | 7 | 5+ | ~3 hours |
| 3 | CE Anvil | ~350 | 3 | 3 | ~1 hour |
| 4 | BookUpgrade + ArtifactUpgrade | ~500 | 1 | 4 | ~30 min |
| **5** | **Equipment** | **717** | **1** | **1** | **~2 min** |

**Trend:** Iterations decreased as documentation quality increased, despite increasing menu complexity.

## Key Patterns Established

### 1. Template Item with Dynamic Rendering
```java
// For items with PAPI placeholders
@Override
public ItemStack setupItemStack() {
    return null; // Rendered dynamically with player context
}
```

### 2. Static Singleton with Lifecycle
```java
private static final Map<String, Menu> menuMap = new HashMap<>();

public static Menu putMenu(Player player, Menu menu) {
    Menu old = menuMap.put(player.getName(), menu);
    if (old != null) old.extraData.setRemoved(true); // Prevent leaked tasks
    return menu;
}

public static void clearAll() {
    menuMap.values().forEach(m -> m.extraData.setRemoved(true));
    menuMap.clear();
}
```

### 3. Auto-Update Task with Safety Guard
```java
new BukkitRunnable() {
    public void run() {
        if (extraData.isRemoved() || !owner.isOnline()) {
            cancel();
            return;
        }
        updatePlayerInfoSlots();
    }
}.runTaskTimer(plugin, 0, 5);
```

### 4. Per-Index Slot Updates (Extra Slots)
BafFramework's `updateSlots(name, item)` updates ALL slots of that name. For per-index rendering:
```java
List<Integer> slots = getSlotsByName(slotName);
for (int i = 0; i < limit; i++) {
    if (hasItem) {
        inventory.setItem(slots.get(i), resolvedItem);
    } else {
        inventory.setItem(slots.get(i), getTemplateItemStack(slotName));
    }
}
```

## What Could Be Improved

1. **Pre-existing `e.printStackTrace()` in MenuModule** — should use logger (not introduced in Phase 5, but noticed)
2. **`updateSlotsAdvance` and `updateSlotsAdvanceForSlot` share 90% code** — could extract shared `resolveDisplayItem()` method. Left as-is to maintain 1:1 parity with legacy.
3. **EquipmentMenu.EquipmentAddReason enum** — new code references legacy class's enum. Could be extracted to shared location when legacy is removed.
