package com.bafmc.customenchantment.config;

import java.util.concurrent.ConcurrentHashMap;

import com.bafmc.customenchantment.menu.BookcraftMenu;
import com.bafmc.customenchantment.menu.BookcraftSettings;
import com.bafmc.bukkit.config.AdvancedConfigurationSection;

public class BookcraftConfig extends AbstractConfig {

	protected void loadConfig() {
		ConcurrentHashMap<String, Double> moneyGroupRequireMap = getMoneyGroupRequireMap(
				config.getAdvancedConfigurationSection("money-require"));

		BookcraftSettings settings = new BookcraftSettings(moneyGroupRequireMap);
		BookcraftMenu.setSettings(settings);
	}

	public ConcurrentHashMap<String, Double> getMoneyGroupRequireMap(AdvancedConfigurationSection config) {
		ConcurrentHashMap<String, Double> map = new ConcurrentHashMap<String, Double>();

		for (String groupName : config.getKeys(false)) {
			map.put(groupName, config.getDouble(groupName));
		}

		return map;
	}

}
