package com.bafmc.customenchantment.menu.bookcraft.item;

import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftCustomMenu;

/**
 * Book slot item - allows players to click to return books
 */
public class BookSlotItem extends AbstractItem<BookCraftCustomMenu> {

    @Override
    public String getType() {
        return CEConstants.MenuItemType.BOOK;
    }

    @Override
    public void handleClick(ClickData data) {
        // Return book by YAML item name ("book1" or "book2")
        menu.returnBook(itemData.getId());
    }
}
