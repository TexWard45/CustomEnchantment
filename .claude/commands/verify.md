# Verification Command

Run comprehensive verification on current codebase state.

## Instructions

Execute verification in this exact order:

1. **Build Check**
   - Run `./gradlew build`
   - If it fails, report errors and STOP

2. **Compilation Check**
   - Run `./gradlew compileJava`
   - Report all errors with file:line

3. **Test Suite**
   - Run `./gradlew test`
   - Report pass/fail count per module
   - Report coverage percentage if JaCoCo configured

4. **Anti-Pattern Audit**
   - Search for `printStackTrace()` in source files
   - Search for `System.out.println()` in source files
   - Report locations

5. **Git Status**
   - Show uncommitted changes
   - Show files modified since last commit

## Output

Produce a concise verification report:

```
VERIFICATION: [PASS/FAIL]

Build:       [OK/FAIL]
Compile:     [OK/X errors]
Tests:       [X/Y passed, Z% coverage]
Secrets:     [OK/X found]
Anti-pattern:[OK/X issues]

Ready for PR: [YES/NO]
```

If any critical issues, list them with fix suggestions.

## Arguments

$ARGUMENTS can be:
- `quick` - Only build + compile
- `full` - All checks (default)
- `pre-commit` - Checks relevant for commits
- `pre-pr` - Full checks plus security scan
