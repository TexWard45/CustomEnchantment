package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.bafframework.custommenu.menu.builder.MenuOpener;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.menu.artifactupgrade.ArtifactUpgradeCustomMenu;
import com.bafmc.customenchantment.menu.artifactupgrade.ArtifactUpgradeExtraData;
import com.bafmc.customenchantment.menu.artifactupgrade.ArtifactUpgradeSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArtifactUpgradeCommand implements AdvancedCommandExecutor {

    private final CustomEnchantment plugin;

    public ArtifactUpgradeCommand(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Argument arg) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        ArtifactUpgradeSettings settings = com.bafmc.customenchantment.menu.artifactupgrade.ArtifactUpgradeMenu.getSettings();
        if (settings == null) {
            plugin.getLogger().severe("[ArtifactUpgradeCommand] ArtifactUpgradeSettings is NULL!");
            player.sendMessage("\u00a7cError: Artifact upgrade settings not loaded!");
            return true;
        }

        ArtifactUpgradeExtraData extraData = new ArtifactUpgradeExtraData(settings);

        try {
            MenuOpener.builder()
                    .player(player)
                    .menuData(plugin, ArtifactUpgradeCustomMenu.MENU_NAME)
                    .extraData(extraData)
                    .async(false)
                    .build();
        } catch (Exception e) {
            plugin.getLogger().severe("[ArtifactUpgradeCommand] Exception opening menu: " + e.getMessage());
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "[ArtifactUpgradeCommand] Stack trace:", e);
            player.sendMessage("\u00a7cError opening menu: " + e.getMessage());
        }

        return true;
    }
}
