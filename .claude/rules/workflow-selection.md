# Workflow Selection

Before starting any implementation task, classify its size and follow the matching workflow.

## Size Classification

| Size | Criteria | Context Budget | Agents |
|------|----------|---------------|--------|
| **XS** | 1 file, < 20 lines | 3K tokens | None |
| **S** | 1-2 files, follows existing pattern | 8K tokens | code-reviewer |
| **M** | 3-5 files, one module | 15K tokens | planner + tdd + reviewer |
| **L** | 5-15 files, crosses modules | 20K/phase | all agents |
| **XL** | 15+ files, new module or major refactor | 20K/phase | all + module-create |
| **XXL** | Multiple plugins affected | 25K/phase | all + cross-project |

## Decision Tree

```
Is the task a question or research? → No workflow (research only)
                                    ↓ No
Single file, < 20 lines?           → XS
                                    ↓ No
1-2 files, existing pattern to copy? → S
                                    ↓ No
3-5 files, stays within one module? → M
                                    ↓ No
Crosses module boundaries?          → L
                                    ↓ No
Creates a new module or major refactor? → XL
                                    ↓ No
Affects multiple plugins?           → XXL
```

## Size Signals

### XS Signals
- "fix typo", "rename", "add log", "update message", "change default"
- User names the exact file and line
- One-liner or trivial change

### S Signals
- "add new condition/execute/effect", "add config field", "simple bugfix"
- Similar code already exists (pattern to follow)
- 1-2 classes created or modified

### M Signals
- "add feature to [module]", "refactor [class group]", "implement [behavior]"
- Requires planning but stays within one module
- Needs tests, code review

### L Signals
- "add system", "crosses [module A] and [module B]", "major feature"
- Changes touch 3+ modules or involve new data flows
- Needs architecture review and phased implementation

### XL Signals
- "new module", "framework migration", "plugin-wide refactor"
- Creates new directories, configs, commands, listeners
- Requires scaffolding and multi-phase delivery

### XXL Signals
- "shared API", "cross-plugin", "BafFramework change"
- Affects multiple repositories or plugin boundaries

## Escalation Rules

- If you discover the task is **larger** than classified: stop, re-classify, switch workflow
- If the task is **smaller** than classified: simplify (skip agents not needed)
- **Always start with the smallest workflow that fits** — over-engineering small tasks wastes tokens

## Workflow Invocation

Each size has a corresponding skill:

| Size | Skill |
|------|-------|
| XS | `/workflow-xs` |
| S | `/workflow-s` |
| M | `/workflow-m` |
| L | `/workflow-l` |
| XL | `/workflow-xl` |
| XXL | `/workflow-xxl` |
