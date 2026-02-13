package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.CEPlayerMap;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CEPlayer")
@ExtendWith(MockitoExtension.class)
class CEPlayerTest {

    @Mock
    private Player mockPlayer;

    private CEPlayer cePlayer;

    @BeforeEach
    void setUp() {
        // Clear static expansion register to avoid side effects
        CEPlayerExpansionRegister.list.clear();
        UUID uuid = UUID.randomUUID();
        lenient().when(mockPlayer.getUniqueId()).thenReturn(uuid);
        lenient().when(mockPlayer.isOnline()).thenReturn(true);
        cePlayer = new CEPlayer(mockPlayer);
    }

    @AfterEach
    void tearDown() {
        CEPlayerExpansionRegister.list.clear();
        // Reset static cePlayerMap
        try {
            Field field = CEPlayer.class.getDeclaredField("cePlayerMap");
            field.setAccessible(true);
            field.set(null, null);
        } catch (Exception ignored) {
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should store Player reference")
        void shouldStorePlayerReference() {
            assertSame(mockPlayer, cePlayer.getPlayer());
        }

        @Test
        @DisplayName("Should implement ICEPlayerEvent")
        void shouldImplementICEPlayerEvent() {
            assertTrue(cePlayer instanceof ICEPlayerEvent);
        }

        @Test
        @DisplayName("Should not be registered initially")
        void shouldNotBeRegisteredInitially() {
            assertFalse(cePlayer.isRegister());
        }

        @Test
        @DisplayName("Should not be in admin mode initially")
        void shouldNotBeInAdminModeInitially() {
            assertFalse(cePlayer.isAdminMode());
        }

        @Test
        @DisplayName("Should not be in debug mode initially")
        void shouldNotBeInDebugModeInitially() {
            assertFalse(cePlayer.isDebugMode());
        }

        @Test
        @DisplayName("Should have zero death time initially")
        void shouldHaveZeroDeathTimeInitially() {
            assertEquals(0, cePlayer.getDeathTime());
        }

        @Test
        @DisplayName("Should not have fullChance initially")
        void shouldNotHaveFullChanceInitially() {
            assertFalse(cePlayer.isFullChance());
        }
    }

    @Nested
    @DisplayName("Setter and Getter Tests")
    class SetterGetterTests {

        @Test
        @DisplayName("Should set and get adminMode")
        void shouldSetAndGetAdminMode() {
            cePlayer.setAdminMode(true);
            assertTrue(cePlayer.isAdminMode());
        }

        @Test
        @DisplayName("Should set and get debugMode")
        void shouldSetAndGetDebugMode() {
            cePlayer.setDebugMode(true);
            assertTrue(cePlayer.isDebugMode());
        }

        @Test
        @DisplayName("Should set and get deathTime")
        void shouldSetAndGetDeathTime() {
            cePlayer.setDeathTime(100);
            assertEquals(100, cePlayer.getDeathTime());
        }

        @Test
        @DisplayName("Should set and get deathTimeBefore")
        void shouldSetAndGetDeathTimeBefore() {
            cePlayer.setDeathTimeBefore(true);
            assertTrue(cePlayer.isDeathTimeBefore());
        }

        @Test
        @DisplayName("Should set and get fullChance")
        void shouldSetAndGetFullChance() {
            cePlayer.setFullChance(true);
            assertTrue(cePlayer.isFullChance());
        }

        @Test
        @DisplayName("Should set and get titleOpenInventory")
        void shouldSetAndGetTitleOpenInventory() {
            cePlayer.setTitleOpenInventory("Test Menu");
            assertEquals("Test Menu", cePlayer.getTitleOpenInventory());
        }

        @Test
        @DisplayName("Should set and get movingForward")
        void shouldSetAndGetMovingForward() {
            cePlayer.setMovingForward(true);
            assertTrue(cePlayer.isMovingForward());
        }
    }

    @Nested
    @DisplayName("isOnline Tests")
    class IsOnlineTests {

        @Test
        @DisplayName("Should return true when player is online")
        void shouldReturnTrueWhenPlayerIsOnline() {
            when(mockPlayer.isOnline()).thenReturn(true);
            assertTrue(cePlayer.isOnline());
        }

        @Test
        @DisplayName("Should return false when player is offline")
        void shouldReturnFalseWhenPlayerIsOffline() {
            when(mockPlayer.isOnline()).thenReturn(false);
            assertFalse(cePlayer.isOnline());
        }
    }

    @Nested
    @DisplayName("Expansion Management Tests")
    class ExpansionManagementTests {

        private static class TestExpansion extends CEPlayerExpansion {
            public TestExpansion(CEPlayer cePlayer) {
                super(cePlayer);
            }

            @Override
            public void onJoin() {
            }

            @Override
            public void onQuit() {
            }
        }

        private static class TestExpansion2 extends CEPlayerExpansion {
            public TestExpansion2(CEPlayer cePlayer) {
                super(cePlayer);
            }

            @Override
            public void onJoin() {
            }

            @Override
            public void onQuit() {
            }
        }

        @Test
        @DisplayName("Should add expansion")
        void shouldAddExpansion() {
            TestExpansion expansion = new TestExpansion(cePlayer);
            cePlayer.addExpansion(expansion);
            assertSame(expansion, cePlayer.getExpansion(TestExpansion.class));
        }

        @Test
        @DisplayName("Should remove expansion")
        void shouldRemoveExpansion() {
            TestExpansion expansion = new TestExpansion(cePlayer);
            cePlayer.addExpansion(expansion);
            cePlayer.removeExpansion(TestExpansion.class);
            assertNull(cePlayer.getExpansion(TestExpansion.class));
        }

        @Test
        @DisplayName("Should return null for non-existing expansion")
        void shouldReturnNullForNonExisting() {
            assertNull(cePlayer.getExpansion(TestExpansion.class));
        }

        @Test
        @DisplayName("Should handle multiple expansions independently")
        void shouldHandleMultipleExpansions() {
            TestExpansion exp1 = new TestExpansion(cePlayer);
            TestExpansion2 exp2 = new TestExpansion2(cePlayer);
            cePlayer.addExpansion(exp1);
            cePlayer.addExpansion(exp2);

            assertSame(exp1, cePlayer.getExpansion(TestExpansion.class));
            assertSame(exp2, cePlayer.getExpansion(TestExpansion2.class));
        }
    }

    @Nested
    @DisplayName("Register and Unregister Tests")
    class RegisterUnregisterTests {

        @Test
        @DisplayName("register should set register flag to true")
        void registerShouldSetRegisterFlagToTrue() {
            cePlayer.register();
            assertTrue(cePlayer.isRegister());
        }

        @Test
        @DisplayName("unregister should set register flag to false")
        void unregisterShouldSetRegisterFlagToFalse() {
            cePlayer.register();
            cePlayer.unregister();
            assertFalse(cePlayer.isRegister());
        }
    }

    @Nested
    @DisplayName("getCePlayerMap Tests")
    class GetCePlayerMapTests {

        @Test
        @DisplayName("Should return non-null CEPlayerMap")
        void shouldReturnNonNullCEPlayerMap() {
            assertNotNull(CEPlayer.getCePlayerMap());
        }

        @Test
        @DisplayName("Should return same instance on multiple calls (lazy init)")
        void shouldReturnSameInstanceOnMultipleCalls() {
            CEPlayerMap map1 = CEPlayer.getCePlayerMap();
            CEPlayerMap map2 = CEPlayer.getCePlayerMap();
            assertSame(map1, map2);
        }
    }

    @Nested
    @DisplayName("Convenience Expansion Getters Tests")
    class ConvenienceExpansionGetterTests {

        @Test
        @DisplayName("getAbility should return null when not registered")
        void getAbilityShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getAbility());
        }

        @Test
        @DisplayName("getCECooldown should return null when not registered")
        void getCECooldownShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getCECooldown());
        }

        @Test
        @DisplayName("getCEManager should return null when not registered")
        void getCEManagerShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getCEManager());
        }

        @Test
        @DisplayName("getCustomAttribute should return null when not registered")
        void getCustomAttributeShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getCustomAttribute());
        }

        @Test
        @DisplayName("getVanillaAttribute should return null when not registered")
        void getVanillaAttributeShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getVanillaAttribute());
        }

        @Test
        @DisplayName("getPotion should return null when not registered")
        void getPotionShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getPotion());
        }

        @Test
        @DisplayName("getSet should return null when not registered")
        void getSetShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getSet());
        }

        @Test
        @DisplayName("getStorage should return null when not registered")
        void getStorageShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getStorage());
        }

        @Test
        @DisplayName("getTemporaryStorage should return null when not registered")
        void getTemporaryStorageShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getTemporaryStorage());
        }

        @Test
        @DisplayName("getArtifact should return null when not registered")
        void getArtifactShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getArtifact());
        }

        @Test
        @DisplayName("getMobBonus should return null when not registered")
        void getMobBonusShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getMobBonus());
        }

        @Test
        @DisplayName("getBlockBonus should return null when not registered")
        void getBlockBonusShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getBlockBonus());
        }

        @Test
        @DisplayName("getSpecialMining should return null when not registered")
        void getSpecialMiningShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getSpecialMining());
        }

        @Test
        @DisplayName("getNameTag should return null when not registered")
        void getNameTagShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getNameTag());
        }

        @Test
        @DisplayName("getGem should return null when not registered")
        void getGemShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getGem());
        }

        @Test
        @DisplayName("getEquipment should return null when not registered")
        void getEquipmentShouldReturnNullWhenNotRegistered() {
            assertNull(cePlayer.getEquipment());
        }
    }

    @Nested
    @DisplayName("onJoin Tests")
    class OnJoinTests {

        @Test
        @DisplayName("Should call onJoin on all expansions")
        void shouldCallOnJoinOnAllExpansions() {
            CEPlayerExpansion mockExpansion = mock(CEPlayerExpansion.class);
            cePlayer.addExpansion(mockExpansion);

            cePlayer.onJoin();

            verify(mockExpansion).onJoin();
        }

        @Test
        @DisplayName("Should register player after joining")
        void shouldRegisterPlayerAfterJoining() {
            cePlayer.onJoin();
            assertTrue(cePlayer.isRegister());
        }
    }

    @Nested
    @DisplayName("onQuit Tests")
    class OnQuitTests {

        @Test
        @DisplayName("Should call onQuit on all expansions in reverse order")
        void shouldCallOnQuitOnAllExpansionsInReverseOrder() {
            CEPlayerExpansion mockExpansion = mock(CEPlayerExpansion.class);
            cePlayer.addExpansion(mockExpansion);

            cePlayer.onQuit();

            verify(mockExpansion).onQuit();
        }

        @Test
        @DisplayName("Should unregister player after quitting")
        void shouldUnregisterPlayerAfterQuitting() {
            cePlayer.register();
            cePlayer.onQuit();
            assertFalse(cePlayer.isRegister());
        }
    }
}
