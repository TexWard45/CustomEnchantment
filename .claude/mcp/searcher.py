"""Hybrid search: BM25 + vector retrieval with Reciprocal Rank Fusion."""

from __future__ import annotations

from typing import Optional

import faiss
import numpy as np
from rank_bm25 import BM25Okapi
from sentence_transformers import SentenceTransformer

from config import BM25_CANDIDATES, DEFAULT_TOP_K, MAX_TOP_K, RRF_K, VECTOR_CANDIDATES
from models import Chunk, SearchResult


def _tokenize(text: str) -> list[str]:
    """Simple whitespace + lowercase tokenizer for BM25."""
    return text.lower().split()


def bm25_search(
    query: str,
    chunks: list[Chunk],
    bm25: BM25Okapi,
    top_n: int = BM25_CANDIDATES,
) -> list[tuple[int, float]]:
    """Return (index, score) pairs from BM25 ranking."""
    tokens = _tokenize(query)
    scores = bm25.get_scores(tokens)
    top_indices = np.argsort(scores)[::-1][:top_n]
    return [(int(i), float(scores[i])) for i in top_indices if scores[i] > 0]


def vector_search(
    query: str,
    index: faiss.Index,
    model: SentenceTransformer,
    top_n: int = VECTOR_CANDIDATES,
) -> list[tuple[int, float]]:
    """Return (index, score) pairs from FAISS vector search."""
    embedding = model.encode([query], normalize_embeddings=True)
    embedding = np.array(embedding, dtype=np.float32)
    scores, indices = index.search(embedding, min(top_n, index.ntotal))
    return [(int(indices[0][i]), float(scores[0][i])) for i in range(len(indices[0])) if indices[0][i] >= 0]


def rrf_fusion(
    *ranked_lists: list[tuple[int, float]],
    k: int = RRF_K,
) -> dict[int, float]:
    """Reciprocal Rank Fusion: merge multiple ranked lists.

    RRF score = sum(1 / (k + rank_i)) for each list where the item appears.
    """
    fused: dict[int, float] = {}
    for ranked in ranked_lists:
        for rank, (idx, _score) in enumerate(ranked):
            fused[idx] = fused.get(idx, 0.0) + 1.0 / (k + rank + 1)
    return fused


def apply_filters(
    chunks: list[Chunk],
    candidates: dict[int, float],
    source_types: Optional[list[str]] = None,
    module: Optional[str] = None,
    tags: Optional[list[str]] = None,
    # Enchantment-specific filters
    group: Optional[str] = None,
    applies_to: Optional[str] = None,
    trigger: Optional[str] = None,
) -> dict[int, float]:
    """Filter candidates by metadata. Returns filtered {index: score}."""
    filtered = {}
    for idx, score in candidates.items():
        chunk = chunks[idx]

        if source_types and chunk.source not in source_types:
            continue

        meta = chunk.metadata

        if module and meta.get("module", "").lower() != module.lower():
            continue

        if tags:
            chunk_tags = [t.lower() for t in meta.get("tags", [])]
            if not any(t.lower() in chunk_tags for t in tags):
                continue

        if group and meta.get("group", "").lower() != group.lower():
            continue

        if applies_to:
            applies = [a.lower() for a in meta.get("applies", [])]
            if not any(applies_to.lower() in a for a in applies):
                continue

        if trigger:
            triggers = [t.lower() for t in meta.get("triggers", [])]
            if not any(trigger.lower() in t for t in triggers):
                continue

        filtered[idx] = score
    return filtered


def search(
    query: str,
    chunks: list[Chunk],
    index: faiss.Index,
    bm25: BM25Okapi,
    model: SentenceTransformer,
    top_k: int = DEFAULT_TOP_K,
    source_types: Optional[list[str]] = None,
    module: Optional[str] = None,
    tags: Optional[list[str]] = None,
    group: Optional[str] = None,
    applies_to: Optional[str] = None,
    trigger: Optional[str] = None,
) -> list[SearchResult]:
    """Full hybrid search pipeline: BM25 + vector → RRF → filter → top_k."""
    top_k = min(top_k, MAX_TOP_K)

    # Step 1: BM25 retrieval
    bm25_results = bm25_search(query, chunks, bm25)

    # Step 2: Vector retrieval
    vector_results = vector_search(query, index, model)

    # Step 3: RRF fusion
    fused = rrf_fusion(bm25_results, vector_results)

    # Step 4: Apply filters
    filtered = apply_filters(
        chunks, fused,
        source_types=source_types,
        module=module,
        tags=tags,
        group=group,
        applies_to=applies_to,
        trigger=trigger,
    )

    # Step 5: Sort by fused score and take top_k
    sorted_results = sorted(filtered.items(), key=lambda x: x[1], reverse=True)[:top_k]

    return [
        SearchResult(
            chunk=chunks[idx],
            score=round(score, 6),
            rank=rank + 1,
        )
        for rank, (idx, score) in enumerate(sorted_results)
    ]
