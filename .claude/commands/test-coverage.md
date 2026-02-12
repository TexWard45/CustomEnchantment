# Test Coverage

Analyze test coverage and generate missing tests:

1. Run tests with coverage: `./gradlew test jacocoTestReport`

2. Analyze coverage report (build/reports/jacoco/test/html/index.html)

3. Identify classes below 80% coverage threshold

4. For each under-covered class:
   - Analyze untested code paths
   - Generate unit tests using JUnit 5 + MockBukkit
   - Focus on business logic and edge cases

5. Verify new tests pass: `./gradlew test`

6. Show before/after coverage metrics

7. Ensure project reaches 80%+ overall coverage

Focus on:
- Happy path scenarios
- Error handling (try-catch branches)
- Edge cases (null, empty collections, boundary values)
- Config defaults and field values
- Condition/Execute hook logic
