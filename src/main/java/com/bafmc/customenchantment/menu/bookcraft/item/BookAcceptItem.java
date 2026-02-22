package com.bafmc.customenchantment.menu.bookcraft.item;

import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftCustomMenu;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftExtraData;
import org.bukkit.entity.Player;

/**
 * Accept button - confirms book crafting
 */
public class BookAcceptItem extends AbstractItem<BookCraftCustomMenu> {

    @Override
    public String getType() {
        return CEConstants.MenuItemType.ACCEPT;
    }

    @Override
    public void handleClick(ClickData data) {
        Player player = data.getPlayer();

        // Confirm book craft
        BookCraftExtraData.BookConfirmReason reason = menu.confirmBookCraft();

        // Send feedback message
        CustomEnchantmentMessage.send(player, reason);
    }
}
