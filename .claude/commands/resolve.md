---
description: Complete issue resolution - merge PR, update docs, close issue, cleanup worktree. Requires human approval before merge.
---

# Resolve Command

Completes the full issue resolution workflow with human approval checkpoint.

## Usage

```
/resolve <issue-number>
/resolve              # Auto-detect from current branch (e.g., 39-feat-xyz → issue #39)
```

## What This Command Does

1. **Gather Status** - Show PR and issue state
2. **Human Approval** - Wait for explicit confirmation before merge
3. **Merge PR** - Squash merge with branch deletion
4. **Update Docs** - Update CHANGELOG and relevant documentation
5. **Cleanup** - Remove worktree, delete local branch, pull latest

## Workflow

### Step 1: Status Check

Display current state:

```
RESOLVE STATUS: Issue #<number>
================================
Issue:    #<number> - <title>
Status:   OPEN / CLOSED
PR:       #<pr-number> - <pr-title>
PR State: OPEN / MERGED / CLOSED
Branch:   <branch-name>
Worktree: <path> (if exists)
CI:       PASSING / FAILING / PENDING
```

### Step 2: Pre-Merge Checklist

Verify before proceeding:

- [ ] PR exists and is open
- [ ] CI checks passing (warn if not)
- [ ] No merge conflicts
- [ ] Issue is linked to PR

If any issues found, report and stop:
```
BLOCKERS FOUND:
- CI checks failing (2 failed)
- Merge conflicts detected

Please resolve before running /resolve again.
```

### Step 3: Human Approval Checkpoint

**CRITICAL**: Must wait for explicit user confirmation.

```
READY TO RESOLVE
================
This will:
1. Merge PR #<number> (squash) into main
2. Delete remote branch: <branch-name> (explicit deletion)
3. Close issue #<number> (if not auto-closed)
4. Pull latest main
5. Update documentation (CHANGELOG, phase docs)
6. Remove worktree: <path>
7. Delete local branch: <branch-name>

Type 'yes' or 'proceed' to continue, or 'cancel' to abort.
```

### Step 4: Execute Resolution

After user confirms:

```bash
# 1. Merge PR (squash)
gh pr merge <pr-number> --squash

# 2. Delete remote branch explicitly (--delete-branch flag is unreliable from worktrees)
git push origin --delete <branch-name>

# 3. Close issue if not auto-closed
gh issue close <issue-number>  # Only if still open

# 4. Change to main repo (not worktree)
cd <main-repo-path>

# 5. Pull latest (to get merged changes)
git pull origin main

# 6. Update documentation
# See Step 4.1 below for details

# 7. Remove worktree
git worktree remove <worktree-path> --force

# 8. Delete local branch
git branch -d <branch-name>
```

**IMPORTANT**: Always delete the remote branch explicitly with `git push origin --delete <branch-name>` after merge. The `--delete-branch` flag on `gh pr merge` is unreliable when running from a worktree.

### Step 4.1: Update Documentation

After merge, update relevant documentation:

**CHANGELOG.md** (always):
```markdown
## [Unreleased]

### Added
- <feature-description> (#<issue-number>)

### Fixed
- <fix-description> (#<issue-number>)
```

**Phase docs** (if applicable):
- Create/update `.claude/docs/phases/PHASE-X.md` for major features
- Update `.claude/docs/tech/` files if API/components changed

**Ask user**:
```
Documentation updates needed:
1. CHANGELOG.md - Add entry for #<issue-number>
2. [Optional] Phase docs if this was a major feature

Update CHANGELOG now? (yes/skip)
```

If user confirms, stage and commit docs:
```bash
git add CHANGELOG.md
git commit -m "docs: Update CHANGELOG for #<issue-number>"
git push origin main
```

### Step 5: Confirmation

Report completion:

```
RESOLVED: Issue #<number>
=========================
PR #<pr-number>: MERGED
Issue #<number>: CLOSED
CHANGELOG: UPDATED (or SKIPPED)
Worktree: REMOVED
Local branch: DELETED
Main branch: UPDATED

View: https://github.com/<owner>/<repo>/issues/<number>
```

## Edge Cases

### No PR Found
```
ERROR: No open PR found for issue #<number>
Create a PR first: gh pr create
```

### In Wrong Directory
```
ERROR: Not in a worktree for issue #<number>
Current directory: <path>
Expected: ../second-brain-<number>-*
```

### CI Failing
```
WARNING: CI checks are failing
- test-unit: FAILED
- lint: PASSED

Continue anyway? (yes/no)
```

### Already Merged
```
INFO: PR #<number> already merged
Proceeding with cleanup only...
```

## Arguments

$ARGUMENTS:
- `<issue-number>` - GitHub issue number (optional, auto-detected from branch)

## Related Commands

- `/plan` - Plan implementation before starting
- `/tdd` - Implement with test-driven development
- `/code-review` - Review code before resolving
- `/checkpoint` - Create checkpoints during work

## Related Rules

- `human-review.md` - Requires human approval before merge
- `git-workflow.md` - Git workflow standards
- `git-worktree.md` - Worktree workflow for issues

## Examples

### Basic Usage
```
User: /resolve 39

Claude:
RESOLVE STATUS: Issue #39
================================
Issue:    #39 - Add knowledge graph visualization
Status:   OPEN
PR:       #66 - feat: Add Knowledge Graph visualization (#39)
PR State: OPEN
Branch:   39-feat-knowledge-graph
Worktree: D:\...\second-brain-39-feat-knowledge-graph
CI:       PASSING

READY TO RESOLVE
================
This will:
1. Merge PR #66 (squash) into main
2. Delete remote branch: 39-feat-knowledge-graph
3. Close issue #39
4. Remove worktree
5. Delete local branch
6. Pull latest main

Type 'yes' to continue, or 'cancel' to abort.

User: yes

Claude:
RESOLVED: Issue #39
=========================
PR #66: MERGED
Issue #39: CLOSED
Worktree: REMOVED
Local branch: DELETED
Main branch: UPDATED
```

### Auto-Detect Issue
```
# When in worktree second-brain-42-fix-auth
User: /resolve

Claude: Detected issue #42 from branch name...
[continues with resolution flow]
```
