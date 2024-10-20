package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CEItemRegister {
	public static final List<CEItemFactory> list = new ArrayList<>();

	public static boolean isCEItem(ItemStack itemStack) {
		return getCEItem(itemStack) != null;
	}

	public static boolean isCEItem(ItemStack itemStack, String type) {
		CEItem ceItem = getCEItem(itemStack);
		return ceItem != null ? ceItem.getType().equals(type) : false;
	}

	public static CEItem getCEItem(ItemStack itemStack, String type) {
		CEItem ceItem = getCEItem(itemStack);

		if (ceItem == null) {
			return null;
		}

		return ceItem.getType().equals(type) ? ceItem : null;
	}

	public static CEItem getCEItem(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}

		CEItem ceItem;
		for (CEItemFactory clazz : list) {
			try {
				ceItem = clazz.create(itemStack);
				if (!ceItem.isMatchType(ceItem.getType())) {
					continue;
				}
				return ceItem;
			} catch (Exception e) {
				continue;
			}
		}

		return null;
	}

	public static void register(CEItemFactory clazz) {
		if (!list.contains(clazz)) {
			list.add(clazz);
		}
	}

	public static void unregister(CEItemFactory clazz) {
		list.remove(clazz);
	}
}
