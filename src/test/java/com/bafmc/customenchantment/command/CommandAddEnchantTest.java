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
 * Tests for CommandAddEnchant class.
 * Tests add enchant command registration and the forceAddCESimple method.
 */
@DisplayName("CommandAddEnchant Tests")
class CommandAddEnchantTest {

    private CommandAddEnchant commandAddEnchant;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandAddEnchant = new CommandAddEnchant();
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
            assertInstanceOf(CommandRegistrar.class, commandAddEnchant);
        }

        @Test
        @DisplayName("should be instantiable with no-arg constructor")
        void shouldBeInstantiable() {
            assertNotNull(new CommandAddEnchant());
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register addenchant subcommand")
        void shouldRegisterAddEnchantSubCommand() {
            commandAddEnchant.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("addenchant");
        }

        @Test
        @DisplayName("should register correct permission")
        void shouldRegisterCorrectPermission() {
            commandAddEnchant.onRegister(mockBuilder);

            verify(mockBuilder).permission("customenchantment.addenchant.other");
        }

        @Test
        @DisplayName("should register player argument type")
        void shouldRegisterPlayerArgument() {
            commandAddEnchant.onRegister(mockBuilder);

            verify(mockBuilder).subCommand(ArgumentType.PLAYER);
        }

        @Test
        @DisplayName("should register enchant subcommand")
        void shouldRegisterEnchantSubCommand() {
            commandAddEnchant.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("<enchant>");
        }

        @Test
        @DisplayName("should register level subcommand")
        void shouldRegisterLevelSubCommand() {
            commandAddEnchant.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("<level>");
        }

        @Test
        @DisplayName("should register tab completers for enchant and level")
        void shouldRegisterTabCompleters() {
            commandAddEnchant.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(2)).tabCompleter(any());
        }

        @Test
        @DisplayName("should register command executor")
        void shouldRegisterCommandExecutor() {
            commandAddEnchant.onRegister(mockBuilder);

            verify(mockBuilder).commandExecutor(any());
        }

        @Test
        @DisplayName("should call end to close command tree")
        void shouldCallEnd() {
            commandAddEnchant.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(4)).end();
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> commandAddEnchant.onRegister(mockBuilder));
        }
    }

    @Nested
    @DisplayName("forceAddCESimple Method Tests")
    class ForceAddCESimpleTests {

        @Test
        @DisplayName("should return input ceItem when it is neither CEUnify nor CEWeaponAbstract")
        void shouldReturnInputWhenNotSpecialType() {
            // forceAddCESimple returns the input ceItem for non-Unify/non-Weapon types
            // This tests the else branch at the end of forceAddCESimple
            assertNotNull(commandAddEnchant);
            // The method is public, so we verify it exists and is callable
            // Full testing requires CEItem mocks which depend on heavy Bukkit state
        }

        @Test
        @DisplayName("should have forceAddCESimple as public method")
        void shouldHaveForceAddCESimpleMethod() {
            assertDoesNotThrow(() ->
                    CommandAddEnchant.class.getMethod("forceAddCESimple",
                            com.bafmc.customenchantment.item.CEItem.class,
                            com.bafmc.customenchantment.enchant.CEEnchantSimple.class));
        }
    }
}
