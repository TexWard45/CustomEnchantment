package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.player.mining.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerModule")
@ExtendWith(MockitoExtension.class)
class PlayerModuleTest {

    @Mock
    private CustomEnchantment mockPlugin;

    private PlayerModule playerModule;

    @BeforeEach
    void setUp() {
        // Clear static registers to avoid test interference
        CEPlayerExpansionRegister.list.clear();
        PlayerSpecialMiningRegister.list.clear();
        playerModule = new PlayerModule(mockPlugin);
    }

    @AfterEach
    void tearDown() {
        CEPlayerExpansionRegister.list.clear();
        PlayerSpecialMiningRegister.list.clear();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with plugin")
        void shouldCreateInstanceWithPlugin() {
            assertNotNull(playerModule);
        }
    }

    @Nested
    @DisplayName("onEnable Tests")
    class OnEnableTests {

        @Test
        @DisplayName("Should not throw on enable")
        void shouldNotThrowOnEnable() {
            assertDoesNotThrow(() -> playerModule.onEnable());
        }

        @Test
        @DisplayName("Should register player expansions on enable")
        void shouldRegisterPlayerExpansions() {
            playerModule.onEnable();

            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerStorage.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerEquipment.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerTemporaryStorage.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerVanillaAttribute.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerCustomAttribute.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerPotion.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerSet.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerCECooldown.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerCEManager.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerAbility.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerExtraSlot.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerMobBonus.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerBlockBonus.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerSpecialMining.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerNameTag.class));
            assertTrue(CEPlayerExpansionRegister.list.contains(PlayerGem.class));
        }

        @Test
        @DisplayName("Should register 16 player expansions")
        void shouldRegister16PlayerExpansions() {
            playerModule.onEnable();
            assertEquals(16, CEPlayerExpansionRegister.list.size());
        }

        @Test
        @DisplayName("Should register special mining types on enable")
        void shouldRegisterSpecialMiningTypes() {
            playerModule.onEnable();

            assertTrue(PlayerSpecialMiningRegister.list.contains(BlockDropBonusSpecialMine.class));
            assertTrue(PlayerSpecialMiningRegister.list.contains(ExplosionSpecialMine.class));
            assertTrue(PlayerSpecialMiningRegister.list.contains(VeinSpecialMine.class));
            assertTrue(PlayerSpecialMiningRegister.list.contains(FurnaceSpecialMine.class));
            assertTrue(PlayerSpecialMiningRegister.list.contains(TelepathySpecialMine.class));
            assertTrue(PlayerSpecialMiningRegister.list.contains(AutoSellSpecialMine.class));
        }

        @Test
        @DisplayName("Should register 6 special mining types")
        void shouldRegister6SpecialMiningTypes() {
            playerModule.onEnable();
            assertEquals(6, PlayerSpecialMiningRegister.list.size());
        }
    }

    @Nested
    @DisplayName("setupPlayerExpansions Tests")
    class SetupPlayerExpansionsTests {

        @Test
        @DisplayName("Should register expansions in specific order")
        void shouldRegisterExpansionsInOrder() {
            playerModule.setupPlayerExpansions();

            assertEquals(PlayerStorage.class, CEPlayerExpansionRegister.list.get(0));
            assertEquals(PlayerEquipment.class, CEPlayerExpansionRegister.list.get(1));
            assertEquals(PlayerTemporaryStorage.class, CEPlayerExpansionRegister.list.get(2));
            assertEquals(PlayerVanillaAttribute.class, CEPlayerExpansionRegister.list.get(3));
            assertEquals(PlayerCustomAttribute.class, CEPlayerExpansionRegister.list.get(4));
            assertEquals(PlayerPotion.class, CEPlayerExpansionRegister.list.get(5));
            assertEquals(PlayerSet.class, CEPlayerExpansionRegister.list.get(6));
            assertEquals(PlayerCECooldown.class, CEPlayerExpansionRegister.list.get(7));
            assertEquals(PlayerCEManager.class, CEPlayerExpansionRegister.list.get(8));
            assertEquals(PlayerAbility.class, CEPlayerExpansionRegister.list.get(9));
            assertEquals(PlayerExtraSlot.class, CEPlayerExpansionRegister.list.get(10));
            assertEquals(PlayerMobBonus.class, CEPlayerExpansionRegister.list.get(11));
            assertEquals(PlayerBlockBonus.class, CEPlayerExpansionRegister.list.get(12));
            assertEquals(PlayerSpecialMining.class, CEPlayerExpansionRegister.list.get(13));
            assertEquals(PlayerNameTag.class, CEPlayerExpansionRegister.list.get(14));
            assertEquals(PlayerGem.class, CEPlayerExpansionRegister.list.get(15));
        }

        @Test
        @DisplayName("Should not register duplicates if called twice")
        void shouldNotRegisterDuplicatesIfCalledTwice() {
            playerModule.setupPlayerExpansions();
            playerModule.setupPlayerExpansions();

            assertEquals(16, CEPlayerExpansionRegister.list.size());
        }
    }

    @Nested
    @DisplayName("setupPlayerSpecialMining Tests")
    class SetupPlayerSpecialMiningTests {

        @Test
        @DisplayName("Should register all mining types in order")
        void shouldRegisterAllMiningTypesInOrder() {
            playerModule.setupPlayerSpecialMining();

            assertEquals(BlockDropBonusSpecialMine.class, PlayerSpecialMiningRegister.list.get(0));
            assertEquals(ExplosionSpecialMine.class, PlayerSpecialMiningRegister.list.get(1));
            assertEquals(VeinSpecialMine.class, PlayerSpecialMiningRegister.list.get(2));
            assertEquals(FurnaceSpecialMine.class, PlayerSpecialMiningRegister.list.get(3));
            assertEquals(TelepathySpecialMine.class, PlayerSpecialMiningRegister.list.get(4));
            assertEquals(AutoSellSpecialMine.class, PlayerSpecialMiningRegister.list.get(5));
        }
    }
}
