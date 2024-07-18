package me.texward.customenchantment.player.mining;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.player.PlayerSpecialMining;

public abstract class AbstractSpecialMine {
	private PlayerSpecialMining playerSpecialMining;

	public AbstractSpecialMine(PlayerSpecialMining playerSpecialMining) {
		this.playerSpecialMining = playerSpecialMining;
	}

	public int getPriority() {
		return 0;
	}
	
	public abstract Boolean isWork(boolean fake);

	public abstract List<ItemStack> getDrops(List<ItemStack> drops, boolean fake);
	
	public abstract void doSpecialMine(SpecialMiningData data, boolean fake);

	public PlayerSpecialMining getPlayerSpecialMining() {
		return playerSpecialMining;
	}
}