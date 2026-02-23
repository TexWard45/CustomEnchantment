# Phase 8 — Feature Development Workflows

## Purpose

Define structured, repeatable workflows for different sizes of development tasks. Each workflow specifies the retrieval strategy, context selection, validation steps, and testing approach appropriate for the task complexity.

## Design

### Workflow Size Classification

| Size | Criteria | Examples | Estimated Time |
|------|----------|---------|----------------|
| **XS** | 1 file, < 20 lines changed | Typo fix, add a log line, rename variable | < 30 min |
| **S** | 1-2 files, follows existing pattern | New condition, new config field, simple bugfix | 1-2 hours |
| **M** | 3-5 files, within one module | New feature within module, multi-class refactor | 2-8 hours |
| **L** | 5-15 files, crosses modules | New system, major refactor, cross-cutting feature | 1-3 days |
| **XL** | 15+ files, architectural change | New module, framework migration, plugin-wide refactor | 1+ weeks |
| **XXL** | Multi-plugin feature | Cross-plugin integration, shared API | Multi-phase |

### XS Workflow: Quick Fix

```
1. RETRIEVE (< 1 min)
   └── Read target file directly (user specified or obvious)

2. MODIFY (< 5 min)
   └── Make the change

3. VALIDATE (< 5 min)
   ├── Run affected tests: ./gradlew :{module}:test
   └── Verify no compilation errors

No planning needed. No context engine. No code review.
```

### S Workflow: Pattern-Following

```
1. RETRIEVE (< 5 min)
   ├── get_context_plan("add new condition XYZ")
   ├── Read module summary for target module
   └── Read one existing example file (pattern template)

2. PLAN (< 5 min)
   ├── Identify pattern to follow
   └── List files to create/modify

3. TEST FIRST (TDD)
   ├── Write test based on existing pattern test
   └── Verify test fails (RED)

4. IMPLEMENT (< 30 min)
   ├── Follow pattern exactly
   └── Register in module's onEnable()

5. VALIDATE
   ├── Run test → should pass (GREEN)
   ├── Code review (auto: code-reviewer agent)
   └── Convention check

Total context: ~8,000 tokens (1 summary + 1 full file + 1 test)
```

### M Workflow: Module Feature

```
1. RETRIEVE (< 10 min)
   ├── get_context_plan("implement feature X in module Y")
   ├── Read module summary
   ├── Read file summaries for all relevant classes
   ├── Identify cross-module dependencies
   └── Read full files for 2-3 key classes

2. PLAN (invoke planner agent)
   ├── Break feature into implementation steps
   ├── Identify risks and edge cases
   ├── Define success criteria
   └── Get user approval

3. IMPLEMENT (TDD per step)
   For each implementation step:
   ├── Write test (RED)
   ├── Implement (GREEN)
   ├── Refactor (IMPROVE)
   └── Code review (code-reviewer agent)

4. INTEGRATION
   ├── Run full module tests: ./gradlew :{module}:test
   ├── Run dependent module tests
   └── Verify 80%+ coverage

5. REVIEW
   ├── Code review (code-reviewer agent)
   ├── Security review (if user input involved)
   └── Convention check

Total context: ~15,000 tokens (module summary + file summaries + 3 full files)
```

### L Workflow: Cross-Module Feature

```
1. DISCOVER (< 15 min)
   ├── get_plugin_summary()
   ├── search_code("relevant query")
   ├── Read module summaries for affected modules
   ├── analyze_impact(primary_class)
   └── Identify all touch points

2. ARCHITECT (invoke architect agent)
   ├── Design component interactions
   ├── Define module boundaries
   ├── Document API contracts
   └── Get user approval

3. PLAN (invoke planner agent)
   ├── Phase the implementation
   ├── Define interfaces first
   ├── Order: data classes → core logic → integration → tests
   └── Create task checklist

4. IMPLEMENT (phased TDD)
   Phase 1: Core data classes and interfaces
   Phase 2: Business logic implementation
   Phase 3: Integration (listeners, commands, configs)
   Phase 4: Tests and validation

   Each phase:
   ├── Load only that phase's context
   ├── TDD cycle
   ├── Phase review (code-reviewer agent)
   └── Update context for next phase

5. VALIDATE
   ├── Full test suite: ./gradlew test
   ├── Coverage: ./gradlew jacocoTestReport
   ├── Security review (security-reviewer agent)
   ├── Performance check (for hot path code)
   └── Impact analysis (verify no regressions)

6. DOCUMENT
   ├── Update module summaries (if structure changed)
   ├── Document new patterns in memory
   ├── Create issue documentation

Total context: ~20,000 tokens per phase (summaries + targeted full files)
```

### XL Workflow: New Module / Architecture

```
1. RESEARCH (< 30 min)
   ├── get_plugin_summary()
   ├── Read all module summaries
   ├── Study existing module patterns
   ├── Review BafFramework module docs
   └── Analyze similar modules

2. ARCHITECT
   ├── Design module structure
   ├── Define public API surface
   ├── Plan integration with existing modules
   ├── Document architecture decisions (ADR)
   └── Get user approval

3. SCAFFOLD (invoke module-create skill)
   ├── Create module directory structure
   ├── Create Module, Config, Manager, Listener stubs
   ├── Register in plugin
   └── Verify builds

4. IMPLEMENT (multi-phase, see L workflow per phase)
   ├── Phase 1: Data layer
   ├── Phase 2: Core logic
   ├── Phase 3: Config and commands
   ├── Phase 4: Event handling
   ├── Phase 5: Integration testing
   └── Each phase follows L workflow

5. INTEGRATE
   ├── Full build: ./gradlew build
   ├── All tests: ./gradlew test
   ├── Coverage verification
   ├── Security review
   └── Performance review

6. DOCUMENT
   ├── Generate module summary
   ├── Update plugin summary
   ├── Update MODULE-MAP.md
   ├── Create module documentation
   └── Document lessons learned
```

### XXL Workflow: Cross-Plugin Feature

```
1. ECOSYSTEM ANALYSIS
   ├── get_ecosystem_summary()  (Level 5)
   ├── get_plugin_summary() for each affected plugin
   ├── Identify shared interfaces/APIs
   └── Map cross-plugin dependencies

2. API DESIGN
   ├── Design shared API (in BafFramework or shared library)
   ├── Define contracts between plugins
   ├── Version compatibility plan
   └── Get approval from all stakeholders

3. IMPLEMENT PER PLUGIN
   ├── Start with API/framework changes
   ├── Then implement in each plugin (L workflow each)
   ├── Integration tests across plugins
   └── Use --add-dir for cross-project context

4. VALIDATE CROSS-PLUGIN
   ├── Build all affected plugins
   ├── Test plugin interaction
   └── Performance test under load
```

## Context Budget by Workflow Size

| Workflow | Context Tokens | Files Read | Agents Used |
|----------|---------------|------------|-------------|
| XS | 3,000 | 1 | 0 |
| S | 8,000 | 2-3 | 1 (code-reviewer) |
| M | 15,000 | 5-8 | 2-3 (planner + tdd + reviewer) |
| L | 20,000/phase | 10-15 total | 4+ (all agents) |
| XL | 20,000/phase | 15-30 total | All agents + module-create |
| XXL | 25,000/phase | 30+ across projects | All agents + cross-project tools |

## Implementation

### Workflow Selection Rule

Add `.claude/rules/workflow-selection.md`:

```markdown
# Workflow Selection

Before starting implementation, classify the task size:

| If... | Then use... |
|-------|------------|
| Single file, < 20 lines | XS: just make the change |
| 1-2 files, existing pattern | S: follow the pattern |
| 3-5 files, one module | M: plan first, TDD |
| 5-15 files, multiple modules | L: architect + phased TDD |
| New module or major refactor | XL: scaffold + multi-phase |
| Multiple plugins affected | XXL: ecosystem analysis first |

Always start with the smallest workflow that fits.
Escalate if you discover the task is larger than expected.
```

### Workflow Skills

```
.claude/skills/
├── workflow-xs/SKILL.md     # Quick fix workflow
├── workflow-s/SKILL.md      # Pattern-following workflow
├── workflow-m/SKILL.md      # Module feature workflow
├── workflow-l/SKILL.md      # Cross-module workflow
├── workflow-xl/SKILL.md     # Architecture workflow
└── workflow-xxl/SKILL.md    # Cross-plugin workflow
```

## Dependencies

- **Phase 5** — Context engine for smart context loading
- **Phase 7** — Agent tools for search_code, analyze_impact, etc.

## Success Criteria

- [ ] Workflows defined for all 6 size categories
- [ ] Context budgets respected (no workflow exceeds its token allocation)
- [ ] Task size correctly classified 90% of the time
- [ ] Each workflow produces consistent, high-quality output
- [ ] Workflows are plugin-agnostic (work for any Java project)

---

## GitHub Issue

### Title
`feat: Phase 8 — Structured Feature Development Workflows`

### Description

Define structured, size-appropriate workflows for development tasks ranging from quick fixes (XS) to cross-plugin features (XXL). Each workflow specifies retrieval strategy, context budget, validation steps, and agent coordination.

### Background / Motivation

Not every task needs the same process. A typo fix shouldn't invoke the planner and architect agents. A new module shouldn't skip the architecture review. Codified workflows ensure consistent quality while matching effort to task complexity.

### Tasks

- [ ] Define task size classification criteria (XS through XXL)
- [ ] Document XS workflow (quick fix, minimal overhead)
- [ ] Document S workflow (pattern-following with TDD)
- [ ] Document M workflow (module feature with planning)
- [ ] Document L workflow (cross-module with architecture)
- [ ] Document XL workflow (new module/major refactor)
- [ ] Document XXL workflow (cross-plugin coordination)
- [ ] Define context budget per workflow size
- [ ] Create workflow selection rule at `.claude/rules/workflow-selection.md`
- [ ] Create workflow skills (one per size)
- [ ] Test each workflow with a representative task
- [ ] Measure: do workflows reduce over-engineering for small tasks?
- [ ] Measure: do workflows prevent under-planning for large tasks?

### Acceptance Criteria

- [ ] All 6 workflow sizes documented with retrieval, validation, and testing steps
- [ ] Workflow selection rule correctly classifies 90% of tasks
- [ ] XS workflow completes in < 5 minutes with < 5,000 tokens
- [ ] L workflow phases stay within 20,000 tokens each
- [ ] Workflows are plugin-agnostic

---
**Priority:** P3 | **Effort:** M | **Labels:** `feature`, `infrastructure`, `phase-8`, `effort:M`
