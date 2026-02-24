"""Builds and persists the FAISS vector index and BM25 model from chunks."""

from __future__ import annotations

import hashlib
import json
import pickle
import time

import faiss
import numpy as np
from rank_bm25 import BM25Okapi
from sentence_transformers import SentenceTransformer

from chunker import build_all_chunks
from config import (
    BM25_PATH,
    CHUNKS_PATH,
    EMBED_DIM,
    EMBED_MODEL,
    FAISS_INDEX_PATH,
    INDEX_DIR,
    MANIFEST_PATH,
    PERSIST_DIR,
    SUMMARIES_DIR,
)
from models import Chunk


def _tokenize(text: str) -> list[str]:
    """Simple whitespace + lowercase tokenizer for BM25."""
    return text.lower().split()


def _compute_manifest() -> dict[str, str]:
    """Compute file modification times for incremental reindex detection."""
    manifest: dict[str, str] = {}
    for json_file in sorted(INDEX_DIR.glob("*.json")):
        stat = json_file.stat()
        manifest[str(json_file)] = f"{stat.st_mtime:.6f}:{stat.st_size}"
    for md_file in sorted(SUMMARIES_DIR.glob("*.md")):
        stat = md_file.stat()
        manifest[str(md_file)] = f"{stat.st_mtime:.6f}:{stat.st_size}"
    return manifest


def _manifest_hash(manifest: dict[str, str]) -> str:
    """Hash the manifest for quick comparison."""
    raw = json.dumps(manifest, sort_keys=True)
    return hashlib.sha256(raw.encode()).hexdigest()


def needs_reindex() -> bool:
    """Check if data files have changed since last index build."""
    if not all(p.exists() for p in [FAISS_INDEX_PATH, CHUNKS_PATH, BM25_PATH, MANIFEST_PATH]):
        return True
    current = _compute_manifest()
    current_hash = _manifest_hash(current)
    try:
        with open(MANIFEST_PATH, encoding="utf-8") as f:
            saved = json.load(f)
        return saved.get("hash") != current_hash
    except (json.JSONDecodeError, KeyError):
        return True


def build_index(force: bool = False) -> dict:
    """Build or rebuild the full search index.

    Returns a dict with timing and count info.
    """
    if not force and not needs_reindex():
        return {"status": "skipped", "reason": "no changes detected"}

    PERSIST_DIR.mkdir(parents=True, exist_ok=True)

    t0 = time.time()

    # 1. Build chunks
    chunks = build_all_chunks()
    t_chunks = time.time()

    # 2. Embed
    model = SentenceTransformer(EMBED_MODEL)
    texts = [c.embed_text for c in chunks]
    embeddings = model.encode(texts, show_progress_bar=True, normalize_embeddings=True)
    embeddings = np.array(embeddings, dtype=np.float32)
    t_embed = time.time()

    # 3. FAISS index (inner product on normalized vectors = cosine similarity)
    index = faiss.IndexFlatIP(EMBED_DIM)
    index.add(embeddings)
    t_faiss = time.time()

    # 4. BM25
    tokenized = [_tokenize(c.embed_text) for c in chunks]
    bm25 = BM25Okapi(tokenized)
    t_bm25 = time.time()

    # 5. Persist
    faiss.write_index(index, str(FAISS_INDEX_PATH))
    with open(CHUNKS_PATH, "wb") as f:
        pickle.dump(chunks, f)
    with open(BM25_PATH, "wb") as f:
        pickle.dump(bm25, f)

    manifest = _compute_manifest()
    with open(MANIFEST_PATH, "w", encoding="utf-8") as f:
        json.dump({"hash": _manifest_hash(manifest), "files": manifest}, f, indent=2)

    t_end = time.time()

    return {
        "status": "built",
        "chunks": len(chunks),
        "timing": {
            "chunking": round(t_chunks - t0, 2),
            "embedding": round(t_embed - t_chunks, 2),
            "faiss": round(t_faiss - t_embed, 2),
            "bm25": round(t_bm25 - t_faiss, 2),
            "persist": round(t_end - t_bm25, 2),
            "total": round(t_end - t0, 2),
        },
    }


def load_index() -> tuple[list[Chunk], faiss.Index, BM25Okapi, SentenceTransformer]:
    """Load persisted index and model. Rebuilds if needed."""
    if needs_reindex():
        build_index(force=True)

    with open(CHUNKS_PATH, "rb") as f:
        chunks = pickle.load(f)
    index = faiss.read_index(str(FAISS_INDEX_PATH))
    with open(BM25_PATH, "rb") as f:
        bm25 = pickle.load(f)
    model = SentenceTransformer(EMBED_MODEL)
    return chunks, index, bm25, model


if __name__ == "__main__":
    result = build_index(force=True)
    print(json.dumps(result, indent=2))
