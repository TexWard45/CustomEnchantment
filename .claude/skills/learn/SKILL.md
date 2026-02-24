---
name: learn
description: Review session and extract learnings into MEMORY.md. Use before ending a productive session.
---

# Session Learning Extraction

Review this session and capture reusable learnings. This prevents repeating the same mistakes across sessions.

## Steps

### 1. Read Current Memory
- Read the auto memory MEMORY.md file
- Note what's already documented to avoid duplicates

### 2. Review Session For Learnings

Scan the conversation for:

**API Misuse:**
- Wrong method called (e.g., `ColorUtils.color()` instead of `.t()`)
- Wrong class used (e.g., `CECraftItemStackNMS` instead of `CEItemRegister` for feature code)
- Inline strings instead of `CEMessageKey` + `messages.yml`
- Missing null checks, wrong import packages

**Convention Violations:**
- Created new file when existing file should have been modified
- Put message text in `config.yml` instead of `messages.yml`
- Used mutable class instead of `record` (Java 21)
- Inline placeholder strings instead of `CEConstants.Placeholder`

**Build/Test Failures:**
- What failed and why
- What the fix was
- Root cause (missing dependency, wrong API, etc.)

**New Patterns Discovered:**
- Approaches that worked well
- Architectural decisions worth remembering
- Cross-module integration patterns

### 3. Categorize and Write

Update MEMORY.md with concise entries grouped by:

```markdown
## API Rules
- [Rule]: [Correct usage] (not [wrong usage])

## Patterns
- [Pattern name]: [Brief description]

## Pitfalls
- [What to avoid]: [Why]
```

### 4. Check for Documentation Gaps

If a learning reveals a missing `.claude/rules/` file:
- Note it in MEMORY.md under "## Documentation Gaps"
- Suggest creating a GitHub issue

## Rules

- ONLY add verified learnings (confirmed by build/test results or explicit correction)
- Keep each entry to 1-2 lines max
- Do NOT add session-specific context (current task, file paths being worked on)
- Do NOT duplicate existing CLAUDE.md or .claude/rules/ instructions
- Keep MEMORY.md under 200 lines total
- Remove outdated entries if they conflict with new learnings
- When in doubt, don't add it — only add patterns confirmed across multiple interactions
