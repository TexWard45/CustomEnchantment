package com.bafmc.customenchantment.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ICEPlayerEvent")
class ICEPlayerEventTest {

    @Nested
    @DisplayName("Interface Contract Tests")
    class InterfaceContractTests {

        @Test
        @DisplayName("Should be an interface")
        void shouldBeAnInterface() {
            assertTrue(ICEPlayerEvent.class.isInterface());
        }

        @Test
        @DisplayName("Should define onJoin method")
        void shouldDefineOnJoinMethod() throws NoSuchMethodException {
            assertNotNull(ICEPlayerEvent.class.getMethod("onJoin"));
        }

        @Test
        @DisplayName("Should define onQuit method")
        void shouldDefineOnQuitMethod() throws NoSuchMethodException {
            assertNotNull(ICEPlayerEvent.class.getMethod("onQuit"));
        }

        @Test
        @DisplayName("onJoin should return void")
        void onJoinShouldReturnVoid() throws NoSuchMethodException {
            assertEquals(void.class, ICEPlayerEvent.class.getMethod("onJoin").getReturnType());
        }

        @Test
        @DisplayName("onQuit should return void")
        void onQuitShouldReturnVoid() throws NoSuchMethodException {
            assertEquals(void.class, ICEPlayerEvent.class.getMethod("onQuit").getReturnType());
        }

        @Test
        @DisplayName("onJoin should have no parameters")
        void onJoinShouldHaveNoParameters() throws NoSuchMethodException {
            assertEquals(0, ICEPlayerEvent.class.getMethod("onJoin").getParameterCount());
        }

        @Test
        @DisplayName("onQuit should have no parameters")
        void onQuitShouldHaveNoParameters() throws NoSuchMethodException {
            assertEquals(0, ICEPlayerEvent.class.getMethod("onQuit").getParameterCount());
        }
    }

    @Nested
    @DisplayName("Implementation Tests")
    class ImplementationTests {

        @Test
        @DisplayName("Should allow anonymous implementation")
        void shouldAllowAnonymousImplementation() {
            ICEPlayerEvent event = new ICEPlayerEvent() {
                @Override
                public void onJoin() {
                    // No-op
                }

                @Override
                public void onQuit() {
                    // No-op
                }
            };

            assertDoesNotThrow(event::onJoin);
            assertDoesNotThrow(event::onQuit);
        }

        @Test
        @DisplayName("CEPlayer should implement ICEPlayerEvent")
        void cePlayerShouldImplementInterface() {
            assertTrue(ICEPlayerEvent.class.isAssignableFrom(CEPlayer.class));
        }

        @Test
        @DisplayName("CEPlayerExpansion should implement ICEPlayerEvent")
        void cePlayerExpansionShouldImplementInterface() {
            assertTrue(ICEPlayerEvent.class.isAssignableFrom(CEPlayerExpansion.class));
        }
    }
}
