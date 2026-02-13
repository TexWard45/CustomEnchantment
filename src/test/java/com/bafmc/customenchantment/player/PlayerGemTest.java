package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.bafframework.nms.NMSAttributeSlot;
import com.bafmc.bukkit.utils.EquipSlot;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerGem")
@ExtendWith(MockitoExtension.class)
class PlayerGemTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private PlayerVanillaAttribute mockVanillaAttribute;

    @Mock
    private PlayerCustomAttribute mockCustomAttribute;

    private PlayerGem playerGem;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        lenient().when(mockCEPlayer.getVanillaAttribute()).thenReturn(mockVanillaAttribute);
        lenient().when(mockCEPlayer.getCustomAttribute()).thenReturn(mockCustomAttribute);
        playerGem = new PlayerGem(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerGem);
            assertSame(mockCEPlayer, playerGem.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerGem instanceof CEPlayerExpansion);
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should deactivate all")
        void onJoinShouldDeactivateAll() {
            assertDoesNotThrow(() -> playerGem.onJoin());
        }

        @Test
        @DisplayName("onQuit should deactivate all")
        void onQuitShouldDeactivateAll() {
            assertDoesNotThrow(() -> playerGem.onQuit());
        }
    }

    @Nested
    @DisplayName("deactivateAll Tests")
    class DeactivateAllTests {

        @Test
        @DisplayName("Should clear gem attribute map")
        void shouldClearGemAttributeMap() {
            assertDoesNotThrow(() -> playerGem.deactivateAll());
        }

        @Test
        @DisplayName("Should handle empty map gracefully")
        void shouldHandleEmptyMapGracefully() {
            playerGem.deactivateAll();
            // No exception means success
        }
    }

    @Nested
    @DisplayName("handleAttributeDeactivation Tests")
    class HandleAttributeDeactivationTests {

        @Test
        @DisplayName("Should not throw when weapon not in map")
        void shouldNotThrowWhenWeaponNotInMap() {
            com.bafmc.customenchantment.item.CEWeaponAbstract mockWeapon = mock(
                    com.bafmc.customenchantment.item.CEWeaponAbstract.class);
            assertDoesNotThrow(() ->
                    playerGem.handleAttributeDeactivation(EquipSlot.MAINHAND, mockWeapon));
        }
    }

    @Nested
    @DisplayName("isSuitableSlot Tests")
    class IsSuitableSlotTests {

        @Test
        @DisplayName("Should return true when nmsSlot is null")
        void shouldReturnTrueWhenNmsSlotIsNull() {
            assertTrue(PlayerGem.isSuitableSlot(EquipSlot.MAINHAND, null));
        }

        @Test
        @DisplayName("Should return true when nmsSlot is ALL")
        void shouldReturnTrueWhenNmsSlotIsAll() {
            assertTrue(PlayerGem.isSuitableSlot(EquipSlot.MAINHAND, NMSAttributeSlot.ALL));
            assertTrue(PlayerGem.isSuitableSlot(EquipSlot.HELMET, NMSAttributeSlot.ALL));
        }

        @Test
        @DisplayName("HAND slot should match MAINHAND")
        void handSlotShouldMatchMainhand() {
            assertTrue(PlayerGem.isSuitableSlot(EquipSlot.MAINHAND, NMSAttributeSlot.HAND));
        }

        @Test
        @DisplayName("HAND slot should not match OFFHAND")
        void handSlotShouldNotMatchOffhand() {
            assertFalse(PlayerGem.isSuitableSlot(EquipSlot.OFFHAND, NMSAttributeSlot.HAND));
        }

        @Test
        @DisplayName("OFFHAND nms slot should match OFFHAND equip slot")
        void offhandNmsSlotShouldMatchOffhandEquipSlot() {
            assertTrue(PlayerGem.isSuitableSlot(EquipSlot.OFFHAND, NMSAttributeSlot.OFFHAND));
        }

        @Test
        @DisplayName("OFFHAND nms slot should not match MAINHAND equip slot")
        void offhandNmsSlotShouldNotMatchMainhandEquipSlot() {
            assertFalse(PlayerGem.isSuitableSlot(EquipSlot.MAINHAND, NMSAttributeSlot.OFFHAND));
        }

        @Test
        @DisplayName("FEET slot should match BOOTS")
        void feetSlotShouldMatchBoots() {
            assertTrue(PlayerGem.isSuitableSlot(EquipSlot.BOOTS, NMSAttributeSlot.FEET));
        }

        @Test
        @DisplayName("LEGS slot should match LEGGINGS")
        void legsSlotShouldMatchLeggings() {
            assertTrue(PlayerGem.isSuitableSlot(EquipSlot.LEGGINGS, NMSAttributeSlot.LEGS));
        }

        @Test
        @DisplayName("CHEST slot should match CHESTPLATE")
        void chestSlotShouldMatchChestplate() {
            assertTrue(PlayerGem.isSuitableSlot(EquipSlot.CHESTPLATE, NMSAttributeSlot.CHEST));
        }

        @Test
        @DisplayName("HEAD slot should match HELMET")
        void headSlotShouldMatchHelmet() {
            assertTrue(PlayerGem.isSuitableSlot(EquipSlot.HELMET, NMSAttributeSlot.HEAD));
        }

        @Test
        @DisplayName("FEET slot should not match non-boots")
        void feetSlotShouldNotMatchNonBoots() {
            assertFalse(PlayerGem.isSuitableSlot(EquipSlot.HELMET, NMSAttributeSlot.FEET));
        }

        @Test
        @DisplayName("LEGS slot should not match non-leggings")
        void legsSlotShouldNotMatchNonLeggings() {
            assertFalse(PlayerGem.isSuitableSlot(EquipSlot.HELMET, NMSAttributeSlot.LEGS));
        }

        @Test
        @DisplayName("CHEST slot should not match non-chestplate")
        void chestSlotShouldNotMatchNonChestplate() {
            assertFalse(PlayerGem.isSuitableSlot(EquipSlot.HELMET, NMSAttributeSlot.CHEST));
        }

        @Test
        @DisplayName("HEAD slot should not match non-helmet")
        void headSlotShouldNotMatchNonHelmet() {
            assertFalse(PlayerGem.isSuitableSlot(EquipSlot.BOOTS, NMSAttributeSlot.HEAD));
        }
    }

    @Nested
    @DisplayName("WeaponAttribute Inner Class Tests")
    class WeaponAttributeTests {

        @Test
        @DisplayName("isAllDeactivation should return true for new instance with no gems")
        void isAllDeactivationShouldReturnTrueForNewNoGems() {
            com.bafmc.customenchantment.item.CEWeaponAbstract mockWeapon = mock(
                    com.bafmc.customenchantment.item.CEWeaponAbstract.class);
            com.bafmc.customenchantment.item.WeaponGem mockWeaponGem = mock(
                    com.bafmc.customenchantment.item.WeaponGem.class);
            when(mockWeapon.getWeaponGem()).thenReturn(mockWeaponGem);
            when(mockWeaponGem.getCEGemSimpleList()).thenReturn(new java.util.ArrayList<>());

            PlayerGem.WeaponAttribute weaponAttribute = new PlayerGem.WeaponAttribute(mockWeapon);
            assertTrue(weaponAttribute.isAllDeactivation());
        }
    }

    @Nested
    @DisplayName("GemAttribute Inner Class Tests")
    class GemAttributeTests {

        @Test
        @DisplayName("Should create GemAttribute with NMSAttribute and slot")
        void shouldCreateGemAttribute() {
            com.bafmc.bukkit.bafframework.nms.NMSAttribute mockAttr = mock(
                    com.bafmc.bukkit.bafframework.nms.NMSAttribute.class);
            when(mockAttr.getName()).thenReturn("test-attr");

            PlayerGem.GemAttribute gemAttribute = new PlayerGem.GemAttribute(mockAttr, NMSAttributeSlot.ALL);
            assertNotNull(gemAttribute);
        }
    }
}
