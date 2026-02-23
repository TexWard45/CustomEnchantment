# Update Index

Regenerate the JSON index files in `.claude/docs/codemap/index/`.

## Prerequisites

Run `/update-codemaps` first if the codemap markdown files are outdated.

## Process

Follow the index-generator skill (`.claude/skills/index-generator/SKILL.md`):

1. Generate `modules.json` — 16 modules with metadata and load order
2. Generate `classes.json` — All classes with name, type, extends, tags, summary
3. Generate `methods.json` — Key API methods (hooks, events, lifecycle)
4. Generate `configs.json` — YAML-to-Java config mappings with @Path fields
5. Generate `enchantments.json` — Enchantment definitions from YAML files
6. Generate `dependencies.json` — Module and class dependency adjacency lists
7. Generate `entry-points.json` — Listeners, commands, tasks with metadata
8. Generate `search-tags.json` — Tag-to-class reverse index

## Output

All files written to `.claude/docs/codemap/index/`:
- `modules.json`
- `classes.json`
- `methods.json`
- `configs.json`
- `enchantments.json`
- `dependencies.json`
- `entry-points.json`
- `search-tags.json`

## Validation

After generation:
- Verify all JSON files parse correctly
- Verify classes.json has 540+ entries
- Verify search-tags.json is consistent with classes.json
- Verify total size < 500KB
