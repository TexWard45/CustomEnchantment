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
1. **File references:** `src/main/java/`, `.java`, `Plugin-*/`, `Bukkit/`
2. **Module mentions:** Module names, class names, package names
3. **Related issues:** `#XX`, `Relates to`, `Blocks`, `Blocked by`

Build conflict map:
```
Issue #35 -> files: [Plugin-BafFramework/.../manager/AttributeManager.java]
Issue #36 -> files: [Plugin-BafFramework/.../manager/AttributeManager.java, Bukkit/.../utils/ItemStackUtils.java]
CONFLICT: #35 and #36 both modify AttributeManager.java
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
- Same Manager or Module class
- Same Listener or Command class
- Database schema/migration changes
- Config class modifications

**MEDIUM conflict risk:**
- Same package/feature area
- Related utility classes
- Shared base classes (Abstract*)

**LOW conflict risk:**
- Different modules entirely
- Independent features
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
  #12 - Fix NPE in PlayerManager on async disconnect [S]
       Files: Plugin-BafFramework/.../manager/PlayerManager.java

P2 - MEDIUM (1 week)
--------------------
  #8 - Add custom enchant registration system [M]
       Files: Plugin-BafFramework/.../enchant/AbstractEnchant.java (new)

  #9 - Improve menu item caching performance [M]
       Files: Plugin-CustomMenu/.../manager/MenuManager.java, Bukkit/.../item/ItemStackBuilder.java
       Blocked by: #8 (needs strategy pattern update)

  #10 - Add Redis cluster support [L]
       Files: Plugin-MultiServer/.../config/MainConfig.java, Plugin-MultiServer/.../manager/RedisManager.java

P3 - BACKLOG
------------
  #5 - Clean up irrelevant .claude/rules [S]
       Files: .claude/rules/, .claude/agents/

  #11 - Add NMS 1.21.2 support [XL]
       Files: Plugin-BafFramework-1.21.2/ (new module)

CONFLICTS DETECTED
==================
  MEDIUM: #8 and #9 both modify strategy pattern classes
  LOW: #10 modifies MultiServer config independently

DEPENDENCY CHAIN
================
  #8 -> #9 (enchant registration enables caching refactor)

SUGGESTED ORDER
===============
1. #12 [P1, S] - Quick fix, critical bug
2. #5 [P3, S] - Quick cleanup, no conflicts
3. #8 [P2, M] - Enables #9
4. #9 [P2, M] - After #8
5. #10 [P2, L] - Independent, can parallel with #8/#9
6. #11 [P3, XL] - Large, independent

SUMMARY
=======
Total open issues: 6
  P0: 0 | P1: 1 | P2: 3 | P3: 2
Conflicts found: 1 MEDIUM, 1 LOW
Dependency chains: 1
```

## Integration

After running `/prioritize`:
- Use `/plan` on the first suggested issue
- Use git worktrees to work on non-conflicting issues in parallel
- Re-run `/prioritize` after completing issues to update order

## Related Commands

- `/plan` - Plan implementation for top priority issue
- `/verify` - Verify current state before starting new issue
- `/tdd` - Implement with test-driven development
