# Testing Requirements

## Minimum Test Coverage: 80%

Test Types (ALL required):
1. **Unit Tests** - Individual functions, utilities, components
2. **Integration Tests** - API endpoints, database operations
3. **E2E Tests** - Critical user flows (Playwright)

## Test-Driven Development

MANDATORY workflow:
1. Write test first (RED)
2. Run test - it should FAIL
3. Write minimal implementation (GREEN)
4. Run test - it should PASS
5. Refactor (IMPROVE)
6. Verify coverage (80%+)

## Bug Fix Workflow (MANDATORY)

**NEVER fix a bug without first writing a test that fails because of that bug.**

See [Bug Fix Workflow Guide](../../docs/BUG_FIX_WORKFLOW.md) for the complete 5-step process:
1. Reproduce the bug
2. Write a failing test
3. Verify test fails
4. Fix the bug
5. Verify test passes

This prevents recurring bugs. Real example: "Prompts Used" bug occurred 3 times (#12, #60, #65) because #60 wasn't fixed with a test.

## Troubleshooting Test Failures

1. Use **tdd-guide** agent
2. Check test isolation
3. Verify mocks are correct
4. Fix implementation, not tests (unless tests are wrong)

## Agent Support

- **tdd-guide** - Use PROACTIVELY for new features, enforces write-tests-first
- **e2e-runner** - Playwright E2E testing specialist
