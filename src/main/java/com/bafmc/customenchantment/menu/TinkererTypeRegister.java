package com.bafmc.customenchantment.menu;

import java.util.ArrayList;
import java.util.List;

import com.bafmc.customenchantment.enchant.CESimple;
import com.bafmc.customenchantment.item.CEBook;
import com.bafmc.customenchantment.item.CEItem;

public class TinkererTypeRegister {
	private static List<TinkererTypeHook> list = new ArrayList<TinkererTypeHook>();

	static {
		register(new TinkererTypeHook() {
			public String getType(CEItem ceItem) {
				if (!(ceItem instanceof CEBook)) {
					return null;
				}
				CEBook ceBook = (CEBook) ceItem;
				CESimple ceSimple = ceBook.getData().getCESimple();
				return "book." + ceSimple.getCEEnchant().getGroupName() + "." + ceSimple.getLevel();
			}
		});
	}

	public static String getType(CEItem ceItem) {
		String type = null;
		for (TinkererTypeHook hook : list) {
			if ((type = hook.getType(ceItem)) != null) {
				break;
			}
		}
		return type;
	}

	public static boolean register(TinkererTypeHook hook) {
		if (hook == null) {
			return false;
		}

		list.add(hook);
		return true;
	}
}
