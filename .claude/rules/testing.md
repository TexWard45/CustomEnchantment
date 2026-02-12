# Testing Requirements

## Stack

- **JUnit 5** (Jupiter) for all tests
- **MockBukkit 4.x** (`org.mockbukkit.mockbukkit`) for Bukkit API
- **Mockito** for non-Bukkit abstractions only
- **JaCoCo** for coverage reports
- Run tests: `./gradlew test`
- Run single module: `./gradlew :{YourModule}:test`

## Test-Driven Development

MANDATORY workflow:
1. Write test first (RED)
2. Run test - it should FAIL
3. Write minimal implementation (GREEN)
4. Run test - it should PASS
5. Refactor (IMPROVE)
6. Verify coverage

## Bug Fix Workflow (MANDATORY)

**NEVER fix a bug without first writing a test that fails because of that bug.**

1. Reproduce the bug
2. Write a failing test
3. Verify test fails
4. Fix the bug
5. Verify test passes

## MockBukkit over Mockito

Always prefer MockBukkit for Bukkit classes. See `bukkit-testing.md` for details.

## Troubleshooting Test Failures

1. Check test isolation (MockBukkit.isMocked() guards)
2. Verify mocks are correct
3. Fix implementation, not tests (unless tests are wrong)
