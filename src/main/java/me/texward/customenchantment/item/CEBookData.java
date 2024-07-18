package me.texward.customenchantment.item;

import me.texward.customenchantment.enchant.CESimple;

public class CEBookData extends CEItemData {

	private CESimple ceSimple;

	public CEBookData() {
	}

	public CEBookData(String pattern, CESimple ceSimple) {
		super(pattern);
		this.ceSimple = ceSimple;
	}

	public CESimple getCESimple() {
		return ceSimple;
	}

	public void setCESimple(CESimple ceSimple) {
		this.ceSimple = ceSimple;
	}
}
