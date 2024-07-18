package me.texward.customenchantment.enchant;

import java.util.ArrayList;
import java.util.List;

import me.texward.customenchantment.attribute.AttributeData;

public class Option {
	private List<AttributeData> list = new ArrayList<AttributeData>();

	public Option(List<AttributeData> list) {
		this.list = list;
	}

	public void setOptionDataList(List<AttributeData> list) {
		this.list = list;
	}

	public List<AttributeData> getOptionDataList() {
		return list;
	}
}