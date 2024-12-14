package com.bafmc.customenchantment.menu.data;

import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import org.bukkit.inventory.ItemStack;

public class BookData {
    private ItemStack itemStack;
    private CEEnchantSimple ceEnchantSimple;

    public BookData(ItemStack itemStack, CEEnchantSimple ceEnchantSimple) {
        this.itemStack = itemStack;
        this.ceEnchantSimple = ceEnchantSimple;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public CEEnchantSimple getCESimple() {
        return ceEnchantSimple;
    }

    public void setCESimple(CEEnchantSimple ceEnchantSimple) {
        this.ceEnchantSimple = ceEnchantSimple;
    }

    public String toString() {
        return "BookData(ceSimple=" + this.getCESimple() + ")";
    }
}