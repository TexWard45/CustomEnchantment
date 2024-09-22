package com.bafmc.customenchantment.player.mining;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class SpecialMiningData implements Cloneable {
	private Player player;
	private Block block;
	private ItemStack itemStack;
	private List<Class<? extends AbstractSpecialMine>> workMap;
	private boolean dropItem;
    private List<ItemStack> drops = new ArrayList<>();
    private List<ItemStack> originalDrops = new ArrayList<>();

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
}
