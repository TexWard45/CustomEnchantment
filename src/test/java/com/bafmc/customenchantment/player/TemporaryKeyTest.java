package com.bafmc.customenchantment.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TemporaryKey")
class TemporaryKeyTest {

    @Nested
    @DisplayName("Constant Definitions Tests")
    class ConstantDefinitionsTests {

        @Test
        @DisplayName("Should define NOW_MULTIPLE_ARROW constant")
        void shouldDefineNowMultipleArrow() {
            assertEquals("now_multiple_arrow", TemporaryKey.NOW_MULTIPLE_ARROW);
        }

        @Test
        @DisplayName("Should define MULTIPLE_ARROW_ENABLE constant")
        void shouldDefineMultipleArrowEnable() {
            assertEquals("multiple_arrow_enable", TemporaryKey.MULTIPLE_ARROW_ENABLE);
        }

        @Test
        @DisplayName("Should define MULTIPLE_ARROW_AMOUNT constant")
        void shouldDefineMultipleArrowAmount() {
            assertEquals("multiple_arrow_amount", TemporaryKey.MULTIPLE_ARROW_AMOUNT);
        }

        @Test
        @DisplayName("Should define MULTIPLE_ARROW_MOD constant")
        void shouldDefineMultipleArrowMod() {
            assertEquals("multiple_arrow_mod", TemporaryKey.MULTIPLE_ARROW_MOD);
        }

        @Test
        @DisplayName("Should define MULTIPLE_ARROW_DAMAGE_RATIO constant")
        void shouldDefineMultipleArrowDamageRatio() {
            assertEquals("multiple_arrow_damage_ratio", TemporaryKey.MULTIPLE_ARROW_DAMAGE_RATIO);
        }

        @Test
        @DisplayName("Should define MULTIPLE_ARROW_LAST_USE constant")
        void shouldDefineMultipleArrowLastUse() {
            assertEquals("multiple_arrow_last_use", TemporaryKey.MULTIPLE_ARROW_LAST_USE);
        }

        @Test
        @DisplayName("Should define MULTIPLE_ARROW_COOLDOWN constant")
        void shouldDefineMultipleArrowCooldown() {
            assertEquals("multiple_arrow_cooldown", TemporaryKey.MULTIPLE_ARROW_COOLDOWN);
        }

        @Test
        @DisplayName("Should define BOW_POWER constant")
        void shouldDefineBowPower() {
            assertEquals("bow_power", TemporaryKey.BOW_POWER);
        }

        @Test
        @DisplayName("Should define LAST_MOVE_TIME constant")
        void shouldDefineLastMoveTime() {
            assertEquals("last_move_time", TemporaryKey.LAST_MOVE_TIME);
        }

        @Test
        @DisplayName("Should define LAST_SNEAK_TIME constant")
        void shouldDefineLastSneakTime() {
            assertEquals("last_sneak_time", TemporaryKey.LAST_SNEAK_TIME);
        }

        @Test
        @DisplayName("Should define LAST_COMBAT_TIME constant")
        void shouldDefineLastCombatTime() {
            assertEquals("last_combat_time", TemporaryKey.LAST_COMBAT_TIME);
        }

        @Test
        @DisplayName("Should define IN_AIR constant")
        void shouldDefineInAir() {
            assertEquals("in_air", TemporaryKey.IN_AIR);
        }

        @Test
        @DisplayName("Should define MINING_TELEPATHY_ENABLE constant")
        void shouldDefineMiningTelepathyEnable() {
            assertEquals("mining_telepathy_enable", TemporaryKey.MINING_TELEPATHY_ENABLE);
        }

        @Test
        @DisplayName("Should define AUTO_SELL_ENABLE constant")
        void shouldDefineAutoSellEnable() {
            assertEquals("auto_sell_enable", TemporaryKey.AUTO_SELL_ENABLE);
        }

        @Test
        @DisplayName("Should define DASH_ENABLE constant")
        void shouldDefineDashEnable() {
            assertEquals("dash_enable", TemporaryKey.DASH_ENABLE);
        }

        @Test
        @DisplayName("Should define DOUBLE_JUMP_ENABLE constant")
        void shouldDefineDoubleJumpEnable() {
            assertEquals("double_jump_enable", TemporaryKey.DOUBLE_JUMP_ENABLE);
        }

        @Test
        @DisplayName("Should define FLASH_ENABLE constant")
        void shouldDefineFlashEnable() {
            assertEquals("flash_enable", TemporaryKey.FLASH_ENABLE);
        }

        @Test
        @DisplayName("Should define LAST_MINE_BLOCK_TYPE constant")
        void shouldDefineLastMineBlockType() {
            assertEquals("last_mine_block_type", TemporaryKey.LAST_MINE_BLOCK_TYPE);
        }

        @Test
        @DisplayName("Should define PARTICLE_TURN constant")
        void shouldDefineParticleTurn() {
            assertEquals("particle_turn", TemporaryKey.PARTICLE_TURN);
        }

        @Test
        @DisplayName("Should define STAFF_PARTICLE constant")
        void shouldDefineStaffParticle() {
            assertEquals("staff_particle", TemporaryKey.STAFF_PARTICLE);
        }
    }

    @Nested
    @DisplayName("Constant Modifiers Tests")
    class ConstantModifiersTests {

        @Test
        @DisplayName("All fields should be public static final String")
        void allFieldsShouldBePublicStaticFinalString() {
            Field[] fields = TemporaryKey.class.getDeclaredFields();
            assertTrue(fields.length > 0, "TemporaryKey should have constants defined");

            for (Field field : fields) {
                int modifiers = field.getModifiers();
                assertTrue(Modifier.isPublic(modifiers),
                        field.getName() + " should be public");
                assertTrue(Modifier.isStatic(modifiers),
                        field.getName() + " should be static");
                assertTrue(Modifier.isFinal(modifiers),
                        field.getName() + " should be final");
                assertEquals(String.class, field.getType(),
                        field.getName() + " should be of type String");
            }
        }
    }

    @Nested
    @DisplayName("Uniqueness Tests")
    class UniquenessTests {

        @Test
        @DisplayName("All constant values should be unique")
        void allConstantValuesShouldBeUnique() throws IllegalAccessException {
            Field[] fields = TemporaryKey.class.getDeclaredFields();
            java.util.Set<String> values = new java.util.HashSet<>();

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
                    String value = (String) field.get(null);
                    assertTrue(values.add(value),
                            "Duplicate constant value found: " + value + " in field " + field.getName());
                }
            }
        }
    }

    @Nested
    @DisplayName("Naming Convention Tests")
    class NamingConventionTests {

        @Test
        @DisplayName("All constant values should use lowercase with underscores")
        void allConstantValuesShouldUseLowercaseWithUnderscores() throws IllegalAccessException {
            Field[] fields = TemporaryKey.class.getDeclaredFields();

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
                    String value = (String) field.get(null);
                    assertEquals(value.toLowerCase(), value,
                            "Constant value for " + field.getName() + " should be lowercase");
                    assertFalse(value.contains(" "),
                            "Constant value for " + field.getName() + " should not contain spaces");
                }
            }
        }
    }

    @Nested
    @DisplayName("Feature Group Tests")
    class FeatureGroupTests {

        @Test
        @DisplayName("Dash feature should have all required keys")
        void dashFeatureShouldHaveAllRequiredKeys() {
            assertNotNull(TemporaryKey.DASH_ENABLE);
            assertNotNull(TemporaryKey.DASH_COOLDOWN);
            assertNotNull(TemporaryKey.DASH_COOLDOWN_MESSAGE);
            assertNotNull(TemporaryKey.DASH_POWER);
            assertNotNull(TemporaryKey.DASH_PARTICLE_FORWARD);
            assertNotNull(TemporaryKey.DASH_PARTICLE_BACKWARD);
            assertNotNull(TemporaryKey.DASH_LAST_USE);
        }

        @Test
        @DisplayName("Double jump feature should have all required keys")
        void doubleJumpFeatureShouldHaveAllRequiredKeys() {
            assertNotNull(TemporaryKey.DOUBLE_JUMP_ENABLE);
            assertNotNull(TemporaryKey.DOUBLE_JUMP_COOLDOWN);
            assertNotNull(TemporaryKey.DOUBLE_JUMP_COOLDOWN_MESSAGE);
            assertNotNull(TemporaryKey.DOUBLE_JUMP_POWER);
            assertNotNull(TemporaryKey.DOUBLE_JUMP_PARTICLE);
            assertNotNull(TemporaryKey.DOUBLE_JUMP_LAST_USE);
        }

        @Test
        @DisplayName("Flash feature should have all required keys")
        void flashFeatureShouldHaveAllRequiredKeys() {
            assertNotNull(TemporaryKey.FLASH_ENABLE);
            assertNotNull(TemporaryKey.FLASH_COOLDOWN);
            assertNotNull(TemporaryKey.FLASH_COOLDOWN_MESSAGE);
            assertNotNull(TemporaryKey.FLASH_POWER);
            assertNotNull(TemporaryKey.FLASH_PARTICLE);
            assertNotNull(TemporaryKey.FLASH_LAST_USE);
            assertNotNull(TemporaryKey.FLASH_SMART);
        }

        @Test
        @DisplayName("Multiple arrow feature should have all required keys")
        void multipleArrowFeatureShouldHaveAllRequiredKeys() {
            assertNotNull(TemporaryKey.NOW_MULTIPLE_ARROW);
            assertNotNull(TemporaryKey.MULTIPLE_ARROW_ENABLE);
            assertNotNull(TemporaryKey.MULTIPLE_ARROW_AMOUNT);
            assertNotNull(TemporaryKey.MULTIPLE_ARROW_MOD);
            assertNotNull(TemporaryKey.MULTIPLE_ARROW_DAMAGE_RATIO);
            assertNotNull(TemporaryKey.MULTIPLE_ARROW_LAST_USE);
            assertNotNull(TemporaryKey.MULTIPLE_ARROW_COOLDOWN);
        }

        @Test
        @DisplayName("Particle feature should have all required keys")
        void particleFeatureShouldHaveAllRequiredKeys() {
            assertNotNull(TemporaryKey.PARTICLE_TURN);
            assertNotNull(TemporaryKey.PARTICLE_ANGLE);
            assertNotNull(TemporaryKey.PARTICLE_COLOR);
            assertNotNull(TemporaryKey.PARTICLE_CONTINUOUS);
            assertNotNull(TemporaryKey.STAFF_PARTICLE);
            assertNotNull(TemporaryKey.STAFF_PARTICLE_TURN);
        }
    }
}
