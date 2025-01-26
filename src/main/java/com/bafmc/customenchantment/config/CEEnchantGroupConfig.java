package com.bafmc.customenchantment.config;

import java.util.Set;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.enchant.CEGroup;
import com.bafmc.customenchantment.enchant.Priority;
import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.utils.GaussianRandomRangeInt;
import com.bafmc.bukkit.utils.RandomRangeInt;
import com.bafmc.bukkit.utils.SparseMap;

public class CEEnchantGroupConfig extends AbstractConfig {

	protected void loadConfig() {
		Set<String> groupKeys = config.getKeys(false);

		for (String key : groupKeys) {
			CEGroup ceGroup = loadCEGroup(key, config.getAdvancedConfigurationSection(key));
			CustomEnchantment.instance().getCeGroupMap().put(ceGroup.getName(), ceGroup);
		}
	}

	public CEGroup loadCEGroup(String key, AdvancedConfigurationSection config) {
		String name = key;
		String display = config.getString("display");
        String enchantDisplay = config.getString("enchant-display");
        String bookDisplay = config.getString("book-display");
		String prefix = config.getString("prefix");
		boolean disableEnchantLore = config.getBoolean("disable-enchant-lore");
		RandomRangeInt success = new GaussianRandomRangeInt(config.getString("success"), config.getDouble("success-sigma", 0d));
		RandomRangeInt destroy = new GaussianRandomRangeInt(config.getString("destroy"), config.getDouble("destroy-sigma", 1d));
		int valuable = config.getInt("valuable");
		Priority priority = Priority.valueOf(config.getString("priority", "NORMAL"));
        boolean craft = config.getBoolean("craft");

		return new CEGroup(name, display, enchantDisplay, bookDisplay, prefix, disableEnchantLore, success, destroy, valuable, priority, craft);
	}
}
