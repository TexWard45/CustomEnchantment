package com.bafmc.customenchantment.config;

import java.util.Set;

import com.bafmc.customenchantment.ConfigVariable;
import com.bafmc.customenchantment.api.EntityTypeList;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.bukkit.config.AdvancedConfigurationSection;

public class CEConfig extends AbstractConfig {

	protected void loadConfig() {
		loadMaterialGroup(config.getAdvancedConfigurationSection("material-group"));
		loadEntityTypeGroup(config.getAdvancedConfigurationSection("entity-group"));

		CEWeapon.setWhitelist(new MaterialList(
				MaterialList.getMaterialList(config.getStringList("ce-item-material-whitelist"))));

		ConfigVariable.MOVE_EVENT_PERIOD = config.getLong("move-event-period", 1000);
		ConfigVariable.ENCHANT_DISABLE_WORLDS = config.getStringList("enchant-disable-worlds");
		ConfigVariable.MAX_ARTIFACT_USE_COUNT = config.getInt("max-artifact-use-count", 3);
	}

	public void loadMaterialGroup(AdvancedConfigurationSection config) {
		Set<String> keys = config.getKeys(false);

		for (String key : keys) {
			MaterialList.defineMaterialList(key, MaterialList.getMaterialList(config.getStringList(key)));
		}
	}

	public void loadEntityTypeGroup(AdvancedConfigurationSection config) {
		Set<String> keys = config.getKeys(false);

		for (String key : keys) {
			EntityTypeList.defineEntityTypeList(key, EntityTypeList.getEntityTypeList(config.getStringList(key)));
		}
	}
}
