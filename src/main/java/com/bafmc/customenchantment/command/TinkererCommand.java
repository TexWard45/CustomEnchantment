package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.bafframework.custommenu.menu.builder.MenuOpener;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.menu.tinkerer.TinkererCustomMenu;
import com.bafmc.customenchantment.menu.tinkerer.TinkererExtraData;
import com.bafmc.customenchantment.menu.tinkerer.TinkererSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to open Tinkerer menu using new CustomMenu BafFramework API
 * Replaces legacy CustomMenuAPI.openCustomMenu() pattern
 */
public class TinkererCommand implements AdvancedCommandExecutor {

    private final CustomEnchantment plugin;

    public TinkererCommand(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Argument arg) {
        plugin.getLogger().info("[TinkererCommand] Command executed by: " + sender.getName());

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            plugin.getLogger().warning("[TinkererCommand] Sender is not a player: " + sender.getClass().getName());
            return true;
        }

        Player player = (Player) sender;
        plugin.getLogger().info("[TinkererCommand] Player confirmed: " + player.getName());

        // Create ExtraData with TinkererSettings
        TinkererExtraData extraData = new TinkererExtraData();
        TinkererSettings settings = TinkererCustomMenu.getSettings();

        if (settings == null) {
            plugin.getLogger().severe("[TinkererCommand] TinkererSettings is NULL! Menu cannot open.");
            player.sendMessage("§cError: Tinkerer settings not loaded!");
            return true;
        }

        plugin.getLogger().info("[TinkererCommand] Settings loaded: size=" + settings.getSize());
        extraData.setSettings(settings);

        // Open menu using new MenuOpener API
        plugin.getLogger().info("[TinkererCommand] Opening menu with name: " + TinkererCustomMenu.MENU_NAME);
        try {
            MenuOpener.builder()
                    .player(player)
                    .menuData(plugin, TinkererCustomMenu.MENU_NAME)
                    .extraData(extraData)
                    .async(false)  // Open synchronously for immediate response
                    .build();
            plugin.getLogger().info("[TinkererCommand] MenuOpener.build() completed successfully");
        } catch (Exception e) {
            plugin.getLogger().severe("[TinkererCommand] Exception opening menu: " + e.getMessage());
            e.printStackTrace();
            player.sendMessage("§cError opening menu: " + e.getMessage());
        }

        return true;
    }
}
