package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.bafframework.custommenu.menu.builder.MenuOpener;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilExtraData;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CEAnvilCommand implements AdvancedCommandExecutor {

    private final CustomEnchantment plugin;

    public CEAnvilCommand(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Argument arg) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        CEAnvilExtraData extraData = new CEAnvilExtraData(new CEAnvilSettings());

        try {
            MenuOpener.builder()
                    .player(player)
                    .menuData(plugin, CEAnvilCustomMenu.MENU_NAME)
                    .extraData(extraData)
                    .async(false)
                    .build();
        } catch (Exception e) {
            plugin.getLogger().severe("[CEAnvilCommand] Exception opening menu: " + e.getMessage());
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "[CEAnvilCommand] Stack trace:", e);
            player.sendMessage("\u00a7cError opening menu: " + e.getMessage());
        }

        return true;
    }
}
