package com.bafmc.customenchantment.api;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.enchant.CEEnchant;
import com.bafmc.customenchantment.enchant.CEGroup;
import com.bafmc.customenchantment.enchant.CESimple;
import com.bafmc.customenchantment.guard.PlayerGuard;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CEAPI {
	public static CEPlayer getCEPlayer(Player player) {
		return CustomEnchantment.instance().getCEPlayerMap().getCEPlayer(player);
	}

	public static PlayerGuard getPlayerGuard(Player player) {
		return CustomEnchantment.instance().getGuardManager().getPlayerGuard(player);
	}

	public static CEEnchant getCEEnchant(String name) {
		return CustomEnchantment.instance().getCEEnchantMap().get(name);
	}

	public static CEGroup getCEGroup(String name) {
		return CustomEnchantment.instance().getCEGroupMap().get(name);
	}

	public static ItemStack getCEBookItemStack(CESimple ceSimple) {
		return ((CEBookStorage) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.BOOK))
				.getCEBook(ceSimple).exportTo();
	}

	public static ItemStack getCEBookItemStack(String type, CESimple ceSimple) {
		return ((CEBookStorage) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.BOOK))
				.getCEBook(type, ceSimple).exportTo();
	}

	public static boolean isCEItem(ItemStack itemStack) {
		return CEItemRegister.isCEItem(itemStack);
	}

	public static boolean isCEItem(ItemStack itemStack, String type) {
		return CEItemRegister.isCEItem(itemStack, type);
	}

	public static CEItem getCEItem(ItemStack itemStack, String type) {
		return CEItemRegister.getCEItem(itemStack, type);
	}

	public static CEItem getCEItemByStorage(String type, String pattern) {
		return CustomEnchantment.instance().getCEItemStorageMap().get(type).get(pattern);
	}

	public static CEItem getCEItem(ItemStack itemStack) {
		return CEItemRegister.getCEItem(itemStack);
	}

	public static ItemStack getVanillaItemStack(String key) {
		return ((VanillaItemStorage) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.STORAGE))
				.getItemStackByParameter(new Parameter(Arrays.asList(key)));
	}

	public static List<ItemStack> getVanillaItemStacks(String key) {
		return ((VanillaItemStorage) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.STORAGE))
				.getItemStacksByParameter(new Parameter(Arrays.asList(key)));
	}

	public static Map<EquipSlot, CEWeaponAbstract> getCEWeaponMap(Player player) {
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		Map<EquipSlot, CEWeaponAbstract> map = new LinkedHashMap<EquipSlot, CEWeaponAbstract>();
		for (EquipSlot slot : EquipSlot.ALL_ARRAY) {
			CEWeaponAbstract weapon = cePlayer.getSlot(slot);
			if (weapon == null) {
				continue;
			}
			map.put(slot, weapon);
		}
		return map;
	}

	public static Map<EquipSlot, CEWeaponAbstract> getCEWeaponMap(Player player, EquipSlot... slots) {
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		Map<EquipSlot, CEWeaponAbstract> map = new LinkedHashMap<>();
		for (EquipSlot slot : slots) {
			CEWeaponAbstract weapon = cePlayer.getSlot(slot);
			if (weapon == null) {
				continue;
			}
			map.put(slot, weapon);
		}
		return map;
	}
}
