package com.bafmc.customenchantment.item;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.enchant.CEEnchant;
import com.bafmc.customenchantment.enchant.CEGroup;
import com.bafmc.customenchantment.enchant.CESimple;
import com.bafmc.bukkit.utils.ItemStackUtils;

public class CEBookStorage extends CEItemStorage<CEBook> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEBook getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		int level = parameter.getInteger(1, 1);

		CEEnchant enchant = CEAPI.getCEEnchant(name);
		if (enchant == null) {
			return null;
		}
		CEGroup group = enchant.getCEGroup();

		int success = parameter.getInteger(2, group.getSuccess().getValue());
		int destroy = parameter.getInteger(3, group.getDestroy().getValue());

        if (parameter.size() >= 5) {
            int xp = parameter.getInteger(4, 0);
            CESimple ceSimple = new CESimple(name, level, success, destroy, xp);
            return getCEBook(ceSimple);
        }

		CESimple ceSimple = new CESimple(name, level, success, destroy);
		return getCEBook(ceSimple);
	}

	public List<ItemStack> getItemStacksByParameter(Parameter parameter) {
		CEBook ceBook = getByParameter(parameter);

		if (ceBook == null) {
			return null;
		}

		int amount = 1;
		// 3: name, level and amount
		// 5: name, level, success, destroy and amount
		if (parameter.size() == 3 || parameter.size() >= 6) {
			amount = parameter.getInteger(parameter.size() - 1, 1);
		}

		amount = Math.max(amount, 1);

		ItemStack itemStack = ceBook.exportTo();
		return ItemStackUtils.getItemStacks(itemStack, amount);
	}

	public CEBook getCEBook(CESimple ceSimple) {
		CEEnchant ceEnchant = ceSimple.getCEEnchant();
        if (ceEnchant.getBookType() != null) {
            return getCEBook(ceEnchant.getBookType(), ceSimple);
        }

        if (ceSimple.getXp() > 0) {
            return getCEBook(CENBT.UPGRADE, ceSimple);
        }

		return getCEBook(CENBT.DEFAULT, ceSimple);
	}
	
	public CEBook getCEBook(String type, CESimple ceSimple) {
        CEGroup ceGroup = ceSimple.getCEEnchant().getCEGroup();
        int level = ceSimple.getLevel();

        String groupName = ceGroup.getName();

        String key = type + "-" + groupName + "-" + level;
        if (!containsKey(key)) {
            key = type + "-" + CENBT.DEFAULT + "-" + level;
        }

        if (!containsKey(key)) {
            key = type + "-" + CENBT.DEFAULT + "-" + 0;
        }

        if (!containsKey(key)) {
            key = CENBT.DEFAULT + "-" + CENBT.DEFAULT + "-" + 0;
        }

		CEBook ceBook = get(key);
		ceBook.getData().setPattern(key);
		ceBook.getData().setCESimple(ceSimple);
		return ceBook;
	}
}
