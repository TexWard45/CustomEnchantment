---
name: iterative-retrieval
description: Pattern for progressively refining context retrieval to solve the subagent context problem
---

# Iterative Retrieval Pattern

Solves the "context problem" in multi-agent workflows where subagents don't know what context they need until they start working.

## The Problem

Subagents are spawned with limited context. They don't know:
- Which files contain relevant code
- What patterns exist in the codebase
- What terminology the project uses

Standard approaches fail:
- **Send everything**: Exceeds context limits
- **Send nothing**: Agent lacks critical information
- **Guess what's needed**: Often wrong

## The Solution: Iterative Retrieval

A 4-phase loop that progressively refines context:

```
┌─────────────────────────────────────────────┐
│                                             │
│   ┌──────────┐      ┌──────────┐            │
│   │ DISPATCH │─────▶│ EVALUATE │            │
│   └──────────┘      └──────────┘            │
│        ▲                  │                 │
│        │                  ▼                 │
│   ┌──────────┐      ┌──────────┐            │
│   │   LOOP   │◀─────│  REFINE  │            │
│   └──────────┘      └──────────┘            │
│                                             │
│        Max 3 cycles, then proceed           │
└─────────────────────────────────────────────┘
```

### Phase 1: DISPATCH

Initial broad query to gather candidate files:

```
Initial query:
  patterns: ['**/src/main/java/**/*.java']
  keywords: ['PlayerData', 'manager', 'listener']
  excludes: ['*Test.java', '**/test/**']

Dispatch to retrieval agent → get candidate files
```

### Phase 2: EVALUATE

Assess retrieved content for relevance:

```
For each file, score relevance:
  path: file path
  relevance: 0.0 - 1.0
  reason: why relevant
  missingContext: what gaps remain
```

Scoring criteria:
- **High (0.8-1.0)**: Directly implements target functionality
- **Medium (0.5-0.7)**: Contains related patterns or types
- **Low (0.2-0.4)**: Tangentially related
- **None (0-0.2)**: Not relevant, exclude

### Phase 3: REFINE

Update search criteria based on evaluation:

```
Refined query:
  patterns: [previous + new patterns discovered]
  keywords: [previous + terminology found in codebase]
  excludes: [previous + confirmed irrelevant paths]
  focusAreas: [identified gaps to fill]
```

### Phase 4: LOOP

Repeat with refined criteria (max 3 cycles):

```
For each cycle (max 3):
  1. Retrieve files with current query
  2. Evaluate relevance of each file
  3. Check: have 3+ high-relevance files with no critical gaps?
     Yes → return results
     No  → refine query and continue
```

## Practical Examples

### Example 1: Bug Fix Context

```
Task: "Fix NPE in PlayerManager when player disconnects during async task"

Cycle 1:
  DISPATCH: Search for "PlayerManager", "disconnect", "async" in src/main/java/**
  EVALUATE: Found PlayerManager.java (0.9), PlayerData.java (0.7), PlayerListener.java (0.3)
  REFINE: Add "ConcurrentHashMap", "runTask" keywords; focus on thread safety

Cycle 2:
  DISPATCH: Search refined terms
  EVALUATE: Found AsyncPlayerHandler.java (0.85), AbstractPerTickTask.java (0.6)
  REFINE: Sufficient context (3 high-relevance files)

Result: PlayerManager.java, PlayerData.java, AsyncPlayerHandler.java
```

### Example 2: Feature Implementation

```
Task: "Add custom enchant system using StrategyRegister"

Cycle 1:
  DISPATCH: Search "StrategyRegister", "enchant", "strategy" in src/main/java/**
  EVALUATE: Found StrategyRegister.java (0.9), ConditionHook.java (0.7) - reveals naming pattern
  REFINE: Add "SingletonRegister", "getIdentify" keywords

Cycle 2:
  DISPATCH: Search refined terms
  EVALUATE: Found ExecuteHook.java (0.8), MenuRegister.java (0.75)
  REFINE: Sufficient context - understand the registration pattern

Result: StrategyRegister.java, ConditionHook.java, ExecuteHook.java, MenuRegister.java
```

## Integration with Agents

Use in agent prompts:

```markdown
When retrieving context for this task:
1. Start with broad keyword search
2. Evaluate each file's relevance (0-1 scale)
3. Identify what context is still missing
4. Refine search criteria and repeat (max 3 cycles)
5. Return files with relevance >= 0.7
```

## Best Practices

1. **Start broad, narrow progressively** - Don't over-specify initial queries
2. **Learn codebase terminology** - First cycle often reveals naming conventions
3. **Track what's missing** - Explicit gap identification drives refinement
4. **Stop at "good enough"** - 3 high-relevance files beats 10 mediocre ones
5. **Exclude confidently** - Low-relevance files won't become relevant

## Related

- Agent definitions in `.claude/agents/`
- `strategic-compact` skill - For managing context window
