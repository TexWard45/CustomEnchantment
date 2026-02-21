package com.bafmc.customenchantment.menu.equipment.item;

import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu;
import org.bukkit.event.inventory.ClickType;

public class ProtectDeadEquipmentItem extends AbstractItem<EquipmentCustomMenu> {

	@Override
	public String getType() {
		return CEConstants.MenuItemType.PROTECT_DEAD_EQUIPMENT;
	}

	@Override
	public void handleClick(ClickData data) {
		if (menu.isInUpdateMenu()) {
			return;
		}

		ClickType clickType = data.getEvent().getClick();
		if (clickType == ClickType.LEFT) {
			menu.returnItem(EquipmentCustomMenu.PROTECT_DEAD_SLOT, data.getClickedSlot());
		}
	}
}
