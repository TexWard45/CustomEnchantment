package com.bafmc.customenchantment.enchant;

import java.util.ArrayList;
import java.util.List;

import com.bafmc.customenchantment.attribute.RangeAttribute;

public class Option {
	private List<RangeAttribute> list = new ArrayList<RangeAttribute>();

	public Option(List<RangeAttribute> list) {
		this.list = list;
	}

	public void setOptionDataList(List<RangeAttribute> list) {
		this.list = list;
	}

	public List<RangeAttribute> getOptionDataList() {
		return list;
	}
}