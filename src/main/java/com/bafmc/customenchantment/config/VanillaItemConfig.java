package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.CEItemStorageMap;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.*;

import java.io.File;

public class VanillaItemConfig {

	public void loadConfig() {
		VanillaItemStorage storage = new VanillaItemStorage();

		AdvancedFileConfiguration saveConfig = new AdvancedFileConfiguration(CustomEnchantment.instance().getSaveItemFile());
		storage.setSaveConfig(saveConfig);

		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.STORAGE, storage);

		File folder = CustomEnchantment.instance().getStorageItemFolder();
		loadFile(folder, storage);
	}

	public void loadFile(File file, VanillaItemStorage storage) {
		if (file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				loadFile(subFile, storage);
			}
			return;
		}

		if (file.getName().endsWith(".yml")) {
			AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(file);
			for (String key : fileConfig.getKeys(false)) {
				boolean weapon = fileConfig.getBoolean(key + ".weapon", false);
				boolean origin = file.getName().equals("save-items.yml");

				VanillaItem item = new VanillaItem(ItemStackUtils.getItemStackWithPlaceholder(fileConfig.getItemStack(key, true, true), null));
				VanillaItemData data = new VanillaItemData(key, weapon, origin);
				item.setData(data);
				storage.put(key, item);
			}
		}
	}
}