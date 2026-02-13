package com.bafmc.customenchantment.player;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerNameTag")
@ExtendWith(MockitoExtension.class)
class PlayerNameTagTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    private PlayerNameTag nameTag;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        nameTag = new PlayerNameTag(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(nameTag);
            assertSame(mockCEPlayer, nameTag.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(nameTag instanceof CEPlayerExpansion);
        }

        @Test
        @DisplayName("Display should be null initially")
        void displayShouldBeNullInitially() {
            // Before onJoin is called, display is the default null
            assertNull(nameTag.getDisplay());
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should reset display to null")
        void onJoinShouldResetDisplayToNull() {
            nameTag.setDisplay("test_display");
            nameTag.onJoin();
            assertNull(nameTag.getDisplay());
        }

        @Test
        @DisplayName("onQuit should not throw")
        void onQuitShouldNotThrow() {
            assertDoesNotThrow(() -> nameTag.onQuit());
        }
    }

    @Nested
    @DisplayName("Display Getter and Setter Tests")
    class DisplayTests {

        @Test
        @DisplayName("Should set and get display value")
        void shouldSetAndGetDisplayValue() {
            nameTag.setDisplay("CustomTag");
            assertEquals("CustomTag", nameTag.getDisplay());
        }

        @Test
        @DisplayName("Should allow null display")
        void shouldAllowNullDisplay() {
            nameTag.setDisplay("test");
            nameTag.setDisplay(null);
            assertNull(nameTag.getDisplay());
        }

        @Test
        @DisplayName("Should allow empty string display")
        void shouldAllowEmptyStringDisplay() {
            nameTag.setDisplay("");
            assertEquals("", nameTag.getDisplay());
        }

        @Test
        @DisplayName("Should allow colored display")
        void shouldAllowColoredDisplay() {
            String coloredName = "\u00a7aGreen \u00a7bName";
            nameTag.setDisplay(coloredName);
            assertEquals(coloredName, nameTag.getDisplay());
        }

        @Test
        @DisplayName("Should overwrite previous display")
        void shouldOverwritePreviousDisplay() {
            nameTag.setDisplay("first");
            nameTag.setDisplay("second");
            assertEquals("second", nameTag.getDisplay());
        }
    }
}
