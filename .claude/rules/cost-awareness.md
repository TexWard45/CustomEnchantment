# Token Conservation

## Reading Files

- Read summaries before full files when available
- Check if information is already in loaded context before reading new files
- For questions: summaries are often sufficient — don't read full source files
- Avoid re-reading files already in context
- For unknown-module tasks, invoke `/context-selector` before loading summaries

## Subagent Delegation

- Delegate verbose operations to subagents (test running, build output, log analysis)
- Subagents isolate large outputs from the main context window
- Use haiku model for subagents by default (see `claude-model-selection.md`)

## Context Hygiene

- Use `/clear` between unrelated tasks to reset context
- Avoid reading files unrelated to the current task
- Prefer targeted grep/glob over broad exploration in the main context

## Efficient Searches

- Use Glob for known file patterns instead of exploring directories
- Use Grep with specific patterns instead of reading entire files
- Use the Explore agent for broad codebase searches (isolates results from main context)
