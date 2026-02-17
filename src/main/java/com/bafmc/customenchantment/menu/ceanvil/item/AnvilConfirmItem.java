package com.bafmc.customenchantment.menu.ceanvil.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;

public class AnvilConfirmItem extends AbstractItem<CEAnvilCustomMenu> {

    @Override
    public String getType() {
        return "anvil-confirm";
    }

    @Override
    public void handleClick(ClickData data) {
        menu.confirm();
    }
}
