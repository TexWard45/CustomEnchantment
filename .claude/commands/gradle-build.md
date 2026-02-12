---
description: Run Gradle build with error analysis. Use when you want to build the project and automatically resolve any errors.
---

# Gradle Build

Run the full Gradle build pipeline and handle errors automatically.

## What This Command Does

1. **Run full build** - `./gradlew build`
2. **Analyze output** - Parse compilation errors, test failures, dependency issues
3. **Auto-fix if needed** - Invoke build-error-resolver agent for failures
4. **Report results** - Summary of build status per module

## Build Targets

```bash
# Full project build
./gradlew build

# Specific module
./gradlew :{ApiModule}:build
./gradlew :{YourModule}:build

# Compile only (skip tests)
./gradlew compileJava

# Clean build
./gradlew clean build
```

## Usage

```
/gradle-build                    # Full build
/gradle-build {YourModule}       # Build specific module
/gradle-build clean              # Clean build
```

## Workflow

1. Run the build command
2. If build succeeds:
   - Show success summary with module status
   - Report test results (passed/failed/skipped)
3. If build fails:
   - Parse error messages
   - Group by module and error type
   - Use **build-error-resolver** agent to fix errors
   - Re-run build to verify

## Output Format

```
BUILD RESULT
============
Module                  Status    Tests
{ApiModule}             PASS      120 passed
{YourModule}            PASS      200 passed

Total: 320 tests, 0 failures
Build: SUCCESS
```

## Related Commands

- `/build-fix` - Focus on fixing build errors only
- `/gradle-test` - Run tests with coverage
