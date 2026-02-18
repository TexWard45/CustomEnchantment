package com.bafmc.customenchantment.menu.equipment.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu;
import org.bukkit.event.inventory.ClickType;

public class ExtraSlotEquipmentItem extends AbstractItem<EquipmentCustomMenu> {

	@Override
	public String getType() {
		return "extra-slot-equipment";
	}

	@Override
	public void handleClick(ClickData data) {
		if (menu.isInUpdateMenu()) {
			return;
		}

		ClickType clickType = data.getEvent().getClick();
		if (clickType == ClickType.LEFT) {
			menu.returnItem(itemData.getId(), data.getClickedSlot());
		}
	}
}
