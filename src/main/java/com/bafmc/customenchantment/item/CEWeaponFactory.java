package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CEWeaponFactory extends CEItemFactory<CEWeapon> {
    public CEWeapon create(ItemStack itemStack) {
        return new CEWeapon(itemStack);
    }
}
