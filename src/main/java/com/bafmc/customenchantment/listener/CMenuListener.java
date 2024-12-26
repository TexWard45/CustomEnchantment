package com.bafmc.customenchantment.listener;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.menu.MenuListenerAbstract;
import com.bafmc.custommenu.api.CustomMenuAPI;
import com.bafmc.custommenu.event.CustomMenuClickEvent;
import com.bafmc.custommenu.event.CustomMenuCloseEvent;
import com.bafmc.custommenu.event.CustomMenuOpenEvent;
import com.bafmc.custommenu.menu.CMenuView;
import com.bafmc.custommenu.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class CMenuListener implements Listener {
	private CustomEnchantment plugin;
	private static List<MenuListenerAbstract> menuListeners = new ArrayList<>();

	public CMenuListener(CustomEnchantment plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public static void registerMenuListener(MenuListenerAbstract listener) {
		menuListeners.add(listener);
	}

	@EventHandler
	public void onMenuOpen(CustomMenuOpenEvent e) {
		String name = e.getCMenu().getName();

		for (MenuListenerAbstract listener : menuListeners) {
			if (listener.getMenuName().equals(name)) {
				listener.onMenuOpen(e);
				break;
			}
		}
	}

	@EventHandler
	public void onMenuClose(CustomMenuCloseEvent e) {
		String name = e.getCMenu().getName();

		for (MenuListenerAbstract listener : menuListeners) {
			if (listener.getMenuName().equals(name)) {
				listener.onMenuClose(e);
				break;
			}
		}
	}

	@EventHandler
	public void onMenuClick(CustomMenuClickEvent e) {
		for (MenuListenerAbstract listener : menuListeners) {
			if (listener.getMenuName().equals(e.getCMenu().getName())) {
				listener.onMenuClick(e);
				break;
			}
		}
	}

	@EventHandler
	public void onMenuClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();

		CPlayer cPlayer = CustomMenuAPI.getCPlayer(player);

		if (!cPlayer.isOpenCustomMenu()) {
			return;
		}

		CMenuView cMenuView = cPlayer.getOpenCustomMenu();
		for (MenuListenerAbstract listener : menuListeners) {
			if (listener.getMenuName().equals(cMenuView.getCMenu().getName())) {
				listener.onInventoryClick(e);
				break;
			}
		}
	}
}
