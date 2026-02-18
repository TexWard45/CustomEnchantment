# Phase 4 Retrospective: Book Upgrade & Artifact Upgrade Migration

## Summary

Phase 4 migrated two menus — **Book Upgrade** (complex 2-phase state machine) and **Artifact Upgrade** (simpler linear workflow) — from the legacy CustomMenu API to BafFramework CustomMenu. Both share the "main item + ingredients" pattern.

**Outcome:** Successful migration with 4 bugs found during testing, all fixed in 1 iteration each. Significant improvement over previous phases.

---

## Cross-Phase Improvement Metrics

| Phase | Menus | Complexity | Bugs | Iterations | Improvement |
|-------|-------|------------|------|------------|-------------|
| 1 (Tinkerer) | 1 | Simple | 0 | 2 | Baseline |
| 2 (BookCraft) | 1 | Complex | 7 | 7 | Learning phase |
| 3 (CE Anvil) | 1 | Very Complex | 3 | 3 | 57% fewer bugs |
| **4 (BookUpgrade + ArtifactUpgrade)** | **2** | **Complex + Medium** | **4** | **1** | **2 menus in 1 iteration** |

**Key achievement:** Phase 4 delivered 2 menus with fewer bugs than Phase 3's single menu (4 vs 3), and ALL bugs were fixed in 1 iteration each (no cascading regressions). The bugs were also qualitatively different — no state management bugs (previously the biggest problem), only display and calculation issues.

---

## Bugs Encountered & Fixed

### Bug 1: Item Duplication in returnMainBook() (CRITICAL — found in code review)

**Symptom:** Potential for duplicating consumed ingredient books when returning the main book after a failed upgrade.

**Root Cause:** `BookUpgradeExtraData.clearMainBook()` was resetting BOTH `mainBook = null` AND `readyToUpgrade = false`. Then `returnMainBook()` checked `!isReadyToUpgrade()` (which was always true after `clearMainBook()`), causing it to return already-consumed ingredients.

**Fix:** Separated concerns — `clearMainBook()` only nulls `mainBook`. The `readyToUpgrade` flag is explicitly set to `false` AFTER the guard check in `returnMainBook()` and in the SUCCESS path of `confirmUpgrade()`.

**Category:** State management (same category as Phase 2-3 bugs)
**Caught by:** Code review agent, NOT manual testing
**Lesson:** Even with all previous lessons applied, state cleanup ordering still needs careful attention. Code review catches what manual testing might miss.

---

### Bug 2: Display Slots Disappear (book-craft.yml + book-upgrade.yml + artifact-upgrade.yml)

**Symptom:** In book-craft, when the menu opens empty, book1/book2/preview slots show as empty air instead of their default YAML display items (LIGHT_GRAY_STAINED_GLASS_PANE). Same issue in book-upgrade with ingredient-preview slots.

**Root Cause:** `inventory.setItem(slot, null)` sets the slot to empty AIR. But the intent was to "restore default appearance." The `super.setupItems()` places YAML-configured items, then `updateMenu()` immediately overwrites them with null because no books/ingredients are present.

**Fix:** Replace all `inventory.setItem(slot, null)` with `inventory.setItem(slot, getTemplateItemStack("itemName"))` which restores the YAML-configured default display.

**Affected files:**
- `BookCraftCustomMenu.java` — 6 occurrences (book1, book2, preview slots)
- `BookUpgradeCustomMenu.java` — 2 occurrences (ingredient-preview slots)
- `ArtifactUpgradeCustomMenu.java` — 1 occurrence (ingredient-preview slots)

**Category:** NEW category — Display restoration
**Lesson:** **`inventory.setItem(slot, null)` ≠ "restore default".** Always use `getTemplateItemStack(name)` to restore YAML defaults. This is a recurring pattern that affected 3 files across 2 phases.

---

### Bug 3: loseXpPercent Applied as Flat Subtraction Instead of Percentage

**Symptom:** When a book upgrade fails, the XP penalty was too harsh. With `loseXpPercent=30` and `currentXp=80`, the result was `80 - 30 = 50` instead of `80 - (80 * 30/100) = 56`.

**Root Cause:** The code did `xp - loseXpPercent.getValue()` (flat subtraction) instead of `xp - (xp * loseXpPercent / 100)` (percentage calculation). The field name clearly says "percent" but the math treated it as a flat value.

**Fix:**
```java
int currentXp = extraData.getMainBook().getCESimple().getXp();
int losePercent = bookUpgradeData.getLoseXpPercent().getValue();
int xpToLose = (int) Math.floor(currentXp * losePercent / 100.0);
extraData.getMainBook().getCESimple().setXp(Math.max(0, currentXp - xpToLose));
```

**Note:** The legacy code had the same implementation. This is a **pre-existing logic issue** that was noticed during Phase 4 migration testing.

**Category:** Business logic / math error
**Lesson:** When a variable/field name says "percent", verify the math actually treats it as a percentage. Don't blindly copy legacy math.

---

### Bug 4: Missing Sound Configuration Across All Menus

**Symptom:** No open sound played when any new menu was opened.

**Root Cause:** Mixed issues:
- `tinkerer.yml` and `book-craft.yml` — missing sound entirely
- `ce-anvil.yml`, `book-upgrade.yml`, `artifact-upgrade.yml` — sound was under `data:` section (legacy format) instead of top-level `sound:` section (BafFramework format)

**Fix:** Added/moved to correct BafFramework format in all 5 menu YAML files:
```yaml
sound:
  open: 'ENTITY_EXPERIENCE_ORB_PICKUP'
```

**Category:** Configuration format
**Lesson:** When migrating YAML configs, verify that ALL framework features (not just the obvious ones) use the correct new format. Sound was easy to miss because it's a "nice to have" that doesn't cause errors.

---

## Lessons Applied from Previous Phases

### From Phase 2 (BookCraft — 7 bugs)

| Lesson | Applied? | Evidence |
|--------|----------|----------|
| Clone ItemStacks when storing | Yes | `clickedItem.clone()` in all ExtraData setters |
| State cleanup on every transition | Yes | Separate clearMainBook/clearIngredients/resetXp |
| Validate results, not inputs | Yes | Collect ingredients first, validate at confirm |
| Test state transitions | Partially | Bug 1 was still a state ordering issue |

### From Phase 3 (CE Anvil — 3 bugs)

| Lesson | Applied? | Evidence |
|--------|----------|----------|
| Template items (no-slot YAML) | Yes | remind-xp, remind-xp-confirm, remind-book-confirm |
| Override setupItems() for templates | Not needed | Framework handles it now |
| Copy Vietnamese text exactly | Yes | All YAML text copied from legacy |
| inventory.setItem() not reopenInventory() | Yes | All updates via updateMenu() |

---

## New Lessons for Future Phases

### 1. The `inventory.setItem(slot, null)` Anti-Pattern

**Pattern name:** Display Restoration Anti-Pattern

**Problem:** Using `inventory.setItem(slot, null)` to "clear" a slot actually sets it to AIR (empty), wiping the YAML default display. This makes the menu look broken when slots are empty.

**Solution:** Always use `getTemplateItemStack(name)` to restore defaults:
```java
// BAD: Sets slot to air (empty)
inventory.setItem(slot, null);

// GOOD: Restores YAML default display
inventory.setItem(slot, getTemplateItemStack("ingredient-preview"));

// ALSO GOOD: updateSlots handles restoration
updateSlots("ingredient-preview", null);  // null may restore default (check framework behavior)
```

**When to apply:** Every time you "clear" a slot that has a YAML default appearance. This applies to ALL menu migrations going forward.

**Checklist for new menus:**
- [ ] Search for ALL `inventory.setItem(slot, null)` calls
- [ ] For each one, ask: "Does this slot have a YAML default display?"
- [ ] If yes, replace with `getTemplateItemStack()` restoration

---

### 2. Verify Field Name Semantics Match Implementation

**Pattern name:** Name-Implementation Mismatch

**Problem:** A field named `loseXpPercent` was used as a flat value subtraction instead of a percentage. This went unnoticed in legacy code.

**Solution:** When migrating code that involves math or named values:
1. Read the field/variable name carefully
2. Check if the math matches the name's semantics
3. Don't assume legacy code is correct — verify business logic

---

### 3. Sound and Other "Secondary" YAML Features

**Pattern name:** Incomplete Feature Migration

**Problem:** Sound configuration was missing from Phase 1-2 menus and in wrong format for Phase 3-4. Easy to miss because it doesn't cause errors.

**Solution:** Use a YAML feature checklist when creating new menu files:
- [ ] `type:` — menu type
- [ ] `title:` — with Vietnamese text copied exactly
- [ ] `row:` — number of rows
- [ ] `sound: open:` — open sound (check legacy YAML)
- [ ] `layout:` — character grid
- [ ] `items:` — all interactive + decorative items
- [ ] Template items (no slot) — for dynamic states

---

### 4. Code Review Catches Critical Bugs That Testing Misses

Bug 1 (item duplication) was caught by the code review agent, not by manual testing. The bug would only manifest in a specific sequence: add main book → add ingredients → consolidate XP → upgrade fails → return main book. A code review examining state cleanup ordering caught it immediately.

**Recommendation:** Always run code review after implementation, before manual testing.

---

## What Went Well

### 1. Two Menus Migrated in One Session

Previous phases each handled one menu. Phase 4 delivered both Book Upgrade and Artifact Upgrade together, leveraging established patterns.

### 2. Pattern Reuse Was Effective

The "main item + ingredients" pattern was applied consistently:
- ExtraData holds state (main item, ingredient list, flags)
- updateMenu() renders all slots based on state
- handlePlayerInventoryClick() validates and routes
- handleClose() returns all items
- Template items for dynamic confirm buttons

### 3. No State Management Regressions

Phase 2 had 7 state-related bugs. Phase 3 had 2. Phase 4 had 1 (caught in code review, not testing). The trend shows effective learning.

### 4. Clean Separation of Concerns

Book Upgrade's 2-phase state machine (XP consolidation → upgrade) is cleanly expressed in `confirmUpgrade()` with two distinct if-blocks, rather than a confusing nested switch.

---

## What Could Be Improved

### 1. Display Restoration Should Be a Standard Pattern

The `inventory.setItem(slot, null)` bug affected 3 menus across 2 phases. This should be documented as a mandatory pattern in the custommenu docs, and future menus should use a helper method:
```java
// Proposed helper in AbstractMenu or base class:
protected void clearSlot(String itemName) {
    inventory.setItem(getSlotByName(itemName), getTemplateItemStack(itemName));
}
```

### 2. YAML Feature Completeness Checklist

Sound was missed in Phase 1-2 menus and incorrectly formatted in Phase 3-4. A standardized YAML template/checklist would prevent this.

### 3. Legacy Business Logic Verification

The `loseXpPercent` bug was pre-existing in legacy code. During migration, all math operations should be verified against field name semantics, not just copied verbatim.

### 4. Automated Testing Would Catch More

All 4 bugs were caught by either code review or manual testing. Unit tests for state transitions and display updates would catch these earlier and prevent regressions.

---

## Bug Category Analysis Across All Phases

| Category | Phase 2 | Phase 3 | Phase 4 | Trend |
|----------|---------|---------|---------|-------|
| State management | 5 | 2 | 1 (code review) | Improving |
| Resource tracking | 2 | 0 | 0 | Resolved |
| Display/rendering | 0 | 0 | 2 (NEW) | New category |
| Business logic | 0 | 1 (pre-existing) | 1 (pre-existing) | Legacy issues |
| Configuration | 0 | 1 (Vietnamese text) | 1 (sound format) | Recurring |
| **Total** | **7** | **3** | **4** | **Improving** |

**Observations:**
- State management bugs are converging to zero (5 → 2 → 1)
- Display restoration is a NEW bug category unique to the BafFramework migration
- Legacy pre-existing bugs are discovered during migration (opportunity, not regression)
- Configuration issues (text, sound) are minor but recurring

---

## Recommendations for Phase 5+

1. **Add `inventory.setItem(slot, null)` to code review checklist** — Flag every usage and verify if `getTemplateItemStack()` should be used instead
2. **Create a YAML template file** — Standard boilerplate for new menu YAMLs with all required sections (type, title, row, sound, layout, items)
3. **Verify legacy math during migration** — Don't copy arithmetic blindly; check if field names match the actual calculation
4. **Run code review agent before manual testing** — Bug 1 proves code review catches critical issues that testing might miss in rare edge cases
5. **Consider helper methods for common patterns** — `clearSlot(name)`, `restoreDefault(name)` to prevent the display restoration anti-pattern
