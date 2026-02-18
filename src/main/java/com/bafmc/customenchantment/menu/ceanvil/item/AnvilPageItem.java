package com.bafmc.customenchantment.menu.ceanvil.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;
import com.bafmc.customenchantment.menu.ceanvil.handler.Slot2Handler;

public class AnvilPageItem extends AbstractItem<CEAnvilCustomMenu> {

    @Override
    public String getType() {
        return "anvil-page";
    }

    @Override
    public void handleClick(ClickData data) {
        String pageAction = itemData.getDataConfig().getString("page-action");
        if (pageAction == null) {
            return;
        }

        Slot2Handler handler = menu.getExtraData().getActiveHandler();
        if (handler != null) {
            handler.clickProcess(menu, pageAction);
        }
    }
}
