package me.texward.customenchantment.menu;

import com._3fmc.bukkit.feature.execute.Execute;
import org.bukkit.inventory.ItemStack;

public class TinkererReward {
	private ItemStack itemStack;
	private Execute execute;

	public TinkererReward(ItemStack itemStack, Execute execute) {
		this.itemStack = itemStack;
		this.execute = execute;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public Execute getExecute() {
		return execute;
	}

	public void setExecute(Execute execute) {
		this.execute = execute;
	}

}
