# Refactor Clean

Safely identify and remove dead code with test verification:

1. Run dead code analysis:
   - Search for unused imports across all modules
   - Find unused private methods and fields
   - Identify empty classes or interfaces
   - Find duplicate code patterns

2. Generate report of findings

3. Categorize findings by severity:
   - SAFE: Unused private methods, dead imports
   - CAUTION: Package-private classes, utility methods
   - DANGER: Public APIs, config classes, module registrations

4. Propose safe deletions only

5. Before each deletion:
   - Run full test suite: `./gradlew test`
   - Verify tests pass
   - Apply change
   - Re-run tests
   - Rollback if tests fail

6. Show summary of cleaned items

Never delete code without running tests first!
