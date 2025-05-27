package com.bafmc.customenchantment.player.mining;

import com.bafmc.bukkit.utils.Chance;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerSpecialMining;
import com.bafmc.customenchantment.player.TemporaryKey;
import com.bafmc.customenchantment.task.SpecialMiningTask;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VeinSpecialMine extends AbstractSpecialMine {

	public VeinSpecialMine(PlayerSpecialMining playerSpecialMining) {
		super(playerSpecialMining);
	}

	public static class VeinMining {
		private ConcurrentHashMap<String, Vein> veinMap = new ConcurrentHashMap<String, Vein>();
		@Getter
        private Vein highestVein;

		public void addVein(String name, Vein vein) {
			if (vein.getChance() < 0) {
				removeVein(name);
				return;
			}

			veinMap.put(name, vein.clone());
			updateHighestVein();
		}

		public void removeVein(String name) {
			if (!veinMap.containsKey(name)) {
				return;
			}

			veinMap.remove(name);
			updateHighestVein();
		}

		public void updateHighestVein() {
			int highestSize = 1;
			double highestChance = 0;
			MaterialList whitelist = null;

			for (Vein vein : this.veinMap.values()) {
				if (highestSize > vein.getLength()) {
					continue;
				}

				if (highestChance > vein.getChance()) {
					continue;
				}

				highestSize = vein.getLength();
				highestChance = vein.getChance();
				whitelist = vein.getWhitelist();
			}

			if (highestSize != 1 || highestChance != 0) {
				this.highestVein = new Vein(highestSize, highestChance, whitelist);
			} else {
				this.highestVein = null;
			}
		}

        public boolean isWork() {
			return highestVein != null && highestVein.isWork();
		}
	}

	public static class Vein implements Cloneable {
		@Getter
        private int length;
		private double chanceDouble;
		private Chance chance;
		@Getter
		private MaterialList whitelist;

		public Vein(int length, double chance, MaterialList whitelist) {
			this.length = length;
			this.chanceDouble = chance;
			this.chance = new Chance(chance);
			this.whitelist = whitelist;
		}

        public double getChance() {
			return chanceDouble;
		}

        public boolean isWork() {
			return chance.work();
		}

		public Vein clone() {
			try {
				return (Vein) super.clone();
			} catch (CloneNotSupportedException e) {
				return new Vein(length, chanceDouble, whitelist);
			}
		}
	}

	private VeinMining vein = new VeinMining();

	public VeinMining getVein() {
		return vein;
	}

	public Boolean isWork(boolean fake) {
		if (fake) {
			return false;
		}
		return vein.isWork();
	}

	public List<ItemStack> getDrops(SpecialMiningData data, List<ItemStack> drops, boolean fake) {
		return drops;
	}

	public void doSpecialMine(SpecialMiningData data, boolean fake) {
		if (fake) {
			return;
		}
		Player player = data.getPlayer();
		Block block = data.getBlock();

		SpecialMiningTask task = CustomEnchantment.instance().getTaskModule().getSpecialMiningTask();
		Vein exp = this.vein.getHighestVein();
		data.setDropItem(true);

		Set<Location> locations = new HashSet<>();

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		Material blockType = (Material) cePlayer.getTemporaryStorage().get(TemporaryKey.LAST_MINE_BLOCK_TYPE);

		if (exp.getWhitelist() != null && !exp.getWhitelist().contains(new MaterialData(blockType))) {
			return;
		}

		veinMine(block, blockType, blockType, locations, exp.getLength());

		locations.remove(block.getLocation()); // Remove original block
		for (Location location : locations) {
			task.add(getPlayerSpecialMining(), location, player, data);
		}
	}

	private int veinMine(Block block, Material blockType, Material compareType, Set<Location> locations, int remaining) {
		if (remaining <= 0 || locations.contains(block.getLocation()) || blockType != compareType) {
			return 0;
		}

		locations.add(block.getLocation()); // Store location
		int count = 1; // Count this block

		// Check adjacent blocks (6 directions)
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				for (int dz = -1; dz <= 1; dz++) {
					if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) == 1) { // Ensures only direct neighbors
						Block neighbor = block.getRelative(dx, dy, dz);

						count += veinMine(neighbor, neighbor.getType(), compareType, locations, remaining - count); // Reduce remaining correctly
					}
				}
			}
		}

		return count;
	}
}
