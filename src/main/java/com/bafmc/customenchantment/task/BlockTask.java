package com.bafmc.customenchantment.task;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import com.bafmc.customenchantment.CustomEnchantment;

public class BlockTask extends BukkitRunnable {
	private class BlockData {
		public Location location;
		public Material material;
		public long start;
		public long duration;

		public BlockData(Location location, Material material, long start, long duration) {
			this.location = location;
			this.material = material;
			this.start = start;
			this.duration = duration;
		}
	}

	private CustomEnchantment plugin;
	private ConcurrentHashMap<String, BlockData> map = new ConcurrentHashMap<String, BlockData>();

	public BlockTask(CustomEnchantment plugin) {
		this.plugin = plugin;
	}

	public void run() {
		if (map.isEmpty()) {
			return;
		}

		for (String key : map.keySet()) {
			BlockData data = map.get(key);

			if (System.currentTimeMillis() - data.start > data.duration) {
				Block block = data.location.getBlock();
				block.setType(Material.AIR);
				map.remove(key);
			}
		}
	}

	public void setBlock(Location location, Material material, long duration) {
		Block block = location.getBlock();
		if (block.getType() != Material.AIR) {
			return;
		}

		block.setType(material);

		if (duration < 0) {
			return;
		}

		this.map.put(toString(block.getLocation()),
				new BlockData(location, material, System.currentTimeMillis(), duration));
	}

	public void remove(Location location) {
		this.map.remove(toString(location));
	}

	public boolean contains(Location location) {
		return this.map.containsKey(toString(location));
	}

	public String toString(Location location) {
		return location.getWorld().getName() + ":" + (int) location.getX() + ":" + (int) location.getY() + ":"
				+ (int) location.getZ();
	}
}