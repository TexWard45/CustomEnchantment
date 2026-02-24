# Phase 7 — Agent Tool Interfaces

## Status: IMPLEMENTED

All 12 MCP tools are live in `.claude/mcp/server.py`. All 8 agent prompts updated with knowledge tool sections.

## Purpose

Expose the Phase 1-6 knowledge infrastructure (indexes, summaries, caching) as MCP tools that all agents use automatically, replacing raw Glob/Grep/Read for code navigation.

## Implementation

### Architecture

All tools are implemented in the existing Python MCP server:
- **`.claude/mcp/server.py`** — 12 tool functions (5 existing + 7 new)
- **`.claude/mcp/lookup.py`** — Pure JSON index loaders with lazy caching

Data sources:
- `classes.json` (541 classes) → `find_class`, `get_class_dependencies`, `get_class_dependents`
- `file-hashes.json` (545 files) → `get_file_summary`, reverse dependency map
- `entry-points.json` → `get_entry_points`
- `PLUGIN-SUMMARY.md` → `get_plugin_summary`
- All indexes combined → `analyze_impact`

### Tool Catalog (12 tools)

```
SEARCH (Phase 4 — existing)
├── search_code(query, top_k?, types?, module?, tags?)     ✅ MCP
├── search_enchantments(query, top_k?, group?, ...)        ✅ MCP
├── search_configs(query, top_k?)                          ✅ MCP

CONTEXT RETRIEVAL (existing + new)
├── get_module_summary(module_name, section?)              ✅ MCP (existing)
├── get_plugin_summary()                                   ✅ MCP (new)
├── get_file_summary(path_or_class)                        ✅ MCP (new)

CODE NAVIGATION (new)
├── find_class(name, fuzzy?)                               ✅ MCP (new)
├── get_class_dependencies(class_name)                     ✅ MCP (new)
├── get_class_dependents(class_name)                       ✅ MCP (new)
├── get_entry_points(module?)                              ✅ MCP (new)

ANALYSIS (new)
├── analyze_impact(class_name, depth?)                     ✅ MCP (new)

MAINTENANCE (existing)
└── reindex(force?)                                        ✅ MCP (existing)
```

### Excluded (handled elsewhere)
- `get_context_plan` → `/context-selector` skill (agent instructions reference it)
- `check_conventions` → Conventions in CLAUDE.md rules
- `run_tests/run_build` → `/gradle-test`, `/gradle-build` skills
- `find_references` → LSP `findReferences` tool (built-in)

### Tool Access Matrix

| Agent | Primary MCP Tools | Purpose |
|-------|------------------|---------|
| planner | search_code, get_module_summary, get_plugin_summary, analyze_impact | Scope understanding |
| architect | get_plugin_summary, analyze_impact, get_entry_points, get_class_dependencies | Architecture analysis |
| tdd-guide | search_code, find_class, get_file_summary, get_class_dependencies | Test target discovery |
| code-reviewer | find_class, get_class_dependents, get_file_summary, analyze_impact | Change impact review |
| security-reviewer | search_code, get_entry_points, find_class, get_class_dependents | Attack surface mapping |
| build-error-resolver | find_class, get_class_dependencies, get_file_summary | Error class lookup |
| doc-updater | get_module_summary, get_plugin_summary, find_class | Doc verification |
| issue-creator | search_code, get_module_summary, get_plugin_summary | Context gathering |

### Context Loading Protocol (all agents)

1. ALWAYS call `get_plugin_summary()` first for orientation
2. Use `search_code()` or `find_class()` to identify relevant classes
3. Load `get_module_summary()` for affected modules (max 4)
4. Use `get_file_summary()` before reading full source files
5. Only Read full source files when cached info is insufficient

## Files Modified

| File | Action | Description |
|------|--------|-------------|
| `.claude/mcp/lookup.py` | CREATED | Index loaders: load_classes, load_file_hashes, load_entry_points, build_reverse_deps |
| `.claude/mcp/server.py` | MODIFIED | Added 7 new tool functions + lookup imports |
| `.claude/agents/planner.md` | MODIFIED | Added Knowledge Tools section |
| `.claude/agents/architect.md` | MODIFIED | Added Knowledge Tools section |
| `.claude/agents/tdd-guide.md` | MODIFIED | Added Knowledge Tools section |
| `.claude/agents/code-reviewer.md` | MODIFIED | Added Knowledge Tools section |
| `.claude/agents/security-reviewer.md` | MODIFIED | Added Knowledge Tools section |
| `.claude/agents/build-error-resolver.md` | MODIFIED | Added Knowledge Tools section |
| `.claude/agents/doc-updater.md` | MODIFIED | Added Knowledge Tools section |
| `.claude/agents/issue-creator.md` | MODIFIED | Added Knowledge Tools section |

## Usage Examples

```python
# Find a class and understand its role
find_class("CECaller")
# → CECaller | Module: EnchantModule | File: .../CECaller.java | Tags: enchantment, combat

# What would break if we change CECaller?
analyze_impact("CECaller", depth=2)
# → Risk: HIGH | 15 affected classes | 3 affected listeners

# Get all listeners in the plugin
get_entry_points()
# → 15 listeners, 9 commands with events and handlers

# Quick file info without reading source
get_file_summary("CECaller")
# → Summary, methods, dependencies, tags from cache
```

## Dependencies

- **Phase 2** — `classes.json`, `entry-points.json`, `dependencies.json` provide raw data
- **Phase 3** — `PLUGIN-SUMMARY.md`, module summaries for context retrieval
- **Phase 4** — MCP server infrastructure, search_code/search_enchantments/search_configs
- **Phase 6** — `file-hashes.json` cache for get_file_summary and reverse deps

## Success Criteria

- [x] All 12 tools registered and callable via MCP
- [x] Each agent's prompt updated with Knowledge Tools section
- [x] Tool access matrix defined per agent role
- [x] Context loading protocol documented in each agent
- [ ] Tool response time < 5 seconds for all operations (verify at runtime)
- [ ] Token savings measurable vs. manual exploration (future eval)

---
**Priority:** P2 | **Effort:** M | **Labels:** `feature`, `infrastructure`, `phase-7`, `effort:M`
