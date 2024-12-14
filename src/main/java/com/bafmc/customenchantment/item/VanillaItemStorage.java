package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import org.bukkit.inventory.ItemStack;

public class VanillaItemStorage extends CEItemStorage<CEItem<? extends CEItemData>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AdvancedFileConfiguration saveConfig;

	public void setSaveConfig(AdvancedFileConfiguration config) {
		this.saveConfig = config;
	}

	public void putItem(String key, ItemStack itemStack) {
		this.saveConfig.set(key, itemStack);
		this.saveConfig.save();
		super.put(key, new VanillaItem(itemStack));
	}
	
	public void removeItem(String key) {
		this.saveConfig.set(key, null);
		this.saveConfig.save();
		super.remove(key);
	}

	@SuppressWarnings("unchecked")
	public CEItem<? extends CEItemData> getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.STORAGE).get(name);
	}
}
