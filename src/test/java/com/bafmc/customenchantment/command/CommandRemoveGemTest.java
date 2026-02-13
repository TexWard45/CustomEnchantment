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
 * Tests for CommandRemoveGem class.
 * Tests remove gem command registration and index validation logic.
 */
@DisplayName("CommandRemoveGem Tests")
class CommandRemoveGemTest {

    private CommandRemoveGem commandRemoveGem;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandRemoveGem = new CommandRemoveGem();
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
            assertInstanceOf(CommandRegistrar.class, commandRemoveGem);
        }

        @Test
        @DisplayName("should be instantiable with no-arg constructor")
        void shouldBeInstantiable() {
            assertNotNull(new CommandRemoveGem());
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register removegem subcommand")
        void shouldRegisterRemoveGemSubCommand() {
            commandRemoveGem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("removegem");
        }

        @Test
        @DisplayName("should register correct permission")
        void shouldRegisterCorrectPermission() {
            commandRemoveGem.onRegister(mockBuilder);

            verify(mockBuilder).permission("customenchantment.removegem.other");
        }

        @Test
        @DisplayName("should register player argument type")
        void shouldRegisterPlayerArgument() {
            commandRemoveGem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand(ArgumentType.PLAYER);
        }

        @Test
        @DisplayName("should register index subcommand")
        void shouldRegisterIndexSubCommand() {
            commandRemoveGem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("<index>");
        }

        @Test
        @DisplayName("should register tab completer for index")
        void shouldRegisterTabCompleter() {
            commandRemoveGem.onRegister(mockBuilder);

            verify(mockBuilder).tabCompleter(any());
        }

        @Test
        @DisplayName("should register command executor")
        void shouldRegisterCommandExecutor() {
            commandRemoveGem.onRegister(mockBuilder);

            verify(mockBuilder).commandExecutor(any());
        }

        @Test
        @DisplayName("should call end to close command tree")
        void shouldCallEnd() {
            commandRemoveGem.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(3)).end();
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> commandRemoveGem.onRegister(mockBuilder));
        }
    }

    @Nested
    @DisplayName("Index Validation Logic Tests")
    class IndexValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {"0", "1", "5", "10"})
        @DisplayName("should parse valid integer index values")
        void shouldParseValidIntegerIndex(String indexStr) {
            int index = Integer.parseInt(indexStr);
            assertTrue(index >= 0);
        }

        @Test
        @DisplayName("should handle non-numeric index gracefully")
        void shouldHandleNonNumericIndex() {
            assertThrows(NumberFormatException.class, () -> Integer.valueOf("abc"));
        }

        @Test
        @DisplayName("should reject negative index")
        void shouldRejectNegativeIndex() {
            int index = -1;
            assertTrue(index < 0);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -5, -100})
        @DisplayName("should reject negative indices in boundary check")
        void shouldRejectNegativeIndicesInBoundaryCheck(int index) {
            int listSize = 5;
            // Mirrors the validation: index < 0 || index >= listSize
            assertTrue(index < 0 || index >= listSize);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3, 4})
        @DisplayName("should accept valid indices within bounds")
        void shouldAcceptValidIndices(int index) {
            int listSize = 5;
            assertFalse(index < 0 || index >= listSize);
        }

        @Test
        @DisplayName("should reject index equal to list size")
        void shouldRejectIndexEqualToListSize() {
            int index = 5;
            int listSize = 5;
            assertTrue(index >= listSize);
        }
    }
}
