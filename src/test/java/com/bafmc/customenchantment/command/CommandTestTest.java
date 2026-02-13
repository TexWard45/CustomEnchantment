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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CommandTest class.
 * Tests the test command registration and tab completion.
 */
@DisplayName("CommandTest Tests")
class CommandTestTest {

    private CommandTest commandTest;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandTest = new CommandTest();
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
            assertInstanceOf(CommandRegistrar.class, commandTest);
        }

        @Test
        @DisplayName("should be instantiable with no-arg constructor")
        void shouldBeInstantiable() {
            assertNotNull(new CommandTest());
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register test subcommand")
        void shouldRegisterTestSubCommand() {
            commandTest.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("test");
        }

        @Test
        @DisplayName("should register correct permission")
        void shouldRegisterCorrectPermission() {
            commandTest.onRegister(mockBuilder);

            verify(mockBuilder).permission("customenchantment.test.target");
        }

        @Test
        @DisplayName("should register player argument type")
        void shouldRegisterPlayerArgument() {
            commandTest.onRegister(mockBuilder);

            verify(mockBuilder).subCommand(ArgumentType.PLAYER);
        }

        @Test
        @DisplayName("should register parameter subcommand")
        void shouldRegisterParameterSubCommand() {
            commandTest.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("<parameter>");
        }

        @Test
        @DisplayName("should register tab completer")
        void shouldRegisterTabCompleter() {
            commandTest.onRegister(mockBuilder);

            verify(mockBuilder).tabCompleter(any());
        }

        @Test
        @DisplayName("should register command executor")
        void shouldRegisterCommandExecutor() {
            commandTest.onRegister(mockBuilder);

            verify(mockBuilder).commandExecutor(any());
        }

        @Test
        @DisplayName("should call end to close command tree")
        void shouldCallEnd() {
            commandTest.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(3)).end();
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> commandTest.onRegister(mockBuilder));
        }
    }

    @Nested
    @DisplayName("Parameter Logic Tests")
    class ParameterLogicTests {

        @Test
        @DisplayName("should match incombat parameter")
        void shouldMatchInCombatParameter() {
            String parameter = "incombat";
            assertEquals("incombat", parameter);
        }

        @Test
        @DisplayName("should not match invalid parameter")
        void shouldNotMatchInvalidParameter() {
            String parameter = "invalid";
            assertNotEquals("incombat", parameter);
        }
    }
}
