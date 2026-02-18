package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.bafframework.custommenu.menu.builder.MenuOpener;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftCustomMenu;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftExtraData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to open BookCraft menu using new CustomMenu BafFramework API
 * Replaces legacy CustomMenuAPI.openCustomMenu() pattern
 */
public class BookCraftCommand implements AdvancedCommandExecutor {

    private final CustomEnchantment plugin;

    public BookCraftCommand(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Argument arg) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // Create ExtraData
        BookCraftExtraData extraData = new BookCraftExtraData();

        // Open menu using new MenuOpener API
        try {
            MenuOpener.builder()
                    .player(player)
                    .menuData(plugin, BookCraftCustomMenu.MENU_NAME)
                    .extraData(extraData)
                    .async(false)  // Open synchronously for immediate response
                    .build();
        } catch (Exception e) {
            plugin.getLogger().severe("[BookCraftCommand] Exception opening menu: " + e.getMessage());
            player.sendMessage("§cError opening menu: " + e.getMessage());
        }

        return true;
    }
}
