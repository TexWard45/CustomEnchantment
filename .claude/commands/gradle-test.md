---
description: Run tests for specific module with coverage report. Use when you want to run tests and see coverage metrics.
---

# Gradle Test

Run tests for the project or a specific module with JaCoCo coverage analysis.

## What This Command Does

1. **Run tests** - Execute JUnit 5 tests via Gradle
2. **Show results** - Test pass/fail summary per module
3. **Coverage report** - JaCoCo coverage metrics if configured
4. **Failure analysis** - Detail failing tests with stack traces

## Usage

```
/gradle-test                        # Run all tests
/gradle-test {YourModule}           # Run specific module tests
/gradle-test {ApiModule}            # Run API module tests
```

## Test Commands

```bash
# All modules
./gradlew test

# Specific module
./gradlew :{ApiModule}:test
./gradlew :{YourModule}:test

# Single test class
./gradlew :{YourModule}:test --tests "{your.package}.feature.MyFeatureTest"

# Tests with JaCoCo coverage
./gradlew test jacocoTestReport
```

## Workflow

1. Run tests for specified scope
2. Parse test results from Gradle output
3. If tests pass:
   - Show pass count per module
   - Show coverage summary if JaCoCo enabled
4. If tests fail:
   - Show failing test names
   - Show assertion errors or stack traces
   - Suggest fixes based on error patterns

## Output Format

```
TEST RESULTS
============
Module                  Tests   Passed  Failed  Skipped
{ApiModule}             120     120     0       0
{YourModule}            200     200     0       0

Total: 320 tests, 320 passed, 0 failed

COVERAGE (if available)
=======================
Module                  Line%   Branch%
{YourModule}            85%     72%
```

## Test Framework

- **JUnit 5** (Jupiter) - Test framework
- **MockBukkit 4.x** - Bukkit API mocking (`org.mockbukkit.mockbukkit`)
- **Mockito 5.x** - Mocking for non-Bukkit classes
- **JaCoCo** - Code coverage

## Related Commands

- `/gradle-build` - Full build including tests
- `/tdd` - Test-driven development workflow
- `/test-coverage` - Detailed coverage analysis
