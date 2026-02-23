# Issue #58: AI Context Engineering Pipeline — Phase 2 (Indexing & Metadata)

## Status: Complete

## Parent Issue
**Parent:** #58 - AI Context Engineering Pipeline

## Phase Information
**Current Phase:** Phase 2 of 3
**Previous Phase:** Phase 1 — Architecture mapping (codemap markdown files)
**Next Phase:** Phase 3 — Context retrieval skills

## Deliverables

### JSON Index Files (`.claude/docs/codemap/index/`)

| File | Entries | Size | Description |
|------|---------|------|-------------|
| modules.json | 16 modules | 5KB | Module metadata, load order, dependencies |
| classes.json | 541 classes | 216KB | All classes with FQN, type, extends, tags, summary |
| methods.json | 257 methods | 25KB | Hook identifiers, event handlers, lifecycle, singletons |
| configs.json | 17 configs | 7KB | YAML-to-Java mappings with @Path fields |
| enchantments.json | 579 enchantments | 176KB | Enchantment defs from 31 YAML files |
| dependencies.json | — | 4KB | Module and class dependency adjacency lists |
| entry-points.json | — | 12KB | Listeners, commands, tasks with metadata |
| search-tags.json | 58 tags | 47KB | Tag-to-entity reverse index (1982 entries) |
| **Total** | | **494KB** | Under 500KB target |

### Skill & Command Files

| File | Purpose |
|------|---------|
| `.claude/skills/index-generator/SKILL.md` | 8-step indexing process definition |
| `.claude/commands/update-index.md` | User-facing command to regenerate indexes |

### Generator Scripts

| File | Purpose |
|------|---------|
| `_generate_classes.py` | Parses CLASS-REGISTRY.md → classes.json |
| `_generate_enchantments.py` | Parses enchantment YAML files → enchantments.json |
| `_generate_search_tags.py` | Aggregates tags from classes + enchantments |

## Verification Results

- [x] All 8 JSON files valid (parseable)
- [x] classes.json contains 541 entries (99% of 545 target)
- [x] 33 unique tags across class entries (domain + pattern tags)
- [x] enchantments.json covers 31 of 32 YAML files (space.yml was empty)
- [x] search-tags.json has 58 tags with 1982 total entries
- [x] Total JSON size: 494KB (under 500KB target)
- [x] No file exceeds 250KB
- [x] `./gradlew build` still passes (no source changes)

## Auto-Tagging Coverage

### Domain Tags Applied
enchantment, items, player, gui, command, config, persistence, events, scheduling, attributes, mobs, filtering, integration, executes, combat, mining, api, nms

### Pattern Tags Applied
hook, conditions, effects, module, listener, config-class, singleton, registry, factory, data-class, manager, builder, task, per-tick, abstract, interface, enum, utility

### Enchantment Tags Applied
common, rare, epic, legendary, supreme, ultimate, event, artifact, mask, tool, set, prime, sword, axe, bow, armor, pickaxe, hoe, boots, trident, gem, shield

## Lessons for Phase 3

1. **YAML parsing**: Some enchantment YAMLs use unquoted `&` color codes that break standard YAML parsers — fallback line-by-line parser needed
2. **CLASS-REGISTRY.md as source of truth**: More reliable than grep-based class discovery (541 vs 370 from grep)
3. **Compact JSON format**: Using `indent=1` kept files readable while staying under size budget
4. **Generator scripts**: Python scripts are reusable for regeneration — keep them in the index directory prefixed with `_`
5. **all.yml duplication**: Enchantment `all.yml` contains copies of entries from other files — deduplication by (id, file) prevents bloat

## Architecture Decision

Chose to keep generator scripts (`_generate_*.py`) alongside the output JSON files in the index directory. This makes regeneration easy:
```bash
cd .claude/docs/codemap/index
python _generate_classes.py
python _generate_enchantments.py
python _generate_search_tags.py
```

Manually created files (modules.json, methods.json, configs.json, dependencies.json, entry-points.json) are maintained by hand based on codemap markdown files.
