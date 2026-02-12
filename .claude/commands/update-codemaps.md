# Update Codemaps

Analyze the codebase structure and update architecture documentation:

1. Scan all Java source files for imports, class hierarchies, and dependencies
2. Generate token-lean codemaps in the following format:
   - codemaps/architecture.md - Overall architecture
   - codemaps/bukkit-core.md - Bukkit module (core API)
   - codemaps/bafframework.md - Plugin-BafFramework
   - codemaps/plugins.md - Child plugins (CustomMenu, MultiServer, UserDatabase)

3. Calculate diff percentage from previous version
4. If changes > 30%, request user approval before updating
5. Add freshness timestamp to each codemap
6. Save reports to .reports/codemap-diff.txt

Use Glob/Grep/Read tools for analysis. Focus on high-level structure, not implementation details.
