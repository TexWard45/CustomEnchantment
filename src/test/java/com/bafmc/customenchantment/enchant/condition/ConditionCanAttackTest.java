package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionCanAttack - checks if player can attack enemy based on faction, gamemode, and op status.
 * Note: canAttackFaction requires FactionAPI which depends on Factions (compileOnly).
 * Tests for canAttackGamemode and canAttackOp can be tested directly.
 */
@DisplayName("ConditionCanAttack Tests")
class ConditionCanAttackTest {

    private ConditionCanAttack condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionCanAttack();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return CAN_ATTACK")
        void shouldReturnCorrectIdentifier() {
            assertEquals("CAN_ATTACK", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("should accept empty args without error")
        void shouldAcceptEmptyArgs() {
            assertDoesNotThrow(() -> condition.setup(new String[]{}));
        }
    }

    @Nested
    @DisplayName("canAttackGamemode Tests")
    class CanAttackGamemodeTests {

        @Test
        @DisplayName("should return true when enemy is in SURVIVAL mode")
        void shouldReturnTrueForSurvivalMode() {
            Player mockPlayer = mock(Player.class);
            Player mockEnemy = mock(Player.class);
            when(mockEnemy.getGameMode()).thenReturn(GameMode.SURVIVAL);

            assertTrue(condition.canAttackGamemode(mockPlayer, mockEnemy));
        }

        @ParameterizedTest(name = "should return false when enemy is in {0} mode")
        @EnumSource(value = GameMode.class, names = {"CREATIVE", "SPECTATOR", "ADVENTURE"})
        @DisplayName("should return false for non-survival gamemodes")
        void shouldReturnFalseForNonSurvivalModes(GameMode mode) {
            Player mockPlayer = mock(Player.class);
            Player mockEnemy = mock(Player.class);
            when(mockEnemy.getGameMode()).thenReturn(mode);

            assertFalse(condition.canAttackGamemode(mockPlayer, mockEnemy));
        }

        @Test
        @DisplayName("should return false when enemy is null")
        void shouldReturnFalseWhenEnemyNull() {
            Player mockPlayer = mock(Player.class);

            assertFalse(condition.canAttackGamemode(mockPlayer, null));
        }
    }

    @Nested
    @DisplayName("canAttackOp Tests")
    class CanAttackOpTests {

        @Test
        @DisplayName("should return true when enemy is not op")
        void shouldReturnTrueWhenNotOp() {
            Player mockPlayer = mock(Player.class);
            Player mockEnemy = mock(Player.class);
            when(mockEnemy.isOp()).thenReturn(false);

            assertTrue(condition.canAttackOp(mockPlayer, mockEnemy));
        }

        @Test
        @DisplayName("should return false when enemy is op")
        void shouldReturnFalseWhenOp() {
            Player mockPlayer = mock(Player.class);
            Player mockEnemy = mock(Player.class);
            when(mockEnemy.isOp()).thenReturn(true);

            assertFalse(condition.canAttackOp(mockPlayer, mockEnemy));
        }

        @Test
        @DisplayName("should return false when enemy is null")
        void shouldReturnFalseWhenEnemyNull() {
            Player mockPlayer = mock(Player.class);

            assertFalse(condition.canAttackOp(mockPlayer, null));
        }
    }

    @Nested
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("should extend ConditionHook")
        void shouldExtendConditionHook() {
            assertInstanceOf(ConditionHook.class, condition);
        }

        @Test
        @DisplayName("should have canAttack method")
        void shouldHaveCanAttackMethod() {
            try {
                var method = ConditionCanAttack.class.getMethod("canAttack", Player.class, Player.class);
                assertNotNull(method);
                assertEquals(boolean.class, method.getReturnType());
            } catch (NoSuchMethodException e) {
                fail("Should have canAttack(Player, Player) method");
            }
        }

        @Test
        @DisplayName("should have canAttackFaction method")
        void shouldHaveCanAttackFactionMethod() {
            try {
                var method = ConditionCanAttack.class.getMethod("canAttackFaction", Player.class, Player.class);
                assertNotNull(method);
                assertEquals(boolean.class, method.getReturnType());
            } catch (NoSuchMethodException e) {
                fail("Should have canAttackFaction(Player, Player) method");
            }
        }

        @Test
        @DisplayName("should have canAttackGamemode method")
        void shouldHaveCanAttackGamemodeMethod() {
            try {
                var method = ConditionCanAttack.class.getMethod("canAttackGamemode", Player.class, Player.class);
                assertNotNull(method);
                assertEquals(boolean.class, method.getReturnType());
            } catch (NoSuchMethodException e) {
                fail("Should have canAttackGamemode(Player, Player) method");
            }
        }

        @Test
        @DisplayName("should have canAttackOp method")
        void shouldHaveCanAttackOpMethod() {
            try {
                var method = ConditionCanAttack.class.getMethod("canAttackOp", Player.class, Player.class);
                assertNotNull(method);
                assertEquals(boolean.class, method.getReturnType());
            } catch (NoSuchMethodException e) {
                fail("Should have canAttackOp(Player, Player) method");
            }
        }

        @Test
        @DisplayName("should be cloneable via ConditionHook")
        void shouldBeCloneable() {
            ConditionHook cloned = condition.clone();
            assertNotNull(cloned);
            assertInstanceOf(ConditionCanAttack.class, cloned);
        }
    }
}
