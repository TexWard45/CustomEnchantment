package me.texward.customenchantment.config;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.item.CEItemType;
import me.texward.customenchantment.item.VanillaItem;
import me.texward.customenchantment.item.VanillaItemStorage;
import me.texward.texwardlib.configuration.AbstractConfig;
import me.texward.texwardlib.configuration.AdvancedFileConfiguration;

public class VanillaItemConfig extends AbstractConfig {

	protected void loadConfig() {
		VanillaItemStorage storage = new VanillaItemStorage();
		storage.setConfig(config);
		
		AdvancedFileConfiguration saveConfig = new AdvancedFileConfiguration(CustomEnchantment.instance().getSaveItemFile());
		storage.setSaveConfig(saveConfig);
		
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.STORAGE, storage);

		for (String key : config.getKeys(false)) {
			storage.put(key, new VanillaItem(config.getItemStack(key, true)));
		}
		
		for (String key : saveConfig.getKeys(false)) {
			storage.put(key, new VanillaItem(saveConfig.getItemStack(key, true)));
		}
	}

}