package com.bafmc.customenchantment.config;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.VanillaItem;
import com.bafmc.customenchantment.item.VanillaItemStorage;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;

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