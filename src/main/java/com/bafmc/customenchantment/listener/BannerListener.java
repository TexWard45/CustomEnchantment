package com.bafmc.customenchantment.listener;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.utils.MaterialUtils;

public class BannerListener implements Listener {
	private CustomEnchantment main;

	public BannerListener(CustomEnchantment main) {
		this.main = main;
		Bukkit.getPluginManager().registerEvents(this, main);
	}

	private List<ClickType> clickTypes = Arrays.asList(ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT);

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory clickInventory = e.getClickedInventory();
		if (clickInventory == null) {
			return;
		}

		InventoryType type = clickInventory.getType();
		if (type != InventoryType.PLAYER) {
			return;
		}

		int rawSlot = e.getRawSlot();
		SlotType slotType = e.getSlotType();
		ClickType clickType = e.getClick();

		if (slotType == SlotType.ARMOR && rawSlot == 5) {
			ItemStack cursor = e.getCursor();
			if (cursor == null) {
				return;
			}

			if (!MaterialUtils.isSimilar(cursor.getType(), "BANNER") && !MaterialUtils.isSimilar(cursor.getType(), "DRAGON_HEAD")) {
				return;
			}

			PlayerInventory playerInventory = (PlayerInventory) clickInventory;
			ItemStack helmet = playerInventory.getHelmet();
			if (helmet != null && cursor.getAmount() == 1) {
				playerInventory.setHelmet(cursor);
				e.setCursor(helmet);
			} else if (helmet == null && (clickType == ClickType.RIGHT || clickType == ClickType.RIGHT)) {
				playerInventory.setHelmet(ItemStackUtils.getItemStack(cursor, 1));
				e.setCursor(ItemStackUtils.getItemStack(cursor, cursor.getAmount() - 1));
			}
			e.setCancelled(true);
			return;
		}

		if (clickTypes.contains(clickType) && slotType == SlotType.ARMOR && rawSlot == 1) {
			ItemStack itemStack = e.getCurrentItem();

			if (itemStack == null) {
				return;
			}
			
			int amount = itemStack.getAmount();

			if (!MaterialUtils.isSimilar(itemStack.getType(), "BANNER") && !MaterialUtils.isSimilar(itemStack.getType(), "DRAGON_HEAD")) {
				return;
			}
			
			Inventory openInventory = ((Player) e.getWhoClicked()).getOpenInventory().getTopInventory();
			if (openInventory != null && openInventory.getType() != InventoryType.CRAFTING) {
				return;
			}

			PlayerInventory playerInventory = (PlayerInventory) clickInventory;
			if (playerInventory.getHelmet() != null) {
				return;
			}
			playerInventory.setHelmet(ItemStackUtils.getItemStack(itemStack, 1));

			ItemStack itemStack2 = ItemStackUtils.getItemStack(itemStack, amount - 1);
			e.setCurrentItem(itemStack2);
			e.setCancelled(true);
			return;
		}
	}
}