package com.bafmc.customenchantment.item.eraseenchant;

import com.bafmc.customenchantment.item.CEItemData;

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
