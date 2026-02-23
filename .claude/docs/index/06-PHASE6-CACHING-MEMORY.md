# Phase 6 — Caching & Cross-Session Memory

## Purpose

Prevent reprocessing previously analyzed files and maintain persistent knowledge about coding conventions, common patterns, and shared utilities across sessions and projects.

## Design

### Three Memory Tiers

```
Tier 1: FILE CACHE (avoid re-reading unchanged files)
  ├── Hash-based change detection
  ├── Cached analysis results per file
  └── Invalidated on git diff

Tier 2: SESSION MEMORY (within one conversation)
  ├── Task context accumulation
  ├── Discovered relationships
  └── Cleared on /clear

Tier 3: PERSISTENT MEMORY (across sessions and projects)
  ├── Coding conventions learned
  ├── Common patterns catalog
  ├── Frequently used utilities
  ├── Bug patterns and fixes
  └── Stored in .claude auto memory + project memory
```

### Tier 1: File Cache

```json
// .claude/docs/codemap/cache/file-hashes.json
{
  "src/main/java/.../ConditionHealth.java": {
    "hash": "sha256:a1b2c3d4...",
    "lastAnalyzed": "2026-02-20T10:30:00Z",
    "summary": "ConditionHealth: checks entity health against threshold...",
    "tags": ["enchantment", "conditions", "combat"],
    "methods": ["getIdentifier", "check"],
    "dependencies": ["ConditionHook", "ConditionData"]
  },
  "src/main/java/.../CECaller.java": {
    "hash": "sha256:e5f6g7h8...",
    "lastAnalyzed": "2026-02-20T10:30:00Z",
    "summary": "CECaller: orchestrates enchantment execution...",
    "tags": ["enchantment", "core"],
    "methods": ["call", "callCE", "callCEFunction"],
    "dependencies": ["CECallerBuilder", "CEFunction", "ConditionHook", "EffectHook"]
  }
}
```

**Update strategy:**
```pseudocode
function shouldReanalyze(file, cache):
  currentHash = sha256(read(file))
  if file not in cache:
    return true  # Never analyzed
  if cache[file].hash != currentHash:
    return true  # Content changed
  return false   # Use cached analysis
```

### Tier 2: Session Memory

Managed by Claude Code's auto-compaction. Enhance with strategic compaction:

```markdown
# .claude/skills/strategic-compact/SKILL.md (already exists)

Enhanced compaction instructions:
When compacting, preserve:
- Current task classification and scope
- Files already read and their summaries
- Discovered dependencies and relationships
- Decisions made and their rationale
- Test results and error messages
```

### Tier 3: Persistent Memory

Structured auto-memory in `~/.claude/projects/{project}/memory/`:

```
memory/
├── MEMORY.md                    # Top-level index (loaded at startup, <200 lines)
├── conventions.md               # Coding style patterns learned
├── patterns.md                  # Design patterns observed
├── utilities.md                 # Frequently used utility classes/methods
├── bug-patterns.md              # Common bugs and their fixes
├── performance-notes.md         # Performance hotspots and optimizations
└── cross-project.md             # Patterns shared across plugins
```

### What Goes in Persistent Memory

```markdown
# MEMORY.md (loaded every session)

## Project Quick Reference
- 16 modules, load order matters (see MODULE-MAP.md)
- Core flow: Event → Listener → CECaller → Conditions → Effects
- Config: @Configuration + @Path annotations, kebab-case YAML → camelCase Java

## Key Patterns
- New condition: extend ConditionHook, implement getIdentifier() + check()
- New effect: extend EffectHook, implement getIdentifier() + execute()
- New expansion: extend CEPlayerExpansion, register in CEPlayerExpansionRegister

## Common Utilities
- ColorUtils.color(text) for color codes
- ItemStackBuilder for creating items
- ConfigUtils.setupResource() for loading YAML

## Bug Watch
- CECaller null checks: player can disconnect during async effect execution
- MockBukkit: always check isMocked() in @BeforeAll
- @Path defaults: always provide sensible defaults (never null)

## See Also
- conventions.md: Detailed style guide observations
- patterns.md: Full pattern catalog with examples
- bug-patterns.md: Bug patterns and prevention strategies
```

### Convention Learning

When Claude observes a pattern repeated across multiple files, it records it:

```pseudocode
function learnConvention(observation, confidence):
  # Only save if observed 3+ times
  if observation.occurrences >= 3:
    addToMemory("conventions.md", {
      "pattern": observation.description,
      "examples": observation.files[:3],
      "confidence": confidence,
      "firstSeen": now()
    })

# Example observations:
# "All condition classes return identifier in SCREAMING_SNAKE_CASE" (seen 30x)
# "Config classes always have empty-string defaults for passwords" (seen 5x)
# "Listeners self-register in constructor" (seen 15x)
```

### Cross-Project Memory

For patterns shared between CustomEnchantment, BafFramework, CustomMenu, etc.:

```markdown
# cross-project.md

## BafFramework Patterns (applies to ALL plugins)
- PluginModule lifecycle: onEnable → onReload → onSave → onDisable
- ArgumentLine splits on ":" not spaces
- ConditionData keys are case-insensitive (lowercased)
- StrategyRegister for extensible config-driven behaviors

## Bukkit/Paper Patterns (applies to ALL Minecraft plugins)
- Main thread: all Bukkit API calls except data prep
- Async: database, HTTP, file I/O
- Events: use @EventHandler(priority=X, ignoreCancelled=true)
- 20 TPS = 50ms budget per tick

## Shared Utilities
- BafFramework: ColorUtils, ItemStackBuilder, InventoryUtils, ExpUtils
- Paper API: Component, MiniMessage for text formatting (1.16+)
```

## Technical Implementation

### File Cache Update Skill

```markdown
# .claude/skills/cache-update/SKILL.md

## Purpose
Update file analysis cache based on git changes.

## Process
1. Run: git diff --name-only HEAD~1
2. For each changed .java file:
   a. Compute new SHA-256 hash
   b. If hash changed, re-analyze (extract class info, tags, summary)
   c. Update .claude/docs/codemap/cache/file-hashes.json
3. For each changed .yml file:
   a. Update config index entry
4. Report: "Updated cache for N files (M changed, K unchanged)"
```

### Memory Update Hook

Add to `.claude/settings.local.json`:

```json
{
  "hooks": {
    "PostToolUse": [
      {
        "matcher": "Edit|Write",
        "command": "echo 'Consider updating memory if a new pattern was discovered'"
      }
    ]
  }
}
```

## Dependencies

- **Phase 2** — Index provides the metadata cached per file
- **Phase 3** — Summaries are the primary cached analysis output

## Success Criteria

- [ ] File cache prevents re-analysis of unchanged files
- [ ] Persistent memory loaded at session start (MEMORY.md < 200 lines)
- [ ] Convention memory captures patterns after 3+ observations
- [ ] Cross-project memory shared between plugins
- [ ] Cache invalidation works correctly with git changes
- [ ] Session compaction preserves task-relevant context

---

## GitHub Issue

### Title
`feat: Phase 6 — Caching & Cross-Session Persistent Memory`

### Description

Implement a three-tier memory system: file cache (avoid re-reading), session memory (strategic compaction), and persistent memory (cross-session convention learning and cross-project knowledge).

### Background / Motivation

Without caching, every session starts from scratch — Claude re-reads the same files, rediscovers the same patterns, and makes the same initial mistakes. Persistent memory ensures that learnings from one session carry forward, and file caching ensures that unchanged files aren't re-analyzed.

### Tasks

- [ ] Implement file hash cache at `.claude/docs/codemap/cache/file-hashes.json`
- [ ] Create cache update skill (git diff → re-analyze changed files only)
- [ ] Structure persistent memory directory (`~/.claude/projects/{project}/memory/`)
- [ ] Create MEMORY.md template with project quick reference
- [ ] Create conventions.md for learned coding patterns
- [ ] Create patterns.md for design pattern catalog
- [ ] Create bug-patterns.md for common bugs and fixes
- [ ] Create cross-project.md for shared knowledge
- [ ] Implement convention learning logic (save after 3+ observations)
- [ ] Enhance strategic-compact skill with context preservation rules
- [ ] Test cache invalidation with simulated file changes
- [ ] Populate initial memory from existing project knowledge

### Acceptance Criteria

- [ ] Unchanged files not re-analyzed (cache hit rate > 80%)
- [ ] MEMORY.md fits in 200 lines and loads at session start
- [ ] Convention memory contains 10+ verified patterns
- [ ] Cross-project memory contains BafFramework and Bukkit patterns
- [ ] Cache update completes in < 10 seconds for typical commits

---
**Priority:** P2 | **Effort:** M | **Labels:** `feature`, `infrastructure`, `phase-6`, `effort:M`
