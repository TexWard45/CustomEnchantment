# Phase 1 — Codebase Understanding & Architecture Mapping

## Purpose

Automatically analyze any Java/Bukkit project and generate a structured architecture map that serves as the foundation for all subsequent phases. This produces the "knowledge skeleton" that Claude uses to understand where things are without reading every file.

## Design

### What Gets Generated

```
.claude/docs/codemap/
├── ECOSYSTEM.md              # High-level overview of all plugins/projects
├── MODULE-MAP.md             # All modules, their purpose, dependencies
├── CLASS-REGISTRY.md         # Every class with one-line summary
├── DEPENDENCY-GRAPH.md       # Module→module and class→class dependencies
├── PATTERN-CATALOG.md        # Detected design patterns
├── CONFIG-SCHEMA-MAP.md      # All YAML configs → Java class mappings
├── API-SURFACE.md            # Public API (what other plugins can use)
├── DOMAIN-MAP.md             # Auto-detected domains (enchant, combat, economy, etc.)
└── ENTRY-POINTS.md           # Event listeners, commands, scheduled tasks
```

### Architecture Analysis Pipeline

```
Step 1: Scan project structure
  → Gradle modules, package hierarchy, resource files
  → Output: raw file tree with metadata

Step 2: Parse Java sources (Tree-sitter or regex-based)
  → Extract: classes, interfaces, enums, annotations
  → Extract: extends/implements relationships
  → Extract: field types (dependency detection)
  → Extract: method signatures (API surface)

Step 3: Detect patterns
  → Singleton (static instance() methods)
  → Registry (extends StrategyRegister, Map<String, T>)
  → Hook (extends ConditionHook/EffectHook/ExecuteHook)
  → Module (extends PluginModule)
  → Listener (implements Listener, @EventHandler)
  → Builder (fluent API pattern)
  → Data class (@Getter/@Setter, no business logic)

Step 4: Map domains
  → Cluster classes by package and naming patterns
  → Tag: combat, enchantment, inventory, persistence, gui, command, config

Step 5: Generate documentation
  → Render all maps as Markdown files
  → Store in .claude/docs/codemap/
```

### Domain Detection Algorithm

```pseudocode
function detectDomains(classList):
  domains = {}

  for class in classList:
    # Package-based detection
    package = class.package
    if "enchant" in package: tag(class, "enchantment")
    if "menu" or "gui" in package: tag(class, "gui")
    if "command" in package: tag(class, "command")
    if "listener" in package: tag(class, "events")
    if "database" or "storage" in package: tag(class, "persistence")
    if "config" in package: tag(class, "configuration")
    if "task" in package: tag(class, "scheduling")
    if "player" in package: tag(class, "player")
    if "item" in package: tag(class, "items")

    # Pattern-based detection
    if extends "ConditionHook": tag(class, "conditions")
    if extends "EffectHook": tag(class, "effects")
    if extends "PluginModule": tag(class, "module-lifecycle")
    if has "@EventHandler": tag(class, "events")
    if extends "AbstractDatabase": tag(class, "persistence")
    if extends "AbstractMenu": tag(class, "gui")

    # Name-based detection
    if "Damage" or "Combat" or "Attack" in class.name: tag(class, "combat")
    if "Economy" or "Money" or "Cost" in class.name: tag(class, "economy")
    if "Guard" or "Mob" in class.name: tag(class, "mobs")

  return domains
```

## Technical Implementation

### Option A: Claude Code Skill (Recommended for Phase 1)

Create a skill at `.claude/skills/codemap-generator/SKILL.md` that:
1. Uses Glob to find all `.java` files
2. Uses Grep to extract class declarations, extends/implements, @annotations
3. Uses Read on key files (module classes, plugin main class)
4. Generates the codemap Markdown files

Pros: No external tools needed, works immediately
Cons: Uses tokens for the initial scan, slower on very large codebases

### Option B: External Script (For recurring updates)

A Gradle task or shell script that:
1. Uses `tree-sitter` CLI to parse all Java files
2. Extracts symbols and relationships into JSON
3. Generates Markdown from the JSON

```bash
# Example: tree-sitter based extraction
tree-sitter parse src/main/java/**/*.java --output json \
  | jq '.children[] | select(.type == "class_declaration")' \
  > .claude/docs/codemap/raw/classes.json
```

Pros: Fast, no token cost, can run in CI
Cons: Requires tree-sitter installation, separate toolchain

### Option C: Hybrid (Best for ongoing use)

- Initial scan: Use Option A (skill) for first-time generation
- Ongoing updates: Use Option B (script) triggered by git hooks
- Verification: Use a skill to validate the codemap matches reality

## Applied to CustomEnchantment

### MODULE-MAP.md (Example Output)

```markdown
# Module Map — CustomEnchantment

## Plugin: CustomEnchantment (com.bafmc.customenchantment)
Minecraft: Paper/Leaf 1.21.1 | Framework: BafFramework | Java 21

### Module Load Order (registration sequence matters)

| # | Module | Package | Purpose | Key Classes |
|---|--------|---------|---------|-------------|
| 1 | FeatureModule | .feature | Item strategies, abilities | DashFeature, DoubleJumpFeature |
| 2 | EnchantModule | .enchant | 30 conditions + 77 effects | ConditionHook impls, EffectHook impls |
| 3 | AttributeModule | .attribute | Custom attribute types | CustomAttributeType, AttributeMapRegister |
| 4 | FilterModule | .filter | Item/entity filtering | FilterRegister |
| 5 | ExecuteModule | .execute | Give/Use item hooks | GiveItemExecute, UseItemExecute |
| 6 | ConfigModule | .config | YAML config loading | MainConfig, CEEnchantConfig, 18 config classes |
| 7 | CustomMenuModule | .custommenu | External menu integration | CustomMenuModule |
| 8 | CommandModule | .command | 31 command handlers | CommandModule |
| 9 | ItemModule | .item | 21 item types (artifacts, books, etc.) | 129 classes |
| 10 | PlayerModule | .player | 13 player expansions | CEPlayer, 41 classes |
| 11 | TaskModule | .task | 14+ background tasks | EffectExecuteTask, CEPlayerTask |
| 12 | GuardModule | .guard | Guard mob system | GuardModule |
| 13 | DatabaseModule | .database | SQLite persistence | DatabaseModule |
| 14 | MenuModule | .menu | 6 menu implementations | 76 classes |
| 15 | PlaceholderModule | .placeholder | PlaceholderAPI integration | PlaceholderModule |
| 16 | ListenerModule | .listener | 15 event listeners | PlayerListener, EntityListener |

### Cross-Module Dependencies
- ListenerModule → EnchantModule (triggers enchant execution via CECaller)
- MenuModule → ConfigModule (reads menu configs)
- PlayerModule → EnchantModule (player enchant data)
- TaskModule → PlayerModule (tick-based player updates)
- ConfigModule → EnchantModule (populates enchant registry maps)
```

## Dependencies

- **None** — This is the foundational phase

## Success Criteria

- [ ] MODULE-MAP.md generated with all 16 modules
- [ ] CLASS-REGISTRY.md lists 550+ classes with one-line summaries
- [ ] DEPENDENCY-GRAPH.md shows module→module relationships
- [ ] PATTERN-CATALOG.md identifies 10+ design patterns
- [ ] DOMAIN-MAP.md tags classes into 8+ domains
- [ ] CONFIG-SCHEMA-MAP.md maps 40+ YAML files to Java config classes
- [ ] ENTRY-POINTS.md lists all listeners, commands, and scheduled tasks
- [ ] Generated docs are < 2000 lines total (stays within context budget)
- [ ] Works on any Gradle Java project, not just CustomEnchantment

---

## GitHub Issue

### Title
`feat: Phase 1 — Codebase Understanding & Architecture Mapping`

### Description

Create an automated codebase analysis system that generates structured architecture maps for any Java/Bukkit plugin project. These maps serve as the foundational knowledge layer for the AI context engineering pipeline.

### Background / Motivation

Currently, Claude Code must grep and glob through 550+ files to understand where things are. This wastes tokens and time on every task. A pre-generated architecture map lets Claude instantly navigate to the right module, class, or pattern without exploratory searches.

This is the foundation that all other phases (indexing, summarization, semantic search) build upon.

### Tasks

- [ ] Design codemap output format (MODULE-MAP.md, CLASS-REGISTRY.md, etc.)
- [ ] Create `codemap-generator` skill at `.claude/skills/codemap-generator/SKILL.md`
- [ ] Implement project structure scanner (Glob-based for .java, .yml, build.gradle)
- [ ] Implement class/interface/enum extractor (Grep-based regex patterns)
- [ ] Implement extends/implements relationship detector
- [ ] Implement design pattern detector (Singleton, Registry, Hook, Module, etc.)
- [ ] Implement domain auto-tagger (enchantment, combat, gui, persistence, etc.)
- [ ] Implement config schema mapper (YAML file → Java @Configuration class)
- [ ] Implement entry point scanner (listeners, commands, scheduled tasks)
- [ ] Generate all codemap files for CustomEnchantment as proof of concept
- [ ] Verify generated maps match manual analysis (validation)
- [ ] Test on BafFramework (second project) to verify plugin-agnostic design
- [ ] Document usage in `.claude/docs/index/01-PHASE1-CODEBASE-UNDERSTANDING.md`

### Technical Notes

- Use Grep patterns for Java analysis: `class\s+(\w+)\s+(extends|implements)`, `@EventHandler`, `@Configuration`, `@Path`
- Lombok annotations (@Getter, @Builder) indicate data classes vs business logic
- BafFramework patterns: PluginModule, ConditionHook, EffectHook, StrategyRegister
- Module load order matters — extract from `registerModules()` in main plugin class
- Config mappings: match YAML filenames to `ConfigUtils.setupResource()` calls

### Acceptance Criteria

- [ ] Running the codemap-generator skill on any Java project produces valid architecture maps
- [ ] Generated MODULE-MAP.md matches the actual 16-module structure of CustomEnchantment
- [ ] CLASS-REGISTRY.md contains at least 95% of all public classes
- [ ] Generated docs total < 2000 lines (token-efficient)
- [ ] No manual editing required after generation (fully automated)
- [ ] Works without external tools (Claude Code native — Glob, Grep, Read only)

### Future Improvements

- Tree-sitter integration for AST-level precision (Phase 2)
- Incremental updates via git diff (only re-analyze changed files)
- Visual dependency graph generation (Mermaid diagrams)
- Automatic staleness detection (flag when codemap diverges from code)

---
**Priority:** P1 | **Effort:** L | **Labels:** `feature`, `infrastructure`, `phase-1`, `effort:L`
