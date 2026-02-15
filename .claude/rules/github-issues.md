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
- `Parent: #123` - This is a child issue of a larger feature
- `Phase 1: #123, Phase 2: #124` - Links to sibling phases

---

## Parent/Child Issues (Phased Implementation)

### When to Use Parent/Child Structure

Use when a feature has **multiple implementation phases** or is too large for a single issue.

**Examples:**
- Multi-phase migrations (Phase 1: Basic, Phase 2: Advanced, Phase 3: Optimization)
- Large features broken into components
- Epic-level features with multiple sub-tasks

### Parent Issue Requirements

**Parent issues MUST include:**

```markdown
## Overview
[High-level description of the entire feature]

## Phases

### Phase 1: [Name] (#<issue-number>)
- **Goal:** [What this phase achieves]
- **Deliverables:** [Key outputs]
- **Status:** ✅ Completed / 🚧 In Progress / ⏳ Pending

### Phase 2: [Name] (#<issue-number>)
- **Goal:** [What this phase achieves]
- **Deliverables:** [Key outputs]
- **Depends on:** Phase 1
- **Status:** ✅ Completed / 🚧 In Progress / ⏳ Pending

### Phase 3: [Name] (#<issue-number>)
- **Goal:** [What this phase achieves]
- **Deliverables:** [Key outputs]
- **Depends on:** Phase 2
- **Status:** ⏳ Pending

## Documentation

Each phase MUST create documentation in:
`.claude/docs/issues/<issue-number>/`

See: [Phased Implementation Rule](.claude/rules/phased-implementation.md)

## Success Criteria
- [ ] All phases completed
- [ ] Each phase documented
- [ ] Lessons documented for future work
```

### Child Issue Requirements

**Each child issue (phase) MUST include:**

```markdown
## Parent Issue
**Parent:** #<parent-issue-number> - [Parent Feature Name]

## Phase Information
**Current Phase:** Phase N of M
**Previous Phases:**
- Phase 1 (#<issue-number>): [Name] - ✅ Completed
- Phase 2 (#<issue-number>): [Name] - ✅ Completed

**Next Phases:**
- Phase N+1 (#<issue-number>): [Name] - ⏳ Pending

## Lessons Applied from Previous Phases

From Phase 1 (#<issue-number>):
- ✅ Applied: [Pattern/lesson]
- ✅ Avoided: [Pitfall]

From Phase 2 (#<issue-number>):
- ✅ Applied: [Pattern/lesson]
- ✅ Avoided: [Pitfall]

See: `.claude/docs/issues/<prev-issue-number>/`

## New Lessons for Future Phases

Document here what Phase N+1 should know:
- Pattern: [New pattern discovered]
- Pitfall: [Mistake to avoid]
- Process: [Process improvement]

Will be documented in: `.claude/docs/issues/<current-issue-number>/`

## Tasks
- [ ] Read previous phase documentation
- [ ] Apply lessons from previous phases
- [ ] [Implementation tasks...]
- [ ] Document lessons for next phase
- [ ] Update parent issue status
```

### Example: Multi-Phase Feature

#### Parent Issue (#25: CustomMenu Migration)

```markdown
## Summary
Migrate all menus from legacy CustomMenu API to BafFramework CustomMenu.

## Phases

### Phase 1: Basic Menu Migration (#28)
- **Goal:** Migrate simple menus and establish patterns
- **Deliverables:**
  - 3 simple menus migrated
  - BafFramework patterns documented
  - Migration guide created
- **Status:** ✅ Completed (2024-01-15)

### Phase 2: BookCraft Menu + FastCraft (#30)
- **Goal:** Migrate complex menu with multi-mode feature
- **Deliverables:**
  - BookCraft menu migrated
  - FastCraft feature implemented
  - Edge cases tested
- **Depends on:** Phase 1 (patterns established)
- **Status:** ✅ Completed (2024-02-15)

### Phase 3: Shop & Trade Menus (#32)
- **Goal:** Apply lessons to remaining menus
- **Deliverables:**
  - Shop menu migrated
  - Trade menu migrated
  - Performance optimization
- **Depends on:** Phase 2 (multi-mode patterns)
- **Status:** 🚧 In Progress

### Phase 4: Performance & Polish (#35)
- **Goal:** Optimize all menus and finalize migration
- **Deliverables:**
  - Performance testing
  - Final cleanup
  - Complete documentation
- **Depends on:** Phase 3 (all menus migrated)
- **Status:** ⏳ Pending

## Documentation
Each phase documented in `.claude/docs/issues/<phase-number>/`

## Success Criteria
- [ ] All menus migrated to BafFramework
- [ ] Performance equal or better than legacy
- [ ] Complete test coverage
- [ ] Documentation for future maintenance
```

#### Child Issue (#30: Phase 2 - BookCraft)

```markdown
## Parent Issue
**Parent:** #25 - CustomMenu Migration

## Phase Information
**Current Phase:** Phase 2 of 4

**Previous Phases:**
- Phase 1 (#28): Basic Menu Migration - ✅ Completed

**Next Phases:**
- Phase 3 (#32): Shop & Trade Menus - ⏳ Pending

## Lessons Applied from Phase 1

From Phase 1 (#28 - see `.claude/docs/issues/28/`):
- ✅ Applied: ItemData pattern for YAML integration
- ✅ Applied: MenuData/ExtraData separation
- ✅ Applied: AbstractItem pattern for click handlers
- ✅ Avoided: Manual slot management (use Settings class)

## Implementation

[Standard issue content: Summary, Problem, Solution, Tasks...]

## New Lessons for Phase 3

**Document for Phase 3 (#32):**

1. **Multi-Mode Features**
   - Pattern: Mode Router with state cleanup
   - Pitfall: Document all modes BEFORE coding
   - See: `.claude/docs/issues/30/LESSONS_FOR_NEXT_PHASE.md`

2. **Resource Operations**
   - Pattern: Before/after verification
   - Pitfall: Distinguish created vs original leftovers
   - See: `.claude/docs/issues/30/ENGINEERING_LESSONS.md`

3. **Testing**
   - Pattern: Test state transitions, not just states
   - Coverage: 8 edge cases for FastCraft
   - See: `.claude/docs/issues/30/FASTCRAFT_EDGE_CASES.md`

**Expected Improvement:**
- Phase 1: 3 iterations (learning)
- Phase 2: 7 iterations (complex, new patterns)
- Phase 3: 2 iterations (applying lessons) ← GOAL

## Documentation
Will be created in: `.claude/docs/issues/30/`

## Tasks
- [x] Read Phase 1 documentation
- [x] Apply ItemData pattern
- [ ] Implement BookCraft menu
- [ ] Implement FastCraft feature
- [ ] Test all edge cases
- [ ] Document lessons for Phase 3
- [ ] Update parent issue (#25) status
```

### Labels for Phased Issues

**Parent issues:**
- `epic` or `parent-issue`
- `effort:XL`
- `multi-phase`

**Child issues:**
- `phase-1`, `phase-2`, etc.
- Individual effort label
- `child-issue`

### Navigating Phase Hierarchy

**From parent issue:**
```markdown
- Phase 1: #28
- Phase 2: #30
- Phase 3: #32
```

**From child issue:**
```markdown
Parent: #25
Previous: #28 (Phase 1)
Current: #30 (Phase 2)
Next: #32 (Phase 3)
```

**In commits:**
```
feat: Implement BookCraft menu

Parent: #25 (CustomMenu Migration)
Phase: 2 of 4 (#30)
Previous: #28 (Basic patterns established)

Applied from Phase 1:
- ItemData pattern
- MenuData/ExtraData separation

See: .claude/docs/issues/28/
```

### Closing Parent Issues

**Only close parent when ALL child issues are complete:**

```markdown
## Status Update - 2024-02-15

- [x] Phase 1 (#28): Basic Migration ✅
- [x] Phase 2 (#30): BookCraft + FastCraft ✅
- [x] Phase 3 (#32): Shop & Trade ✅
- [x] Phase 4 (#35): Performance & Polish ✅

All phases complete. Closing parent issue.

Final documentation: `.claude/docs/issues/25/`
```

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
