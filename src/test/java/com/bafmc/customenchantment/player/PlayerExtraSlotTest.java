package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.item.CEWeaponAbstract;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerExtraSlot")
@ExtendWith(MockitoExtension.class)
class PlayerExtraSlotTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    private PlayerExtraSlot playerExtraSlot;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        playerExtraSlot = new PlayerExtraSlot(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerExtraSlot);
            assertSame(mockCEPlayer, playerExtraSlot.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerExtraSlot instanceof CEPlayerExpansion);
        }

        @Test
        @DisplayName("Should initialize empty previous map")
        void shouldInitializeEmptyPreviousMap() {
            assertTrue(playerExtraSlot.getPreviousExtraSlotActivateMap().isEmpty());
        }

        @Test
        @DisplayName("disableExtraSlot should be false initially")
        void disableExtraSlotShouldBeFalseInitially() {
            assertFalse(playerExtraSlot.isDisableExtraSlot());
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should not throw")
        void onJoinShouldNotThrow() {
            assertDoesNotThrow(() -> playerExtraSlot.onJoin());
        }

        @Test
        @DisplayName("onQuit should not throw")
        void onQuitShouldNotThrow() {
            assertDoesNotThrow(() -> playerExtraSlot.onQuit());
        }
    }

    @Nested
    @DisplayName("Setter and Getter Tests")
    class SetterGetterTests {

        @Test
        @DisplayName("Should set and get disableExtraSlot")
        void shouldSetAndGetDisableExtraSlot() {
            playerExtraSlot.setDisableExtraSlot(true);
            assertTrue(playerExtraSlot.isDisableExtraSlot());
        }

        @Test
        @DisplayName("Should set and get previous map")
        void shouldSetAndGetPreviousMap() {
            Map<EquipSlot, CEWeaponAbstract> map = new HashMap<>();
            CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
            map.put(EquipSlot.MAINHAND, mockWeapon);

            playerExtraSlot.setPreviousExtraSlotActivateMap(map);
            assertEquals(1, playerExtraSlot.getPreviousExtraSlotActivateMap().size());
        }
    }

    @Nested
    @DisplayName("clear Tests")
    class ClearTests {

        @Test
        @DisplayName("Should clear previous map")
        void shouldClearPreviousMap() {
            Map<EquipSlot, CEWeaponAbstract> map = new HashMap<>();
            map.put(EquipSlot.MAINHAND, mock(CEWeaponAbstract.class));
            playerExtraSlot.setPreviousExtraSlotActivateMap(map);

            playerExtraSlot.clear();
            assertTrue(playerExtraSlot.getPreviousExtraSlotActivateMap().isEmpty());
        }
    }

    @Nested
    @DisplayName("ExtraSlotDiff Tests")
    class ExtraSlotDiffTests {

        @Test
        @DisplayName("Should create ExtraSlotDiff with all maps")
        void shouldCreateExtraSlotDiff() {
            Map<EquipSlot, CEWeaponAbstract> extraSlotMap = new HashMap<>();
            Map<EquipSlot, CEWeaponAbstract> notExistsMap = new HashMap<>();
            Map<EquipSlot, Boolean> oldMap = new HashMap<>();

            CEWeaponAbstract weapon = mock(CEWeaponAbstract.class);
            extraSlotMap.put(EquipSlot.MAINHAND, weapon);
            oldMap.put(EquipSlot.MAINHAND, false);

            PlayerExtraSlot.ExtraSlotDiff diff = new PlayerExtraSlot.ExtraSlotDiff(
                    extraSlotMap, notExistsMap, oldMap);

            assertNotNull(diff);
            assertEquals(weapon, diff.getExtraSlot(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("getEquipSlotList should return list of equip slots")
        void getEquipSlotListShouldReturnList() {
            Map<EquipSlot, CEWeaponAbstract> extraSlotMap = new HashMap<>();
            extraSlotMap.put(EquipSlot.MAINHAND, mock(CEWeaponAbstract.class));
            Map<EquipSlot, CEWeaponAbstract> notExistsMap = new HashMap<>();
            Map<EquipSlot, Boolean> oldMap = new HashMap<>();
            oldMap.put(EquipSlot.MAINHAND, true);

            PlayerExtraSlot.ExtraSlotDiff diff = new PlayerExtraSlot.ExtraSlotDiff(
                    extraSlotMap, notExistsMap, oldMap);

            assertEquals(1, diff.getEquipSlotList().size());
            assertTrue(diff.getEquipSlotList().contains(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("isDifferent for slot should return true when old flag is false")
        void isDifferentForSlotShouldReturnTrue() {
            Map<EquipSlot, CEWeaponAbstract> extraSlotMap = new HashMap<>();
            extraSlotMap.put(EquipSlot.MAINHAND, mock(CEWeaponAbstract.class));
            Map<EquipSlot, CEWeaponAbstract> notExistsMap = new HashMap<>();
            Map<EquipSlot, Boolean> oldMap = new HashMap<>();
            oldMap.put(EquipSlot.MAINHAND, false); // Not old = different

            PlayerExtraSlot.ExtraSlotDiff diff = new PlayerExtraSlot.ExtraSlotDiff(
                    extraSlotMap, notExistsMap, oldMap);

            assertTrue(diff.isDifferent(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("isDifferent for slot should return false when old flag is true")
        void isDifferentForSlotShouldReturnFalse() {
            Map<EquipSlot, CEWeaponAbstract> extraSlotMap = new HashMap<>();
            extraSlotMap.put(EquipSlot.MAINHAND, mock(CEWeaponAbstract.class));
            Map<EquipSlot, CEWeaponAbstract> notExistsMap = new HashMap<>();
            Map<EquipSlot, Boolean> oldMap = new HashMap<>();
            oldMap.put(EquipSlot.MAINHAND, true); // Old = not different

            PlayerExtraSlot.ExtraSlotDiff diff = new PlayerExtraSlot.ExtraSlotDiff(
                    extraSlotMap, notExistsMap, oldMap);

            assertFalse(diff.isDifferent(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("isDifferent() should return true when any slot is different")
        void isDifferentShouldReturnTrueWhenAnyDifferent() {
            Map<EquipSlot, CEWeaponAbstract> extraSlotMap = new HashMap<>();
            extraSlotMap.put(EquipSlot.MAINHAND, mock(CEWeaponAbstract.class));
            extraSlotMap.put(EquipSlot.OFFHAND, mock(CEWeaponAbstract.class));
            Map<EquipSlot, CEWeaponAbstract> notExistsMap = new HashMap<>();
            Map<EquipSlot, Boolean> oldMap = new HashMap<>();
            oldMap.put(EquipSlot.MAINHAND, true);
            oldMap.put(EquipSlot.OFFHAND, false);

            PlayerExtraSlot.ExtraSlotDiff diff = new PlayerExtraSlot.ExtraSlotDiff(
                    extraSlotMap, notExistsMap, oldMap);

            assertTrue(diff.isDifferent());
        }

        @Test
        @DisplayName("isDifferent() should return false when all slots are same")
        void isDifferentShouldReturnFalseWhenAllSame() {
            Map<EquipSlot, CEWeaponAbstract> extraSlotMap = new HashMap<>();
            extraSlotMap.put(EquipSlot.MAINHAND, mock(CEWeaponAbstract.class));
            Map<EquipSlot, CEWeaponAbstract> notExistsMap = new HashMap<>();
            Map<EquipSlot, Boolean> oldMap = new HashMap<>();
            oldMap.put(EquipSlot.MAINHAND, true);

            PlayerExtraSlot.ExtraSlotDiff diff = new PlayerExtraSlot.ExtraSlotDiff(
                    extraSlotMap, notExistsMap, oldMap);

            assertFalse(diff.isDifferent());
        }

        @Test
        @DisplayName("getOnlyDifferentArtifactMap should return only different entries")
        void getOnlyDifferentArtifactMapShouldReturnOnlyDifferent() {
            CEWeaponAbstract weapon1 = mock(CEWeaponAbstract.class);
            CEWeaponAbstract weapon2 = mock(CEWeaponAbstract.class);
            Map<EquipSlot, CEWeaponAbstract> extraSlotMap = new HashMap<>();
            extraSlotMap.put(EquipSlot.MAINHAND, weapon1);
            extraSlotMap.put(EquipSlot.OFFHAND, weapon2);
            Map<EquipSlot, CEWeaponAbstract> notExistsMap = new HashMap<>();
            Map<EquipSlot, Boolean> oldMap = new HashMap<>();
            oldMap.put(EquipSlot.MAINHAND, true);  // same
            oldMap.put(EquipSlot.OFFHAND, false);   // different

            PlayerExtraSlot.ExtraSlotDiff diff = new PlayerExtraSlot.ExtraSlotDiff(
                    extraSlotMap, notExistsMap, oldMap);

            Map<EquipSlot, CEWeaponAbstract> result = diff.getOnlyDifferentArtifactMap();
            assertEquals(1, result.size());
            assertTrue(result.containsKey(EquipSlot.OFFHAND));
            assertSame(weapon2, result.get(EquipSlot.OFFHAND));
        }
    }

    @Nested
    @DisplayName("updateArtifactDiff Tests")
    class UpdateArtifactDiffTests {

        @Test
        @DisplayName("Should update previous map from diff")
        void shouldUpdatePreviousMapFromDiff() {
            CEWeaponAbstract weapon = mock(CEWeaponAbstract.class);
            Map<EquipSlot, CEWeaponAbstract> extraSlotMap = new HashMap<>();
            extraSlotMap.put(EquipSlot.MAINHAND, weapon);
            Map<EquipSlot, CEWeaponAbstract> notExistsMap = new HashMap<>();
            Map<EquipSlot, Boolean> oldMap = new HashMap<>();
            oldMap.put(EquipSlot.MAINHAND, false);

            PlayerExtraSlot.ExtraSlotDiff diff = new PlayerExtraSlot.ExtraSlotDiff(
                    extraSlotMap, notExistsMap, oldMap);

            playerExtraSlot.updateArtifactDiff(diff);

            assertEquals(1, playerExtraSlot.getPreviousExtraSlotActivateMap().size());
            assertSame(weapon, playerExtraSlot.getPreviousExtraSlotActivateMap().get(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("Should clear previous map before updating")
        void shouldClearPreviousMapBeforeUpdating() {
            // Add something to previous map first
            Map<EquipSlot, CEWeaponAbstract> initialMap = new HashMap<>();
            initialMap.put(EquipSlot.OFFHAND, mock(CEWeaponAbstract.class));
            playerExtraSlot.setPreviousExtraSlotActivateMap(initialMap);

            // Now update with diff that only has MAINHAND
            Map<EquipSlot, CEWeaponAbstract> extraSlotMap = new HashMap<>();
            extraSlotMap.put(EquipSlot.MAINHAND, mock(CEWeaponAbstract.class));
            Map<EquipSlot, CEWeaponAbstract> notExistsMap = new HashMap<>();
            Map<EquipSlot, Boolean> oldMap = new HashMap<>();
            oldMap.put(EquipSlot.MAINHAND, false);

            PlayerExtraSlot.ExtraSlotDiff diff = new PlayerExtraSlot.ExtraSlotDiff(
                    extraSlotMap, notExistsMap, oldMap);

            playerExtraSlot.updateArtifactDiff(diff);

            // OFFHAND should be gone, only MAINHAND remains
            assertEquals(1, playerExtraSlot.getPreviousExtraSlotActivateMap().size());
            assertTrue(playerExtraSlot.getPreviousExtraSlotActivateMap().containsKey(EquipSlot.MAINHAND));
        }
    }
}
