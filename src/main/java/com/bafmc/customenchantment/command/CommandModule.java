package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.menu.artifactupgrade.ArtifactUpgradeMenu;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftMenu;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeMenu;
import com.bafmc.custommenu.api.CustomMenuAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandModule extends PluginModule<CustomEnchantment> {
    public CommandModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        AdvancedCommandBuilder builder = AdvancedCommandBuilder.builder().plugin(getPlugin()).rootCommand("customenchantment");

        new CustomEnchantmentCommand(getPlugin()).onRegister(builder);

        builder.build();

        AdvancedCommandBuilder bookCraftBuilder = AdvancedCommandBuilder.builder().plugin(getPlugin()).rootCommand("bookcraft");
        bookCraftBuilder.commandExecutor(new AdvancedCommandExecutor() {
            public boolean onCommand(CommandSender sender, Argument arg) {
                if (!(sender instanceof Player)) {
                    return true;
                }
                Player player = (Player) sender;
                CustomMenuAPI.getCPlayer(player).openCustomMenu(BookCraftMenu.MENU_NAME, true);
                return true;
            }
        }).end();
        bookCraftBuilder.build();

        AdvancedCommandBuilder bookUpgradeBuilder = AdvancedCommandBuilder.builder().plugin(getPlugin()).rootCommand("bookupgrade");
        bookUpgradeBuilder.commandExecutor(new AdvancedCommandExecutor() {
            public boolean onCommand(CommandSender sender, Argument arg) {
                if (!(sender instanceof Player)) {
                    return true;
                }
                Player player = (Player) sender;
                CustomMenuAPI.getCPlayer(player).openCustomMenu(BookUpgradeMenu.MENU_NAME, true);
                return true;
            }
        }).end();
        bookUpgradeBuilder.build();

        AdvancedCommandBuilder artifactUpgradeBuilder = AdvancedCommandBuilder.builder().plugin(getPlugin()).rootCommand("artifactupgrade");
        artifactUpgradeBuilder.commandExecutor(new AdvancedCommandExecutor() {
            public boolean onCommand(CommandSender sender, Argument arg) {
                if (!(sender instanceof Player)) {
                    return true;
                }
                Player player = (Player) sender;
                CustomMenuAPI.getCPlayer(player).openCustomMenu(ArtifactUpgradeMenu.MENU_NAME, true);
                return true;
            }
        }).end();
        artifactUpgradeBuilder.build();

        AdvancedCommandBuilder tinkererBuilder = AdvancedCommandBuilder.builder().plugin(getPlugin()).rootCommand("tinkerer");
        tinkererBuilder.commandExecutor(new AdvancedCommandExecutor() {
            public boolean onCommand(CommandSender sender, Argument arg) {
                if (!(sender instanceof Player)) {
                    return true;
                }
                Player player = (Player) sender;
                CustomMenuAPI.getCPlayer(player).openCustomMenu("tinkerer", true);
                return true;
            }
        }).end();
        tinkererBuilder.build();

        AdvancedCommandBuilder anvilBuilder = AdvancedCommandBuilder.builder().plugin(getPlugin()).rootCommand("ceanvil");
        anvilBuilder.commandExecutor(new AdvancedCommandExecutor() {
            public boolean onCommand(CommandSender sender, Argument arg) {
                if (!(sender instanceof Player)) {
                    return true;
                }
                Player player = (Player) sender;
                CustomMenuAPI.getCPlayer(player).openCustomMenu("ce-anvil", true);
                return true;
            }
        }).end();
        anvilBuilder.build();

        AdvancedCommandBuilder equipment = AdvancedCommandBuilder.builder().plugin(getPlugin()).rootCommand("equipment");
        equipment.commandExecutor(new AdvancedCommandExecutor() {
            public boolean onCommand(CommandSender sender, Argument arg) {
                if (!(sender instanceof Player)) {
                    return true;
                }
                Player player = (Player) sender;
                CustomMenuAPI.getCPlayer(player).openCustomMenu("equipment", true);
                return true;
            }
        }).end();
        equipment.build();

        AdvancedCommandBuilder nameTagBuilder = AdvancedCommandBuilder.builder().plugin(getPlugin()).rootCommand("nametag");
        new CommandNameTag().onRegister(nameTagBuilder);
        nameTagBuilder.build();

        AdvancedCommandBuilder filterEnchantBuilder = AdvancedCommandBuilder.builder().plugin(getPlugin()).rootCommand("cefilter");
        new CommandFilterEnchant().onRegister(filterEnchantBuilder);
        filterEnchantBuilder.build();
    }
}
