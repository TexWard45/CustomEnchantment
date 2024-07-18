package me.texward.customenchantment.player.mining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpecialMiningData implements Cloneable {
	private Player player;
	private Block block;
	private ItemStack itemStack;
	private List<Class<? extends AbstractSpecialMine>> workMap;
	private boolean dropItem;

	public SpecialMiningData(Player player, Block block, ItemStack itemStack) {
		this.player = player;
		this.block = block;
		this.itemStack = itemStack;
		this.workMap = new ArrayList<>();
	}
	
	public SpecialMiningData(Player player, Block block, ItemStack itemStack,
			List<Class<? extends AbstractSpecialMine>> workMap, boolean dropItem) {
		this.player = player;
		this.block = block;
		this.itemStack = itemStack;
		this.workMap = workMap;
		this.dropItem = dropItem;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public List<Class<? extends AbstractSpecialMine>> getWorkMap() {
		return workMap;
	}

	public void setWorkMap(List<Class<? extends AbstractSpecialMine>> workMap) {
		this.workMap = workMap;
	}

	public boolean isDropItem() {
		return dropItem;
	}

	public void setDropItem(boolean dropItem) {
		this.dropItem = dropItem;
	}
}
