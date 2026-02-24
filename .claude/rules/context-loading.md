# Context Loading Strategy

## Pre-Read Protocol (MANDATORY)

**Before reading ANY `.java` source file, follow this checklist in order. Stop at the first step that provides sufficient information.**

1. **Check loaded context** — Is the answer already in summaries or files loaded this session? → **Stop, use existing context**
2. **Check module summary** — Does `{module}.md` in `.claude/docs/codemap/summaries/` cover this class? → **Read module summary only**
3. **Check MCP file summary** — Call `get_file_summary(class_name)` to get cached summary, methods, tags → **Use if sufficient**
4. **Read source file** — Only if steps 1-3 are insufficient AND the task requires exact implementation details

**NEVER read source files for:**
- Questions about class purpose, module membership, or relationships (use summaries)
- Questions about what methods a class has (use `get_file_summary`)
- Exploring which classes exist in a module (use module summary or MCP `search_code`)

## Incremental Loading Stages

Load context in stages. **Stop at the earliest sufficient stage.**

| Stage | What | Token Cost | When Sufficient |
|-------|------|-----------|-----------------|
| **1. Orientation** | PLUGIN-SUMMARY.md | ~300 tokens | Always loaded. Sufficient for "which module handles X?" questions |
| **2. Targeted** | 1-2 module summaries + MCP `get_file_summary` | ~200-500 tokens | Questions, discovery, simple bug triage |
| **3. Deep Dive** | Source files (max 2-3 at a time) | ~600-2000 tokens | Implementation, debugging, exact code inspection |

**Rules:**
- **Questions** → Stop at Stage 2. Only proceed to Stage 3 if asking about exact implementation logic
- **Bug fixes** → Start at Stage 2, proceed to Stage 3 only for the specific buggy file(s)
- **Features** → Stage 2 for planning, Stage 3 only for files being modified or serving as patterns
- **Refactors** → Stage 2 + LSP/Grep for impact analysis, Stage 3 for files being changed

## Summary Hierarchy

Summaries live in `.claude/docs/codemap/summaries/`:

| Level | File | Size | Contains |
|-------|------|------|----------|
| 6 | `~/.claude/shared/` | ~2KB | Cross-project: BafFramework API, Bukkit patterns, registry |
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

## Cache-Aware Loading (MANDATORY)

**MANDATORY: Before using the `Read` tool on ANY Java source file, call MCP `get_file_summary(class_name)` first.** If the summary, methods list, and tags answer your question → skip the full read.

### Pre-Read Decision Flow

```
Need info about a Java class?
  ├─ Call get_file_summary(class_name)
  ├─ Summary sufficient? → STOP, use summary
  ├─ Need method signatures? → Check methods list → STOP if found
  └─ Need exact implementation? → Read source file (Stage 3)
```

### When to Use Cache vs Source

| Need | Use `get_file_summary` | Read Source |
|------|------------------------|-------------|
| What does this class do? | Yes (summary) | No |
| What module is it in? | Yes (module field) | No |
| What methods does it have? | Yes (methods list) | No |
| What does it depend on? | Yes (dependencies) | No |
| How is a method implemented? | No | Yes |
| What are the exact parameters? | No | Yes |
| Find a specific code pattern | No | Yes (or Grep) |

### Session Re-Read Prevention

- Track files already read in the current session — never re-read an unchanged file
- If you need to reference a file you already read, use the context already loaded
- After modifying a file, re-read only if you need to verify the edit

### Cache Maintenance

```bash
python .claude/docs/codemap/cache/_generate_cache.py --check   # Validate
python .claude/docs/codemap/cache/_generate_cache.py --diff ... # Incremental update
python .claude/docs/codemap/cache/_generate_cache.py --full     # Full rebuild
```

After completing work that involved source file analysis → suggest running `/cache-update`.

## Regenerating Summaries

When the codebase changes significantly:
```bash
python .claude/docs/codemap/summaries/_generate_summaries.py
```
