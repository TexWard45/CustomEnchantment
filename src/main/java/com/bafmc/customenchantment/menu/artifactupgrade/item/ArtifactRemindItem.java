package com.bafmc.customenchantment.menu.artifactupgrade.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.menu.artifactupgrade.ArtifactUpgradeCustomMenu;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeConfirmReason;

public class ArtifactRemindItem extends AbstractItem<ArtifactUpgradeCustomMenu> {

    @Override
    public String getType() {
        return "artifact-remind";
    }

    @Override
    public void handleClick(ClickData data) {
        ArtifactUpgradeConfirmReason reason = menu.confirmUpgrade();
        CustomEnchantmentMessage.send(data.getPlayer(),
                "menu.artifactupgrade.confirm." + EnumUtils.toConfigStyle(reason));
    }
}
