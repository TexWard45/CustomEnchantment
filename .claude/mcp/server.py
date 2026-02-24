"""FastMCP server for semantic code search over CustomEnchantment codebase."""

from __future__ import annotations

import sys
from contextlib import asynccontextmanager
from pathlib import Path
from typing import Optional

from fastmcp import FastMCP

# Ensure the package directory is importable
sys.path.insert(0, str(Path(__file__).resolve().parent))

from config import DEFAULT_TOP_K, SUMMARIES_DIR
from indexer import build_index, load_index
from searcher import search


# ── Global state loaded at startup ──────────────────────────────────────

_chunks = None
_index = None
_bm25 = None
_model = None


@asynccontextmanager
async def lifespan(app):
    """Load the index and model at startup."""
    global _chunks, _index, _bm25, _model
    _chunks, _index, _bm25, _model = load_index()
    yield


mcp = FastMCP(
    "CustomEnchantment Semantic Search",
    lifespan=lifespan,
)


def _format_results(results) -> str:
    """Format SearchResult list into readable text."""
    if not results:
        return "No results found."
    lines = []
    for r in results:
        lines.append(f"[{r.rank}] (score: {r.score})")
        lines.append(r.chunk.display_text)
        lines.append(f"    id: {r.chunk.id} | source: {r.chunk.source}")
        lines.append("")
    return "\n".join(lines)


# ── Tool 1: search_code ────────────────────────────────────────────────

@mcp.tool()
def search_code(
    query: str,
    top_k: int = DEFAULT_TOP_K,
    types: Optional[str] = None,
    module: Optional[str] = None,
    tags: Optional[str] = None,
) -> str:
    """Search classes, methods, listeners, modules, and summaries by natural language.

    Excludes enchantments and configs (use search_enchantments/search_configs).

    Args:
        query: Natural language search query (e.g. "damage reduction", "player join event")
        top_k: Number of results to return (default 10, max 50)
        types: Comma-separated source types to include (classes,methods,entry-points,modules,summaries)
        module: Filter by module name (e.g. "EnchantModule", "ListenerModule")
        tags: Comma-separated tags to filter by (e.g. "combat,enchantment")
    """
    source_types = [t.strip() for t in types.split(",")] if types else [
        "classes", "methods", "entry-points", "modules", "summaries"
    ]
    tag_list = [t.strip() for t in tags.split(",")] if tags else None

    results = search(
        query, _chunks, _index, _bm25, _model,
        top_k=top_k,
        source_types=source_types,
        module=module,
        tags=tag_list,
    )
    return _format_results(results)


# ── Tool 2: search_enchantments ────────────────────────────────────────

@mcp.tool()
def search_enchantments(
    query: str,
    top_k: int = DEFAULT_TOP_K,
    group: Optional[str] = None,
    applies_to: Optional[str] = None,
    trigger: Optional[str] = None,
) -> str:
    """Search the 579 custom enchantments by natural language with domain-specific filters.

    Args:
        query: Natural language query (e.g. "armor that reflects damage", "fire sword enchantment")
        top_k: Number of results (default 10, max 50)
        group: Filter by enchantment group (epic, legendary, rare, supreme, ultimate, etc.)
        applies_to: Filter by item type (e.g. "SWORD", "ALL_ARMOR", "HELMET")
        trigger: Filter by trigger type (e.g. "DEFENSE", "ATTACK", "ARMOR_EQUIP")
    """
    results = search(
        query, _chunks, _index, _bm25, _model,
        top_k=top_k,
        source_types=["enchantments"],
        group=group,
        applies_to=applies_to,
        trigger=trigger,
    )
    return _format_results(results)


# ── Tool 3: search_configs ─────────────────────────────────────────────

@mcp.tool()
def search_configs(
    query: str,
    top_k: int = DEFAULT_TOP_K,
) -> str:
    """Search config classes and YAML key paths.

    Args:
        query: Natural language query (e.g. "combat settings", "redis configuration")
        top_k: Number of results (default 10, max 50)
    """
    results = search(
        query, _chunks, _index, _bm25, _model,
        top_k=top_k,
        source_types=["configs"],
    )
    return _format_results(results)


# ── Tool 4: get_module_summary ──────────────────────────────────────────

@mcp.tool()
def get_module_summary(
    module_name: str,
    section: Optional[str] = None,
) -> str:
    """Get a Phase 3 markdown summary for a module. Direct lookup, no search.

    Args:
        module_name: Module name (e.g. "enchant", "item", "player", "PLUGIN-SUMMARY")
        section: Optional heading to filter to (e.g. "Key Classes", "Execution Flow")
    """
    # Map module names to filenames
    name = module_name.lower().replace("module", "").strip()
    candidates = [
        SUMMARIES_DIR / f"{name}.md",
        SUMMARIES_DIR / f"{module_name}.md",
        SUMMARIES_DIR / f"{module_name.upper()}.md",
    ]

    md_path = None
    for c in candidates:
        if c.exists():
            md_path = c
            break

    if md_path is None:
        available = [p.stem for p in sorted(SUMMARIES_DIR.glob("*.md"))]
        return f"Module summary not found for '{module_name}'. Available: {', '.join(available)}"

    text = md_path.read_text(encoding="utf-8")

    if section:
        # Find the section by heading
        import re
        pattern = rf"(###?\s+{re.escape(section)}.*?)(?=\n###?\s|\Z)"
        match = re.search(pattern, text, re.DOTALL | re.IGNORECASE)
        if match:
            return match.group(1).strip()
        return f"Section '{section}' not found in {md_path.name}. Full content:\n\n{text}"

    return text


# ── Tool 5: reindex ────────────────────────────────────────────────────

@mcp.tool()
def reindex(force: bool = False) -> str:
    """Rebuild the search index. Detects changes automatically unless force=True.

    Args:
        force: If True, rebuild even if no changes detected (default False)
    """
    global _chunks, _index, _bm25, _model

    result = build_index(force=force)
    if result["status"] == "built":
        # Reload into memory
        _chunks, _index, _bm25, _model = load_index()
        return (
            f"Index rebuilt: {result['chunks']} chunks in {result['timing']['total']}s\n"
            f"Timing breakdown: {result['timing']}"
        )
    return "No changes detected. Index is up to date. Use force=True to rebuild anyway."


# ── Entry point ─────────────────────────────────────────────────────────

if __name__ == "__main__":
    mcp.run()
