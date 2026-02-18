# Issue #30: CustomMenu Migration to BafFramework

## Overview
Migration of all 6 interactive menus from legacy CustomMenu API to BafFramework CustomMenu system.

**Branch:** `30-custommenu-migration`
**Status:** Completed

---

## Phases

### Phase 0: Preparation (#31)
- **Goal:** Foundation setup, dependency analysis
- **Status:** Completed

### Phase 1: Tinkerer Menu (#32)
- **Goal:** Prototype migration pattern with simplest menu
- **Status:** Completed

### Phase 2: BookCraft Menu (#33)
- **Goal:** Migrate BookCraft + implement FastCraft algorithm
- **Iterations:** 7 (state management + resource tracking bugs)
- **Status:** Completed
- **Docs:** See `FASTCRAFT_RETROSPECTIVE.md`, `ENGINEERING_LESSONS.md`, `LESSONS_FOR_NEXT_PHASE.md`

### Phase 3: CE Anvil Menu (#34)
- **Goal:** Migrate most complex menu (14 view types → handler pattern)
- **Status:** Completed

### Phase 4: BookUpgrade + ArtifactUpgrade (#35)
- **Goal:** Migrate two menus with requirement/upgrade systems
- **Status:** Completed

### Phase 5: Equipment Menu (#36)
- **Goal:** Migrate auto-updating menu with skin swap feature
- **Status:** Completed

### Phase 6: Declarative YAMLs (#37)
- **Goal:** Migrate YAML-only menus
- **Status:** CANCELLED (YAML-only menus stay on legacy API, no functional benefit to migrate)

### Phase 7: Cleanup & Finalization (#38)
- **Goal:** Remove legacy code, consolidate commands, final refactoring
- **Deliverables:**
  - Removed ~36 legacy Java files + 9 legacy YAML files
  - Consolidated commands (removed `-new` suffix)
  - Migrated settings references to new CustomMenu classes
  - Refactored BookCraft to remove hardcoded `BookCraftSettings`
  - Added max level validation bug fix
  - Updated messages.yml to match code paths
- **Status:** Completed
- **Docs:** See `.claude/docs/issues/38/issue-38-phase7-cleanup.md`

---

## Success Criteria

- [x] All 6 interactive menus work identically to current behavior
- [x] All 6 YAML configs load correctly under new system (`menu-new/`)
- [x] Legacy classes removed (~36 files)
- [x] Legacy YAML files removed (9 files)
- [x] Commands consolidated (no `-new` suffix)
- [x] Player-inventory click handling works for all interactive menus
- [x] Equipment menu auto-update task lifecycle correct
- [x] Build passes (`./gradlew build -x test`)
- [x] All menus use BafFramework `updateSlots()`/`getTemplateItemStack()` API

---

## Key Metrics

| Metric | Value |
|--------|-------|
| Phases completed | 7 of 8 (Phase 6 cancelled) |
| Legacy Java files deleted | ~36 |
| Legacy YAML files deleted | 9 |
| New menu classes created | 6 (`*CustomMenu.java`) |
| New item classes created | ~30 (`AbstractItem` subclasses) |
| New ExtraData classes | 6 |
| Commands consolidated | 6 |
| Bug fixes | 8+ (FastCraft: 7, max level: 1) |

---

## Documentation Index

### Phase 2 (BookCraft + FastCraft)
- [FASTCRAFT_RETROSPECTIVE.md](./FASTCRAFT_RETROSPECTIVE.md) — Bug analysis (7 iterations)
- [ENGINEERING_LESSONS.md](./ENGINEERING_LESSONS.md) — Universal engineering principles
- [LESSONS_FOR_NEXT_PHASE.md](./LESSONS_FOR_NEXT_PHASE.md) — Practical patterns for future work
- [MENU_IMPLEMENTATION_COMPARISON.md](./MENU_IMPLEMENTATION_COMPARISON.md) — Legacy vs BafFramework comparison
- [FASTCRAFT_EDGE_CASES.md](./FASTCRAFT_EDGE_CASES.md) — Test coverage documentation

### Phase 7 (Cleanup)
- [issue-38-phase7-cleanup.md](../38/issue-38-phase7-cleanup.md) — Final cleanup summary

---

## Key Patterns Established

1. **Template item pattern** — Slotted item = default state, template (no slot) = active state
2. **updateSlots(name, null)** — Resets to YAML default
3. **itemData.getId()** — Returns YAML key for slot-independent click handling
4. **ExtraData** — Per-player menu state management
5. **AbstractItem<MenuType>** — Type-safe click handlers with menu access
6. **MenuOpener.builder()** — Standard menu opening pattern

---

## Architecture Decision: `menu-new/` Folder

BafFramework menus live in `menu-new/`, legacy YAML-only menus stay in `menu/`. This avoids conflicts between the two CustomMenu systems scanning the same folder. Can be revisited when legacy CustomMenu dependency is fully removed.

---

## Status: COMPLETE
All phases delivered. Migration from legacy CustomMenu API to BafFramework CustomMenu is finished.
