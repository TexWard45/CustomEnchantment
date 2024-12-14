package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.CEItemData;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.VanillaItem;
import com.bafmc.customenchantment.item.VanillaItemStorage;

import java.io.File;

public class VanillaItemConfig {

	public void loadConfig() {
		VanillaItemStorage storage = new VanillaItemStorage();

		AdvancedFileConfiguration saveConfig = new AdvancedFileConfiguration(CustomEnchantment.instance().getSaveItemFile());
		storage.setSaveConfig(saveConfig);

		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.STORAGE, storage);

		File folder = CustomEnchantment.instance().getStorageItemFolder();
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".yml")) {
				AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(file);
				for (String key : fileConfig.getKeys(false)) {
					VanillaItem item = new VanillaItem(fileConfig.getItemStack(key, true, true));
					item.setData(new CEItemData() {
						public String getPattern() {
							return key;
						}
					});
					storage.put(key, item);
				}
			}
		}
	}

}