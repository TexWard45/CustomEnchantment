package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.CompareOperation;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionNumberStorage - checks numeric values from temporary storage with comparison.
 */
@DisplayName("ConditionNumberStorage Tests")
class ConditionNumberStorageTest {

    private ConditionNumberStorage condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionNumberStorage();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return NUMBER_STORAGE")
        void shouldReturnCorrectIdentifier() {
            assertEquals("NUMBER_STORAGE", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("should setup with single arg for existence check")
        void shouldSetupWithSingleArg() {
            assertDoesNotThrow(() -> condition.setup(new String[]{"myKey"}));
        }

        @Test
        @DisplayName("should setup with three args for comparison")
        void shouldSetupWithThreeArgs() {
            assertDoesNotThrow(() -> condition.setup(new String[]{"myKey", ">", "10"}));
        }

        @Test
        @DisplayName("should throw for empty args")
        void shouldThrowForEmptyArgs() {
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> condition.setup(new String[]{}));
        }
    }

    @Nested
    @DisplayName("match Tests - Null Player")
    class NullPlayerTests {

        @Test
        @DisplayName("should return false when player is null")
        void shouldReturnFalseWhenPlayerNull() {
            condition.setup(new String[]{"myKey"});
            when(mockData.getPlayer()).thenReturn(null);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(null)).thenReturn(null);

                assertFalse(condition.match(mockData));
            }
        }
    }

    @Nested
    @DisplayName("match Tests - Existence Check")
    class ExistenceCheckTests {

        @Test
        @DisplayName("should return true when key exists in storage (no operator)")
        void shouldReturnTrueWhenKeyExists() {
            condition.setup(new String[]{"myKey"});

            Player mockPlayer = mock(Player.class);
            CEPlayer mockCEPlayer = mock(CEPlayer.class);
            PlayerTemporaryStorage mockStorage = mock(PlayerTemporaryStorage.class);

            when(mockData.getPlayer()).thenReturn(mockPlayer);

            Map<String, String> storagePlaceholder = new HashMap<>();
            storagePlaceholder.put("myKey", "42");

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<CEPlaceholder> mockedPlaceholder = mockStatic(CEPlaceholder.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCEPlayer.getTemporaryStorage()).thenReturn(mockStorage);
                mockedPlaceholder.when(() -> CEPlaceholder.getTemporaryStoragePlaceholder(mockStorage))
                        .thenReturn(storagePlaceholder);

                assertTrue(condition.match(mockData));
            }
        }

        @Test
        @DisplayName("should return false when key does not exist in storage (no operator)")
        void shouldReturnFalseWhenKeyDoesNotExist() {
            condition.setup(new String[]{"myKey"});

            Player mockPlayer = mock(Player.class);
            CEPlayer mockCEPlayer = mock(CEPlayer.class);
            PlayerTemporaryStorage mockStorage = mock(PlayerTemporaryStorage.class);

            when(mockData.getPlayer()).thenReturn(mockPlayer);

            Map<String, String> storagePlaceholder = new HashMap<>();
            Map<String, String> funcDataPlaceholder = new HashMap<>();

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<CEPlaceholder> mockedPlaceholder = mockStatic(CEPlaceholder.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCEPlayer.getTemporaryStorage()).thenReturn(mockStorage);
                mockedPlaceholder.when(() -> CEPlaceholder.getTemporaryStoragePlaceholder(mockStorage))
                        .thenReturn(storagePlaceholder);
                mockedPlaceholder.when(() -> CEPlaceholder.getCEFunctionDataPlaceholder("myKey", mockData))
                        .thenReturn(funcDataPlaceholder);
                mockedPlaceholder.when(() -> CEPlaceholder.setPlaceholder(eq("myKey"), any()))
                        .thenReturn("myKey");

                assertFalse(condition.match(mockData));
            }
        }
    }

    @Nested
    @DisplayName("match Tests - Comparison Check")
    class ComparisonCheckTests {

        @Test
        @DisplayName("should compare storage values with operator")
        void shouldCompareStorageValues() {
            condition.setup(new String[]{"key1", ">", "key2"});

            Player mockPlayer = mock(Player.class);
            CEPlayer mockCEPlayer = mock(CEPlayer.class);
            PlayerTemporaryStorage mockStorage = mock(PlayerTemporaryStorage.class);

            when(mockData.getPlayer()).thenReturn(mockPlayer);

            Map<String, String> storagePlaceholder = new HashMap<>();
            storagePlaceholder.put("key1", "50");
            storagePlaceholder.put("key2", "30");

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<CEPlaceholder> mockedPlaceholder = mockStatic(CEPlaceholder.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCEPlayer.getTemporaryStorage()).thenReturn(mockStorage);
                mockedPlaceholder.when(() -> CEPlaceholder.getTemporaryStoragePlaceholder(mockStorage))
                        .thenReturn(storagePlaceholder);

                assertTrue(condition.match(mockData));
            }
        }
    }
}
