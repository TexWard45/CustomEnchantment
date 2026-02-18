# Universal Engineering Lessons - FastCraft Implementation

## Core Principles (Apply to Any Project)

### 1. **State Machines Need Explicit Documentation**

**Problem:** Features with multiple behaviors based on context (modes/states) are bug-prone.

**Solution:** Before coding, document:
- All possible states
- Transition triggers
- What data to clear on each transition

**Example:**
```
State A → State B: Clear X data, initialize Y
State B → State A: Clear Y data, initialize X
```

**Why it matters:** Forgot to clear data → stale state → wrong behavior

**Applies to:** Any feature with "if X then do A, else do B" logic

---

### 2. **Resource Operations Must Track Before/After**

**Problem:** Operations that add/remove resources (inventory, money, data) can duplicate or lose items.

**Solution:** Always verify the equation:
```
After = Before + Added - Removed
```

**Pattern:**
```java
// Count before
int before = countResources();

// Perform operation
doOperation();

// Count after and verify
int after = countResources();
assert (after == before + added - removed);
```

**Why it matters:** Duplication bugs are silent and hard to detect.

**Applies to:** Inventory systems, currency, database records, file operations

---

### 3. **Distinguish "Returned" vs "Leftover" Resources**

**Problem:** Leftovers from processing can be confused with unused original resources.

**Solution:** Track origin:
```
if (resource.wasCreatedByProcessing) {
    return to user;  // Give back created items
} else if (resource.neverUsed) {
    leave in place;  // Don't duplicate originals
}
```

**Why it matters:** Returned both → duplication. Returned neither → loss.

**Applies to:** Batch processing, pairing algorithms, transaction rollbacks

---

### 4. **Pricing Logic Needs Explicit "What Counts" Rules**

**Problem:** Ambiguous which items count toward cost.

**Solution:** Document explicitly:
```
Price = items_actually_consumed × base_cost
NOT: total_items × base_cost
```

**Example:**
- Total items: 5 (1 menu + 4 inventory)
- Items consumed from inventory: 4
- Price: 4 × cost (menu item is "free" - result replaces it)

**Why it matters:** Off-by-one errors in pricing are common.

**Applies to:** E-commerce, crafting systems, resource exchanges

---

### 5. **Use Position/Index-Based Operations, Not Content-Based**

**Problem:** Matching by content (ItemStack, Object equality) is unreliable.

**Solution:** Track exact positions/indices:
```java
// BAD: Match by content
for (Item item : list) {
    if (item.equals(target)) remove(item);  // Which one if duplicates?
}

// GOOD: Track by position
List<Integer> indicesToRemove = [1, 3, 5];
for (int i : indicesToRemove) {
    list.remove(i);  // Exact, unambiguous
}
```

**Why it matters:** Content matching fails with duplicates or complex equality.

**Applies to:** Collections, inventory slots, database rows

---

### 6. **Validate Results, Not Inputs**

**Problem:** Filtering/validating during collection rejects valid combinations.

**Solution:** Collect everything, validate the result:
```java
// BAD: Filter during collection
for (item : all_items) {
    if (item.level > base) skip;  // Misses valid multi-level combining
    collect(item);
}

// GOOD: Collect all, validate result
for (item : all_items) {
    collect(item);  // Collect everything eligible
}
result = process(collected);
if (!isValid(result)) reject();  // Validate at end
```

**Why it matters:** Early filtering can prevent valid results.

**Applies to:** Search algorithms, data processing, graph traversal

---

### 7. **Methods Should Do What Their Name Says (Or Rename Them)**

**Problem:** `returnBook()` actually returns book to player, but we wanted to consume it.

**Solution:**
- Read what method does, not just its name
- If name misleads, create new method with correct name

```java
// Method named "return" but we want "consume"
// Don't use returnBook() - create consumeBook()

private void consumeBook(int index) {
    list.remove(index);
    updateDisplay();
    // No InventoryUtils.addItem() - book is consumed
}
```

**Why it matters:** Wrong assumptions about method behavior cause bugs.

**Applies to:** All codebases, especially inherited/legacy code

---

### 8. **Clean Up State on Transitions**

**Problem:** Switching modes without cleaning up previous mode's data.

**Solution:** Every state transition needs cleanup:
```java
void transitionTo(NewState state) {
    currentState.onExit();   // Clean up old state
    currentState = state;
    currentState.onEnter();  // Initialize new state
}
```

**Why it matters:** Stale data from previous state causes wrong behavior.

**Applies to:** UI state, game state, workflow engines, FSMs

---

### 9. **Ask "What Gets Consumed vs What Gets Replaced?"**

**Problem:** Unclear what's actually removed vs swapped.

**Solution:** For each operation, document:
```
Consumed (removed):
- Item A from inventory
- Item B from inventory

Replaced (net zero):
- Menu item (removed but result takes its place)

Created (added):
- Result item
- Leftover items
```

**Why it matters:** Affects pricing, inventory tracking, resource management.

**Applies to:** Crafting, trading, transformations, upgrades

---

### 10. **Test State Transitions, Not Just States**

**Problem:** Testing each state in isolation misses transition bugs.

**Solution:** Test the edges, not just nodes:
```java
@Test
void testTransition_StateA_to_StateB() {
    // Start in State A
    enterStateA();

    // Trigger transition
    transitionToB();

    // Verify State A data cleared
    assertNull(stateA_data);

    // Verify State B initialized
    assertNotNull(stateB_data);
}
```

**Why it matters:** Most bugs occur during transitions, not in stable states.

**Applies to:** State machines, workflows, UI navigation, game states

---

## Meta-Lessons (Process Improvement)

### 11. **Symptoms vs Root Causes**

**Problem:** Fixed the display (symptom) without understanding consumption logic (root cause).

**Principle:** Always ask "Why is this happening?" before "How do I fix it?"

**Pattern:**
1. Observe symptom (price shows 5000)
2. Ask why (using totalBooks instead of inventoryBooks)
3. Fix root cause (use correct variable)
4. Verify symptom gone (price shows 4000)

**Applies to:** Debugging, problem-solving, system design

---

### 12. **Invariants Before Implementation**

**Problem:** Started coding without defining "items must never duplicate."

**Principle:** State invariants upfront:
```
MUST be true:
- Items never duplicate
- Price = inventory items only
- State data cleared on transitions
```

**Benefit:** Invariants guide implementation and testing.

**Applies to:** All software development

---

### 13. **Examples Beat Descriptions**

**Problem:** "Combine books from inventory" is vague.

**Principle:** Provide concrete examples:
```
Input: 5 books Level I (1 menu, 4 inventory)
Output: 1 Level III + 1 Level II
Consumed: All 5 books
Price: 4000 (4 inventory × 1000)
```

**Benefit:** Examples clarify edge cases and expected behavior.

**Applies to:** Requirements gathering, API documentation, specifications

---

## Quick Reference Checklist

**Before implementing ANY feature with:**

### State/Mode switching:
- [ ] All states documented
- [ ] Transition diagram created
- [ ] Cleanup identified for each transition

### Resource operations:
- [ ] What gets consumed documented
- [ ] Before/after verification planned
- [ ] Duplication prevention pattern chosen

### Pricing/calculations:
- [ ] "What counts" explicitly stated
- [ ] Examples with numbers provided
- [ ] Edge cases identified

### Testing:
- [ ] Transitions tested, not just states
- [ ] Invariants verified
- [ ] Duplication explicitly checked

---

## Summary

**7 iterations required because we:**
1. ❌ Didn't map state transitions upfront
2. ❌ Didn't track resource consumption carefully
3. ❌ Didn't distinguish created vs original items
4. ❌ Didn't clarify pricing rules
5. ❌ Assumed method behavior from names
6. ❌ Filtered during collection instead of validating results
7. ❌ Forgot to clean up state on transitions

**Could have been 1-2 iterations if we:**
1. ✅ Documented states and transitions first
2. ✅ Defined resource tracking before/after
3. ✅ Clarified leftover handling upfront
4. ✅ Stated pricing rules explicitly
5. ✅ Read method implementations
6. ✅ Validated results, not inputs
7. ✅ Planned state cleanup

**These principles apply to:**
- Game state management
- E-commerce transactions
- Workflow engines
- Resource management systems
- Any system with multiple modes or states
- Any system that adds/removes resources

**Remember:** Most bugs come from unclear state transitions and resource tracking. Document these BEFORE coding.
