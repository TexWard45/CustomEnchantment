package com.bafmc.customenchantment.item.gemdrill;

import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.item.CEItemData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CEGemDrillData extends CEItemData {
	private ConfigData configData;

	@Getter
	@AllArgsConstructor
	public static class ConfigData {
		public int maxDrill;
		public MaterialList applies;
		public String slot;
	}

	public CEGemDrillData() {
	}

	public CEGemDrillData(String pattern, ConfigData configData) {
		super(pattern);
		this.configData = configData;
	}
}
