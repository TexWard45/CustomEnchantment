package com.bafmc.customenchantment.menu.tinkerer.item;

import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.menu.tinkerer.TinkererCustomMenu;
import com.bafmc.customenchantment.menu.tinkerer.TinkererExtraData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Custom item for Accept/Confirm button - confirms tinkerer action
 */
public class TinkerAcceptItem extends AbstractItem<TinkererCustomMenu> {

    @Override
    public String getType() {
        return CEConstants.MenuItemType.ACCEPT;
    }

    @Override
    public void handleClick(ClickData data) {
        Player player = data.getPlayer();

        // Confirm the tinkerer action
        TinkererExtraData.TinkererConfirmReason reason = menu.confirmTinkerer();

        // Send message to player
        CustomEnchantmentMessage.send(player, reason);
    }

    @Override
    public ItemStack setupItemStack() {
        // Use the item from YAML configuration
        return itemData.getItemStack();
    }

    @Override
    public boolean canLoadItem() {
        // Always show the accept button
        return true;
    }
}
