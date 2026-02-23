# Update Codemaps

Regenerate architecture documentation in `.claude/docs/codemap/` by analyzing the codebase.

## Process

Follow the codemap-generator skill at `.claude/skills/codemap-generator/SKILL.md`:

1. **Scan project structure** — Glob for .java files, .yml configs, build.gradle modules
2. **Find main plugin class** — Grep for `extends BafPlugin` or `extends JavaPlugin`
3. **Extract module registration order** — Read registerModules() method
4. **Extract all class declarations** — Grep for class/interface/enum declarations with extends/implements
5. **Detect design patterns** — Grep for Singleton, Registry, Hook, Module, Listener, Builder patterns
6. **Map config schemas** — Cross-reference YAML files with @Configuration classes
7. **Scan entry points** — Find @EventHandler, commands, and scheduled tasks
8. **Classify domains** — Tag classes by package and naming patterns

## Output Files

Generate/update all files in `.claude/docs/codemap/`:

| File | Content |
|------|---------|
| MODULE-MAP.md | All modules with purpose, key classes, load order, dependencies |
| CLASS-REGISTRY.md | Every class with type, extends/implements, one-line summary |
| DEPENDENCY-GRAPH.md | Module→module and class→class dependency relationships |
| PATTERN-CATALOG.md | Detected design patterns with implementing classes |
| CONFIG-SCHEMA-MAP.md | YAML files → Java config class mappings |
| DOMAIN-MAP.md | Classes grouped by functional domain |
| ENTRY-POINTS.md | Event listeners, commands, scheduled tasks |

## Constraints

- Total output MUST be under 2000 lines
- Use concise one-line summaries
- Use Glob/Grep/Read tools only — no external tools
- Add freshness timestamp at the top of each file

## Validation

After generation, verify:
- MODULE-MAP.md lists all modules from registerModules()
- CLASS-REGISTRY.md covers 95%+ of public classes
- PATTERN-CATALOG.md identifies 10+ patterns
- Total line count < 2000
