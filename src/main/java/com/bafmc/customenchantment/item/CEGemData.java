package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.api.MaterialList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CEGemData extends CEItemData implements Cloneable {
	private String display;
	private int level;
	private MaterialList appliesMaterialList;
	private List<String> appliesDescription;

	public CEGemData() {
	}

	public CEGemData(String pattern, String display, MaterialList appliesMaterialList, List<String> appliesDescription) {
		super(pattern);
		this.display = display;
		this.appliesMaterialList = appliesMaterialList;
		this.appliesDescription = appliesDescription;
	}

	public CEGemData clone() {
		CEGemData ceGemData = new CEGemData();
		ceGemData.setPattern(this.getPattern());
		ceGemData.setDisplay(this.getDisplay());
		ceGemData.setLevel(this.getLevel());
		ceGemData.setAppliesMaterialList(this.getAppliesMaterialList());
		ceGemData.setAppliesDescription(this.getAppliesDescription());
		return ceGemData;
	}
}
