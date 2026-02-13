package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for IEffectAction interface - defines effect action contract.
 */
@DisplayName("IEffectAction Tests")
class IEffectActionTest {

    @Nested
    @DisplayName("Interface Contract")
    class InterfaceContractTests {

        @Test
        @DisplayName("should be an interface")
        void shouldBeAnInterface() {
            assertTrue(IEffectAction.class.isInterface());
        }

        @Test
        @DisplayName("should declare updateAndExecute method")
        void shouldDeclareUpdateAndExecute() throws NoSuchMethodException {
            Method method = IEffectAction.class.getDeclaredMethod("updateAndExecute", CEFunctionData.class);
            assertNotNull(method);
            assertEquals(void.class, method.getReturnType());
        }

        @Test
        @DisplayName("should declare execute method")
        void shouldDeclareExecute() throws NoSuchMethodException {
            Method method = IEffectAction.class.getDeclaredMethod("execute", CEFunctionData.class);
            assertNotNull(method);
            assertEquals(void.class, method.getReturnType());
        }

        @Test
        @DisplayName("should have exactly 2 declared methods")
        void shouldHaveTwoMethods() {
            assertEquals(2, IEffectAction.class.getDeclaredMethods().length);
        }
    }

    @Nested
    @DisplayName("Implementation Tests")
    class ImplementationTests {

        @Test
        @DisplayName("should be implementable")
        void shouldBeImplementable() {
            IEffectAction action = new IEffectAction() {
                @Override
                public void updateAndExecute(CEFunctionData data) {
                    // no-op
                }

                @Override
                public void execute(CEFunctionData data) {
                    // no-op
                }
            };

            assertNotNull(action);
        }

        @Test
        @DisplayName("implementation should receive CEFunctionData parameter")
        void shouldReceiveCEFunctionData() {
            final boolean[] called = {false};

            IEffectAction action = new IEffectAction() {
                @Override
                public void updateAndExecute(CEFunctionData data) {
                    // no-op
                }

                @Override
                public void execute(CEFunctionData data) {
                    called[0] = true;
                    assertNull(data);
                }
            };

            action.execute(null);
            assertTrue(called[0]);
        }
    }
}
