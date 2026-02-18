package com.bafmc.customenchantment.menu.artifactupgrade.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.artifactupgrade.ArtifactUpgradeCustomMenu;

public class SelectedArtifactItem extends AbstractItem<ArtifactUpgradeCustomMenu> {

    @Override
    public String getType() {
        return "selected-artifact";
    }

    @Override
    public void handleClick(ClickData data) {
        menu.returnSelectedArtifact();
    }
}
