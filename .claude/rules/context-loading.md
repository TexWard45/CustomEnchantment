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

Module files: `enchant.md`, `item.md`, `player.md`, `menu.md`, `command.md`, `listener.md`, `task.md`, `config.md`, `guard.md`, `database.md`, `attribute.md`, `filter.md`, `execute.md`, `feature.md`, `custommenu.md`, `placeholder.md`, `root.md`

## Task Classification

| Type | Keyword Signals |
|------|----------------|
| **question** | what, why, how, explain, show, list, where, describe |
| **bugfix** | fix, NPE, error, exception, crash, wrong, broken, fails |
| **feature** | add, create, implement, new, support, integrate, enable |
| **refactor** | refactor, rename, extract, move, split, consolidate |

## Scope Detection

| Scope | Signals | Token Budget |
|-------|---------|-------------|
| **narrow** | Names a specific class/method/field | ~2K (PLUGIN-SUMMARY + 0-1 module) |
| **module** | Names or implies 1-2 modules | ~4.5K (PLUGIN-SUMMARY + 1-2 modules) |
| **broad** | Crosses 3+ modules; system-wide change | ~8K (PLUGIN-SUMMARY + 2-4 modules) |
| **discovery** | No module identifiable; "where is X handled?" | ~3K (PLUGIN-SUMMARY + MCP/Grep) |

## Loading Matrix (Task Type x Scope)

| | Narrow | Module | Broad | Discovery |
|--|--------|--------|-------|-----------|
| **question** | PLUGIN-SUMMARY + relevant `{module}.md` | PLUGIN-SUMMARY + 1-2 `{module}.md` | PLUGIN-SUMMARY + 2-4 `{module}.md` | PLUGIN-SUMMARY + MCP `search_code` |
| **bugfix** | PLUGIN-SUMMARY + `{module}.md` + source file(s) | PLUGIN-SUMMARY + 1-2 `{module}.md` + source file(s) | PLUGIN-SUMMARY + 2-3 `{module}.md` + source files | PLUGIN-SUMMARY + MCP/Grep to locate, then source |
| **feature** | PLUGIN-SUMMARY + `{module}.md` | PLUGIN-SUMMARY + 1-2 `{module}.md` + similar patterns | PLUGIN-SUMMARY + 2-4 `{module}.md` + source stubs | PLUGIN-SUMMARY + MCP search, then module summaries |
| **refactor** | PLUGIN-SUMMARY + `{module}.md` + source file(s) | PLUGIN-SUMMARY + 1-2 `{module}.md` + source files + callers | PLUGIN-SUMMARY + 2-4 `{module}.md` + LSP references | PLUGIN-SUMMARY + MCP/Grep to map usage |

**Always start with PLUGIN-SUMMARY.** Add module summaries only for modules relevant to the task. Read source files only when the matrix cell includes "source."

## Stopping Rules — When NOT to Read Source Files

- **Questions**: Summaries are sufficient unless asking about exact implementation logic
- **Never load all module summaries** — pick only relevant ones (max 4)
- **Never load JSON indexes directly** — `classes.json` (221KB), `enchantments.json` (181KB) are too large; use MCP search or Grep instead
- **Stop reading** once you have enough context to answer/implement — do not read "just in case"
- **Skip source files** for questions about structure, relationships, or module responsibilities

## MCP Semantic Search

Use MCP tools when you don't know which file/class to look at:

| Tool | Use Case |
|------|----------|
| `search_code` | Find classes, methods, listeners by description |
| `search_enchantments` | Find enchantments (579 total) with group/trigger/item filters |
| `search_configs` | Find config classes and YAML key paths |
| `get_module_summary` | Direct lookup of a module summary by name |

For discovery scope or unknown modules, invoke `/context-selector` before loading summaries.

## Regenerating Summaries

When the codebase changes significantly:
```bash
python .claude/docs/codemap/summaries/_generate_summaries.py
```
