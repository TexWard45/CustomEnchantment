package com.bafmc.customenchantment.item;

import java.util.List;

import com.bafmc.bukkit.utils.ItemStackUtils;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.bukkit.utils.RandomRangeInt;
import com.bafmc.bukkit.utils.StorageMap;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class CEItemStorage<T extends CEItem<? extends CEItemData>> extends StorageMap<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract T getByParameter(Parameter parameter);

	public List<ItemStack> getItemStacksByParameter(Parameter parameter) {
		T ceItem = getByParameter(parameter);

		if (ceItem == null) {
			return null;
		}

		int amount = 1;
		if (parameter.size() > 1) {
			amount = new RandomRangeInt(parameter.getString(parameter.size() - 1, "1")).getValue();
		}
		amount = Math.max(amount, 1);
		
		ItemStack itemStack = ceItem.exportTo();
        itemStack = ItemStackUtils.getItemStackWithPlaceholder(itemStack, null);
        itemStack = ItemStackUtils.updateColorToItemStack(itemStack);
		return ItemStackUtils.getItemStacks(itemStack, amount);
	}
	
	public ItemStack getItemStackByParameter(Parameter parameter) {
		return getItemStacksByParameter(parameter).get(0);
	}
}