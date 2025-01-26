package com.bafmc.customenchantment.item.gemdrill;

import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.item.CEItemData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
public class CEGemDrillData extends CEItemData {
	private ConfigData configData;

	@Getter
	@AllArgsConstructor
	public static class ConfigData {
		private int maxDrill;
		private MaterialList applies;
		private String slot;
		private Map<Integer, Double> slotChance;
	}

	public CEGemDrillData() {
	}

	public CEGemDrillData(String pattern, ConfigData configData) {
		super(pattern);
		this.configData = configData;
	}
}
