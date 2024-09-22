package com.bafmc.customenchantment.enchant.effect;

import java.util.List;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.enchant.EffectUtil;
import com.bafmc.bukkit.utils.RandomRange;

public class EffectAddPotion extends EffectHook {
	private List<PotionEffectType> list;
	private RandomRange level;
	private RandomRange duration;

	public String getIdentify() {
		return "ADD_POTION";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		this.list = EffectUtil.getPotionEffectList(args[0]);
		this.level = new RandomRange(args[1]);
		this.duration = new RandomRange(args[2]);
	}

	public void execute(CEFunctionData data) {
		for (PotionEffectType potionType : this.list) {
			int level = this.level.getIntValue();
			int duration = this.duration.getIntValue();
			PotionEffect potionEffect = new PotionEffect(potionType, duration, level);
			data.getLivingEntity().addPotionEffect(potionEffect);
		}
	}
}
