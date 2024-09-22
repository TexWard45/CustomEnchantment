package com.bafmc.customenchantment.enchant.condition;

import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;
import com.bafmc.bukkit.utils.StringUtils;

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