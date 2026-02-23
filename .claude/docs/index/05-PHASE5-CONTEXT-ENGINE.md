# Phase 5 — Context Selection Engine

## Purpose

The brain of the pipeline. Analyzes incoming tasks and automatically decides what context to load — which summaries, which index entries, which files — to minimize tokens while maximizing relevance.

## Design

### Context Selection Flow

```
User Task
    │
    ▼
┌──────────────────────┐
│  Task Analyzer        │  Classify: question, bugfix, feature, refactor, cross-cutting
│  (rule-based + LLM)  │  Extract: keywords, target modules, target files
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│  Relevance Scorer     │  Score each module/file against task requirements
│                       │  Use: tags, dependencies, summaries, semantic search
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│  Token Budget Planner │  Allocate token budget across context levels
│                       │  Priority: task-critical > dependencies > reference
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│  Context Assembler    │  Load selected summaries, index entries, files
│                       │  Order: most relevant first (avoid lost-in-middle)
└──────────────────────┘
```

### Task Classification

```pseudocode
function classifyTask(task):
  # Keywords indicating task type
  questionKeywords = ["what", "why", "how", "explain", "show", "list", "where", "describe"]
  bugfixKeywords = ["fix", "bug", "error", "NPE", "exception", "broken", "crash"]
  featureKeywords = ["add", "create", "implement", "new", "build"]
  refactorKeywords = ["refactor", "clean", "improve", "optimize", "move", "rename"]

  taskType = classify(task, [questionKeywords, bugfixKeywords, featureKeywords, refactorKeywords])

  # Extract target scope
  targetModules = []
  for module in allModules:
    if module.name.lower() in task.lower():
      targetModules.append(module)

  targetFiles = []
  for className in extractClassNames(task):
    if className in index:
      targetFiles.append(index[className].file)

  # Estimate scope size
  if len(targetFiles) > 0:
    scope = "narrow"      # Specific files mentioned
  elif len(targetModules) > 0:
    scope = "module"      # Module-level work
  elif taskType == "question":
    scope = "broad"       # High-level question
  else:
    scope = "discovery"   # Need to find relevant code first

  return TaskClassification(taskType, targetModules, targetFiles, scope)
```

### Token Budget Allocation

```
Total context budget: 30,000 tokens (safe operating zone)

Allocation by task scope:

NARROW (specific file/class mentioned):
  ├── System prompt + rules:    8,000 tokens (fixed)
  ├── Target file(s):          10,000 tokens (full content)
  ├── Dependency summaries:     5,000 tokens (Level 2 summaries)
  ├── Module context:           3,000 tokens (Level 3 summary)
  └── Buffer for conversation:  4,000 tokens
  Total: 30,000

MODULE (module-level work):
  ├── System prompt + rules:    8,000 tokens (fixed)
  ├── Module summary:           2,000 tokens (Level 3)
  ├── File summaries:           8,000 tokens (Level 2, top 20 files)
  ├── Key files (full):         6,000 tokens (2-3 most relevant)
  ├── Cross-module deps:        2,000 tokens (Level 3 of deps)
  └── Buffer for conversation:  4,000 tokens
  Total: 30,000

BROAD (high-level question):
  ├── System prompt + rules:    8,000 tokens (fixed)
  ├── Plugin summary:           2,000 tokens (Level 4)
  ├── All module summaries:     5,000 tokens (Level 3, all 16)
  ├── Relevant details:         5,000 tokens (Level 2 for relevant modules)
  └── Buffer for conversation: 10,000 tokens
  Total: 30,000

DISCOVERY (need to find relevant code):
  ├── System prompt + rules:    8,000 tokens (fixed)
  ├── Plugin summary:           2,000 tokens (Level 4)
  ├── Search results:           5,000 tokens (semantic search top 10)
  ├── Result summaries:         5,000 tokens (Level 2 for search hits)
  └── Buffer for conversation: 10,000 tokens
  Total: 30,000
```

### Relevance Scoring

```pseudocode
function scoreRelevance(entity, task):
  score = 0.0

  # Direct mention (highest signal)
  if entity.name in task.text:
    score += 10.0

  # Tag match
  taskTags = extractTags(task)
  matchingTags = intersection(entity.tags, taskTags)
  score += len(matchingTags) * 2.0

  # Dependency proximity (if editing file A, nearby files matter)
  if task.targetFiles:
    for target in task.targetFiles:
      distance = dependencyGraph.shortestPath(entity, target)
      if distance <= 2:
        score += 5.0 / distance

  # Semantic similarity (from Phase 4)
  if semanticSearch.available:
    similarity = semanticSearch.score(task.text, entity.summary)
    score += similarity * 3.0

  # Recency (recently modified files are more relevant for bugfixes)
  if task.type == "bugfix":
    daysAgo = daysSince(entity.lastModified)
    if daysAgo < 7:
      score += 2.0

  return score
```

## Technical Implementation

### As a Claude Code Rule

The simplest implementation is a `.claude/rules/context-loading.md` rule that instructs Claude on context selection:

```markdown
# Context Loading Strategy

## Before Starting Any Task

1. **Classify the task**: question, bugfix, feature, refactor
2. **Identify scope**: specific files, module, broad, discovery
3. **Load context in order**:

### For specific files/classes mentioned:
- Read the target file(s) directly
- Read Level 2 summaries of dependencies (.claude/docs/codemap/summaries/)
- Read the Module summary for context

### For module-level work:
- Read the Module summary first (.claude/docs/codemap/summaries/{module}/MODULE-SUMMARY.md)
- Read Level 2 summaries to identify relevant files
- Only read full files when you need implementation details

### For broad questions:
- Read PLUGIN-SUMMARY.md for overview
- Read relevant MODULE-SUMMARY.md files
- Do NOT read individual files unless specifically needed

### For discovery (don't know where to look):
- Read PLUGIN-SUMMARY.md
- Use semantic search (MCP) or index tags to find relevant code
- Read summaries of search results before reading full files

## Token Conservation Rules
- NEVER read more than 5 full files in one task
- ALWAYS read summaries before full files
- If a summary answers the question, don't read the full file
- For cross-module work, read module summaries before any file
```

### As a Claude Code Skill (More Sophisticated)

```markdown
# Context Selector Skill

## Trigger
Automatically invoked at task start (via hook or manual /context command)

## Process
1. Analyze task text for keywords, class names, module names
2. Classify task type and scope
3. Query index for tag matches (Phase 2)
4. Query semantic search for meaning matches (Phase 4)
5. Score and rank all candidates
6. Select context within token budget
7. Output: ordered list of files/summaries to load

## Output Format
Context Plan:
- LOAD: .claude/docs/codemap/summaries/enchant/MODULE-SUMMARY.md (module overview)
- LOAD: .claude/docs/codemap/summaries/enchant/ConditionHealth.md (target class summary)
- READ: src/main/java/.../ConditionHealth.java (implementation needed)
- SKIP: All other modules (not relevant)
- Budget: ~8,000 tokens of context loaded (within 30K budget)
```

## Applied to Minecraft Plugin Tasks

### Example: "Add a new condition that checks player's mana"

```
Classification: feature, module scope (EnchantModule)
Scope: module (no specific files mentioned)

Context loaded:
1. Level 3: EnchantModule summary (how conditions work)
2. Level 2: ConditionHealth.md (example pattern to follow)
3. Level 2: ConditionPermission.md (another example)
4. Full file: ConditionHealth.java (template for new condition)
5. Level 3: PlayerModule summary (where mana data lives)
6. Config: enchantment YAML example showing condition syntax

Token budget: ~12,000 tokens of context
Without engine: would have loaded 30+ files, ~60,000 tokens
Savings: 80% token reduction
```

### Example: "Fix NPE in CECaller when player disconnects during combat"

```
Classification: bugfix, narrow scope (CECaller mentioned)
Scope: narrow (specific class)

Context loaded:
1. Full file: CECaller.java (the bug is here)
2. Level 2: CEFunctionData.md (execution context, may contain null player)
3. Level 2: PlayerListener.md (handles disconnect)
4. Level 3: TaskModule summary (async effects might reference stale player)
5. Issue docs: search for similar NPE fixes in .claude/docs/issues/

Token budget: ~15,000 tokens
Without engine: would read CECaller + all its dependencies, ~40,000 tokens
Savings: 60% token reduction
```

## Dependencies

- **Phase 3** — Hierarchical summaries are the primary context source
- **Phase 2** — Index enables tag-based and dependency-based relevance scoring
- **Phase 4** (optional) — Semantic search enhances discovery mode

## Success Criteria

- [ ] Task classifier correctly categorizes 90% of test tasks
- [ ] Relevance scorer ranks correct files in top 5 for 90% of tasks
- [ ] Token usage reduced by 50%+ compared to unguided context loading
- [ ] Context loading rule implemented and tested with 10 example tasks
- [ ] No false negatives (critical context never omitted)
- [ ] Works without semantic search (Phase 4 is optional enhancement)

---

## GitHub Issue

### Title
`feat: Phase 5 — Context Selection Engine with Smart Loading Rules`

### Description

Build the intelligence layer that analyzes incoming tasks and automatically selects the optimal context to load. This is the "brain" that decides what Claude should know before starting work, minimizing token usage while ensuring all necessary context is available.

### Background / Motivation

Phases 1-4 created the knowledge infrastructure (maps, index, summaries, search). Phase 5 connects that infrastructure to actual development tasks. Without a selection engine, Claude either loads too much (wasting tokens) or too little (missing context and making mistakes).

The engine must handle four task types (question, bugfix, feature, refactor) across four scopes (narrow, module, broad, discovery) and produce a context plan within a token budget.

### Tasks

- [ ] Define task classification rules (question, bugfix, feature, refactor)
- [ ] Define scope detection logic (narrow, module, broad, discovery)
- [ ] Define token budget allocation by scope type
- [ ] Implement relevance scoring function (direct mention, tags, dependencies, recency)
- [ ] Create context loading rule at `.claude/rules/context-loading.md`
- [ ] Create context selector skill at `.claude/skills/context-selector/`
- [ ] Test with 10 representative tasks across all task types
- [ ] Measure token savings compared to unguided loading
- [ ] Document decision logic in `.claude/docs/index/05-PHASE5-CONTEXT-ENGINE.md`
- [ ] Validate: no false negatives (critical context never omitted)

### Acceptance Criteria

- [ ] 90% correct task classification on test set
- [ ] 50%+ token reduction compared to baseline
- [ ] Context plans generated in < 5 seconds
- [ ] No critical context omissions in 10 test scenarios
- [ ] Rule-based implementation works without semantic search (Phase 4 optional)

---
**Priority:** P2 | **Effort:** M | **Labels:** `feature`, `infrastructure`, `phase-5`, `effort:M`
