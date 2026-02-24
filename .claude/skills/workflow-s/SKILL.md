---
name: workflow-s
description: Pattern-following workflow for small tasks. 1-2 files, follows existing pattern. TDD with code review. Context budget 8K tokens.
---

# S Workflow: Pattern-Following

For tasks that follow an existing pattern in the codebase. New conditions, effects, config fields, simple bugfixes.

## Context Budget: 8,000 tokens max

## Steps

### 1. RETRIEVE (<5 min)
1. Read `PLUGIN-SUMMARY.md` (or skip if already loaded)
2. Read module summary for the target module
3. Read one existing example file as a pattern template
4. Optionally use MCP `search_code` to find the best example

### 2. PLAN (<5 min)
- Identify the pattern to follow
- List files to create/modify (should be 1-2)
- No planner agent needed — the pattern IS the plan

### 3. TEST FIRST (TDD)
- Write test based on existing pattern's test file
- Run test — verify it fails (RED)

### 4. IMPLEMENT (<30 min)
- Follow the pattern exactly
- Register in module's `onEnable()` if applicable
- Run test — verify it passes (GREEN)

### 5. VALIDATE
- Run module tests: `./gradlew :{module}:test`
- Run **code-reviewer** agent on the changed files
- Fix any CRITICAL or HIGH issues

## Agents Used

| Agent | When |
|-------|------|
| code-reviewer | After implementation (mandatory) |

## Examples

| Task | Pattern Source | Files |
|------|--------------|-------|
| New enchant condition | Existing `*Condition.java` | 1 source + 1 test |
| New enchant effect | Existing `*Effect.java` | 1 source + 1 test |
| New config field | Existing `*Config.java` | 1 source |
| Simple NPE bugfix | Stack trace points to file | 1 source + 1 test |
| New player expansion | Existing `*Expansion.java` | 1 source + 1 test |

## Escalation

If any of these are true, switch to `/workflow-m`:
- No clear pattern to follow
- 3+ files need modification
- Requires understanding cross-class interactions
- Planner agent is needed to break down the work
