package me.texward.customenchantment.enchant;

import java.util.ArrayList;
import java.util.List;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.texwardlib.util.RandomRangeInt;

public class CEGroup {
	private String name;
	private String display;
	private String prefix;
	private boolean disableEnchantLore;
	private RandomRangeInt success;
	private RandomRangeInt destroy;
	private int valuable;
	private Priority priority;

	public CEGroup(String name, String display, String prefix, boolean disableEnchantLore, RandomRangeInt success, RandomRangeInt destroy,
			int valuable, Priority priority) {
		this.name = name;
		this.display = display;
		this.prefix = prefix;
		this.disableEnchantLore = disableEnchantLore;
		this.success = success;
		this.destroy = destroy;
		this.valuable = valuable;
		this.priority = priority;
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getDisplay() {
		return display;
	}

	public boolean isDisableEnchantLore() {
		return disableEnchantLore;
	}

	public RandomRangeInt getSuccess() {
		return success;
	}

	public RandomRangeInt getDestroy() {
		return destroy;
	}

	public int getValuable() {
		return valuable;
	}

	public Priority getPriority() {
		return priority;
	}

	public List<String> getEnchantNameList() {
		List<String> enchants = new ArrayList<String>();
		for (CEEnchant enchant : CustomEnchantment.instance().getCEEnchantMap().values()) {
			if (name.equals(enchant.getGroupName())) {
				enchants.add(enchant.getName());
			}
		}
		return enchants;
	}

	public List<CEEnchant> getEnchantList() {
		List<CEEnchant> enchants = new ArrayList<CEEnchant>();
		for (CEEnchant enchant : CustomEnchantment.instance().getCEEnchantMap().values()) {
			if (name.equals(enchant.getGroupName())) {
				enchants.add(enchant);
			}
		}
		return enchants;
	}
}