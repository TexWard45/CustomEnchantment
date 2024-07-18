package me.texward.customenchantment.config;

import java.util.Set;

import me.texward.customenchantment.ConfigVariable;
import me.texward.customenchantment.api.EntityTypeList;
import me.texward.customenchantment.api.MaterialList;
import me.texward.customenchantment.item.CEWeapon;
import me.texward.texwardlib.configuration.AbstractConfig;
import me.texward.texwardlib.configuration.AdvancedConfigurationSection;

public class CEConfig extends AbstractConfig {

	protected void loadConfig() {
		loadMaterialGroup(config.getAdvancedConfigurationSection("material-group"));
		loadEntityTypeGroup(config.getAdvancedConfigurationSection("entity-group"));

		CEWeapon.setWhitelist(new MaterialList(
				MaterialList.getMaterialList(config.getStringList("ce-item-material-whitelist"))));

		ConfigVariable.MOVE_EVENT_PERIOD = config.getLong("move-event-period", 1000);
		ConfigVariable.ENCHANT_DISABLE_WORLDS = config.getStringList("enchant-disable-worlds");
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
