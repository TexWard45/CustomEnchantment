package com.bafmc.customenchantment.api;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.enchant.CEEnchant;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.enchant.CEGroup;
import com.bafmc.customenchantment.guard.PlayerGuard;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.book.CEBookStorage;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CEAPI {
	public static CEPlayer getCEPlayer(Player player) {
		return CEPlayer.getCePlayerMap().getCEPlayer(player);
	}

	public static List<CEPlayer> getCEPlayers() {
		return CEPlayer.getCePlayerMap().getCEPlayers();
	}

	public static PlayerGuard getPlayerGuard(Player player) {
		return CustomEnchantment.instance().getGuardModule().getGuardManager().getPlayerGuard(player);
	}

	public static CEEnchant getCEEnchant(String name) {
		return CustomEnchantment.instance().getCeEnchantMap().get(name);
	}

	public static CEGroup getCEGroup(String name) {
		return CustomEnchantment.instance().getCeGroupMap().get(name);
	}

	public static ItemStack getCEBookItemStack(CEEnchantSimple ceEnchantSimple) {
		return ((CEBookStorage) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.BOOK))
				.getCEBook(ceEnchantSimple).exportTo();
	}

	public static ItemStack getCEBookItemStack(String type, CEEnchantSimple ceEnchantSimple) {
		return ((CEBookStorage) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.BOOK))
				.getCEBook(type, ceEnchantSimple).exportTo();
	}

	public static boolean isCEItem(ItemStack itemStack) {
		return CEItemRegister.isCEItem(itemStack);
	}

	public static boolean isCEItem(ItemStack itemStack, String type) {
		return CEItemRegister.isCEItem(itemStack, type);
	}

	public static CEItemStorage getCEItemStorage(String type) {
		return CustomEnchantment.instance().getCeItemStorageMap().get(type);
	}

	public static CEItem getCEItem(ItemStack itemStack, String type) {
		return CEItemRegister.getCEItem(itemStack, type);
	}

	public static CEItem getCEItemByStorage(String type, String pattern) {
		return CustomEnchantment.instance().getCeItemStorageMap().get(type).get(pattern);
	}

	public static CEItem getCEItem(ItemStack itemStack) {
		return CEItemRegister.getCEItem(itemStack);
	}

	public static CEItemSimple getCEItemSimple(ItemStack itemStack) {
		return CEItemRegister.getCEItemSimple(itemStack);
	}

	public static String getCEItemType(ItemStack itemStack) {
		return CEItemRegister.getCEItemType(itemStack);
	}

	public static ItemStack getVanillaItemStack(String key) {
		return CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.STORAGE)
				.getItemStackByParameter(new Parameter(Arrays.asList(key)));
	}

	public static List<ItemStack> getVanillaItemStacks(String key) {
		return CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.STORAGE)
				.getItemStacksByParameter(new Parameter(Arrays.asList(key)));
	}

	public static ItemStack getGemItemStack(String key, int level) {
		List<String> list = new ArrayList<>();
		list.add(key);
		list.add(String.valueOf(level));

		Parameter parameter = new Parameter(list);
		return (CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.GEM)).getItemStacksByParameter(parameter).get(0);
	}

	public static Map<EquipSlot, CEWeaponAbstract> getCEWeaponMap(Player player) {
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		Map<EquipSlot, CEWeaponAbstract> map = new LinkedHashMap<EquipSlot, CEWeaponAbstract>();
		for (EquipSlot slot : EquipSlot.ALL_ARRAY) {
			CEWeaponAbstract weapon = cePlayer.getEquipment().getSlot(slot);
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
			CEWeaponAbstract weapon = cePlayer.getEquipment().getSlot(slot);
			if (weapon == null) {
				continue;
			}
			map.put(slot, weapon);
		}
		return map;
	}
}
