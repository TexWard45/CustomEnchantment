package me.texward.customenchantment.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.texward.custommenu.menu.CItem;
import me.texward.custommenu.menu.CMenuView;

public abstract class MenuAbstract {
	protected CMenuView menuView;
	protected Player player;

	public MenuAbstract(CMenuView menuView, Player player) {
		this.menuView = menuView;
		this.player = player;
	}
	
	public void updateSlots(String itemName, ItemStack itemStack) {
		for (Integer slot : getSlots(itemName)) {
			if (itemStack == null) {
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
