package com.bafmc.customenchantment.item.artifact;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CEArtifactFactory extends CEItemFactory<CEArtifact> {
    public CEArtifact create(ItemStack itemStack) {
        return new CEArtifact(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.ARTIFACT);
    }
}
