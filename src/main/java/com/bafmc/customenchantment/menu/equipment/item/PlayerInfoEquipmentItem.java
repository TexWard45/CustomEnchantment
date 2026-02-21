package com.bafmc.customenchantment.menu.equipment.item;

import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu;
import org.bukkit.inventory.ItemStack;

public class PlayerInfoEquipmentItem extends AbstractItem<EquipmentCustomMenu> {

	@Override
	public String getType() {
		return CEConstants.MenuItemType.PLAYER_INFO_EQUIPMENT;
	}

	@Override
	public ItemStack setupItemStack() {
		// Return null — rendered dynamically by updatePlayerInfoSlots() with player context
		// Default getItemStack() resolves PAPI with null player, causing NPE
		return null;
	}

	@Override
	public void handleClick(ClickData data) {
		// Display only — no click action
	}
}
