package com.bafmc.customenchantment.menu.bookcraft.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftCustomMenu;

/**
 * Book preview item - shows result book (no click action)
 */
public class BookPreviewItem extends AbstractItem<BookCraftCustomMenu> {

    @Override
    public String getType() {
        return "preview";
    }

    @Override
    public void handleClick(ClickData data) {
        // No action - preview is read-only
    }
}
