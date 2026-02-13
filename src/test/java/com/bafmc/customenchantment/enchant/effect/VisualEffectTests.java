package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for visual-related effects:
 * - EffectPacketParticle
 * - EffectPacketRedstoneParticle
 * - EffectPacketCircleRedstoneParticle
 * - EffectPacketSpiralRedstoneParticle
 * - EffectSetStaffParticle
 * - EffectRemoveStaffParticle
 */
@DisplayName("Visual Effects Tests")
class VisualEffectTests extends EffectBaseTest {

    @Nested
    @DisplayName("EffectPacketParticle Tests")
    class EffectPacketParticleTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectPacketParticle effect = new EffectPacketParticle();
            assertEffectIdentifier(effect, "PACKET_PARTICLE");
        }

        @Test
        @DisplayName("should be async")
        void shouldBeAsync() {
            EffectPacketParticle effect = new EffectPacketParticle();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectPacketParticle effect = new EffectPacketParticle();
            String[] args = {"EXPLOSION", "10", "0", "PLAYER", "0:0:0", "1.0", "10"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectPacketParticle effect = new EffectPacketParticle();
            String[] args = {"EXPLOSION", "10", "0", "PLAYER", "0:0:0", "1.0", "10"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectPacketRedstoneParticle Tests")
    class EffectPacketRedstoneParticleTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectPacketRedstoneParticle effect = new EffectPacketRedstoneParticle();
            assertEffectIdentifier(effect, "PACKET_REDSTONE_PARTICLE");
        }

        @Test
        @DisplayName("should be async")
        void shouldBeAsync() {
            EffectPacketRedstoneParticle effect = new EffectPacketRedstoneParticle();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectPacketRedstoneParticle effect = new EffectPacketRedstoneParticle();
            String[] args = {"10", "0", "PLAYER", "0:0:0", "0:0:0", "1.0", "10", "#FF0000", "1"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectPacketRedstoneParticle effect = new EffectPacketRedstoneParticle();
            String[] args = {"10", "0", "PLAYER", "0:0:0", "0:0:0", "1.0", "10", "#FF0000", "1"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectPacketCircleRedstoneParticle Tests")
    class EffectPacketCircleRedstoneParticleTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectPacketCircleRedstoneParticle effect = new EffectPacketCircleRedstoneParticle();
            assertEffectIdentifier(effect, "PACKET_CIRCLE_REDSTONE_PARTICLE");
        }

        @Test
        @DisplayName("should be async")
        void shouldBeAsync() {
            EffectPacketCircleRedstoneParticle effect = new EffectPacketCircleRedstoneParticle();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectPacketCircleRedstoneParticle effect = new EffectPacketCircleRedstoneParticle();
            String[] args = {"10", "0", "PLAYER", "5.0", "0:0:0", "1.0", "10", "#FF0000", "1"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectPacketCircleRedstoneParticle effect = new EffectPacketCircleRedstoneParticle();
            String[] args = {"10", "0", "PLAYER", "5.0", "0:0:0", "1.0", "10", "#FF0000", "1"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectPacketSpiralRedstoneParticle Tests")
    class EffectPacketSpiralRedstoneParticleTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectPacketSpiralRedstoneParticle effect = new EffectPacketSpiralRedstoneParticle();
            assertEffectIdentifier(effect, "PACKET_SPIRAL_REDSTONE_PARTICLE");
        }

        @Test
        @DisplayName("should be async")
        void shouldBeAsync() {
            EffectPacketSpiralRedstoneParticle effect = new EffectPacketSpiralRedstoneParticle();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectPacketSpiralRedstoneParticle effect = new EffectPacketSpiralRedstoneParticle();
            String[] args = {"10", "0", "PLAYER", "5.0", "0:0:0", "1.0", "10", "#FF0000", "1"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectPacketSpiralRedstoneParticle effect = new EffectPacketSpiralRedstoneParticle();
            String[] args = {"10", "0", "PLAYER", "5.0", "0:0:0", "1.0", "10", "#FF0000", "1"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectSetStaffParticle Tests")
    class EffectSetStaffParticleTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectSetStaffParticle effect = new EffectSetStaffParticle();
            assertEffectIdentifier(effect, "SET_STAFF_PARTICLE");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectSetStaffParticle effect = new EffectSetStaffParticle();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectSetStaffParticle effect = new EffectSetStaffParticle();
            String[] args = {"particle_name"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectSetStaffParticle effect = new EffectSetStaffParticle();
            String[] args = {"particle_name"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectRemoveStaffParticle Tests")
    class EffectRemoveStaffParticleTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveStaffParticle effect = new EffectRemoveStaffParticle();
            assertEffectIdentifier(effect, "REMOVE_STAFF_PARTICLE");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectRemoveStaffParticle effect = new EffectRemoveStaffParticle();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveStaffParticle effect = new EffectRemoveStaffParticle();
            String[] args = {"particle_name"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectRemoveStaffParticle effect = new EffectRemoveStaffParticle();
            String[] args = {"particle_name"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }
}
