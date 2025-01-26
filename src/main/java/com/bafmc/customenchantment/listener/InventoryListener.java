package com.bafmc.customenchantment.listener;

import com.bafmc.bukkit.bafframework.utils.EnchantmentUtils;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentLog;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.CEUnifyWeapon.Target;
import com.bafmc.customenchantment.item.nametag.CENameTag;
import com.destroystokyo.paper.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InventoryListener implements Listener {
	public static final List<Integer> exceptSlot = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 45);
	private CustomEnchantment plugin;

	public InventoryListener(CustomEnchantment plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPrepareGrindstone(PrepareGrindstoneEvent e) {
        ItemStack itemStack = e.getResult();
        if (itemStack == null) {
            return;
        }

        CEItem ceItem = CEAPI.getCEItem(itemStack);
        if (ceItem == null) {
            return;
        }

        e.setResult(ceItem.exportTo());
    }

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPrepareSmithing(PrepareSmithingEvent e) {
		ItemStack itemStack = e.getResult();
		if (itemStack == null) {
			return;
		}

		CEItem ceItem = CEAPI.getCEItem(itemStack);
		if (ceItem == null) {
			return;
		}

		if (ceItem instanceof CEWeaponAbstract weapon) {
			CEItemData data = ceItem.getData();
			if (data == null) {
				weapon.getWeaponAttribute().clearAttribute();
			}
		}

		e.setResult(ceItem.exportTo());
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAnvilClick(InventoryClickEvent e) {
		if (e.getInventory().getType() != InventoryType.ANVIL || !(e.getWhoClicked() instanceof Player)) {
			return;
		}
		Player player = (Player) e.getWhoClicked();
		AnvilInventory anvil = (AnvilInventory) e.getInventory();

//		if (!plugin.getColoredAnvilTask().containsAnvilInventory(anvil)) {
//			plugin.getColoredAnvilTask().addAnvilInventory(anvil);
//		}

		ItemStack itemStack0 = anvil.getItem(0);
		ItemStack itemStack1 = anvil.getItem(1);

		CEItem ceItem0 = CEAPI.getCEItem(itemStack0);
		CEItem ceItem1 = CEAPI.getCEItem(itemStack1);

		if (!(ceItem0 instanceof CEWeaponAbstract) || !(ceItem1 instanceof CENameTag)) {
			return;
		}

		CENameTag nametag = (CENameTag) ceItem1;

		ItemStack itemStack2 = anvil.getItem(2);
		CEItem ceItem2 = CEAPI.getCEItem(itemStack2);
		if (ceItem2 instanceof CEUnify) {
			e.setCancelled(true);
			return;
		}

		if (ItemStackUtils.isEmpty(e.getCursor()) && !ItemStackUtils.isEmpty(itemStack2) && e.getRawSlot() == 2) {
			ItemStack resultItem = getColoredItem(nametag, e.getCurrentItem());
			anvil.setItem(0, new ItemStack(Material.AIR));
			anvil.setItem(1, new ItemStack(Material.AIR));
			e.setCursor(resultItem);

			if (ceItem1 instanceof CENameTag) {
				ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
				reason.setWriteLogs(true);
				reason.setPlayer(player);
				reason.setCEItem1(ceItem1);
				reason.setCEItem2(ceItem0);
				reason.putData("pattern", ceItem1.getData().getPattern());

				String oldDisplay = itemStack0.hasItemMeta() ? itemStack0.getItemMeta().getDisplayName() : null;
				String newDisplay = resultItem.hasItemMeta() ? resultItem.getItemMeta().getDisplayName() : null;
				reason.putData("old-display", oldDisplay);
				reason.putData("new-display", newDisplay);
				CustomEnchantmentLog.writeItemActionLogs(reason);

				CustomEnchantmentMessage.send(player,
						"ce-item." + ceItem1.getType() + "." + reason.getReason().toLowerCase(),
						reason.getPlaceholder());
			}

			InventoryUtils.addItem(player,
					Arrays.asList(ItemStackUtils.getItemStack(itemStack1, itemStack1.getAmount() - 1)));
		}
	}

	public static ItemStack getColoredItem(CENameTag nametag, ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}

		if (!itemStack.hasItemMeta()) {
			return itemStack;
		}

		ItemMeta meta = itemStack.getItemMeta();
		if (!meta.hasDisplayName()) {
			return itemStack;
		}
		meta.setDisplayName(nametag.getNewDisplay(meta.getDisplayName()));
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAnvilPrepare(PrepareAnvilEvent e) {
		AnvilInventory anvil = e.getInventory();

		ItemStack itemStack0 = anvil.getItem(0);
		ItemStack itemStack1 = anvil.getItem(1);

		CEItem ceItem0 = CEAPI.getCEItem(itemStack0);
		CEItem ceItem1 = CEAPI.getCEItem(itemStack1);

		// Vanilla Anvil
		if (ceItem0 == null && ceItem1 == null) {
			return;
		}

		// Use nametag on weapon
		boolean useNameTag = ceItem0 instanceof CEWeaponAbstract && ceItem1 instanceof CENameTag;
		if (useNameTag) {
			onNameTagAnvil(e, (CEWeaponAbstract) ceItem0, (CENameTag) ceItem1);
		}

		// Prevent use anvil
		updateEmptyDisplayAndDefaultColor(e, itemStack0, e.getResult(), useNameTag);

		// Update CEItem
		CEItem result = CEAPI.getCEItem(e.getResult());
		if (result instanceof CEUnify) {
			e.setResult(null);
		} else if (result instanceof CEWeaponAbstract) {
			e.setResult(result.exportTo());
		}

		final List<HumanEntity> viewers = e.getViewers();
		new BukkitRunnable() {
			public void run() {
				for (HumanEntity viewer : viewers) {
					((Player) viewer).updateInventory();
				}
			}
		}.runTask(plugin);
	}

	public void updateEmptyDisplayAndDefaultColor(PrepareAnvilEvent e, ItemStack itemStack0, ItemStack result,
			boolean useNameTag) {
		if (itemStack0 == null || result == null) {
			return;
		}
		if (!itemStack0.hasItemMeta() || !itemStack0.getItemMeta().hasDisplayName()) {
			return;
		}
		if (!result.hasItemMeta() || result.getItemMeta().getDisplayName() == null) {
			e.setResult(null);
			return;
		}
		if (!result.getItemMeta().getDisplayName().replace("" + ChatColor.COLOR_CHAR, "")
				.equals(itemStack0.getItemMeta().getDisplayName().replace("" + ChatColor.COLOR_CHAR, ""))) {
			return;
		}

		if (useNameTag) {
			e.setResult(null);
		} else {
			ItemMeta meta = result.getItemMeta();
			meta.setDisplayName(itemStack0.getItemMeta().getDisplayName());
			result.setItemMeta(meta);
		}
	}
	
	public void onNameTagAnvil(PrepareAnvilEvent e, CEWeaponAbstract weapon, CENameTag nametag) {
		AnvilInventory anvil = e.getInventory();

		if (weapon == null || nametag == null) {
			return;
		}

		ItemStack itemStack0 = anvil.getItem(0).clone();
		ItemMeta meta = itemStack0.getItemMeta();

		String oldDisplay = meta.getDisplayName();
		String renameText = anvil.getRenameText();
		
		if (renameText.equals("%nametag%")) {
			Player player = (Player) e.getInventory().getViewers().get(0);
			renameText = CEAPI.getCEPlayer(player).getNameTag().getDisplay();
			renameText = renameText == null ? "%nametag%" : renameText;
		}
		
		String newDisplay = nametag.getNewDisplay(renameText);

		if (oldDisplay == null && newDisplay == null) {
			return;
		}

		if (newDisplay.isEmpty() || newDisplay.equals(oldDisplay)) {
			return;
		}
		
		meta.setDisplayName(newDisplay);
		itemStack0.setItemMeta(meta);

		e.setResult(itemStack0);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent e) {
		int rawSlot = e.getRawSlot();
		ClickType clickType = e.getClick();
		InventoryAction inventoryAction = e.getAction();
		ItemStack cursor = e.getCursor();

		if (!(clickType == ClickType.LEFT) && inventoryAction == InventoryAction.SWAP_WITH_CURSOR && rawSlot >= 0
				&& !exceptSlot.contains(rawSlot)) {
			onCEItemInventory(e);
		} else if ((clickType == ClickType.MIDDLE || clickType == ClickType.SHIFT_RIGHT) && cursor.getType() == Material.AIR) {
			onMaskInventory(e);
		}
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public void onCEItemInventory(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();

		ItemStack clickItem = e.getWhoClicked().getInventory().getItem(slot);
		ItemStack cursorItem = e.getCursor();

		if (clickItem == null) {
			return;
		}

		if (clickItem.getAmount() > 1) {
			e.setCancelled(true);
			return;
		}

		if (cursorItem == null) {
			return;
		}

		boolean onFastEnchant = onFastEnchant(e);
		if (onFastEnchant) {
			return;
		}

		CEItem clickType = CEAPI.getCEItem(clickItem);
		if (clickType == null) {
			return;
		}

		CEItem cursorType = CEAPI.getCEItem(cursorItem);
		if (cursorType == null) {
			return;
		}

		ApplyReason reason = cursorType.applyTo(clickType);
		if (reason.isChangeSource()) {
			clickType = reason.getSource();
		}

		switch (reason.getResult()) {
		case FAIL:
			e.setCancelled(true);
			e.setCursor(ItemStackUtils.getItemStack(cursorItem, cursorItem.getAmount() - 1));
			break;
		case FAIL_AND_UPDATE:
			e.setCancelled(true);
			e.setCursor(ItemStackUtils.getItemStack(cursorItem, cursorItem.getAmount() - 1));
			e.setCurrentItem(clickType.exportTo());
			break;
		case DESTROY:
			e.setCancelled(true);
			e.setCurrentItem(null);
			e.setCursor(ItemStackUtils.getItemStack(cursorItem, cursorItem.getAmount() - 1));
			break;
		case NOTHING:
			break;
		case CANCEL:
			e.setCancelled(true);
			break;
		case SUCCESS:
			e.setCancelled(true);
			e.setCursor(ItemStackUtils.getItemStack(cursorItem, cursorItem.getAmount() - 1));
			e.setCurrentItem(clickType.exportTo());
			break;
		default:
			break;
		}

		if (reason.isWriteLogs()) {
			reason.setPlayer(player);
			reason.setCEItem1(cursorType);
			reason.setCEItem2(clickType);
			CustomEnchantmentLog.writeItemActionLogs(reason);
		}

		if (reason.getRewards() != null) {
			InventoryUtils.addItem(player, reason.getRewards());
		}

		CustomEnchantmentMessage.send(player,
				"ce-item." + cursorType.getType() + "." + reason.getReason().toLowerCase(), reason.getPlaceholder());
	}

	public boolean onFastEnchant(InventoryClickEvent e) {
		int slot = e.getSlot();
		ItemStack clickItem = e.getWhoClicked().getInventory().getItem(slot);
		ItemStack cursorItem = e.getCursor();

		if (cursorItem.getType() != Material.ENCHANTED_BOOK) {
			return false;
		}

		Map<Enchantment, Integer> map = EnchantmentUtils.getNewEnchantMap(clickItem, cursorItem);
		if (map.isEmpty()) {
			return false;
		}
		clickItem.addUnsafeEnchantments(map);

		e.setCursor(ItemStackUtils.getItemStack(cursorItem, cursorItem.getAmount() - 1));
		e.setCancelled(true);

		CEItem result = CEAPI.getCEItem(clickItem);
		if (result instanceof CEWeaponAbstract) {
			e.setCurrentItem(result.exportTo());
		}
		return true;
	}

	public void onMaskInventory(InventoryClickEvent e) {
		ItemStack itemStack = e.getCurrentItem();
		if (itemStack != null && itemStack.getAmount() > 1) {
			return;
		}

		CEItem ceItem = CEAPI.getCEItem(itemStack);
		if (!(ceItem instanceof CEUnify)) {
			return;
		}

		CEUnify mask = (CEUnify) ceItem;
		if (!mask.getUnifyWeapon().isSet()) {
			return;
		}

		Player player = (Player) e.getWhoClicked();
		ItemStack maskItemStack = mask.getUnifyWeapon().getItemStack(Target.UNIFY);
		ItemStack weaponItemStack = mask.getUnifyWeapon().getItemStack(Target.WEAPON);

		e.setCurrentItem(maskItemStack);
		e.setCursor(weaponItemStack);
		e.setCancelled(true);

		new BukkitRunnable() {
			public void run() {
				player.updateInventory();
			}
		}.runTask(plugin);
	}
}
