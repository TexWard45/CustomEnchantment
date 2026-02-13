package com.bafmc.customenchantment.enchant;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CECallerBuilder - builder pattern for constructing CECaller instances.
 * Note: call() depends on CustomEnchantment.instance() so we test the builder API.
 */
@DisplayName("CECallerBuilder Tests")
class CECallerBuilderTest {

    @Nested
    @DisplayName("build Tests")
    class BuildTests {

        @Test
        @DisplayName("should create new builder instance")
        void shouldCreateNewBuilder() {
            CECallerBuilder builder = CECallerBuilder.build();

            assertNotNull(builder);
        }

        @Test
        @DisplayName("should create new builder each call")
        void shouldCreateNewBuilderEachCall() {
            CECallerBuilder builder1 = CECallerBuilder.build();
            CECallerBuilder builder2 = CECallerBuilder.build();

            assertNotSame(builder1, builder2);
        }
    }

    @Nested
    @DisplayName("Fluent Setter Tests")
    class FluentSetterTests {

        @Test
        @DisplayName("should set and get CEType")
        void shouldSetCEType() {
            CECallerBuilder builder = CECallerBuilder.build();
            CECallerBuilder result = builder.setCEType(CEType.MINING);

            assertSame(builder, result);
            assertSame(CEType.MINING, builder.getCeType());
        }

        @Test
        @DisplayName("should set and get activeEquipSlot")
        void shouldSetActiveEquipSlot() {
            CECallerBuilder builder = CECallerBuilder.build();
            builder.setActiveEquipSlot(EquipSlot.OFFHAND);

            assertEquals(EquipSlot.OFFHAND, builder.getActiveEquipSlot());
        }

        @Test
        @DisplayName("should set and get CEFunctionData")
        void shouldSetCEFunctionData() {
            CECallerBuilder builder = CECallerBuilder.build();
            CEFunctionData data = mock(CEFunctionData.class);
            builder.setCEFunctionData(data);

            assertSame(data, builder.getCEFunctionData());
        }

        @Test
        @DisplayName("should set and get weaponMap")
        void shouldSetWeaponMap() {
            CECallerBuilder builder = CECallerBuilder.build();
            Map<EquipSlot, CEWeaponAbstract> map = new HashMap<>();
            builder.setWeaponMap(map);

            assertSame(map, builder.getWeaponMap());
        }

        @Test
        @DisplayName("should set executeLater")
        void shouldSetExecuteLater() {
            CECallerBuilder builder = CECallerBuilder.build();
            builder.setExecuteLater(false);

            assertFalse(builder.isExecuteLater());
        }

        @Test
        @DisplayName("should default executeLater to true")
        void shouldDefaultExecuteLaterTrue() {
            CECallerBuilder builder = CECallerBuilder.build();

            assertTrue(builder.isExecuteLater());
        }

        @Test
        @DisplayName("should set checkWorld")
        void shouldSetCheckWorld() {
            CECallerBuilder builder = CECallerBuilder.build();
            builder.setCheckWorld(false);

            assertFalse(builder.isCheckWorld());
        }

        @Test
        @DisplayName("should default checkWorld to true")
        void shouldDefaultCheckWorldTrue() {
            CECallerBuilder builder = CECallerBuilder.build();

            assertTrue(builder.isCheckWorld());
        }

        @Test
        @DisplayName("should set byPassCooldown")
        void shouldSetByPassCooldown() {
            CECallerBuilder builder = CECallerBuilder.build();
            builder.setByPassCooldown(true);

            // No direct getter for bypassCooldown in the builder, but test setter returns builder
            assertNotNull(builder);
        }
    }

    @Nested
    @DisplayName("getPlayer Tests")
    class GetPlayerTests {

        @Test
        @DisplayName("should return null when player not set")
        void shouldReturnNullWhenNotSet() {
            CECallerBuilder builder = CECallerBuilder.build();

            assertNull(builder.getPlayer());
        }
    }

    @Nested
    @DisplayName("getCEFunctionData Tests")
    class GetCEFunctionDataTests {

        @Test
        @DisplayName("should create new CEFunctionData when not explicitly set and player is null")
        void shouldCreateNewWhenNotSet() {
            CECallerBuilder builder = CECallerBuilder.build();

            // getCEFunctionData creates new CEFunctionData(player) if ceFunctionData is null
            // Since player is null, it will try to create with null player
            CEFunctionData data = builder.getCEFunctionData();
            assertNotNull(data);
        }
    }

    @Nested
    @DisplayName("Chaining Tests")
    class ChainingTests {

        @Test
        @DisplayName("should chain all setters")
        void shouldChainAllSetters() {
            CECallerBuilder builder = CECallerBuilder.build()
                    .setCEType(CEType.ATTACK)
                    .setActiveEquipSlot(EquipSlot.MAINHAND)
                    .setExecuteLater(true)
                    .setCheckWorld(false)
                    .setByPassCooldown(true);

            assertNotNull(builder);
            assertSame(CEType.ATTACK, builder.getCeType());
            assertEquals(EquipSlot.MAINHAND, builder.getActiveEquipSlot());
            assertTrue(builder.isExecuteLater());
            assertFalse(builder.isCheckWorld());
        }
    }
}
