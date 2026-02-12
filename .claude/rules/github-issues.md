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

### When to Escalate
- P2 -> P1: Multiple users reporting, workaround complex
- P1 -> P0: Business impact, revenue loss, compliance risk

---

## Effort Definitions

| Effort | Time | Scope | Complexity |
|--------|------|-------|------------|
| **S** | < 2h | 1-2 files | Straightforward, pattern exists |
| **M** | 2-8h | 3-5 files | Some complexity, clear approach |
| **L** | 1-3 days | Multiple components | Requires planning, new patterns |
| **XL** | 1+ weeks | Architectural | Major feature, cross-cutting concerns |

### Estimation Tips
- Include testing time
- Include documentation time
- Add buffer for unknowns (50% for L/XL)
- Consider review/iteration cycles

---

## Quality Standards

### Good Issue Checklist
- [ ] Problem is clearly defined
- [ ] Solution is actionable
- [ ] Tasks are small enough to complete in one session
- [ ] All relevant context is included
- [ ] No assumptions - explicit is better
- [ ] Edge cases are documented
- [ ] Success criteria is measurable

### Common Anti-Patterns

**Bad:** "Fix the bug"
**Good:** "fix: Resolve NPE in PlayerManager.getData when player disconnects during async task"

**Bad:** "Make it faster"
**Good:** "perf: Reduce menu open latency from 200ms to under 50ms by caching ItemStack builders"

**Bad:** Tasks like "Implement feature"
**Good:** Specific tasks like "Create MoneyCondition class", "Add getPlayerData method to PlayerManager", "Write unit tests for ArgumentLine parsing"

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

### Status Labels
| Label | Description |
|-------|-------------|
| `needs-triage` | Not yet prioritized |
| `in-progress` | Currently being worked on |
| `blocked` | Waiting on external dependency |
| `ready-for-review` | PR submitted, needs review |

### Effort Labels
| Label | Description |
|-------|-------------|
| `effort:S` | Small - < 2 hours |
| `effort:M` | Medium - 2-8 hours |
| `effort:L` | Large - 1-3 days |
| `effort:XL` | Extra large - 1+ weeks |

---

## Issue Templates

Use the appropriate template from `.github/ISSUE_TEMPLATE/`:
- `bug_report.md` - For bugs and defects
- `feature_request.md` - For new features
- `improvement.md` - For enhancements to existing features

---

## Linking Related Work

### Reference Format
- Issues: `#123` or `org/repo#123`
- PRs: `#123` or `org/repo#123`
- Commits: `abc1234` or full SHA
- Files: `` `src/main/java/com/example/myplugin/MyClass.java:123` ``

### Relationship Keywords
- `Fixes #123` - Closes issue when PR merges
- `Relates to #123` - Connected but doesn't close
- `Blocked by #123` - Dependency
- `Supersedes #123` - Replaces old issue

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

---

## Examples

### Minimal Valid Issue
```markdown
## Summary
Add cooldown feedback to command execution so players know when they can use it again.

## Problem
### Current Behavior
Command silently fails during cooldown, players spam it repeatedly.

### Expected Behavior
Command should send a message showing remaining cooldown time.

### Impact
All players using commands with cooldowns, causes confusion, medium severity.

## Solution
### Approach
Add cooldown check with remaining time message in AbstractCommand.execute().

### Technical Details
- Modify: `src/main/java/.../command/AbstractCommand.java`

## Tasks
- [ ] Add cooldown remaining time calculation
- [ ] Send formatted cooldown message to player
- [ ] Write unit tests
- [ ] Test

---
**Priority:** P2 | **Effort:** S | **Labels:** enhancement
```

### Comprehensive Issue
See the example in `.claude/agents/issue-creator.md`
