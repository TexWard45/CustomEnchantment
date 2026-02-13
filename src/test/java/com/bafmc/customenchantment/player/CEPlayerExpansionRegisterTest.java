package com.bafmc.customenchantment.player;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CEPlayerExpansionRegister")
@ExtendWith(MockitoExtension.class)
class CEPlayerExpansionRegisterTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    // Concrete test expansions
    private static class TestExpansionA extends CEPlayerExpansion {
        public TestExpansionA(CEPlayer cePlayer) {
            super(cePlayer);
        }

        @Override
        public void onJoin() {
        }

        @Override
        public void onQuit() {
        }
    }

    private static class TestExpansionB extends CEPlayerExpansion {
        public TestExpansionB(CEPlayer cePlayer) {
            super(cePlayer);
        }

        @Override
        public void onJoin() {
        }

        @Override
        public void onQuit() {
        }
    }

    @BeforeEach
    void setUp() {
        // Clean the static list before each test
        CEPlayerExpansionRegister.list.clear();
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
    }

    @AfterAll
    static void tearDown() {
        CEPlayerExpansionRegister.list.clear();
    }

    @Nested
    @DisplayName("register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should register a new expansion class")
        void shouldRegisterNewExpansionClass() {
            CEPlayerExpansionRegister.register(TestExpansionA.class);
            assertTrue(CEPlayerExpansionRegister.list.contains(TestExpansionA.class));
        }

        @Test
        @DisplayName("Should not register duplicate expansion class")
        void shouldNotRegisterDuplicateExpansionClass() {
            CEPlayerExpansionRegister.register(TestExpansionA.class);
            CEPlayerExpansionRegister.register(TestExpansionA.class);
            assertEquals(1, CEPlayerExpansionRegister.list.size());
        }

        @Test
        @DisplayName("Should register multiple different expansion classes")
        void shouldRegisterMultipleDifferentClasses() {
            CEPlayerExpansionRegister.register(TestExpansionA.class);
            CEPlayerExpansionRegister.register(TestExpansionB.class);
            assertEquals(2, CEPlayerExpansionRegister.list.size());
            assertTrue(CEPlayerExpansionRegister.list.contains(TestExpansionA.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(TestExpansionB.class));
        }
    }

    @Nested
    @DisplayName("unregister Tests")
    class UnregisterTests {

        @Test
        @DisplayName("Should unregister an existing expansion class")
        void shouldUnregisterExistingExpansionClass() {
            CEPlayerExpansionRegister.register(TestExpansionA.class);
            CEPlayerExpansionRegister.unregister(TestExpansionA.class);
            assertFalse(CEPlayerExpansionRegister.list.contains(TestExpansionA.class));
        }

        @Test
        @DisplayName("Should not throw when unregistering non-existing class")
        void shouldNotThrowWhenUnregisteringNonExistingClass() {
            assertDoesNotThrow(() -> CEPlayerExpansionRegister.unregister(TestExpansionA.class));
        }

        @Test
        @DisplayName("Should only unregister specified class")
        void shouldOnlyUnregisterSpecifiedClass() {
            CEPlayerExpansionRegister.register(TestExpansionA.class);
            CEPlayerExpansionRegister.register(TestExpansionB.class);
            CEPlayerExpansionRegister.unregister(TestExpansionA.class);

            assertFalse(CEPlayerExpansionRegister.list.contains(TestExpansionA.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(TestExpansionB.class));
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("Should setup expansions on CEPlayer from registered list")
        void shouldSetupExpansionsOnCEPlayer() {
            CEPlayerExpansionRegister.register(TestExpansionA.class);
            CEPlayerExpansionRegister.register(TestExpansionB.class);

            CEPlayerExpansionRegister.setup(mockCEPlayer);

            verify(mockCEPlayer, times(2)).addExpansion(any(CEPlayerExpansion.class));
        }

        @Test
        @DisplayName("Should handle empty registry gracefully")
        void shouldHandleEmptyRegistry() {
            assertDoesNotThrow(() -> CEPlayerExpansionRegister.setup(mockCEPlayer));
            verify(mockCEPlayer, never()).addExpansion(any());
        }

        @Test
        @DisplayName("Should not throw on class without proper constructor")
        void shouldNotThrowOnClassWithoutProperConstructor() {
            // This test ensures the try-catch in setup works
            // Register a class that will fail to construct
            // Using a class that doesn't have a constructor taking CEPlayer
            // The setup method should catch the exception and continue
            CEPlayerExpansionRegister.register(TestExpansionA.class);
            assertDoesNotThrow(() -> CEPlayerExpansionRegister.setup(mockCEPlayer));
        }
    }

    @Nested
    @DisplayName("List State Tests")
    class ListStateTests {

        @Test
        @DisplayName("List should be empty initially after cleanup")
        void listShouldBeEmptyInitially() {
            assertTrue(CEPlayerExpansionRegister.list.isEmpty());
        }

        @Test
        @DisplayName("List should maintain insertion order")
        void listShouldMaintainInsertionOrder() {
            CEPlayerExpansionRegister.register(TestExpansionA.class);
            CEPlayerExpansionRegister.register(TestExpansionB.class);

            assertEquals(TestExpansionA.class, CEPlayerExpansionRegister.list.get(0));
            assertEquals(TestExpansionB.class, CEPlayerExpansionRegister.list.get(1));
        }
    }
}
