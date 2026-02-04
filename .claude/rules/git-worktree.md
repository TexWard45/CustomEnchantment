# Git Worktree Workflow for Issue Implementation

## MANDATORY: Use Worktrees for All Issue Implementation

When implementing any GitHub issue or feature ticket, **ALWAYS** use git worktrees to isolate work.

## Workflow Steps

### 1. Create Worktree Before Starting

```bash
# From main repository
git fetch origin
git worktree add ../second-brain-{issue-number}-{short-description} -b {branch-name}
```

### 2. Naming Conventions

**Worktree directory:**
```
../second-brain-{issue-number}-{short-description}
```

Examples:
- `../second-brain-15-fix-topic-badge`
- `../second-brain-31-feat-timeline-view`
- `../second-brain-35-feat-pgvector`

**Branch naming:**
```
{issue-number}-{type}-{short-description}
```

Types: `fix`, `feat`, `refactor`, `docs`, `test`, `chore`

Examples:
- `15-fix-topic-badge`
- `31-feat-timeline-view`
- `35-feat-pgvector-search`

### 3. Change to Worktree Directory

```bash
cd ../second-brain-{issue-number}-{short-description}
```

**IMPORTANT:** All implementation work must be done in the worktree directory, NOT the main repository.

### 4. Implement the Feature

- Make changes in the worktree
- Commit frequently with descriptive messages
- Run tests and typecheck before finalizing

### 5. When Work is Complete

Use the `/resolve` command for streamlined completion:
```
/resolve {issue-number}
```

This handles:
1. Merge PR (with human approval)
2. Close issue
3. Remove worktree
4. Delete local branch
5. Pull latest main

**Manual alternative** (if not using /resolve):
```
1. Push branch: git push -u origin {branch-name}
2. Create PR: gh pr create
3. After merge, cleanup:
   - cd ../second-brain
   - git worktree remove ../second-brain-{issue-number}-{short-description}
   - git branch -d {branch-name}
```

## Example Workflow

**User:** "Implement issue #15"

**Claude:**
```bash
# 1. Fetch latest and create worktree
cd /d/VisualCodeWorkspace/second-brain
git fetch origin
git worktree add ../second-brain-15-fix-topic-badge -b 15-fix-topic-badge

# 2. Change to worktree
cd ../second-brain-15-fix-topic-badge

# 3. Implement changes...
# (make edits, run tests, commit)

# 4. Push and create PR
git push -u origin 15-fix-topic-badge
gh pr create

# 5. When ready to complete
/resolve 15
```

## Why Worktrees?

1. **Isolation** - Changes don't affect main branch until merged
2. **Parallel work** - Can work on multiple issues simultaneously
3. **Clean state** - Main repo stays on main branch for quick checks
4. **Easy cleanup** - Remove worktree after merge, no residual files

## Quick Reference Commands

```bash
# List all worktrees
git worktree list

# Create worktree with new branch
git worktree add ../second-brain-{N}-{desc} -b {N}-{type}-{desc}

# Remove worktree (after merge)
git worktree remove ../second-brain-{N}-{desc}

# Prune stale worktree references
git worktree prune
```

## Exceptions

Skip worktree creation for:
- Documentation-only changes (README, CHANGELOG)
- Quick hotfixes that are 1-2 lines
- Changes explicitly requested to be made directly on main

For these cases, still create a branch but can work in main repo:
```bash
git checkout -b {branch-name}
# ... make changes ...
git checkout main
```
