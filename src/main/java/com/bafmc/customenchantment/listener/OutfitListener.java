package com.bafmc.customenchantment.listener;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItemOptimizeLoader;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OutfitListener implements Listener {
	private static final int OFFHAND_SLOT = 40;
	private CustomEnchantment plugin;

	public OutfitListener(CustomEnchantment plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory clickInventory = e.getClickedInventory();
		if (clickInventory == null || clickInventory.getType() == InventoryType.CRAFTING) {
			return;
		}

		Player player = (Player) e.getWhoClicked();
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		if (cePlayer.getEquipment().hasWings() && EquipSlot.isMatchingEquipSlot(e.getSlot(), EquipSlot.OFFHAND)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
		Item item = e.getItemDrop();

		CEItemOptimizeLoader loader = new CEItemOptimizeLoader(item.getItemStack());
		if (loader.isCESkin()) {
			ItemStack itemStack = loader.getWeaponItemStack();
			if (itemStack != null) {
				item.setItemStack(itemStack);
			}
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(BlockDropItemEvent e) {
		for (Item item : e.getItems()) {
			CEItemOptimizeLoader loader = new CEItemOptimizeLoader(item.getItemStack());
			if (loader.isCESkin()) {
				ItemStack itemStack = loader.getWeaponItemStack();
				if (itemStack != null) {
					item.setItemStack(itemStack);
				}
			}
		}
	}
}
