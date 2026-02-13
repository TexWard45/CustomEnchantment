package com.bafmc.customenchantment.player;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CEPlayerExpansion")
@ExtendWith(MockitoExtension.class)
class CEPlayerExpansionTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    // Concrete test implementation of abstract class
    private static class TestExpansion extends CEPlayerExpansion {
        private boolean joinCalled = false;
        private boolean quitCalled = false;

        public TestExpansion(CEPlayer cePlayer) {
            super(cePlayer);
        }

        @Override
        public void onJoin() {
            joinCalled = true;
        }

        @Override
        public void onQuit() {
            quitCalled = true;
        }

        public boolean isJoinCalled() {
            return joinCalled;
        }

        public boolean isQuitCalled() {
            return quitCalled;
        }
    }

    private TestExpansion expansion;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        expansion = new TestExpansion(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should store CEPlayer reference")
        void shouldStoreCEPlayerReference() {
            assertSame(mockCEPlayer, expansion.getCEPlayer());
        }

        @Test
        @DisplayName("Should store Player reference from CEPlayer")
        void shouldStorePlayerReference() {
            assertSame(mockPlayer, expansion.getPlayer());
        }
    }

    @Nested
    @DisplayName("Accessor Tests")
    class AccessorTests {

        @Test
        @DisplayName("getCEPlayer should return the same CEPlayer")
        void getCEPlayerShouldReturnSameCEPlayer() {
            assertEquals(mockCEPlayer, expansion.getCEPlayer());
        }

        @Test
        @DisplayName("getPlayer should return the same Player")
        void getPlayerShouldReturnSamePlayer() {
            assertEquals(mockPlayer, expansion.getPlayer());
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should be callable")
        void onJoinShouldBeCallable() {
            expansion.onJoin();
            assertTrue(expansion.isJoinCalled());
        }

        @Test
        @DisplayName("onQuit should be callable")
        void onQuitShouldBeCallable() {
            expansion.onQuit();
            assertTrue(expansion.isQuitCalled());
        }

        @Test
        @DisplayName("onJoin and onQuit should be independently callable")
        void onJoinAndOnQuitShouldBeIndependent() {
            expansion.onJoin();
            assertTrue(expansion.isJoinCalled());
            assertFalse(expansion.isQuitCalled());

            expansion.onQuit();
            assertTrue(expansion.isQuitCalled());
        }
    }

    @Nested
    @DisplayName("Interface Implementation Tests")
    class InterfaceImplementationTests {

        @Test
        @DisplayName("Should implement ICEPlayerEvent interface")
        void shouldImplementICEPlayerEvent() {
            assertTrue(ICEPlayerEvent.class.isAssignableFrom(CEPlayerExpansion.class));
        }

        @Test
        @DisplayName("Should be abstract class")
        void shouldBeAbstractClass() {
            assertTrue(java.lang.reflect.Modifier.isAbstract(CEPlayerExpansion.class.getModifiers()));
        }

        @Test
        @DisplayName("onJoin should be abstract")
        void onJoinShouldBeAbstract() throws NoSuchMethodException {
            assertTrue(java.lang.reflect.Modifier.isAbstract(
                    CEPlayerExpansion.class.getDeclaredMethod("onJoin").getModifiers()));
        }

        @Test
        @DisplayName("onQuit should be abstract")
        void onQuitShouldBeAbstract() throws NoSuchMethodException {
            assertTrue(java.lang.reflect.Modifier.isAbstract(
                    CEPlayerExpansion.class.getDeclaredMethod("onQuit").getModifiers()));
        }
    }
}
