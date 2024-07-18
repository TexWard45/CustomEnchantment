package me.texward.customenchantment.item;

import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.Parameter;
import me.texward.texwardlib.configuration.AdvancedFileConfiguration;

public class VanillaItemStorage extends CEItemStorage<CEItem<? extends CEItemData>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AdvancedFileConfiguration config;
	private AdvancedFileConfiguration saveConfig;

	public void setConfig(AdvancedFileConfiguration config) {
		this.config = config;
	}
	
	public void setSaveConfig(AdvancedFileConfiguration config) {
		this.saveConfig = config;
	}

	public void putItem(String key, ItemStack itemStack) {
		this.saveConfig.set(key, itemStack);
		this.saveConfig.save();
		super.put(key, new VanillaItem(itemStack));
	}
	
	public void removeItem(String key) {
		this.config.set(key, null);
		this.config.save();
		super.remove(key);
	}

	@SuppressWarnings("unchecked")
	public CEItem<? extends CEItemData> getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.STORAGE).get(name);
	}
}
