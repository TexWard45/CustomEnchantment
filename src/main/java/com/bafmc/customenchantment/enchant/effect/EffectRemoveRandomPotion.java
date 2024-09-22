package com.bafmc.customenchantment.enchant.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.enchant.EffectUtil;
import com.bafmc.bukkit.utils.RandomRange;

public class EffectRemoveRandomPotion extends EffectHook {
	public String type;
	public RandomRange amount;
	public List<PotionEffectType> potionType;
	public Random random = new Random();

	public String getIdentify() {
		return "REMOVE_RANDOM_POTION";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		this.type = args[0];
		this.amount = new RandomRange(args[1]);
		this.potionType = EffectUtil.getPotionEffectList(args[2]);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}
		
		List<PotionEffectType> potions = new ArrayList<PotionEffectType>();
		if (type.equals("DUPLICATE")) {
			int amount = potionType.size();
			for (int i = 0; i < this.amount.getIntValue(); i++) {
				PotionEffectType pe = potionType.get(random.nextInt(amount));

				if (!potions.contains(pe))
					potions.add(pe);
			}
		} else {
			List<PotionEffectType> copy = new ArrayList<PotionEffectType>();
			for (PotionEffectType potion : this.potionType) {
				copy.add(potion);
			}

			for (int i = 0; i < this.amount.getIntValue(); i++) {
				int amount = copy.size();
				int random = this.random.nextInt(amount);

				PotionEffectType pe = copy.get(random);
				potions.add(pe);

				copy.remove(pe);
			}
		}

		for (PotionEffectType potionType : potions)
			player.removePotionEffect(potionType);
	}
}
