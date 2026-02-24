# CustomEnchantment Semantic Search MCP Server

Phase 4 of the AI Context Pipeline — semantic code search via MCP.

## Setup

```bash
# Install dependencies
pip install -r .claude/mcp/requirements.txt

# Build the index (first time, ~30s)
cd .claude/mcp && python indexer.py
```

The MCP server is configured in `.mcp.json` at the project root. Claude Code will auto-start it.

## Architecture

```
Query → BM25 (keyword match) ──┐
                                ├── RRF Fusion → Filters → Top-K results
Query → FAISS (vector search) ─┘
```

**Stack:** FastMCP + sentence-transformers (`all-MiniLM-L6-v2`, 384-dim, local) + FAISS + BM25

**Data sources:** 1,485 chunks from Phase 2/3 indexes and summaries:
- 541 classes, 579 enchantments, 162 methods, 17 configs, 16 modules, 24 entry points, ~146 summary sections

## MCP Tools

| Tool | Purpose |
|------|---------|
| `search_code(query, top_k, types, module, tags)` | Search classes, methods, listeners, modules, summaries |
| `search_enchantments(query, top_k, group, applies_to, trigger)` | Search 579 enchantments with domain filters |
| `search_configs(query, top_k)` | Search config classes and YAML keys |
| `get_module_summary(module_name, section)` | Direct lookup of Phase 3 markdown summaries |
| `reindex(force)` | Rebuild index (auto-detects changes) |

## Files

```
.claude/mcp/
├── server.py          # FastMCP entry point, 5 tools
├── indexer.py         # Embedding, FAISS build, BM25, persistence
├── searcher.py        # Hybrid BM25 + vector + RRF fusion
├── chunker.py         # 7 chunk builders (classes, enchants, methods, configs, modules, entry-points, summaries)
├── models.py          # Chunk, SearchResult dataclasses
├── config.py          # Paths and constants
├── requirements.txt   # Python dependencies
├── tests/             # pytest test suite (39 tests)
└── persist/           # gitignored, runtime-generated indexes
```

## Testing

```bash
cd .claude/mcp
python -m pytest tests/ -v
```

## Reindexing

After changing Phase 2/3 data files, rebuild the index:

```bash
cd .claude/mcp && python indexer.py
```

Or use the `reindex` MCP tool (auto-detects file changes).
