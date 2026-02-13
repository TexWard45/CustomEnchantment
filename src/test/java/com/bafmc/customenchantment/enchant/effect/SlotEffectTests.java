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
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectActiveEquipSlot effect = new EffectActiveEquipSlot();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectActiveEquipSlot effect = new EffectActiveEquipSlot();
            String[] args = {"HAND"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectActiveEquipSlot effect = new EffectActiveEquipSlot();
            String[] args = {"HAND"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should handle null player gracefully")
        void shouldHandleNullPlayerGracefully() {
            EffectActiveEquipSlot effect = new EffectActiveEquipSlot();
            String[] args = {"HAND"};
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
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectDeactiveEquipSlot effect = new EffectDeactiveEquipSlot();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectDeactiveEquipSlot effect = new EffectDeactiveEquipSlot();
            String[] args = {"HAND"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectDeactiveEquipSlot effect = new EffectDeactiveEquipSlot();
            String[] args = {"HAND"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should handle null player gracefully")
        void shouldHandleNullPlayerGracefully() {
            EffectDeactiveEquipSlot effect = new EffectDeactiveEquipSlot();
            String[] args = {"HAND"};
            effect.setup(args);

            assertExecuteHandlesNullPlayer(effect);
        }
    }
}
