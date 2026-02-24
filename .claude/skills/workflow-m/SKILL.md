---
name: workflow-m
description: Module feature workflow for medium tasks. 3-5 files within one module. Uses planner, TDD, and code review. Context budget 15K tokens.
---

# M Workflow: Module Feature

For features or refactors that stay within one module but involve multiple classes. Needs upfront planning and TDD.

## Context Budget: 15,000 tokens max

## Steps

### 1. RETRIEVE (<10 min)
1. Read `PLUGIN-SUMMARY.md`
2. Run `/context-selector` or read the target module summary directly
3. Read file summaries for all relevant classes (from cache or module summary)
4. Identify cross-module dependencies (check module dependency table)
5. Read full source for 2-3 key classes

### 2. PLAN (planner agent)
1. Invoke **planner** agent with:
   - Task description
   - Module summary context
   - Existing patterns to follow
2. Break feature into implementation steps
3. Identify risks and edge cases
4. Define success criteria
5. Get user approval before proceeding

### 3. IMPLEMENT (TDD per step)

For each implementation step from the plan:

```
a. Write test (RED)
   - Follow testing patterns from bukkit-testing.md
   - Use MockBukkit for Bukkit classes
   - Cover edge cases

b. Implement (GREEN)
   - Write minimal code to pass the test
   - Follow java-conventions.md naming
   - Follow module-design.md patterns

c. Refactor (IMPROVE)
   - Clean up without changing behavior
   - Ensure conventions are followed
```

### 4. INTEGRATION
- Run full module tests: `./gradlew :{module}:test`
- Run dependent module tests if applicable
- Verify 80%+ coverage: `./gradlew :{module}:test jacocoTestReport`

### 5. REVIEW
- Run **code-reviewer** agent on all changed files
- Run **security-reviewer** agent if user input is involved
- Fix CRITICAL and HIGH issues
- Fix MEDIUM issues when practical

## Agents Used

| Agent | When | Model |
|-------|------|-------|
| planner | Step 2 — before implementation | sonnet |
| code-reviewer | Step 5 — after implementation | sonnet |
| security-reviewer | Step 5 — if user input involved | sonnet |

## Examples

| Task | Module | Files |
|------|--------|-------|
| New item type with factory | ItemModule | Factory + Config + Tests (~4 files) |
| Add enchant trigger system | EnchantModule | Trigger + Handler + Config + Tests (~5 files) |
| Player data expansion group | PlayerModule | Expansion + Manager + Tests (~4 files) |
| Menu section refactor | MenuModule | 3-4 menu classes refactored |

## Escalation

If any of these are true, switch to `/workflow-l`:
- Changes cross module boundaries
- 6+ files need modification
- An architect review is needed for design decisions
- Multiple integration points between systems
