package com.bafmc.customenchantment.config;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.bafmc.bukkit.feature.execute.Execute;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.menu.TinkererMenu;
import com.bafmc.customenchantment.menu.TinkererReward;
import com.bafmc.customenchantment.menu.TinkererSettings;
import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.utils.StringUtils;

public class TinkererConfig extends AbstractConfig {

	protected void loadConfig() {
		ConcurrentHashMap<String, TinkererReward> map = getTinkererRewardMap(
				config.getAdvancedConfigurationSection("tinkerers"));
		List<Integer> tinkerSlots = StringUtils.getIntList(config.getString("tinkerer-slots"), ",", "-");
		List<Integer> rewardSlots = StringUtils.getIntList(config.getString("reward-slots"), ",", "-");

		TinkererSettings settings = new TinkererSettings(map, tinkerSlots, rewardSlots);
		TinkererMenu.setSettings(settings);
	}

	public ConcurrentHashMap<String, TinkererReward> getTinkererRewardMap(AdvancedConfigurationSection config) {
		ConcurrentHashMap<String, TinkererReward> map = new ConcurrentHashMap<String, TinkererReward>();

		map.putAll(getTinkererBookMap("book", config.getAdvancedConfigurationSection("book")));

		return map;
	}

	public ConcurrentHashMap<String, TinkererReward> getTinkererBookMap(String key,
			AdvancedConfigurationSection config) {
		ConcurrentHashMap<String, TinkererReward> map = new ConcurrentHashMap<String, TinkererReward>();

		for (String group : config.getKeys(false)) {
			ConcurrentHashMap<String, TinkererReward> groupMap = getTinkererBookGroupMap(
					config.getAdvancedConfigurationSection(group));

			for (String level : groupMap.keySet()) {
				map.put(key + "." + group + "." + level, groupMap.get(level));
			}
		}

		return map;
	}

	public ConcurrentHashMap<String, TinkererReward> getTinkererBookGroupMap(AdvancedConfigurationSection config) {
		ConcurrentHashMap<String, TinkererReward> map = new ConcurrentHashMap<String, TinkererReward>();

		for (String level : config.getKeys(false)) {
			ItemStack itemStack = config.getItemStack(level + ".item");
			Execute execute = new Execute(config.getStringList(level + ".rewards"));

			TinkererReward reward = new TinkererReward(itemStack, execute);
			map.put(level, reward);
		}

		return map;
	}

}
