---
name: strategic-compact
description: Suggests manual context compaction at logical intervals to preserve context through task phases rather than arbitrary auto-compaction.
---

# Strategic Compact Skill

Suggests manual `/compact` at strategic points in your workflow rather than relying on arbitrary auto-compaction.

## Why Strategic Compaction?

Auto-compaction triggers at arbitrary points:
- Often mid-task, losing important context
- No awareness of logical task boundaries
- Can interrupt complex multi-step operations

Strategic compaction at logical boundaries:
- **After exploration, before execution** - Compact research context, keep implementation plan
- **After completing a milestone** - Fresh start for next phase
- **Before major context shifts** - Clear exploration context before different task

## How It Works

The `suggest-compact.sh` script runs on PreToolUse (Edit/Write) and:

1. **Tracks tool calls** - Counts tool invocations in session
2. **Threshold detection** - Suggests at configurable threshold (default: 50 calls)
3. **Periodic reminders** - Reminds every 25 calls after threshold

## Hook Setup

Add to your `~/.claude/settings.json`:

```json
{
  "hooks": {
    "PreToolUse": [{
      "matcher": "tool == \"Edit\" || tool == \"Write\"",
      "hooks": [{
        "type": "command",
        "command": "~/.claude/skills/strategic-compact/suggest-compact.sh"
      }]
    }]
  }
}
```

## Configuration

Environment variables:
- `COMPACT_THRESHOLD` - Tool calls before first suggestion (default: 50)

## Best Practices

1. **Compact after planning** - Once plan is finalized, compact to start fresh
2. **Compact after debugging** - Clear error-resolution context before continuing
3. **Don't compact mid-implementation** - Preserve context for related changes
4. **Read the suggestion** - The hook tells you *when*, you decide *if*

## Context Preservation Rules

When compacting (manually or via auto-compaction), preserve these in order of priority:

1. **Task State** — Current task type (question/bugfix/feature/refactor), scope (narrow/module/broad), and implementation phase
2. **Files Read** — List of summaries and source files already loaded, with one-line notes on findings
3. **Discovered Relationships** — Class dependencies, data flows, cross-module interactions found
4. **Decisions Made** — Architectural choices, rejected alternatives, rationale
5. **Test Results** — Passing/failing tests, error messages, relevant stack traces
6. **Cache State** — Which files are known-unchanged (from file-hashes.json check)
7. **Memory Pointers** — Which memory files (conventions.md, patterns.md, etc.) have been consulted

### Before Compacting Checklist

Save new findings to persistent memory before they are lost:
- New conventions discovered (3+ observations) → `conventions.md`
- Bug patterns found → `bug-patterns.md`
- Design decisions for this project → `MEMORY.md`
- Cross-project learnings → `cross-project.md`

## Related

- [The Longform Guide](https://x.com/affaanmustafa/status/2014040193557471352) - Token optimization section
- Persistent memory files: `~/.claude/projects/{project}/memory/`
