package com.bafmc.customenchantment.item.removeenchant;

import com.bafmc.customenchantment.item.CEItemData;

import java.util.List;

public class CERemoveEnchantData extends CEItemData {

	private List<String> groups;

	public CERemoveEnchantData() {
	}

	public CERemoveEnchantData(String pattern, List<String> groups) {
		super(pattern);
		this.groups = groups;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

}
