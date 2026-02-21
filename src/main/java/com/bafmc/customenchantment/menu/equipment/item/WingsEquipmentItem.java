package com.bafmc.customenchantment.menu.equipment.item;

import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu;
import org.bukkit.event.inventory.ClickType;

public class WingsEquipmentItem extends AbstractItem<EquipmentCustomMenu> {

	@Override
	public String getType() {
		return CEConstants.MenuItemType.WINGS_EQUIPMENT;
	}

	@Override
	public void handleClick(ClickData data) {
		if (menu.isInUpdateMenu()) {
			return;
		}

		ClickType clickType = data.getEvent().getClick();
		if (clickType == ClickType.RIGHT) {
			menu.swapSkinWithCooldown(EquipmentCustomMenu.WINGS_SLOT, data.getClickedSlot(), data.getPlayer());
		}
	}
}
