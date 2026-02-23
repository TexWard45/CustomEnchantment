---
name: github-issues
description: Guidelines for creating well-structured GitHub issues. Use when creating issues, bug reports, or feature requests. Covers title formatting, priority/effort definitions, labels, parent/child issue structure, and phased implementation templates.
---

# GitHub Issue Guidelines

## Required Sections Checklist

Every issue MUST include:

- [ ] **Title** - Clear, concise, uses correct type prefix
- [ ] **Summary** - 2-3 sentences explaining what and why
- [ ] **Problem** - Current behavior, expected behavior, impact
- [ ] **Solution** - Approach, technical details, alternatives
- [ ] **Tasks** - Actionable checklist of implementation steps
- [ ] **Metadata** - Priority, effort, labels

Optional but recommended:
- [ ] **Hints** - Code references, useful links, pitfalls
- [ ] **Screenshots/Examples** - Visual context when helpful

---

## Title Formatting

### Format
```
[type]: Brief imperative description
```

### Types
| Type | Use For | Example |
|------|---------|---------|
| `feat` | New features | `feat: Add custom enchant system` |
| `fix` | Bug fixes | `fix: Resolve NPE in PlayerData lookup` |
| `refactor` | Code restructuring | `refactor: Extract condition logic to hook` |
| `docs` | Documentation | `docs: Add module lifecycle reference` |
| `test` | Testing | `test: Add unit tests for ArgumentLine` |
| `chore` | Maintenance | `chore: Update dependencies` |
| `perf` | Performance | `perf: Optimize menu rendering per tick` |

### Rules
- Use imperative mood: "Add" not "Added" or "Adds"
- Keep under 60 characters
- No period at end
- Be specific but concise

---

## Priority Definitions

| Priority | SLA | Criteria | Examples |
|----------|-----|----------|----------|
| **P0** | Immediate | System down, data loss, security vulnerability | Server crash, data corruption, exploit vulnerability |
| **P1** | 24 hours | Major feature broken, significant user impact | Plugin fails to load, player data lost, core command broken |
| **P2** | 1 week | Feature degraded, workaround exists | Slow TPS, menu glitch, minor config issue |
| **P3** | Backlog | Nice-to-have, cosmetic, minor inconvenience | Typo, message formatting, edge case handling |

---

## Effort Definitions

| Effort | Time | Scope | Complexity |
|--------|------|-------|------------|
| **S** | < 2h | 1-2 files | Straightforward, pattern exists |
| **M** | 2-8h | 3-5 files | Some complexity, clear approach |
| **L** | 1-3 days | Multiple components | Requires planning, new patterns |
| **XL** | 1+ weeks | Architectural | Major feature, cross-cutting concerns |

---

## Labels Guide

### Category Labels
| Label | Description | Color |
|-------|-------------|-------|
| `bug` | Something isn't working | Red |
| `feature` | New feature request | Green |
| `enhancement` | Improvement to existing feature | Blue |
| `documentation` | Documentation updates | Yellow |
| `refactor` | Code quality improvement | Purple |
| `security` | Security-related | Orange |

### Effort Labels
| Label | Description |
|-------|-------------|
| `effort:S` | Small - < 2 hours |
| `effort:M` | Medium - 2-8 hours |
| `effort:L` | Large - 1-3 days |
| `effort:XL` | Extra large - 1+ weeks |

---

## Linking Related Work

### Relationship Keywords
- `Fixes #123` - Closes issue when PR merges
- `Relates to #123` - Connected but doesn't close
- `Blocked by #123` - Dependency
- `Parent: #123` - This is a child issue of a larger feature

---

## Parent/Child Issues (Phased Implementation)

### Parent Issue Template

```markdown
## Overview
[High-level description of the entire feature]

## Phases

### Phase 1: [Name] (#<issue-number>)
- **Goal:** [What this phase achieves]
- **Deliverables:** [Key outputs]
- **Status:** Completed / In Progress / Pending

### Phase 2: [Name] (#<issue-number>)
- **Goal:** [What this phase achieves]
- **Depends on:** Phase 1
- **Status:** Pending

## Success Criteria
- [ ] All phases completed
- [ ] Each phase documented
```

### Child Issue Template

```markdown
## Parent Issue
**Parent:** #<parent-issue-number> - [Parent Feature Name]
**Phase:** N of M

## Lessons Applied from Previous Phases
- Applied: [Pattern/lesson]
- Avoided: [Pitfall]

## Tasks
- [ ] Read previous phase documentation
- [ ] [Implementation tasks...]
- [ ] Document lessons for next phase
- [ ] Update parent issue status
```

---

## Quality Standards

### Good Issue Checklist
- [ ] Problem is clearly defined
- [ ] Solution is actionable
- [ ] Tasks are small enough to complete in one session
- [ ] All relevant context is included
- [ ] Edge cases are documented
- [ ] Success criteria is measurable

### Common Anti-Patterns

**Bad:** "Fix the bug"
**Good:** "fix: Resolve NPE in PlayerManager.getData when player disconnects during async task"

**Bad:** Tasks like "Implement feature"
**Good:** Specific tasks like "Create MoneyCondition class", "Write unit tests for ArgumentLine parsing"

---

## Review Checklist

Before submitting an issue, verify:

1. **Uniqueness:** No existing issue covers this
2. **Completeness:** All required sections filled
3. **Clarity:** A new team member could understand it
4. **Actionable:** Clear path to "done"
5. **Scoped:** Single concern per issue
6. **Estimated:** Priority and effort assigned
7. **Labeled:** Appropriate labels applied
