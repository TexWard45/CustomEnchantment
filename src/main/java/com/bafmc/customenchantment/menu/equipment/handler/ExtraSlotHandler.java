package com.bafmc.customenchantment.menu.equipment.handler;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.config.data.ExtraSlotSettingsData;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.item.outfit.CEOutfit;
import com.bafmc.customenchantment.item.sigil.CESigil;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu.EquipmentAddReason;
import com.bafmc.customenchantment.player.PlayerEquipment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ExtraSlotHandler implements EquipmentSlotHandler {

	private final EquipmentCustomMenu menu;

	public ExtraSlotHandler(EquipmentCustomMenu menu) {
		this.menu = menu;
	}

	@Override
	public boolean canAdd(ItemStack item, CEItem ceItem) {
		ExtraSlotSettingsData extraSlot = CustomEnchantment.instance().getMainConfig().getExtraSlotSettings(ceItem);
		return extraSlot != null && ceItem instanceof CEWeaponAbstract;
	}

	@Override
	public EquipmentAddReason add(InventoryClickEvent e, ItemStack item, CEItem ceItem) {
		if (item.getAmount() > 1) {
			return EquipmentAddReason.NOTHING;
		}

		CEWeaponAbstract ceWeaponAbstract = (CEWeaponAbstract) ceItem;
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(menu.getOwner()).getEquipment();

		int maxExtraSlotUseCount = getMaxExtraSlotUseCount(ceWeaponAbstract);
		if (maxExtraSlotUseCount <= 0) {
			return EquipmentAddReason.NO_EXTRA_SLOT;
		}

		int emptyIndex = getEmptyExtraSlotIndex(ceWeaponAbstract);
		if (emptyIndex != -1) {
			if (checkDuplicateExtraSlot(ceWeaponAbstract)) {
				return EquipmentAddReason.DUPLICATE_EXTRA_SLOT;
			}
		} else {
			return EquipmentAddReason.MAX_EXTRA_SLOT;
		}

		ExtraSlotSettingsData extraSlot = CustomEnchantment.instance().getMainConfig().getExtraSlotSettings(ceItem);
		playerEquipment.setSlot(extraSlot.getSlot(emptyIndex), ceWeaponAbstract, true);
		menu.updateMenuWithPreventAction();
		e.setCurrentItem(null);
		menu.getExtraData().addLastClickTime(EquipSlot.EXTRA_SLOT, System.currentTimeMillis());
		return EquipmentAddReason.ADD_EXTRA_SLOT;
	}

	@Override
	public boolean canReturn(String itemName) {
		return itemName.startsWith(EquipmentCustomMenu.EXTRA_SLOT);
	}

	@Override
	public void returnItem(String itemName, int slot) {
		if (menu.getExtraData().isInLastClickCooldown(EquipSlot.EXTRA_SLOT)) {
			return;
		}

		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(menu.getOwner()).getEquipment();
		Map<String, ExtraSlotSettingsData> map = CustomEnchantment.instance().getMainConfig().getExtraSlotSettingMap();
		ExtraSlotSettingsData data = map.entrySet().stream()
				.filter(entry -> itemName.equals(EquipmentCustomMenu.EXTRA_SLOT + "-" + entry.getKey()))
				.map(Map.Entry::getValue)
				.findFirst()
				.orElse(null);

		if (data != null) {
			List<Integer> extraSlots = menu.getSlotsByName(itemName);
			int index = extraSlots.indexOf(slot);
			if (index != -1) {
				CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(data.getSlot(index));
				if (weaponAbstract != null) {
					playerEquipment.setSlot(data.getSlot(index), null, true);
					InventoryUtils.addItem(menu.getOwner(), weaponAbstract.getDefaultItemStack());
					menu.updateMenuWithPreventAction();
				}
			}
		}
	}

	// ==================== Extra Slot Helpers ====================

	public int getMaxExtraSlotUseCount(CEWeaponAbstract ceItem) {
		return CustomEnchantment.instance().getMainConfig().getExtraSlotSettings(ceItem).getMaxCount();
	}

	public int getEmptyExtraSlotIndex(CEWeaponAbstract ceWeaponAbstract) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(menu.getOwner()).getEquipment();
		playerEquipment.sortExtraSlot();

		ExtraSlotSettingsData data = CustomEnchantment.instance().getMainConfig().getExtraSlotSettings(ceWeaponAbstract);

		int maxArtifactUseCount = getMaxExtraSlotUseCount(ceWeaponAbstract);
		for (int i = 0; i < maxArtifactUseCount; i++) {
			if (playerEquipment.getSlot(data.getSlot(i)) == null) {
				return i;
			}
		}
		return -1;
	}

	public boolean checkDuplicateExtraSlot(CEWeaponAbstract ceWeaponAbstract) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(menu.getOwner()).getEquipment();
		ExtraSlotSettingsData data = CustomEnchantment.instance().getMainConfig().getExtraSlotSettings(ceWeaponAbstract);
		String targetPattern = ceWeaponAbstract.getData().getPattern();
		int maxArtifactUseCount = getMaxExtraSlotUseCount(ceWeaponAbstract);

		for (int i = 0; i < maxArtifactUseCount; i++) {
			CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(data.getSlot(i));
			if (weaponAbstract == null) continue;

			String existingPattern = switch (weaponAbstract) {
				case CEArtifact artifact -> artifact.getData().getPattern();
				case CESigil sigil -> sigil.getData().getId();
				case CEOutfit outfit -> outfit.getData().getId();
				default -> null;
			};

			if (targetPattern.equals(existingPattern)) {
				return true;
			}
		}

		return false;
	}
}
