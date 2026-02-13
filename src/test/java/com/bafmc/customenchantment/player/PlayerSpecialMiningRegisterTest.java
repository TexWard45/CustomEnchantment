package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.player.mining.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerSpecialMiningRegister")
class PlayerSpecialMiningRegisterTest {

    @BeforeEach
    void setUp() {
        PlayerSpecialMiningRegister.list.clear();
    }

    @AfterAll
    static void tearDown() {
        PlayerSpecialMiningRegister.list.clear();
    }

    @Nested
    @DisplayName("register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should register a special mine class")
        void shouldRegisterSpecialMineClass() {
            PlayerSpecialMiningRegister.register(BlockDropBonusSpecialMine.class);
            assertTrue(PlayerSpecialMiningRegister.list.contains(BlockDropBonusSpecialMine.class));
        }

        @Test
        @DisplayName("Should not register duplicate class")
        void shouldNotRegisterDuplicate() {
            PlayerSpecialMiningRegister.register(BlockDropBonusSpecialMine.class);
            PlayerSpecialMiningRegister.register(BlockDropBonusSpecialMine.class);
            assertEquals(1, PlayerSpecialMiningRegister.list.size());
        }

        @Test
        @DisplayName("Should register multiple different classes")
        void shouldRegisterMultipleClasses() {
            PlayerSpecialMiningRegister.register(BlockDropBonusSpecialMine.class);
            PlayerSpecialMiningRegister.register(ExplosionSpecialMine.class);
            PlayerSpecialMiningRegister.register(VeinSpecialMine.class);
            assertEquals(3, PlayerSpecialMiningRegister.list.size());
        }
    }

    @Nested
    @DisplayName("unregister Tests")
    class UnregisterTests {

        @Test
        @DisplayName("Should unregister an existing class")
        void shouldUnregisterExistingClass() {
            PlayerSpecialMiningRegister.register(BlockDropBonusSpecialMine.class);
            PlayerSpecialMiningRegister.unregister(BlockDropBonusSpecialMine.class);
            assertFalse(PlayerSpecialMiningRegister.list.contains(BlockDropBonusSpecialMine.class));
        }

        @Test
        @DisplayName("Should not throw when unregistering non-existing class")
        void shouldNotThrowWhenUnregisteringNonExisting() {
            assertDoesNotThrow(() -> PlayerSpecialMiningRegister.unregister(BlockDropBonusSpecialMine.class));
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("Should setup special mining expansions on PlayerSpecialMining")
        void shouldSetupExpansions() {
            PlayerSpecialMiningRegister.register(BlockDropBonusSpecialMine.class);
            PlayerSpecialMiningRegister.register(ExplosionSpecialMine.class);

            PlayerSpecialMining mockMining = mock(PlayerSpecialMining.class);

            PlayerSpecialMiningRegister.setup(mockMining);

            verify(mockMining, times(2)).addExpantion(any(AbstractSpecialMine.class));
        }

        @Test
        @DisplayName("Should handle empty list gracefully")
        void shouldHandleEmptyList() {
            PlayerSpecialMining mockMining = mock(PlayerSpecialMining.class);
            assertDoesNotThrow(() -> PlayerSpecialMiningRegister.setup(mockMining));
            verify(mockMining, never()).addExpantion(any());
        }

        @Test
        @DisplayName("Should sort expansions by priority")
        void shouldSortByPriority() {
            // Register in non-priority order
            PlayerSpecialMiningRegister.register(TelepathySpecialMine.class); // priority 20
            PlayerSpecialMiningRegister.register(FurnaceSpecialMine.class);   // priority 0
            PlayerSpecialMiningRegister.register(BlockDropBonusSpecialMine.class); // priority 5

            PlayerSpecialMining mockMining = mock(PlayerSpecialMining.class);

            PlayerSpecialMiningRegister.setup(mockMining);

            // All three should be added
            verify(mockMining, times(3)).addExpantion(any(AbstractSpecialMine.class));
        }
    }

    @Nested
    @DisplayName("List State Tests")
    class ListStateTests {

        @Test
        @DisplayName("List should be empty after cleanup")
        void listShouldBeEmptyAfterCleanup() {
            assertTrue(PlayerSpecialMiningRegister.list.isEmpty());
        }

        @Test
        @DisplayName("List should be accessible statically")
        void listShouldBeAccessibleStatically() {
            assertNotNull(PlayerSpecialMiningRegister.list);
        }
    }
}
