"""Configuration for the semantic search MCP server."""

from __future__ import annotations

import os
from pathlib import Path

# Project root: the CustomEnchantment repo root
_this_dir = Path(__file__).resolve().parent  # .claude/mcp/
PROJECT_ROOT = _this_dir.parent.parent  # repo root

# Phase 2/3 data paths
INDEX_DIR = PROJECT_ROOT / ".claude" / "docs" / "codemap" / "index"
SUMMARIES_DIR = PROJECT_ROOT / ".claude" / "docs" / "codemap" / "summaries"

# Persistence directory (gitignored)
PERSIST_DIR = _this_dir / "persist"
FAISS_INDEX_PATH = PERSIST_DIR / "faiss.index"
CHUNKS_PATH = PERSIST_DIR / "chunks.pkl"
BM25_PATH = PERSIST_DIR / "bm25.pkl"
MANIFEST_PATH = PERSIST_DIR / "manifest.json"

# Embedding model
EMBED_MODEL = os.environ.get("CE_EMBED_MODEL", "all-MiniLM-L6-v2")
EMBED_DIM = 384

# Search defaults
DEFAULT_TOP_K = 10
MAX_TOP_K = 50
RRF_K = 60  # reciprocal rank fusion constant
BM25_CANDIDATES = 3000
VECTOR_CANDIDATES = 3000
