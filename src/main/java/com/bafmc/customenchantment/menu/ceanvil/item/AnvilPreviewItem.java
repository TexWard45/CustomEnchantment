package com.bafmc.customenchantment.menu.ceanvil.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;
import com.bafmc.customenchantment.menu.ceanvil.handler.Slot2Handler;

public class AnvilPreviewItem extends AbstractItem<CEAnvilCustomMenu> {

    @Override
    public String getType() {
        return "anvil-preview";
    }

    @Override
    public void handleClick(ClickData data) {
        String previewName = itemData.getDataConfig().getString("preview-name");
        if (previewName == null) {
            return;
        }

        Slot2Handler handler = menu.getExtraData().getActiveHandler();
        if (handler != null) {
            handler.clickProcess(menu, previewName);
        }
    }
}
