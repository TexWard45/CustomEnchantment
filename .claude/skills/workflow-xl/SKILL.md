---
name: workflow-xl
description: Architecture workflow for new modules or major refactors. 15+ files. Uses module scaffolding, multi-phase L workflow, and full agent suite. Context budget 20K/phase.
---

# XL Workflow: New Module / Architecture

For creating new modules, framework migrations, or plugin-wide refactors. Extends the L workflow with scaffolding and multi-phase delivery.

## Context Budget: 20,000 tokens per phase

## Steps

### 1. RESEARCH (<30 min)
1. Read `PLUGIN-SUMMARY.md`
2. Read all affected module summaries (up to 4)
3. Study existing module patterns (read a similar module's structure)
4. Review BafFramework module docs (`.claude/docs/bafframework/MODULE_SYSTEM.md`)
5. Use MCP `search_code` to find analogous patterns

### 2. ARCHITECT (architect agent)
1. Invoke **architect** agent with:
   - Full task description
   - Existing module patterns for reference
   - BafFramework constraints
2. Design module structure:
   - Public API surface
   - Internal class hierarchy
   - Config schema
   - Command structure
   - Event handling
3. Plan integration with existing modules
4. Document architecture decisions
5. Get user approval

### 3. SCAFFOLD (module-create skill)
1. Run `/module-create` to generate boilerplate:
   - Module class extending `PluginModule`
   - Config class with `@Configuration`
   - Manager class
   - Listener class (if event-driven)
2. Register in plugin's `registerModules()`
3. Verify it builds: `./gradlew build`

### 4. IMPLEMENT (multi-phase, each follows L workflow)

Break into phases (typically 4-5):

| Phase | Focus | Deliverable |
|-------|-------|-------------|
| 1 | Data layer | Data classes, POJOs, enums |
| 2 | Core logic | Managers, services, business rules |
| 3 | Config & commands | Config classes, command handlers |
| 4 | Event handling | Listeners, hooks, integrations |
| 5 | Integration testing | Cross-module tests, edge cases |

Each phase follows the L workflow (Steps 4-5).

### 5. INTEGRATE
- Full build: `./gradlew build`
- All tests: `./gradlew test`
- Coverage verification: `./gradlew jacocoTestReport`
- Run **security-reviewer** agent
- Performance review for hot paths

### 6. DOCUMENT
- Generate module summary (update summaries)
- Update `PLUGIN-SUMMARY.md` with new module
- Update `MODULE-MAP.md` in codemap
- Document patterns in session memory
- Run `/cache-update`

## Agents Used

| Agent | When | Model |
|-------|------|-------|
| architect | Step 2 — module design | opus |
| planner | Step 4 — phase planning | sonnet |
| tdd-guide | Step 4 — per-phase TDD | sonnet |
| code-reviewer | Step 4 — after each phase | sonnet |
| security-reviewer | Step 5 — final security review | sonnet |
| doc-updater | Step 6 — documentation | haiku |

## Escalation

If any of these are true, switch to `/workflow-xxl`:
- Changes affect multiple plugins or repositories
- A shared API needs to be designed across plugin boundaries
- BafFramework itself needs modification
