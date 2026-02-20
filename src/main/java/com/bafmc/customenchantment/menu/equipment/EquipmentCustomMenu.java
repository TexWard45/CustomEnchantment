package com.bafmc.customenchantment.menu.equipment;

import com.bafmc.bukkit.bafframework.custommenu.menu.AbstractMenu;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ItemData;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.list.DefaultItem;
import com.bafmc.bukkit.bafframework.utils.MaterialUtils;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.ItemStackUtils;
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
import com.bafmc.customenchantment.menu.equipment.handler.ArmorWeaponHandler;
import com.bafmc.customenchantment.menu.equipment.handler.EquipmentSlotHandler;
import com.bafmc.customenchantment.menu.equipment.handler.ExtraSlotHandler;
import com.bafmc.customenchantment.menu.equipment.handler.ProtectDeadHandler;
import com.bafmc.customenchantment.menu.equipment.item.EquipmentSlotItem;
import com.bafmc.customenchantment.menu.equipment.item.ExtraSlotEquipmentItem;
import com.bafmc.customenchantment.menu.equipment.item.PlayerInfoEquipmentItem;
import com.bafmc.customenchantment.menu.equipment.item.ProtectDeadEquipmentItem;
import com.bafmc.customenchantment.menu.equipment.item.WingsEquipmentItem;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerEquipment;
import com.bafmc.customenchantment.player.PlayerStorage;
import com.bafmc.customenchantment.utils.StorageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EquipmentCustomMenu extends AbstractMenu<MenuData, EquipmentExtraData> {

	public enum EquipmentAddReason {
		SUCCESS, NOT_SUPPORT_ITEM, UNDRESS_FIRST,
		ADD_EXTRA_SLOT, MAX_EXTRA_SLOT, DUPLICATE_EXTRA_SLOT, NOTHING, NO_EXTRA_SLOT,
		ADD_PROTECT_DEAD, EXCEED_PROTECT_DEAD, DIFFERENT_PROTECT_DEAD
	}

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

	private static final Map<String, EquipmentCustomMenu> menuMap = new ConcurrentHashMap<>();
	private static final Map<String, Long> swapSkinCooldowns = new ConcurrentHashMap<>();
	private static final long SWAP_SKIN_COOLDOWN_MS = 1000;

	public static final Map<String, EquipSlot> EQUIP_SLOT_MAP = Map.of(
			EquipSlot.HELMET.name().toLowerCase(), EquipSlot.HELMET,
			EquipSlot.CHESTPLATE.name().toLowerCase(), EquipSlot.CHESTPLATE,
			EquipSlot.LEGGINGS.name().toLowerCase(), EquipSlot.LEGGINGS,
			EquipSlot.BOOTS.name().toLowerCase(), EquipSlot.BOOTS,
			EquipSlot.MAINHAND.name().toLowerCase(), EquipSlot.MAINHAND
	);

	private BukkitTask updateTask;
	private List<EquipmentSlotHandler> handlers;

	private final List<String> itemInteractList = Arrays.asList(
			PROTECT_DEAD_SLOT,
			EquipSlot.HELMET.name().toLowerCase(),
			EquipSlot.CHESTPLATE.name().toLowerCase(),
			EquipSlot.LEGGINGS.name().toLowerCase(),
			EquipSlot.BOOTS.name().toLowerCase(),
			EquipSlot.OFFHAND.name().toLowerCase(),
			EquipSlot.MAINHAND.name().toLowerCase()
	);

	// ==================== Accessors ====================

	public Player getOwner() {
		return owner;
	}

	// ==================== Static Singleton ====================

	public static EquipmentCustomMenu putMenu(Player player, EquipmentCustomMenu menu) {
		EquipmentCustomMenu old = menuMap.put(player.getName(), menu);
		if (old != null) {
			old.extraData.setRemoved(true);
		}
		return menu;
	}

	public static EquipmentCustomMenu getMenu(Player player) {
		return menuMap.get(player.getName());
	}

	public static EquipmentCustomMenu removeMenu(Player player) {
		swapSkinCooldowns.remove(player.getName());
		EquipmentCustomMenu menu = menuMap.remove(player.getName());
		if (menu != null) {
			menu.extraData.setRemoved(true);
		}
		return menu;
	}

	public static void clearAll() {
		menuMap.values().forEach(menu -> menu.extraData.setRemoved(true));
		menuMap.clear();
		swapSkinCooldowns.clear();
	}

	// ==================== Menu Lifecycle ====================

	@Override
	public String getType() {
		return MENU_NAME;
	}

	@Override
	public void registerItems() {
		registerItem(DefaultItem.class);
		registerItem(EquipmentSlotItem.class);
		registerItem(ExtraSlotEquipmentItem.class);
		registerItem(ProtectDeadEquipmentItem.class);
		registerItem(WingsEquipmentItem.class);
		registerItem(PlayerInfoEquipmentItem.class);
	}

	@Override
	public void setupItems() {
		super.setupItems();
		handlers = List.of(
				new ExtraSlotHandler(this),
				new ProtectDeadHandler(this),
				new ArmorWeaponHandler(this)
		);
		putMenu(owner, this);
		updateMenu();
		autoUpdateMenu();
	}

	@Override
	public void handlePlayerInventoryClick(ClickData data) {
		if (extraData.isInUpdateMenu()) {
			data.getEvent().setCancelled(true);
			return;
		}

		ItemStack clickedItem = data.getEvent().getCurrentItem();
		if (clickedItem == null || clickedItem.getType().isAir()) {
			return;
		}

		CEItem ceItem = CEAPI.getCEItem(clickedItem);
		EquipmentAddReason reason = addItem(data.getEvent(), clickedItem, ceItem);
		CustomEnchantmentMessage.send(data.getPlayer(), "menu.equipment.add-equipment." + EnumUtils.toConfigStyle(reason));
	}

	@Override
	public void handleClose() {
		if (updateTask != null) {
			updateTask.cancel();
		}
		removeMenu(owner);
	}

	// ==================== State Check ====================

	public boolean isInUpdateMenu() {
		return extraData.isInUpdateMenu();
	}

	// ==================== Add Item (handler delegation) ====================

	public EquipmentAddReason addItem(InventoryClickEvent e, ItemStack itemStack, CEItem ceItem) {
		for (EquipmentSlotHandler handler : handlers) {
			if (handler.canAdd(itemStack, ceItem)) {
				return handler.add(e, itemStack, ceItem);
			}
		}
		return EquipmentAddReason.NOT_SUPPORT_ITEM;
	}

	// ==================== Return Item (handler delegation) ====================

	public void returnItem(String itemName, int slot) {
		if (itemInteractList.contains(itemName) || itemName.startsWith(EXTRA_SLOT)) {
			if (owner.getInventory().firstEmpty() == -1) {
				CustomEnchantmentMessage.send(owner, "menu.equipment.return-item.no-empty-slot");
				return;
			}
		}

		for (EquipmentSlotHandler handler : handlers) {
			if (handler.canReturn(itemName)) {
				handler.returnItem(itemName, slot);
				return;
			}
		}
	}

	// ==================== Swap Skin ====================

	public void swapSkinWithCooldown(String itemName, int slot, Player player) {
		long currentTime = System.currentTimeMillis();
		Long lastTime = swapSkinCooldowns.get(player.getName());
		if (lastTime != null && (currentTime - lastTime) < SWAP_SKIN_COOLDOWN_MS) {
			return;
		}
		swapSkinCooldowns.put(player.getName(), currentTime);
		swapSkin(itemName, slot);
	}

	public void swapSkin(String itemName, int slot) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(owner).getEquipment();
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

	// ==================== Menu Update ====================

	public void updateMenuWithPreventAction() {
		extraData.setInUpdateMenu(true);
		Bukkit.getScheduler().runTask(CustomEnchantment.instance(), () -> {
			updateMenu();
			extraData.setInUpdateMenu(false);
		});
	}

	public void updateMenu() {
		updateSlotsAdvance(HELMET_SLOT, EquipSlot.HELMET.getItemStack(owner));
		updateSlotsAdvance(CHESTPLATE_SLOT, EquipSlot.CHESTPLATE.getItemStack(owner));
		updateSlotsAdvance(LEGGINGS_SLOT, EquipSlot.LEGGINGS.getItemStack(owner));
		updateSlotsAdvance(BOOTS_SLOT, EquipSlot.BOOTS.getItemStack(owner));
		updateSlotsAdvance(MAINHAND_SLOT, EquipSlot.MAINHAND.getItemStack(owner));
		updateOffhandSlots();
		updateProtectDeadSlots();
		updateExtraSlots();
		updatePlayerInfoSlots();
		updateWingsSlots();
	}

	// ==================== Skin Display Rendering ====================

	public enum NextSwapSkinStatus {
		NO_SKIN, SKIN_OFF, CAN_SWAP
	}

	public void updateSlotsAdvance(String itemName, ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR) {
			updateSlots(itemName, null);
			return;
		}
		updateSlots(itemName, applySkinTemplate(itemName, itemStack));
	}

	public NextSwapSkinStatus getSwapSkinIndex(String itemName) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(owner).getEquipment();
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
			if (weaponAbstract == null) {
				ItemStack itemStack = equipSlot.getItemStack(owner);
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

	private ItemStack applySkinTemplate(String itemName, ItemStack itemStack) {
		NextSwapSkinStatus status = getSwapSkinIndex(itemName);
		if (status == NextSwapSkinStatus.CAN_SWAP) {
			ItemStack swapTemplate = getTemplateItemStackForEquipment(itemName + "-swap");
			if (swapTemplate != null) return applyItemPlaceholders(itemStack, swapTemplate);
		} else if (status == NextSwapSkinStatus.SKIN_OFF) {
			ItemStack noSkinTemplate = getTemplateItemStackForEquipment(itemName + "-no-skin");
			if (noSkinTemplate != null) return noSkinTemplate;
		} else {
			ItemStack equipTemplate = getTemplateItemStackForEquipment(itemName + "-equip");
			if (equipTemplate != null) return applyItemPlaceholders(itemStack, equipTemplate);
		}
		return itemStack;
	}

	private ItemStack getTemplateItemStackForEquipment(String name) {
		ItemData itemData = menuData.getItemMap().get(name);
		return itemData != null ? itemData.getItemStack(owner) : null;
	}

	private ItemStack applyItemPlaceholders(ItemStack sourceItem, ItemStack template) {
		ItemMeta templateMeta = template.getItemMeta();
		String display = templateMeta.hasDisplayName() ? templateMeta.getDisplayName() : "";
		List<String> lore = templateMeta.hasLore() ? templateMeta.getLore() : new ArrayList<>();

		String itemDisplay = ItemStackUtils.getDisplayName(sourceItem);
		List<String> itemLore = ItemStackUtils.getLore(sourceItem);

		PlaceholderBuilder placeholder = PlaceholderBuilder.builder();
		placeholder.put("{item_display}", itemDisplay != null ? itemDisplay : MaterialUtils.getDisplayName(sourceItem.getType()));
		placeholder.put("{item_lore}", itemLore != null ? itemLore : Arrays.asList("__LORE_REMOVER__"));

		display = placeholder.build().apply(display);
		lore = placeholder.build().apply(lore);
		lore.removeIf(line -> line.contains("__LORE_REMOVER__"));

		ItemStack result = sourceItem.clone();
		ItemMeta resultMeta = result.getItemMeta();
		resultMeta.setDisplayName(display);
		resultMeta.setLore(lore);
		result.setItemMeta(resultMeta);
		return result;
	}

	// ==================== Subsystem Updates ====================

	public void updatePlayerInfoSlots() {
		List<Integer> slots = getSlotsByName(PLAYER_INFO_SLOT);
		ItemStack itemStack = getTemplateItemStackForEquipment(PLAYER_INFO_SLOT);
		if (itemStack == null) {
			return;
		}
		for (int slot : slots) {
			inventory.setItem(slot, itemStack);
		}
	}

	public void updateOffhandSlots() {
		CEPlayer cePlayer = CEAPI.getCEPlayer(owner);
		PlayerEquipment playerEquipment = cePlayer.getEquipment();

		ItemStack offhandItemStack;
		if (playerEquipment.hasWings()) {
			offhandItemStack = playerEquipment.getOffhandItemStack();
		} else {
			offhandItemStack = EquipSlot.OFFHAND.getItemStack(owner);
		}

		updateSlotsAdvance(OFFHAND_SLOT, offhandItemStack);
	}

	public void updateWingsSlots() {
		List<Integer> slots = getSlotsByName(WINGS_SLOT);
		if (slots.isEmpty()) {
			return;
		}

		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(owner).getEquipment();
		CEOutfit outfit = playerEquipment.getCEOutfit();

		ItemStack itemStack = outfit != null ? getWingsItemStack(outfit, playerEquipment) : null;
		updateSlotsAdvance(WINGS_SLOT, itemStack);
	}

	private ItemStack getWingsItemStack(CEOutfit outfit, PlayerEquipment playerEquipment) {
		int skinIndex = playerEquipment.getSkinIndex(outfit.getData().getPattern(), WINGS_TYPE);
		if (skinIndex == -1) {
			return getTemplateItemStackForEquipment(WINGS_OFF_SLOT);
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
		PlayerStorage storage = CEAPI.getCEPlayer(owner).getStorage();
		String type = StorageUtils.getProtectDeadType(storage);
		ItemStack itemStack = null;
		if (type != null) {
			int amount = StorageUtils.getProtectDeadAmount(storage);

			CEItem ceItem = CEAPI.getCEItemByStorage(CEItemType.PROTECT_DEAD, type);
			if (ceItem instanceof CEProtectDead protectDead && protectDead.getData().isAdvancedMode()) {
				itemStack = protectDead.getDefaultItemStack().clone();

				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setMaxStackSize(99);
				itemStack.setItemMeta(itemMeta);

				itemStack.setAmount(amount);
			} else {
				StorageUtils.removeProtectDead(storage);
			}
		}

		List<Integer> slots = getSlotsByName(PROTECT_DEAD_SLOT);
		for (int slot : slots) {
			if (itemStack != null) {
				inventory.setItem(slot, itemStack);
			} else {
				inventory.setItem(slot, getTemplateItemStack(PROTECT_DEAD_SLOT));
			}
		}
	}

	public void updateExtraSlots() {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(owner).getEquipment();
		playerEquipment.sortExtraSlot();

		Map<String, ExtraSlotSettingsData> map = CustomEnchantment.instance().getMainConfig().getExtraSlotSettingMap();

		for (String key : map.keySet()) {
			ExtraSlotSettingsData data = map.get(key);
			String slotName = EXTRA_SLOT + "-" + key;
			List<Integer> slots = getSlotsByName(slotName);

			int limit = Math.min(data.getMaxCount(), slots.size());
			for (int i = 0; i < limit; i++) {
				CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(data.getSlot(i));
				ItemStack itemStack = (weaponAbstract instanceof CEArtifact || weaponAbstract instanceof CESigil || weaponAbstract instanceof CEOutfit)
						? weaponAbstract.getDefaultItemStack()
						: null;

				if (itemStack != null) {
					updateSlotsAdvanceForSlot(slotName, itemStack, slots.get(i));
				} else {
					inventory.setItem(slots.get(i), getTemplateItemStack(slotName));
				}
			}
		}
	}

	private void updateSlotsAdvanceForSlot(String itemName, ItemStack itemStack, int slot) {
		if (itemStack == null || itemStack.getType() == Material.AIR) {
			inventory.setItem(slot, getTemplateItemStack(itemName));
			return;
		}
		inventory.setItem(slot, applySkinTemplate(itemName, itemStack));
	}

	// ==================== Auto Update ====================

	private void autoUpdateMenu() {
		updateTask = Bukkit.getScheduler().runTaskTimer(CustomEnchantment.instance(), () -> {
			if (extraData.isRemoved() || !owner.isOnline()) {
				updateTask.cancel();
				return;
			}
			updatePlayerInfoSlots();
		}, 0, 5);
	}
}
