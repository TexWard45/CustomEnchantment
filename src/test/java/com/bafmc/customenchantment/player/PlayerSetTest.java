package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.utils.EquipSlot;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerSet")
@ExtendWith(MockitoExtension.class)
class PlayerSetTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private PlayerTemporaryStorage mockTempStorage;

    private PlayerSet playerSet;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        lenient().when(mockCEPlayer.getTemporaryStorage()).thenReturn(mockTempStorage);
        playerSet = new PlayerSet(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerSet);
            assertSame(mockCEPlayer, playerSet.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerSet instanceof CEPlayerExpansion);
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should remove set_ keys from temporary storage")
        void onJoinShouldRemoveSetKeys() {
            playerSet.onJoin();
            verify(mockTempStorage).removeStartsWith("set_");
        }

        @Test
        @DisplayName("onQuit should remove set_ keys from temporary storage")
        void onQuitShouldRemoveSetKeys() {
            playerSet.onQuit();
            verify(mockTempStorage).removeStartsWith("set_");
        }
    }

    @Nested
    @DisplayName("getSetPrefix Tests")
    class GetSetPrefixTests {

        @Test
        @DisplayName("Should return correct set prefix format")
        void shouldReturnCorrectFormat() {
            assertEquals("set_dragon_armor_point", playerSet.getSetPrefix("dragon", "armor_point"));
        }

        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            assertEquals("set__", playerSet.getSetPrefix("", ""));
        }
    }

    @Nested
    @DisplayName("addSetPoint Tests")
    class AddSetPointTests {

        @Test
        @DisplayName("Should increment point from 0 to 1")
        void shouldIncrementPointFrom0To1() {
            Map<String, Object> map = new HashMap<>();
            playerSet.addSetPoint(map, "set_test_point");
            assertEquals(1, map.get("set_test_point"));
        }

        @Test
        @DisplayName("Should increment existing point")
        void shouldIncrementExistingPoint() {
            Map<String, Object> map = new HashMap<>();
            map.put("set_test_point", 3);
            playerSet.addSetPoint(map, "set_test_point");
            assertEquals(4, map.get("set_test_point"));
        }
    }

    @Nested
    @DisplayName("setSetPoint Tests")
    class SetSetPointTests {

        @Test
        @DisplayName("Should set point to specific value")
        void shouldSetPointToSpecificValue() {
            Map<String, Object> map = new HashMap<>();
            playerSet.setSetPoint(map, "set_test_level", 5);
            assertEquals(5, map.get("set_test_level"));
        }

        @Test
        @DisplayName("Should overwrite existing value")
        void shouldOverwriteExistingValue() {
            Map<String, Object> map = new HashMap<>();
            map.put("set_test_level", 3);
            playerSet.setSetPoint(map, "set_test_level", 7);
            assertEquals(7, map.get("set_test_level"));
        }
    }

    @Nested
    @DisplayName("addSetToLevel Tests")
    class AddSetToLevelTests {

        @Test
        @DisplayName("Should add points for each level up to specified level")
        void shouldAddPointsForEachLevel() {
            Map<String, Object> map = new HashMap<>();
            playerSet.addSetToLevel(map, "set_dragon_armor_point", 3);

            assertEquals(1, map.get("set_dragon_armor_point_1"));
            assertEquals(1, map.get("set_dragon_armor_point_2"));
            assertEquals(1, map.get("set_dragon_armor_point_3"));
        }

        @Test
        @DisplayName("Should handle level 1")
        void shouldHandleLevel1() {
            Map<String, Object> map = new HashMap<>();
            playerSet.addSetToLevel(map, "set_test", 1);

            assertEquals(1, map.get("set_test_1"));
            assertNull(map.get("set_test_2"));
        }
    }

    @Nested
    @DisplayName("addSet Tests")
    class AddSetTests {

        @Test
        @DisplayName("Should add armor point for HELMET slot")
        void shouldAddArmorPointForHelmet() {
            Map<String, Object> map = new HashMap<>();
            playerSet.addSet(map, EquipSlot.HELMET, "dragon", 1);

            assertEquals(1, map.get("set_dragon_armor_point_1"));
            assertEquals(1, map.get("set_dragon_armor_point"));
            assertEquals(1, map.get("set_dragon_point"));
        }

        @Test
        @DisplayName("Should add weapon point for MAINHAND slot")
        void shouldAddWeaponPointForMainhand() {
            Map<String, Object> map = new HashMap<>();
            playerSet.addSet(map, EquipSlot.MAINHAND, "dragon", 1);

            assertEquals(1, map.get("set_dragon_weapon_point_1"));
            assertEquals(1, map.get("set_dragon_weapon_point"));
            assertEquals(1, map.get("set_dragon_mainweapon_point_1"));
            assertEquals(1, map.get("set_dragon_mainweapon_point"));
            assertEquals(1, map.get("set_dragon_point"));
        }

        @Test
        @DisplayName("Should add offweapon point for OFFHAND slot")
        void shouldAddOffweaponPointForOffhand() {
            Map<String, Object> map = new HashMap<>();
            playerSet.addSet(map, EquipSlot.OFFHAND, "dragon", 1);

            assertEquals(1, map.get("set_dragon_weapon_point_1"));
            assertEquals(1, map.get("set_dragon_weapon_point"));
            assertEquals(1, map.get("set_dragon_offweapon_point_1"));
            assertEquals(1, map.get("set_dragon_offweapon_point"));
            assertEquals(1, map.get("set_dragon_point"));
        }

        @Test
        @DisplayName("Should add general point for all slot types")
        void shouldAddGeneralPointForAllSlots() {
            Map<String, Object> map = new HashMap<>();
            playerSet.addSet(map, EquipSlot.HELMET, "test", 1);
            assertEquals(1, map.get("set_test_point"));
        }
    }

    @Nested
    @DisplayName("updateHighestSetArmorLevel Tests")
    class UpdateHighestSetArmorLevelTests {

        @Test
        @DisplayName("Should set level when 4 armor pieces match")
        void shouldSetLevelWhen4ArmorPiecesMatch() {
            Map<String, Object> map = new HashMap<>();
            map.put("set_dragon_armor_point_1", 4);

            Map<String, Integer> highestSetLevel = new HashMap<>();
            highestSetLevel.put("dragon", 1);

            playerSet.updateHighestSetArmorLevel(map, highestSetLevel);

            assertEquals(1, map.get("set_dragon_level"));
        }

        @Test
        @DisplayName("Should not set level when less than 4 armor pieces")
        void shouldNotSetLevelWhenLessThan4ArmorPieces() {
            Map<String, Object> map = new HashMap<>();
            map.put("set_dragon_armor_point_1", 3);

            Map<String, Integer> highestSetLevel = new HashMap<>();
            highestSetLevel.put("dragon", 1);

            playerSet.updateHighestSetArmorLevel(map, highestSetLevel);

            assertNull(map.get("set_dragon_level"));
        }

        @Test
        @DisplayName("Should set highest matching level")
        void shouldSetHighestMatchingLevel() {
            Map<String, Object> map = new HashMap<>();
            map.put("set_dragon_armor_point_1", 4);
            map.put("set_dragon_armor_point_2", 4);
            map.put("set_dragon_armor_point_3", 3);

            Map<String, Integer> highestSetLevel = new HashMap<>();
            highestSetLevel.put("dragon", 3);

            playerSet.updateHighestSetArmorLevel(map, highestSetLevel);

            assertEquals(2, map.get("set_dragon_level"));
        }
    }
}
