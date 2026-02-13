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
 * Tests for ConditionTextStorage - compares text values from temporary storage.
 */
@DisplayName("ConditionTextStorage Tests")
class ConditionTextStorageTest {

    private ConditionTextStorage condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionTextStorage();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return TEXT_STORAGE")
        void shouldReturnCorrectIdentifier() {
            assertEquals("TEXT_STORAGE", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("should setup with three args")
        void shouldSetupWithThreeArgs() {
            assertDoesNotThrow(() -> condition.setup(new String[]{"value1", "=", "value2"}));
        }

        @Test
        @DisplayName("should throw for fewer than 3 args")
        void shouldThrowForFewerArgs() {
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> condition.setup(new String[]{"value1", "="}));
        }
    }

    @Nested
    @DisplayName("match Tests - Text Comparison")
    class TextComparisonTests {

        @Test
        @DisplayName("should return true for EQUALS when strings match")
        void shouldReturnTrueForEqualsMatch() {
            condition.setup(new String[]{"key1", "=", "key2"});

            Player mockPlayer = mock(Player.class);
            CEPlayer mockCEPlayer = mock(CEPlayer.class);
            PlayerTemporaryStorage mockStorage = mock(PlayerTemporaryStorage.class);

            when(mockData.getPlayer()).thenReturn(mockPlayer);

            Map<String, String> storagePlaceholder = new HashMap<>();
            Map<String, String> map1 = new HashMap<>();
            Map<String, String> map2 = new HashMap<>();

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<CEPlaceholder> mockedPlaceholder = mockStatic(CEPlaceholder.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCEPlayer.getTemporaryStorage()).thenReturn(mockStorage);
                mockedPlaceholder.when(() -> CEPlaceholder.getTemporaryStoragePlaceholder(mockStorage))
                        .thenReturn(storagePlaceholder);
                mockedPlaceholder.when(() -> CEPlaceholder.getCEFunctionDataPlaceholder("key1", mockData))
                        .thenReturn(map1);
                mockedPlaceholder.when(() -> CEPlaceholder.getCEFunctionDataPlaceholder("key2", mockData))
                        .thenReturn(map2);
                mockedPlaceholder.when(() -> CEPlaceholder.setPlaceholder(eq("key1"), any()))
                        .thenReturn("hello");
                mockedPlaceholder.when(() -> CEPlaceholder.setPlaceholder(eq("key2"), any()))
                        .thenReturn("hello");

                assertTrue(condition.match(mockData));
            }
        }

        @Test
        @DisplayName("should return false for EQUALS when strings differ")
        void shouldReturnFalseForEqualsMismatch() {
            condition.setup(new String[]{"key1", "=", "key2"});

            Player mockPlayer = mock(Player.class);
            CEPlayer mockCEPlayer = mock(CEPlayer.class);
            PlayerTemporaryStorage mockStorage = mock(PlayerTemporaryStorage.class);

            when(mockData.getPlayer()).thenReturn(mockPlayer);

            Map<String, String> storagePlaceholder = new HashMap<>();
            Map<String, String> map1 = new HashMap<>();
            Map<String, String> map2 = new HashMap<>();

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<CEPlaceholder> mockedPlaceholder = mockStatic(CEPlaceholder.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCEPlayer.getTemporaryStorage()).thenReturn(mockStorage);
                mockedPlaceholder.when(() -> CEPlaceholder.getTemporaryStoragePlaceholder(mockStorage))
                        .thenReturn(storagePlaceholder);
                mockedPlaceholder.when(() -> CEPlaceholder.getCEFunctionDataPlaceholder("key1", mockData))
                        .thenReturn(map1);
                mockedPlaceholder.when(() -> CEPlaceholder.getCEFunctionDataPlaceholder("key2", mockData))
                        .thenReturn(map2);
                mockedPlaceholder.when(() -> CEPlaceholder.setPlaceholder(eq("key1"), any()))
                        .thenReturn("hello");
                mockedPlaceholder.when(() -> CEPlaceholder.setPlaceholder(eq("key2"), any()))
                        .thenReturn("world");

                assertFalse(condition.match(mockData));
            }
        }

        @Test
        @DisplayName("should return true for NOT_EQUALS when strings differ")
        void shouldReturnTrueForNotEqualsDifference() {
            condition.setup(new String[]{"key1", "!=", "key2"});

            Player mockPlayer = mock(Player.class);
            CEPlayer mockCEPlayer = mock(CEPlayer.class);
            PlayerTemporaryStorage mockStorage = mock(PlayerTemporaryStorage.class);

            when(mockData.getPlayer()).thenReturn(mockPlayer);

            Map<String, String> storagePlaceholder = new HashMap<>();
            Map<String, String> map1 = new HashMap<>();
            Map<String, String> map2 = new HashMap<>();

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<CEPlaceholder> mockedPlaceholder = mockStatic(CEPlaceholder.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCEPlayer.getTemporaryStorage()).thenReturn(mockStorage);
                mockedPlaceholder.when(() -> CEPlaceholder.getTemporaryStoragePlaceholder(mockStorage))
                        .thenReturn(storagePlaceholder);
                mockedPlaceholder.when(() -> CEPlaceholder.getCEFunctionDataPlaceholder("key1", mockData))
                        .thenReturn(map1);
                mockedPlaceholder.when(() -> CEPlaceholder.getCEFunctionDataPlaceholder("key2", mockData))
                        .thenReturn(map2);
                mockedPlaceholder.when(() -> CEPlaceholder.setPlaceholder(eq("key1"), any()))
                        .thenReturn("hello");
                mockedPlaceholder.when(() -> CEPlaceholder.setPlaceholder(eq("key2"), any()))
                        .thenReturn("world");

                assertTrue(condition.match(mockData));
            }
        }

        @Test
        @DisplayName("should return true for EQUALSIGNORECASE when strings match ignoring case")
        void shouldReturnTrueForEqualsIgnoreCase() {
            condition.setup(new String[]{"key1", "==", "key2"});

            Player mockPlayer = mock(Player.class);
            CEPlayer mockCEPlayer = mock(CEPlayer.class);
            PlayerTemporaryStorage mockStorage = mock(PlayerTemporaryStorage.class);

            when(mockData.getPlayer()).thenReturn(mockPlayer);

            Map<String, String> storagePlaceholder = new HashMap<>();
            Map<String, String> map1 = new HashMap<>();
            Map<String, String> map2 = new HashMap<>();

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<CEPlaceholder> mockedPlaceholder = mockStatic(CEPlaceholder.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCEPlayer.getTemporaryStorage()).thenReturn(mockStorage);
                mockedPlaceholder.when(() -> CEPlaceholder.getTemporaryStoragePlaceholder(mockStorage))
                        .thenReturn(storagePlaceholder);
                mockedPlaceholder.when(() -> CEPlaceholder.getCEFunctionDataPlaceholder("key1", mockData))
                        .thenReturn(map1);
                mockedPlaceholder.when(() -> CEPlaceholder.getCEFunctionDataPlaceholder("key2", mockData))
                        .thenReturn(map2);
                mockedPlaceholder.when(() -> CEPlaceholder.setPlaceholder(eq("key1"), any()))
                        .thenReturn("Hello");
                mockedPlaceholder.when(() -> CEPlaceholder.setPlaceholder(eq("key2"), any()))
                        .thenReturn("hello");

                assertTrue(condition.match(mockData));
            }
        }

        @Test
        @DisplayName("should return false for unsupported operation")
        void shouldReturnFalseForUnsupportedOp() {
            condition.setup(new String[]{"key1", ">", "key2"});

            Player mockPlayer = mock(Player.class);
            CEPlayer mockCEPlayer = mock(CEPlayer.class);
            PlayerTemporaryStorage mockStorage = mock(PlayerTemporaryStorage.class);

            when(mockData.getPlayer()).thenReturn(mockPlayer);

            Map<String, String> storagePlaceholder = new HashMap<>();
            Map<String, String> map1 = new HashMap<>();
            Map<String, String> map2 = new HashMap<>();

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<CEPlaceholder> mockedPlaceholder = mockStatic(CEPlaceholder.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCEPlayer.getTemporaryStorage()).thenReturn(mockStorage);
                mockedPlaceholder.when(() -> CEPlaceholder.getTemporaryStoragePlaceholder(mockStorage))
                        .thenReturn(storagePlaceholder);
                mockedPlaceholder.when(() -> CEPlaceholder.getCEFunctionDataPlaceholder("key1", mockData))
                        .thenReturn(map1);
                mockedPlaceholder.when(() -> CEPlaceholder.getCEFunctionDataPlaceholder("key2", mockData))
                        .thenReturn(map2);
                mockedPlaceholder.when(() -> CEPlaceholder.setPlaceholder(eq("key1"), any()))
                        .thenReturn("hello");
                mockedPlaceholder.when(() -> CEPlaceholder.setPlaceholder(eq("key2"), any()))
                        .thenReturn("world");

                assertFalse(condition.match(mockData));
            }
        }
    }
}
