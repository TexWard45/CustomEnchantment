# Token Conservation

- Read summaries before full files when available (`.claude/docs/codemap/summaries/`)
- Use subagents for verbose operations (test running, log analysis, codebase exploration)
- Check if information is already in loaded context before reading new files
- For questions: often summaries and index files are sufficient — don't read full source files
- Prefer targeted file reads over broad exploration
- Use Haiku model for subagent tasks where appropriate
