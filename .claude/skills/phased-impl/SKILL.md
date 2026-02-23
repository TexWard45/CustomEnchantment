---
name: phased-impl
description: Guidelines for multi-phase feature implementation with knowledge inheritance. Use when starting a new phase of a multi-phase feature. Ensures lessons from previous phases are applied and documented.
---

# Phased Implementation & Knowledge Inheritance

## Overview

When implementing features across multiple phases, each phase MUST reference and inherit lessons from previous phases to avoid repeating mistakes.

---

## Rule: Document & Reference Previous Phases

### BEFORE starting a new phase:

1. **Check if previous phase documentation exists:**
   ```bash
   ls .claude/docs/issues/<issue-number>/
   ```

2. **Read the master document:**
   ```
   .claude/docs/issues/<issue-number>/issue-<number>-<name>.md
   ```

3. **Extract applicable lessons:**
   - Patterns that worked well
   - Pitfalls encountered
   - Process improvements
   - Testing strategies

4. **Reference in current work:**
   - Cite specific lessons in implementation plan
   - Apply patterns from previous phase
   - Avoid known pitfalls

---

## Documentation Structure

### For Each Issue/Feature

```
.claude/docs/issues/<issue-number>/
├── issue-<number>-<name>.md           (Master index - READ THIS FIRST)
├── <TOPIC>_RETROSPECTIVE.md           (What went wrong/right)
├── <TOPIC>_LESSONS.md                 (Extracted principles)
├── <TOPIC>_PATTERNS.md                (Reusable code patterns)
├── <TOPIC>_COMPARISON.md              (Technical comparisons)
└── <TOPIC>_EDGE_CASES.md              (Test documentation)
```

### Master Document Must Include

```markdown
# Issue #<number>: <Feature Name> - Phase <N>

## Previous Phases (if applicable)
- Phase 1: [Link to issue-<number>] - Key learnings: X, Y, Z

## Lessons Applied from Previous Phases
- Applied pattern X from Phase 1 (see issue-<number>/PATTERNS.md)
- Avoided pitfall Y from Phase 2 (see issue-<number>/RETROSPECTIVE.md)

## New Lessons for Future Phases
- Learned: <new insight>
- Pattern: <new pattern>
- Pitfall: <new mistake to avoid>
```

---

## Checklist: Starting New Phase

Before coding ANY new phase:

- [ ] **Check for previous phase docs:**
  - [ ] Read `.claude/docs/issues/<prev-number>/issue-*.md`
  - [ ] Extract applicable patterns
  - [ ] Note pitfalls to avoid

- [ ] **Reference previous lessons:**
  - [ ] List which patterns applying
  - [ ] List which pitfalls avoiding
  - [ ] State expected improvement

- [ ] **Plan current phase:**
  - [ ] Document new requirements
  - [ ] Apply previous patterns
  - [ ] Identify new challenges

- [ ] **Create documentation:**
  - [ ] Create `.claude/docs/issues/<current-number>/`
  - [ ] Create master document
  - [ ] Reference previous phases

---

## Documentation During Implementation

### Commit Message Template for Bug Fixes

```
fix: [Brief description of what was fixed]

Problem:
- [What went wrong - be specific]

Root Cause:
- [Why it happened - technical reason]

Solution:
- [How we fixed it]

Prevents:
- [What future bugs this prevents]

Related:
- See .claude/docs/issues/<issue-number>/[RELEVANT_DOC.md]
```

### Feature Implementation Commit Template

```
feat: [Brief description]

Context:
- Phase N of [feature] (issue #XX)

Approach:
- [How implemented]

Key Decisions:
- [Why this approach]

Related:
- .claude/docs/issues/<number>/
```

---

## When to Create Issue Documentation

### Always Create For:
- Multi-phase features
- Complex features (5+ iterations)
- Features with reusable patterns
- Features that taught important lessons

### Optional For:
- Simple bug fixes (unless pattern emerged)
- One-off tasks
- Trivial changes

---

## Anti-Pattern: Ignoring Previous Work

**Bad:** Starting Phase 3 without reading Phase 2 docs -> Makes same mistakes -> Takes 7 iterations again

**Good:** Reading Phase 2 docs -> Extracting patterns -> Applying upfront -> Completes in 2 iterations

---

## Golden Rule

Never start a new phase without reading previous phase documentation.

**Measure Success:**
```
Phase N iterations < Phase N-1 iterations
```

If this isn't happening, we're not learning from previous phases!
