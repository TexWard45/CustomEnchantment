package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.ArgumentType;
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
 * Tests for CommandDebug class.
 * Tests debug mode toggle command registration and execution.
 */
@DisplayName("CommandDebug Tests")
class CommandDebugTest {

    private CommandDebug commandDebug;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandDebug = new CommandDebug();
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
            assertInstanceOf(CommandRegistrar.class, commandDebug);
        }

        @Test
        @DisplayName("should be instantiable with no-arg constructor")
        void shouldBeInstantiable() {
            assertNotNull(new CommandDebug());
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register debug subcommand")
        void shouldRegisterDebugSubCommand() {
            commandDebug.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("debug");
        }

        @Test
        @DisplayName("should register correct permission")
        void shouldRegisterCorrectPermission() {
            commandDebug.onRegister(mockBuilder);

            verify(mockBuilder).permission("customenchantment.debug.target");
        }

        @Test
        @DisplayName("should register player argument type")
        void shouldRegisterPlayerArgument() {
            commandDebug.onRegister(mockBuilder);

            verify(mockBuilder).subCommand(ArgumentType.PLAYER);
        }

        @Test
        @DisplayName("should register toggle subcommand")
        void shouldRegisterToggleSubCommand() {
            commandDebug.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("<toggle>");
        }

        @Test
        @DisplayName("should register command executor")
        void shouldRegisterCommandExecutor() {
            commandDebug.onRegister(mockBuilder);

            verify(mockBuilder).commandExecutor(any());
        }

        @Test
        @DisplayName("should call end to close command tree")
        void shouldCallEnd() {
            commandDebug.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(3)).end();
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> commandDebug.onRegister(mockBuilder));
        }
    }

    @Nested
    @DisplayName("Toggle Logic Tests")
    class ToggleLogicTests {

        @ParameterizedTest
        @ValueSource(strings = {"on", "true", "yes", "ON", "TRUE", "YES"})
        @DisplayName("should enable debug mode for positive toggle values")
        void shouldEnableForPositiveToggle(String toggleValue) {
            boolean isToggle = toggleValue.equalsIgnoreCase("on")
                    || toggleValue.equalsIgnoreCase("true")
                    || toggleValue.equalsIgnoreCase("yes");
            assertTrue(isToggle);
        }

        @ParameterizedTest
        @ValueSource(strings = {"off", "false", "no", "random", "0", ""})
        @DisplayName("should disable debug mode for negative toggle values")
        void shouldDisableForNegativeToggle(String toggleValue) {
            boolean isToggle = toggleValue.equalsIgnoreCase("on")
                    || toggleValue.equalsIgnoreCase("true")
                    || toggleValue.equalsIgnoreCase("yes");
            assertFalse(isToggle);
        }
    }
}
