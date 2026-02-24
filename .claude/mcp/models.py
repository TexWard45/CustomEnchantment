"""Data models for the semantic search MCP server."""

from __future__ import annotations

from dataclasses import dataclass, field


@dataclass
class Chunk:
    """A single searchable unit of information."""

    id: str  # unique key, e.g. "class:CECaller" or "enchant:cactus"
    source: str  # "classes", "enchantments", "methods", "configs", "modules", "entry-points", "summaries"
    embed_text: str  # text sent to the embedding model
    display_text: str  # text returned to the user
    metadata: dict = field(default_factory=dict)
    # metadata keys vary by source:
    #   classes:      module, type, tags, fqn, file
    #   enchantments: group, applies, triggers, effects, file
    #   methods:      class, category, tags
    #   configs:      module, yamlFile, keyPaths
    #   modules:      dependsOn, dependedBy, tags
    #   entry-points: module, events, tags
    #   summaries:    module, section


@dataclass
class SearchResult:
    """A ranked search result."""

    chunk: Chunk
    score: float
    rank: int
