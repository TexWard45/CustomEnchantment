---
name: workflow-xs
description: Quick fix workflow for trivial changes. 1 file, <20 lines. No planning, no agents. Completes in <5 minutes with <3K tokens.
---

# XS Workflow: Quick Fix

For single-file changes under 20 lines. Typo fixes, log additions, default value changes, variable renames.

## Context Budget: 3,000 tokens max

## Steps

### 1. RETRIEVE (<1 min)
- Read the target file directly (user specified or obvious from context)
- No summaries needed — go straight to the source

### 2. MODIFY (<3 min)
- Make the change
- Keep it minimal — do not refactor surrounding code

### 3. VALIDATE (<1 min)
- Run affected module tests: `./gradlew :{module}:test`
- If no tests exist for this change, skip (do not write tests for XS changes)

## Rules

- **No planning** — just do it
- **No agents** — no code-reviewer, no planner
- **No summaries** — read the file directly
- **No new files** — XS changes modify existing files only
- **No scope creep** — if you discover the change is bigger, escalate to S or M

## Examples

| Task | Action |
|------|--------|
| "Fix typo in EnchantModule" | Read file, fix typo, run tests |
| "Change default max-count to 20" | Read config class, update default, run tests |
| "Add log line in CECaller" | Read file, add log, run tests |
| "Rename variable in PlayerListener" | Read file, rename, run tests |

## Escalation

If any of these are true, switch to `/workflow-s`:
- Change touches 2+ files
- Change requires understanding a pattern first
- A test needs to be written
