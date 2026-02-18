package com.bafmc.customenchantment.menu.tinkerer.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.tinkerer.TinkererCustomMenu;
import org.bukkit.inventory.ItemStack;

/**
 * Custom item for Tinkerer slots - returns item to player on click
 */
public class TinkerSlotItem extends AbstractItem<TinkererCustomMenu> {

    @Override
    public String getType() {
        return "tinker";
    }

    @Override
    public void handleClick(ClickData data) {
        int slot = data.getClickedSlot();

        // Return the item at this slot
        menu.returnItem(slot);
    }

    @Override
    public ItemStack setupItemStack() {
        // This item type is dynamically set by the menu's updateMenu() method
        // Return null as placeholder (menu sets actual items via setTemporaryItem)
        return null;
    }

    @Override
    public boolean canLoadItem() {
        // Always load tinker slot items (even if empty)
        return true;
    }
}
