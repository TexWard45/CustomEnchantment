package com.bafmc.customenchantment.menu.data;

import com.bafmc.customenchantment.enchant.CESimple;
import org.bukkit.inventory.ItemStack;

public class BookData {
    private ItemStack itemStack;
    private CESimple ceSimple;

    public BookData(ItemStack itemStack, CESimple ceSimple) {
        this.itemStack = itemStack;
        this.ceSimple = ceSimple;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public CESimple getCESimple() {
        return ceSimple;
    }

    public void setCESimple(CESimple ceSimple) {
        this.ceSimple = ceSimple;
    }

    public String toString() {
        return "BookData(ceSimple=" + this.getCESimple() + ")";
    }
}