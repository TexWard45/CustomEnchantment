package com.bafmc.customenchantment.menu.bookupgrade;

import com.bafmc.custommenu.menu.CMenuView;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BookUpgradeMenuOpener {
    private static HashMap<String, BookUpgradeMenu> map = new HashMap<>();

    public static void setSettings(BookUpgradeSettings settings) {
        BookUpgradeMenu.setSettings(settings);
    }

    public static BookUpgradeMenu putBookUpgradeMenu(Player player, CMenuView cMenuView) {
        BookUpgradeMenu menu = map.get(player.getName());

        if (menu == null) {
            menu = new BookUpgradeMenu(cMenuView, player);
            map.put(player.getName(), menu);
        }
        return menu;
    }

    public static BookUpgradeMenu getBookUpgradeMenu(Player player) {
        return map.get(player.getName());
    }

    public static BookUpgradeMenu removeBookUpgradeMenu(Player player) {
        return map.remove(player.getName());
    }
}