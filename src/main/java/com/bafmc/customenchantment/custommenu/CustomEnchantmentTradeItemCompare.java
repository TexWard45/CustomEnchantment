package com.bafmc.customenchantment.custommenu;

import com.bafmc.bukkit.api.PlaceholderAPI;
import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.gem.CEGemSimple;
import com.bafmc.custommenu.menu.trade.TradeItemCompare;
import com.bafmc.custommenu.menu.trade.TradeItemRequiredHistory;
import com.bafmc.finditem.item.UniqueItem;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomEnchantmentTradeItemCompare extends TradeItemCompare {

	public String getIdentify() {
		return "customenchantment";
	}

	public ItemStack setupItemStack(Player player, ItemStack itemStack, AdvancedConfigurationSection dataConfig, TradeItemRequiredHistory history) {
		if (dataConfig == null || !dataConfig.isSet("type")) {
			return itemStack;
		}

		String type = dataConfig.getString("type", null);
		String name = dataConfig.getString("name", null);
		if (type == null || name == null) {
			return itemStack;
		}

		name = PlaceholderAPI.setPlaceholders(player, dataConfig.getString("name"));
		if (name == null) {
			return itemStack;
		}

		if (type.equals(CEItemType.STORAGE)) {
			return CEAPI.getVanillaItemStack(name);
		}

		if (type.equals(CEItemType.WEAPON)) {
			return getWeaponMigration(type, name, history);
		}

		if (type.equals(CEItemType.GEM)) {
			int level = dataConfig.getInt("level", 1);
			return CEAPI.getGemItemStack(name, level);
		}

		return CEAPI.getCEItemByStorage(type, name).exportTo();
	}

	public ItemStack getWeaponMigration(String type, String name, TradeItemRequiredHistory history) {
		ItemStack itemStack = CEAPI.getCEItemByStorage(type, name).exportTo();
		if (itemStack == null) {
			return itemStack;
		}

		CEWeaponAbstract resultWeapon = (CEWeaponAbstract) CEAPI.getCEItem(itemStack);
		CEWeaponAbstract currentWeapon = getCurrentWeapon(history);
		if (resultWeapon == null || currentWeapon == null) {
			return itemStack;
		}
		// Migrate weapon data
		WeaponData resultWeaponData = resultWeapon.getWeaponData();
		WeaponData currentWeaponData = currentWeapon.getWeaponData();

		resultWeaponData.setExtraEnchantPoint(currentWeaponData.getExtraEnchantPoint());
		resultWeaponData.setExtraProtectDead(currentWeaponData.getExtraProtectDead());
		resultWeaponData.setExtraProtectDestroy(currentWeaponData.getExtraProtectDestroy());

		// Migrate enchant
		WeaponEnchant resultWeaponWeaponEnchant = resultWeapon.getWeaponEnchant();
		WeaponEnchant currentWeaponWeaponEnchant = currentWeapon.getWeaponEnchant();

		List<String> resultEnchantList = new ArrayList<>();
		for (CEEnchantSimple ceEnchantSimple : resultWeaponWeaponEnchant.getCESimpleList()) {
			resultEnchantList.add(ceEnchantSimple.getName());
		}

		for (CEEnchantSimple ceEnchantSimple : currentWeaponWeaponEnchant.getCESimpleList()) {
			if (resultEnchantList.contains(ceEnchantSimple.getName())) { // Skip if already exist
				continue;
			}

			resultWeaponWeaponEnchant.forceAddCESimple(ceEnchantSimple);
		}

		// Migrate gem
		WeaponGem resultWeaponGem = resultWeapon.getWeaponGem();
		WeaponGem currentWeaponGem = currentWeapon.getWeaponGem();
		for (CEGemSimple ceGemSimple : currentWeaponGem.getCEGemSimpleList()) {
			resultWeaponGem.addCEGemSimple(ceGemSimple);
		}

		// Migrate display
		WeaponDisplay resultWeaponDisplay = resultWeapon.getWeaponDisplay();
		WeaponDisplay currentWeaponDisplay = currentWeapon.getWeaponDisplay();

		if (currentWeaponDisplay.getDisplayName() != null) {
			resultWeaponDisplay.setDisplayName(currentWeaponDisplay.getDisplayName());
		}

		// Migrate enchantment
		Map<Enchantment, Integer> enchantmentMap = new HashMap<>(resultWeapon.getDefaultItemStack().getEnchantments());
		for (Map.Entry<Enchantment, Integer> entry : currentWeapon.getDefaultItemStack().getEnchantments().entrySet()) {
			if (enchantmentMap.containsKey(entry.getKey())) {
				continue;
			}

			enchantmentMap.put(entry.getKey(), entry.getValue());
		}
		resultWeaponDisplay.setEnchantMap(enchantmentMap);

		return resultWeapon.exportTo();
	}

	public CEWeaponAbstract getCurrentWeapon(TradeItemRequiredHistory history) {
		if (history == null) {
			return null;
		}

		for (TradeItemRequiredHistory.Data data : history.getMap().values()) {
			for (ItemStack itemStack : data.getTradedItemStackList()) {
				CEItem ceItem = CEAPI.getCEItem(itemStack);
				if (ceItem instanceof CEWeaponAbstract) {
					return (CEWeaponAbstract) ceItem;
				}
			}
		}

		return null;
	}

	public boolean isSimilarItem(Player player, ItemStack itemStack1, ItemStack itemStack2) {
		CEItem ceItem1 = CEAPI.getCEItem(itemStack1);
		CEItem ceItem2 = CEAPI.getCEItem(itemStack2);

		if (ceItem1 == null || ceItem2 == null) {
			return false;
		}

		if (!(ceItem1 instanceof CEWeaponAbstract weapon1) || !(ceItem2 instanceof CEWeaponAbstract weapon2)) {
			return false;
		}

		CEItemData weaponData1 = weapon1.getData();
		CEItemData weaponData2 = weapon2.getData();

		if (weaponData1 != null && weaponData2 != null) {
			return weapon1.getData().getPattern().equals(weapon2.getData().getPattern());
		}

		weapon1.clearTimeModifier();
		weapon1.clearRepairCost();
		weapon2.clearTimeModifier();
		weapon2.clearRepairCost();

		if (weapon1.getWeaponAttribute().equals(weapon2.getWeaponAttribute())) {
			weapon1.clearAttribute();
			weapon2.clearAttribute();
		}

        ItemStack weaponItemStack1 = weapon1.exportTo();
        ItemStack weaponItemStack2 = weapon2.exportTo();
        if (Bukkit.getPluginManager().isPluginEnabled("FindItem")) {

            UniqueItem uniqueItem = new UniqueItem(weaponItemStack1);
            if (uniqueItem.getId() != null) {
                uniqueItem.deleteLoreFormat();
                uniqueItem.removeId();

                weaponItemStack1 = uniqueItem.getItemStack();
            }

            uniqueItem = new UniqueItem(weaponItemStack2);
            if (uniqueItem.getId() != null) {
                uniqueItem.deleteLoreFormat();
                uniqueItem.removeId();

                weaponItemStack2 = uniqueItem.getItemStack();
            }
        }

		return weaponItemStack1.isSimilar(weaponItemStack2);
	}

}
