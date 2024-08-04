package me.texward.customenchantment.player.mining;

import me.texward.customenchantment.player.PlayerSpecialMining;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class AbstractSpecialMine {
	private PlayerSpecialMining playerSpecialMining;

	public AbstractSpecialMine(PlayerSpecialMining playerSpecialMining) {
		this.playerSpecialMining = playerSpecialMining;
	}

	public int getPriority() {
		return 0;
	}
	
	public abstract Boolean isWork(boolean fake);

	public abstract List<ItemStack> getDrops(SpecialMiningData specialMiningData, List<ItemStack> drops, boolean fake);

	public abstract void doSpecialMine(SpecialMiningData data, boolean fake);

	public PlayerSpecialMining getPlayerSpecialMining() {
		return playerSpecialMining;
	}
}