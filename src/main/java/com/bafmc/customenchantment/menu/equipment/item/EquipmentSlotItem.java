package com.bafmc.customenchantment.menu.equipment.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu;
import org.bukkit.event.inventory.ClickType;

public class EquipmentSlotItem extends AbstractItem<EquipmentCustomMenu> {

	@Override
	public String getType() {
		return "equipment-slot";
	}

	@Override
	public void handleClick(ClickData data) {
		if (menu.isInUpdateMenu()) {
			return;
		}

		String name = itemData.getId();
		ClickType clickType = data.getEvent().getClick();

		if (clickType == ClickType.LEFT) {
			menu.returnItem(name, data.getClickedSlot());
		} else if (clickType == ClickType.RIGHT) {
			menu.swapSkinWithCooldown(name, data.getClickedSlot(), data.getPlayer());
		}
	}
}
