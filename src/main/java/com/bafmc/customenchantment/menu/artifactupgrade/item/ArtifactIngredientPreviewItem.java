package com.bafmc.customenchantment.menu.artifactupgrade.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.artifactupgrade.ArtifactUpgradeCustomMenu;

import java.util.List;

public class ArtifactIngredientPreviewItem extends AbstractItem<ArtifactUpgradeCustomMenu> {

    @Override
    public String getType() {
        return "artifact-ingredient-preview";
    }

    @Override
    public void handleClick(ClickData data) {
        int clickedSlot = data.getEvent().getSlot();

        List<Integer> slots = menu.getSlotsByName("ingredient-preview");

        int slotIndex = -1;
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) == clickedSlot) {
                slotIndex = i;
                break;
            }
        }

        if (slotIndex >= 0) {
            menu.returnIngredientAt(slotIndex);
        }
    }
}
