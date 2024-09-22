package com.bafmc.customenchantment.player.mining;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.player.PlayerSpecialMining;
import com.bafmc.bukkit.utils.Chance;

public class FurnaceSpecialMine extends AbstractSpecialMine {
	private MiningFurnace furnace = new MiningFurnace();

	public static class MiningFurnace {
		private ConcurrentHashMap<String, Double> furnaceChanceMap = new ConcurrentHashMap<String, Double>();
		private Chance highestFurnaceChance = new Chance(0);

		public void addFurnaceChance(String name, double chance) {
			if (chance < 0) {
				removeFurnaceChance(name);
				return;
			}

			furnaceChanceMap.put(name, chance);
			updateHighestFurnaceChance();
		}

		public void removeFurnaceChance(String name) {
			if (!furnaceChanceMap.containsKey(name)) {
				return;
			}

			furnaceChanceMap.remove(name);
			updateHighestFurnaceChance();
		}

		public void updateHighestFurnaceChance() {
			double highestFurnaceChance = 0;

			for (Double chance : this.furnaceChanceMap.values()) {
				if (highestFurnaceChance < chance) {
					highestFurnaceChance = chance;
				}
			}

			this.highestFurnaceChance = new Chance(highestFurnaceChance);
		}

		public List<ItemStack> applyFurnace(List<ItemStack> items) {
			for (ItemStack item : items) {
				switch (item.getType()) {
				case IRON_ORE:
					item.setType(Material.IRON_INGOT);
					break;
				case GOLD_ORE:
					item.setType(Material.GOLD_INGOT);
					break;
				default:
					break;
				}
			}
			return items;
		}

		public boolean isWork() {
			return highestFurnaceChance.work();
		}
	}

	public FurnaceSpecialMine(PlayerSpecialMining playerSpecialMining) {
		super(playerSpecialMining);
	}

	public MiningFurnace getFurnace() {
		return furnace;
	}

	public Boolean isWork(boolean fake) {
		return furnace.isWork();
	}

	public List<ItemStack> getDrops(SpecialMiningData data, List<ItemStack> drops, boolean fake) {
		return furnace.applyFurnace(drops);
	}

	public void doSpecialMine(SpecialMiningData data, boolean fake) {

	}

}
