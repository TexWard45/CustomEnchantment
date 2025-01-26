package com.bafmc.customenchantment.menu;

import com.bafmc.custommenu.menu.CItem;
import com.bafmc.custommenu.menu.CMenuView;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuAbstract {
	protected CMenuView menuView;
	protected Player player;

	public MenuAbstract(CMenuView menuView, Player player) {
		this.menuView = menuView;
		this.player = player;
	}
	
	public void updateSlots(String itemName, ItemStack itemStack) {
		for (Integer slot : getSlots(itemName)) {
			if (itemStack == null || itemStack.getType() == Material.AIR) {
				menuView.removeTemporaryItem(slot);
			} else {
				menuView.setTemporaryItem(slot, itemStack);
			}
		}
	}

	public List<Integer> getSlots(String itemName) {
		CItem cItem = menuView.getCMenu().getMenuItem().getCItemByName(itemName);
		if (cItem == null) {
			return new ArrayList<Integer>();
		}
		return cItem.getSlots();
	}
	
	public ItemStack getItemStack(Player player, String itemName) {
		CItem cItem = menuView.getCMenu().getMenuItem().getCItemByName(itemName);
		return cItem.getItemStack(player);
	}
}
