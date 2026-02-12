# Git Worktree Workflow for Issue Implementation

## MANDATORY: Use Worktrees for All Issue Implementation

When implementing any GitHub issue or feature ticket, **ALWAYS** use git worktrees to isolate work.

## Workflow Steps

### 1. Create Worktree Before Starting

```bash
git fetch origin
git worktree add ../CustomEnchantment-{issue-number}-{short-description} -b {branch-name}
```

### 2. Naming Conventions

**Worktree directory:**
```
../CustomEnchantment-{issue-number}-{short-description}
```

Examples:
- `../CustomEnchantment-15-fix-nms-attribute`
- `../CustomEnchantment-31-feat-custom-enchant`

**Branch naming:**
```
{issue-number}-{type}-{short-description}
```

Types: `fix`, `feat`, `refactor`, `docs`, `test`, `chore`

### 3. Implementation

All work must be done in the worktree directory, NOT the main repository.

### 4. Completion

```bash
git push -u origin {branch-name}
gh pr create
# After merge:
git worktree remove ../CustomEnchantment-{issue-number}-{short-description}
git branch -d {branch-name}
```

## Exceptions

Skip worktree for:
- Documentation-only changes
- Quick 1-2 line hotfixes
- Changes explicitly requested on master
