package com.bafmc.customenchantment.menu.ceanvil;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class AnvilItemData {

    private ItemStack itemStack;
    private CEItem ceItem;

    public AnvilItemData(ItemStack itemStack, CEItem ceItem) {
        this.itemStack = itemStack.clone();
        this.ceItem = ceItem;
    }

    public void updateItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.ceItem = CEAPI.getCEItem(itemStack);
    }
}
