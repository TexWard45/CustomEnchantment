# AI Context Pipeline — User Guide

A practical guide for using and maintaining the AI Context Engineering Pipeline built in #58.

---

## Part 1: Implementing a New Feature

### How Claude Uses the Pipeline

When you ask Claude to implement something, the pipeline automatically provides efficient context loading. Here's what happens behind the scenes and how you can help:

### Step 1: Ask Claude to Implement

Just describe what you want. Claude will automatically:

1. **Classify the task** — question, bugfix, feature, or refactor
2. **Detect scope** — narrow (1 class), module (1-2 modules), broad (3+ modules), or discovery
3. **Select a workflow** — XS through XXL based on size (see `.claude/rules/workflow-selection.md`)
4. **Load minimal context** — summaries first, source files only when needed

```
You: "Add a new condition that checks if a player is in a specific biome"

Claude will automatically:
  1. Load PLUGIN-SUMMARY.md (always loaded)
  2. Identify this is EnchantModule scope (conditions live there)
  3. Load enchant.md summary to see existing condition patterns
  4. Use MCP search_code("condition biome") to check if one exists
  5. Read ONE existing condition source file as a template
  6. Follow TDD workflow to implement
```

### Step 2: Use Workflows for Sized Tasks

You can explicitly invoke a workflow if you know the size:

| Command | Size | When |
|---------|------|------|
| `/workflow-xs` | 1 file, < 20 lines | Typo fix, rename, add log |
| `/workflow-s` | 1-2 files, existing pattern | New condition/effect, config field |
| `/workflow-m` | 3-5 files, one module | Add feature to a module |
| `/workflow-l` | 5-15 files, cross-module | Major feature, system integration |
| `/workflow-xl` | 15+ files, new module | New module, major refactor |
| `/workflow-xxl` | Multiple plugins | Cross-plugin feature |

### Step 3: Use MCP Search for Discovery

When you're not sure where something lives:

```
You: "Where does the plugin handle item drops?"
Claude uses: MCP search_code("item drop handling")
  → Returns relevant classes, methods, listeners
  → No need to read dozens of files
```

Available MCP search tools:
- **`search_code`** — Find classes, methods, listeners by description
- **`search_enchantments`** — Search 579 enchantments by group/trigger/item type
- **`search_configs`** — Find config classes and YAML key paths
- **`get_file_summary`** — Get cached summary for any class (avoids reading source)
- **`get_module_summary`** — Get full module summary
- **`find_class`** — Exact class lookup by name
- **`analyze_impact`** — What would break if you change a class?

### Step 4: Cross-Project Context

When working across plugins, launch Claude with shared docs:

```bash
# Access BafFramework docs + Bukkit patterns from any plugin
claude --add-dir "C:\Users\nhata\.claude\shared"

# Also add another plugin's source for cross-reference
claude --add-dir "C:\Users\nhata\.claude\shared" --add-dir "D:\IntellijWorkspace\3FMC\Bukkit\CustomMenu"
```

### Example: Full Feature Implementation Flow

```
1. You:    "Add a BIOME condition that checks player's current biome"
2. Claude: Classifies as S-size feature (1-2 files, existing condition pattern)
3. Claude: Invokes /workflow-s
4. Claude: Loads enchant.md summary → sees 30 existing conditions
5. Claude: Reads ConditionHealth.java as template (via get_file_summary first)
6. Claude: Writes ConditionBiomeTest.java (TDD — test first)
7. Claude: Writes ConditionBiome.java (implementation)
8. Claude: Registers in EnchantModule.onEnable()
9. Claude: Runs ./gradlew test
10. Claude: Code review via code-reviewer agent
11. Claude: Shows summary, waits for your approval to commit
```

---

## Part 2: Keeping Data Up-to-Date

The pipeline has 5 data layers that may need updating. Here's when and how to update each.

### Quick Reference: Update Commands

| What | When to Update | Command |
|------|---------------|---------|
| **File cache** | After any code change | `/cache-update` |
| **MCP search index** | After adding/removing classes | MCP `reindex()` |
| **Codemaps** | After adding new modules or major restructuring | `/update-codemaps` |
| **JSON indexes** | After codemaps are regenerated | `/update-index` |
| **Summaries** | After indexes are regenerated | `/update-docs` |

### Layer 1: File Cache (Most Frequent)

**What:** `file-hashes.json` — cached summary, methods, tags for each Java file.
**When:** After any code change (new files, modified files, deleted files).
**How:**

```bash
# Incremental update (after recent commits) — fastest
python .claude/docs/codemap/cache/_generate_cache.py --diff $(git diff --name-only HEAD~1 -- '*.java')

# Update all changes since branching from master
python .claude/docs/codemap/cache/_generate_cache.py --diff $(git diff --name-only origin/master...HEAD -- '*.java')

# Validate cache integrity
python .claude/docs/codemap/cache/_generate_cache.py --check

# Full rebuild (if cache is corrupted or missing)
python .claude/docs/codemap/cache/_generate_cache.py --full
```

Or simply run `/cache-update` and Claude handles it.

### Layer 2: MCP Search Index (After New/Deleted Classes)

**What:** In-memory search index used by `search_code`, `search_enchantments`, etc.
**When:** After adding or removing Java classes, or adding new enchantment YAMLs.
**How:**

```
# In Claude session, call the MCP tool:
MCP reindex()           # Auto-detects changes
MCP reindex(force=True) # Force full rebuild
```

Claude can also call this automatically when it detects stale results.

### Layer 3: Codemaps (After Structural Changes)

**What:** 7 architecture markdown files in `.claude/docs/codemap/`.
**When:** After adding new modules, changing module structure, or major refactoring.
**How:** Run `/update-codemaps` — Claude re-analyzes the codebase using Glob/Grep/Read.

**Files regenerated:**
- `MODULE-MAP.md` — Module load order, dependencies
- `CLASS-REGISTRY.md` — All classes with type, extends, implements
- `DEPENDENCY-GRAPH.md` — Module and class dependencies
- `PATTERN-CATALOG.md` — Singletons, strategies, builders, hooks
- `CONFIG-SCHEMA-MAP.md` — YAML ↔ Java class mappings
- `DOMAIN-MAP.md` — Classes tagged by domain (enchantment, combat, etc.)
- `ENTRY-POINTS.md` — Listeners, commands, scheduled tasks

### Layer 4: JSON Indexes (After Codemaps Change)

**What:** 8 JSON files in `.claude/docs/codemap/index/`.
**When:** After codemaps are regenerated (Layer 3).
**How:** Run `/update-index` — Claude parses codemaps + source into structured JSON.

**Files regenerated:**
- `modules.json` — 16 modules with metadata
- `classes.json` — 541 classes with tags, summary, dependencies
- `methods.json` — 2800+ key methods
- `configs.json` — 17 config files with field paths
- `enchantments.json` — 579 enchantments with group/trigger/item
- `dependencies.json` — Module dependency graph
- `entry-points.json` — Listeners and commands
- `search-tags.json` — Reverse tag index

### Layer 5: Summaries (After Indexes Change)

**What:** 19 markdown files in `.claude/docs/codemap/summaries/`.
**When:** After JSON indexes are regenerated (Layer 4), or after significant class changes.
**How:**

```bash
python .claude/docs/codemap/summaries/_generate_summaries.py
```

Or run `/update-docs` — Claude regenerates all summaries.

**Files regenerated:**
- `ECOSYSTEM.md` (Level 5) — Plugin in server stack
- `PLUGIN-SUMMARY.md` (Level 4) — All 16 modules overview
- 17 module files (Level 3) — `enchant.md`, `item.md`, `player.md`, etc.

### Update Decision Tree

```
Did you change Java source code?
  ├─ Yes → Run /cache-update
  │   ├─ Added/removed classes? → Also run MCP reindex()
  │   └─ Changed module structure? → Full pipeline (see below)
  └─ No
      ├─ Changed enchantment YAMLs? → Run MCP reindex()
      └─ Changed config YAMLs? → No update needed (configs parsed at runtime)
```

### Full Pipeline Rebuild (Rare)

When you need to rebuild everything (new module, major refactor):

```
1. /update-codemaps          # Regenerate architecture maps
2. /update-index             # Rebuild JSON indexes from codemaps
3. python .../_generate_summaries.py   # Regenerate summaries
4. /cache-update             # Rebuild file cache
5. MCP reindex(force=True)   # Rebuild search index
```

This takes ~5-10 minutes but is rarely needed. Most changes only need `/cache-update`.

---

## Part 3: What Each Pipeline Layer Does

```
Layer 5: Shared Docs (~/.claude/shared/)
  └─ Cross-project: BafFramework API, Bukkit patterns, registry
       ↑ Manual update when framework changes

Layer 4: Summaries (.claude/docs/codemap/summaries/)
  └─ 19 hierarchical markdown summaries (Ecosystem → Plugin → Module → Class)
       ↑ /update-docs or _generate_summaries.py

Layer 3: JSON Indexes (.claude/docs/codemap/index/)
  └─ 8 structured JSON files (541 classes, 579 enchants, 2800+ methods)
       ↑ /update-index

Layer 2: Codemaps (.claude/docs/codemap/)
  └─ 7 architecture markdown files (modules, classes, patterns, deps)
       ↑ /update-codemaps

Layer 1: File Cache (.claude/docs/codemap/cache/)
  └─ SHA-256 hashes + metadata for each Java file
       ↑ /cache-update (most frequent)

Layer 0: MCP Search Index (in-memory)
  └─ Hybrid BM25+FAISS search across all entities
       ↑ MCP reindex()
```

### How Claude Reads Context (Top-Down)

```
1. PLUGIN-SUMMARY.md (~300 tokens) — Always loaded. "Which module handles X?"
2. {module}.md (~500 tokens) — Loaded for relevant modules. "What classes are in this module?"
3. MCP get_file_summary() (~100 tokens) — Before reading source. "What does this class do?"
4. Source file (~600-2000 tokens) — Only when exact implementation needed.
```

This means Claude typically uses ~1K-3K tokens of context instead of ~50K-200K.

---

## Part 4: Tips & Best Practices

### For Best Results

1. **Be specific about scope** — "Add a condition to EnchantModule" is better than "add a feature"
2. **Name the pattern** — "Add a new ConditionHook like ConditionHealth" helps Claude find the template instantly
3. **Use `/context-selector`** — When you're unsure which modules are involved, this skill analyzes your task and recommends what to load

### When Things Feel Slow

- Run `/cache-update` — stale cache causes unnecessary file reads
- Run `MCP reindex()` — search results may be outdated
- Check if context window is full — use `/clear` between unrelated tasks

### After Pulling Changes

```bash
git pull origin master
/cache-update                  # Update file cache
MCP reindex()                  # Refresh search index
```

### After Major Refactoring

Run the full pipeline rebuild (see Part 2).

### Shared Docs Maintenance

Update `~/.claude/shared/` when:
- BafFramework API changes → Update `shared/bafframework/QUICK_REFERENCE.md`
- Upgrading Minecraft version → Update `shared/registry/version-map.json`
- Adding a new plugin to the ecosystem → Update `shared/registry/registry.json`
