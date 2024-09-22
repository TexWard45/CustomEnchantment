package com.bafmc.customenchantment.player.mining;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.player.PlayerSpecialMining;
import com.bafmc.customenchantment.task.SpecialMiningTask;
import com.bafmc.bukkit.utils.Chance;

public class ExplosionSpecialMine extends AbstractSpecialMine {

	public ExplosionSpecialMine(PlayerSpecialMining playerSpecialMining) {
		super(playerSpecialMining);
	}

	public static class MiningExplosion {
		private ConcurrentHashMap<String, Explosion> explosionMap = new ConcurrentHashMap<String, Explosion>();
		private Explosion highestExplosion;

		public void addExplosion(String name, Explosion explosion) {
			if (explosion.getChance() < 0) {
				removeExplosion(name);
				return;
			}

			explosionMap.put(name, explosion.clone());
			updateHighestExplosion();
		}

		public void removeExplosion(String name) {
			if (!explosionMap.containsKey(name)) {
				return;
			}

			explosionMap.remove(name);
			updateHighestExplosion();
		}

		public void updateHighestExplosion() {
			int highestAOE = 1;
			double highestChance = 0;
			boolean drop = false;

			for (Explosion explosion : this.explosionMap.values()) {
				if (highestAOE > explosion.getAoe()) {
					continue;
				}

				if (highestChance > explosion.getChance()) {
					continue;
				}

				highestAOE = explosion.getAoe();
				highestChance = explosion.getChance();
				drop = explosion.isDrop();
			}

			if (highestAOE != 1 || highestChance != 0) {
				this.highestExplosion = new Explosion(highestAOE, highestChance, drop);
			} else {
				this.highestExplosion = null;
			}
		}

		public Explosion getHighestExplosion() {
			return highestExplosion;
		}

		public boolean isWork() {
			return highestExplosion != null && highestExplosion.isWork();
		}
	}

	public static class Explosion implements Cloneable {
		private int aoe;
		private double chanceDouble;
		private Chance chance;
		private boolean drop;

		public Explosion(int aoe, double chance, boolean drop) {
			this.aoe = aoe;
			this.chanceDouble = chance;
			this.chance = new Chance(chance);
			this.drop = drop;
		}

		public int getAoe() {
			return aoe;
		}

		public double getChance() {
			return chanceDouble;
		}

		public boolean isDrop() {
			return drop;
		}

		public boolean isWork() {
			return chance.work();
		}

		public Explosion clone() {
			try {
				return (Explosion) super.clone();
			} catch (CloneNotSupportedException e) {
				return new Explosion(aoe, chanceDouble, drop);
			}
		}
	}

	private MiningExplosion explosion = new MiningExplosion();

	public MiningExplosion getExplosion() {
		return explosion;
	}

	public Boolean isWork(boolean fake) {
		if (fake) {
			return false;
		}
		return explosion.isWork();
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

		SpecialMiningTask task = CustomEnchantment.instance().getSpecialMiningTask();
		Explosion exp = this.explosion.getHighestExplosion();
		data.setDropItem(exp.isDrop());

		Location center = block.getLocation();
		World world = center.getWorld();
		double xCenter = center.getX();
		double yCenter = center.getY();
		double zCenter = center.getZ();
		int radius = exp.getAoe() / 2;

		for (int y = -radius; y <= radius; y++) {
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					if (x == 0 && y == 0 && z == 0) {
						continue;
					}

					Location location = new Location(world, xCenter + x, yCenter + y, zCenter + z);
					task.add(getPlayerSpecialMining(), location, player, data);
				}
			}
		}
	}
}
