---
description: Enforce test-driven development workflow. Scaffold interfaces, generate tests FIRST, then implement minimal code to pass. Ensure 80%+ coverage.
---

# TDD Command

This command invokes the **tdd-guide** agent to enforce test-driven development methodology for Java/Bukkit.

## What This Command Does

1. **Scaffold Interfaces** - Define data classes and interfaces first
2. **Generate Tests First** - Write failing JUnit 5 tests (RED)
3. **Implement Minimal Code** - Write just enough to pass (GREEN)
4. **Refactor** - Improve code while keeping tests green (REFACTOR)
5. **Verify Coverage** - Ensure 80%+ test coverage

## When to Use

Use `/tdd` when:
- Implementing new features or modules
- Adding new classes or methods
- Fixing bugs (write test that reproduces bug first)
- Refactoring existing code
- Building core business logic

## How It Works

The tdd-guide agent will:

1. **Define data classes** for inputs/outputs
2. **Write tests that will FAIL** (because code doesn't exist yet)
3. **Run tests** and verify they fail for the right reason
4. **Write minimal implementation** to make tests pass
5. **Run tests** and verify they pass
6. **Refactor** code while keeping tests green
7. **Check coverage** and add more tests if below 80%

## TDD Cycle

```
RED -> GREEN -> REFACTOR -> REPEAT

RED:      Write a failing test
GREEN:    Write minimal code to pass
REFACTOR: Improve code, keep tests passing
REPEAT:   Next feature/scenario
```

## Example Usage

```
User: /tdd I need a manager to track player cooldowns

Agent (tdd-guide):
# TDD Session: Player Cooldown Manager

## Step 1: Define Data Class (SCAFFOLD)

```java
@Getter @Setter
public class CooldownData {
    private String playerName;
    private String action;
    private long expireTime;
}
```

## Step 2: Write Failing Test (RED)

```java
class CooldownManagerTest {
    @Test
    void shouldReturnFalseWhenNoCooldown() {
        CooldownManager manager = new CooldownManager();
        assertFalse(manager.hasCooldown("player1", "attack"));
    }

    @Test
    void shouldReturnTrueWhenCooldownActive() {
        CooldownManager manager = new CooldownManager();
        manager.setCooldown("player1", "attack", 5000);
        assertTrue(manager.hasCooldown("player1", "attack"));
    }

    @Test
    void shouldReturnFalseWhenCooldownExpired() {
        CooldownManager manager = new CooldownManager();
        manager.setCooldown("player1", "attack", -1000); // Already expired
        assertFalse(manager.hasCooldown("player1", "attack"));
    }
}
```

## Step 3: Run Tests - Verify FAIL

```bash
./gradlew :{YourModule}:test --tests "*.CooldownManagerTest"
# FAIL - CooldownManager class doesn't exist yet
```

## Step 4: Implement Minimal Code (GREEN)
...
```

## TDD Best Practices

**DO:**
- Write the test FIRST, before any implementation
- Run tests and verify they FAIL before implementing
- Write minimal code to make tests pass
- Refactor only after tests are green
- Add edge cases and error scenarios
- Aim for 80%+ coverage (100% for critical code)

**DON'T:**
- Write implementation before tests
- Skip running tests after each change
- Write too much code at once
- Ignore failing tests
- Test implementation details (test behavior)
- Mock everything (prefer MockBukkit for Bukkit API)

## Test Framework

- **JUnit 5** (Jupiter) - Test framework
- **MockBukkit 4.x** - Bukkit API testing (`org.mockbukkit.mockbukkit`)
- **Mockito 5.x** - Mocking for non-Bukkit classes only
- **JaCoCo** - Code coverage

## Test Commands

```bash
# Run all tests
./gradlew test

# Run specific module
./gradlew :{YourModule}:test

# Run single test class
./gradlew :{YourModule}:test --tests "*.CooldownManagerTest"

# Run with coverage
./gradlew test jacocoTestReport
```

## Coverage Requirements

- **80% minimum** for all code
- **100% required** for:
  - Condition hooks (check logic)
  - Execute hooks (execution logic)
  - Data serialization/deserialization
  - Core business logic

## Integration with Other Commands

- Use `/plan` first to understand what to build
- Use `/tdd` to implement with tests
- Use `/gradle-build` if build errors occur
- Use `/code-review` to review implementation
- Use `/gradle-test` to verify coverage
