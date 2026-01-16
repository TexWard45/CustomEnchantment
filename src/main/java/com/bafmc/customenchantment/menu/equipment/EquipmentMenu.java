package com.bafmc.customenchantment.menu.equipment;

import com.bafmc.bukkit.bafframework.event.ItemEquipEvent;
import com.bafmc.bukkit.bafframework.utils.MaterialUtils;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.config.data.ExtraSlotSettingsData;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.CEWeaponType;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.item.outfit.CEOutfit;
import com.bafmc.customenchantment.item.protectdead.CEProtectDead;
import com.bafmc.customenchantment.item.sigil.CESigil;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EquipmentMenu extends MenuAbstract {
	public static final String MENU_NAME = "equipment";
	public static final String PROTECT_DEAD_SLOT = "protect-dead";
	public static final String EXTRA_SLOT = "extra-slot";
	public static final String PLAYER_INFO_SLOT = "player-info";
	public static final String WINGS_SLOT = "wings";
	public static final String WINGS_OFF_SLOT = "wings-no-skin";
	public static final String WINGS_TYPE = "WINGS";
	public static final String HELMET_SLOT = "helmet";
	public static final String CHESTPLATE_SLOT = "chestplate";
	public static final String LEGGINGS_SLOT = "leggings";
	public static final String BOOTS_SLOT = "boots";
	public static final String MAINHAND_SLOT = "mainhand";
	public static final String OFFHAND_SLOT = "offhand";
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

	private Map<EquipSlot, Long> lastClickTime = new HashMap<>();
	public void addLastClickTime(EquipSlot equipSlot, long time) {
		lastClickTime.put(equipSlot, time);
	}

	public boolean isInLastClickCooldown(EquipSlot equipSlot) {
		if (!lastClickTime.containsKey(equipSlot)) {
			return false;
		}
		long lastTime = lastClickTime.get(equipSlot);
		return System.currentTimeMillis() - lastTime < 500;
	}

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
			addLastClickTime(EquipSlot.EXTRA_SLOT, System.currentTimeMillis());
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
			}else if (MaterialUtils.isSimilar(type, "CHESTPLATE") || "CHESTPLATE".equalsIgnoreCase(weaponType)) {
				equipSlot = EquipSlot.CHESTPLATE;
				equipmentSlot = EquipmentSlot.CHEST;
			}else if (MaterialUtils.isSimilar(type, "LEGGINGS") || "LEGGINGS".equalsIgnoreCase(weaponType)) {
				equipSlot = EquipSlot.LEGGINGS;
				equipmentSlot = EquipmentSlot.LEGS;
			}else if (MaterialUtils.isSimilar(type, "BOOTS") || "BOOTS".equalsIgnoreCase(weaponType)) {
				equipSlot = EquipSlot.BOOTS;
				equipmentSlot = EquipmentSlot.FEET;
			}

			if (equipSlot != null) {
				ItemStack oldItemStack = equipSlot.getItemStack(player);
				if (oldItemStack.getAmount() > 1) {
					return EquipmentAddReason.NOTHING;
				}
				player.getInventory().setItem(equipmentSlot, itemStack);
				this.updateMenuWithPreventAction();
				e.setCurrentItem(oldItemStack);
				addLastClickTime(equipSlot, System.currentTimeMillis());
				return EquipmentAddReason.SUCCESS;
			}else {
				// If vanilla item, then hand or offhand
				if (player.getInventory().getItem(EquipmentSlot.HAND).getType() == Material.AIR) {
					player.getInventory().setItem(EquipmentSlot.HAND, itemStack);
					this.updateMenuWithPreventAction();
					e.setCurrentItem(null);
					addLastClickTime(EquipSlot.MAINHAND, System.currentTimeMillis());
					return EquipmentAddReason.SUCCESS;
				}else {
					CEPlayer cePlayer = CEAPI.getCEPlayer(player);
					PlayerEquipment playerEquipment = cePlayer.getEquipment();
					if (!playerEquipment.hasWings()) {
						e.setCurrentItem(EquipSlot.OFFHAND.getItemStack(player));
						player.getInventory().setItem(EquipmentSlot.OFF_HAND, itemStack);
					}else {
						ItemEquipEvent itemEquipEvent = new ItemEquipEvent(player, EquipSlot.OFFHAND, playerEquipment.getOffhandItemStack(), itemStack);
						Bukkit.getPluginManager().callEvent(itemEquipEvent);

						e.setCurrentItem(playerEquipment.getOffhandItemStack());
						playerEquipment.setOffhandItemStack(itemStack);
					}
					this.updateMenuWithPreventAction();
					addLastClickTime(EquipSlot.OFFHAND, System.currentTimeMillis());
					return EquipmentAddReason.SUCCESS;
				}
			}
		}
	}

	public void updateMenuWithPreventAction() {
		inUpdateMenu = true;
		Bukkit.getScheduler().runTask(CustomEnchantment.instance(), () -> {
			updateMenu();
			inUpdateMenu = false;
		});
	}

	public void updateMenu() {
		updateSlotsAdvance(HELMET_SLOT, EquipSlot.HELMET.getItemStack(player), null);
		updateSlotsAdvance(CHESTPLATE_SLOT, EquipSlot.CHESTPLATE.getItemStack(player), null);
		updateSlotsAdvance(LEGGINGS_SLOT, EquipSlot.LEGGINGS.getItemStack(player), null);
		updateSlotsAdvance(BOOTS_SLOT, EquipSlot.BOOTS.getItemStack(player), null);
		updateSlotsAdvance(MAINHAND_SLOT, EquipSlot.MAINHAND.getItemStack(player), null);
		updateOffhandSlots();
		updateProtectDeadSlots();
		updateExtraSlots();
		updatePlayerInfoSlots();
		updateWingsSlots();
	}

	public void updateSlotsAdvance(String itemName, ItemStack itemStack, List<Integer> specificSlots) {
		if (itemStack == null || itemStack.getType() == Material.AIR) {
			updateSlots(itemName, null, specificSlots);
			return;
		}

		NextSwapSkinStatus status = getSwapSkinIndex(itemName);

		if (status == NextSwapSkinStatus.CAN_SWAP) {
			ItemStack swapItemStack = getItemStack(player, itemName + "-swap");

			if (swapItemStack != null) {
				ItemMeta itemMeta = swapItemStack.getItemMeta();
				String display = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : "";
				List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();

				String itemDisplay = ItemStackUtils.getDisplayName(itemStack);
				List<String> itemLore =  ItemStackUtils.getLore(itemStack);

				PlaceholderBuilder placeholder = PlaceholderBuilder.builder();
				placeholder.put("{item_display}", itemDisplay != null ? itemDisplay : MaterialUtils.getDisplayName(itemStack.getType()));
				placeholder.put("{item_lore}", itemLore != null ? itemLore : Arrays.asList("__LORE_REMOVER__"));

				display = placeholder.build().apply(display);
				lore = placeholder.build().apply(lore);
				lore.removeIf(line -> line.contains("__LORE_REMOVER__"));

				itemStack = itemStack.clone();
				ItemMeta newItemMeta = itemStack.getItemMeta();

				newItemMeta.setDisplayName(display);
				newItemMeta.setLore(lore);
				itemStack.setItemMeta(newItemMeta);
			}
		}else if (status == NextSwapSkinStatus.SKIN_OFF) {
			ItemStack noSkinItemStack = getItemStack(player, itemName + "-no-skin");

			if (noSkinItemStack != null) {
				itemStack = noSkinItemStack;
			}
		}else {
			ItemStack equipItemStack = getItemStack(player, itemName + "-equip");

			if (equipItemStack != null) {
				ItemMeta itemMeta = equipItemStack.getItemMeta();
				String display = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : "";
				List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();

				String itemDisplay = ItemStackUtils.getDisplayName(itemStack);
				List<String> itemLore =  ItemStackUtils.getLore(itemStack);

				PlaceholderBuilder placeholder = PlaceholderBuilder.builder();
				placeholder.put("{item_display}", itemDisplay != null ? itemDisplay : MaterialUtils.getDisplayName(itemStack.getType()));
				placeholder.put("{item_lore}", itemLore != null ? itemLore : Arrays.asList("__LORE_REMOVER__"));

				display = placeholder.build().apply(display);
				lore = placeholder.build().apply(lore);
				lore.removeIf(line -> line.contains("__LORE_REMOVER__"));

				itemStack = itemStack.clone();
				ItemMeta newItemMeta = itemStack.getItemMeta();
				newItemMeta.setDisplayName(display);
				newItemMeta.setLore(lore);
				itemStack.setItemMeta(newItemMeta);
			}
		}

		updateSlots(itemName, itemStack, specificSlots);
	}

	public enum NextSwapSkinStatus {
		NO_SKIN, SKIN_OFF, CAN_SWAP;
	}

	public NextSwapSkinStatus getSwapSkinIndex(String itemName) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		CEOutfit ceOutfit = playerEquipment.getCEOutfit();
		if (ceOutfit == null) {
			return NextSwapSkinStatus.NO_SKIN;
		}

		String customType;
		if (itemName.equals(WINGS_SLOT)) {
			customType = WINGS_TYPE;
		} else {
			EquipSlot equipSlot = EQUIP_SLOT_MAP.get(itemName);
			if (equipSlot == null) {
				return NextSwapSkinStatus.NO_SKIN;
			}
			CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(equipSlot);
			// Try to get from player equipment if not found in slot
			if (weaponAbstract == null) {
				ItemStack itemStack = equipSlot.getItemStack(player);
				CEItem ceItem = CEAPI.getCEItem(itemStack);
				if (ceItem instanceof CEWeaponAbstract ceWeaponAbstract) {
					weaponAbstract = ceWeaponAbstract;
				}
			}

			customType = weaponAbstract != null ? weaponAbstract.getWeaponTypeName() : null;
		}

		if (customType == null) {
			return NextSwapSkinStatus.NO_SKIN;
		}

		String outfit = ceOutfit.getData().getPattern();
		List<String> skinList = ceOutfit.getData().getConfigByLevelData().getSkinListByCustomType(customType);
		if (skinList == null || skinList.isEmpty()) {
			return NextSwapSkinStatus.NO_SKIN;
		}

		int currentIndex = playerEquipment.getSkinIndex(outfit, customType);
		if (currentIndex == -1) {
			return NextSwapSkinStatus.SKIN_OFF;
		}

		int newIndex = customType.equals(WINGS_TYPE) && currentIndex + 1 >= skinList.size()
				? -1
				: (currentIndex + 1) % skinList.size();

		if (currentIndex == newIndex) {
			return NextSwapSkinStatus.NO_SKIN;
		}

		return NextSwapSkinStatus.CAN_SWAP;
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
		updateTemporaryItem(slots, getItemStack(player, PLAYER_INFO_SLOT));
	}

	public void updateOffhandSlots() {
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		PlayerEquipment playerEquipment = cePlayer.getEquipment();

		ItemStack offhandItemStack = null;
		if (playerEquipment.hasWings()) {
			offhandItemStack = playerEquipment.getOffhandItemStack();
		}else {
			offhandItemStack = EquipSlot.OFFHAND.getItemStack(player);
		}

		updateSlotsAdvance(OFFHAND_SLOT, offhandItemStack, null);
	}

	public void updateWingsSlots() {
		List<Integer> slots = getSlots(WINGS_SLOT);
		if (slots.isEmpty()) {
			return;
		}

		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		CEOutfit outfit = playerEquipment.getCEOutfit();

		ItemStack itemStack = outfit != null ? getWingsItemStack(outfit, playerEquipment) : null;
		updateSlotsAdvance(WINGS_SLOT, itemStack, null);
	}

	private ItemStack getWingsItemStack(CEOutfit outfit, PlayerEquipment playerEquipment) {
		int skinIndex = playerEquipment.getSkinIndex(outfit.getData().getPattern(), WINGS_TYPE);
		if (skinIndex == -1) {
			return getItemStack(player, WINGS_OFF_SLOT);
		}

		String skinName = outfit.getData().getConfigByLevelData().getSkinByIndex(WINGS_TYPE, skinIndex);
		return (skinName != null && !skinName.isEmpty()) ? getSkinItemStack(skinName) : null;
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
		updateTemporaryItem(slots, itemStack);
	}

	public void updateExtraSlots() {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		playerEquipment.sortExtraSlot();

		Map<String, ExtraSlotSettingsData> map = CustomEnchantment.instance().getMainConfig().getExtraSlotSettingMap();

		for (String key : map.keySet()) {
			ExtraSlotSettingsData data = map.get(key);

			int maxArtifactUseCount = data.getMaxCount();
			List<Integer> slots = getSlots(EXTRA_SLOT + "-" + key);

			int limit = Math.min(maxArtifactUseCount, slots.size());
			for (int i = 0; i < limit; i++) {
				CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(data.getSlot(i));
				ItemStack itemStack = (weaponAbstract instanceof CEArtifact || weaponAbstract instanceof CESigil || weaponAbstract instanceof CEOutfit)
						? weaponAbstract.getDefaultItemStack()
						: null;
				updateSlotsAdvance(EXTRA_SLOT + "-" + key, itemStack, List.of(slots.get(i)));
			}
		}
	}

	public int getMaxExtraSlotUseCount(CEWeaponAbstract ceItem) {
		return CustomEnchantment.instance().getMainConfig().getExtraSlotSettings(ceItem).getMaxCount();
	}

	private static final Map<String, EquipSlot> EQUIP_SLOT_MAP = Map.of(
			EquipSlot.HELMET.name().toLowerCase(), EquipSlot.HELMET,
			EquipSlot.CHESTPLATE.name().toLowerCase(), EquipSlot.CHESTPLATE,
			EquipSlot.LEGGINGS.name().toLowerCase(), EquipSlot.LEGGINGS,
			EquipSlot.BOOTS.name().toLowerCase(), EquipSlot.BOOTS,
			EquipSlot.MAINHAND.name().toLowerCase(), EquipSlot.MAINHAND
	);

	private static final Map<EquipSlot, EquipmentSlot> EQUIPMENT_SLOT_MAP = Map.of(
			EquipSlot.HELMET, EquipmentSlot.HEAD,
			EquipSlot.CHESTPLATE, EquipmentSlot.CHEST,
			EquipSlot.LEGGINGS, EquipmentSlot.LEGS,
			EquipSlot.BOOTS, EquipmentSlot.FEET,
			EquipSlot.MAINHAND, EquipmentSlot.HAND
	);

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
			if (isInLastClickCooldown(EquipSlot.EXTRA_SLOT)) {
				return;
			}

			Map<String, ExtraSlotSettingsData> map = CustomEnchantment.instance().getMainConfig().getExtraSlotSettingMap();
			ExtraSlotSettingsData data = map.entrySet().stream()
					.filter(entry -> itemName.equals(EquipmentMenu.EXTRA_SLOT + "-" + entry.getKey()))
					.map(Map.Entry::getValue)
					.findFirst()
					.orElse(null);

			if (data != null) {
				List<Integer> extraSlots = getSlots(itemName);
				int index = extraSlots.indexOf(slot);
				if (index != -1) {
					CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(data.getSlot(index));
					if (weaponAbstract != null) {
						playerEquipment.setSlot(data.getSlot(index), null, true);
						InventoryUtils.addItem(player, weaponAbstract.getDefaultItemStack());
						updateMenuWithPreventAction();
					}
				}
			}
		} else if (itemName.equals(EquipmentMenu.PROTECT_DEAD_SLOT)) {
			if (getSlots(PROTECT_DEAD_SLOT).contains(slot)) {
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
		} else if (itemName.equals(EquipSlot.OFFHAND.name().toLowerCase())) {
			if (playerEquipment.hasWings() && !playerEquipment.hasOffhandItemStack()) {
				return;
			}
			ItemStack itemStack = playerEquipment.getActualOffhandItemStack();
			if (itemStack != null) {
				if (isInLastClickCooldown(EquipSlot.OFFHAND)) {
					return;
				}

				if (!playerEquipment.hasWings()) {
					player.getInventory().setItem(EquipmentSlot.OFF_HAND, null);
				} else {
					ItemEquipEvent itemEquipEvent = new ItemEquipEvent(player, EquipSlot.OFFHAND, itemStack, null);
					Bukkit.getPluginManager().callEvent(itemEquipEvent);
					playerEquipment.setOffhandItemStack(null);
				}
				InventoryUtils.addItem(player, itemStack);
				updateMenuWithPreventAction();
			}
		} else {
			EquipSlot equipSlot = EQUIP_SLOT_MAP.get(itemName);
			if (equipSlot != null) {
				ItemStack itemStack = equipSlot.getItemStack(player);
				if (itemStack != null) {
					if (isInLastClickCooldown(equipSlot)) {
						return;
					}

					// Find first empty slot excluding equipment slots
					int firstEmpty = -1;
					for (int i = 0; i < 36; i++) { // Only check storage slots (0-35)
						if (player.getInventory().getItem(i) == null) {
							firstEmpty = i;
							break;
						}
					}

					if (firstEmpty != -1) {
						player.getInventory().setItem(EQUIPMENT_SLOT_MAP.get(equipSlot), null);
						player.getInventory().setItem(firstEmpty, itemStack);
					}

					updateMenuWithPreventAction();
				}
			}
		}
	}

	public void swapSkin(String itemName, int slot) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		CEOutfit ceOutfit = playerEquipment.getCEOutfit();
		if (ceOutfit == null) {
			return;
		}

		String customType;
		if (itemName.equals(WINGS_SLOT)) {
			customType = WINGS_TYPE;
		} else {
			EquipSlot equipSlot = EQUIP_SLOT_MAP.get(itemName);
			if (equipSlot == null) {
				return;
			}
			CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(equipSlot);
			customType = weaponAbstract != null ? weaponAbstract.getWeaponTypeName() : null;
		}

		String outfit = ceOutfit.getData().getPattern();
		List<String> skinList = ceOutfit.getData().getConfigByLevelData().getSkinListByCustomType(customType);
		if (skinList == null || skinList.isEmpty()) {
			return;
		}

		int currentIndex = playerEquipment.getSkinIndex(outfit, customType);
		int newIndex = customType.equals(WINGS_TYPE) && currentIndex + 1 >= skinList.size()
				? -1
				: (currentIndex + 1) % skinList.size();

		playerEquipment.setSkinIndex(outfit, customType, newIndex);
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

	public void updateTemporaryItem(List<Integer> slots, ItemStack itemStack) {
		if (itemStack == null) {
			slots.forEach(menuView::removeTemporaryItem);
		} else {
			slots.forEach(slot -> menuView.setTemporaryItem(slot, itemStack));
		}
	}
}
