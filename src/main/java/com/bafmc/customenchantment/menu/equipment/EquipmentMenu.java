package com.bafmc.customenchantment.menu.equipment;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.utils.MaterialUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.item.protectdead.CEProtectDead;
import com.bafmc.customenchantment.menu.MenuAbstract;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerEquipment;
import com.bafmc.customenchantment.player.PlayerStorage;
import com.bafmc.customenchantment.utils.StorageUtils;
import com.bafmc.custommenu.menu.CMenuView;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipmentMenu extends MenuAbstract {
	public static final String MENU_NAME = "equipment";
	public static final String PROTECT_DEAD_SLOT = "protect-dead";
	public static final String ARTIFACT_SLOT = "artifact";
	private static HashMap<String, EquipmentMenu> map = new HashMap<String, EquipmentMenu>();
	@Getter
	private boolean inUpdateMenu = false;

	public static EquipmentMenu putMenu(Player player, CMenuView cMenuView) {
		EquipmentMenu menu = map.get(player.getName());

		if (menu == null) {
			menu = new EquipmentMenu(cMenuView, player);
			map.put(player.getName(), menu);
		}

		return menu;
	}

	public static EquipmentMenu getMenu(Player player) {
		return map.get(player.getName());
	}

	public static EquipmentMenu removeMenu(Player player) {
		return map.remove(player.getName());
	}

	public enum EquipmentAddReason {
		SUCCESS, NOT_SUPPORT_ITEM,UNDRESS_FIRST,
		// ARTIFACT
		ADD_ARTIFACT, MAX_ARTIFACT, DUPLICATE_ARTIFACT, NOTHING, NO_ARTIFACT_SLOT,
		// PROTECT_DEAD
		ADD_PROTECT_DEAD, EXCEED_PROTECT_DEAD, DIFFERENT_PROTECT_DEAD;
	}

	public enum EquipmentConfirmReason {
		SUCCESS, NOTHING;
	}

	public EquipmentMenu(CMenuView menuView, Player player) {
		super(menuView, player);
		this.updateMenu();
	}

	private Map<EquipSlot, Long> lastClickTime = new HashMap<EquipSlot, Long>();
	public EquipmentAddReason addItem(InventoryClickEvent e, ItemStack itemStack, CEItem ceItem) {
		if (ceItem instanceof CEArtifact) {
			if (itemStack.getAmount() > 1) {
				return EquipmentAddReason.NOTHING;
			}

			PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
			int maxArtifactUseCount = getMaxArtifactUseCount();
			if (maxArtifactUseCount <= 0) {
				return EquipmentAddReason.NO_ARTIFACT_SLOT;
			}

			int emptyIndex = getEmptyArtifactIndex();
			if (emptyIndex != -1) {
				if (checkDuplicateArtifactSlot((CEArtifact) ceItem)) {
					return EquipmentAddReason.DUPLICATE_ARTIFACT;
				}
			} else {
				return EquipmentAddReason.MAX_ARTIFACT;
			}

			playerEquipment.setSlot(EquipSlot.getExtraSlot(emptyIndex), (CEArtifact) ceItem, true);
			updateMenuWithPreventAction();
			e.setCurrentItem(null);
			return EquipmentAddReason.ADD_ARTIFACT;
		}else if (ceItem instanceof CEProtectDead protectDead && protectDead.getData().isAdvancedMode()) {
			CEPlayer cePlayer = CEAPI.getCEPlayer(player);
			PlayerStorage playerStorage = cePlayer.getStorage();

			String type = StorageUtils.getProtectDeadType(playerStorage);
			int amount = StorageUtils.getProtectDeadAmount(playerStorage);

			if (type == null) {
				type = protectDead.getData().getPattern();
				StorageUtils.setProtectDeadType(playerStorage, type);
				StorageUtils.setProtectDeadAmount(playerStorage, StorageUtils.getProtectDeadAmount(playerStorage) + protectDead.getData().getExtraPoint());
				this.updateMenuWithPreventAction();
				e.setCurrentItem(null);
				return EquipmentAddReason.ADD_PROTECT_DEAD;
			}else {
				if (StorageUtils.isDifferentProtectDead(playerStorage, protectDead.getData().getPattern())) {
					return EquipmentAddReason.DIFFERENT_PROTECT_DEAD;
				}else {
					int newAmount = amount + protectDead.getData().getExtraPoint();
					if (newAmount > protectDead.getData().getMaxPoint()) {
						return EquipmentAddReason.EXCEED_PROTECT_DEAD;
					}
					StorageUtils.setProtectDeadAmount(playerStorage, StorageUtils.getProtectDeadAmount(playerStorage) + protectDead.getData().getExtraPoint());
					this.updateMenuWithPreventAction();
					e.setCurrentItem(null);
					return EquipmentAddReason.ADD_PROTECT_DEAD;
				}
			}
		}else {
			Material type = itemStack.getType();

			EquipSlot equipSlot = null;
			EquipmentSlot equipmentSlot = null;
			if (MaterialUtils.isSimilar(type, "HELMET")
					|| type == Material.TURTLE_HELMET
					|| MaterialUtils.isSimilar(type, "BANNER")
					|| MaterialUtils.isSimilar(type, "HEAD")) {
				equipSlot = EquipSlot.HELMET;
				equipmentSlot = EquipmentSlot.HEAD;
			}else if (MaterialUtils.isSimilar(type, "CHESTPLATE")) {
				equipSlot = EquipSlot.CHESTPLATE;
				equipmentSlot = EquipmentSlot.CHEST;
			}else if (MaterialUtils.isSimilar(type, "LEGGINGS")) {
				equipSlot = EquipSlot.LEGGINGS;
				equipmentSlot = EquipmentSlot.LEGS;
			}else if (MaterialUtils.isSimilar(type, "BOOTS")) {
				equipSlot = EquipSlot.BOOTS;
				equipmentSlot = EquipmentSlot.FEET;
			}

			if (equipSlot != null) {
				lastClickTime.putIfAbsent(equipSlot, 0L);

				long currentTime = System.currentTimeMillis();
				if (currentTime - lastClickTime.get(equipSlot) < 500) {
					return EquipmentAddReason.NOTHING;
				}

				lastClickTime.put(equipSlot, currentTime);

				ItemStack oldItemStack = equipSlot.getItemStack(player);
				if (oldItemStack.getAmount() > 1) {
					return EquipmentAddReason.NOTHING;
				}
				player.getInventory().setItem(equipmentSlot, itemStack);
				this.updateMenuWithPreventAction();
				e.setCurrentItem(oldItemStack);
				return EquipmentAddReason.SUCCESS;
			}else if (ceItem instanceof CEWeaponAbstract) {
				if (EquipSlot.MAINHAND.getItemStack(player).getType() != Material.AIR) {
					if (EquipSlot.OFFHAND.getItemStack(player).getType() != Material.AIR) {
						return EquipmentAddReason.NOT_SUPPORT_ITEM;
					}
					player.getInventory().setItem(EquipmentSlot.OFF_HAND, itemStack);
					this.updateMenuWithPreventAction();
					e.setCurrentItem(null);
					return EquipmentAddReason.SUCCESS;
				}

				if (EquipSlot.HAND.getItemStack(player).getType() != Material.AIR) {
					return EquipmentAddReason.NOT_SUPPORT_ITEM;
				}
				player.getInventory().setItem(EquipmentSlot.HAND, itemStack);
				this.updateMenuWithPreventAction();
				e.setCurrentItem(null);
				return EquipmentAddReason.SUCCESS;
			}
		}
		return EquipmentAddReason.NOT_SUPPORT_ITEM;
	}

	public void updateMenuWithPreventAction() {
		inUpdateMenu = true;
		Bukkit.getScheduler().runTask(CustomEnchantment.instance(), () -> {
			updateMenu();
			inUpdateMenu = false;
		});
	}

	public void updateMenu() {
		updateSlots("helmet", EquipSlot.HELMET.getItemStack(player));
		updateSlots("chestplate", EquipSlot.CHESTPLATE.getItemStack(player));
		updateSlots("leggings", EquipSlot.LEGGINGS.getItemStack(player));
		updateSlots("boots", EquipSlot.BOOTS.getItemStack(player));
		updateSlots("offhand", EquipSlot.OFFHAND.getItemStack(player));
		updateSlots("mainhand", EquipSlot.MAINHAND.getItemStack(player));
		updateProtectDeadSlots();
		updateArtifactSlots();
	}

	public void updateProtectDeadSlots() {
		PlayerStorage storage = CEAPI.getCEPlayer(player).getStorage();
		String type = StorageUtils.getProtectDeadType(storage);
		ItemStack itemStack = null;
		if (type != null) {
			int amount = StorageUtils.getProtectDeadAmount(storage);

			CEItem ceItem = CEAPI.getCEItemByStorage(CEItemType.PROTECT_DEAD, type);
			if (ceItem instanceof CEProtectDead protectDead && protectDead.getData().isAdvancedMode()) {
				itemStack = protectDead.getDefaultItemStack().clone();

				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.hasMaxStackSize();
				itemMeta.setMaxStackSize(99);
				itemStack.setItemMeta(itemMeta);

				itemStack.setAmount(amount);
			}else {
				StorageUtils.removeProtectDead(storage);
			}
		}

		List<Integer> slots = getSlots(PROTECT_DEAD_SLOT);
		for (int slot : slots) {
			if (itemStack != null) {
				menuView.setTemporaryItem(slot, itemStack);
			}else {
				menuView.removeTemporaryItem(slot);
			}
		}
	}

	public void updateArtifactSlots() {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		playerEquipment.sortExtraSlot();
		int maxArtifactUseCount = getMaxArtifactUseCount();
		for (int i = 0; i < maxArtifactUseCount; i++) {
			List<Integer> slots = getSlots(ARTIFACT_SLOT);

			if (i < slots.size()) {
				CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(EquipSlot.getExtraSlot(i));
				if (weaponAbstract instanceof CEArtifact) {
					menuView.setTemporaryItem(slots.get(i), weaponAbstract.getDefaultItemStack());
				}else {
					menuView.removeTemporaryItem(slots.get(i));
				}
			}
		}
	}

	public int getMaxArtifactUseCount() {
		return CustomEnchantment.instance().getMainConfig().getMaxArtifactUseCount();
	}

	private List<String> itemInteractList = Arrays.asList(
			EquipmentMenu.PROTECT_DEAD_SLOT,
			EquipmentMenu.ARTIFACT_SLOT,
			EquipSlot.HELMET.name().toLowerCase(),
			EquipSlot.CHESTPLATE.name().toLowerCase(),
			EquipSlot.LEGGINGS.name().toLowerCase(),
			EquipSlot.BOOTS.name().toLowerCase(),
			EquipSlot.OFFHAND.name().toLowerCase(),
			EquipSlot.MAINHAND.name().toLowerCase()
	);
	public void returnItem(String itemName, int slot) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();

		if (itemInteractList.contains(itemName) && player.getInventory().firstEmpty() == -1) {
			CustomEnchantmentMessage.send(player, "menu.equipment.return-item.no-empty-slot");
			return;
		}

		if (itemName.equals(EquipmentMenu.ARTIFACT_SLOT)) {
			List<Integer> artifactSlots = getSlots(ARTIFACT_SLOT);
			if (artifactSlots.contains(slot)) {
				int index = -1;
				for (int i = 0; i < artifactSlots.size(); i++) {
					if (artifactSlots.get(i) == slot) {
						index = i;
						break;
					}
				}

				if (index != -1) {
					CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(EquipSlot.getExtraSlot(index));
					if (weaponAbstract != null) {
						playerEquipment.setSlot(EquipSlot.getExtraSlot(index), null, true);
						InventoryUtils.addItem(player, weaponAbstract.getDefaultItemStack());
					}
					updateMenuWithPreventAction();
				}
			}
		}else if (itemName.equals(EquipmentMenu.PROTECT_DEAD_SLOT)) {
			List<Integer> protectDeadSlots = getSlots(PROTECT_DEAD_SLOT);
			if (protectDeadSlots.contains(slot)) {
				CEPlayer cePlayer = CEAPI.getCEPlayer(player);
				PlayerStorage playerStorage = cePlayer.getStorage();

				String type = StorageUtils.getProtectDeadType(playerStorage);
				if (type != null) {
					CEItem ceItem = CEAPI.getCEItemByStorage(CEItemType.PROTECT_DEAD, type);
					if (ceItem instanceof CEProtectDead protectDead && protectDead.getData().isAdvancedMode()) {
						StorageUtils.useProtectDead(playerStorage);
						InventoryUtils.addItem(player, protectDead.exportTo());
					} else {
						StorageUtils.removeProtectDead(playerStorage);
					}
					updateMenu();
				}
			}
		}else {
			if (itemName.equals(EquipSlot.HELMET.name().toLowerCase())) {
				ItemStack itemStack = EquipSlot.HELMET.getItemStack(player);
				if (itemStack != null) {
					player.getInventory().setItem(EquipmentSlot.HEAD, null);
					InventoryUtils.addItem(player, itemStack);
					updateMenuWithPreventAction();
				}
			} else if (itemName.equals(EquipSlot.CHESTPLATE.name().toLowerCase())) {
				ItemStack itemStack = EquipSlot.CHESTPLATE.getItemStack(player);
				if (itemStack != null) {
					player.getInventory().setItem(EquipmentSlot.CHEST, null);
					InventoryUtils.addItem(player, itemStack);
					updateMenuWithPreventAction();
				}
			} else if (itemName.equals(EquipSlot.LEGGINGS.name().toLowerCase())) {
				ItemStack itemStack = EquipSlot.LEGGINGS.getItemStack(player);
				if (itemStack != null) {
					player.getInventory().setItem(EquipmentSlot.LEGS, null);
					InventoryUtils.addItem(player, itemStack);
					updateMenuWithPreventAction();
				}
			} else if (itemName.equals(EquipSlot.BOOTS.name().toLowerCase())) {
				ItemStack itemStack = EquipSlot.BOOTS.getItemStack(player);
				if (itemStack != null) {
					player.getInventory().setItem(EquipmentSlot.FEET, null);
					InventoryUtils.addItem(player, itemStack);
					updateMenuWithPreventAction();
				}
			} else if (itemName.equals(EquipSlot.OFFHAND.name().toLowerCase())) {
				ItemStack itemStack = EquipSlot.OFFHAND.getItemStack(player);
				if (itemStack != null) {
					player.getInventory().setItem(EquipmentSlot.OFF_HAND, null);
					InventoryUtils.addItem(player, itemStack);
					updateMenuWithPreventAction();
				}
			} else if (itemName.equals(EquipSlot.MAINHAND.name().toLowerCase())) {
				ItemStack itemStack = EquipSlot.MAINHAND.getItemStack(player);
				if (itemStack != null) {
					player.getInventory().setItem(EquipmentSlot.HAND, null);
					InventoryUtils.addItem(player, itemStack);
					updateMenuWithPreventAction();
				}
			}
		}
    }

	public void returnItems() {
	}

	public int getEmptyArtifactIndex() {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		playerEquipment.sortExtraSlot();

		int maxArtifactUseCount = getMaxArtifactUseCount();
		for (int i = 0; i < maxArtifactUseCount; i++) {
			if (playerEquipment.getSlot(EquipSlot.getExtraSlot(i)) == null) {
				return i;
			}
		}
		return -1;
	}

	public boolean checkDuplicateArtifactSlot(CEArtifact ceArtifact) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		int maxArtifactUseCount = getMaxArtifactUseCount();
		for (int i = 0; i < maxArtifactUseCount; i++) {
			CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(EquipSlot.getExtraSlot(i));
			if (weaponAbstract instanceof CEArtifact) {
				CEArtifact artifact = (CEArtifact) weaponAbstract;
				if (artifact.getData().getPattern().equals(ceArtifact.getData().getPattern())) {
					return true;
				}
			}
		}
		return false;
	}
}
