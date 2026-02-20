package com.bafmc.customenchantment.menu.equipment.handler;

import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu.EquipmentAddReason;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface EquipmentSlotHandler {

	boolean canAdd(ItemStack item, CEItem ceItem);

	EquipmentAddReason add(InventoryClickEvent e, ItemStack item, CEItem ceItem);

	boolean canReturn(String itemName);

	void returnItem(String itemName, int slot);
}
