---
name: context-selector
description: Classify tasks and select minimal context for discovery scope or ambiguous tasks. Use when you don't know which module to load, or the task spans unknown areas. Outputs a context loading plan.
---

# Context Selection Engine

Invoke this skill when the target module is unknown or the task is ambiguous. For known-module tasks, use the loading matrix in `context-loading.md` directly.

## Step 1: Classify the Task

### Task Type (pick one)

| Type | Keyword Signals |
|------|----------------|
| **question** | what, why, how, explain, show, list, where, describe |
| **bugfix** | fix, NPE, error, exception, crash, wrong, broken, fails |
| **feature** | add, create, implement, new, support, integrate, enable |
| **refactor** | refactor, rename, extract, move, split, consolidate |

**Compound tasks**: If a task combines types (e.g., "fix bug and add feature"), use the **higher-effort type** as primary. Precedence: refactor > feature > bugfix > question.

### Scope (pick one)

| Scope | Signals |
|-------|---------|
| **narrow** | Names a specific class, method, or field |
| **module** | Names or implies 1-2 modules |
| **broad** | Crosses 3+ modules or is system-wide |
| **discovery** | No module identifiable; "where is X?" |

**Scope upgrade signals**: "all", "every", "across modules", "system-wide" → upgrade to broad.
**Scope downgrade signals**: "just", "only", "specifically", "in class X" → downgrade to narrow.

## Step 2: Score Module Relevance (Discovery/Broad Only)

For each module, assign a relevance score based on keyword matches:

| Score | Meaning | Action |
|-------|---------|--------|
| 3 | Primary target | Load `{module}.md` |
| 2 | Supporting/dependency | Load `{module}.md` if budget allows |
| 1 | Tangential | Skip unless needed later |
| 0 | Irrelevant | Skip |

### Keyword → Module Mapping

| Keywords | Module(s) |
|----------|-----------|
| enchant, enchantment, effect, condition hook, execute hook | `enchant` |
| listener, event, bukkit event, damage, combat, move | `listener` |
| player, CEPlayer, expansion, player data | `player` |
| config, YAML, settings, CEEnchantMap, CEGroupMap | `config` |
| item, artifact, outfit, sigil, weapon, CEItem | `item` |
| menu, GUI, inventory, tinkerer, book craft | `menu` |
| command, /ce, subcommand, argument | `command` |
| task, tick, scheduler, repeating, TPS | `task` |
| database, MySQL, Redis, persistence, save | `database` |
| attribute, modifier, stat, buff | `attribute` |
| filter, item filter, material | `filter` |
| guard, protection, region | `guard` |
| execute, action, broadcast | `execute` |
| feature, requirement, condition (general) | `feature` |
| custommenu, custom menu integration | `custommenu` |
| placeholder, PAPI, PlaceholderAPI | `placeholder` |
| plugin, main class, registration, module order | `root` |

If no keywords match, use MCP `search_code` with the task description to identify relevant modules.

## Step 3: Expand Dependencies (Score-3 Modules Only)

For each score-3 module, auto-add its direct dependencies at score-2 (1-hop max):

| Score-3 Module | Auto-add at Score-2 |
|----------------|---------------------|
| ListenerModule | EnchantModule |
| MenuModule | ConfigModule |
| PlayerModule | EnchantModule |
| TaskModule | PlayerModule |
| ConfigModule | EnchantModule |
| AttributeModule | PlayerModule |
| CustomMenuModule | ConfigModule |
| ItemModule | ConfigModule |
| DatabaseModule | PlayerModule |

Do not cascade — score-2 modules do not trigger further expansion.

## Step 4: Build the Loading Plan

Based on task type, scope, and scored modules, assemble the context plan:

1. **Always**: Read `PLUGIN-SUMMARY.md`
2. **Score-3 modules**: Read their `{module}.md` summaries
3. **Score-2 modules**: Read if within token budget (narrow: 0-1, module: 1-2, broad: 2-4)
4. **Source files**: Only when the loading matrix cell includes "source" (bugfix, refactor)
5. **MCP search**: Use for discovery scope or when module mapping yields no results

### Token Budget Enforcement

| Scope | Max Summaries | Max Source Files | Total Budget |
|-------|--------------|-----------------|-------------|
| narrow | 1 module | 1-2 files | ~2K tokens |
| module | 2 modules | 2-3 files | ~4.5K tokens |
| broad | 4 modules | 3-5 files | ~8K tokens |
| discovery | 0 modules (search first) | 0-2 files | ~3K tokens |

## Pre-Built Templates

### Template 1: Fix Bug in Known Class
```
Type: bugfix | Scope: narrow
Load: PLUGIN-SUMMARY → {module}.md for the class's module → source file
```

### Template 2: Add Enchant Condition/Execute
```
Type: feature | Scope: module
Load: PLUGIN-SUMMARY → enchant.md → existing similar condition/execute source (for pattern)
```

### Template 3: Understand Data Flow
```
Type: question | Scope: broad
Load: PLUGIN-SUMMARY → source/target module summaries → check dependencies.json flow
```

### Template 4: Update Config
```
Type: feature | Scope: narrow
Load: PLUGIN-SUMMARY → config.md → relevant config class source
```

### Template 5: Add Command
```
Type: feature | Scope: module
Load: PLUGIN-SUMMARY → command.md → root.md (for plugin registration)
```

## Fallback: Search Without MCP

If MCP semantic search is unavailable:

1. `Grep` for class/method names in `.claude/docs/codemap/summaries/` to find the module
2. `Grep` in source directories for specific patterns
3. Check `dependencies.json` for module relationships
4. Fall back to reading `PLUGIN-SUMMARY.md` and scanning module descriptions

## Output Format

After running this skill, output a context plan:

```
Classification: {type} | {scope}
Modules: {score-3 list} + deps: {score-2 list}
Loading Plan:
  1. PLUGIN-SUMMARY.md
  2. {module}.md (score 3)
  3. {module}.md (score 2, if budget allows)
  4. {source files, if needed}
Est. Tokens: ~{number}
```
