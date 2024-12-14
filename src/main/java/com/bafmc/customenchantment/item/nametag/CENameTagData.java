package com.bafmc.customenchantment.item.nametag;

import com.bafmc.customenchantment.item.CEItemData;

import java.util.List;

public class CENameTagData extends CEItemData {
	private List<Character> colorCharacterEnableList;

	public CENameTagData() {
	}

	public CENameTagData(String pattern, List<Character> colorCharacterEnableList) {
		super(pattern);
	}

	public List<Character> getColorCharacterEnableList() {
		return colorCharacterEnableList;
	}

	public void setColorCharacterEnableList(List<Character> colorCharacterEnableList) {
		this.colorCharacterEnableList = colorCharacterEnableList;
	}

}
