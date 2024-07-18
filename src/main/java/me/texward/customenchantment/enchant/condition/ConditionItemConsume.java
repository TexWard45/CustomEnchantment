package me.texward.customenchantment.enchant.condition;

import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.api.MaterialData;
import me.texward.customenchantment.api.MaterialList;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;
import me.texward.texwardlib.util.StringUtils;

public class ConditionItemConsume extends ConditionHook {
	private MaterialList list;

	public String getIdentify() {
		return "ITEM_CONSUME";
	}

	public void setup(String[] args) {
		this.list = MaterialList.getMaterialList(StringUtils.split(args[0], ",", 0));
	}

	@Override
	public boolean match(CEFunctionData data) {
		ItemStack itemStack = data.getItemConsume();
		if (itemStack == null) {
			return false;
		}

		return list.contains(new MaterialData(itemStack));
	}

}