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
from lookup import (
    find_class_entries,
    get_class_deps,
    get_dependents,
    get_file_info,
    get_listeners,
    get_commands,
    load_entry_points,
    invalidate_all,
    load_classes,
)
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
        # Also invalidate lookup caches
        invalidate_all()
        return (
            f"Index rebuilt: {result['chunks']} chunks in {result['timing']['total']}s\n"
            f"Timing breakdown: {result['timing']}"
        )
    return "No changes detected. Index is up to date. Use force=True to rebuild anyway."


# ── Tool 6: find_class ─────────────────────────────────────────────────

@mcp.tool()
def find_class(
    name: str,
    fuzzy: bool = False,
) -> str:
    """Exact class lookup by name. Returns module, file, tags, summary, extends.

    Args:
        name: Class simple name (e.g. "CECaller", "PlayerListener")
        fuzzy: If True, also match partial/substring names (default False)
    """
    entries = find_class_entries(name, fuzzy=fuzzy)
    if not entries:
        suggestion = ""
        if not fuzzy:
            fuzzy_results = find_class_entries(name, fuzzy=True)
            if fuzzy_results:
                names = [e["name"] for e in fuzzy_results[:5]]
                suggestion = f"\nDid you mean: {', '.join(names)}? (use fuzzy=True)"
        return f"Class '{name}' not found.{suggestion}"

    lines = []
    for e in entries:
        lines.append(f"**{e.get('name', '?')}**")
        lines.append(f"  FQN: {e.get('fqn', '?')}")
        lines.append(f"  Module: {e.get('module', '?')}")
        lines.append(f"  File: {e.get('file', '?')}")
        lines.append(f"  Type: {e.get('type', '?')}")
        if e.get("extends"):
            lines.append(f"  Extends: {e['extends']}")
        if e.get("tags"):
            lines.append(f"  Tags: {', '.join(e['tags'])}")
        lines.append(f"  Summary: {e.get('summary', '?')}")
        lines.append("")
    return "\n".join(lines)


# ── Tool 7: get_class_dependencies ─────────────────────────────────────

@mcp.tool()
def get_class_dependencies(class_name: str) -> str:
    """What does this class depend on? Returns dependency list with metadata.

    Args:
        class_name: Class simple name (e.g. "CECaller", "PlayerListener")
    """
    deps = get_class_deps(class_name)
    if not deps:
        info = get_file_info(class_name)
        if info is None:
            return f"Class '{class_name}' not found in file-hashes cache."
        return f"Class '{class_name}' has no recorded dependencies."

    # Enrich each dependency with module info
    lines = [f"**{class_name}** depends on {len(deps)} classes:\n"]
    by_name, _ = load_classes()
    for dep in sorted(deps):
        dep_entries = by_name.get(dep.lower(), [])
        if dep_entries:
            e = dep_entries[0]
            lines.append(f"  - **{dep}** ({e.get('module', '?')}) — {e.get('summary', '?')}")
        else:
            lines.append(f"  - **{dep}** (external/unknown)")
    return "\n".join(lines)


# ── Tool 8: get_class_dependents ───────────────────────────────────────

@mcp.tool()
def get_class_dependents(class_name: str) -> str:
    """What classes depend on this one? Returns all classes that import/reference it.

    Args:
        class_name: Class simple name (e.g. "CECaller", "ConditionData")
    """
    dependents = get_dependents(class_name)
    if not dependents:
        return f"No classes found that depend on '{class_name}'."

    # Enrich with module info
    lines = [f"**{class_name}** is depended on by {len(dependents)} classes:\n"]
    by_name, _ = load_classes()
    for dep in sorted(set(dependents)):
        dep_entries = by_name.get(dep.lower(), [])
        if dep_entries:
            e = dep_entries[0]
            lines.append(f"  - **{dep}** ({e.get('module', '?')}) — {e.get('summary', '?')}")
        else:
            lines.append(f"  - **{dep}**")
    return "\n".join(lines)


# ── Tool 9: get_entry_points ──────────────────────────────────────────

@mcp.tool()
def get_entry_points(module: Optional[str] = None) -> str:
    """Get listeners and commands. Optionally filter by module.

    Args:
        module: Filter by module name (e.g. "ListenerModule", "CommandModule"). If omitted, returns all.
    """
    listeners = get_listeners(module)
    commands = get_commands(module)

    lines = []

    if listeners:
        lines.append(f"## Listeners ({len(listeners)})\n")
        for l in listeners:
            events = ", ".join(l.get("events", []))
            conditional = f" [conditional: {l.get('condition', '?')}]" if l.get("conditional") else ""
            lines.append(f"  - **{l.get('class', '?')}** ({l.get('module', '?')}){conditional}")
            lines.append(f"    Events: {events}")
            if l.get("priorityNotes"):
                lines.append(f"    Priority: {l['priorityNotes']}")
            lines.append("")

    if commands:
        lines.append(f"## Commands ({len(commands)})\n")
        for c in commands:
            subs = ", ".join(c.get("subcommands", []))
            lines.append(f"  - **{c.get('name', '?')}** → {c.get('handler', '?')} ({c.get('module', '?')})")
            if subs:
                lines.append(f"    Subcommands: {subs}")
            lines.append("")

    if not lines:
        return f"No entry points found{' for module ' + module if module else ''}."

    return "\n".join(lines)


# ── Tool 10: get_plugin_summary ────────────────────────────────────────

@mcp.tool()
def get_plugin_summary() -> str:
    """Get the Level 4 plugin overview (PLUGIN-SUMMARY.md). No arguments needed."""
    summary_path = SUMMARIES_DIR / "PLUGIN-SUMMARY.md"
    if not summary_path.exists():
        return "PLUGIN-SUMMARY.md not found."
    return summary_path.read_text(encoding="utf-8")


# ── Tool 11: get_file_summary ─────────────────────────────────────────

@mcp.tool()
def get_file_summary(path_or_class: str) -> str:
    """Get cached file summary, methods, tags, and dependencies.

    Accepts either a file path or class name.

    Args:
        path_or_class: Relative file path (e.g. "src/main/java/.../CECaller.java") or class name (e.g. "CECaller")
    """
    info = get_file_info(path_or_class)
    if info is None:
        return f"No cached info found for '{path_or_class}'."

    lines = [f"**{info.get('className', path_or_class)}**"]
    lines.append(f"  Module: {info.get('module', '?')}")
    lines.append(f"  Lines: {info.get('lineCount', '?')}")
    lines.append(f"  Summary: {info.get('summary', '?')}")

    if info.get("tags"):
        lines.append(f"  Tags: {', '.join(info['tags'])}")

    if info.get("methods"):
        lines.append(f"\n  Methods ({len(info['methods'])}):")
        for m in info["methods"]:
            if isinstance(m, str):
                lines.append(f"    - {m}")
            elif isinstance(m, dict):
                lines.append(f"    - {m.get('name', '?')}({m.get('params', '')}) → {m.get('returns', 'void')}")

    if info.get("dependencies"):
        lines.append(f"\n  Dependencies ({len(info['dependencies'])}):")
        for d in info["dependencies"]:
            lines.append(f"    - {d}")

    return "\n".join(lines)


# ── Tool 12: analyze_impact ───────────────────────────────────────────

@mcp.tool()
def analyze_impact(class_name: str, depth: int = 2) -> str:
    """Analyze what would be affected by changing a class.

    Returns direct/transitive dependents, affected entry points, and affected configs.

    Args:
        class_name: Class simple name (e.g. "CECaller", "ConditionData")
        depth: How many levels of transitive dependents to follow (default 2, max 3)
    """
    depth = max(1, min(depth, 3))

    # Direct dependents
    direct = set(get_dependents(class_name))
    if not direct:
        # Check if class even exists
        info = get_file_info(class_name)
        if info is None:
            return f"Class '{class_name}' not found in indexes."
        return f"No dependents found for '{class_name}'. This class has no known consumers."

    # Transitive dependents
    all_dependents = set(direct)
    current_level = direct
    for _ in range(depth - 1):
        next_level = set()
        for dep in current_level:
            for transitive in get_dependents(dep):
                if transitive not in all_dependents and transitive.lower() != class_name.lower():
                    next_level.add(transitive)
        all_dependents.update(next_level)
        current_level = next_level
        if not current_level:
            break

    indirect = all_dependents - direct

    # Find affected entry points (listeners/commands among dependents)
    ep = load_entry_points()
    all_listeners = ep.get("listeners", {}).get("core", []) + ep.get("listeners", {}).get("conditional", [])
    all_commands = ep.get("commands", {}).get("root", []) + ep.get("commands", {}).get("subcommands", [])

    affected_listeners = [
        l for l in all_listeners
        if l.get("class", "") in all_dependents or l.get("class", "") == class_name
    ]
    affected_commands = [
        c for c in all_commands
        if c.get("handler", c.get("class", "")) in all_dependents
        or c.get("handler", c.get("class", "")) == class_name
    ]

    # Find affected config classes among dependents
    by_name, _ = load_classes()
    affected_configs = []
    for dep in all_dependents:
        dep_entries = by_name.get(dep.lower(), [])
        for e in dep_entries:
            if "config" in (e.get("tags") or []) or "Config" in dep:
                affected_configs.append(dep)
                break

    # Risk assessment
    total_affected = len(all_dependents)
    if total_affected >= 20 or affected_listeners:
        risk = "HIGH"
    elif total_affected >= 5:
        risk = "MEDIUM"
    else:
        risk = "LOW"

    # Format report
    lines = [f"# Impact Analysis: {class_name}\n"]
    lines.append(f"**Risk Level:** {risk}")
    lines.append(f"**Total affected classes:** {total_affected}\n")

    lines.append(f"## Direct Dependents ({len(direct)})")
    for d in sorted(direct):
        dep_entries = by_name.get(d.lower(), [])
        module = dep_entries[0].get("module", "?") if dep_entries else "?"
        lines.append(f"  - {d} ({module})")

    if indirect:
        lines.append(f"\n## Transitive Dependents ({len(indirect)})")
        for d in sorted(indirect):
            dep_entries = by_name.get(d.lower(), [])
            module = dep_entries[0].get("module", "?") if dep_entries else "?"
            lines.append(f"  - {d} ({module})")

    if affected_listeners:
        lines.append(f"\n## Affected Listeners ({len(affected_listeners)})")
        for l in affected_listeners:
            events = ", ".join(l.get("events", [])[:3])
            lines.append(f"  - {l.get('class', '?')} [{events}...]")

    if affected_commands:
        lines.append(f"\n## Affected Commands ({len(affected_commands)})")
        for c in affected_commands:
            lines.append(f"  - {c.get('name', '?')} → {c.get('handler', '?')}")

    if affected_configs:
        lines.append(f"\n## Affected Configs ({len(affected_configs)})")
        for c in sorted(set(affected_configs)):
            lines.append(f"  - {c}")

    return "\n".join(lines)


# ── Entry point ─────────────────────────────────────────────────────────

if __name__ == "__main__":
    mcp.run()
