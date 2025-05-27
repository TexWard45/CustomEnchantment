package com.bafmc.customenchantment.enchant;

import java.util.ArrayList;
import java.util.List;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.bukkit.utils.RandomRangeInt;
import lombok.Getter;

@Getter
public class CEGroup {
	private String name;
	private String display;
    private String enchantDisplay;
    private String bookDisplay;
	private String prefix;
	private boolean disableEnchantLore;
	private RandomRangeInt success;
	private RandomRangeInt destroy;
	private int valuable;
	private Priority priority;
    private boolean craft;
	private boolean filter;

	public CEGroup(String name, String display, String enchantDisplay, String bookDisplay, String prefix, boolean disableEnchantLore, RandomRangeInt success, RandomRangeInt destroy,
			int valuable, Priority priority, boolean craft, boolean filter) {
		this.name = name;
		this.display = display;
        this.enchantDisplay = enchantDisplay;
        this.bookDisplay = bookDisplay;
		this.prefix = prefix;
		this.disableEnchantLore = disableEnchantLore;
		this.success = success;
		this.destroy = destroy;
		this.valuable = valuable;
		this.priority = priority;
        this.craft = craft;
		this.filter = filter;
	}

	public List<String> getEnchantNameList() {
		List<String> enchants = new ArrayList<String>();
		for (CEEnchant enchant : CustomEnchantment.instance().getCeEnchantMap().values()) {
			if (name.equals(enchant.getGroupName())) {
				enchants.add(enchant.getName());
			}
		}
		return enchants;
	}

	public List<CEEnchant> getEnchantList() {
		List<CEEnchant> enchants = new ArrayList<CEEnchant>();
		for (CEEnchant enchant : CustomEnchantment.instance().getCeEnchantMap().values()) {
			if (name.equals(enchant.getGroupName())) {
				enchants.add(enchant);
			}
		}
		return enchants;
	}
}