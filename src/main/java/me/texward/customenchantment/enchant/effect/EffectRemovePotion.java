package me.texward.customenchantment.enchant.effect;

import java.util.List;

import org.bukkit.potion.PotionEffectType;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.enchant.EffectUtil;

public class EffectRemovePotion extends EffectHook {
	private List<PotionEffectType> list;

	public String getIdentify() {
		return "REMOVE_POTION";
	}
	
	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		this.list = EffectUtil.getPotionEffectList(args[0]);
	}

	public void execute(CEFunctionData data) {
		for (PotionEffectType potionType : this.list) {
			data.getLivingEntity().removePotionEffect(potionType);
		}
	}
}
