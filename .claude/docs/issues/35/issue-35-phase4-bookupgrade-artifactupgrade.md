# Issue #35: CustomMenu Migration - Phase 4 (Book Upgrade & Artifact Upgrade)

## Overview

Phase 4 of the CustomMenu migration epic (#30). Migrated **Book Upgrade** and **Artifact Upgrade** menus from legacy CustomMenu API to BafFramework CustomMenu system. Both menus share similar "main item + ingredients" patterns.

**Branch:** `30-feat-custommenu-migration`
**Status:** Completed
**Bugs found during testing:** 4 (all fixed in 1 iteration each)
**New files:** 16 (14 Java + 2 YAML)
**Modified files:** 6

---

## Documentation Index

### [PHASE4_RETROSPECTIVE.md](./PHASE4_RETROSPECTIVE.md)
**Complete analysis of Phase 4 implementation, bugs, and improvements.**

**Contents:**
- 4 bugs found and fixed
- Cross-phase improvement metrics
- Lessons applied from Phase 1-3
- New lessons for Phase 5+
- Recurring pattern analysis (`inventory.setItem(slot, null)` anti-pattern)

**Key Insight:** Only 4 bugs vs Phase 2's 7 and Phase 3's 3. Lessons from previous phases were effectively applied, especially ItemStack cloning and state cleanup. The remaining bugs were a new category: **display restoration** (`null` vs `getTemplateItemStack()`).

---

## Previous Phases

| Phase | Issue | Menus | Iterations | Key Learning |
|-------|-------|-------|------------|-------------|
| 1 | #32 | Tinkerer | 2 | BafFramework basics |
| 2 | #30 | BookCraft + FastCraft | 7 | State machines, resource tracking |
| 3 | #34 | CE Anvil | 3 | Handler pattern, template items |
| **4** | **#35** | **Book Upgrade + Artifact Upgrade** | **1** | **Display restoration, percentage logic** |

---

## Files Created

### Book Upgrade (8 files)
- `menu/bookupgrade/BookUpgradeCustomMenu.java` — Main menu, 2-phase state machine (XP consolidation then upgrade)
- `menu/bookupgrade/BookUpgradeExtraData.java` — State: mainBook, bookIngredients, randomXp, readyToUpgrade
- `menu/bookupgrade/item/BookUpgradeSlotItem.java` — Click → return main book
- `menu/bookupgrade/item/BookUpgradeResultItem.java` — Display only: preview/result
- `menu/bookupgrade/item/IngredientPreviewItem.java` — Click → remove specific ingredient
- `menu/bookupgrade/item/BookUpgradeRemindItem.java` — Click → confirm upgrade/XP
- `command/BookUpgradeCommand.java` — Opens menu via MenuOpener
- `resources/menu-new/book-upgrade.yml` — 6 rows, template items

### Artifact Upgrade (8 files)
- `menu/artifactupgrade/ArtifactUpgradeCustomMenu.java` — Linear workflow menu
- `menu/artifactupgrade/ArtifactUpgradeExtraData.java` — State: selectedArtifact, ingredients, totalPoint
- `menu/artifactupgrade/item/SelectedArtifactItem.java` — Click → return artifact
- `menu/artifactupgrade/item/PreviewArtifactItem.java` — Display only
- `menu/artifactupgrade/item/ArtifactIngredientPreviewItem.java` — Click → remove ingredient
- `menu/artifactupgrade/item/ArtifactRemindItem.java` — Click → confirm upgrade
- `command/ArtifactUpgradeCommand.java` — Opens menu via MenuOpener
- `resources/menu-new/artifact-upgrade.yml` — 6 rows, template items

### Modified (6 files)
- `plugin.yml` — Added `bookupgrade-new`, `artifactupgrade-new` commands
- `command/CommandModule.java` — Registered both new commands
- `menu/MenuModule.java` — Registered both menu strategies
- `menu/bookcraft/BookCraftCustomMenu.java` — Fixed display restoration bug
- `resources/menu-new/book-craft.yml`, `ce-anvil.yml`, `tinkerer.yml` — Sound config
