---
name: workflow-l
description: Cross-module workflow for large features. 5-15 files across modules. Uses architect, planner, phased TDD, and full review. Context budget 20K/phase.
---

# L Workflow: Cross-Module Feature

For features that cross module boundaries. New systems, major refactors, cross-cutting concerns. Requires architecture review and phased implementation.

## Context Budget: 20,000 tokens per phase

## Steps

### 1. DISCOVER (<15 min)
1. Read `PLUGIN-SUMMARY.md`
2. Use MCP `search_code` to find all relevant code
3. Read module summaries for all affected modules (max 4)
4. Use MCP `analyze_impact` on the primary class being changed
5. Identify all touch points across modules

### 2. ARCHITECT (architect agent)
1. Invoke **architect** agent with:
   - Task description
   - Affected module summaries
   - Impact analysis results
2. Design component interactions
3. Define module boundary contracts
4. Document API contracts between modules
5. Get user approval on the architecture

### 3. PLAN (planner agent)
1. Invoke **planner** agent with:
   - Architecture from Step 2
   - Task description
2. Phase the implementation (typically 3-4 phases):
   - Phase 1: Core data classes and interfaces
   - Phase 2: Business logic implementation
   - Phase 3: Integration (listeners, commands, configs)
   - Phase 4: Tests and validation
3. Create task checklist
4. Get user approval on the plan

### 4. IMPLEMENT (phased TDD)

For each phase:

```
a. Load ONLY that phase's context
   - Read files relevant to this phase
   - Stay within 20K token budget

b. TDD cycle per implementation step
   - Write test (RED)
   - Implement (GREEN)
   - Refactor (IMPROVE)

c. Phase review
   - Run code-reviewer agent on phase changes
   - Run module tests for affected modules

d. Update context for next phase
   - Note what was created/changed
   - Identify what next phase needs to know
```

### 5. VALIDATE
- Full test suite: `./gradlew test`
- Coverage report: `./gradlew jacocoTestReport`
- Run **security-reviewer** agent (mandatory for L+)
- Performance check for hot-path code
- Impact analysis: verify no regressions in other modules

### 6. DOCUMENT
- Update module summaries if structure changed
- Document new patterns in session memory
- Suggest running `/cache-update` to refresh file cache

## Agents Used

| Agent | When | Model |
|-------|------|-------|
| architect | Step 2 — architecture design | opus |
| planner | Step 3 — implementation phasing | sonnet |
| tdd-guide | Step 4 — per-phase TDD | sonnet |
| code-reviewer | Step 4c, Step 5 — after each phase | sonnet |
| security-reviewer | Step 5 — final security review | sonnet |

## Phase Context Management

Each phase resets context to stay within budget:

| Phase | Context Loaded |
|-------|---------------|
| 1: Data | PLUGIN-SUMMARY + affected module summaries + data class sources |
| 2: Logic | Phase 1 output summary + manager/service sources |
| 3: Integration | Phase 2 output summary + listener/command sources |
| 4: Tests | Phase 3 output summary + test templates |

Use `/strategic-compact` between phases if context is getting large.

## Escalation

If any of these are true, switch to `/workflow-xl`:
- A new module needs to be created
- 16+ files are affected
- The change is architectural (framework-level patterns change)
