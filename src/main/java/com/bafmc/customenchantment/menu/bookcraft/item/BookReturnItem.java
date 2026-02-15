package com.bafmc.customenchantment.menu.bookcraft.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftCustomMenu;

/**
 * Return button item for BookCraft menu
 * Closes the menu and returns all books to player inventory
 */
public class BookReturnItem extends AbstractItem<BookCraftCustomMenu> {

    public BookReturnItem() {
        // Constructor - item instance created
    }

    @Override
    public String getType() {
        return "return";
    }

    @Override
    public void handleClick(ClickData data) {
        // Close menu (handleClose will return books automatically)
        data.getPlayer().closeInventory();
    }
}
