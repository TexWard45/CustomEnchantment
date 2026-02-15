# Phased Implementation & Knowledge Inheritance

## Overview

When implementing features across multiple phases, each phase MUST reference and inherit lessons from previous phases to avoid repeating mistakes.

---

## Rule: Document & Reference Previous Phases

### For ANY Multi-Phase Feature

**BEFORE starting a new phase:**

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
- Phase 2: [Link to issue-<number>] - Key learnings: A, B, C

## Lessons Applied from Previous Phases
- Applied pattern X from Phase 1 (see issue-<number>/PATTERNS.md)
- Avoided pitfall Y from Phase 2 (see issue-<number>/RETROSPECTIVE.md)

## New Lessons for Future Phases
- Learned: <new insight>
- Pattern: <new pattern>
- Pitfall: <new mistake to avoid>
```

---

## Process: Starting a New Phase

### Step 1: Check for Previous Documentation

```bash
# Example: Starting Phase 3 of CustomMenu migration
cd .claude/docs/issues/

# Check what exists
ls -R
# Output:
# 30/  (Phase 2 - BookCraft migration)
#   issue-30-custommenu-migration.md
#   FASTCRAFT_RETROSPECTIVE.md
#   LESSONS_FOR_NEXT_PHASE.md
#   ...
```

### Step 2: Read Master Document

**Read first:**
```
.claude/docs/issues/30/issue-30-custommenu-migration.md
```

**Look for:**
- "Lessons Applied to Future Work" section
- "Process Changes for Next Phase" section
- "Key Patterns" or "Reusable Patterns" sections

### Step 3: Extract Applicable Lessons

**Create a checklist:**
```markdown
## Lessons from Phase 2 (Issue #30)

### Patterns to Apply
- [x] Mode Router Pattern (for state switching)
- [x] Duplication Prevention Pattern (for inventory ops)
- [ ] Leftover Classification Pattern (not applicable)

### Pitfalls to Avoid
- [x] Document all modes upfront (before coding)
- [x] Plan state cleanup on transitions
- [x] Track resources before/after

### Process Improvements
- [x] Create state transition diagram first
- [x] Provide 5+ concrete examples
- [x] State invariants explicitly
```

### Step 4: Reference in Implementation

**In your plan/commit message:**
```markdown
## Implementation Approach

Applying lessons from Phase 2 (see .claude/docs/issues/30/):

1. **State Management** (applying Mode Router Pattern)
   - Documented all modes: [list modes]
   - Created transition diagram: [diagram]
   - Planned cleanup: [cleanup rules]

2. **Resource Tracking** (applying Duplication Prevention)
   - Before/after verification implemented
   - Position-based operations (not content-based)

3. **Avoided Pitfalls**
   - ✅ Modes documented before coding (not Phase 2 mistake)
   - ✅ State cleanup on every transition (not Phase 2 mistake)
```

---

## Example: Multi-Phase Feature

### Phase 1: Basic Menu Migration
```
.claude/docs/issues/28/
└── issue-28-basic-menu-migration.md
    - Lesson: BafFramework patterns
    - Pattern: ItemData loading
    - Pitfall: Manual slot management
```

### Phase 2: BookCraft with FastCraft
```
.claude/docs/issues/30/
├── issue-30-custommenu-migration.md
│   ## Previous Phases
│   - Phase 1 (Issue #28): Learned BafFramework patterns
│
│   ## Lessons Applied from Phase 1
│   - Used ItemData pattern (see issue-28)
│   - Avoided manual slot management
│
│   ## New Lessons for Phase 3
│   - Multi-mode features need state diagrams
│   - Resource operations need before/after tracking
└── LESSONS_FOR_NEXT_PHASE.md
```

### Phase 3: Additional Menus
```
.claude/docs/issues/32/
└── issue-32-menu-migrations.md
    ## Previous Phases
    - Phase 1 (Issue #28): BafFramework basics
    - Phase 2 (Issue #30): Multi-mode features, resource tracking

    ## Lessons Applied
    From Phase 1:
    - ✅ ItemData pattern
    - ✅ MenuData/ExtraData separation

    From Phase 2:
    - ✅ State diagram created BEFORE coding
    - ✅ Resource tracking planned upfront
    - ✅ 7 concrete examples provided

    ## Expected Result
    Phase 2 took 7 iterations due to unclear states.
    Phase 3 should take 1-2 iterations with lessons applied.
```

---

## When to Create Issue Documentation

### Always Create For:
- ✅ Multi-phase features
- ✅ Complex features (5+ iterations)
- ✅ Features with reusable patterns
- ✅ Features that taught important lessons

### Optional For:
- ⚠️ Simple bug fixes (unless pattern emerged)
- ⚠️ One-off tasks
- ⚠️ Trivial changes

### Structure:
```
.claude/docs/issues/<issue-number>/
├── issue-<number>-<name>.md      (REQUIRED - master index)
├── *_RETROSPECTIVE.md            (if bugs occurred)
├── *_LESSONS.md                  (if lessons learned)
├── *_PATTERNS.md                 (if patterns emerged)
└── *_COMPARISON.md               (if alternatives evaluated)
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

### Rule: Document Troubles & Solutions in Commits

**Every significant commit MUST document:**
1. **What problem** was encountered
2. **How it was solved**
3. **Why this solution** was chosen
4. **Reference** to issue documentation

**Purpose:** Future phases can learn from ALL commits in current phase, not just final docs.

### Commit Message Template for Bug Fixes

```
fix: [Brief description of what was fixed]

Problem:
- [What went wrong - be specific]
- [Impact: what broke, who affected]

Root Cause:
- [Why it happened - technical reason]
- [Example: "Stale FastCraft data not cleared on mode switch"]

Solution:
- [How we fixed it]
- [Code changes made]

Why This Solution:
- [Reasoning behind approach]
- [Alternatives considered]

Prevents:
- [What future bugs this prevents]

Related:
- See .claude/docs/issues/<issue-number>/[RELEVANT_DOC.md]
- Applies pattern: [Pattern name if applicable]
```

### Example: Bug Fix Commit

```
fix: Clear FastCraft data when switching to regular mode

Problem:
- Regular mode (2 books) was executing FastCraft logic
- Price showing wrong calculation (FastCraft price vs regular)
- Impact: All 2-book crafting operations broken

Root Cause:
- When adding 2nd book, mode switched to regular
- But extraData.setFastCraft(null) was never called
- Regular mode check saw stale FastCraft data and used it

Solution:
- Added extraData.setFastCraft(null) in addBook() when list.size() == 2
- Ensures clean state on mode transition

Why This Solution:
- Simple: One line of code
- Safe: Clears state before entering new mode
- Follows pattern: State cleanup on transitions

Prevents:
- Stale state bugs in any multi-mode feature
- Wrong logic execution due to leftover data

Related:
- Pattern: State Transition with Cleanup
- See .claude/docs/issues/30/LESSONS_FOR_NEXT_PHASE.md#state-cleanup
```

### Example: Feature Implementation Commit

```
feat: Implement FastCraft book combining from inventory

Context:
- Phase 2 of CustomMenu migration (issue #30)
- Adds automatic book combining feature (FastCraft mode)

Approach:
- Collect all matching books from inventory (slot-based tracking)
- Pair books bottom-up (Level I → II → III)
- Track consumed slots for removal
- Return created leftovers, leave originals in place

Key Decisions:
1. Slot-based removal (not ItemStack matching)
   - Why: ItemStack.equals() unreliable with NBT
   - Benefit: Exact, unambiguous removal

2. Distinguish created vs original leftovers
   - Why: Prevent duplication (learned from testing)
   - Pattern: if (leftover.fromInventory) skip

3. Price = inventory books only (not menu book)
   - Why: Menu book consumed but result replaces it
   - Net cost: inventory books consumed

Challenges Encountered:
- Initially removed menu book by calling returnBook()
  - Problem: Returned it to player instead of consuming
  - Fix: Use list.clear() instead
  - Lesson: Don't trust method names, read implementation

Testing:
- 8 edge cases added (see FastCraftEdgeCaseTest.java)
- All passing

Related:
- .claude/docs/issues/30/FASTCRAFT_RETROSPECTIVE.md
- .claude/docs/issues/30/ENGINEERING_LESSONS.md#resource-operations
```

### Quick Commit Template (For Minor Fixes)

```
fix: [Brief description]

Problem: [One line - what broke]
Cause: [One line - why]
Fix: [One line - how]

Related: .claude/docs/issues/<number>/
```

### Commit Message Checklist

For **bug fix** commits:
- [ ] What broke (observable problem)
- [ ] Root cause (technical reason)
- [ ] How we fixed it
- [ ] Why this approach
- [ ] What it prevents in future
- [ ] Reference to issue docs

For **feature** commits:
- [ ] Context (what phase, what issue)
- [ ] Approach taken
- [ ] Key decisions made
- [ ] Challenges encountered & solutions
- [ ] Testing done
- [ ] Reference to patterns/docs

---

## Using Commit History for Future Phases

### Finding Relevant Commits

```bash
# See all commits for an issue
git log --grep="issue #30" --oneline

# See detailed commit messages
git log --grep="issue #30" --no-merges

# See commits with specific keyword
git log --grep="state cleanup" --grep="duplication" --no-merges
```

### Extracting Lessons from Commits

**When starting Phase 3:**

```bash
# 1. Check issue docs
cat .claude/docs/issues/30/issue-30-custommenu-migration.md

# 2. Review commit history for patterns
git log --grep="issue #30" --no-merges | grep -A 10 "Problem:"
git log --grep="issue #30" --no-merges | grep -A 5 "Pattern:"

# 3. Extract key fixes
git log --grep="fix:" --grep="issue #30" --oneline
```

**Look for:**
- "Problem:" → What went wrong
- "Root Cause:" → Why it happened
- "Solution:" → How it was fixed
- "Prevents:" → What to avoid in future
- "Pattern:" → Reusable approach

### Building Knowledge Base

**Commits become searchable knowledge:**

```bash
# Find all state cleanup fixes
git log --grep="state cleanup" --oneline

# Find all duplication prevention fixes
git log --grep="duplication" --oneline

# Find all mode transition issues
git log --grep="mode transition" --grep="mode switch" --oneline
```

**Each commit = mini-lesson for future phases**

---

## Anti-Pattern: Ignoring Previous Work

### ❌ Bad: Starting from Scratch
```
Developer: "Starting Phase 3 menu migration"
[Codes without checking Phase 2 docs]
[Makes same state management mistakes]
[Takes 7 iterations again]
```

### ✅ Good: Building on Previous Work
```
Developer: "Starting Phase 3 menu migration"
[Reads .claude/docs/issues/30/]
[Extracts: Mode Router pattern, state cleanup checklist]
[Applies lessons upfront]
[Completes in 2 iterations]
```

---

## Example Reference Pattern

### In Code Comments
```java
/**
 * Mode Router Pattern
 *
 * Applied from Phase 2 (see .claude/docs/issues/30/LESSONS_FOR_NEXT_PHASE.md)
 *
 * Key insight: Always clean up previous mode data before entering new mode
 * to prevent stale state bugs.
 */
public void handleModeTransition() {
    if (newMode == FastCraft) {
        clearRegularModeData();  // From Phase 2 lesson
        enterFastCraftMode();
    }
}
```

### In Commit Messages
```
feat: Implement Shop menu with multi-mode support

Applied lessons from Phase 2 (issue #30):
- Created state diagram before coding
- Planned state cleanup on transitions
- Added before/after resource verification

Expected: 2 iterations (vs 7 in Phase 2)

References:
- .claude/docs/issues/30/LESSONS_FOR_NEXT_PHASE.md
- .claude/docs/issues/30/ENGINEERING_LESSONS.md
```

### In Pull Request Descriptions
```markdown
## Lessons Applied

From **Phase 2** (Issue #30 - BookCraft):
- ✅ Mode Router Pattern ([ref](../.claude/docs/issues/30/LESSONS_FOR_NEXT_PHASE.md#mode-router-pattern))
- ✅ State cleanup checklist ([ref](../.claude/docs/issues/30/LESSONS_FOR_NEXT_PHASE.md#state-cleanup))
- ✅ Duplication Prevention ([ref](../.claude/docs/issues/30/ENGINEERING_LESSONS.md#resource-operations))

## Impact

Phase 2 took 7 iterations due to unclear state management.
This PR completed in 2 iterations by applying documented lessons.
```

---

## Benefits

### 1. Faster Implementation
- **Phase 1:** 5 iterations (learning)
- **Phase 2:** 7 iterations (complex, new patterns)
- **Phase 3:** 2 iterations (applied lessons) ← **Goal**

### 2. Knowledge Retention
- Patterns documented, not forgotten
- New team members can learn from past work
- Mistakes not repeated

### 3. Continuous Improvement
- Each phase improves on previous
- Patterns refined over time
- Process gets more efficient

### 4. Clear Progress Tracking
- Can measure improvement across phases
- Can justify time spent on documentation
- Can demonstrate learning

---

## Summary

**Golden Rule:** Never start a new phase without reading previous phase documentation.

**Process:**
1. Check `.claude/docs/issues/<prev-number>/`
2. Read master document
3. Extract patterns and pitfalls
4. Reference in current work
5. Document new lessons for next phase

**Expected Result:**
- Iterations decrease with each phase
- Patterns emerge and get reused
- Team learns from past mistakes
- Quality improves over time

**Measure Success:**
```
Phase N iterations < Phase N-1 iterations
```

If this isn't happening, we're not learning from previous phases!
