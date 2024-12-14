package com.bafmc.customenchantment.feature.item;

import com.bafmc.bukkit.feature.argument.ArgumentLine;
import com.bafmc.bukkit.feature.item.AbstractItem;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantmentItem extends AbstractItem {
    @Override
    public boolean isMatch(ItemStack itemStack, ArgumentLine argumentLine) {
        String type = argumentLine.getString(0);
        ArgumentLine argParameter = new ArgumentLine(argumentLine.toString(), 1);
        Parameter parameter = Parameter.fromArgumentLine(argParameter);
        return CustomEnchantment.instance().getCeItemStorageMap().get(type).getItemStacksByParameter(parameter).get(0).isSimilar(itemStack);
    }

    @Override
    public ItemStack getItemStack(ArgumentLine argumentLine) {
        String type = argumentLine.getString(0);
        ArgumentLine argParameter = new ArgumentLine(argumentLine.toString(), 1);
        Parameter parameter = Parameter.fromArgumentLine(argParameter);
        return CustomEnchantment.instance().getCeItemStorageMap().get(type).getItemStacksByParameter(parameter).get(0);
    }

    @Override
    public String getType() {
        return "CUSTOM_ENCHANTMENT";
    }
}
