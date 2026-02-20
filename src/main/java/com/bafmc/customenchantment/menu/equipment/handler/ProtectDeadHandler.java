package com.bafmc.customenchantment.menu.equipment.handler;

import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.protectdead.CEProtectDead;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu.EquipmentAddReason;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerStorage;
import com.bafmc.customenchantment.utils.StorageUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ProtectDeadHandler implements EquipmentSlotHandler {

	private final EquipmentCustomMenu menu;

	public ProtectDeadHandler(EquipmentCustomMenu menu) {
		this.menu = menu;
	}

	@Override
	public boolean canAdd(ItemStack item, CEItem ceItem) {
		return ceItem instanceof CEProtectDead protectDead && protectDead.getData().isAdvancedMode();
	}

	@Override
	public EquipmentAddReason add(InventoryClickEvent e, ItemStack item, CEItem ceItem) {
		CEProtectDead protectDead = (CEProtectDead) ceItem;
		CEPlayer cePlayer = CEAPI.getCEPlayer(menu.getOwner());
		PlayerStorage playerStorage = cePlayer.getStorage();

		String type = StorageUtils.getProtectDeadType(playerStorage);
		int amount = StorageUtils.getProtectDeadAmount(playerStorage);

		if (type == null) {
			type = protectDead.getData().getPattern();
			StorageUtils.setProtectDeadType(playerStorage, type);
			StorageUtils.setProtectDeadAmount(playerStorage, StorageUtils.getProtectDeadAmount(playerStorage) + protectDead.getData().getExtraPoint());
			menu.updateMenuWithPreventAction();
			e.setCurrentItem(null);
			return EquipmentAddReason.ADD_PROTECT_DEAD;
		} else {
			if (StorageUtils.isDifferentProtectDead(playerStorage, protectDead.getData().getPattern())) {
				return EquipmentAddReason.DIFFERENT_PROTECT_DEAD;
			} else {
				int newAmount = amount + protectDead.getData().getExtraPoint();
				if (newAmount > protectDead.getData().getMaxPoint()) {
					return EquipmentAddReason.EXCEED_PROTECT_DEAD;
				}
				StorageUtils.setProtectDeadAmount(playerStorage, StorageUtils.getProtectDeadAmount(playerStorage) + protectDead.getData().getExtraPoint());
				menu.updateMenuWithPreventAction();
				e.setCurrentItem(null);
				return EquipmentAddReason.ADD_PROTECT_DEAD;
			}
		}
	}

	@Override
	public boolean canReturn(String itemName) {
		return itemName.equals(EquipmentCustomMenu.PROTECT_DEAD_SLOT);
	}

	@Override
	public void returnItem(String itemName, int slot) {
		if (menu.getSlotsByName(EquipmentCustomMenu.PROTECT_DEAD_SLOT).contains(slot)) {
			CEPlayer cePlayer = CEAPI.getCEPlayer(menu.getOwner());
			PlayerStorage playerStorage = cePlayer.getStorage();
			String type = StorageUtils.getProtectDeadType(playerStorage);
			if (type != null) {
				CEItem ceItem = CEAPI.getCEItemByStorage(CEItemType.PROTECT_DEAD, type);
				if (ceItem instanceof CEProtectDead protectDead && protectDead.getData().isAdvancedMode()) {
					StorageUtils.useProtectDead(playerStorage);
					InventoryUtils.addItem(menu.getOwner(), protectDead.exportTo());
				} else {
					StorageUtils.removeProtectDead(playerStorage);
				}
				menu.updateMenu();
			}
		}
	}
}
