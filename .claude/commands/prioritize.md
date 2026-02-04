---
description: Analyze open issues, sort by priority, detect conflicts, and suggest optimal ordering
---

# Prioritize Command

Analyze GitHub issues to create a prioritized work plan with conflict detection.

## Usage

```
/prioritize [options]
```

**Options:**
- `--all` - Include closed issues in analysis
- `--conflicts` - Focus on conflict detection only
- `--deps` - Focus on dependency analysis only

## Instructions

Execute this prioritization workflow:

### Step 1: Fetch Issues

```bash
gh issue list --state open --json number,title,labels,body,createdAt --limit 100
```

### Step 2: Parse Priority and Effort

Extract from issue body (typically in footer):
- **Priority:** Look for `P0`, `P1`, `P2`, `P3` or `Priority: PX`
- **Effort:** Look for `S`, `M`, `L`, `XL` or `Effort: X`

Priority definitions:
| Priority | SLA | Description |
|----------|-----|-------------|
| P0 | Immediate | System down, security, data loss |
| P1 | 24 hours | Major feature broken |
| P2 | 1 week | Feature degraded, workaround exists |
| P3 | Backlog | Nice-to-have, cosmetic |

Effort definitions:
| Effort | Time | Scope |
|--------|------|-------|
| S | <2h | 1-2 files |
| M | 2-8h | 3-5 files |
| L | 1-3 days | Multiple components |
| XL | 1+ weeks | Architectural |

### Step 3: Detect Conflicts

Scan issue bodies for:
1. **File references:** `src/`, `.tsx`, `.ts`, `components/`, `routers/`
2. **Component mentions:** Component names, module names
3. **Related issues:** `#XX`, `Relates to`, `Blocks`, `Blocked by`

Build conflict map:
```
Issue #35 -> files: [src/server/api/routers/note.ts, prisma/schema.prisma]
Issue #36 -> files: [src/server/api/routers/note.ts, src/server/api/routers/insight.ts]
CONFLICT: #35 and #36 both modify src/server/api/routers/note.ts
```

### Step 4: Identify Dependencies

Look for dependency keywords:
- "Blocks #XX" or "blocks: #XX"
- "Blocked by #XX" or "depends on #XX"
- "After #XX" or "requires #XX"
- "Related: #XX"

Build dependency graph:
```
#42 (DB migration) -> blocks -> #35 (pgvector)
#35 (pgvector) -> blocks -> #36 (evolution timeline)
```

### Step 5: Generate Report

Output format:

```
PRIORITIZED ISSUES
==================

P0 - IMMEDIATE (Security/Critical)
----------------------------------
  (none)

P1 - HIGH (24h SLA)
-------------------
  #XX - Title [Effort]
       Files: file1.ts, file2.ts

P2 - MEDIUM (1 week)
--------------------
  #XX - Title [Effort]
       Files: file1.ts
       Blocked by: #YY

P3 - BACKLOG
------------
  #XX - Title [Effort]

UNPRIORITIZED (needs triage)
----------------------------
  #XX - Title
       (no priority label found)

CONFLICTS DETECTED
==================
  #XX and #YY both modify:
    - src/server/api/routers/note.ts

  #AA and #BB both modify:
    - src/components/layout/ContextNavigator.tsx

DEPENDENCY CHAIN
================
  #42 -> #35 -> #36
  (Complete #42 first, then #35, then #36)

SUGGESTED ORDER
===============
Based on priority, effort, conflicts, and dependencies:

1. #XX - [P1, S] Title (no blockers, quick win)
2. #YY - [P1, M] Title (unblocks #ZZ)
3. #ZZ - [P2, L] Title (after #YY)
...

SUMMARY
=======
Total open issues: X
  P0: X | P1: X | P2: X | P3: X | Unprioritzed: X
Conflicts found: X
Dependency chains: X
```

## Conflict Detection Rules

**HIGH conflict risk:**
- Same router file (note.ts, insight.ts, etc.)
- Same component file
- Database schema changes
- Store files (Zustand)

**MEDIUM conflict risk:**
- Same directory/feature area
- Related API endpoints
- Shared utilities

**LOW conflict risk:**
- Different feature areas
- Independent components
- Documentation only

## Suggested Order Algorithm

1. **P0 issues first** - Always top priority
2. **Unblockers** - Issues that unblock others
3. **Quick wins** - Low effort (S) items clear backlog
4. **No conflicts** - Independent issues can parallel
5. **Conflict groups** - Do sequentially to avoid merge pain

## Example Output

```
PRIORITIZED ISSUES
==================

P1 - HIGH (24h SLA)
-------------------
  #38 - Add daily/weekly reflection summaries [L]
       Files: src/server/api/routers/insight.ts

P2 - MEDIUM (1 week)
--------------------
  #35 - Enable pgvector for semantic search [M]
       Files: src/server/api/routers/note.ts, prisma/schema.prisma

  #36 - Add thought evolution timeline UI [M]
       Files: src/server/api/routers/note.ts, src/server/api/routers/insight.ts
       Blocked by: #35 (needs semantic search)

  #37 - Add English Practice Support [M]
       Files: src/server/api/routers/insight.ts

  #61 - Codebase audit and clean code [XL]
       Files: Multiple (ContextNavigator.tsx, ThinkingSpace.tsx, note.ts, insight.ts)

P3 - BACKLOG
------------
  #60 - LiveKit voice capabilities [XL]
       Files: src/components/capture/, new files

  #67 - Fix Space dropdown overflow [S]
       Files: src/components/explore/NoteViewer.tsx

CONFLICTS DETECTED
==================
  HIGH: #35, #36, #61 all modify src/server/api/routers/note.ts
  HIGH: #36, #37, #38, #61 all modify src/server/api/routers/insight.ts
  MEDIUM: #61 touches files from #35, #36, #37, #38

DEPENDENCY CHAIN
================
  #35 -> #36 (pgvector enables evolution search)
  #61 should be LAST (refactor after features)

SUGGESTED ORDER
===============
1. #67 [P2, S] - Quick fix, no conflicts
2. #35 [P2, M] - Enables #36, database foundation
3. #38 [P1, L] - High priority, insight router
4. #36 [P2, M] - After #35
5. #37 [P2, M] - Can parallel with #36
6. #61 [P3, XL] - LAST: refactor after all features
7. #60 [P3, XL] - Independent, can start anytime

SUMMARY
=======
Total open issues: 7
  P0: 0 | P1: 1 | P2: 4 | P3: 2
Conflicts found: 2 HIGH, 1 MEDIUM
Dependency chains: 1
```

## Integration

After running `/prioritize`:
- Use `/plan` on the first suggested issue
- Use git worktrees to work on non-conflicting issues in parallel
- Re-run `/prioritize` after completing issues to update order

## Related Commands

- `/plan` - Plan implementation for top priority issue
- `/orchestrate` - Run multi-agent workflow on issue
- `/verify` - Verify current state before starting new issue
