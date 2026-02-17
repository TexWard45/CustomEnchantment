package com.bafmc.customenchantment.menu.ceanvil.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;

public class AnvilSlotItem extends AbstractItem<CEAnvilCustomMenu> {

    @Override
    public String getType() {
        return "anvil-slot";
    }

    @Override
    public void handleClick(ClickData data) {
        String slotName = itemData.getDataConfig().getString("slot-name");
        if (slotName != null) {
            menu.returnItem(slotName);
        }
    }
}
