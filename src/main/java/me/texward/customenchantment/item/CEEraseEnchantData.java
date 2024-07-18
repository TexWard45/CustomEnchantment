package me.texward.customenchantment.item;

import java.util.List;

public class CEEraseEnchantData extends CEItemData {

	private List<String> blacklistEnchantments;

	public CEEraseEnchantData() {
	}

	public CEEraseEnchantData(String pattern, List<String> blacklistEnchantments) {
		super(pattern);
		this.blacklistEnchantments = blacklistEnchantments;
	}

	public List<String> getBlacklistEnchantments() {
		return blacklistEnchantments;
	}

	public void setBlacklistEnchantments(List<String> blacklistEnchantments) {
		this.blacklistEnchantments = blacklistEnchantments;
	}

}
