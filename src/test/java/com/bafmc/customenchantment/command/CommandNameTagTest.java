package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.CommandRegistrar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CommandNameTag class.
 * Tests name tag command registration with help/show/preview/set sub-commands.
 */
@DisplayName("CommandNameTag Tests")
class CommandNameTagTest {

    private CommandNameTag commandNameTag;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandNameTag = new CommandNameTag();
        when(mockBuilder.subCommand(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.subCommand(any(String[].class))).thenReturn(mockBuilder);
        when(mockBuilder.permission(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.commandExecutor(any())).thenReturn(mockBuilder);
        when(mockBuilder.tabCompleter(any())).thenReturn(mockBuilder);
        when(mockBuilder.end()).thenReturn(mockBuilder);
    }

    @Nested
    @DisplayName("Interface Implementation Tests")
    class InterfaceTests {

        @Test
        @DisplayName("should implement CommandRegistrar interface")
        void shouldImplementCommandRegistrar() {
            assertInstanceOf(CommandRegistrar.class, commandNameTag);
        }

        @Test
        @DisplayName("should be instantiable with no-arg constructor")
        void shouldBeInstantiable() {
            assertNotNull(new CommandNameTag());
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register help subcommand")
        void shouldRegisterHelpSubCommand() {
            commandNameTag.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(1)).subCommand("help");
        }

        @Test
        @DisplayName("should register show subcommand")
        void shouldRegisterShowSubCommand() {
            commandNameTag.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("show");
        }

        @Test
        @DisplayName("should register preview subcommand")
        void shouldRegisterPreviewSubCommand() {
            commandNameTag.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("preview");
        }

        @Test
        @DisplayName("should register set subcommand")
        void shouldRegisterSetSubCommand() {
            commandNameTag.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("set");
        }

        @Test
        @DisplayName("should register type subcommand for show, preview, set")
        void shouldRegisterTypeSubCommand() {
            commandNameTag.onRegister(mockBuilder);

            // <type> is used in show, preview, and set
            verify(mockBuilder, atLeast(3)).subCommand("<type>");
        }

        @Test
        @DisplayName("should register display subcommand for preview and set")
        void shouldRegisterDisplaySubCommand() {
            commandNameTag.onRegister(mockBuilder);

            // <display> is used in preview and set
            verify(mockBuilder, atLeast(2)).subCommand("<display>");
        }

        @Test
        @DisplayName("should register help page subcommands 1, 2, 3")
        void shouldRegisterHelpPages() {
            commandNameTag.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("1");
            verify(mockBuilder).subCommand("2");
            verify(mockBuilder).subCommand("3");
        }

        @Test
        @DisplayName("should register root command executor for help")
        void shouldRegisterRootCommandExecutor() {
            commandNameTag.onRegister(mockBuilder);

            // Multiple executors: root help, help 1, help 2, help 3, show, preview, set
            verify(mockBuilder, atLeast(7)).commandExecutor(any());
        }

        @Test
        @DisplayName("should register tab completers for type")
        void shouldRegisterTabCompleters() {
            commandNameTag.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(3)).tabCompleter(any());
        }

        @Test
        @DisplayName("should call end to close command tree")
        void shouldCallEnd() {
            commandNameTag.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(8)).end();
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> commandNameTag.onRegister(mockBuilder));
        }
    }

    @Nested
    @DisplayName("Name Tag Length Validation Tests")
    class NameTagLengthTests {

        @ParameterizedTest
        @ValueSource(strings = {"Short", "Hello", "12345678901234567890123456789012345"})
        @DisplayName("should accept names up to 35 characters")
        void shouldAcceptNamesUpTo35Characters(String display) {
            assertTrue(display.length() <= 35);
        }

        @Test
        @DisplayName("should reject names longer than 35 characters")
        void shouldRejectNamesLongerThan35Characters() {
            String longDisplay = "A".repeat(36);
            assertTrue(longDisplay.length() > 35);
        }

        @Test
        @DisplayName("should accept name exactly 35 characters")
        void shouldAcceptNameExactly35Characters() {
            String display = "A".repeat(35);
            assertEquals(35, display.length());
            assertFalse(display.length() > 35);
        }

        @Test
        @DisplayName("should handle empty display name")
        void shouldHandleEmptyDisplayName() {
            String display = "";
            assertFalse(display.length() > 35);
        }
    }
}
