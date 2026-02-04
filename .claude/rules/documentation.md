# Documentation Rules

## Mandatory Documentation Updates

Every time a new feature, component, or significant change is added, documentation MUST be updated.

### After EVERY Code Change

#### 1. Phase Changes
When completing any phase:
- Create/update `.claude/docs/phases/PHASE-X.md` with:
  - What was built
  - Files created/modified
  - Dependencies added
  - Technical details

- Create/update `.claude/docs/review/PHASE-X-REVIEW.md` with:
  - Code quality assessment
  - Issues found
  - Recommendations

#### 2. Feature Changes
When adding/modifying features:
- Update `.claude/docs/CHANGELOG.md` with:
  - PR number and brief description
  - Categorize under: Added, Changed, Fixed, Removed, or Documentation
  - Group by date (YYYY-MM-DD format)

Example entry:
```markdown
### Added
- **Feature name** (#PR): Brief description of what was added
```

#### 3. Component Changes
When creating/modifying components:
- Update `.claude/docs/tech/COMPONENTS.md` with:
  - Component name
  - Props interface
  - Usage example

#### 4. API Changes
When adding/modifying tRPC routers:
- Update `.claude/docs/tech/API.md` with:
  - Router name
  - Procedures
  - Input/output types

#### 5. Database Changes
When modifying Prisma schema:
- Update `.claude/docs/tech/DATABASE.md` with:
  - Model changes
  - New relations
  - Migration notes

#### 6. Bug Fixes
When fixing bugs:
- Add to `CHANGELOG.md` under "Fixed"
- Update relevant review doc if it was a known issue

### Documentation Checklist (Use Before Commit)

Before every commit, verify:
- [ ] CHANGELOG.md updated (if user-facing change)
- [ ] Phase docs updated (if part of phase work)
- [ ] Tech docs updated (if API/component/schema change)
- [ ] README.md updated (if setup/usage changed)
- [ ] SETUP.md updated (if dependencies/config changed)

### Commit Message Format

Include docs in commit when updated:
- `feat: Add user auth` - missing docs
- `feat: Add user auth + update docs` - correct
- `feat: Add user auth` + separate `docs: Update for auth` - also correct

### Never Skip Documentation

Even for small changes:
- New utility function -> Update tech docs
- New hook -> Update HOOKS.md
- Config change -> Update SETUP.md
- Env variable added -> Update .env.example + SETUP.md

### File Locations Reference

```
.claude/docs/
├── PROJECT-SPEC.md      # Vision (update rarely)
├── UI-CONCEPT.md        # UI philosophy (update rarely)
├── SETUP.md             # Setup guide (update when deps/config change)
├── CHANGELOG.md         # Every release/feature
├── phases/              # Phase summaries
├── review/              # Code reviews
└── tech/                # Technical reference
    ├── ARCHITECTURE.md
    ├── API.md
    ├── COMPONENTS.md
    ├── DATABASE.md
    ├── HOOKS.md
    └── CONFIG.md
```

### Auto-Documentation Triggers

Use the `/update-docs` skill or doc-updater agent when:
- Completing a feature branch
- Before creating a PR
- After merging significant changes
- When requested by user

### Documentation Quality Standards

1. **Be Specific** - Include file paths, function names, component props
2. **Be Current** - Update immediately, not "later"
3. **Be Complete** - Cover all aspects (usage, props, examples)
4. **Be Accurate** - Test examples before documenting
5. **Be Concise** - Don't over-document obvious things
