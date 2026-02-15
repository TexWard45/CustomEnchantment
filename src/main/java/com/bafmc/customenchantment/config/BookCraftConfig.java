package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.api.EconomyAPI;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.config.annotation.Configuration;
import com.bafmc.bukkit.config.annotation.Path;
import com.bafmc.bukkit.config.annotation.ValueType;
import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftCustomMenu;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftMenuLegacy;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftSettings;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Getter
public class BookCraftConfig {
	@Path("money-require")
	@ValueType(Double.class)
	private Map<String, Double> moneyGroupRequireMap = new LinkedHashMap<>();

	public double getMoneyRequire(String groupName) {
		return moneyGroupRequireMap.containsKey(groupName) ? moneyGroupRequireMap.get(groupName) : 0;
	}

	public boolean isRequireMoney(Player player, String groupName) {
		return getMoneyRequire(groupName) <= EconomyAPI.getMoney(player);
	}

	public boolean payMoney(Player player, String groupName) {
		if (!isRequireMoney(player, groupName)) {
			return false;
		}
		EconomyAPI.takeMoney(player, getMoneyRequire(groupName));
		return true;
	}

	//FastCraft Zone
	public boolean isRequireMoney(Player player, String groupName, double amount) {
		return (getMoneyRequire(groupName)*amount) <= EconomyAPI.getMoney(player);
	}

	public boolean payMoney(Player player, String groupName, double amount) {
		if (!isRequireMoney(player, groupName, amount)) {
			return false;
		}
		EconomyAPI.takeMoney(player, getMoneyRequire(groupName)*amount);
		return true;
	}

	/**
	 * Initialize BookCraft settings for both legacy and new CustomMenu API
	 * Loads slot configuration from book-craft.yml
	 */
	public void initializeSettings() {
		// Load slot positions from config (similar to TinkererConfig)
		AdvancedFileConfiguration config = new AdvancedFileConfiguration(CustomEnchantment.instance().getBookCraftFile());
		List<Integer> bookSlots = StringUtils.getIntList(config.getString("book-slots"), ",", "-");
		int previewSlot = config.getInt("preview-slot", 22);
		int acceptSlot = config.getInt("accept-slot", 31);

		BookCraftSettings settings = new BookCraftSettings(bookSlots, previewSlot, acceptSlot);

		// Legacy menu (disabled FastCraft)
		// BookCraftMenuLegacy doesn't need settings currently

		// NEW: Set settings for new CustomMenu API
		BookCraftCustomMenu.setSettings(settings);
	}
}
