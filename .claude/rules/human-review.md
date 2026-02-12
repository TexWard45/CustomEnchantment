# Human Review Enforcement

## CRITICAL: Human Approval Required

Claude must NEVER autonomously complete git operations that bypass human review.

---

## Forbidden Actions (WITHOUT Explicit Human Request)

### Never Auto-Commit
```bash
# FORBIDDEN without explicit user request
git commit -m "..."
```

### Never Push to Remote
```bash
# FORBIDDEN without explicit user request
git push
git push -u origin branch-name
```

### Never Merge Branches
```bash
# FORBIDDEN - always requires human action
git merge main
git merge feature-branch
```

### Never Force Push
```bash
# ABSOLUTELY FORBIDDEN
git push --force
git push -f
```

---

## Required Workflow After Implementation

### Step 1: Stage Changes Only
```bash
# OK to stage files
git add src/main/java/com/example/MyClass.java
git add -A  # Only if user approves
```

### Step 2: Show Summary for Review
After completing work, Claude MUST:

1. **Run verification checks:**
   ```bash
   ./gradlew build test
   ```

2. **Display change summary:**
   ```bash
   git status
   git diff --stat
   ```

3. **List files modified:**
   - New files created
   - Existing files modified
   - Files deleted

4. **Provide commit message suggestion:**
   ```
   Suggested commit message:
   feat: Add custom enchant system

   - Add AbstractEnchant base class
   - Add EnchantRegister with strategy pattern
   - Add enchant listener for item events
   ```

### Step 3: Wait for Human Approval
Claude must explicitly state:
```
Changes are staged and ready for review.
Please review the changes and run:
  git commit -m "your message"
  git push

Or let me know if you'd like me to commit with the suggested message.
```

---

## Exceptions

Claude MAY commit/push only when user explicitly requests:
- "commit these changes"
- "commit and push"
- "make a commit"
- "push to origin"
- "create a PR" (implies push)

Even with explicit request, Claude should:
1. Show the commit message before committing
2. Confirm the target branch before pushing
3. Never force push to main/master

---

## Rationale

1. **Human oversight** - Prevents unintended changes from being committed
2. **Code review** - Allows human to review before permanent history
3. **Safety** - Prevents accidental pushes to production branches
4. **Control** - User maintains full control of git history

---

## Quick Reference

| Action | Without Request | With Request |
|--------|-----------------|--------------|
| `git add` | OK | OK |
| `git status` | OK | OK |
| `git diff` | OK | OK |
| `git commit` | FORBIDDEN | OK (show message first) |
| `git push` | FORBIDDEN | OK (confirm branch) |
| `git merge` | FORBIDDEN | FORBIDDEN (user action) |
| `git push --force` | FORBIDDEN | FORBIDDEN |
