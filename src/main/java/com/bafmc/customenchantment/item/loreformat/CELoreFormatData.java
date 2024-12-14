package com.bafmc.customenchantment.item.loreformat;

import com.bafmc.customenchantment.item.CEItemData;

public class CELoreFormatData extends CEItemData {

	private String type;

	public CELoreFormatData() {
	}

	public CELoreFormatData(String pattern, String type) {
		super(pattern);
        this.type = type;
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
