package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public abstract class CEItemFactory<T extends CEItem> {
    public abstract T create(ItemStack itemStack);

    public void register() {
        CEItemRegister.register(this);
    }
}
