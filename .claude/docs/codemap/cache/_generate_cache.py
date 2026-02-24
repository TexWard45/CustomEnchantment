#!/usr/bin/env python3
"""
Generate SHA-256 file hash cache for Java source files.

Modes:
  --full           Rebuild entire cache from scratch
  --diff FILE...   Update cache for specific changed files only
  --check          Validate cache integrity without modifying it

Uses classes.json and methods.json from Phase 2 indexes for metadata lookup.
Output: .claude/docs/codemap/cache/file-hashes.json
"""

import argparse
import hashlib
import json
import sys
import time
from datetime import datetime, timezone
from pathlib import Path

# Paths relative to project root
PROJECT_ROOT = Path(__file__).resolve().parent.parent.parent.parent.parent
SRC_ROOT = PROJECT_ROOT / "src" / "main" / "java"
CACHE_FILE = PROJECT_ROOT / ".claude" / "docs" / "codemap" / "cache" / "file-hashes.json"
CLASSES_JSON = PROJECT_ROOT / ".claude" / "docs" / "codemap" / "index" / "classes.json"
METHODS_JSON = PROJECT_ROOT / ".claude" / "docs" / "codemap" / "index" / "methods.json"


def sha256_file(filepath: Path) -> str:
    """Compute SHA-256 hash of a file's contents."""
    h = hashlib.sha256()
    with open(filepath, "rb") as f:
        for chunk in iter(lambda: f.read(8192), b""):
            h.update(chunk)
    return f"sha256:{h.hexdigest()}"


def load_classes_index() -> dict:
    """Load classes.json and build lookup by class name."""
    if not CLASSES_JSON.exists():
        print(f"Warning: {CLASSES_JSON} not found, metadata will be limited", file=sys.stderr)
        return {}
    with open(CLASSES_JSON, "r", encoding="utf-8") as f:
        data = json.load(f)
    lookup = {}
    for cls in data.get("classes", []):
        lookup[cls["name"]] = cls
    return lookup


def load_methods_index() -> dict:
    """Load methods.json and build lookup by class name -> list of method names."""
    if not METHODS_JSON.exists():
        print(f"Warning: {METHODS_JSON} not found, method info will be limited", file=sys.stderr)
        return {}
    with open(METHODS_JSON, "r", encoding="utf-8") as f:
        data = json.load(f)
    lookup = {}
    for method in data.get("methods", []):
        cls_name = method["class"]
        if cls_name not in lookup:
            lookup[cls_name] = []
        lookup[cls_name].append(method["name"])
    return lookup


def relative_path(filepath: Path) -> str:
    """Get relative path from project root using forward slashes."""
    return filepath.relative_to(PROJECT_ROOT).as_posix()


def class_name_from_file(filepath: Path) -> str:
    """Extract class name from Java file path."""
    return filepath.stem


def count_lines(filepath: Path) -> int:
    """Count lines in a file."""
    with open(filepath, "r", encoding="utf-8", errors="replace") as f:
        return sum(1 for _ in f)


def extract_dependencies_from_source(filepath: Path) -> list:
    """Extract import-based dependencies from Java source."""
    deps = []
    try:
        with open(filepath, "r", encoding="utf-8", errors="replace") as f:
            for line in f:
                line = line.strip()
                if line.startswith("import ") and "com.bafmc.customenchantment" in line:
                    # Extract class name from import
                    parts = line.rstrip(";").split(".")
                    if parts:
                        dep = parts[-1]
                        if dep != "*":
                            deps.append(dep)
                elif line.startswith("public class") or line.startswith("public interface"):
                    break  # Stop after reaching class declaration
    except Exception:
        pass
    return deps


def build_entry(filepath: Path, classes_lookup: dict, methods_lookup: dict) -> dict:
    """Build a cache entry for a single Java file."""
    cls_name = class_name_from_file(filepath)
    cls_info = classes_lookup.get(cls_name, {})
    methods = methods_lookup.get(cls_name, [])

    return {
        "hash": sha256_file(filepath),
        "lastAnalyzed": datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ"),
        "className": cls_name,
        "module": cls_info.get("module", "Unknown"),
        "lineCount": count_lines(filepath),
        "summary": cls_info.get("summary", ""),
        "tags": cls_info.get("tags", []),
        "methods": methods,
        "dependencies": extract_dependencies_from_source(filepath),
    }


def find_all_java_files() -> list:
    """Find all .java files under src/main/java/."""
    if not SRC_ROOT.exists():
        print(f"Error: {SRC_ROOT} does not exist", file=sys.stderr)
        sys.exit(1)
    return sorted(SRC_ROOT.rglob("*.java"))


def load_cache() -> dict:
    """Load existing cache file or return empty structure."""
    if CACHE_FILE.exists():
        with open(CACHE_FILE, "r", encoding="utf-8") as f:
            return json.load(f)
    return {"generated": "", "fileCount": 0, "files": {}}


def save_cache(cache: dict):
    """Write cache to disk."""
    cache["generated"] = datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")
    cache["fileCount"] = len(cache["files"])
    CACHE_FILE.parent.mkdir(parents=True, exist_ok=True)
    with open(CACHE_FILE, "w", encoding="utf-8") as f:
        json.dump(cache, f, indent=1, ensure_ascii=False)


def cmd_full():
    """Rebuild entire cache from scratch."""
    start = time.time()
    classes_lookup = load_classes_index()
    methods_lookup = load_methods_index()
    java_files = find_all_java_files()

    cache = {"generated": "", "fileCount": 0, "files": {}}
    for filepath in java_files:
        rel = relative_path(filepath)
        cache["files"][rel] = build_entry(filepath, classes_lookup, methods_lookup)

    save_cache(cache)
    elapsed = time.time() - start
    print(f"Cache rebuilt: {len(java_files)} files indexed in {elapsed:.1f}s")
    print(f"Output: {CACHE_FILE}")


def cmd_diff(file_paths: list):
    """Update cache for specific files only."""
    start = time.time()
    classes_lookup = load_classes_index()
    methods_lookup = load_methods_index()
    cache = load_cache()

    changed = 0
    new = 0
    deleted = 0
    unchanged = 0

    for fp_str in file_paths:
        # Normalize path
        fp = Path(fp_str)
        if not fp.is_absolute():
            fp = PROJECT_ROOT / fp

        if not fp.suffix == ".java":
            continue

        rel = relative_path(fp) if fp.exists() else fp_str.replace("\\", "/")

        if not fp.exists():
            # File was deleted
            if rel in cache["files"]:
                del cache["files"][rel]
                deleted += 1
            continue

        new_hash = sha256_file(fp)
        existing = cache["files"].get(rel)

        if existing and existing["hash"] == new_hash:
            unchanged += 1
            continue

        if existing:
            changed += 1
        else:
            new += 1

        cache["files"][rel] = build_entry(fp, classes_lookup, methods_lookup)

    save_cache(cache)
    elapsed = time.time() - start
    total = changed + new + deleted + unchanged
    print(f"Cache updated: {total} files processed in {elapsed:.1f}s")
    print(f"  Changed: {changed} | New: {new} | Deleted: {deleted} | Unchanged: {unchanged}")


def cmd_check():
    """Validate cache integrity without modifying it."""
    start = time.time()
    cache = load_cache()
    java_files = find_all_java_files()

    cached_paths = set(cache.get("files", {}).keys())
    actual_paths = set()
    stale = 0
    missing = 0
    valid = 0

    for filepath in java_files:
        rel = relative_path(filepath)
        actual_paths.add(rel)

        if rel not in cached_paths:
            missing += 1
            continue

        current_hash = sha256_file(filepath)
        if cache["files"][rel]["hash"] != current_hash:
            stale += 1
        else:
            valid += 1

    orphaned = len(cached_paths - actual_paths)
    total = len(java_files)
    hit_rate = (valid / total * 100) if total > 0 else 0
    elapsed = time.time() - start

    print(f"Cache check: {total} files in {elapsed:.1f}s")
    print(f"  Valid: {valid} | Stale: {stale} | Missing: {missing} | Orphaned: {orphaned}")
    print(f"  Hit rate: {hit_rate:.1f}%")

    if hit_rate < 80:
        print("FAIL: Cache hit rate below 80%", file=sys.stderr)
        sys.exit(1)
    else:
        print("PASS: Cache hit rate above 80%")


def main():
    parser = argparse.ArgumentParser(description="File hash cache for Java source files")
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument("--full", action="store_true", help="Rebuild entire cache")
    group.add_argument("--diff", nargs="*", metavar="FILE", help="Update cache for changed files")
    group.add_argument("--check", action="store_true", help="Validate cache integrity")
    args = parser.parse_args()

    if args.full:
        cmd_full()
    elif args.check:
        cmd_check()
    else:
        if not args.diff:
            print("Error: --diff requires at least one file path", file=sys.stderr)
            sys.exit(1)
        cmd_diff(args.diff)


if __name__ == "__main__":
    main()
