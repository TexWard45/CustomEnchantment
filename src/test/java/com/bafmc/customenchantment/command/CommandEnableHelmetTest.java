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
 * Tests for CommandEnableHelmet class.
 * Tests enable helmet command registration.
 */
@DisplayName("CommandEnableHelmet Tests")
class CommandEnableHelmetTest {

    private CommandEnableHelmet commandEnableHelmet;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandEnableHelmet = new CommandEnableHelmet();
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
            assertInstanceOf(CommandRegistrar.class, commandEnableHelmet);
        }

        @Test
        @DisplayName("should be instantiable with no-arg constructor")
        void shouldBeInstantiable() {
            assertNotNull(new CommandEnableHelmet());
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register enablehelmet subcommand")
        void shouldRegisterEnableHelmetSubCommand() {
            commandEnableHelmet.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("enablehelmet");
        }

        @Test
        @DisplayName("should register correct permission")
        void shouldRegisterCorrectPermission() {
            commandEnableHelmet.onRegister(mockBuilder);

            verify(mockBuilder).permission("customenchantment.enablehelmet.other");
        }

        @Test
        @DisplayName("should register player argument type")
        void shouldRegisterPlayerArgument() {
            commandEnableHelmet.onRegister(mockBuilder);

            verify(mockBuilder).subCommand(ArgumentType.PLAYER);
        }

        @Test
        @DisplayName("should register command executor")
        void shouldRegisterCommandExecutor() {
            commandEnableHelmet.onRegister(mockBuilder);

            verify(mockBuilder).commandExecutor(any());
        }

        @Test
        @DisplayName("should call end to close command tree")
        void shouldCallEnd() {
            commandEnableHelmet.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(2)).end();
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> commandEnableHelmet.onRegister(mockBuilder));
        }
    }
}
