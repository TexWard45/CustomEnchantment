package com.bafmc.customenchantment.menu.bookupgrade.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeCustomMenu;

public class BookUpgradeSlotItem extends AbstractItem<BookUpgradeCustomMenu> {

    @Override
    public String getType() {
        return "book-upgrade-slot";
    }

    @Override
    public void handleClick(ClickData data) {
        menu.returnMainBook();
        menu.updateMenu();
    }
}
