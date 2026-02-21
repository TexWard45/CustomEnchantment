package com.bafmc.customenchantment.listener;

import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerStorage;
import com.bafmc.customenchantment.utils.StorageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CEProtectDeadListener implements Listener {
	private CustomEnchantment plugin;

	public CEProtectDeadListener(CustomEnchantment plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent e) {
		if (e.getKeepInventory()) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(e.getEntity());

		List<ItemStack> keepItems = new ArrayList<>();

		boolean removeAdvancedProtectDead;
		boolean diconnect = false;
		PlayerStorage storage = cePlayer.getStorage();

		// Call custom death event after player disconnects
		if (storage == null || storage.getConfig() == null) {
			diconnect = true;

			storage = new PlayerStorage(cePlayer);
			storage.onJoin();
		}

		removeAdvancedProtectDead = StorageUtils.getProtectDeadAmount(storage) > 0;

		if (removeAdvancedProtectDead) {
			if (!e.getDrops().isEmpty() || e.getDroppedExp() > 0) {
				StorageUtils.useProtectDead(storage);

				int amountLeft = StorageUtils.getProtectDeadAmount(storage);
				PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder().put(CEConstants.ItemPlaceholder.AMOUNT, amountLeft);
				CustomEnchantmentMessage.send(e.getPlayer(), "ce-item.protectdead.use-advanced", placeholderBuilder.build());

				e.getDrops().clear();
			}

			e.setKeepInventory(true);
			e.setKeepLevel(true);
		} else {
			ListIterator<ItemStack> ite = e.getDrops().listIterator();
			while (ite.hasNext()) {
				ItemStack itemStack = ite.next();

				CEWeaponAbstract weapon = CEWeaponAbstract.getCEWeapon(itemStack);
				if (weapon == null) {
					continue;
				}

				int protectDead = weapon.getWeaponData().getExtraProtectDead();

				if (protectDead > 0) {
					weapon.getWeaponData().setExtraProtectDead(protectDead - 1);
					keepItems.add(weapon.exportTo());
					ite.remove();
				}
			}
		}

		if (!keepItems.isEmpty()) {
			storage.getConfig().setItemStackList("save-items-on-death", keepItems);
		}

		if (diconnect) {
			storage.onQuit();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		List<ItemStack> list = cePlayer.getStorage().getConfig().getItemStackList("save-items-on-death");
		InventoryUtils.addItem(player, list);

		cePlayer.getStorage().getConfig().set("save-items-on-death", null);
	}
}
