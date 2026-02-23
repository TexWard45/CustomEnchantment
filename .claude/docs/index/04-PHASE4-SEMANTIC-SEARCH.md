# Phase 4 — Embedding & Semantic Retrieval (MCP Server)

## Purpose

Enable natural language search across the codebase. Instead of knowing exact class names or grep patterns, Claude (or the developer) can ask "code that reduces incoming damage" and find `EffectModifyDamage`, `PlayerVanillaAttribute`, `ConditionHealth`, and related YAML configs.

## Design

### Why Semantic Search Matters

| Query | Keyword Search (grep) | Semantic Search |
|-------|----------------------|-----------------|
| "damage reduction" | Finds files containing "damage reduction" literally | Finds `EffectModifyDamage`, `ConditionHealth`, defense configs |
| "player joins server" | Finds "PlayerJoinEvent" | Finds `PlayerListener.onJoin`, `CEPlayerExpansionRegister.createCEPlayer`, `PlayerStorage.onJoin` |
| "how to add a new enchantment" | Misses (no file contains this phrase) | Finds `CEEnchantConfig`, enchantment YAML templates, `ConditionHook` base class |
| "thread safety in combat" | Misses | Finds `EffectExecuteTask`, `ConcurrentHashMap` usages, `CECaller` sync notes |

### Architecture

```
┌─────────────────┐     ┌──────────────────────┐
│   Claude Code   │────▶│    MCP Server          │
│   (client)      │◀────│    (code-search)       │
└─────────────────┘     │                        │
                        │  Tools:                │
                        │  - search_code(query)  │
                        │  - search_similar(file) │
                        │  - index_project(path) │
                        │  - update_index()      │
                        │                        │
                        │  ┌──────────────────┐  │
                        │  │ Embedding Model   │  │
                        │  │ (Voyage Code 3)   │  │
                        │  └────────┬─────────┘  │
                        │           │             │
                        │  ┌────────▼─────────┐  │
                        │  │  Vector Store     │  │
                        │  │  (Qdrant/FAISS)   │  │
                        │  │  + BM25 Index     │  │
                        │  └──────────────────┘  │
                        └──────────────────────────┘
```

### Hybrid Search Strategy

Combine keyword (BM25) and semantic (vector) search for best results:

```pseudocode
function hybridSearch(query, topK=10):
  # Keyword search (exact identifier matches)
  bm25Results = bm25Index.search(query, topK=20)

  # Semantic search (meaning-based)
  queryEmbedding = embed(query)
  vectorResults = vectorStore.search(queryEmbedding, topK=20)

  # Reciprocal Rank Fusion (RRF)
  combined = {}
  for rank, result in enumerate(bm25Results):
    combined[result.id] += 1 / (60 + rank)  # RRF constant = 60
  for rank, result in enumerate(vectorResults):
    combined[result.id] += 1 / (60 + rank)

  # Sort by combined score, return top K
  return sorted(combined, by=score, limit=topK)
```

### What Gets Embedded

| Content Type | Chunking Strategy | Embedding Content |
|-------------|-------------------|-------------------|
| Java classes | One chunk per class | Class summary + method signatures |
| Java methods | One chunk per public method | Method signature + docstring + body (first 50 lines) |
| YAML configs | One chunk per top-level key | Config section with comments |
| Summaries (Phase 3) | One chunk per summary | Full summary text |
| Documentation | One chunk per section (## heading) | Section content |
| Issue docs | One chunk per issue | Problem + solution + lessons |

### Chunking Example

```java
// Original: ConditionHealth.java (45 lines)
// Becomes 2 chunks:

// Chunk 1: Class-level
"ConditionHealth extends ConditionHook. Checks entity health against threshold.
 Identifier: HEALTH. Domain: enchantment, conditions, combat.
 Module: EnchantModule. Methods: getIdentifier(), check(ConditionData, String)"

// Chunk 2: check() method
"check(ConditionData data, String value): Parses operator and threshold from
 value string (format: 'HEALTH:>50%'). Gets target entity from data.
 Compares entity.getHealth() against threshold. Supports >, <, =, >=, <=.
 Returns boolean. Handles percentage mode (value/maxHealth)."
```

## Technical Implementation

### Option A: Use Existing MCP Server (Recommended Start)

Install `claude-context` or `code-index-mcp`:

```bash
# Option 1: claude-context (hybrid search, requires API key)
claude mcp add claude-context \
  -e VOYAGE_API_KEY=your-key \
  -e ZILLIZ_CLOUD_URI=your-uri \
  -e ZILLIZ_CLOUD_TOKEN=your-token \
  -- npx @zilliz/claude-context-mcp@latest

# Option 2: code-index-mcp (local-first, no external services)
claude mcp add code-index -- npx -y code-index-mcp

# Option 3: coderlm (symbol navigation, tree-sitter based)
claude mcp add coderlm -- npx -y coderlm
```

### Option B: Custom MCP Server (For advanced needs)

Build a custom MCP server tailored to Bukkit plugins:

```python
# Pseudocode for custom MCP server

class BukkitCodeSearchServer(MCPServer):

    @tool("search_code")
    def search_code(self, query: str, scope: str = "all") -> list:
        """Search code by natural language query.

        Args:
            query: Natural language description of what to find
            scope: "all", "enchantments", "conditions", "effects",
                   "configs", "listeners", "commands"
        """
        results = self.hybrid_search(query, scope_filter=scope)
        return [
            {
                "file": r.file_path,
                "class": r.class_name,
                "summary": r.summary,
                "relevance": r.score,
                "tags": r.tags
            }
            for r in results[:10]
        ]

    @tool("find_related")
    def find_related(self, class_name: str) -> list:
        """Find classes related to the given class.

        Returns classes that:
        - Import or are imported by the target
        - Share the same domain tags
        - Are referenced in the same configs
        """
        target = self.index.get_class(class_name)
        dependents = self.dependency_graph.neighbors(target)
        similar = self.vector_store.find_similar(target.embedding, topK=5)
        return merge(dependents, similar)

    @tool("search_enchantments")
    def search_enchantments(self, query: str) -> list:
        """Search enchantment definitions by description.

        Example: "enchantments that heal the player" → Life Steal, Regeneration, ...
        """
        return self.hybrid_search(query, scope_filter="enchantments")

    @tool("index_project")
    def index_project(self, path: str) -> dict:
        """Index or re-index a project directory."""
        files = glob(path + "/**/*.java") + glob(path + "/**/*.yml")
        chunks = self.chunk_files(files)
        embeddings = self.embed_batch(chunks)
        self.vector_store.upsert(embeddings)
        self.bm25_index.rebuild(chunks)
        return {"indexed": len(files), "chunks": len(chunks)}
```

### Embedding Pipeline

```pseudocode
function embedProject(projectRoot):
  chunks = []

  # 1. Embed Java class summaries (from Phase 3)
  for summary in loadSummaries(projectRoot):
    chunks.append({
      "id": summary.classId,
      "text": summary.content,
      "metadata": {
        "type": "class-summary",
        "module": summary.module,
        "tags": summary.tags,
        "file": summary.filePath
      }
    })

  # 2. Embed method signatures with context
  for method in loadIndex(projectRoot).methods:
    chunks.append({
      "id": method.id,
      "text": f"{method.className}.{method.signature}: {method.summary}",
      "metadata": {
        "type": "method",
        "class": method.className,
        "module": method.module
      }
    })

  # 3. Embed config sections
  for config in loadIndex(projectRoot).configs:
    for section in config.sections:
      chunks.append({
        "id": f"{config.file}:{section.path}",
        "text": section.content,
        "metadata": {
          "type": "config",
          "yamlFile": config.file,
          "javaClass": config.javaClass
        }
      })

  # 4. Embed documentation
  for doc in glob(projectRoot + "/.claude/docs/**/*.md"):
    for section in splitBySections(doc):
      chunks.append({
        "id": f"{doc.path}:{section.heading}",
        "text": section.content,
        "metadata": {"type": "documentation"}
      })

  # 5. Batch embed and store
  embeddings = voyageCode3.embed_batch([c.text for c in chunks])
  vectorStore.upsert(zip(chunks, embeddings))
  bm25Index.build([c.text for c in chunks])
```

### Cost Estimation

| Component | Chunks | Tokens | Cost (Voyage Code 3) |
|-----------|--------|--------|---------------------|
| 550 class summaries | 550 | ~80K | ~$0.01 |
| 2000 method chunks | 2000 | ~300K | ~$0.04 |
| 100 config sections | 100 | ~50K | ~$0.01 |
| 100 doc sections | 100 | ~100K | ~$0.01 |
| **Total initial** | **2750** | **~530K** | **~$0.07** |
| **Incremental update** (10 files) | ~20 | ~3K | ~$0.001 |

Negligible cost — the entire project can be embedded for less than $0.10.

## Dependencies

- **Phase 2** — Index provides metadata for embedding chunks
- **Phase 3** — Summaries provide the primary text to embed

## Success Criteria

- [ ] MCP server installed and accessible from Claude Code
- [ ] `search_code("damage reduction")` returns relevant classes
- [ ] `search_code("player joins")` finds join-related code across modules
- [ ] Hybrid search outperforms pure keyword search on 10 test queries
- [ ] Index updates in < 60 seconds for typical commits
- [ ] Search latency < 2 seconds per query
- [ ] Works across multiple projects (not hardcoded to CustomEnchantment)

---

## GitHub Issue

### Title
`feat: Phase 4 — Embedding & Semantic Retrieval via MCP Server`

### Description

Set up a semantic code search system using vector embeddings and an MCP server. This enables natural language queries like "code that handles damage reduction" to find relevant classes, methods, and configs without knowing exact identifiers.

### Background / Motivation

Keyword search (grep) fails when the developer doesn't know the exact class name or when the concept spans multiple files. Semantic search bridges this gap by understanding meaning. Combined with keyword search (hybrid approach), this provides the most robust code retrieval possible.

An MCP server integrates natively with Claude Code, providing search tools that all agents can use without custom configuration.

### Tasks

- [ ] Evaluate MCP server options (claude-context, code-index-mcp, coderlm, custom)
- [ ] Select and install primary MCP server
- [ ] Configure embedding model (Voyage Code 3 or alternative)
- [ ] Configure vector store (Qdrant, Zilliz Cloud, or FAISS)
- [ ] Define chunking strategy (class summaries, methods, configs, docs)
- [ ] Implement embedding pipeline for Java classes
- [ ] Implement embedding pipeline for YAML configs
- [ ] Implement embedding pipeline for documentation
- [ ] Implement hybrid search (BM25 + vector with RRF fusion)
- [ ] Index full CustomEnchantment project
- [ ] Create 10 test queries with expected results for validation
- [ ] Implement incremental update (re-embed only changed chunks)
- [ ] Configure MCP server in `.mcp.json` for team sharing
- [ ] Document setup and usage in `.claude/docs/index/04-PHASE4-SEMANTIC-SEARCH.md`

### Technical Notes

- Voyage Code 3 provides best code retrieval accuracy (13.8% better than OpenAI)
- Hybrid search (BM25 + dense vectors) catches both exact identifiers and semantic matches
- Reciprocal Rank Fusion (RRF) is a simple, effective way to merge ranked results
- MCP configuration can be project-shared (`.mcp.json`) or personal (local scope)
- Total embedding cost for the full project is < $0.10

### Acceptance Criteria

- [ ] `search_code("damage reduction")` returns EffectModifyDamage and related classes
- [ ] `search_code("player joins server")` returns PlayerListener, CEPlayerExpansionRegister
- [ ] Hybrid search returns better results than pure keyword or pure vector search
- [ ] Search latency < 2 seconds
- [ ] Incremental update for 10 changed files < 30 seconds
- [ ] MCP server works across multiple projects

### Future Improvements

- Cross-project search (search BafFramework + CustomEnchantment simultaneously)
- Search by code example ("find code similar to this snippet")
- Weighted search scopes (prioritize enchant module for enchant-related queries)
- Search history and relevance feedback

---
**Priority:** P2 | **Effort:** L | **Labels:** `feature`, `infrastructure`, `phase-4`, `effort:L`
