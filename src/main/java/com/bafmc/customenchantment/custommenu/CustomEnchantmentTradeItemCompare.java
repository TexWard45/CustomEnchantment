package com.bafmc.customenchantment.custommenu;

import com.bafmc.finditem.item.UniqueItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.custommenu.menu.trade.TradeItemCompare;
import com.bafmc.bukkit.api.PlaceholderAPI;
import com.bafmc.bukkit.config.AdvancedConfigurationSection;

public class CustomEnchantmentTradeItemCompare extends TradeItemCompare {

	public String getIdentify() {
		return "customenchantment";
	}

	public ItemStack setupItemStack(Player player, ItemStack itemStack, AdvancedConfigurationSection dataConfig) {
		if (dataConfig == null || !dataConfig.isSet("type")) {
			return itemStack;
		}

		if (dataConfig.getString("type").equals("storage")) {
			String name = PlaceholderAPI.setPlaceholders(player, dataConfig.getString("name"));

			if (name != null) {
				return CEAPI.getVanillaItemStack(name);
			}
		}

		return CEAPI.getCEItemByStorage(dataConfig.getString("type"), dataConfig.getString("name")).exportTo();
	}

	public boolean isSimilarItem(Player player, ItemStack itemStack1, ItemStack itemStack2) {
		CEItem ceItem1 = CEAPI.getCEItem(itemStack1);
		CEItem ceItem2 = CEAPI.getCEItem(itemStack2);

		if (ceItem1 == null || ceItem2 == null) {
			return false;
		}

		if (!(ceItem1 instanceof CEWeaponAbstract) || !(ceItem2 instanceof CEWeaponAbstract)) {
			return false;
		}
		CEWeaponAbstract weapon1 = (CEWeaponAbstract) ceItem1;
		CEWeaponAbstract weapon2 = (CEWeaponAbstract) ceItem2;
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
