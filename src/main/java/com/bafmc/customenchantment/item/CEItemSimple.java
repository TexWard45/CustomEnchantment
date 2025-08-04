package com.bafmc.customenchantment.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class CEItemSimple {
    private ItemStack itemStack;
    private String type;
    private String pattern;

    public CEItem getCEItem() {
        return CEItemRegister.getCEItem(itemStack);
    }
}
