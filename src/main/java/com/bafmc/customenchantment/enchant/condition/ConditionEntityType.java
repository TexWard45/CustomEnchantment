package com.bafmc.customenchantment.enchant.condition;

import org.bukkit.entity.LivingEntity;

import com.bafmc.customenchantment.api.EntityTypeList;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;
import com.bafmc.bukkit.utils.StringUtils;

public class ConditionEntityType extends ConditionHook {
	private EntityTypeList list;
	
	public String getIdentify() {
		return "ENTITY_TYPE";
	}

	public void setup(String[] args) {
		this.list = EntityTypeList.getEntityTypeList(StringUtils.split(args[0], ",", 0));
	}

	@Override
	public boolean match(CEFunctionData data) {
		LivingEntity le = data.getLivingEntity();
		if (le == null) {
			return false;
		}

		return list.contains(le.getType());
	}
	
}