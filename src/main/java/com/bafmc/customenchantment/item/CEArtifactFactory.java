package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CEArtifactFactory extends CEItemFactory<CEArtifact> {
    public CEArtifact create(ItemStack itemStack) {
        return new CEArtifact(itemStack);
    }
}
