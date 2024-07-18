package me.texward.customenchantment.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.item.CEWeaponAbstract;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.texwardlib.util.InventoryUtils;

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

		List<ItemStack> keepItems = new ArrayList<ItemStack>();
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

		if (!keepItems.isEmpty()) {
			cePlayer.getStorage().getConfig().setItemStackList("save-items-on-death", keepItems);
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
