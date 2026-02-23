# Phase 9 — Performance & Cost Optimization

## Purpose

Systematically reduce token usage, avoid context noise, and maximize the value of every token spent. Target: 50-70% reduction in average tokens per task compared to current baseline.

## Design

### Cost Baseline Measurement

Before optimizing, measure current costs:

```
Metrics to track:
├── Tokens per task (input + output)
├── Files read per task
├── Cache hit rate (same file read twice in session)
├── Wasted reads (files read but not used in solution)
├── Agent overhead (tokens spent on agent coordination)
└── Context noise ratio (irrelevant tokens / total tokens)
```

### Optimization Strategies

#### Strategy 1: Conditional Rule Loading (Immediate Win)

Move module-specific rules to path-scoped rules:

```yaml
# .claude/rules/enchant-module.md
---
paths:
  - "src/main/java/**/enchant/**"
  - "src/main/resources/enchantment/**"
---
# EnchantModule Conventions
- All conditions extend ConditionHook
- All effects extend EffectHook
- Register in EnchantModule.onEnable()
- Identifier format: SCREAMING_SNAKE_CASE
```

This rule only loads when Claude works on enchantment files, saving tokens on all other tasks.

**Token savings: ~2,000 tokens per non-enchant task**

#### Strategy 2: Skills Over Rules (Medium Win)

Move large reference docs from always-loaded rules to on-demand skills:

| Currently (always loaded) | Move to (loaded on demand) |
|---------------------------|---------------------------|
| game-server-performance.md (400+ lines) | `.claude/skills/perf-check/SKILL.md` |
| github-issues.md (500+ lines) | `.claude/skills/issue-creator/SKILL.md` |
| phased-implementation.md (400+ lines) | `.claude/skills/phased-impl/SKILL.md` |
| bukkit-testing.md (200+ lines) | `.claude/skills/testing-guide/SKILL.md` |

Only the skill description (2-3 lines) loads at startup. Full content loads on invocation.

**Token savings: ~8,000 tokens per session for non-matching tasks**

#### Strategy 3: Summary-First Reading (Core Optimization)

```
BEFORE (current):
  Task: "Fix NPE in ConditionHealth"
  → Read ConditionHealth.java (500 tokens)
  → Read ConditionHook.java (800 tokens)
  → Read CECaller.java (2000 tokens)
  → Read CEFunctionData.java (600 tokens)
  → Read PlayerListener.java (1500 tokens)
  Total: ~5,400 tokens just for reading

AFTER (with summaries):
  Task: "Fix NPE in ConditionHealth"
  → Read ConditionHealth.md summary (50 tokens)
  → Read CECaller.md summary (80 tokens) → not relevant, skip full read
  → Read ConditionHealth.java (500 tokens) → only the target file
  Total: ~630 tokens for reading (88% reduction)
```

#### Strategy 4: Incremental Context Loading

```pseudocode
# Load in stages, not all at once

Stage 1: Orientation (always)
  load(PLUGIN-SUMMARY.md)   # 300 tokens
  load(relevant MODULE-SUMMARY.md)  # 200 tokens

Stage 2: Targeted (based on task analysis)
  for file in relevantFiles[:3]:
    load(file.summary)  # 50-100 tokens each
  # Total: ~150-300 tokens

Stage 3: Deep dive (only if needed)
  for file in criticalFiles[:2]:
    load(file.fullContent)  # 300-1000 tokens each
  # Total: ~600-2000 tokens

# Stages 1+2 often sufficient for questions and simple fixes
# Stage 3 only for implementation
```

#### Strategy 5: Smart Model Selection

```
Task Classification → Model Selection:

Simple/routine → Haiku ($1/$5 per M tokens)
  - Grep searches, file reads, simple edits
  - Subagent tasks, boilerplate generation
  - Test running, build verification

Standard development → Sonnet ($3/$15 per M tokens)
  - Feature implementation, bug fixes
  - Code review, TDD cycles
  - Most development work (90%)

Complex architecture → Opus ($5/$25 per M tokens)
  - Architectural decisions
  - Cross-module refactoring
  - Deep debugging of subtle issues
```

#### Strategy 6: Parallel Subagent Delegation

Delegate verbose operations to subagents (isolated context):

```
Main conversation context: 20,000 tokens

Instead of reading test output in main context (+5,000 tokens):
  → Delegate to subagent: "Run tests and report pass/fail summary"
  → Subagent returns: "All 45 tests pass. Coverage: 82%." (+50 tokens)
  Savings: 4,950 tokens

Instead of exploring codebase in main context (+15,000 tokens):
  → Delegate to Explore agent: "Find all classes that handle damage"
  → Agent returns: list of 8 files with summaries (+500 tokens)
  Savings: 14,500 tokens
```

#### Strategy 7: Hook-Based Output Filtering

```json
// .claude/settings.local.json
{
  "hooks": {
    "PostToolUse": [
      {
        "matcher": "Bash",
        "command": "echo 'If output > 100 lines, summarize key results only'"
      }
    ]
  }
}
```

#### Strategy 8: Cache-Aware Context Loading

```pseudocode
function loadWithCache(file):
  # Check if file was already read in this session
  if file in sessionCache:
    return sessionCache[file]  # 0 new tokens

  # Check if file unchanged since last session
  if fileCache.isUnchanged(file):
    return fileCache.getSummary(file)  # Load summary, not full file

  # File changed — read fresh
  content = read(file)
  sessionCache[file] = content
  return content
```

### Projected Savings

| Strategy | Token Savings | Effort |
|----------|--------------|--------|
| Conditional rules | 2,000/task | Low |
| Skills over rules | 8,000/session | Low |
| Summary-first reading | 50-80% of reads | Medium (needs Phase 3) |
| Incremental loading | 30-50% of context | Medium (needs Phase 5) |
| Smart model selection | 40-60% cost | Low |
| Subagent delegation | 70-90% per delegation | Low |
| Hook output filtering | 20-30% of verbose output | Low |
| Cache-aware loading | 30-50% of repeated reads | Medium (needs Phase 6) |

**Combined estimated savings: 50-70% reduction in tokens per task**

### Cost Targets

| Metric | Current (Est.) | Target |
|--------|---------------|--------|
| Avg tokens per simple task | ~50K | ~15K |
| Avg tokens per medium task | ~150K | ~50K |
| Avg tokens per large task | ~300K | ~100K |
| Daily cost (active development) | ~$8 | ~$3 |
| Monthly cost | ~$160 | ~$60 |

## Implementation

### Quick Wins (Implement Immediately)

1. Move large rules to skills (30 min work, immediate savings)
2. Add path-scoped rules for module-specific guidance
3. Use Haiku for all subagent tasks
4. `/clear` between unrelated tasks

### Medium-Term (After Phases 3-5)

5. Summary-first reading protocol
6. Incremental context loading
7. Cache-aware context management

### Measurement

Create a cost tracking rule:

```markdown
# .claude/rules/cost-awareness.md

## Token Conservation
- Read summaries before full files
- Use subagents for verbose operations (test running, log analysis)
- Clear context between unrelated tasks
- Check if information is already in loaded summaries before reading new files
- For questions: often summaries are sufficient, don't read full files
```

## Dependencies

- **Phase 3** — Summaries enable summary-first reading
- **Phase 5** — Context engine enables incremental loading
- **Phase 6** — Caching enables cache-aware loading

## Success Criteria

- [ ] Path-scoped rules implemented for module-specific guidance
- [ ] Large reference docs moved to on-demand skills
- [ ] Subagent tasks use Haiku model
- [ ] Token usage measurably reduced (track via /cost)
- [ ] No quality degradation from optimization
- [ ] Cost tracking rule in place

---

## GitHub Issue

### Title
`feat: Phase 9 — Performance & Cost Optimization`

### Description

Systematically reduce token usage across all AI operations. Implement 8 optimization strategies targeting 50-70% reduction in average tokens per task without quality degradation.

### Background / Motivation

Token costs scale linearly with usage. Every optimization that reduces tokens per task directly reduces cost. More importantly, lower token usage means more headroom within context windows, enabling longer sessions and more complex tasks before hitting limits.

### Tasks

**Quick Wins (No Dependencies)**
- [ ] Move game-server-performance.md to on-demand skill (400+ lines saved)
- [ ] Move github-issues.md to on-demand skill (500+ lines saved)
- [ ] Move phased-implementation.md to on-demand skill (400+ lines saved)
- [ ] Add path-scoped rules for EnchantModule, PlayerModule, MenuModule
- [ ] Configure subagent tasks to use Haiku model
- [ ] Add cost-awareness rule at `.claude/rules/cost-awareness.md`

**After Phase 3**
- [ ] Implement summary-first reading protocol
- [ ] Add rule: "read summary before full file"

**After Phase 5**
- [ ] Implement incremental context loading
- [ ] Define token budgets per workflow size

**After Phase 6**
- [ ] Implement cache-aware context loading
- [ ] Track cache hit rates

**Measurement**
- [ ] Baseline: measure tokens per task before optimization
- [ ] Track token usage weekly via /cost
- [ ] Compare: tokens per task after each optimization round

### Acceptance Criteria

- [ ] Average tokens per simple task < 20K (from ~50K baseline)
- [ ] Path-scoped rules only load when relevant files are active
- [ ] No quality degradation (same test pass rates, same code review scores)
- [ ] Quick wins implemented within Phase 9 (no dependency on other phases)

---
**Priority:** P2 | **Effort:** M | **Labels:** `feature`, `infrastructure`, `phase-9`, `effort:M`
