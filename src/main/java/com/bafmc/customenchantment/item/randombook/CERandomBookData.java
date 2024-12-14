package com.bafmc.customenchantment.item.randombook;

import com.bafmc.customenchantment.item.CEItemData;

public class CERandomBookData extends CEItemData {

	private CERandomBookFilter filter;

	public CERandomBookData() {
	}

	public CERandomBookData(String pattern, CERandomBookFilter filter) {
		super(pattern);
		this.filter = filter;
	}

	public void setFilter(CERandomBookFilter filter) {
		this.filter = filter;
	}

	public CERandomBookFilter getFilter() {
		return filter;
	}

}
