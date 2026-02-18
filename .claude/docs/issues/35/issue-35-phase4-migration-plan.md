# Issue #35: Phase 4 - Book Upgrade & Artifact Upgrade Migration

## Parent Issue
**Parent:** #30 - CustomMenu Migration (Master Epic)

## Phase Information
**Current Phase:** Phase 4 of 8
**Previous Phases:**
- Phase 0 (#31): Migration Preparation - Completed
- Phase 1 (#32): Tinkerer Menu (Prototype) - Completed
- Phase 2 (#33): BookCraft Menu + FastCraft - Completed (7 iterations)
- Phase 3 (#34): CE Anvil Menu - Completed (3 iterations)

**Next Phases:**
- Phase 5 (#36): Equipment Menu
- Phase 6 (#37): Declarative YAML Menus (34 files)
- Phase 7 (#38): Cleanup + Finalization

---

## Context

Migrate two upgrade menus from legacy CustomMenu API to BafFramework CustomMenu system:
1. **Book Upgrade** - Complex two-workflow menu (XP accumulation + upgrade execution)
2. **Artifact Upgrade** - Medium-complexity single-workflow menu (ingredient collection + chance-based upgrade)

Both share similar "main item + ingredients" collection patterns. Legacy code remains frozen; new menus run in parallel via `-new` commands.

---

## Lessons Applied from Previous Phases

### From Phase 2 (#33 - BookCraft, 7 iterations)
| Lesson | Application |
|--------|-------------|
| State cleanup on mode switch | Clear `readyToUpgrade`, `randomXp`, `ingredients` on every state change |
| Validate results, not inputs | Collect all ingredients first, validate at end |
| Distinguish created vs original items | Ingredients consumed vs returned based on consolidation state |
| Pricing "what counts" rules | Requirements paid on both success AND failure |

### From Phase 3 (#34 - CE Anvil, 3 iterations)
| Lesson | Application |
|--------|-------------|
| Clone ItemStacks when storing | `itemStack.clone()` in all ExtraData setters |
| Template items (no-slot YAML) | `remind-xp-confirm`, `remind-book-confirm`, `remind-ingredient`, `remind-artifact-confirm` |
| Override `setupItems()` for templates | Skip items without `slot` property |
| Copy Vietnamese text, don't retype | Copy display names from legacy YAML exactly |
| Test state transitions exhaustively | Map every (state, action) -> (new state) combination |

### Process Improvement Target
- Phase 2: 7 iterations (learning)
- Phase 3: 3 iterations (applying lessons)
- **Phase 4 target: 1-2 iterations**

---

## Critical Constraint: DO NOT TOUCH Legacy Code

All legacy files remain **frozen**:

**Book Upgrade (frozen):**
- `menu/bookupgrade/BookUpgradeMenu.java`
- `menu/bookupgrade/BookUpgradeMenuListener.java`
- `menu/bookupgrade/BookUpgradeMenuOpener.java`
- `resources/menu/book-upgrade.yml`

**Artifact Upgrade (frozen):**
- `menu/artifactupgrade/ArtifactUpgradeMenu.java`
- `menu/artifactupgrade/ArtifactUpgradeMenuListener.java`
- `menu/artifactupgrade/ArtifactUpgradeMenuOpener.java`
- `resources/menu/artifact-upgrade.yml`

---

## Part A: Book Upgrade Menu Analysis

### Architecture Overview

Book Upgrade has TWO sequential workflows sharing state:
1. **XP Accumulation**: Add ingredient books -> consolidate XP into main book
2. **Upgrade Execution**: When XP >= required -> pay requirements -> roll chance -> success/fail

**Key classes (existing, reusable):**
- `BookUpgradeSettings` - Config: XP tables, requirements, success chances (already loaded by MenuModule)
- `BookUpgradeData` - Per-level data: `successChance`, `nextEnchant`, `requirementList`, `loseXpPercent`
- `BookUpgradeLevelData` - Wrapper: `Map<Integer, BookUpgradeData>`
- `XpGroup` / `RequiredXpGroup` - XP lookup tables
- `RandomRangeInt` - Min/max range for XP calculations

### State Machine

```
EMPTY
  | [player clicks enchant book in inventory]
  v
MAIN_BOOK_SELECTED (readyToUpgrade=false, no ingredients)
  | [player clicks ingredient books]
  v
ACCUMULATING_XP (readyToUpgrade=false, ingredients not empty)
  | [player clicks "remind-xp-confirm"]
  v  (ingredients consumed, XP added to main book)
XP_CONSOLIDATED
  | [if currentXp >= requiredXp]
  v
READY_TO_UPGRADE (readyToUpgrade=true)
  | [player clicks "remind-book-confirm"]
  |---> SUCCESS: result book given, state cleared -> EMPTY
  |---> FAIL: XP penalty, requirements paid -> MAIN_BOOK_SELECTED (reduced XP)
```

**State transitions requiring cleanup:**
| From | Action | To | Cleanup |
|------|--------|----|---------|
| ACCUMULATING | remove ingredient | ACCUMULATING/MAIN_BOOK | `ingredients.remove(i)`, `randomXp -= xp` |
| ANY | return main book | EMPTY | clear mainBook, ingredients, randomXp, readyToUpgrade |
| ACCUMULATING | confirm XP | XP_CONSOLIDATED | clear ingredients, reset randomXp, update mainBook XP |
| READY | upgrade SUCCESS | EMPTY | pay requirements, give result, clear all |
| READY | upgrade FAIL | MAIN_BOOK | pay requirements, XP penalty, clear readyToUpgrade |
| ANY | close menu | EMPTY | return mainBook + unconsumed ingredients |

### Resource Tracking

| Event | Main Book | Ingredients | Requirements | XP |
|-------|-----------|-------------|--------------|-----|
| Add ingredient | unchanged | +1 to list | not paid | += ingredientXp (range) |
| Remove ingredient | unchanged | -1 from list | not paid | -= ingredientXp (range) |
| XP consolidation | XP updated | **consumed** (cleared) | not paid | reset to 0 |
| Upgrade SUCCESS | **consumed** (replaced by result) | already consumed | **paid** | N/A |
| Upgrade FAIL | XP penalty applied | already consumed | **paid** | *= (100 - loseXpPercent) |
| Close menu | returned | returned (only if NOT consolidated) | not paid | N/A |

**Critical invariant:** `readyToUpgrade` flag determines whether ingredients are returned on close:
- `readyToUpgrade == false` -> ingredients still pending, RETURN them
- `readyToUpgrade == true` -> ingredients already consumed into XP, do NOT return

### Business Logic Flow

#### addBook(ItemStack, CEEnchantSimple)
```
1. Validate: isPerfectCopy (success=100%, destroy=0%) -> if not: NOT_PERFECT_BOOK
2. Validate: hasBookUpgradeData(enchantName, level) -> if not: NOT_UPGRADE_BOOK
3. If no mainBook yet:
   -> setMainBook(itemStack, ceEnchantSimple)
   -> return SUCCESS
4. If readyToUpgrade:
   -> return NOTHING (can't add more)
5. Get XP config for ingredient
   -> if null: NOT_XP_BOOK
6. Check enchant compatibility (same or whitelisted)
   -> if different: DIFFERENT_ENCHANT
7. addBookIngredient(itemStack, ceEnchantSimple)
   -> bookIngredients.add(new BookData(itemStack.clone(), ceEnchant))
   -> randomXp += ingredientXp
   -> return SUCCESS
```

#### confirmUpgrade()
```
Path 1: readyToUpgrade == false && hasIngredients
  -> XP Consolidation
  -> newXp = min(currentXp + randomXp.getValue(), requiredXp)
  -> mainBook.ceEnchantSimple.setXp(newXp)
  -> regenerate mainBook ItemStack
  -> clear bookIngredients, reset randomXp
  -> recalculate readyToUpgrade
  -> return SUCCESS_XP

Path 2: readyToUpgrade == true
  -> Check requirements (RequirementManager.checkFailedRequirement)
  -> if failed: return NOTHING
  -> Roll chance: successChance.work()
  -> if CEPlayer.isFullChance(): always succeed

  -> FAIL path:
     -> RequirementManager.payRequirements()
     -> XP penalty: newXp = currentXp * (100 - loseXpPercent) / 100
     -> Update mainBook, clear readyToUpgrade
     -> return FAIL_CHANCE

  -> SUCCESS path:
     -> RequirementManager.payRequirements()
     -> Create result book: CEAPI.getCEBookItemStack(nextEnchant)
     -> InventoryUtils.addItem(player, resultBook)
     -> Broadcast success
     -> Clear all state
     -> return SUCCESS
```

### Slot Layout (6 rows = 54 slots)

```
Row 0: o o o O O O o o o   (0-8)     borders
Row 1: o O O O b O O O o   (9-17)    b=book-upgrade (slot 12)
Row 2: o O P P P P P O o   (18-26)   P=ingredient-preview (slots 20-24)
Row 3: o O O O B O O O o   (27-35)   B=book-result (slot 31)
Row 4: o o o O a O o o o   (36-44)   a=remind/accept (slot 40)
Row 5: o o o o R o o o o   (45-53)   R=return (slot 49)
```

**Note:** Exact slot positions need verification from legacy YAML layout grid.

### Item Names Used in Legacy

| Item Name | Slot | Purpose | Dynamic? |
|-----------|------|---------|----------|
| `border` | grid `o` | Decorative | No |
| `border2` | grid `O` | Decorative | No |
| `book-upgrade` | `b` | Main book display + click to return | Yes - shows selected book |
| `book-result` | `B` | Preview result | Yes - shows upgrade result |
| `ingredient-preview` | `P` (multiple) | Show ingredients | Yes - per ingredient |
| `remind` | `a` | Default: "choose book" | Yes - swapped by state |
| `remind-xp` | `a` (template) | "Add XP books" | Template |
| `remind-xp-confirm` | `a` (template) | "Confirm XP consolidation" | Template |
| `remind-book-confirm` | `a` (template) | "Confirm upgrade" | Template |
| `accept` | `a` (priority -1) | Clickable confirm | Hidden by default |
| `return` | `R` | Go back / close | No |

---

## Part B: Artifact Upgrade Menu Analysis

### Architecture Overview

Artifact Upgrade has a SINGLE linear workflow:
1. Select artifact -> Add ingredients -> Accumulate points -> Confirm upgrade (chance-based)

**Key classes (existing, reusable):**
- `ArtifactUpgradeSettings` - Config: ingredient points, upgrade level data, maxIngredientCount
- `ArtifactUpgradeData` - Per-level data: `requiredPoint`, `requirementList`
- `ArtifactUpgradeLevelData` - Wrapper: `Map<Integer, ArtifactUpgradeData>`

### State Machine

```
EMPTY
  | [player clicks artifact in inventory]
  v
ARTIFACT_SELECTED (preview shows next level)
  | [player clicks ingredient items]
  v
INGREDIENTS_ADDED (totalPoint accumulating, chance % shown)
  | [player clicks confirm]
  |---> SUCCESS: upgraded artifact given -> EMPTY
  |---> FAIL: ingredients consumed, artifact returned -> EMPTY
```

**State transitions:**
| From | Action | To | Cleanup |
|------|--------|----|---------|
| EMPTY | add artifact | ARTIFACT_SELECTED | set artifact, create preview |
| ARTIFACT_SELECTED | add ingredient | INGREDIENTS_ADDED | ingredients.add(), totalPoint += point |
| INGREDIENTS_ADDED | remove ingredient | INGREDIENTS_ADDED/ARTIFACT_SELECTED | ingredients.remove(), totalPoint -= point |
| ANY | return artifact | EMPTY | clear all: artifact, preview, ingredients, totalPoint |
| INGREDIENTS_ADDED | confirm SUCCESS | EMPTY | pay requirements, give upgraded artifact, clear all |
| INGREDIENTS_ADDED | confirm FAIL | EMPTY | pay requirements, consume ingredients, return original artifact, clear all |
| ANY | close menu | EMPTY | return artifact + all ingredients |

### Resource Tracking

| Event | Artifact | Ingredients | Requirements |
|-------|----------|-------------|-------------|
| Add ingredient | unchanged | +1, totalPoint += point | not paid |
| Remove ingredient | unchanged | -1, totalPoint -= point | not paid |
| Upgrade SUCCESS | **consumed** (replaced by next level) | **consumed** | **paid** |
| Upgrade FAIL | **returned** (original) | **consumed** | **paid** |
| Close menu | returned | returned | not paid |

**Key difference from Book Upgrade:** On failure, artifact is returned but ingredients are consumed. Both success and failure clear ALL state.

### Business Logic Flow

#### addItem(ItemStack, CEItem)
```
1. Is CEArtifact?
   -> NO: if selectedArtifact exists, try as ingredient (see step 4)
   -> NO: if selectedArtifact null, return NOT_ARTIFACT
2. Is max level?
   -> YES: return MAX_LEVEL
3. setSelectedArtifact(itemStack, ceArtifact)
   -> Create preview: storage.getItemStackByParameter(group, nextLevel)
   -> return ADD_SELECTED_ARTIFACT

4. For ingredients:
   -> selectedArtifact must exist
   -> Get requiredPoint from settings.getRequiredPoint(ceItem)
   -> if requiredPoint <= 0: return NOT_INGREDIENT
   -> if ingredientItems.size() >= maxIngredientCount: return MAX_INGREDIENT
   -> addIngredient(ceItem, requiredPoint)
   -> totalPoint += requiredPoint
   -> return ADD_INGREDIENT
```

#### confirmUpgrade()
```
1. Validate: selectedArtifact exists -> if not: NO_SELECTED_ARTIFACT
2. Validate: ingredientItems not empty -> if empty: NO_INGREDIENT
3. Check requirements: RequirementManager.checkFailedRequirement()
   -> if failed: return NOTHING
4. Calculate chance: min(1.0, totalPoint / requiredPoint) * 100
   -> GUARD: if requiredPoint == 0, default to 100%
5. Roll chance OR check CEPlayer.isFullChance()

-> FAIL path:
   -> RequirementManager.payRequirements()
   -> clearIngredients() (consumed)
   -> returnItem(selectedArtifact) -> give original back to player
   -> clear all state
   -> return FAIL_CHANCE

-> SUCCESS path:
   -> RequirementManager.payRequirements()
   -> Give previewArtifact (next level) to player
   -> Broadcast success
   -> clear all state
   -> return SUCCESS
```

### Slot Layout (6 rows = 54 slots)

```
Row 0: o o o O O O o o o   (0-8)     borders
Row 1: o O O O b O O O o   (9-17)    b=selected-artifact (slot 12)
Row 2: o O P P P P P O o   (18-26)   P=ingredient-preview (slots 20-24, spiral: 22,23,21,24,20)
Row 3: o O O O B O O O o   (27-35)   B=preview-artifact (slot 31)
Row 4: o o o O a O o o o   (36-44)   a=remind/accept (slot 40)
Row 5: o o o o R o o o o   (45-53)   R=return (slot 49)
```

### Item Names Used in Legacy

| Item Name | Slot | Purpose | Dynamic? |
|-----------|------|---------|----------|
| `border` | grid `o` | Orange glass pane | No |
| `border2` | grid `O` | Yellow glass pane | No |
| `selected-artifact` | `b` | Show selected artifact | Yes |
| `preview-artifact` | `B` | Show next-level preview | Yes |
| `ingredient-preview` | `P` (5 slots, spiral) | Show collected ingredients | Yes |
| `remind` | `a` | Default: "choose artifact" | Yes |
| `remind-ingredient` | `a` (template) | "Add ingredients" | Template |
| `remind-artifact-confirm` | `a` (template) | "Confirm upgrade" with chance% | Template |
| `accept` | `a` (priority -1) | Clickable confirm | Hidden |
| `return` | `R` | Go back / close | No |

---

## Part C: Implementation Plan

### File Structure

```
src/main/java/com/bafmc/customenchantment/

menu/bookupgrade/                          (NEW alongside legacy)
  BookUpgradeCustomMenu.java               AbstractMenu<MenuData, BookUpgradeExtraData>
  BookUpgradeExtraData.java                ExtraData with BookData, ingredients, xp, flags
  item/
    BookUpgradeSlotItem.java               Click -> return main book
    BookUpgradeResultItem.java             Display only: preview/result
    IngredientPreviewItem.java             Click -> remove specific ingredient
    BookUpgradeRemindItem.java             Click -> confirm upgrade/xp

menu/artifactupgrade/                      (NEW alongside legacy)
  ArtifactUpgradeCustomMenu.java           AbstractMenu<MenuData, ArtifactUpgradeExtraData>
  ArtifactUpgradeExtraData.java            ExtraData with artifact, ingredients, totalPoint
  item/
    SelectedArtifactItem.java              Click -> return artifact
    PreviewArtifactItem.java               Display only
    ArtifactIngredientPreviewItem.java     Click -> remove ingredient
    ArtifactRemindItem.java                Click -> confirm upgrade

command/
  BookUpgradeCommand.java                  AdvancedCommandExecutor -> MenuOpener
  ArtifactUpgradeCommand.java              AdvancedCommandExecutor -> MenuOpener

resources/menu-new/
  book-upgrade.yml                         BafFramework format
  artifact-upgrade.yml                     BafFramework format
```

### Implementation Order

**Step 1: Book Upgrade (complex first)**
1. `BookUpgradeExtraData.java` - data class
2. `BookUpgradeCustomMenu.java` - core menu (largest file)
3. Item classes (4 files)
4. `book-upgrade.yml` - YAML
5. `BookUpgradeCommand.java` - command

**Step 2: Artifact Upgrade (simpler, reuse patterns)**
6. `ArtifactUpgradeExtraData.java` - data class
7. `ArtifactUpgradeCustomMenu.java` - core menu
8. Item classes (4 files)
9. `artifact-upgrade.yml` - YAML
10. `ArtifactUpgradeCommand.java` - command

**Step 3: Registration (append only)**
11. `plugin.yml` - add 2 command entries
12. `CommandModule.java` - register 2 commands
13. `MenuModule.java` - register 2 menu strategies

### Key Patterns to Follow

**ExtraData pattern** (from Phase 1-3):
```java
@Getter @Setter
public class BookUpgradeExtraData extends ExtraData {
    private BookData mainBook;                        // Main enchant book
    private List<BookData> bookIngredients = new ArrayList<>();  // XP ingredients
    private RandomRangeInt randomXp = new RandomRangeInt(0);     // Accumulated XP range
    private boolean readyToUpgrade = false;           // Controls workflow path
}
```

**Command pattern** (from Phase 1-3):
```java
public class BookUpgradeCommand implements AdvancedCommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Argument arg) {
        Player player = (Player) sender;
        BookUpgradeExtraData extraData = new BookUpgradeExtraData();
        MenuOpener.builder()
            .player(player)
            .menuData(plugin, BookUpgradeCustomMenu.MENU_NAME)
            .extraData(extraData)
            .async(false)
            .build();
        return true;
    }
}
```

**handlePlayerInventoryClick pattern** (from Phase 1-3):
```java
@Override
public void handlePlayerInventoryClick(ClickData data) {
    ItemStack clicked = data.getEvent().getCurrentItem();
    if (clicked == null || clicked.getType().isAir()) return;

    // Clone with amount=1 to prevent taking whole stack
    ItemStack singleItem = clicked.clone();
    singleItem.setAmount(1);

    // Validate and add
    ReasonEnum reason = addItem(singleItem, ceItem);

    // Update original stack on success
    if (reason == SUCCESS) {
        int newAmount = clicked.getAmount() - 1;
        if (newAmount <= 0) data.getEvent().setCurrentItem(null);
        else clicked.setAmount(newAmount);
    }
}
```

**handleClose pattern** (CRITICAL):
```java
@Override
public void handleClose() {
    returnAllItems();  // ALWAYS return items on close
}
```

---

## Risk Assessment

| Risk | Severity | Menu | Mitigation |
|------|----------|------|------------|
| Stale `readyToUpgrade` flag | HIGH | Book Upgrade | Recalculate in EVERY `updateMenu()` call |
| Division by zero (requiredPoint=0) | HIGH | Artifact | Guard check: `if (requiredPoint <= 0) chance = 100%` |
| ItemStack reference invalidation | HIGH | Both | Clone ALL ItemStacks when storing in ExtraData |
| Template items crash `setupItems()` | MEDIUM | Both | Override `setupItems()`, skip items without slots |
| Vietnamese diacritics lost | MEDIUM | Both | Copy from legacy YAML, do NOT retype |
| Ingredients not returned on close | HIGH | Both | `handleClose()` ALWAYS calls `returnAllItems()` |
| Amount>1 taking whole stack | HIGH | Both | Clone with `amount=1`, update original |
| Ingredients returned after consolidation | HIGH | Book Upgrade | Check `readyToUpgrade` in `returnAllItems()` |

---

## Verification Plan

### Build
```bash
./gradlew build    # Must compile cleanly
./gradlew test     # All existing tests must pass
```

### Manual Testing - Book Upgrade (`/bookupgrade-new`)
- [ ] Menu opens with correct layout and Vietnamese text
- [ ] Add main book -> shows in slot, preview updates
- [ ] Add ingredient books -> show in preview slots (center-outward)
- [ ] Remove ingredient (click preview) -> returns to inventory, XP recalculates
- [ ] Confirm XP consolidation -> ingredients consumed, main book XP updates
- [ ] When XP >= required -> readyToUpgrade, confirm button changes
- [ ] Confirm upgrade SUCCESS -> result book given, broadcast, state cleared
- [ ] Confirm upgrade FAIL -> XP penalty, requirements paid, book kept
- [ ] Return main book -> all items returned, state cleared
- [ ] Close menu -> all items returned safely
- [ ] Non-upgrade book rejected with message
- [ ] Item with amount>1 -> takes only 1
- [ ] Legacy `/bookupgrade` still works unchanged

### Manual Testing - Artifact Upgrade (`/artifactupgrade-new`)
- [ ] Menu opens with correct layout and Vietnamese text
- [ ] Add artifact -> shows in slot, preview shows next level
- [ ] Max-level artifact rejected
- [ ] Add ingredients -> show in spiral preview slots, chance% updates
- [ ] Remove ingredient -> returns to inventory, chance recalculates
- [ ] Confirm upgrade SUCCESS -> upgraded artifact given, broadcast
- [ ] Confirm upgrade FAIL -> ingredients consumed, artifact returned, requirements paid
- [ ] Close menu -> all items returned safely
- [ ] Non-ingredient item rejected
- [ ] Max ingredient count enforced
- [ ] Player with "full chance" buff -> always succeeds
- [ ] Legacy `/artifactupgrade` still works unchanged

---

## Documentation References

- Phase 2 lessons: `.claude/docs/issues/30/LESSONS_FOR_NEXT_PHASE.md`
- Phase 2 engineering: `.claude/docs/issues/30/ENGINEERING_LESSONS.md`
- Phase 3 retrospective: `.claude/docs/issues/34/PHASE3_RETROSPECTIVE.md`
- Phase 3 architecture: `.claude/docs/issues/34/ARCHITECTURE_COMPARISON.md`
- Command migration: `.claude/docs/COMMAND_MIGRATION.md`
- CustomMenu API: `.claude/docs/custommenu/`
