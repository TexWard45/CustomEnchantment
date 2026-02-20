# Issue #39: Extract EquipmentCustomMenu into Handler Pattern & Fix Runtime Issues

## Overview

**Issue:** [#39](https://github.com/TexWard45/CustomEnchantment/issues/39)
**Type:** refactor + enhancement
**Priority:** P2 | **Effort:** M
**Status:** Open
**Parent context:** Phase 5 of #30 (CustomMenu Migration) produced the current EquipmentCustomMenu

## Problem Summary

EquipmentCustomMenu was migrated from legacy CustomMenu to BafFramework in Phase 5 (#30), but internal logic was ported 1:1 without structural refactoring. It is a **796-line god object** with runtime issues that other menus already fixed during migration.

### Five Core Problems

| # | Problem | Severity | Category |
|---|---------|----------|----------|
| 1 | God object (796 lines) — `addItem()` 122L, `returnItem()` 97L | Medium | Maintainability |
| 2 | Leaked BukkitRunnable — `autoUpdateMenu()` has no stored reference | High | Runtime bug |
| 3 | Thread-unsafe static maps — `menuMap` and `swapSkinCooldowns` are `HashMap` | High | Thread safety |
| 4 | Code duplication — `updateSlotsAdvance()` ~95% identical in two places | Low | Code quality |
| 5 | No PlayerQuit cleanup — `menuMap` entries leak on disconnect | Medium | Memory leak |

## Current Architecture

### File Structure

```
menu/equipment/
├── EquipmentCustomMenu.java          (796 lines - god object)
├── EquipmentExtraData.java           (30 lines - state)
└── item/
    ├── EquipmentSlotItem.java        (31 lines)
    ├── ExtraSlotEquipmentItem.java
    ├── ProtectDeadEquipmentItem.java
    ├── WingsEquipmentItem.java
    └── PlayerInfoEquipmentItem.java
```

### Key Methods (EquipmentCustomMenu.java)

| Method | Lines | Responsibility |
|--------|-------|----------------|
| `putMenu/getMenu/removeMenu/clearAll` | ~100-127 | Static singleton management |
| `registerItems/setupItems` | ~128-151 | Menu lifecycle |
| `handlePlayerInventoryClick/handleClose` | ~152-173 | Click handling |
| `addItem()` | ~181-304 (122 lines) | Add item — 3 paths: extra slot, protect dead, armor/weapon |
| `returnItem()` | ~306-404 (97 lines) | Return item — 4 paths: extra slot, protect dead, offhand, armor |
| `swapSkinWithCooldown/swapSkin` | ~406-450 | Skin swap feature |
| `updateMenuWithPreventAction/updateMenu` | ~452-474 | Menu refresh |
| `updateSlotsAdvance` (x2 variants) | ~481-507, 709-735 | Skin display rendering (DUPLICATED) |
| `updatePlayerInfo/updateOffhand/updateWings/updateProtectDead/updateExtraSlots` | ~591-707 | Subsystem updates |
| `autoUpdateMenu` | ~737-749 | Periodic task (LEAKED) |

### State (EquipmentExtraData.java)

```java
@Getter @Setter
public class EquipmentExtraData extends ExtraData {
    private boolean inUpdateMenu = false;        // Prevent concurrent updates
    private boolean removed = false;             // Track if menu is disposed
    private Map<EquipSlot, Long> lastClickTime;  // Per-slot 500ms cooldown
}
```

## Target Architecture

### Reference: CEAnvilCustomMenu Handler Pattern

CEAnvilCustomMenu (342 lines) uses a Strategy pattern with `Slot2Handler` interface + 14 handler implementations:

```java
interface Slot2Handler {
    boolean isSuitable(CEItem ceItem);
    void updateView(CEAnvilCustomMenu menu);
    void updateConfirm(CEAnvilCustomMenu menu);
    void clickProcess(CEAnvilCustomMenu menu, String itemName);
    ApplyReason apply(CEItem ceItem1, CEItem ceItem2);
    void clearPreviews(CEAnvilCustomMenu menu);
}
```

Handler registration via factory map:
```java
private static final Map<String, Supplier<Slot2Handler>> handlerFactoryMap = new HashMap<>();
public static void registerHandler(String type, Supplier<Slot2Handler> factory) { ... }
```

### Proposed Equipment Handler Pattern

```java
interface EquipmentAddHandler {
    EquipmentAddReason add(ItemStack item, CEItem ceItem);
    void returnItem(String name, int slot);
}

// Implementations
class ExtraSlotHandler implements EquipmentAddHandler { ... }
class ProtectDeadHandler implements EquipmentAddHandler { ... }
class ArmorWeaponHandler implements EquipmentAddHandler { ... }
```

### Target File Structure

```
menu/equipment/
├── EquipmentCustomMenu.java          (reduced to ~400 lines)
├── EquipmentExtraData.java           (unchanged)
├── handler/                          (NEW)
│   ├── EquipmentAddHandler.java      (interface)
│   ├── ExtraSlotHandler.java
│   ├── ProtectDeadHandler.java
│   └── ArmorWeaponHandler.java
└── item/
    ├── EquipmentSlotItem.java
    ├── ExtraSlotEquipmentItem.java
    ├── ProtectDeadEquipmentItem.java
    ├── WingsEquipmentItem.java
    └── PlayerInfoEquipmentItem.java
```

## Task Checklist

### Runtime Fixes (do first — these are bugs)

- [ ] Store `BukkitTask` reference from `autoUpdateMenu()`, cancel in `handleClose()` + `onDisable()`
- [ ] Replace `HashMap` with `ConcurrentHashMap` for `menuMap` and `swapSkinCooldowns`
- [ ] Add PlayerQuit listener to clean up `menuMap` entries

### Structural Refactoring

- [ ] Extract `EquipmentAddHandler` interface
- [ ] Create `ExtraSlotHandler` implementation
- [ ] Create `ProtectDeadHandler` implementation
- [ ] Create `ArmorWeaponHandler` implementation
- [ ] Refactor `addItem()` to delegate to handlers
- [ ] Refactor `returnItem()` to delegate to handlers
- [ ] Extract `applySkinTemplate()` helper to eliminate `updateSlotsAdvance` duplication

### Verification

- [ ] All existing behavior preserved (manual server test)
- [ ] No leaked tasks on `/reload`
- [ ] Thread-safe map access under load

## Key Fixes (Code Snippets)

### Fix 1: BukkitTask Leak

```java
// BEFORE (leaked — no reference)
private void autoUpdateMenu() {
    new BukkitRunnable() { ... }.runTaskTimer(plugin, 0, 5);
}

// AFTER (stored + cancelled)
private BukkitTask updateTask;

private void startAutoUpdate() {
    updateTask = Bukkit.getScheduler().runTaskTimer(
        CustomEnchantment.instance(), this::updatePlayerInfoSlots, 0, 5);
}

@Override
public void handleClose() {
    if (updateTask != null) updateTask.cancel();
    removeMenu(owner);
}
```

### Fix 2: Thread-Safe Collections

```java
// BEFORE
private static final Map<String, EquipmentCustomMenu> menuMap = new HashMap<>();
private static final Map<String, Long> swapSkinCooldowns = new HashMap<>();

// AFTER
private static final Map<String, EquipmentCustomMenu> menuMap = new ConcurrentHashMap<>();
private static final Map<String, Long> swapSkinCooldowns = new ConcurrentHashMap<>();
```

### Fix 3: Duplication Elimination

```java
// BEFORE: Two near-identical methods (~30 lines each)
// updateSlotsAdvance() — lines 481-507
// updateSlotsAdvanceForSlot() — lines 709-735

// AFTER: Single method with parameter
private void applySkinTemplate(EquipSlot slot, ItemStack displayItem) {
    // Unified logic here
}
```

## Lessons from Phase 5 (#30)

Reference: `.claude/docs/issues/30/`

| Lesson | Applicable? | How |
|--------|------------|-----|
| Multi-mode features need state diagrams | Yes | Document handler dispatch flow before coding |
| Resource operations need before/after tracking | Yes | Verify item add/return preserves inventory state |
| Mode Router Pattern | Partially | Handler selection is simpler than mode routing |
| State cleanup on transitions | Yes | Ensure handler switch doesn't leave stale state |

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Behavior regression in add/return logic | Medium | High | Manual test all equipment interactions |
| Handler dispatch wrong for edge cases | Low | Medium | Map all item types to handlers upfront |
| Thread safety fix causes deadlock | Low | High | ConcurrentHashMap is lock-free for reads |
| Task cancellation breaks auto-update | Low | Medium | Test `/reload` cycle with menu open |

## Notes

- The task leak and thread safety bugs were carried over from the legacy `EquipmentMenu` during #30 migration
- All other 5 menus fixed these issues during migration; Equipment is the only remaining one
- CEAnvil's handler pattern is proven — 14 handlers working cleanly in production
