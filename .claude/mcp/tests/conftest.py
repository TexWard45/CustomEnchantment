"""Shared fixtures for MCP server tests."""

from __future__ import annotations

import sys
from pathlib import Path

import pytest

# Add parent directory to path so we can import server modules
sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from chunker import build_all_chunks
from indexer import load_index
from models import Chunk


@pytest.fixture(scope="session")
def all_chunks():
    """Build all chunks once for the test session."""
    return build_all_chunks()


@pytest.fixture(scope="session")
def search_components():
    """Load the full search index (chunks, faiss, bm25, model) once."""
    chunks, index, bm25, model = load_index()
    return chunks, index, bm25, model
