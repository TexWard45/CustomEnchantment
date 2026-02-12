# Bukkit Plugin Testing Guidelines

## Prioritize MockBukkit Over Mockito

When writing unit tests for Bukkit plugins, **always prefer MockBukkit** over Mockito mocks for Bukkit API classes.

### Why MockBukkit?

1. **Real implementations** - MockBukkit provides actual working implementations of Bukkit classes
2. **Proper initialization** - Bukkit server is properly initialized, enabling all API features
3. **Less boilerplate** - No need to mock every method chain
4. **Better test coverage** - Tests actual behavior, not mocked assumptions
5. **Deprecated API support** - Properly handles deprecated constructors (e.g., AttributeModifier)

### MockBukkit 4.x Setup Pattern

```java
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

@DisplayName("My Tests")
class MyTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
        server = MockBukkit.mock();
    }

    @AfterAll
    static void tearDownAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
    }
}
```

### Package Names (MockBukkit 4.x)

```java
// Correct imports for MockBukkit 4.x
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

// NOT the old package names
// import be.seeseemelk.mockbukkit.MockBukkit;  // WRONG - old version
```

### When to Use MockBukkit

| Use Case | Approach |
|----------|----------|
| Player interactions | `server.addPlayer()` returns real PlayerMock |
| ItemStack creation | `new ItemStack(Material.DIAMOND)` - real objects |
| Inventory operations | Use MockBukkit inventory implementations |
| Attribute modifiers | MockBukkit handles deprecated constructors |
| World/Location | `server.addSimpleWorld("world")` |
| Plugin loading | `MockBukkit.load(MyPlugin.class)` |

### When Mockito is Still Acceptable

Use Mockito **only** when:
1. Testing non-Bukkit classes (your own abstractions)
2. Mocking external plugin APIs not supported by MockBukkit
3. Specific verification of method calls is required
4. Testing edge cases that MockBukkit cannot simulate

### Examples

#### Good: Using MockBukkit for Player

```java
@Test
void shouldHandlePlayerJoin() {
    Player player = server.addPlayer();  // Real PlayerMock

    myPlugin.onPlayerJoin(player);

    assertEquals("Welcome!", player.nextMessage());
}
```

#### Good: Using Real ItemStack

```java
@Test
void shouldCreateDiamondItem() {
    ItemStack item = new ItemStack(Material.DIAMOND, 64);

    assertEquals(Material.DIAMOND, item.getType());
    assertEquals(64, item.getAmount());
}
```

#### Bad: Mocking Bukkit Classes

```java
// AVOID THIS - Use MockBukkit instead
@Test
void shouldHandlePlayer() {
    Player player = mock(Player.class);
    when(player.getName()).thenReturn("TestPlayer");
    when(player.getUniqueId()).thenReturn(UUID.randomUUID());
    // ... endless mocking
}
```

### Test Isolation

Always check MockBukkit state to prevent "MockBukkit already started" errors:

```java
@BeforeAll
static void setUpAll() {
    if (MockBukkit.isMocked()) {
        MockBukkit.unmock();  // Clean up previous state
    }
    server = MockBukkit.mock();
}
```

### Gradle Dependency

```gradle
testImplementation 'org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.14.0'
```

### Capability Resolution (if using Leaf/Paper API)

```gradle
configurations.all {
    resolutionStrategy.capabilitiesResolution.withCapability('io.papermc.paper:paper-mojangapi') {
        selectHighestVersion()
    }
}
```

## Checklist Before Writing Tests

- [ ] MockBukkit setup in @BeforeAll/@AfterAll
- [ ] Using `org.mockbukkit.mockbukkit` package (not `be.seeseemelk`)
- [ ] Real ItemStack instead of mock(ItemStack.class)
- [ ] `server.addPlayer()` instead of mock(Player.class)
- [ ] Proper test isolation with `isMocked()` checks
- [ ] Only use Mockito for non-Bukkit classes
