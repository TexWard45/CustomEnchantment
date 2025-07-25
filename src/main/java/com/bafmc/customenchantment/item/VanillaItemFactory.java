package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class VanillaItemFactory extends CEItemFactory<VanillaItem> {
	public VanillaItem create(ItemStack itemStack) {
		return new VanillaItem(itemStack);
	}

	public boolean isMatchType(String type) {
		return type.equals(CEItemType.STORAGE);
	}
}
