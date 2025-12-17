package com.bafmc.customenchantment.item.outfit;

import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.utils.RandomRangeInt;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CEOutfitStorage extends CEItemStorage<CEOutfit> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEOutfit getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEOutfit) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.OUTFIT).get(name);
	}

	public List<ItemStack> getItemStacksByParameter(Parameter parameter) {
		CEOutfit ceItem = getByParameter(parameter);
		if (ceItem == null) {
			return null;
		}

		int level = 1;
		if (parameter.size() <= 3) {
			level = parameter.getInteger(1, 1);
		}

		int amount = 1;
		if (parameter.size() > 2) {
			amount = new RandomRangeInt(parameter.getString(parameter.size() - 1, "1")).getValue();
		}
		amount = Math.max(amount, 1);

		ItemStack itemStack = ceItem.exportTo();

		// Clone itemStack to avoid modifying the original itemStack
		CEOutfit newArtifact = new CEOutfit(itemStack);
		newArtifact.getData().setLevel(level);
		itemStack = newArtifact.exportTo();

		itemStack = ItemStackUtils.getItemStackWithPlaceholder(itemStack, null);
		itemStack = ItemStackUtils.updateColorToItemStack(itemStack);
		return ItemStackUtils.getItemStacks(itemStack, amount);
	}

	public ItemStack getItemStackByParameter(String name, int level, int amount) {
		CEOutfit ceOutfit = (CEOutfit) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.OUTFIT).get(name);
		ItemStack itemStack = ceOutfit.exportTo();

		// Clone itemStack to avoid modifying the original itemStack
		CEOutfit newArtifact = new CEOutfit(itemStack);
		newArtifact.getData().setLevel(level);
		itemStack = newArtifact.exportTo();

		itemStack = ItemStackUtils.getItemStackWithPlaceholder(itemStack, null);
		itemStack = ItemStackUtils.updateColorToItemStack(itemStack);
		return ItemStackUtils.getItemStacks(itemStack, amount).get(0);
	}
}
