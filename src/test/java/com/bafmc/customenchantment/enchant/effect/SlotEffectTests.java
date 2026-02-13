package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for slot-related effects:
 * - EffectActiveEquipSlot
 * - EffectDeactiveEquipSlot
 */
@DisplayName("Slot Effects Tests")
class SlotEffectTests extends EffectBaseTest {

    @Nested
    @DisplayName("EffectActiveEquipSlot Tests")
    class EffectActiveEquipSlotTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectActiveEquipSlot effect = new EffectActiveEquipSlot();
            assertEffectIdentifier(effect, "ACTIVE_EQUIP_SLOT");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectActiveEquipSlot effect = new EffectActiveEquipSlot();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectActiveEquipSlot effect = new EffectActiveEquipSlot();
            // args: slot (EquipSlot enum), name
            String[] args = {"MAINHAND", "slot_name"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectActiveEquipSlot effect = new EffectActiveEquipSlot();
            String[] args = {"MAINHAND", "slot_name"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should handle null player gracefully")
        void shouldHandleNullPlayerGracefully() {
            EffectActiveEquipSlot effect = new EffectActiveEquipSlot();
            String[] args = {"MAINHAND", "slot_name"};
            effect.setup(args);

            assertExecuteHandlesNullPlayer(effect);
        }
    }

    @Nested
    @DisplayName("EffectDeactiveEquipSlot Tests")
    class EffectDeactiveEquipSlotTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectDeactiveEquipSlot effect = new EffectDeactiveEquipSlot();
            assertEffectIdentifier(effect, "DEACTIVE_EQUIP_SLOT");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectDeactiveEquipSlot effect = new EffectDeactiveEquipSlot();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectDeactiveEquipSlot effect = new EffectDeactiveEquipSlot();
            // args: slot (EquipSlot enum), name, [duration]
            String[] args = {"MAINHAND", "slot_name"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectDeactiveEquipSlot effect = new EffectDeactiveEquipSlot();
            String[] args = {"MAINHAND", "slot_name"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should handle null player gracefully")
        void shouldHandleNullPlayerGracefully() {
            EffectDeactiveEquipSlot effect = new EffectDeactiveEquipSlot();
            String[] args = {"MAINHAND", "slot_name"};
            effect.setup(args);

            assertExecuteHandlesNullPlayer(effect);
        }
    }
}
