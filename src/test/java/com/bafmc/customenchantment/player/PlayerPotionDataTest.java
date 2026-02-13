package com.bafmc.customenchantment.player;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PlayerPotionData.
 * Note: PotionEffectType cannot be mocked with Mockito due to Bukkit server initialization requirements.
 * Tests focus on class structure, method signatures, and field management without requiring Bukkit server.
 */
@DisplayName("PlayerPotionData")
class PlayerPotionDataTest {

    @Nested
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("Should have constructor with PotionEffectType and int parameters")
        void shouldHaveCorrectConstructor() throws NoSuchMethodException {
            assertNotNull(PlayerPotionData.class.getConstructor(PotionEffectType.class, int.class));
        }

        @Test
        @DisplayName("Should have getType method returning PotionEffectType")
        void shouldHaveGetTypeMethod() throws NoSuchMethodException {
            assertEquals(PotionEffectType.class,
                    PlayerPotionData.class.getMethod("getType").getReturnType());
        }

        @Test
        @DisplayName("Should have setType method accepting PotionEffectType")
        void shouldHaveSetTypeMethod() throws NoSuchMethodException {
            assertNotNull(PlayerPotionData.class.getMethod("setType", PotionEffectType.class));
        }

        @Test
        @DisplayName("Should have getAmplifier method returning int")
        void shouldHaveGetAmplifierMethod() throws NoSuchMethodException {
            assertEquals(int.class,
                    PlayerPotionData.class.getMethod("getAmplifier").getReturnType());
        }

        @Test
        @DisplayName("Should have setAmplifier method accepting int")
        void shouldHaveSetAmplifierMethod() throws NoSuchMethodException {
            assertNotNull(PlayerPotionData.class.getMethod("setAmplifier", int.class));
        }

        @Test
        @DisplayName("Should have getPotionEffect method returning PotionEffect")
        void shouldHaveGetPotionEffectMethod() throws NoSuchMethodException {
            assertEquals(PotionEffect.class,
                    PlayerPotionData.class.getMethod("getPotionEffect").getReturnType());
        }
    }

    @Nested
    @DisplayName("Field Tests")
    class FieldTests {

        @Test
        @DisplayName("Should have effect field of type PotionEffect")
        void shouldHaveEffectField() throws NoSuchFieldException {
            Field field = PlayerPotionData.class.getDeclaredField("effect");
            assertEquals(PotionEffect.class, field.getType());
        }

        @Test
        @DisplayName("Should have type field of type PotionEffectType")
        void shouldHaveTypeField() throws NoSuchFieldException {
            Field field = PlayerPotionData.class.getDeclaredField("type");
            assertEquals(PotionEffectType.class, field.getType());
        }

        @Test
        @DisplayName("Should have amplifier field of type int")
        void shouldHaveAmplifierField() throws NoSuchFieldException {
            Field field = PlayerPotionData.class.getDeclaredField("amplifier");
            assertEquals(int.class, field.getType());
        }
    }

    @Nested
    @DisplayName("Method Count Tests")
    class MethodCountTests {

        @Test
        @DisplayName("Should have getter and setter for type")
        void shouldHaveGetterAndSetterForType() {
            assertDoesNotThrow(() -> PlayerPotionData.class.getMethod("getType"));
            assertDoesNotThrow(() -> PlayerPotionData.class.getMethod("setType", PotionEffectType.class));
        }

        @Test
        @DisplayName("Should have getter and setter for amplifier")
        void shouldHaveGetterAndSetterForAmplifier() {
            assertDoesNotThrow(() -> PlayerPotionData.class.getMethod("getAmplifier"));
            assertDoesNotThrow(() -> PlayerPotionData.class.getMethod("setAmplifier", int.class));
        }

        @Test
        @DisplayName("Should have getPotionEffect method")
        void shouldHaveGetPotionEffectMethod() {
            assertDoesNotThrow(() -> PlayerPotionData.class.getMethod("getPotionEffect"));
        }
    }
}
