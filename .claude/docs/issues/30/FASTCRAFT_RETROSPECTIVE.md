# FastCraft Implementation - Retrospective & Lessons Learned

## Executive Summary

This document analyzes the iterative debugging process for the FastCraft feature migration, identifying root causes of repeated issues and providing guidance for future AI-assisted development.

---

## Issues Fixed (In Order)

### 1. Price Display Showing Wrong Value (5000 instead of 4000)

**Problem:**
- Price calculation used `totalBooksUsed` (5 books: 1 menu + 4 inventory)
- Should use `slotsToRemoveCount` (4 books: only inventory books consumed)

**Root Cause:**
- Misunderstanding of which books count toward price
- Menu book is consumed but result replaces it (net cost = inventory only)

**Fix:**
```java
// BEFORE (WRONG)
showAcceptButton(fastCraft.getTotalBooksUsed());

// AFTER (CORRECT)
showAcceptButton(fastCraft.getSlotsToRemoveCount());
```

**Lesson:** Clarify business logic upfront - "price = inventory books consumed, not total books"

---

### 2. FastCraft Triggering with 2 Books

**Problem:**
- Adding 2nd book still triggered FastCraft mode
- Should switch to regular 2-book combine mode

**Root Cause:**
- `addBook()` always called `triggerFastCraft()` regardless of book count
- No routing logic based on menu state

**Fix:**
```java
if (list.size() == 1) {
    triggerFastCraft();  // FastCraft mode
} else if (list.size() == 2) {
    updatePreview();     // Regular mode
}
```

**Lesson:** Define mode boundaries explicitly - "1 book = FastCraft, 2 books = Regular"

---

### 3. Regular Mode Executing FastCraft Logic

**Problem:**
- With 2 books in menu, clicking accept executed FastCraft instead of regular combine
- Display showed regular mode but logic used FastCraft

**Root Cause:**
- `extraData.getFastCraft()` still contained data from when there was 1 book
- Never cleared when switching modes

**Fix:**
```java
else if (list.size() == 2) {
    extraData.setFastCraft(null);  // Clear old FastCraft data
    updatePreview();
}
```

**Lesson:** State management is critical - clear stale data when switching modes

---

### 4. Books Not Being Consumed (Duplication)

**Problem:**
- After FastCraft, player kept menu book + got result = duplication
- Could craft repeatedly with same book

**Root Cause:**
- `confirm()` called `menu.returnBook()` which GIVES BACK the book
- Should just remove it from menu list

**Fix:**
```java
// BEFORE (WRONG)
menu.returnBook(slot);  // Removes AND gives back

// AFTER (CORRECT)
menuBooks.clear();  // Just remove from menu
```

**Lesson:** Don't assume method behavior from name - verify what it does

---

### 5. Leftover Books Disappearing

**Problem:**
- 6 books Level I → Should get 1 Level III + 1 Level II
- Actually got: 1 Level III only (Level II leftover disappeared)

**Root Cause:**
- Algorithm paired books and tracked final result
- Leftover books at intermediate levels were NOT returned to player

**Fix:**
```java
// Collect all leftover books from booksByLevel map
for (int level = 1; level <= maxLevel; level++) {
    List<BookCandidate> leftovers = booksByLevel.getOrDefault(level, new ArrayList<>());
    for (BookCandidate leftover : leftovers) {
        leftoverBooks.add(leftover);
    }
}

// Give leftover books to player
for (BookData leftover : leftoverBooks) {
    InventoryUtils.addItem(player, Arrays.asList(leftover.getItemStack()));
}
```

**Lesson:** Track ALL outputs, not just primary result - leftovers matter

---

### 6. Not Collecting Books Above Base Level

**Problem:**
- Have: 2 I, 1 II, 1 III, 1 V
- Click Level I → Should craft to Level IV
- Actually: Only collected Level I books, ignored II and III

**Root Cause:**
- Algorithm skipped books above base level to prevent issues
- But this prevented legitimate multi-level combining

**Fix:**
```java
// BEFORE (WRONG)
if (enchant.getLevel() > baseLevel) {
    continue;  // Skip higher level books
}

// AFTER (CORRECT)
// Collect ALL levels (validation at end handles invalid cases)
if (enchant.getLevel() >= maxLevel) {
    continue;  // Only skip max level books
}
```

**Lesson:** Validation should happen at the end, not during collection

---

### 7. Leftover Books Duplicating

**Problem:**
- Have: 2 I, 1 II, 1 III, 1 V
- After FastCraft: 1 IV, 2 V (Level V duplicated!)

**Root Cause:**
- Level V was collected but never paired (odd number)
- Stayed in inventory slot 2 (not removed)
- ALSO given back as "leftover"
- Player got it twice!

**Fix:**
```java
for (BookCandidate leftover : leftovers) {
    // CRITICAL: Only give back books CREATED through combining
    // Original inventory books that weren't used should stay in inventory
    if (leftover.fromInventory) {
        continue;  // Skip - already in player's inventory
    }

    // Give back combined books that became leftovers
    leftoverBooks.add(leftoverBook);
}
```

**Lesson:** Distinguish between "leftover from pairing" vs "never used" - different handling

---

## Root Cause Analysis

### Why Issues Kept Recurring

#### 1. Incomplete Requirements Understanding
- **Missing:** Clear definition of FastCraft vs Regular mode
- **Missing:** What counts toward price (inventory vs total)
- **Missing:** Leftover book handling rules
- **Missing:** Mode transition rules

#### 2. Fixing Symptoms Instead of Root Causes
- Fixed price display without understanding consumption logic
- Fixed mode switching without clearing state
- Each fix broke something else due to incomplete understanding

#### 3. Insufficient Upfront Examples
- No comprehensive test cases showing expected behavior
- Edge cases discovered during testing, not upfront
- User provided examples AFTER issues occurred

#### 4. Assumptions Without Verification
- Assumed `returnBook()` should return books (name implies it!)
- Assumed leftovers don't need special handling
- Assumed higher-level books shouldn't be collected

---

## How to Improve Prompts (Guide for Future)

### 1. Define Modes Explicitly

**Bad Prompt:**
> "Add FastCraft feature that combines books from inventory"

**Good Prompt:**
> "There are TWO modes:
> - **FastCraft Mode:** Triggers when 1 book in menu. Scans inventory for matching books and combines all together.
> - **Regular Mode:** Triggers when 2 books in menu. Combines just those 2 books.
>
> Mode switching:
> - Adding 1st book → FastCraft
> - Adding 2nd book → Switch to Regular (clear FastCraft data)
> - Removing book from 2 → FastCraft
> - Removing book from 1 → Remind button"

### 2. Provide Concrete Examples Upfront

**Bad Prompt:**
> "Price should be based on books consumed"

**Good Prompt:**
> "Price calculation examples:
> - 6 books Level I → 1 Level III (consumes 4 inventory + 1 menu)
>   - Price = 4 × 1000 = 4000 (NOT 5 × 1000)
>   - Menu book consumed but result replaces it
>   - Net cost = inventory books only
>
> - 2 books Level I (regular mode) → 1 Level II
>   - Price = 1 × 1000 = 1000 (single base cost)"

### 3. State Invariants and Constraints

**Bad Prompt:**
> "Books should be consumed when crafting"

**Good Prompt:**
> "Invariants (MUST always be true):
> - Books never duplicate (consumed exactly once)
> - Price only counts inventory books (menu book free)
> - Leftover books always returned to player
> - Original inventory books not used stay in place
> - Only books created through combining need to be given back"

### 4. Show Edge Cases Explicitly

**Bad Prompt:**
> "Handle odd numbers of books"

**Good Prompt:**
> "Edge cases to handle:
>
> **Case 1: Odd number at base level**
> - Input: 5 books Level I
> - Combining: I+I=II, I+I=II, leftover I → II+II=III
> - Output: 1 Level III + 1 leftover Level I
> - Leftover I should be returned (created through odd remainder)
>
> **Case 2: Incompatible levels**
> - Input: 1 book I (menu) + 1 book VIII (inventory)
> - Combining: Can't pair (different levels)
> - Validation: Reject (slotsToRemoveCount = 0)
> - Output: Show REMIND button, not FastCraft
>
> **Case 3: Multi-level combining**
> - Input: 2 I, 1 II, 1 III
> - Combining: 2I→II, 2II→III, 2III→IV
> - Output: 1 Level IV
> - Books consumed: All 4 (2I + 1II + 1III)"

### 5. Explain the "Why" Not Just "What"

**Bad Prompt:**
> "FastCraft should only work with 1 book in menu"

**Good Prompt:**
> "FastCraft should only work with 1 book in menu BECAUSE:
> - Regular mode handles 2-book combining (simpler logic)
> - FastCraft's purpose is to scan inventory for ADDITIONAL books
> - With 2 books already in menu, no need to scan
> - Prevents confusion about which book is the 'base'"

### 6. Provide Reference Implementation or Tests

**Good Prompt:**
> "Expected behavior (test cases):
>
> ```java
> @Test
> void testFastCraft_6BooksLevelI() {
>     // Given: 6 books Level I (1 menu + 5 inventory)
>     // When: FastCraft executes
>     // Then:
>     //   - Result: 1 Level III
>     //   - Leftover: 1 Level II
>     //   - Books consumed: All 6
>     //   - Price: 5000 (5 inventory books)
> }
> ```"

---

## Why Mode Distinction Was Missed

### 1. Name Ambiguity
- "FastCraft" doesn't clearly indicate "1-book mode"
- Could mean "faster crafting" (speed) not "mode switch"

### 2. Legacy Code Not Read Carefully
- Legacy BookCraft had the distinction
- I didn't read it thoroughly before starting
- Assumed I understood the requirements

### 3. Gradual Feature Creep
- Started with simple "combine books from inventory"
- Mode distinction added later as bug fix
- Should have been in original spec

### 4. Lack of State Diagram
- No visual representation of mode transitions
- Didn't see: 0 books → 1 book → 2 books flow
- Text description wasn't enough

**What Would Help:**
```
State Diagram:

[0 books] --add book--> [1 book: FastCraft] --add book--> [2 books: Regular]
    ^                         |                              |
    |                    remove book                    remove book
    +-------------------------+------------------------------+
```

---

## Recommended Documentation Structure

For future complex features, provide:

### 1. Overview
- What is this feature?
- Why is it needed?
- How does it differ from existing features?

### 2. Modes/States
- List all modes
- Transition diagram
- Conditions for each mode

### 3. Examples (Given/When/Then)
- Happy path
- Edge cases
- Error cases

### 4. Invariants
- What must ALWAYS be true
- What should NEVER happen

### 5. Business Logic
- Calculations (with examples)
- Pricing rules
- Validation rules

### 6. Data Flow
- Where data comes from
- How it's transformed
- Where it goes

---

## Final Recommendations

### For User (Prompt Engineering)

1. **Start with modes/states** - Define boundaries first
2. **Provide 3-5 concrete examples** - Cover normal + edge cases
3. **State invariants explicitly** - "Books never duplicate"
4. **Explain WHY not just WHAT** - Helps AI understand intent
5. **Reference existing code** - "Like X but different because Y"
6. **Use diagrams if possible** - State machines, flow charts

### For AI Assistant (Self-Improvement)

1. **Ask clarifying questions upfront** - Don't assume
2. **Summarize understanding back** - Verify before coding
3. **Think about state management** - Mode switches, data cleanup
4. **Consider edge cases early** - Odd numbers, empty, max values
5. **Read legacy code thoroughly** - Clues about requirements
6. **Test incrementally** - Don't fix everything at once
7. **Check for duplication invariant** - Items should never duplicate

---

## Success Metrics (What Worked)

### Good Aspects of This Process

1. **Detailed logging** - Made debugging much easier
2. **Incremental fixes** - Each issue isolated and fixed
3. **User patience** - Provided clear feedback each iteration
4. **Concrete examples** - User showed actual inputs/outputs
5. **Test-driven** - User tested each fix immediately

### What Made It Harder

1. **Requirements emerged gradually** - Not all upfront
2. **Mode distinction unclear** - FastCraft vs Regular
3. **Multiple interacting fixes** - Each change affected others
4. **Assumption-driven development** - Should have asked more

---

## Conclusion

The FastCraft feature is now working correctly, but required 7 major iterations to fix:
1. Price calculation
2. Mode switching
3. State cleanup
4. Book consumption
5. Leftover handling
6. Level collection
7. Duplication prevention

**Key Takeaway:** Upfront clarity about modes, states, and invariants would have prevented most issues. Providing concrete examples with expected inputs/outputs is more valuable than general descriptions.

**For Future Features:**
- Define ALL modes explicitly
- Show 5+ examples covering edge cases
- State invariants ("never duplicate")
- Explain WHY behind each rule
- Provide state transition diagram

This would reduce implementation from 7 iterations to likely 1-2 iterations.
