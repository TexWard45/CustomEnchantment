package me.texward.customenchantment.item;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.api.Parameter;
import me.texward.texwardlib.util.ItemStackUtils;
import me.texward.texwardlib.util.RandomRangeInt;
import me.texward.texwardlib.util.StorageMap;

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
		return ItemStackUtils.getItemStacks(itemStack, amount);
	}
	
	public ItemStack getItemStackByParameter(Parameter parameter) {
		return getItemStacksByParameter(parameter).get(0);
	}
}