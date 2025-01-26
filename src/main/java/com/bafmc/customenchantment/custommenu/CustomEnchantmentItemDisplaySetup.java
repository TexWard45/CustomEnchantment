package com.bafmc.customenchantment.custommenu;

import com.bafmc.bukkit.api.PlaceholderAPI;
import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.custommenu.menu.ItemDisplaySetup;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantmentItemDisplaySetup extends ItemDisplaySetup {
	public String getKey() {
		return "customenchantment";
	}

	public ItemStack getItemStack(Player player, ItemStack itemStack, AdvancedConfigurationSection dataConfig) {
		if (dataConfig == null || !dataConfig.isSet("type")) {
			return itemStack;
		}
		
		int amount = dataConfig.getInt("amount", 1);

		if (dataConfig.getString("type").equals("storage")) {
			String name = PlaceholderAPI.setPlaceholders(player, dataConfig.getString("name"));

			if (name != null) {
				itemStack = CEAPI.getVanillaItemStack(name);
				itemStack.setAmount(amount);
				return itemStack;
			}
		}

		if (dataConfig.getString("type").equals("gem")) {
			String name = PlaceholderAPI.setPlaceholders(player, dataConfig.getString("name"));
			int level = dataConfig.getInt("level", 1);

			return CEAPI.getGemItemStack(name, level);
		}

		itemStack = CEAPI.getCEItemByStorage(dataConfig.getString("type"), dataConfig.getString("name")).exportTo();
		itemStack.setAmount(amount);
		return itemStack;
	}
}
