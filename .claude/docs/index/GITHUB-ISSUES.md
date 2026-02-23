# GitHub Issues — AI Context Engineering Pipeline

All issues are ready to be copied directly into GitHub. Use parent issue #XX (this tracker) to link them.

---

## Parent Issue

### Title
`feat: AI Context Engineering Pipeline — Scalable AI Development Infrastructure`

### Labels
`epic`, `infrastructure`, `multi-phase`

### Body

```markdown
## Summary

Build a plugin-agnostic, scalable AI development infrastructure that enables Claude Code to work
efficiently on large Minecraft plugin codebases without reading the entire project for every task.

## Motivation

- Current: Claude reads 10-30 files per task, ~50K-200K tokens average
- Target: Claude reads 3-8 files per task, ~15K-50K tokens average
- Enable: Cross-project knowledge sharing, auto-generated architecture maps, semantic search

## Phases

### Phase 1: Codebase Understanding & Architecture Mapping
- **Goal:** Auto-generate architecture maps from code analysis
- **Deliverables:** MODULE-MAP.md, CLASS-REGISTRY.md, DEPENDENCY-GRAPH.md, PATTERN-CATALOG.md
- **Status:** ⏳ Pending

### Phase 2: Indexing & Metadata System
- **Goal:** Create universal searchable index of all code entities
- **Deliverables:** JSON index files with class metadata, tags, dependencies
- **Depends on:** Phase 1
- **Status:** ⏳ Pending

### Phase 3: Hierarchical Summaries
- **Goal:** Multi-level summaries (Ecosystem → Plugin → Module → File → Function)
- **Deliverables:** Summary files at each level, context loading rules
- **Depends on:** Phase 1, 2
- **Status:** ⏳ Pending

### Phase 4: Semantic Search (MCP Server)
- **Goal:** Natural language code search via embeddings
- **Deliverables:** MCP server with hybrid BM25+vector search
- **Depends on:** Phase 2
- **Status:** ⏳ Pending

### Phase 5: Context Selection Engine
- **Goal:** Smart context loading based on task analysis
- **Deliverables:** Context loading rules, token budget allocation
- **Depends on:** Phase 3
- **Status:** ⏳ Pending

### Phase 6: Caching & Memory
- **Goal:** Persistent cross-session knowledge, file caching
- **Deliverables:** File hash cache, convention memory, cross-project memory
- **Depends on:** Phase 2, 3
- **Status:** ⏳ Pending

### Phase 7: Agent Tool Interfaces
- **Goal:** Unified tool API for all agents
- **Deliverables:** search_code, find_class, analyze_impact tools
- **Depends on:** Phase 2, 4
- **Status:** ⏳ Pending

### Phase 8: Feature Development Workflows
- **Goal:** Size-appropriate workflows (XS through XXL)
- **Deliverables:** Workflow definitions, workflow selection rules
- **Depends on:** Phase 5, 7
- **Status:** ⏳ Pending

### Phase 9: Performance & Cost Optimization
- **Goal:** 50-70% token reduction
- **Deliverables:** Path-scoped rules, skills migration, cost tracking
- **Depends on:** Phase 5, 6
- **Status:** ⏳ Pending

### Phase 10: Multi-Plugin Support
- **Goal:** Shared knowledge across all plugins
- **Deliverables:** Shared docs, project registry, cross-project search
- **Depends on:** Phase 6, 7
- **Status:** ⏳ Pending

## Success Criteria
- [ ] All 10 phases completed
- [ ] Token usage reduced by 50%+ (measured via /cost)
- [ ] Cross-project search operational
- [ ] Architecture maps auto-generated for any Java project
- [ ] Documentation in `.claude/docs/index/`

## Documentation
`.claude/docs/index/` — Complete system design and phase documentation
```

---

## Phase Issues (Copy-Paste Ready)

### Issue 1: Phase 1 — Codebase Understanding

**Title:** `feat: Phase 1 — Codebase Understanding & Architecture Mapping`
**Labels:** `feature`, `infrastructure`, `phase-1`, `effort:L`
**Priority:** P1

**Body:**
```markdown
## Parent Issue
**Parent:** #XX - AI Context Engineering Pipeline
**Phase:** 1 of 10

## Summary
Create an automated codebase analysis system that generates structured architecture maps
for any Java/Bukkit plugin project.

## Problem
Claude must grep/glob through 550+ files to understand project structure. This wastes tokens
and time on every task. No structural map exists.

## Solution
Build a `codemap-generator` skill that automatically produces:
- MODULE-MAP.md (all modules with purpose and dependencies)
- CLASS-REGISTRY.md (every class with one-line summary)
- DEPENDENCY-GRAPH.md (module→module and class→class)
- PATTERN-CATALOG.md (detected design patterns)
- CONFIG-SCHEMA-MAP.md (YAML → Java class mappings)
- DOMAIN-MAP.md (auto-detected domains)
- ENTRY-POINTS.md (listeners, commands, tasks)

## Tasks
- [ ] Design codemap output format
- [ ] Create codemap-generator skill
- [ ] Implement project structure scanner
- [ ] Implement class/interface extractor
- [ ] Implement extends/implements relationship detector
- [ ] Implement design pattern detector
- [ ] Implement domain auto-tagger
- [ ] Implement config schema mapper
- [ ] Implement entry point scanner
- [ ] Generate codemaps for CustomEnchantment
- [ ] Validate against manual analysis
- [ ] Test on BafFramework (second project)

## Acceptance Criteria
- [ ] MODULE-MAP.md matches actual 16-module structure
- [ ] CLASS-REGISTRY.md contains 95%+ of all public classes
- [ ] Total generated docs < 2000 lines
- [ ] Works on any Gradle Java project

## Technical Notes
- Use Grep patterns for Java: `class\s+(\w+)\s+(extends|implements)`
- Extract module load order from `registerModules()` in main plugin class
- Lombok annotations indicate data classes vs business logic

## Documentation
See: `.claude/docs/index/01-PHASE1-CODEBASE-UNDERSTANDING.md`
```

---

### Issue 2: Phase 2 — Indexing & Metadata

**Title:** `feat: Phase 2 — Universal Indexing & Metadata System`
**Labels:** `feature`, `infrastructure`, `phase-2`, `effort:L`
**Priority:** P1

**Body:**
```markdown
## Parent Issue
**Parent:** #XX - AI Context Engineering Pipeline
**Phase:** 2 of 10 | **Depends on:** Phase 1

## Summary
Build a comprehensive JSON index of every code entity with rich metadata (tags, dependencies,
summaries, usage locations).

## Tasks
- [ ] Define JSON schema for indexed entities
- [ ] Design tag taxonomy (domain, pattern, framework, lifecycle)
- [ ] Implement Java class/method/field extractors
- [ ] Implement YAML config binder
- [ ] Implement dependency graph builder
- [ ] Implement auto-tagger
- [ ] Implement reverse tag index
- [ ] Implement enchantment definition indexer
- [ ] Implement incremental update (git diff based)
- [ ] Generate full index for CustomEnchantment
- [ ] Store at `.claude/docs/codemap/index/`

## Acceptance Criteria
- [ ] All 550+ classes indexed
- [ ] Each class has domain and pattern tags
- [ ] Tag queries return correct results in < 1 second
- [ ] Incremental update < 30 seconds
- [ ] Index JSON < 500KB total

## Documentation
See: `.claude/docs/index/02-PHASE2-INDEXING-METADATA.md`
```

---

### Issue 3: Phase 3 — Hierarchical Summaries

**Title:** `feat: Phase 3 — Hierarchical Context System & Summarization`
**Labels:** `feature`, `infrastructure`, `phase-3`, `effort:L`
**Priority:** P1

**Body:**
```markdown
## Parent Issue
**Parent:** #XX - AI Context Engineering Pipeline
**Phase:** 3 of 10 | **Depends on:** Phase 1, 2

## Summary
Build five-level summaries (Ecosystem → Plugin → Module → File → Function) with rules
for when each level should be loaded.

## Tasks
- [ ] Define five-level hierarchy
- [ ] Create file-level summary template
- [ ] Define summarization rules
- [ ] Implement summary generator skill
- [ ] Generate all level summaries
- [ ] Define context loading rules
- [ ] Store in `.claude/docs/codemap/summaries/`

## Acceptance Criteria
- [ ] All 16 modules have Level 3 summaries
- [ ] Top 100 classes have Level 2 summaries
- [ ] Total summary size < 200KB
- [ ] A developer can understand the enchantment system from summaries alone

## Documentation
See: `.claude/docs/index/03-PHASE3-HIERARCHICAL-SUMMARIES.md`
```

---

### Issue 4: Phase 4 — Semantic Search

**Title:** `feat: Phase 4 — Embedding & Semantic Retrieval via MCP Server`
**Labels:** `feature`, `infrastructure`, `phase-4`, `effort:L`
**Priority:** P2

**Body:**
```markdown
## Parent Issue
**Parent:** #XX - AI Context Engineering Pipeline
**Phase:** 4 of 10 | **Depends on:** Phase 2

## Summary
Set up semantic code search using vector embeddings and an MCP server for natural language queries.

## Tasks
- [ ] Evaluate MCP server options
- [ ] Install and configure MCP server
- [ ] Configure embedding model (Voyage Code 3)
- [ ] Configure vector store (Qdrant or Zilliz)
- [ ] Define chunking strategy
- [ ] Implement embedding pipelines (Java, YAML, docs)
- [ ] Implement hybrid search (BM25 + vector)
- [ ] Index full project
- [ ] Create 10 test queries for validation
- [ ] Implement incremental update

## Acceptance Criteria
- [ ] "damage reduction" finds EffectModifyDamage and related classes
- [ ] Hybrid search outperforms pure keyword search
- [ ] Search latency < 2 seconds
- [ ] Total embedding cost < $0.10

## Documentation
See: `.claude/docs/index/04-PHASE4-SEMANTIC-SEARCH.md`
```

---

### Issue 5: Phase 5 — Context Engine

**Title:** `feat: Phase 5 — Context Selection Engine with Smart Loading Rules`
**Labels:** `feature`, `infrastructure`, `phase-5`, `effort:M`
**Priority:** P2

**Body:**
```markdown
## Parent Issue
**Parent:** #XX - AI Context Engineering Pipeline
**Phase:** 5 of 10 | **Depends on:** Phase 3

## Summary
Build intelligence that analyzes tasks and selects optimal context to load, minimizing tokens.

## Tasks
- [ ] Define task classification rules
- [ ] Define scope detection logic
- [ ] Define token budget allocation
- [ ] Implement relevance scoring
- [ ] Create context loading rule
- [ ] Create context selector skill
- [ ] Test with 10 representative tasks
- [ ] Measure token savings

## Acceptance Criteria
- [ ] 90% correct task classification
- [ ] 50%+ token reduction vs. unguided loading
- [ ] No critical context omissions

## Documentation
See: `.claude/docs/index/05-PHASE5-CONTEXT-ENGINE.md`
```

---

### Issue 6: Phase 6 — Caching & Memory

**Title:** `feat: Phase 6 — Caching & Cross-Session Persistent Memory`
**Labels:** `feature`, `infrastructure`, `phase-6`, `effort:M`
**Priority:** P2

**Body:**
```markdown
## Parent Issue
**Parent:** #XX - AI Context Engineering Pipeline
**Phase:** 6 of 10 | **Depends on:** Phase 2, 3

## Summary
Three-tier memory: file cache, session memory, persistent cross-session conventions.

## Tasks
- [ ] Implement file hash cache
- [ ] Create cache update skill
- [ ] Structure persistent memory directory
- [ ] Create MEMORY.md, conventions.md, patterns.md, bug-patterns.md
- [ ] Create cross-project.md
- [ ] Implement convention learning logic
- [ ] Populate initial memory

## Acceptance Criteria
- [ ] Cache hit rate > 80% for unchanged files
- [ ] MEMORY.md < 200 lines
- [ ] Convention memory contains 10+ verified patterns

## Documentation
See: `.claude/docs/index/06-PHASE6-CACHING-MEMORY.md`
```

---

### Issue 7: Phase 7 — Agent Tools

**Title:** `feat: Phase 7 — Unified Agent Tool Interfaces`
**Labels:** `feature`, `infrastructure`, `phase-7`, `effort:M`
**Priority:** P2

**Body:**
```markdown
## Parent Issue
**Parent:** #XX - AI Context Engineering Pipeline
**Phase:** 7 of 10 | **Depends on:** Phase 2, 4

## Summary
Standard tool API (search_code, find_class, analyze_impact) for all agents.

## Tasks
- [ ] Define full tool catalog
- [ ] Implement tools as skills or MCP server
- [ ] Update all 8 agent prompts
- [ ] Define tool access matrix per agent
- [ ] Test each tool
- [ ] Measure token savings

## Acceptance Criteria
- [ ] All agents use tools instead of raw grep/glob
- [ ] Tool response < 5 seconds
- [ ] 50%+ token reduction per agent task

## Documentation
See: `.claude/docs/index/07-PHASE7-AGENT-TOOLS.md`
```

---

### Issue 8: Phase 8 — Feature Workflows

**Title:** `feat: Phase 8 — Structured Feature Development Workflows`
**Labels:** `feature`, `infrastructure`, `phase-8`, `effort:M`
**Priority:** P3

**Body:**
```markdown
## Parent Issue
**Parent:** #XX - AI Context Engineering Pipeline
**Phase:** 8 of 10 | **Depends on:** Phase 5, 7

## Summary
Size-appropriate workflows (XS through XXL) with retrieval and validation strategies.

## Tasks
- [ ] Define task size classification
- [ ] Document all 6 workflow sizes
- [ ] Define context budget per size
- [ ] Create workflow selection rule
- [ ] Create workflow skills
- [ ] Test each workflow

## Acceptance Criteria
- [ ] All 6 sizes documented
- [ ] XS completes in < 5 min with < 5K tokens
- [ ] L phases stay within 20K tokens each

## Documentation
See: `.claude/docs/index/08-PHASE8-FEATURE-WORKFLOWS.md`
```

---

### Issue 9: Phase 9 — Cost Optimization

**Title:** `feat: Phase 9 — Performance & Cost Optimization`
**Labels:** `feature`, `infrastructure`, `phase-9`, `effort:M`
**Priority:** P2

**Body:**
```markdown
## Parent Issue
**Parent:** #XX - AI Context Engineering Pipeline
**Phase:** 9 of 10 | **Depends on:** Phase 5, 6 (quick wins have no deps)

## Summary
8 optimization strategies targeting 50-70% token reduction.

## Tasks
**Quick Wins (no dependencies)**
- [ ] Move large rules to on-demand skills
- [ ] Add path-scoped rules for modules
- [ ] Configure Haiku for subagents
- [ ] Add cost-awareness rule

**After Phase 3**
- [ ] Summary-first reading protocol

**After Phase 5-6**
- [ ] Incremental + cache-aware context loading

**Measurement**
- [ ] Baseline token measurement
- [ ] Weekly tracking

## Acceptance Criteria
- [ ] Simple task < 20K tokens (from ~50K)
- [ ] No quality degradation
- [ ] Quick wins implemented independently

## Documentation
See: `.claude/docs/index/09-PHASE9-COST-OPTIMIZATION.md`
```

---

### Issue 10: Phase 10 — Multi-Plugin

**Title:** `feat: Phase 10 — Multi-Plugin & Cross-Project Knowledge Layer`
**Labels:** `feature`, `infrastructure`, `phase-10`, `effort:L`
**Priority:** P3

**Body:**
```markdown
## Parent Issue
**Parent:** #XX - AI Context Engineering Pipeline
**Phase:** 10 of 10 | **Depends on:** Phase 6, 7

## Summary
Shared knowledge layer across all plugins: BafFramework docs, Bukkit patterns,
cross-project search, reusable component detection.

## Tasks
- [ ] Create shared knowledge directory
- [ ] Extract BafFramework API into shared docs
- [ ] Extract Bukkit patterns into shared docs
- [ ] Create project registry
- [ ] Implement reusable component detection
- [ ] Create version compatibility map
- [ ] Configure cross-project MCP server
- [ ] Document --add-dir workflow

## Acceptance Criteria
- [ ] BafFramework API available to all plugins
- [ ] Cross-project search finds code across 2+ projects
- [ ] New plugin bootstraps with shared knowledge in < 5 min

## Documentation
See: `.claude/docs/index/10-PHASE10-MULTI-PLUGIN.md`
```
