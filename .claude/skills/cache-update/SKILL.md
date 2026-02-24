---
name: cache-update
description: Update file analysis cache based on git changes. Detects changed Java files and re-analyzes only those.
---

# Cache Update Skill

Updates the file hash cache at `.claude/docs/codemap/cache/file-hashes.json` by detecting changed files via git and re-analyzing only those that changed.

## When to Use

- After pulling new changes from remote
- After completing a feature implementation
- When the cache-check reports stale entries
- At the start of a session if cache may be outdated

## Commands

### Incremental update (after recent commits)

```bash
python .claude/docs/codemap/cache/_generate_cache.py --diff $(git diff --name-only HEAD~1 -- '*.java')
```

### Update from branch divergence point

```bash
python .claude/docs/codemap/cache/_generate_cache.py --diff $(git diff --name-only origin/master...HEAD -- '*.java')
```

### Full rebuild (when cache is missing or corrupted)

```bash
python .claude/docs/codemap/cache/_generate_cache.py --full
```

### Validate cache integrity

```bash
python .claude/docs/codemap/cache/_generate_cache.py --check
```

## How It Works

1. `--diff` accepts a list of changed file paths
2. For each file: computes SHA-256 hash and compares to cached hash
3. If hash differs or file is new: re-extracts metadata from source + Phase 2 indexes
4. If file was deleted: removes entry from cache
5. Reports summary: changed, new, deleted, unchanged counts

## Cache Structure

Each entry in `file-hashes.json` contains:
- `hash` — SHA-256 of file contents
- `lastAnalyzed` — ISO timestamp of last analysis
- `className` — Java class name
- `module` — Which plugin module it belongs to
- `lineCount` — File line count
- `summary` — One-line description (from classes.json)
- `tags` — Searchable tags
- `methods` — Key method names (from methods.json)
- `dependencies` — Internal project imports

## Integration with Context Loading

Before reading a Java source file, check the cache:
1. Look up the file's relative path in `file-hashes.json`
2. If the cached `summary` and `tags` are sufficient for the task, skip reading the full file
3. If deeper inspection is needed, read the source file directly

## Performance

- Full rebuild: ~0.3s for 545 files
- Incremental update: <0.1s for typical 1-5 file changes
- Cache check: ~0.1s
