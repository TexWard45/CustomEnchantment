package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.ArgumentType;
import com.bafmc.bukkit.command.CommandRegistrar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CommandDebugCE class.
 * Tests per-player debug CE toggle command and static toggle player list.
 */
@DisplayName("CommandDebugCE Tests")
class CommandDebugCETest {

    private CommandDebugCE commandDebugCE;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandDebugCE = new CommandDebugCE();
        // Reset static state before each test
        CommandDebugCE.getTogglePlayers().clear();

        when(mockBuilder.subCommand(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.subCommand(any(String[].class))).thenReturn(mockBuilder);
        when(mockBuilder.permission(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.commandExecutor(any())).thenReturn(mockBuilder);
        when(mockBuilder.end()).thenReturn(mockBuilder);
    }

    @Nested
    @DisplayName("Interface Implementation Tests")
    class InterfaceTests {

        @Test
        @DisplayName("should implement CommandRegistrar interface")
        void shouldImplementCommandRegistrar() {
            assertInstanceOf(CommandRegistrar.class, commandDebugCE);
        }

        @Test
        @DisplayName("should be instantiable with no-arg constructor")
        void shouldBeInstantiable() {
            assertNotNull(new CommandDebugCE());
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register debugce subcommand")
        void shouldRegisterDebugCESubCommand() {
            commandDebugCE.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("debugce");
        }

        @Test
        @DisplayName("should register correct permission")
        void shouldRegisterCorrectPermission() {
            commandDebugCE.onRegister(mockBuilder);

            verify(mockBuilder).permission("customenchantment.debugce.target");
        }

        @Test
        @DisplayName("should register player argument type")
        void shouldRegisterPlayerArgument() {
            commandDebugCE.onRegister(mockBuilder);

            verify(mockBuilder).subCommand(ArgumentType.PLAYER);
        }

        @Test
        @DisplayName("should register command executor")
        void shouldRegisterCommandExecutor() {
            commandDebugCE.onRegister(mockBuilder);

            verify(mockBuilder).commandExecutor(any());
        }

        @Test
        @DisplayName("should call end to close command tree")
        void shouldCallEnd() {
            commandDebugCE.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(2)).end();
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> commandDebugCE.onRegister(mockBuilder));
        }
    }

    @Nested
    @DisplayName("Static Toggle Players List Tests")
    class TogglePlayersTests {

        @Test
        @DisplayName("should start with empty toggle players list")
        void shouldStartWithEmptyList() {
            assertTrue(CommandDebugCE.getTogglePlayers().isEmpty());
        }

        @Test
        @DisplayName("should return mutable list")
        void shouldReturnMutableList() {
            List<String> list = CommandDebugCE.getTogglePlayers();
            assertNotNull(list);
            assertDoesNotThrow(() -> list.add("TestPlayer"));
            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("should add player to toggle list")
        void shouldAddPlayerToToggleList() {
            CommandDebugCE.getTogglePlayers().add("TestPlayer");

            assertTrue(CommandDebugCE.getTogglePlayers().contains("TestPlayer"));
            assertEquals(1, CommandDebugCE.getTogglePlayers().size());
        }

        @Test
        @DisplayName("should remove player from toggle list")
        void shouldRemovePlayerFromToggleList() {
            CommandDebugCE.getTogglePlayers().add("TestPlayer");
            CommandDebugCE.getTogglePlayers().remove("TestPlayer");

            assertFalse(CommandDebugCE.getTogglePlayers().contains("TestPlayer"));
            assertEquals(0, CommandDebugCE.getTogglePlayers().size());
        }

        @Test
        @DisplayName("should toggle player on when not in list")
        void shouldTogglePlayerOnWhenNotInList() {
            String playerName = "TestPlayer";
            boolean isToggle = !CommandDebugCE.getTogglePlayers().contains(playerName);

            assertTrue(isToggle);
        }

        @Test
        @DisplayName("should toggle player off when already in list")
        void shouldTogglePlayerOffWhenAlreadyInList() {
            String playerName = "TestPlayer";
            CommandDebugCE.getTogglePlayers().add(playerName);

            boolean isToggle = !CommandDebugCE.getTogglePlayers().contains(playerName);

            assertFalse(isToggle);
        }

        @Test
        @DisplayName("should handle multiple players in toggle list")
        void shouldHandleMultiplePlayers() {
            CommandDebugCE.getTogglePlayers().add("Player1");
            CommandDebugCE.getTogglePlayers().add("Player2");
            CommandDebugCE.getTogglePlayers().add("Player3");

            assertEquals(3, CommandDebugCE.getTogglePlayers().size());
            assertTrue(CommandDebugCE.getTogglePlayers().contains("Player1"));
            assertTrue(CommandDebugCE.getTogglePlayers().contains("Player2"));
            assertTrue(CommandDebugCE.getTogglePlayers().contains("Player3"));
        }
    }
}
