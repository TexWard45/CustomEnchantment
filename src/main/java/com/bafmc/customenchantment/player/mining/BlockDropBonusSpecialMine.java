package com.bafmc.customenchantment.player.mining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.player.PlayerSpecialMining;
import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.RandomRange;

public class BlockDropBonusSpecialMine extends AbstractSpecialMine {
	private MiningBlockDropBonus blockDropBonus = new MiningBlockDropBonus();

	public BlockDropBonusSpecialMine(PlayerSpecialMining playerSpecialMining) {
		super(playerSpecialMining);
	}

	public static class MiningBlockDropBonus {
		private HashMap<String, BonusItem> bonusOre = new HashMap<String, BonusItem>();
		private Random random = new Random();

		public enum BonusType {
			ALL, ONE, NONE;
		}

		public class BonusItem {
			private BonusType type;
			private MaterialList require;
			private MaterialData reward;
			private HashMap<Integer, RandomRange> amount;
			private boolean removeItem;
			private int max = 100;
			private Chance chance;

			public BonusItem(BonusType type, MaterialList require, MaterialData reward,
					HashMap<Integer, RandomRange> amount, boolean removeItem, Chance chance) {
				this.type = type;
				this.reward = reward;
				this.require = require;
				this.amount = amount;
				this.removeItem = removeItem;
				this.chance = chance.clone();
				int total = 0;
				List<Integer> number = new ArrayList<Integer>(this.amount.keySet());
				for (int i = 0; i < number.size(); i++) {
					total += number.get(i);
				}
				this.max = total;
			}

			public int getAmount() {
				int r = 1 + random.nextInt(this.max);
				List<Integer> number = new ArrayList<Integer>(this.amount.keySet());
				int total = 0;
				for (int i = 0; i < number.size(); i++) {
					int limit = number.get(i);
					total += limit;
					if (r <= total) {
						return this.amount.get(limit).getIntValue();
					}
				}
				return 0;
			}
		}

		public void addBonus(String unique, BonusType type, MaterialList require, MaterialData reward,
				HashMap<Integer, RandomRange> amount, boolean removeItem, Chance chance) {
			BonusItem bonus = new BonusItem(type, require, reward, amount, removeItem, chance);
			bonusOre.put(unique, bonus);
		}

		public void removeBonus(String unique) {
			bonusOre.remove(unique);
		}

		public List<ItemStack> getBonus(List<ItemStack> items) {
			items = getBonus(bonusOre, items);
			return items;
		}

		public boolean isWork() {
			return !bonusOre.isEmpty();
		}

		public List<ItemStack> getBonus(HashMap<String, BonusItem> maps, List<ItemStack> items) {
			if (!maps.isEmpty()) {
				List<ItemStack> itemsBonus = new ArrayList<ItemStack>();
				for (BonusItem bonus : maps.values()) {
					if (!bonus.chance.work()) {
						continue;
					}

					MaterialList data = bonus.require;
					MaterialData reward = bonus.reward;
					int amountItemSame = 0;
					List<ItemStack> removeItem = new ArrayList<ItemStack>();
					for (ItemStack item : items) {
						if (data.contains(new MaterialData(item))) {
							amountItemSame++;
							if (bonus.removeItem)
								removeItem.add(item);
						}
					}
					if (amountItemSame > 0) {
						if (bonus.type == BonusType.ONE) {
							itemsBonus.add(new ItemStack(reward.getMaterial(), bonus.getAmount() * amountItemSame));
							items.removeAll(removeItem);
						}
						if (bonus.type == BonusType.ALL) {
							itemsBonus.add(new ItemStack(reward.getMaterial(), bonus.getAmount()));
							items.removeAll(removeItem);
						}
					}
				}
				items.addAll(itemsBonus);
			}
			return items;
		}
	}

	public int getPriority() {
		return 5;
	}
	
	public MiningBlockDropBonus getBlockDropBonus() {
		return blockDropBonus;
	}

	public Boolean isWork(boolean fake) {
		return blockDropBonus.isWork();
	}

	public List<ItemStack> getDrops(SpecialMiningData data, List<ItemStack> drops, boolean fake) {
		return blockDropBonus.getBonus(drops);
	}

	public void doSpecialMine(SpecialMiningData data, boolean fake) {

	}

}
