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
 * Tests for CommandGiveItem class.
 * Tests give item command registration with extensive subcommand tree.
 */
@DisplayName("CommandGiveItem Tests")
class CommandGiveItemTest {

    private CommandGiveItem commandGiveItem;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandGiveItem = new CommandGiveItem();
        when(mockBuilder.subCommand(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.subCommand(any(String[].class))).thenReturn(mockBuilder);
        when(mockBuilder.permission(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.commandExecutor(any())).thenReturn(mockBuilder);
        when(mockBuilder.commandExecutorUntilNextCommand(any())).thenReturn(mockBuilder);
        when(mockBuilder.tabCompleter(any())).thenReturn(mockBuilder);
        when(mockBuilder.end()).thenReturn(mockBuilder);
    }

    @Nested
    @DisplayName("Interface Implementation Tests")
    class InterfaceTests {

        @Test
        @DisplayName("should implement CommandRegistrar interface")
        void shouldImplementCommandRegistrar() {
            assertInstanceOf(CommandRegistrar.class, commandGiveItem);
        }

        @Test
        @DisplayName("should be instantiable with no-arg constructor")
        void shouldBeInstantiable() {
            assertNotNull(new CommandGiveItem());
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register give subcommand")
        void shouldRegisterGiveSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("give");
        }

        @Test
        @DisplayName("should register correct permission")
        void shouldRegisterCorrectPermission() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).permission("customenchantment.reload");
        }

        @Test
        @DisplayName("should register player argument type")
        void shouldRegisterPlayerArgument() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand(ArgumentType.PLAYER);
        }

        @Test
        @DisplayName("should register book subcommand")
        void shouldRegisterBookSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("book");
        }

        @Test
        @DisplayName("should register gem subcommand")
        void shouldRegisterGemSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("gem");
        }

        @Test
        @DisplayName("should register nametag subcommand")
        void shouldRegisterNametagSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("nametag");
        }

        @Test
        @DisplayName("should register weapon subcommand")
        void shouldRegisterWeaponSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("weapon");
        }

        @Test
        @DisplayName("should register mask subcommand")
        void shouldRegisterMaskSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("mask");
        }

        @Test
        @DisplayName("should register artifact subcommand")
        void shouldRegisterArtifactSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("artifact");
        }

        @Test
        @DisplayName("should register sigil subcommand")
        void shouldRegisterSigilSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("sigil");
        }

        @Test
        @DisplayName("should register outfit subcommand")
        void shouldRegisterOutfitSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("outfit");
        }

        @Test
        @DisplayName("should register storage subcommand")
        void shouldRegisterStorageSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("storage");
        }

        @Test
        @DisplayName("should register voucher subcommand")
        void shouldRegisterVoucherSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("voucher");
        }

        @Test
        @DisplayName("should register protectdead subcommand")
        void shouldRegisterProtectDeadSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("protectdead");
        }

        @Test
        @DisplayName("should register protectdestroy subcommand")
        void shouldRegisterProtectDestroySubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("protectdestroy");
        }

        @Test
        @DisplayName("should register randombook subcommand")
        void shouldRegisterRandomBookSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("randombook");
        }

        @Test
        @DisplayName("should register increaseratebook subcommand")
        void shouldRegisterIncreaseRateBookSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("increaseratebook");
        }

        @Test
        @DisplayName("should register skin subcommand")
        void shouldRegisterSkinSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("skin");
        }

        @Test
        @DisplayName("should register banner subcommand")
        void shouldRegisterBannerSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("banner");
        }

        @Test
        @DisplayName("should register gemdrill subcommand")
        void shouldRegisterGemDrillSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("gemdrill");
        }

        @Test
        @DisplayName("should register enchantpoint subcommand")
        void shouldRegisterEnchantPointSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("enchantpoint");
        }

        @Test
        @DisplayName("should register removeenchant subcommand for give")
        void shouldRegisterRemoveEnchantSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("removeenchant");
        }

        @Test
        @DisplayName("should register removegem subcommand for give")
        void shouldRegisterRemoveGemSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("removegem");
        }

        @Test
        @DisplayName("should register eraseenchant subcommand")
        void shouldRegisterEraseEnchantSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("eraseenchant");
        }

        @Test
        @DisplayName("should register loreformat subcommand")
        void shouldRegisterLoreFormatSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("loreformat");
        }

        @Test
        @DisplayName("should register removeprotectdead subcommand")
        void shouldRegisterRemoveProtectDeadSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("removeprotectdead");
        }

        @Test
        @DisplayName("should register removeenchantpoint subcommand")
        void shouldRegisterRemoveEnchantPointSubCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("removeenchantpoint");
        }

        @Test
        @DisplayName("should register command executor until next command")
        void shouldRegisterCommandExecutorUntilNextCommand() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder).commandExecutorUntilNextCommand(any());
        }

        @Test
        @DisplayName("should register multiple tab completers")
        void shouldRegisterMultipleTabCompleters() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(10)).tabCompleter(any());
        }

        @Test
        @DisplayName("should call end many times to close deep command tree")
        void shouldCallEndManyTimes() {
            commandGiveItem.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(20)).end();
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> commandGiveItem.onRegister(mockBuilder));
        }
    }
}
