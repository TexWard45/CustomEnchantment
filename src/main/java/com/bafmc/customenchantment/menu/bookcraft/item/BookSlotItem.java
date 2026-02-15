package com.bafmc.customenchantment.menu.bookcraft.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftCustomMenu;

/**
 * Book slot item - allows players to click to return books
 */
public class BookSlotItem extends AbstractItem<BookCraftCustomMenu> {

    @Override
    public String getType() {
        return "book";
    }

    @Override
    public void handleClick(ClickData data) {
        // Return book at this slot to player
        menu.returnBook(data.getClickedSlot());
    }
}
