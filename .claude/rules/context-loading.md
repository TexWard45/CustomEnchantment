# Context Loading Strategy

## Summary Hierarchy

Summaries live in `.claude/docs/codemap/summaries/`:

| Level | File | Size | Contains |
|-------|------|------|----------|
| 5 | `ECOSYSTEM.md` | ~1KB | Plugin in server stack, integration points |
| 4 | `PLUGIN-SUMMARY.md` | ~3KB | All 16 modules at a glance, data flows |
| 3 | `{module}.md` | ~4-15KB | Module classes with key methods |
| 2 | Embedded in Level 3 | — | ~121 detailed class summaries |
| 1 | Embedded in Level 2 | — | Key method signatures per class |

## When to Load What

### Questions / Explanations
1. Read `PLUGIN-SUMMARY.md` first
2. Read the relevant `{module}.md` if question is module-specific
3. **Do NOT read source files** unless the summary is insufficient

### Bug Fixes
1. Read `PLUGIN-SUMMARY.md` for context
2. Read the relevant `{module}.md` to understand class relationships
3. Read the specific source file(s) for the bug

### New Features
1. Read `PLUGIN-SUMMARY.md` for architecture overview
2. Read `{module}.md` for the target module
3. Read `{module}.md` for any similar existing patterns
4. Read source files only for the classes you need to modify/extend

### Code Review
1. Read `PLUGIN-SUMMARY.md` if unfamiliar with the codebase
2. Read relevant `{module}.md` to understand affected area
3. Read the changed source files

## Rules

- **Never load all module summaries at once** — pick only the relevant ones
- **Start at the highest useful level** — Level 4 for broad context, Level 3 for module work
- **Summaries replace source reads for understanding** — only read source when you need exact implementation details
- **Phase 2 JSON indexes** (`.claude/docs/codemap/index/`) are for search/lookup, not for loading into context
- Module files are named by module: `enchant.md`, `item.md`, `player.md`, `menu.md`, `command.md`, `listener.md`, `task.md`, `config.md`, `guard.md`, `database.md`, `attribute.md`, `filter.md`, `execute.md`, `feature.md`, `custommenu.md`, `placeholder.md`, `root.md`

## MCP Semantic Search (Phase 4)

When MCP server is running, use these tools for natural language search:

| Tool | Use Case | Example |
|------|----------|---------|
| `search_code` | Find classes, methods, listeners, modules | "damage reduction", "player join event" |
| `search_enchantments` | Find enchantments with domain filters | "legendary sword fire", group="legendary" |
| `search_configs` | Find config classes and YAML keys | "combat settings", "redis configuration" |
| `get_module_summary` | Direct lookup of Phase 3 summaries | module_name="enchant", section="Key Classes" |
| `reindex` | Rebuild index after data changes | force=True to rebuild regardless |

**When to use MCP search vs direct file reads:**
- Use MCP search when you don't know which class/file to look at
- Use direct file reads when you already know the exact file
- MCP search is especially useful for finding enchantments (579 total) and cross-module relationships

## Regenerating Summaries

When the codebase changes significantly, regenerate with:
```bash
python .claude/docs/codemap/summaries/_generate_summaries.py
```
