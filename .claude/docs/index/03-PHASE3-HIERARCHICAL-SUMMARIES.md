# Phase 3 — Hierarchical Context System & Summarization

## Purpose

Create multi-level code summaries that let Claude understand any part of the codebase at the right level of detail. Instead of reading a 500-line file, Claude reads a 5-line summary first, then dives deeper only if needed.

## Design

### Five-Level Hierarchy

```
Level 5: ECOSYSTEM (all plugins in the workspace)
  "3FMC plugin ecosystem: CustomEnchantment (enchants/artifacts/combat),
   BafFramework (core framework), CustomMenu (GUI system), CustomShop (economy)"

Level 4: PLUGIN (one project)
  "CustomEnchantment: 16 modules, 550+ classes. Adds custom enchantments,
   artifacts, outfits, and RPG mechanics to Minecraft. Built on BafFramework.
   Core flow: Event → Listener → CECaller → Condition checks → Effect execution"

Level 3: MODULE (one Gradle/logical module)
  "EnchantModule: Registers 30 ConditionHooks and 77 EffectHooks.
   Core class: CECaller orchestrates enchant execution.
   Key types: CEEnchant, CEFunction, CELevel, CEFunctionData.
   Dependencies: ConfigModule (config loading), PlayerModule (player data)"

Level 2: FILE (one Java file or YAML config)
  "ConditionHealth.java: ConditionHook implementation. Checks entity health
   against a threshold. Identifier: 'HEALTH'. Supports operators: >, <, =, >=, <=.
   Usage: condition: 'HEALTH:>50%' in enchantment YAML. 45 lines."

Level 1: FUNCTION (one method or code block)
  "check(ConditionData data, String value): Parses operator and threshold from
   value string. Gets target entity from ConditionData. Compares entity.getHealth()
   against threshold. Returns true if condition met. Handles percentage mode."
```

### When to Load Each Level

| Task Type | Start Level | Drill Down If... |
|-----------|-------------|-------------------|
| "What does this project do?" | Level 5 (Ecosystem) | — |
| "How do enchantments work?" | Level 4 (Plugin) → 3 (Module) | Need specific implementation |
| "Add a new condition" | Level 3 (Module) → 2 (File) | Need to see existing pattern |
| "Fix NPE in ConditionHealth" | Level 2 (File) → 1 (Function) | Need exact code |
| "Refactor condition system" | Level 3 (Module) → 2 (all files) | Understanding full scope |
| "Cross-plugin feature" | Level 5 → 4 (each plugin) → 3 | Identifying touch points |

### Context Loading Rules

```pseudocode
function selectContext(task):
  # Analyze task to determine required context level
  if task.isQuestion and task.isHighLevel:
    return loadLevel(5)  # Ecosystem overview only

  if task.targetModule is known:
    context = loadLevel(3, task.targetModule)  # Module summary
    if task.requiresImplementation:
      context += loadLevel(2, task.targetFiles)  # Relevant file summaries
      if task.requiresEditing:
        context += loadLevel(1, task.targetMethods)  # Actual code
    return context

  if task.isSearching:
    context = loadLevel(4)  # Plugin overview for orientation
    relevantModules = searchIndex(task.keywords)
    context += loadLevel(3, relevantModules)
    return context

  # Default: Plugin + relevant module summaries
  return loadLevel(4) + loadLevel(3, inferModules(task))
```

## Summarization Strategy

### File-Level Summary Template

For each Java file, generate:

```markdown
## {ClassName} ({file path}, {line count} lines)

**Type:** {class|interface|enum|abstract class}
**Extends:** {parent class}
**Implements:** {interfaces}
**Domain:** {tags}
**Module:** {module name}

**Purpose:** {1-2 sentence responsibility description}

**Key Methods:**
- `{method1}`: {one-line description}
- `{method2}`: {one-line description}

**Dependencies:** {classes this depends on}
**Used By:** {classes that depend on this}

**Constraints/Edge Cases:**
- {important constraint or edge case}

**Config Integration:** {if uses @Path or reads config}
```

### Summarization Rules

1. **Preserve constraints** — Always include validation rules, boundary conditions, thread safety requirements
2. **Preserve edge cases** — Document known gotchas, null handling, special return values
3. **Preserve API contracts** — Method signatures, return types, exception throwing
4. **Compress implementation details** — Replace algorithm code with natural language description
5. **Keep identifiers exact** — Class names, method names, config paths must be verbatim
6. **Flag complexity** — Note if a file is unusually complex or has known tech debt

### Compression Ratios

| Content Type | Original | Summary | Ratio |
|-------------|----------|---------|-------|
| Data class (POJO) | 50-100 lines | 3-5 lines | 20:1 |
| Simple condition | 40-80 lines | 5-8 lines | 10:1 |
| Complex manager | 200-500 lines | 15-25 lines | 20:1 |
| Config class | 30-100 lines | 5-10 lines | 10:1 |
| Listener (multi-event) | 100-300 lines | 10-15 lines | 15:1 |
| Module class | 50-150 lines | 8-12 lines | 12:1 |

### Summary Evolution

Summaries are **living documents** that get refined:

```
Generation 1: Auto-generated from code analysis
  "ConditionHealth checks health" (vague)

Generation 2: Enhanced with context from usage analysis
  "ConditionHealth checks entity health against threshold.
   Supports >, <, =, >=, <= operators. Handles percentage mode."

Generation 3: Enhanced with lessons from bugs/issues
  "ConditionHealth checks entity health against threshold.
   IMPORTANT: Must handle null entity (mob despawn during check).
   Known issue: percentage mode rounds down, may cause off-by-one."
```

## Technical Implementation

### Summary Generation Skill

Create `.claude/skills/summary-generator/SKILL.md`:

```markdown
# Summary Generator

Generates hierarchical summaries for code files.

## Usage
Invoked by codemap update process or manually via /update-summaries

## Process
1. Read target file
2. Extract structure (class, methods, fields, annotations)
3. Generate Level 2 (file) summary using template
4. Generate Level 1 (function) summaries for public methods
5. Store in .claude/docs/codemap/summaries/{module}/{ClassName}.md

## Template
[File-level summary template from above]
```

### Storage Structure

```
.claude/docs/codemap/summaries/
├── ECOSYSTEM.md                        # Level 5
├── PLUGIN-SUMMARY.md                   # Level 4
├── enchant/                            # Level 3 + 2
│   ├── MODULE-SUMMARY.md              # Module overview
│   ├── CECaller.md                    # File summary
│   ├── ConditionHealth.md             # File summary
│   └── ...
├── player/
│   ├── MODULE-SUMMARY.md
│   ├── CEPlayer.md
│   └── ...
├── menu/
│   ├── MODULE-SUMMARY.md
│   └── ...
└── ... (other modules)
```

### Cost Estimation for Initial Generation

Using Haiku 4.5 for summarization:

| Component | Files | Input Tokens | Output Tokens | Cost |
|-----------|-------|-------------|---------------|------|
| 550 class summaries | 550 | ~500K | ~50K | ~$0.75 |
| 16 module summaries | 16 | ~15K | ~5K | ~$0.03 |
| 1 plugin summary | 1 | ~5K | ~1K | ~$0.01 |
| **Total** | 567 | ~520K | ~56K | **~$0.80** |

Extremely affordable — this is a one-time cost with incremental updates thereafter.

## Applied to CustomEnchantment

### Level 3 Example: EnchantModule Summary

```markdown
# EnchantModule Summary

**Purpose:** Core enchantment system. Registers all condition checks and effect
executions that power the custom enchantment system.

**Size:** 141 Java files | 30 conditions + 77 effects

**Core Execution Flow:**
Event → Listener → CECallerBuilder → CECaller.call() → per-enchant:
  → CEFunction.check(conditions) → CEFunction.execute(effects)

**Key Classes:**
- `CECaller` — Orchestrator. Iterates equipped enchants, checks conditions, executes effects
- `CECallerBuilder` — Builder pattern for constructing CECaller instances
- `CEEnchant` — Enchantment definition (loaded from YAML)
- `CEFunction` — Single condition+effect pair within an enchant level
- `CEFunctionData` — Execution context (player, entity, damage, type)
- `CEType` — Enum: ATTACK, DEFENSE, MINING, BOW, etc.

**Condition Hooks (30):** ConditionHealth, ConditionEquipSlot, ConditionEntityType,
ConditionPermission, ConditionWorld, ConditionBiome, ... (see CONDITIONS.md for full list)

**Effect Hooks (77):** EffectTrueDamage, EffectAddPotion, EffectTeleport,
EffectParticle, EffectSound, EffectModifyDamage, ... (see EFFECTS.md for full list)

**Dependencies:** ConfigModule (loads enchant YAML), PlayerModule (player enchant data)
**Depended On By:** ListenerModule, TaskModule, MenuModule

**Performance Notes:** CECaller.call() runs on every combat/mining event.
Hot path — avoid allocations. Effects can be sync or async (EffectTaskSeparate).
```

## Dependencies

- **Phase 1** — Architecture maps define the hierarchy structure
- **Phase 2** — Index provides class metadata for summary generation

## Success Criteria

- [ ] Level 5 (Ecosystem) summary fits in 20 lines
- [ ] Level 4 (Plugin) summary fits in 50 lines
- [ ] Level 3 (Module) summaries fit in 20-30 lines each
- [ ] Level 2 (File) summaries fit in 5-15 lines each
- [ ] Level 1 (Function) summaries fit in 1-3 lines each
- [ ] Summaries preserve constraints, edge cases, and API contracts
- [ ] Context loading rules correctly select level based on task type
- [ ] Total summary storage < 200KB
- [ ] Summaries can be regenerated from code (not manually maintained)

---

## GitHub Issue

### Title
`feat: Phase 3 — Hierarchical Context System & Summarization`

### Description

Build a five-level hierarchical summary system (Ecosystem → Plugin → Module → File → Function) that lets Claude understand any part of the codebase at the right granularity. Include context loading rules that automatically select the appropriate level based on the task.

### Background / Motivation

Reading a 500-line Java file costs ~750 tokens. Reading a 15-line summary costs ~25 tokens — a 30x reduction. For a 550-file project, the difference between loading everything vs. loading targeted summaries is the difference between hitting context limits and staying well within budget.

The hierarchical approach means Claude can start broad ("How do enchantments work?") and drill down only where needed, minimizing unnecessary context loading.

### Tasks

- [ ] Define five-level hierarchy (Ecosystem, Plugin, Module, File, Function)
- [ ] Create file-level summary template with all required fields
- [ ] Define summarization rules (what to preserve, what to compress)
- [ ] Implement summary generator skill (`.claude/skills/summary-generator/`)
- [ ] Generate Level 5 (Ecosystem) summary
- [ ] Generate Level 4 (Plugin) summary for CustomEnchantment
- [ ] Generate Level 3 (Module) summaries for all 16 modules
- [ ] Generate Level 2 (File) summaries for key classes (~100 most important)
- [ ] Define context loading rules (task type → required level)
- [ ] Implement context selection logic as a skill or rule
- [ ] Create summary evolution process (auto-generated → enhanced with lessons)
- [ ] Store summaries in `.claude/docs/codemap/summaries/`
- [ ] Validate compression ratios meet targets (10:1 to 20:1)
- [ ] Test: does loading only summaries provide enough context for common tasks?

### Technical Notes

- Use Haiku 4.5 for cost-efficient bulk summarization (~$0.80 for full project)
- File summaries must preserve: constraints, edge cases, API contracts, thread safety notes
- Class names and method names must be verbatim (no paraphrasing identifiers)
- Module summaries should include the execution flow (how data moves through the module)
- Level selection can be implemented as a Claude Code rule that guides context loading

### Acceptance Criteria

- [ ] All 16 modules have Level 3 summaries
- [ ] Top 100 classes have Level 2 summaries
- [ ] Context loading rules correctly escalate from Level 5 to Level 1
- [ ] A developer can understand the enchantment system from Level 3+4 summaries alone
- [ ] Total summary size < 200KB
- [ ] Summaries are auto-generated (no manual writing required)

### Future Improvements

- Automatic summary refresh on code changes (git hook integration)
- Summary quality scoring (detect when summaries become stale)
- User feedback loop (Claude marks which summaries were helpful/missing)
- Natural language query interface ("summarize the combat system")

---
**Priority:** P1 | **Effort:** L | **Labels:** `feature`, `infrastructure`, `phase-3`, `effort:L`
