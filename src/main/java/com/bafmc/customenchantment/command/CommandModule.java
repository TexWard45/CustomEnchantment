package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.command.TinkererCommand;
import com.bafmc.customenchantment.command.BookCraftCommand;
import com.bafmc.customenchantment.menu.artifactupgrade.ArtifactUpgradeMenu;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftMenuLegacy;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeMenu;
import com.bafmc.custommenu.api.CustomMenuAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

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
                CustomMenuAPI.getCPlayer(player).openCustomMenu(BookCraftMenuLegacy.MENU_NAME, true);
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

        // OLD: Legacy tinkerer command (kept for compatibility during migration)
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

        // NEW: Migrated tinkerer command using new CustomMenu BafFramework API
        getPlugin().getLogger().info("[CommandModule] Registering /tinkerer-new command...");
        AdvancedCommandBuilder tinkererNewBuilder = AdvancedCommandBuilder.builder()
                .plugin(getPlugin())
                .rootCommand("tinkerer-new");
        tinkererNewBuilder.commandExecutor(new TinkererCommand(getPlugin())).end();
        tinkererNewBuilder.build();
        getPlugin().getLogger().info("[CommandModule] /tinkerer-new command registered successfully");

        // NEW: Migrated bookcraft command using new CustomMenu BafFramework API
        getPlugin().getLogger().info("[CommandModule] Registering /bookcraft-new command...");
        AdvancedCommandBuilder bookcraftNewBuilder = AdvancedCommandBuilder.builder()
                .plugin(getPlugin())
                .rootCommand("bookcraft-new");
        bookcraftNewBuilder.commandExecutor(new BookCraftCommand(getPlugin())).end();
        bookcraftNewBuilder.build();
        getPlugin().getLogger().info("[CommandModule] /bookcraft-new command registered successfully");

        // NEW: Migrated ceanvil command using new CustomMenu BafFramework API
        getPlugin().getLogger().info("[CommandModule] Registering /ceanvil-new command...");
        AdvancedCommandBuilder ceanvilNewBuilder = AdvancedCommandBuilder.builder()
                .plugin(getPlugin())
                .rootCommand("ceanvil-new");
        ceanvilNewBuilder.commandExecutor(new CEAnvilCommand(getPlugin())).end();
        ceanvilNewBuilder.build();
        getPlugin().getLogger().info("[CommandModule] /ceanvil-new command registered successfully");

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
                if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) {
                    player.closeInventory();
                }
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
