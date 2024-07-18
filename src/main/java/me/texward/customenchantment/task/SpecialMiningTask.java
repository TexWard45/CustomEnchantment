package me.texward.customenchantment.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.player.PlayerSpecialMining;
import me.texward.customenchantment.player.mining.SpecialMiningData;
import me.texward.texwardlib.api.FactionAPI;

public class SpecialMiningTask extends BukkitRunnable {
	private static final int maxProcessPerTick = 50;
	private static List<Material> except = Arrays.asList(Material.AIR, Material.BEDROCK, Material.LAVA, Material.WATER);

	public class Data {
		public PlayerSpecialMining specialMining;
		public Location location;
		public Player player;
		public SpecialMiningData settings;

		public Data(PlayerSpecialMining specialMining, Location location, Player player, SpecialMiningData settings) {
			this.specialMining = specialMining;
			this.location = location;
			this.player = player;
			this.settings = settings;
		}

		public boolean canBreak(Block block, boolean factionSupport) {
			if (factionSupport && !FactionAPI.canBuildBlock(player, location, true)) {
				return false;
			}

			Block target = location.getBlock();
			if (except.contains(target.getType())) {
				return false;
			}
			return true;
		}

		public void breakBlock(Block block) {
			specialMining.callFakeBreakBlock(block, settings);
		}
	}

	private CustomEnchantment plugin;
	private List<Data> list = new ArrayList<Data>();

	public SpecialMiningTask(CustomEnchantment plugin) {
		this.plugin = plugin;
	}

	public void run() {
		boolean factionSupport = FactionAPI.isFactionSupport();
		int process = 0;

		ListIterator<Data> ite = list.listIterator();

		while (ite.hasNext()) {
			if (process >= maxProcessPerTick) {
				break;
			}

			Data data = ite.next();

			Block block = data.location.getBlock();
			if (data.canBreak(block, factionSupport)) {
				data.breakBlock(block);
			}

			ite.remove();
			process++;
		}
	}

	public void add(PlayerSpecialMining specialMining, Location location, Player player, SpecialMiningData settings) {
		this.list.add(new Data(specialMining, location, player, settings));
	}
}
