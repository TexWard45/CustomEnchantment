---
name: workflow-xxl
description: Cross-plugin workflow for multi-plugin features. Ecosystem analysis, shared API design, per-plugin L workflows. Context budget 25K/phase.
---

# XXL Workflow: Cross-Plugin Feature

For features that span multiple plugins or require shared API changes. Coordinates across repositories with ecosystem-level analysis.

## Context Budget: 25,000 tokens per phase

## Steps

### 1. ECOSYSTEM ANALYSIS (<30 min)
1. Read `ECOSYSTEM.md` (Level 5 summary) if available
2. Read `PLUGIN-SUMMARY.md` for each affected plugin
3. Identify shared interfaces and APIs between plugins
4. Map cross-plugin dependencies
5. Use `--add-dir` to load context from other plugin directories

### 2. API DESIGN (architect agent)
1. Invoke **architect** agent with:
   - Full feature description
   - All affected plugin summaries
   - Existing cross-plugin integration points
2. Design shared API:
   - Define contracts between plugins
   - Decide where shared code lives (BafFramework? Shared library?)
   - Version compatibility plan
3. Get approval from all stakeholders

### 3. PLAN (planner agent)
1. Invoke **planner** agent with:
   - API design from Step 2
   - Per-plugin impact analysis
2. Order implementation:
   - Start with shared API / framework changes
   - Then implement in each plugin (dependency order)
   - Integration tests across plugins last
3. Create per-plugin task checklists

### 4. IMPLEMENT PER PLUGIN

For each affected plugin (in dependency order):

```
a. Switch to plugin's worktree/directory
b. Follow L workflow (Steps 1-6) for that plugin's changes
c. Verify build passes independently
d. Document inter-plugin contracts
```

### 5. INTEGRATION TESTING
- Build all affected plugins
- Test plugin interaction (manual or integration tests)
- Performance test under load if applicable
- Verify no regressions in any plugin

### 6. DOCUMENT
- Update ecosystem summary
- Update each plugin's PLUGIN-SUMMARY.md
- Document shared API contracts
- Document cross-plugin patterns in memory
- Run `/cache-update` for each plugin

## Agents Used

| Agent | When | Model |
|-------|------|-------|
| architect | Step 2 — API design | opus |
| planner | Step 3 — cross-plugin planning | sonnet |
| All L-workflow agents | Step 4 — per-plugin implementation | (varies) |
| security-reviewer | Step 5 — cross-plugin security | sonnet |

## Cross-Plugin Context Loading

Use `--add-dir` to reference other plugin codebases:

```
When working on PluginA but needing context from PluginB:
1. Load PluginB's PLUGIN-SUMMARY.md via --add-dir
2. Use MCP search_code scoped to PluginB
3. Read only the shared API surface, not internals
```

## Coordination Rules

- **One plugin at a time**: Complete one plugin's changes before moving to the next
- **API first**: Shared API/framework changes must be done and tested before plugin implementations
- **Independent builds**: Each plugin must build and test independently before integration
- **Version pins**: Pin dependency versions to avoid breaking changes during implementation
