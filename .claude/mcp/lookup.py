"""Pure JSON index loaders for the MCP server's lookup tools.

Provides lazy-loaded, cached access to Phase 2/6 index data:
- classes.json  → class metadata (name, module, file, tags, summary)
- file-hashes.json → per-file summary, methods, dependencies
- entry-points.json → listeners, commands, scheduled tasks
- dependencies.json → module-level dependency graph
"""

from __future__ import annotations

import json
from pathlib import Path
from typing import Optional

from config import INDEX_DIR, PROJECT_ROOT

# Cache directory for file-hashes.json
_CACHE_DIR = PROJECT_ROOT / ".claude" / "docs" / "codemap" / "cache"

# ── Lazy singletons ──────────────────────────────────────────────────

_classes_by_name: dict[str, list[dict]] | None = None
_classes_list: list[dict] | None = None
_file_hashes: dict[str, dict] | None = None
_entry_points: dict | None = None
_dependencies: dict | None = None
_reverse_deps: dict[str, list[str]] | None = None


def _load_json(path: Path) -> dict | list:
    """Load a JSON file, returning empty dict on missing/corrupt file."""
    if not path.exists():
        return {}
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except (json.JSONDecodeError, OSError):
        return {}


# ── Classes index ────────────────────────────────────────────────────

def load_classes() -> tuple[dict[str, list[dict]], list[dict]]:
    """Load classes.json → (name→[entries], flat list).

    Returns a dict keyed by lowercase class name mapping to list of
    matching class entries (handles name collisions), plus the flat list.
    """
    global _classes_by_name, _classes_list
    if _classes_by_name is not None:
        return _classes_by_name, _classes_list

    data = _load_json(INDEX_DIR / "classes.json")
    raw = data.get("classes", []) if isinstance(data, dict) else []

    by_name: dict[str, list[dict]] = {}
    for cls in raw:
        key = cls.get("name", "").lower()
        by_name.setdefault(key, []).append(cls)

    _classes_by_name = by_name
    _classes_list = raw
    return _classes_by_name, _classes_list


def find_class_entries(name: str, fuzzy: bool = False) -> list[dict]:
    """Find class entries by name.

    Exact match is case-insensitive on class simple name.
    Fuzzy match also checks if the query is a substring of the class name.
    """
    by_name, all_classes = load_classes()
    key = name.lower()

    # Exact match
    exact = by_name.get(key, [])
    if exact or not fuzzy:
        return exact

    # Fuzzy: substring match
    results = []
    for cls in all_classes:
        if key in cls.get("name", "").lower():
            results.append(cls)
    return results[:20]  # Cap fuzzy results


def class_name_to_file(class_name: str) -> str | None:
    """Resolve a class name to its file path (from classes.json)."""
    entries = find_class_entries(class_name)
    if entries:
        return entries[0].get("file")
    return None


# ── File hashes / cache ─────────────────────────────────────────────

def load_file_hashes() -> dict[str, dict]:
    """Load file-hashes.json → dict keyed by relative file path."""
    global _file_hashes
    if _file_hashes is not None:
        return _file_hashes

    data = _load_json(_CACHE_DIR / "file-hashes.json")
    _file_hashes = data.get("files", {}) if isinstance(data, dict) else {}
    return _file_hashes


def get_file_info(path_or_class: str) -> dict | None:
    """Look up file info by path or class name.

    Accepts either a relative file path or a class simple name.
    Returns the file-hashes entry with summary, methods, dependencies, tags.
    """
    hashes = load_file_hashes()

    # Try direct path match
    if path_or_class in hashes:
        return hashes[path_or_class]

    # Try class name → file path → lookup
    file_path = class_name_to_file(path_or_class)
    if file_path and file_path in hashes:
        return hashes[file_path]

    # Try partial path match (e.g. "CECaller.java")
    for path, info in hashes.items():
        if path.endswith(path_or_class) or info.get("className", "").lower() == path_or_class.lower():
            return info

    return None


# ── Entry points ─────────────────────────────────────────────────────

def load_entry_points() -> dict:
    """Load entry-points.json → full structure."""
    global _entry_points
    if _entry_points is not None:
        return _entry_points

    _entry_points = _load_json(INDEX_DIR / "entry-points.json")
    if not isinstance(_entry_points, dict):
        _entry_points = {}
    return _entry_points


def get_listeners(module: str | None = None) -> list[dict]:
    """Get listener entries, optionally filtered by module."""
    ep = load_entry_points()
    listeners_section = ep.get("listeners", {})
    all_listeners = listeners_section.get("core", []) + listeners_section.get("conditional", [])

    if module:
        mod_lower = module.lower().replace("module", "").strip()
        return [
            l for l in all_listeners
            if mod_lower in l.get("module", "").lower()
        ]
    return all_listeners


def get_commands(module: str | None = None) -> list[dict]:
    """Get command entries, optionally filtered by module."""
    ep = load_entry_points()
    commands_section = ep.get("commands", {})
    all_commands = commands_section.get("root", []) + commands_section.get("subcommands", [])

    if module:
        mod_lower = module.lower().replace("module", "").strip()
        return [
            c for c in all_commands
            if mod_lower in c.get("module", "").lower()
        ]
    return all_commands


# ── Dependencies (reverse map) ───────────────────────────────────────

def load_dependencies() -> dict:
    """Load dependencies.json → full structure."""
    global _dependencies
    if _dependencies is not None:
        return _dependencies

    _dependencies = _load_json(INDEX_DIR / "dependencies.json")
    if not isinstance(_dependencies, dict):
        _dependencies = {}
    return _dependencies


def build_reverse_deps() -> dict[str, list[str]]:
    """Build reverse dependency map: class_name → [classes that depend on it].

    Uses file-hashes.json dependency lists to create an inverted index.
    """
    global _reverse_deps
    if _reverse_deps is not None:
        return _reverse_deps

    hashes = load_file_hashes()
    reverse: dict[str, list[str]] = {}

    for _path, info in hashes.items():
        source_class = info.get("className", "")
        if not source_class:
            continue
        for dep in info.get("dependencies", []):
            dep_lower = dep.lower()
            reverse.setdefault(dep_lower, []).append(source_class)

    _reverse_deps = reverse
    return _reverse_deps


def get_dependents(class_name: str) -> list[str]:
    """Get all classes that depend on the given class."""
    reverse = build_reverse_deps()
    return reverse.get(class_name.lower(), [])


def get_class_deps(class_name: str) -> list[str]:
    """Get what the given class depends on (from file-hashes)."""
    info = get_file_info(class_name)
    if info:
        return info.get("dependencies", [])
    return []


# ── Cache invalidation ───────────────────────────────────────────────

def invalidate_all():
    """Clear all cached data (call after reindex)."""
    global _classes_by_name, _classes_list, _file_hashes
    global _entry_points, _dependencies, _reverse_deps
    _classes_by_name = None
    _classes_list = None
    _file_hashes = None
    _entry_points = None
    _dependencies = None
    _reverse_deps = None
