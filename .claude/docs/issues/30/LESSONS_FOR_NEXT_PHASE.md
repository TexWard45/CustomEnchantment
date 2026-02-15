# Lessons Learned - Applied to Future Development

## Quick Reference Checklist

Before starting any new feature implementation, verify:

- [ ] All modes/states clearly defined
- [ ] State transitions mapped out
- [ ] Cleanup logic identified for each state change
- [ ] Duplication prevention strategy in place
- [ ] Price/cost calculation rules explicit
- [ ] Leftover/remainder handling planned
- [ ] Edge cases listed upfront

---

## Pattern Recognition for Future Features

### 1. Multi-Mode Features

**Red Flag:** Feature has different behavior based on context/state

**Action Before Coding:**
1. List ALL modes explicitly
2. Draw state transition diagram
3. Identify what data must be cleared on each transition
4. Plan separate code paths for each mode

**Example from FastCraft:**
```
Mode 1: FastCraft (1 book in menu)
  - Scan inventory
  - Combine all matching books
  - Price = inventory books consumed

Mode 2: Regular (2 books in menu)
  - Don't scan inventory
  - Combine just the 2 menu books
  - Price = 1x base cost

Transition 1→2: Clear extraData.setFastCraft(null)
Transition 2→1: Recalculate FastCraft
```

### 2. Resource Consumption Features

**Red Flag:** Feature removes items from inventory

**Questions to Ask Upfront:**
1. What exactly gets consumed? (all items? only some?)
2. What gets returned to player? (result? leftovers?)
3. How to prevent duplication?
4. What if operation fails mid-way?

**Duplication Prevention Pattern:**
```java
// ALWAYS verify before implementing:
// 1. What's removed from inventory
// 2. What's added to inventory
// 3. Net change should be correct

// Example:
// Remove: 6 books Level I
// Add: 1 book Level III + 1 book Level II
// Net: -4 books Level I (correct)
```

### 3. Price/Cost Calculations

**Red Flag:** Feature has monetary cost

**Clarify Upfront:**
1. What counts toward cost? (all items? only consumed items?)
2. Are there "free" items? (menu book in FastCraft)
3. Multiplier vs base cost?
4. Edge case pricing (single item, max items)

**Pattern:**
```java
// WRONG: totalItemsUsed * price
// CORRECT: itemsActuallyConsumedFromInventory * price
// REASON: Menu items might be "free" (replaced by result)
```

### 4. Leftover/Remainder Handling

**Red Flag:** Algorithm processes items in batches (pairing, grouping)

**Questions:**
1. What happens to odd items?
2. Are leftovers returned to player?
3. Are leftovers kept in inventory?
4. Can leftovers be distinguished from never-used items?

**Pattern:**
```java
// Track TWO types of leftovers:
// 1. Items created through processing but not fully consumed
//    → Return to player (give back)
// 2. Original items never used in processing
//    → Leave in inventory (don't duplicate)

if (leftover.fromInventory) {
    // Type 2: Leave in place
} else {
    // Type 1: Give back
}
```

---

## Common Pitfalls to Avoid

### Pitfall 1: "It's just a name" Assumptions

**Mistake:** Assumed `returnBook()` returns book to player (it does!)
**Lesson:** Don't trust method names blindly - read implementation

**Prevention:**
- Read method implementation before using
- Check what it does to state (modifies? returns?)
- Verify side effects

### Pitfall 2: Partial State Updates

**Mistake:** Changed mode but didn't clear old mode's data
**Lesson:** State transitions need cleanup

**Prevention Pattern:**
```java
// When switching modes:
void switchToMode(Mode newMode) {
    // 1. Clear old mode data
    clearCurrentModeData();

    // 2. Set new mode
    this.currentMode = newMode;

    // 3. Initialize new mode data
    initializeNewModeData();
}
```

### Pitfall 3: Validation at Wrong Layer

**Mistake:** Filtered data during collection, should validate at end
**Lesson:** Collect all, validate result

**Prevention Pattern:**
```java
// WRONG: Filter during collection
for (Item item : inventory) {
    if (item.level > baseLevel) continue;  // Skip high levels
    collect(item);
}

// CORRECT: Collect all, validate result
for (Item item : inventory) {
    collect(item);  // Collect everything
}
Result result = process(collected);
if (!isValid(result)) {  // Validate at end
    reject();
}
```

### Pitfall 4: Fixing Symptoms Instead of Root Cause

**Mistake:** Fixed price display without understanding consumption logic
**Lesson:** Understand the "why" before fixing the "what"

**Prevention:**
- Ask: "Why is this happening?" before "How do I fix it?"
- Trace through full data flow
- Identify root cause, not just visible symptom

---

## Questions to Ask BEFORE Coding

### For ANY New Feature

1. **Modes/States:**
   - How many distinct behaviors does this have?
   - What triggers mode changes?
   - What data must be cleared on transitions?

2. **Data Flow:**
   - Where does data come from?
   - How is it transformed?
   - Where does it go?
   - What gets persisted?

3. **Invariants:**
   - What must ALWAYS be true?
   - What should NEVER happen?
   - How to prevent violations?

4. **Edge Cases:**
   - What if empty?
   - What if single item?
   - What if maximum items?
   - What if odd number?

5. **Cleanup:**
   - What resources need releasing?
   - What happens on failure?
   - What state must be reset?

---

## Red Flags That Need Clarification

If you see these in requirements, STOP and ask for details:

🚩 **"Combine items from inventory"**
   → Which items? All? Matching? How many?

🚩 **"Calculate price"**
   → Based on what? Total? Consumed? Inventory only?

🚩 **"Handle leftovers"**
   → Return them? Keep them? Where? How to identify?

🚩 **"Different modes"**
   → How many? Transition rules? State cleanup?

🚩 **"Remove items from inventory"**
   → Duplication prevention? Rollback on failure?

🚩 **"Fast/Quick/Auto feature"**
   → Fast how? Auto when? Different from manual?

---

## Testing Strategy

### Test in This Order

1. **Happy path with minimum inputs**
   - 2 items, simple case
   - Verify basic flow works

2. **Edge case: Empty**
   - 0 items
   - Should reject gracefully

3. **Edge case: Single**
   - 1 item
   - Should reject or handle special

4. **Edge case: Odd number**
   - 3, 5, 7 items
   - Verify leftover handling

5. **Edge case: Maximum**
   - Max allowed items
   - Verify no overflow

6. **Mode transitions**
   - Switch between all modes
   - Verify state cleanup

7. **Failure scenarios**
   - Not enough money
   - Inventory full
   - Verify rollback/cleanup

8. **Duplication check**
   - Count items before
   - Count items after
   - Verify net change correct

---

## Code Review Self-Checklist

Before submitting any implementation:

### State Management
- [ ] All mode data cleared on transitions?
- [ ] No stale references remaining?
- [ ] State always consistent?

### Resource Safety
- [ ] Items never duplicated?
- [ ] Items never lost?
- [ ] Proper cleanup on failure?

### Calculation Correctness
- [ ] Price based on correct items?
- [ ] Count calculations verified?
- [ ] Edge cases handled?

### Leftover Handling
- [ ] Created items vs original items distinguished?
- [ ] Leftovers returned correctly?
- [ ] No duplication of inventory items?

### Logging
- [ ] Key decisions logged?
- [ ] Before/after state logged?
- [ ] Enough info to debug issues?

---

## Patterns to Memorize

### Pattern 1: Mode Router

```java
void handleAction() {
    if (list.size() == 1) {
        handleMode1();
    } else if (list.size() == 2) {
        clearMode1Data();  // ← Critical!
        handleMode2();
    } else {
        handleError();
    }
}
```

### Pattern 2: Duplication Prevention

```java
// Before operation
int countBefore = countItems(player.getInventory());

// Perform operation
performOperation();

// After operation
int countAfter = countItems(player.getInventory());

// Verify
assertEquals(expectedChange, countAfter - countBefore);
```

### Pattern 3: Leftover Classification

```java
for (Item leftover : leftovers) {
    if (leftover.isOriginalFromInventory()) {
        // Leave in inventory (don't duplicate)
        continue;
    }

    if (leftover.wasCreatedThroughProcessing()) {
        // Give back to player
        giveToPlayer(leftover);
    }
}
```

### Pattern 4: State Transition with Cleanup

```java
void transitionToState(State newState) {
    // 1. Exit current state
    if (currentState != null) {
        currentState.onExit();  // Cleanup
    }

    // 2. Enter new state
    currentState = newState;
    currentState.onEnter();  // Initialize
}
```

---

## Specific Learnings for Next Phase

### 1. Menu Features

**Always check:**
- Does this have multiple modes based on menu state?
- What happens when items are added/removed?
- Is state cleared on transitions?

### 2. Inventory Operations

**Always verify:**
- What's removed?
- What's added?
- Net change correct?
- No duplication possible?

### 3. Pricing/Cost Features

**Always clarify:**
- What counts toward cost?
- Any "free" items?
- Multiplier or flat cost?

### 4. Batch Processing (Pairing, Grouping)

**Always plan:**
- How to handle odd numbers?
- What to do with leftovers?
- Original vs created items distinction?

---

## Communication with User

### When to Ask for Clarification

**Immediate red flags:**
- Requirements mention "modes" without defining them
- Pricing logic unclear
- Item consumption not explicit
- Leftover handling not specified

**Good clarification questions:**
```
❌ "Should this work?"
✅ "I see two modes: X and Y. When user does Z, which mode applies?"

❌ "What about edge cases?"
✅ "Given 5 items (odd number), should the leftover be A or B?"

❌ "How should this behave?"
✅ "Mode transition: 1 item → 2 items. Should I clear X data?"
```

### When to Propose Solution

Only after:
1. All modes identified
2. State transitions mapped
3. Edge cases listed
4. Invariants stated

**Format:**
```
Understanding:
- Mode A: [behavior]
- Mode B: [behavior]
- Transition A→B: [cleanup]

Edge Cases:
- Empty: [behavior]
- Odd number: [leftover handling]

Invariants:
- Never duplicate items
- Price = inventory only

Proposal:
[implementation approach]

Is this correct?
```

---

## Summary: Apply These for Next Phase

### 🎯 Top 5 Priority Actions

1. **Map out modes FIRST** - Before any code
2. **Plan state cleanup** - Every transition
3. **Check duplication invariant** - Every inventory operation
4. **Distinguish leftover types** - Created vs original
5. **Verify calculations** - With concrete examples

### 🚀 Process Improvement

**Old approach:**
1. Read requirements
2. Start coding
3. Fix issues as they appear
4. Repeat fixes 7 times

**New approach:**
1. Read requirements
2. **Ask clarifying questions**
3. **Map modes and states**
4. **List edge cases**
5. **Verify understanding with user**
6. Start coding
7. Fix 1-2 minor issues

**Result:** 7 iterations → 1-2 iterations

---

## Final Checklist for Phase 3+

Before starting ANY new feature:

- [ ] **Modes defined?** All states listed
- [ ] **Transitions mapped?** Cleanup identified
- [ ] **Examples provided?** 3-5 concrete cases
- [ ] **Invariants stated?** "Never X", "Always Y"
- [ ] **Edge cases listed?** Empty, single, odd, max
- [ ] **Calculations explicit?** What counts toward price/cost
- [ ] **Leftover handling planned?** Created vs original
- [ ] **Duplication prevented?** Before/after verification

If ANY checkbox is unchecked → **ASK FOR CLARIFICATION FIRST**

Don't assume. Don't guess. **Verify understanding.**
