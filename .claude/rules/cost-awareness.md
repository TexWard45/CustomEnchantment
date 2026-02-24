# Token Conservation

## Reading Files (MANDATORY Pre-Read Checklist)

**Before reading ANY Java source file, complete this checklist:**

1. [ ] **Check loaded context** — Is the information already available from summaries or files read this session?
2. [ ] **Check module summary** — Does the module's `.md` summary in `.claude/docs/codemap/summaries/` cover this?
3. [ ] **Call MCP `get_file_summary`** — Does the cached summary, methods list, or tags answer the question?
4. [ ] **Only then** use the `Read` tool on the source file

**Stopping Criteria — Do NOT read source files when:**
- The task is a **question** about structure, purpose, or relationships → summaries are sufficient
- You only need to know **what methods a class has** → use `get_file_summary`
- You only need to know **what module a class belongs to** → use `get_file_summary` or module summary
- The file was **already read** in this session and hasn't been modified
- You are reading "just in case" — only read files you have a specific reason to inspect

**For unknown-module tasks**, invoke `/context-selector` before loading summaries.

## Subagent Delegation

- Delegate verbose operations to subagents (test running, build output, log analysis)
- Subagents isolate large outputs from the main context window
- **ALWAYS set `model: "haiku"`** for subagent Task calls unless code reasoning is required (see `agents.md`)

## Context Hygiene

- Use `/clear` between unrelated tasks to reset context
- Avoid reading files unrelated to the current task
- Prefer targeted grep/glob over broad exploration in the main context
- Never re-read files already loaded in the current session

## Efficient Searches

- Use Glob for known file patterns instead of exploring directories
- Use Grep with specific patterns instead of reading entire files
- Use the Explore agent (with `model: "haiku"`) for broad codebase searches (isolates results from main context)
- Use MCP `search_code` / `search_enchantments` / `search_configs` for semantic discovery
