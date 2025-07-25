package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public abstract class CEItemFactory<T extends CEItem> {
    public abstract T create(ItemStack itemStack);

    public abstract boolean isMatchType(String type);

    public boolean isMatchType(ItemStack itemStack) {
        T item = create(itemStack);
        return item != null && isMatchType(item.getType());
    }

    public void register() {
        CEItemRegister.register(this);
    }

    public boolean isAutoGenerateNewItem() {
        return false;
    }
}
