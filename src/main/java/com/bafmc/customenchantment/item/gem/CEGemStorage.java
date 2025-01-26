package com.bafmc.customenchantment.item.gem;

import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.utils.RandomRangeInt;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CEGemStorage extends CEItemStorage<CEGem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEGem getByParameter(Parameter parameter) {
		String name = parameter.getString(0);

		CEItem ceItem = CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.GEM).get(name);
		if (ceItem == null) {
			return null;
		}

		ItemStack itemStack = ceItem.exportTo();

		int level = 1;
		if (parameter.size() <= 3) {
			level = parameter.getInteger(1, 1);
		}

		// Clone itemStack to avoid modifying the original itemStack
		CEGem ceGem = new CEGem(itemStack);
		ceGem.getData().setLevel(level);
		return ceGem;
	}

	public List<ItemStack> getItemStacksByParameter(Parameter parameter) {
		CEGem ceItem = getByParameter(parameter);
		if (ceItem == null) {
			return null;
		}

		int amount = 1;
		if (parameter.size() > 2) {
			amount = new RandomRangeInt(parameter.getString(parameter.size() - 1, "1")).getValue();
		}
		amount = Math.max(amount, 1);

		ItemStack itemStack = ceItem.exportTo();

		// Clone itemStack to avoid modifying the original itemStack
		itemStack = ItemStackUtils.getItemStackWithPlaceholder(itemStack, null);
		itemStack = ItemStackUtils.updateColorToItemStack(itemStack);
		return ItemStackUtils.getItemStacks(itemStack, amount);
	}
}
