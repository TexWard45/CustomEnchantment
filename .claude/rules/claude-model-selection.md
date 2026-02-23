# Claude Model Selection

## Model Selection Strategy

**Haiku** (fastest, lowest cost — use by default for subagents):
- Build error resolution, doc generation, issue creation
- File searches, grep operations, codebase exploration
- Test running, build verification
- Boilerplate and template-based generation

**Sonnet** (best coding model — use for development work):
- Feature implementation, bug fixes, code review
- TDD cycles, security review, planning
- Orchestrating multi-agent workflows

**Opus** (deepest reasoning — use sparingly):
- Complex architectural decisions
- Cross-module refactoring with subtle interactions
- Deep debugging of non-obvious issues

## Agent Model Assignments

| Agent | Model | Rationale |
|-------|-------|-----------|
| build-error-resolver | haiku | Pattern-matching on error messages |
| doc-updater | haiku | Template-based documentation |
| issue-creator | haiku | Structured issue generation |
| code-reviewer | sonnet | Requires code quality judgment |
| security-reviewer | sonnet | Requires careful security reasoning |
| tdd-guide | sonnet | Standard development workflow |
| planner | sonnet | Needs good reasoning for plans |
| architect | opus | Deep architectural reasoning |

## Ad-Hoc Task Tool Model Selection

When using the Task tool directly (not custom agents), set `model` explicitly:

```
model: "haiku"  → Explore agent, file search, build checks
model: "sonnet" → Code generation, review, analysis
model: "opus"   → Architecture, complex debugging
```

**Rule: Default to haiku. Upgrade only when task requires code reasoning.**

## Context Window Management

Avoid last 20% of context window for:
- Large-scale refactoring
- Feature implementation spanning multiple files
- Debugging complex interactions

## Ultrathink + Plan Mode

For complex tasks requiring deep reasoning:
1. Use `ultrathink` for enhanced thinking
2. Enable **Plan Mode** for structured approach
3. "Rev the engine" with multiple critique rounds
4. Use split role sub-agents for diverse analysis

## Build Troubleshooting

If build fails:
1. Use **build-error-resolver** agent (haiku)
2. Analyze error messages
3. Fix incrementally
4. Verify after each fix
