package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.CommandRegistrar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CommandDebugAll class.
 * Tests global debug mode toggle command registration and static state.
 */
@DisplayName("CommandDebugAll Tests")
class CommandDebugAllTest {

    private CommandDebugAll commandDebugAll;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandDebugAll = new CommandDebugAll();
        // Reset static state before each test
        CommandDebugAll.debugMode = false;

        when(mockBuilder.subCommand(anyString())).thenReturn(mockBuilder);
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
            assertInstanceOf(CommandRegistrar.class, commandDebugAll);
        }

        @Test
        @DisplayName("should be instantiable with no-arg constructor")
        void shouldBeInstantiable() {
            assertNotNull(new CommandDebugAll());
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register debugall subcommand")
        void shouldRegisterDebugAllSubCommand() {
            commandDebugAll.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("debugall");
        }

        @Test
        @DisplayName("should register correct permission")
        void shouldRegisterCorrectPermission() {
            commandDebugAll.onRegister(mockBuilder);

            verify(mockBuilder).permission("customenchantment.debugall");
        }

        @Test
        @DisplayName("should register command executor")
        void shouldRegisterCommandExecutor() {
            commandDebugAll.onRegister(mockBuilder);

            verify(mockBuilder).commandExecutor(any());
        }

        @Test
        @DisplayName("should call end to close command tree")
        void shouldCallEnd() {
            commandDebugAll.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(1)).end();
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> commandDebugAll.onRegister(mockBuilder));
        }
    }

    @Nested
    @DisplayName("Static Debug Mode Tests")
    class StaticDebugModeTests {

        @Test
        @DisplayName("should default debug mode to false")
        void shouldDefaultDebugModeToFalse() {
            assertFalse(CommandDebugAll.isDebugMode());
        }

        @Test
        @DisplayName("should be able to set debug mode to true")
        void shouldSetDebugModeToTrue() {
            CommandDebugAll.debugMode = true;

            assertTrue(CommandDebugAll.isDebugMode());
        }

        @Test
        @DisplayName("should toggle debug mode from false to true")
        void shouldToggleFromFalseToTrue() {
            assertFalse(CommandDebugAll.isDebugMode());

            boolean isToggle = !CommandDebugAll.isDebugMode();
            CommandDebugAll.debugMode = isToggle;

            assertTrue(CommandDebugAll.isDebugMode());
        }

        @Test
        @DisplayName("should toggle debug mode from true to false")
        void shouldToggleFromTrueToFalse() {
            CommandDebugAll.debugMode = true;
            assertTrue(CommandDebugAll.isDebugMode());

            boolean isToggle = !CommandDebugAll.isDebugMode();
            CommandDebugAll.debugMode = isToggle;

            assertFalse(CommandDebugAll.isDebugMode());
        }
    }
}
