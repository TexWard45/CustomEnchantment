package me.texward.customenchantment.enchant.condition;

import org.bukkit.entity.LivingEntity;

import me.texward.customenchantment.api.EntityTypeList;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;
import me.texward.texwardlib.util.StringUtils;

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