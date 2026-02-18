package com.bafmc.customenchantment.menu.bookupgrade.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeCustomMenu;

import java.util.List;

public class IngredientPreviewItem extends AbstractItem<BookUpgradeCustomMenu> {

    @Override
    public String getType() {
        return "ingredient-preview";
    }

    @Override
    public void handleClick(ClickData data) {
        int clickedSlot = data.getEvent().getSlot();

        List<Integer> slots = menu.getSlotsByName("ingredient-preview");
        List<Integer> reordered = BookUpgradeCustomMenu.reorderSlots(slots);

        int slotIndex = -1;
        for (int i = 0; i < reordered.size(); i++) {
            if (reordered.get(i) == clickedSlot) {
                slotIndex = i;
                break;
            }
        }

        if (slotIndex >= 0) {
            menu.returnIngredient(slotIndex);
        }
    }
}
