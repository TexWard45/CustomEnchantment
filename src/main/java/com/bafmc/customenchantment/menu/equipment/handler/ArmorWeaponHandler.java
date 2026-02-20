package com.bafmc.customenchantment.menu.equipment.handler;

import com.bafmc.bukkit.bafframework.event.ItemEquipEvent;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.MaterialUtils;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.CEWeaponType;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu.EquipmentAddReason;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerEquipment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ArmorWeaponHandler implements EquipmentSlotHandler {

	private static final Map<String, EquipSlot> EQUIP_SLOT_MAP = EquipmentCustomMenu.EQUIP_SLOT_MAP;

	private static final Map<EquipSlot, EquipmentSlot> EQUIPMENT_SLOT_MAP = Map.of(
			EquipSlot.HELMET, EquipmentSlot.HEAD,
			EquipSlot.CHESTPLATE, EquipmentSlot.CHEST,
			EquipSlot.LEGGINGS, EquipmentSlot.LEGS,
			EquipSlot.BOOTS, EquipmentSlot.FEET,
			EquipSlot.MAINHAND, EquipmentSlot.HAND
	);

	private final EquipmentCustomMenu menu;

	public ArmorWeaponHandler(EquipmentCustomMenu menu) {
		this.menu = menu;
	}

	@Override
	public boolean canAdd(ItemStack item, CEItem ceItem) {
		return true; // Fallback handler — always accepts
	}

	@Override
	public EquipmentAddReason add(InventoryClickEvent e, ItemStack itemStack, CEItem ceItem) {
		Material type = itemStack.getType();

		CEWeaponType weaponTypeEnum = null;
		if (ceItem instanceof CEWeaponAbstract ceWeaponAbstract) {
			weaponTypeEnum = ceWeaponAbstract.getWeaponType();
		}

		String weaponType = weaponTypeEnum != null ? weaponTypeEnum.name() : null;

		EquipSlot equipSlot = null;
		EquipmentSlot equipmentSlot = null;
		if (MaterialUtils.isSimilar(type, "HELMET")
				|| type == Material.TURTLE_HELMET
				|| MaterialUtils.isSimilar(type, "BANNER")
				|| MaterialUtils.isSimilar(type, "HEAD")
				|| "HELMET".equalsIgnoreCase(weaponType)) {
			equipSlot = EquipSlot.HELMET;
			equipmentSlot = EquipmentSlot.HEAD;
		} else if (MaterialUtils.isSimilar(type, "CHESTPLATE") || "CHESTPLATE".equalsIgnoreCase(weaponType)) {
			equipSlot = EquipSlot.CHESTPLATE;
			equipmentSlot = EquipmentSlot.CHEST;
		} else if (MaterialUtils.isSimilar(type, "LEGGINGS") || "LEGGINGS".equalsIgnoreCase(weaponType)) {
			equipSlot = EquipSlot.LEGGINGS;
			equipmentSlot = EquipmentSlot.LEGS;
		} else if (MaterialUtils.isSimilar(type, "BOOTS") || "BOOTS".equalsIgnoreCase(weaponType)) {
			equipSlot = EquipSlot.BOOTS;
			equipmentSlot = EquipmentSlot.FEET;
		}

		if (equipSlot != null) {
			ItemStack oldItemStack = equipSlot.getItemStack(menu.getOwner());
			if (oldItemStack.getAmount() > 1) {
				return EquipmentAddReason.NOTHING;
			}
			menu.getOwner().getInventory().setItem(equipmentSlot, itemStack);
			menu.updateMenuWithPreventAction();
			e.setCurrentItem(oldItemStack);
			menu.getExtraData().addLastClickTime(equipSlot, System.currentTimeMillis());
			return EquipmentAddReason.SUCCESS;
		} else {
			if (menu.getOwner().getInventory().getItem(EquipmentSlot.HAND).getType() == Material.AIR) {
				menu.getOwner().getInventory().setItem(EquipmentSlot.HAND, itemStack);
				menu.updateMenuWithPreventAction();
				e.setCurrentItem(null);
				menu.getExtraData().addLastClickTime(EquipSlot.MAINHAND, System.currentTimeMillis());
				return EquipmentAddReason.SUCCESS;
			} else {
				CEPlayer cePlayer = CEAPI.getCEPlayer(menu.getOwner());
				PlayerEquipment playerEquipment = cePlayer.getEquipment();
				if (!playerEquipment.hasWings()) {
					e.setCurrentItem(EquipSlot.OFFHAND.getItemStack(menu.getOwner()));
					menu.getOwner().getInventory().setItem(EquipmentSlot.OFF_HAND, itemStack);
				} else {
					ItemEquipEvent itemEquipEvent = new ItemEquipEvent(menu.getOwner(), EquipSlot.OFFHAND, playerEquipment.getOffhandItemStack(), itemStack);
					Bukkit.getPluginManager().callEvent(itemEquipEvent);

					e.setCurrentItem(playerEquipment.getOffhandItemStack());
					playerEquipment.setOffhandItemStack(itemStack);
				}
				menu.updateMenuWithPreventAction();
				menu.getExtraData().addLastClickTime(EquipSlot.OFFHAND, System.currentTimeMillis());
				return EquipmentAddReason.SUCCESS;
			}
		}
	}

	@Override
	public boolean canReturn(String itemName) {
		return itemName.equals(EquipSlot.OFFHAND.name().toLowerCase()) || EQUIP_SLOT_MAP.containsKey(itemName);
	}

	@Override
	public void returnItem(String itemName, int slot) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(menu.getOwner()).getEquipment();

		if (itemName.equals(EquipSlot.OFFHAND.name().toLowerCase())) {
			returnOffhand(playerEquipment);
		} else {
			returnArmorOrWeapon(itemName, playerEquipment);
		}
	}

	private void returnOffhand(PlayerEquipment playerEquipment) {
		if (playerEquipment.hasWings() && !playerEquipment.hasOffhandItemStack()) {
			return;
		}
		ItemStack itemStack = playerEquipment.getActualOffhandItemStack();
		if (itemStack != null) {
			if (menu.getExtraData().isInLastClickCooldown(EquipSlot.OFFHAND)) {
				return;
			}

			if (!playerEquipment.hasWings()) {
				menu.getOwner().getInventory().setItem(EquipmentSlot.OFF_HAND, null);
			} else {
				ItemEquipEvent itemEquipEvent = new ItemEquipEvent(menu.getOwner(), EquipSlot.OFFHAND, itemStack, null);
				Bukkit.getPluginManager().callEvent(itemEquipEvent);
				playerEquipment.setOffhandItemStack(null);
			}
			InventoryUtils.addItem(menu.getOwner(), itemStack);
			menu.updateMenuWithPreventAction();
		}
	}

	private void returnArmorOrWeapon(String itemName, PlayerEquipment playerEquipment) {
		EquipSlot equipSlot = EQUIP_SLOT_MAP.get(itemName);
		if (equipSlot != null) {
			ItemStack itemStack = equipSlot.getItemStack(menu.getOwner());
			if (itemStack != null) {
				if (menu.getExtraData().isInLastClickCooldown(equipSlot)) {
					return;
				}

				int firstEmpty = -1;
				for (int i = 0; i < 36; i++) {
					if (menu.getOwner().getInventory().getItem(i) == null) {
						firstEmpty = i;
						break;
					}
				}

				if (firstEmpty != -1) {
					menu.getOwner().getInventory().setItem(EQUIPMENT_SLOT_MAP.get(equipSlot), null);
					menu.getOwner().getInventory().setItem(firstEmpty, itemStack);
				}

				menu.updateMenuWithPreventAction();
			}
		}
	}
}
