package me.texward.customenchantment.enchant.condition;

import java.util.List;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;
import me.texward.texwardlib.util.EnumUtils;

public class ConditionDamageCause extends ConditionHook {
	private List<DamageCause> list;

	public String getIdentify() {
		return "DAMAGE_CAUSE";
	}

	public void setup(String[] args) {
		this.list = EnumUtils.getEnumListByString(DamageCause.class, args[0]);
	}

	@Override
	public boolean match(CEFunctionData data) {
		DamageCause damageCause = data.getDamageCause();
		if (damageCause == null) {
			return false;
		}

		return list.contains(damageCause);
	}

}