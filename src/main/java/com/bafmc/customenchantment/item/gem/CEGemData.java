package com.bafmc.customenchantment.item.gem;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.item.CEItemData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class CEGemData extends CEItemData implements Cloneable {
	@Setter
	private int level;
	private ConfigData configData;

	@AllArgsConstructor
	@Getter
	@Builder
	public static class ConfigData {
		private String display;
		private String itemDisplay;
		private List<String> itemLore;
		private MaterialList appliesMaterialList;
		private List<String> appliesDescription;
		private List<String> appliesSlot;
		private Map<Integer, List<NMSAttribute>> nmsAttributeLevelMap;
		private int limitPerItem;
	}

	public CEGemData() {
	}

	public CEGemData(String pattern, ConfigData configData) {
		super(pattern);
		this.configData = configData;
	}

	public CEGemData clone() {
		CEGemData ceGemData = new CEGemData();
		ceGemData.setPattern(this.getPattern());
		ceGemData.level = this.level;
		ceGemData.configData = this.configData;
		return ceGemData;
	}

	public List<NMSAttribute> getNMSAttributes() {
		return configData.nmsAttributeLevelMap.containsKey(level) ? configData.nmsAttributeLevelMap.get(level) : new ArrayList<>();
	}
}
