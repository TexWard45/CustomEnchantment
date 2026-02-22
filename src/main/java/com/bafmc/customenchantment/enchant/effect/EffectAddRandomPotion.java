package com.bafmc.customenchantment.enchant.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.enchant.EffectUtil;
import com.bafmc.bukkit.utils.RandomRange;

public class EffectAddRandomPotion extends EffectHook {
	private String type;
	private RandomRange amount;
	private List<PotionEffectType> potionType;
	private RandomRange level;
	private RandomRange duration;
	private Random random = new Random();

	public String getIdentify() {
		return "ADD_RANDOM_POTION";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		this.type = args[0];
		this.amount = new RandomRange(args[1]);
		this.potionType = EffectUtil.getPotionEffectList(args[2]);
		this.level = new RandomRange(args[3]);
		this.duration = new RandomRange(args[4]);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		List<PotionEffectType> potions = new ArrayList<PotionEffectType>();
		if (type.equals(CEConstants.RewardType.DUPLICATE)) {
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

		int level = this.level.getIntValue();
		int duration = this.duration.getIntValue();
		for (PotionEffectType potionType : potions)
			player.addPotionEffect(new PotionEffect(potionType, duration, level));
	}
}
