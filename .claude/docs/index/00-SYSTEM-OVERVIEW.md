# AI Context Engineering Pipeline — System Overview

## Vision

A **plugin-agnostic, scalable AI development infrastructure** that enables Claude Code to work efficiently on large Minecraft plugin codebases (and any Java project) without reading the entire project for every task.

## Current State Assessment

### What Already Exists (CustomEnchantment)
- 18 `.claude/rules/` files covering conventions, testing, security, performance
- 8 specialized agents (planner, architect, tdd-guide, code-reviewer, etc.)
- 6 skills (tdd-workflow, verification-loop, iterative-retrieval, etc.)
- 20 slash commands for workflow orchestration
- 100+ documentation files in `.claude/docs/`

### What's Missing
1. **No structural index** — Claude must grep/glob to find anything
2. **No semantic search** — Can't find "code that handles damage reduction" without exact keywords
3. **No hierarchical summaries** — Must read full files to understand what they do
4. **No cross-project knowledge** — BafFramework context manually duplicated per project
5. **No incremental updates** — Documentation drifts from code over time
6. **No cost optimization** — Large rules/docs loaded on every message even when irrelevant

## Architecture

```
                    ┌──────────────────────────────────┐
                    │         USER / CLAUDE CODE        │
                    └──────────────┬───────────────────┘
                                   │
                    ┌──────────────▼───────────────────┐
                    │     CONTEXT SELECTION ENGINE      │
                    │  (decides WHAT context to load)   │
                    │                                   │
                    │  Task Analysis → Relevance Score  │
                    │  → Load only what's needed        │
                    └──────────────┬───────────────────┘
                                   │
              ┌────────────────────┼────────────────────┐
              │                    │                     │
   ┌──────────▼────────┐ ┌────────▼─────────┐ ┌────────▼─────────┐
   │  STRUCTURAL INDEX  │ │  SEMANTIC SEARCH  │ │  HIERARCHICAL    │
   │                    │ │                    │ │  SUMMARIES       │
   │  - Module map      │ │  - Code embeddings│ │                  │
   │  - Class registry  │ │  - Vector store   │ │  - Ecosystem     │
   │  - Dependencies    │ │  - Hybrid search  │ │  - Plugin        │
   │  - Config schemas  │ │  - MCP server     │ │  - Module        │
   │  - API surfaces    │ │                    │ │  - File          │
   └──────────┬────────┘ └────────┬─────────┘ │  - Function      │
              │                    │            └────────┬─────────┘
              └────────────────────┼────────────────────┘
                                   │
                    ┌──────────────▼───────────────────┐
                    │      SHARED KNOWLEDGE LAYER      │
                    │                                   │
                    │  - BafFramework API reference     │
                    │  - Bukkit/Paper patterns          │
                    │  - Cross-project conventions      │
                    │  - Reusable component registry    │
                    └──────────────┬───────────────────┘
                                   │
                    ┌──────────────▼───────────────────┐
                    │    CACHING & PERSISTENCE LAYER   │
                    │                                   │
                    │  - File hash cache (avoid reindex)│
                    │  - Session memory                 │
                    │  - Convention memory              │
                    │  - Pattern memory                 │
                    └──────────────────────────────────┘
```

## Implementation Phases

| Phase | Name | Purpose | Depends On |
|-------|------|---------|------------|
| 1 | Codebase Understanding | Generate architecture maps from code analysis | — |
| 2 | Indexing & Metadata | Create searchable structural index | Phase 1 |
| 3 | Hierarchical Summaries | Multi-level code summaries | Phase 1, 2 |
| 4 | MCP Semantic Search | Vector embeddings + hybrid search | Phase 2 |
| 5 | Context Selection Engine | Smart context loading rules | Phase 3 |
| 6 | Caching & Memory | Persistent cross-session knowledge | Phase 2, 3 |
| 7 | Agent Tool Interfaces | Unified tool API for all agents | Phase 2, 4 |
| 8 | Feature Development Workflows | Structured workflows by task size | Phase 5, 7 |
| 9 | Performance & Cost Optimization | Token reduction, smart caching | Phase 5, 6 |
| 10 | Multi-Plugin Support | Shared knowledge across projects | Phase 6, 7 |

## Design Principles

1. **Claude Code Native** — Build on CLAUDE.md, rules, skills, MCP; not a separate tool
2. **Incremental** — Each phase delivers standalone value
3. **Plugin-Agnostic** — Works for enchantments, economy, combat, any Java project
4. **Token-Efficient** — Load minimum context for maximum relevance
5. **Git-Integrated** — Indexes update from git diffs, not full rescans
6. **Human-in-the-Loop** — User approves context strategy for complex tasks

## Key Metrics

| Metric | Current (Estimated) | Target |
|--------|---------------------|--------|
| Tokens per task (avg) | 50K-200K | 15K-50K |
| Files read per task | 10-30 | 3-8 |
| Time to find relevant code | 30-60s (multi-grep) | 5-10s (indexed search) |
| Cross-project context available | Manual copy | Automatic shared layer |
| Stale documentation | ~30% drift | <5% drift (auto-updated) |

## Technology Choices

| Component | Choice | Rationale |
|-----------|--------|-----------|
| Indexing | Tree-sitter + Java grammar | AST-level precision, incremental parsing |
| MCP Server | coderlm or code-index-mcp | Symbol navigation without loading files |
| Embeddings | Voyage Code 3 | Best code retrieval accuracy |
| Vector Store | Qdrant (self-hosted) or Zilliz Cloud (managed) | Hybrid BM25+dense search |
| Summaries | Claude Haiku 4.5 | Cost-efficient summarization |
| Caching | File hash + JSON metadata | Simple, git-friendly |
| Memory | `.claude/` directory structure | Native Claude Code integration |
