# Verification Loop Skill

A comprehensive verification system for Claude Code sessions.

## When to Use

Invoke this skill:
- After completing a feature or significant code change
- Before creating a PR
- When you want to ensure quality gates pass
- After refactoring

## Verification Phases

### Phase 1: Build Verification
```bash
# Check if project builds
./gradlew build 2>&1 | tail -20
```

If build fails, STOP and fix before continuing.

### Phase 2: Test Suite
```bash
# Run all tests
./gradlew test 2>&1 | tail -50

# Run specific module tests
./gradlew :Bukkit:test
./gradlew :Plugin-BafFramework:test
```

Report:
- Total tests: X
- Passed: X
- Failed: X
- Coverage: X%

### Phase 3: Coverage Check
```bash
# Generate JaCoCo coverage report
./gradlew jacocoTestReport

# Coverage report at:
# {module}/build/reports/jacoco/test/html/index.html
# Target: 80% minimum
```

### Phase 4: Security Scan
```bash
# Check for hardcoded secrets in Java files
grep -rn "password\|api_key\|secret\|token" --include="*.java" . 2>/dev/null | grep -v "= \"\"" | head -10

# Check for printStackTrace usage
grep -rn "printStackTrace" --include="*.java" . 2>/dev/null | head -10

# Check for System.out usage
grep -rn "System\.out\." --include="*.java" . 2>/dev/null | head -10
```

### Phase 5: Diff Review
```bash
# Show what changed
git diff --stat
git diff HEAD~1 --name-only
```

Review each changed file for:
- Unintended changes
- Missing error handling
- Potential edge cases

## Output Format

After running all phases, produce a verification report:

```
VERIFICATION REPORT
==================

Build:     [PASS/FAIL]
Tests:     [PASS/FAIL] (X/Y passed, Z% coverage)
Security:  [PASS/FAIL] (X issues)
Diff:      [X files changed]

Overall:   [READY/NOT READY] for PR

Issues to Fix:
1. ...
2. ...
```

## Continuous Mode

For long sessions, run verification every 15 minutes or after major changes:

```markdown
Set a mental checkpoint:
- After completing each class
- After finishing a module
- Before moving to next task

Run: /verify
```

## Integration with Hooks

This skill complements PostToolUse hooks but provides deeper verification.
Hooks catch issues immediately; this skill provides comprehensive review.
