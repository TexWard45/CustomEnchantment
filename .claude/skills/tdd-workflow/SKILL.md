---
name: tdd-workflow
description: Use this skill when writing new features, fixing bugs, or refactoring code. Enforces test-driven development with 80%+ coverage using JUnit 5 and MockBukkit.
---

# Test-Driven Development Workflow

This skill ensures all code development follows TDD principles with comprehensive test coverage.

## When to Activate

- Writing new features or modules
- Fixing bugs or issues
- Refactoring existing code
- Adding new conditions/executes/hooks
- Creating new managers or utilities

## Core Principles

### 1. Tests BEFORE Code
ALWAYS write tests first, then implement code to make tests pass.

### 2. Coverage Requirements
- Minimum 80% coverage (unit + integration)
- All edge cases covered
- Error scenarios tested
- Boundary conditions verified

### 3. Test Types

#### Unit Tests
- Individual methods and utilities
- Data class behavior
- Config default values
- Condition/Execute hook logic

#### Integration Tests
- Module lifecycle (onEnable/onReload/onDisable)
- Listener event handling with MockBukkit
- Player interactions
- Command execution

## TDD Workflow Steps

### Step 1: Define the Feature
```
Feature: Player cooldown system
- Track per-player cooldowns for actions
- Check if cooldown is active
- Auto-expire after duration
```

### Step 2: Generate Test Cases

```java
@DisplayName("CooldownManager Tests")
class CooldownManagerTest {

    @Test
    @DisplayName("Should return false when no cooldown exists")
    void shouldReturnFalseWhenNoCooldown() {
        CooldownManager manager = new CooldownManager();
        assertFalse(manager.hasCooldown("player1", "attack"));
    }

    @Test
    @DisplayName("Should return true when cooldown is active")
    void shouldReturnTrueWhenCooldownActive() {
        CooldownManager manager = new CooldownManager();
        manager.setCooldown("player1", "attack", 5000);
        assertTrue(manager.hasCooldown("player1", "attack"));
    }

    @Test
    @DisplayName("Should return false when cooldown expired")
    void shouldReturnFalseWhenExpired() {
        CooldownManager manager = new CooldownManager();
        manager.setCooldown("player1", "attack", -1000);
        assertFalse(manager.hasCooldown("player1", "attack"));
    }
}
```

### Step 3: Run Tests (They Should Fail)
```bash
./gradlew test --tests "*.CooldownManagerTest"
# Tests should fail - class doesn't exist yet
```

### Step 4: Implement Code
Write minimal code to make tests pass.

### Step 5: Run Tests Again
```bash
./gradlew test --tests "*.CooldownManagerTest"
# Tests should now pass
```

### Step 6: Refactor
Improve code quality while keeping tests green.

### Step 7: Verify Coverage
```bash
./gradlew test jacocoTestReport
# Verify 80%+ coverage
```

## Testing Patterns

### MockBukkit Setup
```java
class MyTest {
    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        if (MockBukkit.isMocked()) MockBukkit.unmock();
        server = MockBukkit.mock();
    }

    @AfterAll
    static void tearDownAll() {
        if (MockBukkit.isMocked()) MockBukkit.unmock();
    }
}
```

### Testing Data Classes
```java
@Test
void shouldBuildWithAllFields() {
    MyData data = MyData.builder()
        .name("test")
        .value(42)
        .build();
    assertEquals("test", data.getName());
    assertEquals(42, data.getValue());
}
```

### Testing Config Defaults
```java
@Test
void shouldHaveDefaultValues() {
    MyConfig config = new MyConfig();
    assertTrue(config.isEnabled());
    assertEquals(10, config.getMaxCount());
}
```

### Testing Conditions
```java
@Test
void shouldMatchWhenConditionMet() {
    MyCondition condition = new MyCondition();
    ConditionData data = new ConditionData();
    data.put("level", "10");
    assertTrue(condition.check(data, "5")); // level >= required
}
```

## Common Testing Mistakes

### Wrong: Mocking Bukkit classes
```java
Player player = mock(Player.class); // Use MockBukkit instead
```

### Right: Using MockBukkit
```java
Player player = server.addPlayer(); // Real PlayerMock
```

### Wrong: No test isolation
```java
// Tests depend on shared mutable state
static CooldownManager manager = new CooldownManager();
```

### Right: Fresh state per test
```java
@BeforeEach
void setUp() {
    manager = new CooldownManager(); // Fresh instance
}
```

## Test Commands

```bash
# All tests
./gradlew test

# Specific module
./gradlew :YourModule:test

# Single test class
./gradlew test --tests "*.CooldownManagerTest"

# With coverage
./gradlew test jacocoTestReport
```

## Best Practices

1. **Write Tests First** - Always TDD
2. **One Assert Per Test** - Focus on single behavior
3. **Descriptive Test Names** - Use @DisplayName
4. **Arrange-Act-Assert** - Clear test structure
5. **Use MockBukkit** - Not Mockito for Bukkit classes
6. **Test Edge Cases** - Null, empty, boundary values
7. **Test Error Paths** - Not just happy paths
8. **Keep Tests Fast** - Unit tests < 50ms each
9. **Clean Up** - @BeforeEach / @AfterEach
10. **Review Coverage** - Identify gaps
