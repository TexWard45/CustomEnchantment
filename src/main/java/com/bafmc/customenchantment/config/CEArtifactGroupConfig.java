package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.utils.SparseMap;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.mask.group.CEArtifactGroup;

import java.util.List;
import java.util.Set;

public class CEArtifactGroupConfig extends AbstractConfig {
	protected void loadConfig() {
		Set<String> groupKeys = config.getKeySection("list", false);

		AdvancedConfigurationSection settingsConfig = config.getAdvancedConfigurationSection("settings");
		for (String key : groupKeys) {
			AdvancedConfigurationSection mainConfig = config.getAdvancedConfigurationSection("list." + key);
			CEArtifactGroup ceGroup = loadCEMaskGroup(key, mainConfig, settingsConfig);
			CustomEnchantment.instance().getCeArtifactGroupMap().put(ceGroup.getName(), ceGroup);
		}
	}

	public CEArtifactGroup loadCEMaskGroup(String key, AdvancedConfigurationSection mainConfig, AdvancedConfigurationSection settingsConfig) {
        String display = mainConfig.getString("display", settingsConfig.getString("display"));
		String itemDisplay = mainConfig.getString("item-display", settingsConfig.getString("item-display"));
		List<String> itemLore = mainConfig.getStringList("item-lore", settingsConfig.getStringList("item-lore"));
		SparseMap<String> levels = loadLevels(mainConfig, settingsConfig);

		return CEArtifactGroup.builder()
				.name(key)
				.display(display)
				.itemDisplay(itemDisplay)
				.itemLore(itemLore)
				.levelColors(levels)
				.build();
	}

	public SparseMap<String> loadLevels(AdvancedConfigurationSection mainConfig, AdvancedConfigurationSection settingsConfig) {
		SparseMap<String> map = new SparseMap<>();

		AdvancedConfigurationSection config = null;
		if (mainConfig.isSet("levels")) {
			config = mainConfig.getAdvancedConfigurationSection("levels");
		}else if (settingsConfig.isSet("levels")) {
			config = settingsConfig.getAdvancedConfigurationSection("levels");
		}

		if (config != null) {
			for (String levelKey : config.getKeys(false)) {
				try {
					map.put(Integer.valueOf(levelKey), config.getString(levelKey + ".color"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return map;
	}
}
