package me.texward.customenchantment.item;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.exception.CENotSuitableTypeException;

public class CEItemRegister {
	public static final List<Class<? extends CEItem>> list = new ArrayList<Class<? extends CEItem>>();

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
		
		for (Class<? extends CEItem> clazz : list) {
			try {
				Constructor<?> constructor = clazz.getConstructor(ItemStack.class);
				CEItem ceItem = (CEItem) constructor.newInstance(itemStack);
				if (!ceItem.isMatchType(ceItem.getType())) {
					throw new CENotSuitableTypeException("Not support that type");
				}
				return ceItem;
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}

	public static void register(Class<? extends CEItem> clazz) {
		if (!list.contains(clazz)) {
			list.add(clazz);
		}
	}

	public static void unregister(Class<? extends CEItem> clazz) {
		list.remove(clazz);
	}
}
