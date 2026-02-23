# Phase 7 — Agent Tool Interfaces

## Purpose

Define a unified tool API that all agents (planner, architect, tdd-guide, code-reviewer, etc.) use to interact with the knowledge infrastructure. Tools abstract away the complexity of indexing, search, and summarization into simple function calls.

## Design

### Tool Catalog

```
CODE NAVIGATION
├── search_code(query, scope?)        # Semantic + keyword search
├── find_class(name)                  # Exact class lookup with metadata
├── find_references(symbol)           # Where is this used?
├── get_dependencies(class)           # What does this depend on?
├── get_dependents(class)             # What depends on this?
├── get_entry_points(module?)         # Listeners, commands, tasks

CONTEXT RETRIEVAL
├── get_module_summary(module)        # Level 3 summary
├── get_plugin_summary(plugin?)       # Level 4 summary
├── get_file_summary(file)            # Level 2 summary
├── get_context_plan(task_description)# Phase 5 context selection

CONFIG & SCHEMA
├── get_config_schema(yamlFile)       # YAML → Java class mapping
├── search_config(path_pattern)       # Find config by YAML path
├── get_enchantment(name)             # Enchantment definition with full metadata

BUILD & TEST
├── run_tests(module?)                # Execute tests
├── run_build()                       # Compile project
├── get_test_coverage(module?)        # JaCoCo coverage report

ANALYSIS
├── analyze_impact(file_or_class)     # What's affected by changing this?
├── detect_patterns(directory)        # What patterns are used here?
├── check_conventions(file)           # Does this follow project conventions?
```

### Tool Implementations

#### search_code(query, scope?)

```pseudocode
function search_code(query, scope="all"):
  """
  Semantic + keyword hybrid search across the codebase.

  Args:
    query: Natural language or keyword search
    scope: "all", "enchantments", "conditions", "effects",
           "configs", "listeners", "commands", module name

  Returns:
    List of {file, class, summary, relevance, tags}
  """

  # If Phase 4 MCP server available, use it
  if mcpServer.available("code-search"):
    return mcpServer.call("search_code", {query, scope})

  # Fallback: index-based search
  results = []

  # Tag-based search
  tags = extractTags(query)
  if tags:
    results += index.queryByTags(tags, scope)

  # Keyword search in class names and summaries
  keywords = extractKeywords(query)
  for keyword in keywords:
    results += index.searchNames(keyword)
    results += index.searchSummaries(keyword)

  # Deduplicate and rank
  return dedup(results).sortBy("relevance")[:10]
```

#### analyze_impact(file_or_class)

```pseudocode
function analyze_impact(target):
  """
  Analyze what would be affected by changing target file/class.

  Returns:
    - Direct dependents (classes that import/use target)
    - Indirect dependents (transitive, up to depth 3)
    - Config files that reference target
    - Tests that cover target
    - Entry points affected (listeners, commands)
  """
  direct = index.getDependents(target)
  indirect = []
  for dep in direct:
    indirect += index.getDependents(dep)  # depth 2
    for dep2 in index.getDependents(dep):
      indirect += index.getDependents(dep2)  # depth 3

  configs = index.getConfigReferences(target)
  tests = glob(f"src/test/**/*{target.simpleName}*Test.java")
  entryPoints = [ep for ep in index.entryPoints if target in ep.dependencies]

  return ImpactAnalysis(
    direct=direct,
    indirect=dedup(indirect),
    configs=configs,
    tests=tests,
    entryPoints=entryPoints,
    riskLevel=assessRisk(len(direct), len(indirect))
  )
```

#### get_context_plan(task_description)

```pseudocode
function get_context_plan(task):
  """
  Use Phase 5 context engine to generate an optimal context loading plan.

  Returns ordered list of files/summaries to load with token budget.
  """
  classification = classifyTask(task)
  candidates = findRelevantEntities(task, classification)
  scored = [(entity, scoreRelevance(entity, task)) for entity in candidates]
  scored.sort(reverse=True)

  plan = []
  tokenBudget = 20000  # Available context tokens
  for entity, score in scored:
    tokens = estimateTokens(entity)
    if tokenBudget - tokens >= 0:
      plan.append({"load": entity.path, "tokens": tokens, "reason": score.explanation})
      tokenBudget -= tokens

  return ContextPlan(items=plan, totalTokens=20000 - tokenBudget)
```

### Tool Access by Agent

| Agent | Tools Available | Primary Use |
|-------|----------------|-------------|
| planner | search_code, get_module_summary, get_plugin_summary, analyze_impact | Scope understanding |
| architect | get_plugin_summary, analyze_impact, detect_patterns, get_dependencies | Architecture analysis |
| tdd-guide | search_code, find_class, run_tests, get_test_coverage | Test creation |
| code-reviewer | find_references, get_dependents, check_conventions, analyze_impact | Review quality |
| security-reviewer | search_code("user input"), find_references, get_entry_points | Security surface |
| build-error-resolver | find_class, get_dependencies, run_build | Error resolution |

### Integration with Existing Agents

Update each agent's `.claude/agents/{name}.md` to reference available tools:

```markdown
# Enhanced Agent Template

## Available Knowledge Tools
Before starting work, use these tools to gather context efficiently:

1. `get_context_plan("{task description}")` — Get optimal context loading plan
2. `get_module_summary("{module}")` — Understand module purpose and API
3. `search_code("{query}")` — Find relevant code by description
4. `find_references("{symbol}")` — Find where a symbol is used
5. `analyze_impact("{class}")` — Understand change blast radius

## Context Loading Protocol
1. ALWAYS call get_context_plan() first
2. Load recommended context in order
3. Only read full files when summaries are insufficient
4. Never load more than 5 full files
```

## Technical Implementation

### As Claude Code Skills

Each tool becomes a skill that agents invoke:

```
.claude/skills/
├── search-code/SKILL.md         # Wraps MCP search or index query
├── analyze-impact/SKILL.md      # Dependency traversal
├── context-plan/SKILL.md        # Phase 5 context selection
└── convention-check/SKILL.md    # Convention validation
```

### As MCP Server Tools

For tools that benefit from persistent state (search_code, get_dependencies):

```json
// .mcp.json
{
  "mcpServers": {
    "bukkit-knowledge": {
      "command": "node",
      "args": ["mcp-server/index.js"],
      "env": {
        "PROJECT_ROOT": ".",
        "INDEX_PATH": ".claude/docs/codemap/index"
      }
    }
  }
}
```

### As Bash Wrappers

For simple tools that just read index files:

```bash
# get_module_summary.sh
#!/bin/bash
MODULE=$1
cat ".claude/docs/codemap/summaries/${MODULE}/MODULE-SUMMARY.md"
```

## Dependencies

- **Phase 2** — Index provides data for find_class, find_references, get_dependencies
- **Phase 4** — MCP server provides search_code implementation
- **Phase 5** — Context engine provides get_context_plan

## Success Criteria

- [ ] All tools in catalog have working implementations (skill, MCP, or Bash)
- [ ] Each agent's prompt updated to reference available tools
- [ ] Agents use tools instead of raw grep/glob for code navigation
- [ ] Tool response time < 5 seconds for all operations
- [ ] Tools work across multiple projects (not hardcoded to one)
- [ ] Agents load 50% fewer tokens when using tools vs. manual exploration

---

## GitHub Issue

### Title
`feat: Phase 7 — Unified Agent Tool Interfaces`

### Description

Create a standard tool API that all specialized agents (planner, architect, tdd-guide, code-reviewer, etc.) use for code navigation, context retrieval, and impact analysis. Tools abstract the knowledge infrastructure into simple function calls.

### Background / Motivation

Agents currently use raw Glob/Grep/Read to find code, which is inconsistent and inefficient. A unified tool API ensures every agent benefits from the index, summaries, and semantic search built in Phases 1-6. It also enables measuring and optimizing how agents interact with the knowledge base.

### Tasks

- [ ] Define full tool catalog (navigation, context, config, build, analysis)
- [ ] Implement search_code tool (skill wrapping MCP or index query)
- [ ] Implement find_class tool (exact lookup from index)
- [ ] Implement find_references tool (dependency graph traversal)
- [ ] Implement get_module_summary tool (read from summaries)
- [ ] Implement get_context_plan tool (Phase 5 engine)
- [ ] Implement analyze_impact tool (transitive dependency analysis)
- [ ] Update all 8 agent prompts with tool usage instructions
- [ ] Define tool access matrix (which agents get which tools)
- [ ] Test each tool with representative queries
- [ ] Measure token savings vs. agent manual exploration

### Acceptance Criteria

- [ ] All agents use tools for code navigation (not raw grep/glob)
- [ ] Tool response time < 5 seconds
- [ ] analyze_impact correctly identifies transitive dependencies
- [ ] Token usage reduced by 50%+ per agent task

---
**Priority:** P2 | **Effort:** M | **Labels:** `feature`, `infrastructure`, `phase-7`, `effort:M`
