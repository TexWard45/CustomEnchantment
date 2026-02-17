# Phase 3 Retrospective: CE Anvil Menu Migration

## Summary

Phase 3 migrated the CE Anvil menu — the most complex menu in the entire plugin — from the legacy CustomMenu API to the BafFramework CustomMenu system. The CE Anvil has **13 different Slot2 view handlers**, pagination, preview slots with spiral ordering, dynamic confirm buttons, and a state machine managing slot1/slot2 item interactions.

**Outcome:** Successful migration after encountering and fixing 3 bugs during in-game testing. All 12 handler types + DefaultHandler work correctly alongside the legacy `/ceanvil` command.

---

## Architecture Overview

### What Was Built

```
menu/ceanvil/
├── CEAnvilCustomMenu.java       # Main menu (AbstractMenu subclass)
├── CEAnvilExtraData.java        # State container (activeHandler, itemData1, itemData2)
├── CEAnvilSettings.java         # YAML-driven slot/preview mapping
├── AnvilItemData.java           # Item + CEItem wrapper
├── handler/
│   ├── Slot2Handler.java        # Interface (isSuitable, updateView, apply, etc.)
│   ├── DefaultHandler.java      # No slot2 → shows removable enchants
│   ├── BookHandler.java         # Enchant book application
│   ├── EnchantPointHandler.java # Enchant point application
│   ├── GemHandler.java          # Gem application
│   ├── GemDrillHandler.java     # Gem slot drilling
│   ├── ProtectDeadHandler.java  # Death protection scroll
│   ├── ProtectDestroyHandler.java # Destroy protection scroll
│   ├── LoreFormatHandler.java   # Lore formatting
│   ├── EraseEnchantHandler.java # Enchant erasure
│   ├── RemoveEnchantHandler.java     # Remove specific enchant (list)
│   ├── RemoveEnchantPointHandler.java # Remove enchant point (list)
│   ├── RemoveGemHandler.java          # Remove gem (list)
│   ├── RemoveProtectDeadHandler.java  # Remove death protection (list)
│   └── AbstractListHandler.java       # Base for paginated list handlers
├── item/
│   ├── AnvilSlotItem.java       # Handles slot1/slot2 clicks → returnItem
│   ├── AnvilConfirmItem.java    # Handles confirm click → menu.confirm()
│   ├── AnvilPreviewItem.java    # Handles preview clicks → handler.clickProcess()
│   └── AnvilPageItem.java       # Handles page navigation → handler.clickProcess()
```

### Key Design Decisions

1. **Handler Strategy Pattern** — Each Slot2 item type maps to a `Slot2Handler` implementation via `HashMap<String, Supplier<Slot2Handler>>`. Handlers are created per-player via supplier (prototype pattern) to avoid state sharing.

2. **Template Items Without Slots** — YAML items like `confirm-book`, `confirm-gem`, `has-next-page` have NO `slot:` property. They exist purely as ItemStack templates fetched by handlers via `menu.getTemplateItemStack("confirm-book")`.

3. **setupItems() Override** — The default `super.setupItems()` crashes on items without slots (NPE on `getSlots()`). We override to skip slot-less template items.

4. **CEAnvilSettings** — Parses slot mappings and default ItemStacks at menu open time, cached for the session. Handlers use this to update slots by name without knowing slot numbers.

5. **AbstractListHandler<L>** — Template method pattern for the 4 paginated handlers (RemoveEnchant, RemoveEnchantPoint, RemoveGem, RemoveProtectDead). Subclasses implement `getList()`, `getDisplayItem()`, `getApplyReason()`, `getConfirmTemplateName()`.

---

## Bugs Encountered & Fixed

### Bug 1: Slot2-First Item Ordering

**Symptom:** When a player adds a slot2 item (e.g., enchant book) BEFORE adding a slot1 item (weapon), the menu sometimes showed stale data or incorrect CEItem references.

**Root Cause:** `AnvilItemData` stored the raw `ItemStack` reference from the player's inventory. When the player's inventory changed (e.g., the item was removed from their inventory slot), the reference could become stale.

**Fix:** Clone the ItemStack in `AnvilItemData` constructor + add null guard for `ceItem`:
```java
public AnvilItemData(ItemStack itemStack, CEItem ceItem) {
    this.itemStack = itemStack.clone();  // Clone to prevent reference issues
    this.ceItem = ceItem;
}
```

**Lesson:** Always clone ItemStacks when storing them outside their original context. Bukkit ItemStack references can become invalid when the source inventory changes.

---

### Bug 2: Handler Lost on Slot1 Return-and-Re-Add

**Symptom:** If a player had both slot1 (weapon) and slot2 (book) filled, then returned slot1 and re-added a different weapon, the handler was lost — the menu showed no preview or confirm button.

**Root Cause:** The `returnItem("slot1")` method was nulling the handler regardless of whether slot2 still had an item:
```java
// BEFORE (broken):
if ("slot1".equals(name) && extraData.getItemData1() != null) {
    extraData.setItemData1(null);
    extraData.setActiveHandler(null);  // Always cleared!
}
```

When slot1 was re-added via `addItem()`, the handler factory check only runs for slot2 items. Since slot2 already had an item, no new handler was created.

**Fix:** Preserve the handler when slot2 still has an item:
```java
// AFTER (fixed):
if ("slot1".equals(name) && extraData.getItemData1() != null) {
    extraData.setItemData1(null);
    // Only null handler if slot2 is also empty
    if (extraData.getItemData2() == null) {
        extraData.setActiveHandler(null);
    }
}
```

**Lesson:** State transitions must consider ALL related state, not just the directly affected field. When clearing slot1, the handler state depends on slot2's state too.

---

### Bug 3: RemoveEnchantPoint Off-by-One Validation

**Symptom:** With 6 enchant books (using 6 enchant points) and 2 extra enchant points (total capacity = 7), attempting to remove 1 enchant point was blocked with "remove book first" — even though after removal, 6 remaining points exactly fit 6 books.

**Root Cause:** Off-by-one comparison in `CERemoveEnchantPoint.applyByMenuTo()`:
```java
// BEFORE (broken):
int maxEnchantSlot = basePoints + extraPoints;  // 5 + 2 = 7
if (ceWeapon.getWeaponEnchant().getTotalEnchantPoint() >= maxEnchantSlot - 1) {
    // 6 >= 6 → TRUE → blocked!  (should allow exact fit)
    return new ApplyReason("remove-book-first", ApplyResult.CANCEL);
}
```

**Fix:** Changed `>=` to `>`:
```java
// AFTER (fixed):
if (ceWeapon.getWeaponEnchant().getTotalEnchantPoint() > maxEnchantSlot - 1) {
    // 6 > 6 → FALSE → allowed (exact fit is OK)
    return new ApplyReason("remove-book-first", ApplyResult.CANCEL);
}
```

**Note:** This was a **pre-existing bug** in `CERemoveEnchantPoint.java`, not introduced by the migration. It was consistent with the legacy code but logically incorrect. The fix aligns with `WeaponEnchant.isEnoughEnchantPointForNextCE()` which uses `<=` to allow exact fill when adding books.

**Lesson:** Migration is a great opportunity to discover and fix pre-existing bugs. When the legacy code and new code are identical but behavior seems wrong, investigate the business logic itself.

---

## Bonus Fix: Vietnamese Diacritics in ce-anvil.yml

The new YAML config `menu-new/ce-anvil.yml` was initially created without Vietnamese accent marks (e.g., "Lo ren" instead of "Lò rèn", "Xem truoc" instead of "Xem trước"). Fixed all text to match the legacy `menu/ce-anvil.yml` exactly.

**Lesson:** When creating new config files based on legacy, copy the text content exactly rather than retyping it. UTF-8 diacritics are easily lost when typed from memory.

---

## What Went Well

### 1. Handler Strategy Pattern Was the Right Choice

The `Slot2Handler` interface cleanly separates the 13 different item type behaviors. Each handler is self-contained and testable. Adding a new item type in the future requires only:
- Create a `NewHandler implements Slot2Handler`
- Register it: `CEAnvilCustomMenu.registerHandler(CEItemType.NEW, NewHandler::new)`

### 2. Template Items (No-Slot Items) Solved the Dynamic Confirm Problem

The legacy system had 13 `confirm-*` items all mapped to the same slot with `priority: -1`. Instead of replicating this complex priority system, we simply define them as slot-less YAML entries and fetch them as ItemStack templates:
```java
ItemStack confirmItem = menu.getTemplateItemStack("confirm-book");
menu.updateSlots("confirm", confirmItem);
```

This is simpler, more explicit, and doesn't require the framework to support priority.

### 3. AbstractListHandler Eliminated Code Duplication

The 4 list-based handlers (RemoveEnchant, RemoveEnchantPoint, RemoveGem, RemoveProtectDead) share ~90% of their logic: pagination, preview display, selection highlighting, page indicators. `AbstractListHandler<L>` extracts all of this, and subclasses only implement 4 methods:
- `getList()` → what to display
- `getDisplayItem()` → how to display each item
- `getApplyReason()` → what happens on confirm
- `getConfirmTemplateName()` → which confirm template to use

### 4. CEAnvilSettings Decoupled Slot Numbers from Code

Handlers never reference slot numbers directly. They call `menu.updateSlots("preview3", itemStack)` and `CEAnvilSettings` resolves "preview3" to the actual slot number from YAML. This means the menu layout can be changed in YAML without touching any Java code.

### 5. Legacy Coexistence Worked Perfectly

Both `/ceanvil` (legacy) and `/ceanvil-new` (new) run simultaneously. This enabled safe in-game testing against the production-ready legacy version.

---

## What Could Be Improved

### 1. More Upfront Testing of State Transitions

Bugs 1 and 2 were both state management issues. Creating a state transition test matrix before coding would have caught them:

| Initial State | Action | Expected Handler | Expected slot1 | Expected slot2 |
|---|---|---|---|---|
| EMPTY | add weapon | DefaultHandler | set | null |
| SLOT1_ONLY | add book | BookHandler | set | set |
| BOTH_SLOTS | return slot1 | BookHandler (preserved) | null | set |
| SLOT2_ONLY | add weapon | BookHandler (existing) | set | set |
| BOTH_SLOTS | return slot2 | DefaultHandler (reverted) | set | null |

### 2. The setupItems() Override Is a Framework Workaround

Having to override `setupItems()` to skip slot-less items is a workaround for a framework limitation. Ideally, the framework should handle items without slots gracefully. This should be reported upstream.

### 3. Vietnamese Text Should Be Copied, Not Retyped

The missing diacritics issue was entirely preventable by copying the legacy YAML content instead of retyping.

---

## Lessons Applied from Phase 2

From Phase 2 (Issue #30 - BookCraft + FastCraft):

- **State cleanup on transitions** — Applied: handler preservation/cleanup logic in `returnItem()` considers both slot states
- **Mode Router Pattern** — Applied: handler switching follows the pattern of cleaning old handler's previews before setting new handler
- **ItemStack cloning** — Applied (after Bug 1): always clone ItemStacks when storing outside original context

---

## Lessons for Future Phases

### For Phase 4+ Menu Migrations

1. **Template items are powerful** — Use YAML items without `slot:` as fetchable templates for any dynamic content. Override `setupItems()` to skip them.

2. **Handler strategy + supplier = clean state** — Register handlers as `Supplier<Handler>` to create fresh instances per player. Never share handler state between menu instances.

3. **Test state transitions exhaustively** — Map every (state, action) → (new state) combination. Don't assume symmetric transitions (add/remove) are mirrors of each other.

4. **Pre-existing bugs are opportunities** — Migration is the perfect time to discover bugs hidden in legacy code. When new code is identical to legacy but behavior seems wrong, investigate the business logic.

5. **Copy text content, don't retype** — Especially for Vietnamese/Unicode text. Use the legacy file as the source of truth.

---

## Files Modified

### New Files Created
- `src/main/java/com/bafmc/customenchantment/menu/ceanvil/` (entire package, 22 files)
- `src/main/java/com/bafmc/customenchantment/command/CEAnvilCommand.java`
- `src/main/resources/menu-new/ce-anvil.yml`

### Existing Files Modified
- `src/main/java/com/bafmc/customenchantment/menu/MenuModule.java` — Added CEAnvilCustomMenu + handler registration
- `src/main/java/com/bafmc/customenchantment/command/CommandModule.java` — Added `/ceanvil-new` command
- `src/main/resources/plugin.yml` — Added `ceanvil-new` command entry
- `src/main/java/com/bafmc/customenchantment/item/removeenchantpoint/CERemoveEnchantPoint.java` — Fixed off-by-one bug (line 66: `>=` → `>`)

### Legacy Files Untouched
All 13 legacy `Slot2CE*View.java` files, `CEAnvilMenu.java`, `CEAnvilMenuListener.java`, and `menu/ce-anvil.yml` remain completely untouched. Both old and new systems coexist.

---

## Metrics

| Metric | Value |
|---|---|
| New Java files | 22 |
| New YAML files | 1 |
| Modified files | 4 |
| Handler types | 13 (12 + DefaultHandler) |
| Bugs found in testing | 3 |
| Pre-existing bugs fixed | 1 (RemoveEnchantPoint) |
| Iterations to fix bugs | 1 each (clear root cause) |
| Phase 2 iterations | 7 (complex, learning) |
| Phase 3 iterations | 3 (applying lessons) |
