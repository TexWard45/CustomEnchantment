---
name: update-summaries
description: Regenerate hierarchical codebase summaries from Phase 2 indexes and Java source
user_invocable: true
---

# Summary Generator

Regenerates the five-level hierarchical summary system in `.claude/docs/codemap/summaries/`.

## Prerequisites

Phase 2 JSON indexes must exist in `.claude/docs/codemap/index/`:
- `classes.json` (541 class entries)
- `modules.json` (16 modules)
- `methods.json` (257 method entries)
- `dependencies.json` (module dependency graph)
- `entry-points.json` (listeners and commands)
- `configs.json` (17 config files)
- `enchantments.json` (enchantment definitions)

## Usage

```bash
python .claude/docs/codemap/summaries/_generate_summaries.py
```

## Output Structure

| File | Level | Content |
|------|-------|---------|
| `ECOSYSTEM.md` | 5 | Plugin position in server stack |
| `PLUGIN-SUMMARY.md` | 4 | All modules overview, data flows |
| `{module}.md` (17 files) | 3 | Module-level summaries with embedded L2/L1 |

### Module Files
`root.md`, `enchant.md`, `item.md`, `player.md`, `menu.md`, `command.md`, `listener.md`, `task.md`, `config.md`, `guard.md`, `database.md`, `attribute.md`, `filter.md`, `execute.md`, `feature.md`, `custommenu.md`, `placeholder.md`

## What the Generator Does

1. Loads Phase 2 JSON indexes for class metadata
2. Parses Java source files for fields, methods, annotations, line counts
3. Classifies classes into "detailed" or "pattern-grouped" categories
4. Scores classes by importance (line count, module/abstract/entry-point status)
5. Selects top N classes per module for detailed Level 2 summaries
6. Groups repetitive classes (conditions, effects, item impls) into pattern summaries
7. Generates markdown files with embedded Level 1 method signatures

## When to Regenerate

- After adding new modules or significant new classes
- After Phase 2 indexes are regenerated
- After major refactoring that changes class structure

## Validation

After regeneration, verify:
- Total markdown size < 200KB
- All 16 modules have summary files
- ~100+ detailed class summaries across all files
- Key classes (CECaller, CEEnchant, CEFunction, PlayerStorage) have useful summaries
