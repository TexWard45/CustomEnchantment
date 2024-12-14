package com.bafmc.customenchantment.item.book;

import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.item.CEItemData;

public class CEBookData extends CEItemData {

	private CEEnchantSimple ceEnchantSimple;

	public CEBookData() {
	}

	public CEBookData(String pattern, CEEnchantSimple ceEnchantSimple) {
		super(pattern);
		this.ceEnchantSimple = ceEnchantSimple;
	}

	public CEEnchantSimple getCESimple() {
		return ceEnchantSimple;
	}

	public void setCESimple(CEEnchantSimple ceEnchantSimple) {
		this.ceEnchantSimple = ceEnchantSimple;
	}
}
