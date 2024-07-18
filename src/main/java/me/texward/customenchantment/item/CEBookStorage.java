package me.texward.customenchantment.item;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.api.Parameter;
import me.texward.customenchantment.enchant.CEEnchant;
import me.texward.customenchantment.enchant.CEGroup;
import me.texward.customenchantment.enchant.CESimple;
import me.texward.texwardlib.util.ItemStackUtils;

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
		if (parameter.size() == 3 || parameter.size() >= 5) {
			amount = parameter.getInteger(parameter.size() - 1, 1);
		}

		amount = Math.max(amount, 1);

		ItemStack itemStack = ceBook.exportTo();
		return ItemStackUtils.getItemStacks(itemStack, amount);
	}

	public CEBook getCEBook(CESimple ceSimple) {
		CEEnchant ceEnchant = ceSimple.getCEEnchant();
		CEGroup ceGroup = ceEnchant.getCEGroup();

		String name = ceGroup.getName();
		int level = ceSimple.getLevel();
		
		String key = name + "-" + level;
		if (!containsKey(key)) {
			key = name + "-" + 0;
		}

		if (!containsKey(key)) {
			key = CENBT.DEFAULT + "-" + 0;
		}

		CEBook ceBook = get(key);
		ceBook.getData().setPattern(key);
		ceBook.getData().setCESimple(ceSimple);
		return ceBook;
	}
	
	public CEBook getCEBook(String type, CESimple ceSimple) {
		String name = type;
		int level = ceSimple.getLevel();
		
		String key = name + "-" + level;
		if (!containsKey(key)) {
			key = name + "-" + 0;
		}

		if (!containsKey(key)) {
			key = CENBT.DEFAULT + "-" + 0;
		}
		
		CEBook ceBook = get(key);
		ceBook.getData().setPattern(key);
		ceBook.getData().setCESimple(ceSimple);
		return ceBook;
	}
}
