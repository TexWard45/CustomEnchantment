package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.bafframework.custommenu.menu.builder.MenuOpener;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeCustomMenu;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeExtraData;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BookUpgradeCommand implements AdvancedCommandExecutor {

    private final CustomEnchantment plugin;

    public BookUpgradeCommand(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Argument arg) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        BookUpgradeSettings settings = BookUpgradeCustomMenu.getSettings();
        if (settings == null) {
            plugin.getLogger().severe("[BookUpgradeCommand] BookUpgradeSettings is NULL!");
            player.sendMessage("\u00a7cError: Book upgrade settings not loaded!");
            return true;
        }

        BookUpgradeExtraData extraData = new BookUpgradeExtraData(settings);

        try {
            MenuOpener.builder()
                    .player(player)
                    .menuData(plugin, BookUpgradeCustomMenu.MENU_NAME)
                    .extraData(extraData)
                    .async(false)
                    .build();
        } catch (Exception e) {
            plugin.getLogger().severe("[BookUpgradeCommand] Exception opening menu: " + e.getMessage());
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "[BookUpgradeCommand] Stack trace:", e);
            player.sendMessage("\u00a7cError opening menu: " + e.getMessage());
        }

        return true;
    }
}
