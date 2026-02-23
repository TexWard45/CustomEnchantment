# Implementation Roadmap

## Dependency Graph

```
Phase 1 ──┬──► Phase 2 ──┬──► Phase 4 ──┬──► Phase 7 ──┬──► Phase 8
           │              │              │              │
           │              ├──► Phase 3 ──┼──► Phase 5 ──┤
           │              │              │              │
           │              └──► Phase 6 ──┘     │        └──► Phase 10
           │                                   │
           │                   Phase 9 (quick wins: no deps)
           │                   Phase 9 (full: depends on 5, 6)
           │
           └──────────────────────────────────────────────
```

## Recommended Implementation Order

### Wave 1: Foundation (Weeks 1-3)
**Goal:** Generate the knowledge infrastructure

| Order | Phase | Effort | Dependencies |
|-------|-------|--------|--------------|
| 1 | Phase 1 — Architecture Mapping | L (1 week) | None |
| 2 | Phase 2 — Indexing & Metadata | L (1 week) | Phase 1 |
| 3 | Phase 9 Quick Wins — Cost Optimization | S (1 day) | None |

**Deliverables:**
- Architecture maps for CustomEnchantment
- Searchable JSON index
- Path-scoped rules, large docs moved to skills

**Milestone:** Claude can answer "what module handles X?" from the index without grepping.

### Wave 2: Intelligence (Weeks 3-5)
**Goal:** Make the infrastructure usable

| Order | Phase | Effort | Dependencies |
|-------|-------|--------|--------------|
| 4 | Phase 3 — Hierarchical Summaries | L (1 week) | Phase 1, 2 |
| 5 | Phase 5 — Context Selection Engine | M (3 days) | Phase 3 |
| 6 | Phase 6 — Caching & Memory | M (3 days) | Phase 2, 3 |

**Deliverables:**
- Five-level summary hierarchy
- Smart context loading rules
- File cache and persistent memory

**Milestone:** Token usage drops 50%+ for typical development tasks.

### Wave 3: Search & Tools (Weeks 5-7)
**Goal:** Enable natural language code navigation

| Order | Phase | Effort | Dependencies |
|-------|-------|--------|--------------|
| 7 | Phase 4 — Semantic Search (MCP) | L (1 week) | Phase 2 |
| 8 | Phase 7 — Agent Tool Interfaces | M (3 days) | Phase 2, 4 |

**Deliverables:**
- MCP-based semantic search
- Unified tool API for all agents

**Milestone:** Agents use structured tools instead of raw grep/glob.

### Wave 4: Workflows & Scale (Weeks 7-10)
**Goal:** Codify best practices and extend to multiple projects

| Order | Phase | Effort | Dependencies |
|-------|-------|--------|--------------|
| 9 | Phase 8 — Feature Workflows | M (3 days) | Phase 5, 7 |
| 10 | Phase 9 Full — Cost Optimization | M (3 days) | Phase 5, 6 |
| 11 | Phase 10 — Multi-Plugin Support | L (1 week) | Phase 6, 7 |

**Deliverables:**
- Size-appropriate development workflows
- Full cost optimization suite
- Cross-plugin knowledge sharing

**Milestone:** System works across all plugins in the ecosystem.

## Effort Summary

| Phase | Effort | Priority | Wave |
|-------|--------|----------|------|
| Phase 1 | Large (1 week) | P1 | 1 |
| Phase 2 | Large (1 week) | P1 | 1 |
| Phase 3 | Large (1 week) | P1 | 2 |
| Phase 4 | Large (1 week) | P2 | 3 |
| Phase 5 | Medium (3 days) | P2 | 2 |
| Phase 6 | Medium (3 days) | P2 | 2 |
| Phase 7 | Medium (3 days) | P2 | 3 |
| Phase 8 | Medium (3 days) | P3 | 4 |
| Phase 9 | Medium (split) | P2 | 1 + 4 |
| Phase 10 | Large (1 week) | P3 | 4 |
| **Total** | **~8-10 weeks** | | |

## Quick Start: What to Implement First

If you want immediate value with minimal effort:

1. **Phase 9 Quick Wins** (1 day) — Move large rules to skills, add path-scoped rules
2. **Phase 1** (1 week) — Generate architecture maps
3. **Phase 3 partial** (3 days) — Generate module-level summaries only

These three items alone will reduce token usage by ~40% and give Claude a much better
understanding of the codebase.

## Measuring Progress

Track these metrics weekly:

| Metric | Baseline | After Wave 1 | After Wave 2 | After Wave 4 |
|--------|----------|-------------|-------------|-------------|
| Tokens per simple task | ~50K | ~35K | ~15K | ~12K |
| Tokens per medium task | ~150K | ~100K | ~50K | ~40K |
| Files read per task | 10-30 | 8-15 | 3-8 | 3-5 |
| Time to find code | 30-60s | 15-30s | 5-10s | 3-5s |
| Cross-project context | Manual | Manual | Partial | Full |
