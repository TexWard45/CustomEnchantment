package me.texward.customenchantment.item;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

public class VanillaItem extends CEItem<CEItemData> {
	public VanillaItem(ItemStack itemStack) {
		super(CEItemType.STORAGE, itemStack);
	}

	public void importFrom(ItemStack source) {
	}

	public ItemStack exportTo(CEItemData data) {
		return getDefaultItemStack().clone();
	}
	
	public Map<String, String> getPlaceholder(CEItemData data) {
		return null;
	}
}
