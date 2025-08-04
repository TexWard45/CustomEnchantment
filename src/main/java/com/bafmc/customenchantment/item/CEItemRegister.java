package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
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

	public static CEItemSimple getCEItemSimple(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}

		CECraftItemStackNMS craftItemStack = new CECraftItemStackNMS(itemStack);
		NMSNBTTagCompound tag = craftItemStack.getCECompound();

		if (tag.hasKey(CENBT.TYPE)) {
			String type = tag.getString(CENBT.TYPE);
			String pattern = tag.getString(CENBT.PATTERN);
			return new CEItemSimple(itemStack, type, pattern);
		}

		return null;
	}

	public static CEItem getCEItem(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}

		CECraftItemStackNMS craftItemStack = new CECraftItemStackNMS(itemStack);
		NMSNBTTagCompound tag = craftItemStack.getCECompound();

		if (tag.hasKey(CENBT.TYPE)) {
			String type = tag.getString(CENBT.TYPE);
			for (CEItemFactory clazz : list) {
				if (!clazz.isMatchType(type)) {
					continue;
				}

				return clazz.create(itemStack);
			}
		}

		for (CEItemFactory clazz : list) {
			if (!clazz.isAutoGenerateNewItem()) {
				continue;
			}

			if (!clazz.isMatchType(itemStack)) {
				continue;
			}

			return clazz.create(itemStack);
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

	public static String getCEItemType(ItemStack itemStack) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(itemStack);

		NMSNBTTagCompound tag = itemStackNMS.getCECompound();
		return tag.getString("type");
	}
}
