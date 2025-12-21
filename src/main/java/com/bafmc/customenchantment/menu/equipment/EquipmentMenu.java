package com.bafmc.customenchantment.menu.equipment;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.utils.MaterialUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.config.data.ExtraSlotSettingsData;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.item.outfit.CEOutfit;
import com.bafmc.customenchantment.item.protectdead.CEProtectDead;
import com.bafmc.customenchantment.item.sigil.CESigil;
import com.bafmc.customenchantment.menu.MenuAbstract;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerEquipment;
import com.bafmc.customenchantment.player.PlayerStorage;
import com.bafmc.customenchantment.task.OutfitItemAsyncTask;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipmentMenu extends MenuAbstract {
	public static final String MENU_NAME = "equipment";
	public static final String PROTECT_DEAD_SLOT = "protect-dead";
	public static final String EXTRA_SLOT = "extra-slot";
	public static final String PLAYER_INFO_SLOT = "player-info";
	public static final String WINGS_SLOT = "wings";
	private static HashMap<String, EquipmentMenu> map = new HashMap<String, EquipmentMenu>();
	@Getter
	private boolean inUpdateMenu = false;
	private boolean removed = false;

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
		EquipmentMenu menu = map.remove(player.getName());

		if (menu != null) {
			menu.setRemoved(true);
		}

		return menu;
	}

	private void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public enum EquipmentAddReason {
		SUCCESS, NOT_SUPPORT_ITEM,UNDRESS_FIRST,
		// EXTRA_SLOT
		ADD_EXTRA_SLOT, MAX_EXTRA_SLOT, DUPLICATE_EXTRA_SLOT, NOTHING, NO_EXTRA_SLOT,
		// PROTECT_DEAD
		ADD_PROTECT_DEAD, EXCEED_PROTECT_DEAD, DIFFERENT_PROTECT_DEAD;
	}

	public enum EquipmentConfirmReason {
		SUCCESS, NOTHING;
	}

	public EquipmentMenu(CMenuView menuView, Player player) {
		super(menuView, player);
		this.updateMenu();
		this.autoUpdateMenu();
	}

	private Map<EquipSlot, Long> lastClickTime = new HashMap<EquipSlot, Long>();
	public EquipmentAddReason addItem(InventoryClickEvent e, ItemStack itemStack, CEItem ceItem) {
		ExtraSlotSettingsData extraSlot = CustomEnchantment.instance().getMainConfig().getExtraSlotSettings(ceItem);
		if (extraSlot != null && ceItem instanceof CEWeaponAbstract ceWeaponAbstract) {
			if (itemStack.getAmount() > 1) {
				return EquipmentAddReason.NOTHING;
			}

			PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
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

			playerEquipment.setSlot(extraSlot.getSlot(emptyIndex), ceWeaponAbstract, true);
			updateMenuWithPreventAction();
			e.setCurrentItem(null);
			return EquipmentAddReason.ADD_EXTRA_SLOT;
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

			String customType = null;
			if (ceItem instanceof CEWeaponAbstract ceWeaponAbstract) {
				customType = ceWeaponAbstract.getCustomType();
			}

			EquipSlot equipSlot = null;
			EquipmentSlot equipmentSlot = null;
			if (MaterialUtils.isSimilar(type, "HELMET")
					|| type == Material.TURTLE_HELMET
					|| MaterialUtils.isSimilar(type, "BANNER")
					|| MaterialUtils.isSimilar(type, "HEAD")
					|| "HELMET".equalsIgnoreCase(customType)) {
				equipSlot = EquipSlot.HELMET;
				equipmentSlot = EquipmentSlot.HEAD;
			}else if (MaterialUtils.isSimilar(type, "CHESTPLATE") || "CHESTPLATE".equalsIgnoreCase(customType)) {
				equipSlot = EquipSlot.CHESTPLATE;
				equipmentSlot = EquipmentSlot.CHEST;
			}else if (MaterialUtils.isSimilar(type, "LEGGINGS") || "LEGGINGS".equalsIgnoreCase(customType)) {
				equipSlot = EquipSlot.LEGGINGS;
				equipmentSlot = EquipmentSlot.LEGS;
			}else if (MaterialUtils.isSimilar(type, "BOOTS") || "BOOTS".equalsIgnoreCase(customType)) {
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
		updateExtraSlots();
		updatePlayerInfoSlots();
		updateWingsSlot();
	}

	public void autoUpdateMenu() {
		new BukkitRunnable() {
			public void run() {
				if (removed) {
					cancel();
					return;
				}
				updatePlayerInfoSlots();
			}
		}.runTaskTimer(CustomEnchantment.instance(), 0, 5);
	}

	public void updatePlayerInfoSlots() {
		List<Integer> slots = getSlots(PLAYER_INFO_SLOT);
		for (int slot : slots) {
			menuView.setTemporaryItem(slot, getItemStack(player, "player-info"));
		}
	}

	public void updateWingsSlot() {
		List<Integer> slots = getSlots(WINGS_SLOT);

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		CEOutfit outfit = cePlayer.getEquipment().getCEOutfit();
		if (outfit == null) {
			for (int slot : slots) {
				menuView.removeTemporaryItem(slot);
			}
			return;
		}

		int skinIndex = cePlayer.getEquipment().getSkinIndex(outfit.getData().getPattern(), "WINGS");
		if (skinIndex == -1) {
			for (int slot : slots) {
				menuView.setTemporaryItem(slot, getItemStack(player, "wings-off"));
			}
			return;
		}

		String skinName = outfit.getData().getConfigByLevelData().getSkinByIndex("WINGS", skinIndex);
		if (skinName == null || skinName.isEmpty()) {
			return;
		}

		ItemStack itemStack = getSkinItemStack(skinName);
		if (itemStack == null) {
			return;
		}

		for (int slot : slots) {
			menuView.setTemporaryItem(slot, itemStack);
		}
	}

	private ItemStack getSkinItemStack(String skinName) {
		Parameter parameter = new Parameter(List.of(skinName));
		List<ItemStack> itemStacks = CustomEnchantment.instance()
				.getCeItemStorageMap()
				.get(CEItemType.SKIN)
				.getItemStacksByParameter(parameter);

		return (itemStacks != null && !itemStacks.isEmpty()) ? itemStacks.get(0) : null;
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

	public void updateExtraSlots() {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		playerEquipment.sortExtraSlot();

		Map<String, ExtraSlotSettingsData> map = CustomEnchantment.instance().getMainConfig().getExtraSlotSettingMap();

		for (String key : map.keySet()) {
			ExtraSlotSettingsData data = map.get(key);

			int maxArtifactUseCount = data.getMaxCount();
			List<Integer> slots = getSlots(EXTRA_SLOT + "-" + key);

			for (int i = 0; i < maxArtifactUseCount; i++) {
				if (i < slots.size()) {
					CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(data.getSlot(i));
					if (weaponAbstract instanceof CEArtifact || weaponAbstract instanceof CESigil || weaponAbstract instanceof CEOutfit) {
						menuView.setTemporaryItem(slots.get(i), weaponAbstract.getDefaultItemStack());
					}else {
						menuView.removeTemporaryItem(slots.get(i));
					}
				}
			}
		}
	}

	public int getMaxExtraSlotUseCount(CEWeaponAbstract ceItem) {
		return CustomEnchantment.instance().getMainConfig().getExtraSlotSettings(ceItem).getMaxCount();
	}

	private List<String> itemInteractList = Arrays.asList(
			EquipmentMenu.PROTECT_DEAD_SLOT,
			EquipSlot.HELMET.name().toLowerCase(),
			EquipSlot.CHESTPLATE.name().toLowerCase(),
			EquipSlot.LEGGINGS.name().toLowerCase(),
			EquipSlot.BOOTS.name().toLowerCase(),
			EquipSlot.OFFHAND.name().toLowerCase(),
			EquipSlot.MAINHAND.name().toLowerCase()
	);
	public void returnItem(String itemName, int slot) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();

		if (itemInteractList.contains(itemName) || itemName.startsWith(EquipmentMenu.EXTRA_SLOT)) {
			if (player.getInventory().firstEmpty() == -1) {
				CustomEnchantmentMessage.send(player, "menu.equipment.return-item.no-empty-slot");
				return;
			}
		}

		if (itemName.startsWith(EquipmentMenu.EXTRA_SLOT)) {
			Map<String, ExtraSlotSettingsData> map = CustomEnchantment.instance().getMainConfig().getExtraSlotSettingMap();

			String specificSlot = null;
			ExtraSlotSettingsData data = null;

			for (String key : map.keySet()) {
				if (itemName.equals(EquipmentMenu.EXTRA_SLOT + "-" + key)) {
					data = map.get(key);
					specificSlot = EquipmentMenu.EXTRA_SLOT + "-" + key;
					break;
				}
			}

			List<Integer> extraSlots = getSlots(specificSlot);
			if (extraSlots.contains(slot)) {
				int index = -1;
				for (int i = 0; i < extraSlots.size(); i++) {
					if (extraSlots.get(i) == slot) {
						index = i;
						break;
					}
				}

				if (index != -1) {
					CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(data.getSlot(index));
					if (weaponAbstract != null) {
						playerEquipment.setSlot(data.getSlot(index), null, true);
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

	public void swapSkin(String itemName, int slot) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		CEOutfit ceOutfit = CEAPI.getCEPlayer(player).getEquipment().getCEOutfit();
		if (ceOutfit == null) {
			return;
		}

		CEWeaponAbstract weaponAbstract = null;
		String customType = null;

		if (itemName.equals(EquipSlot.HELMET.name().toLowerCase())) {
			weaponAbstract = playerEquipment.getSlot(EquipSlot.HELMET);
			customType = weaponAbstract != null ? weaponAbstract.getCustomType() : null;
		} else if (itemName.equals(EquipSlot.CHESTPLATE.name().toLowerCase())) {
			weaponAbstract = playerEquipment.getSlot(EquipSlot.CHESTPLATE);
			customType = weaponAbstract != null ? weaponAbstract.getCustomType() : null;
		} else if (itemName.equals(EquipSlot.LEGGINGS.name().toLowerCase())) {
			weaponAbstract = playerEquipment.getSlot(EquipSlot.LEGGINGS);
			customType = weaponAbstract != null ? weaponAbstract.getCustomType() : null;
		} else if (itemName.equals(EquipSlot.BOOTS.name().toLowerCase())) {
			weaponAbstract = playerEquipment.getSlot(EquipSlot.BOOTS);
			customType = weaponAbstract != null ? weaponAbstract.getCustomType() : null;
		} else if (itemName.equals("wings")) {
			customType = "WINGS";
		}

		if (customType == null || customType.isEmpty()) {
			return;
		}

		String outfit = ceOutfit.getData().getPattern();
		List<String> skinList = ceOutfit.getData().getConfigByLevelData().getSkinListByCustomType(customType);
		if (skinList == null || skinList.isEmpty()) {
			return;
		}

		if (customType.equals("WINGS")) {
			int index = playerEquipment.getSkinIndex(outfit, customType);
			if (index + 1 >= skinList.size()) {
				playerEquipment.setSkinIndex(outfit, customType, -1);
			}else {
				playerEquipment.setSkinIndex(outfit, customType, index + 1);
			}
		}else {
			playerEquipment.setSkinIndex(outfit, customType, (playerEquipment.getSkinIndex(outfit, customType) + 1) % skinList.size());
		}

		updateMenuWithPreventAction();
	}

	public void returnItems() {
	}

	public int getEmptyExtraSlotIndex(CEWeaponAbstract ceWeaponAbstract) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
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
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		int maxArtifactUseCount = getMaxExtraSlotUseCount(ceWeaponAbstract);

		ExtraSlotSettingsData data = CustomEnchantment.instance().getMainConfig().getExtraSlotSettings(ceWeaponAbstract);

		for (int i = 0; i < maxArtifactUseCount; i++) {
			CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(data.getSlot(i));
			if (weaponAbstract instanceof CEArtifact) {
				CEArtifact artifact = (CEArtifact) weaponAbstract;
				if (artifact.getData().getPattern().equals(ceWeaponAbstract.getData().getPattern())) {
					return true;
				}
			}
			if (weaponAbstract instanceof CESigil) {
				CESigil sigil = (CESigil) weaponAbstract;
				if (sigil.getData().getId().equals(ceWeaponAbstract.getData().getPattern())) {
					return true;
				}
			}

			if (weaponAbstract instanceof CEOutfit) {
				CEOutfit outfit = (CEOutfit) weaponAbstract;
				if (outfit.getData().getId().equals(ceWeaponAbstract.getData().getPattern())) {
					return true;
				}
			}
		}

		return false;
	}
}
