package com.bafmc.customenchantment.item.artifact;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CEArtifactFactory extends CEItemFactory<CEArtifact> {
    public CEArtifact create(ItemStack itemStack) {
        return new CEArtifact(itemStack);
    }
}
