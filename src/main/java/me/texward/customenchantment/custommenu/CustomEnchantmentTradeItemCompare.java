package me.texward.customenchantment.custommenu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.item.CEWeaponAbstract;
import me.texward.custommenu.menu.trade.TradeItemCompare;
import me.texward.texwardlib.api.PlaceholderAPI;
import me.texward.texwardlib.configuration.AdvancedConfigurationSection;

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

		return weapon1.exportTo().isSimilar(weapon2.exportTo());
	}

}
