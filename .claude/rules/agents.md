# Agent Orchestration

## Available Agents

Located in `.claude/agents/`:

| Agent | Model | Purpose | When to Use |
|-------|-------|---------|-------------|
| planner | sonnet | Implementation planning | Complex features, refactoring |
| architect | opus | System design | Architectural decisions |
| tdd-guide | sonnet | Test-driven development | New features, bug fixes |
| code-reviewer | sonnet | Code review | After writing code |
| security-reviewer | sonnet | Security analysis | Before commits |
| build-error-resolver | haiku | Fix Java/Gradle build errors | When build fails |
| doc-updater | haiku | Documentation updates | Updating codemaps/docs |
| issue-creator | haiku | GitHub issue generation | Creating well-structured issues |

## Model Selection for Subagent Tasks

**MANDATORY: ALWAYS set the `model` parameter** when using the Task tool for ad-hoc subagents. Never rely on the default — explicitly specify the model.

When spawning ad-hoc subagents via the Task tool, select the model by task complexity:

| Model | Use For | Examples |
|-------|---------|---------|
| **haiku** | Routine, templated, search tasks | File searches, build verification, doc generation, issue creation, codebase exploration |
| **sonnet** | Standard development work | Code review, TDD, planning, security review, feature implementation |
| **opus** | Deep reasoning required | Architecture decisions, complex debugging, cross-module refactoring |

**Default to `model: "haiku"`** for ALL Task tool calls unless the task explicitly requires code reasoning or judgment. When in doubt, use haiku — it handles 80% of subagent tasks.

```
# CORRECT: Always set model explicitly
Task(model: "haiku", subagent_type: "Explore", prompt: "Find all listeners...")
Task(model: "haiku", subagent_type: "Bash", prompt: "Run gradle build...")
Task(model: "sonnet", subagent_type: "code-reviewer", prompt: "Review this code...")

# WRONG: Missing model parameter
Task(subagent_type: "Explore", prompt: "Find all listeners...")
```

## Immediate Agent Usage

No user prompt needed:
1. Complex feature requests - Use **planner** agent
2. Architectural decisions - Use **architect** agent
3. Code just written/modified - Use **code-reviewer** agent
4. Bug fix or new feature - Use **tdd-guide** agent

## Parallel Task Execution

ALWAYS use parallel Task execution for independent operations:

```markdown
# GOOD: Parallel execution
Launch 3 agents in parallel:
1. Agent 1: Security analysis of auth module
2. Agent 2: Code review of new listener
3. Agent 3: Build verification

# BAD: Sequential when unnecessary
First agent 1, then agent 2, then agent 3
```

## Multi-Perspective Analysis

For complex problems, use split role sub-agents:
- Factual reviewer
- Senior engineer
- Security expert
- Consistency reviewer
- Redundancy checker
