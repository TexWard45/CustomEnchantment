package me.texward.customenchantment.config;

import java.util.concurrent.ConcurrentHashMap;

import me.texward.customenchantment.menu.BookcraftMenu;
import me.texward.customenchantment.menu.BookcraftSettings;
import me.texward.texwardlib.configuration.AbstractConfig;
import me.texward.texwardlib.configuration.AdvancedConfigurationSection;

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
