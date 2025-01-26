package com.bafmc.customenchantment.menu.artifactupgrade;

import com.bafmc.custommenu.menu.CMenuView;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ArtifactUpgradeMenuOpener {
    private static HashMap<String, ArtifactUpgradeMenu> map = new HashMap<>();

    public static void setSettings(ArtifactUpgradeSettings settings) {
        ArtifactUpgradeMenu.setSettings(settings);
    }

    public static ArtifactUpgradeMenu putArtifactUpgradeMenu(Player player, CMenuView cMenuView) {
        ArtifactUpgradeMenu menu = map.get(player.getName());

        if (menu == null) {
            menu = new ArtifactUpgradeMenu(cMenuView, player);
            map.put(player.getName(), menu);
        }
        return menu;
    }

    public static ArtifactUpgradeMenu getArtifactUpgradeMenu(Player player) {
        return map.get(player.getName());
    }

    public static ArtifactUpgradeMenu removeArtifactUpgradeMenu(Player player) {
        return map.remove(player.getName());
    }
}