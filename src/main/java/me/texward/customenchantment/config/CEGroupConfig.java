package me.texward.customenchantment.config;

import java.util.Set;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.enchant.CEGroup;
import me.texward.customenchantment.enchant.Priority;
import me.texward.texwardlib.configuration.AbstractConfig;
import me.texward.texwardlib.configuration.AdvancedConfigurationSection;
import me.texward.texwardlib.util.GaussianRandomRangeInt;
import me.texward.texwardlib.util.RandomRangeInt;
import me.texward.texwardlib.util.SparseMap;

public class CEGroupConfig extends AbstractConfig {

	protected void loadConfig() {
		Set<String> groupKeys = config.getKeys(false);

		for (String key : groupKeys) {
			CEGroup ceGroup = loadCEGroup(key, config.getAdvancedConfigurationSection(key));
			CustomEnchantment.instance().getCEGroupMap().put(ceGroup.getName(), ceGroup);
		}
	}

	public CEGroup loadCEGroup(String key, AdvancedConfigurationSection config) {
		String name = key;
		String display = config.getStringColor("display");
		String prefix = config.getStringColor("prefix");
		boolean disableEnchantLore = config.getBoolean("disable-enchant-lore");
		RandomRangeInt success = new GaussianRandomRangeInt(config.getString("success"), config.getDouble("success-sigma", 0d));
		RandomRangeInt destroy = new GaussianRandomRangeInt(config.getString("destroy"), config.getDouble("destroy-sigma", 1d));
		int valuable = config.getInt("valuable");
		Priority priority = Priority.valueOf(config.getString("priority", "NORMAL"));

		return new CEGroup(name, display, prefix, disableEnchantLore, success, destroy, valuable, priority);
	}

	public SparseMap<String> loadLevels(AdvancedConfigurationSection config) {
		SparseMap<String> map = new SparseMap<String>();

		for (String levelKey : config.getKeys(false)) {
			try {
				map.put(Integer.valueOf(levelKey), config.getStringColor(levelKey));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return map;
	}
}
