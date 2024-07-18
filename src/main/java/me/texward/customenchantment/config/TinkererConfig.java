package me.texward.customenchantment.config;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com._3fmc.bukkit.feature.execute.Execute;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.menu.TinkererMenu;
import me.texward.customenchantment.menu.TinkererReward;
import me.texward.customenchantment.menu.TinkererSettings;
import me.texward.texwardlib.configuration.AbstractConfig;
import me.texward.texwardlib.configuration.AdvancedConfigurationSection;
import me.texward.texwardlib.util.StringUtils;

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
