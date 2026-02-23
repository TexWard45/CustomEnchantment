---
name: index-generator
description: Generate structured JSON index files in .claude/docs/codemap/index/ from the existing codemap markdown files and source code. Produces modules.json, classes.json, methods.json, configs.json, enchantments.json, dependencies.json, entry-points.json, and search-tags.json.
---

# Index Generator

Automatically generate structured JSON index files for programmatic querying of the codebase.
Builds on the codemap markdown files produced by the codemap-generator skill.
Uses only Glob, Grep, and Read tools — no external dependencies.

## Output Directory

All files are generated in `.claude/docs/codemap/index/`.

## Prerequisites

The following codemap files must exist in `.claude/docs/codemap/`:
- MODULE-MAP.md
- CLASS-REGISTRY.md
- DEPENDENCY-GRAPH.md
- PATTERN-CATALOG.md
- CONFIG-SCHEMA-MAP.md
- DOMAIN-MAP.md
- ENTRY-POINTS.md

## Generation Process

### Step 1: Generate modules.json

**Sources:** MODULE-MAP.md + Grep for `extends PluginModule`

```
Read: MODULE-MAP.md → Extract module table rows
Grep: "extends PluginModule" → Verify module class list
Read: Each module class → Extract lifecycle methods, dependencies
Count: Classes per module from CLASS-REGISTRY.md
```

Output: Module name, order, package, purpose, classCount, dependsOn, dependedBy, tags.

### Step 2: Generate classes.json

**Sources:** Grep all Java source files for class declarations

```
Grep: "^public (abstract )?(class|interface|enum) (\w+)" across all Java files
Extract: name, package, module, file path, extends, implements
Read: First 10 lines of each file for annotations
```

Auto-tag using rules:

| Signal | Tags |
|--------|------|
| `.enchant.*` package | enchantment |
| `.enchant.condition.*` | conditions |
| `.enchant.effect.*` | effects |
| `.item.*` package | items |
| `.player.*` package | player |
| `.menu.*` package | gui |
| `.command.*` package | command |
| `.config.*` package | config |
| `.database.*` package | persistence |
| `.listener.*` package | events |
| `.task.*` package | scheduling |
| extends ConditionHook | hook, conditions |
| extends EffectHook | hook, effects |
| extends PluginModule | module |
| implements Listener | listener |
| @Configuration | config-class |
| static instance() | singleton |
| *Data suffix | data-class |
| *Factory suffix | factory |
| *Manager suffix | manager |

### Step 3: Generate methods.json

**Scope:** Key API methods only (hooks, events, lifecycle, commands)

```
Grep: "getIdentify()" return values from ConditionHook/EffectHook impls
Grep: "@EventHandler" + extract event type from parameter
Extract: Module lifecycle methods (onEnable, onReload, onSave, onDisable)
Extract: Command execute methods
```

### Step 4: Generate configs.json

**Sources:** CONFIG-SCHEMA-MAP.md + Grep @Configuration/@Path

```
Read: CONFIG-SCHEMA-MAP.md → Extract YAML-to-Java mappings
Grep: "@Configuration" → Find config classes
Grep: "@Path" → Extract field paths and types
```

### Step 5: Generate enchantments.json

**Source:** Read each YAML file in `src/main/resources/enchantment/`

```
Read: Each enchantment YAML file
Extract: id, group, display, max-level, applies, trigger types
Extract: Condition/effect identifiers from levels config
Auto-tag: group name + applies material type + trigger type
```

### Step 6: Generate dependencies.json

**Sources:** DEPENDENCY-GRAPH.md + import analysis

```
Read: DEPENDENCY-GRAPH.md → Module-level dependencies
Grep: "getPlugin().getModule" → Module→module references
Grep: "^import " → Top imports per class (class→class deps)
```

### Step 7: Generate entry-points.json

**Sources:** ENTRY-POINTS.md + verification

```
Read: ENTRY-POINTS.md → Listeners, commands, tasks
Grep: "@EventHandler" → Verify event handler list
Grep: "AdvancedCommandBuilder" → Verify command registrations
```

### Step 8: Generate search-tags.json

**Sources:** Aggregate tags from all generated JSON files

```
Read: classes.json → Collect all class tags
Read: enchantments.json → Collect enchantment tags
Build: Reverse index — tag → [entity IDs]
Sort: Alphabetically by tag, then by entity within tag
```

## JSON Schemas

See the plan document for detailed schema definitions for each file.

## Validation

After generation, verify:
- [ ] All 8 JSON files are valid (parseable)
- [ ] classes.json contains 540+ entries (95%+ of 545 classes)
- [ ] Each class has at least 1 domain tag
- [ ] enchantments.json covers all enchantment YAML files
- [ ] search-tags.json is consistent with classes.json tags
- [ ] Total JSON size < 500KB
- [ ] Each file < 100KB

## Token Budget

All generated JSON files combined should be under 500KB total.
Use concise summaries, not verbose descriptions.
