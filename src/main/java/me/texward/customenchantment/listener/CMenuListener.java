package me.texward.customenchantment.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.CustomEnchantmentMessage;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.item.CEBook;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.menu.BookcraftMenu;
import me.texward.customenchantment.menu.BookcraftMenu.BookAddReason;
import me.texward.customenchantment.menu.BookcraftMenu.BookcraftConfirmReason;
import me.texward.customenchantment.menu.CEAnvilMenu;
import me.texward.customenchantment.menu.CEAnvilMenu.CEAnvilAddReason;
import me.texward.customenchantment.menu.TinkererMenu;
import me.texward.customenchantment.menu.TinkererMenu.TinkererAddReason;
import me.texward.customenchantment.menu.TinkererMenu.TinkererConfirmReason;
import me.texward.custommenu.api.CustomMenuAPI;
import me.texward.custommenu.event.CustomMenuClickEvent;
import me.texward.custommenu.event.CustomMenuCloseEvent;
import me.texward.custommenu.event.CustomMenuOpenEvent;
import me.texward.custommenu.menu.CMenuView;
import me.texward.custommenu.player.CPlayer;
import me.texward.texwardlib.util.EnumUtils;
import me.texward.texwardlib.util.ItemStackUtils;

public class CMenuListener implements Listener {
	private CustomEnchantment plugin;

	public CMenuListener(CustomEnchantment plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMenuOpen(CustomMenuOpenEvent e) {
		String name = e.getCMenu().getName();

		if (name.equals(BookcraftMenu.MENU_NAME)) {
			BookcraftMenu.putBookCraftMenu(e.getCPlayer().getPlayer(), e.getCMenuView());
		} else if (name.equals(TinkererMenu.MENU_NAME)) {
			TinkererMenu.putTinkererMenu(e.getCPlayer().getPlayer(), e.getCMenuView());
		} else if (name.equals(CEAnvilMenu.MENU_NAME)) {
			CEAnvilMenu.putCEAnvilMenu(e.getCPlayer().getPlayer(), e.getCMenuView());
		}
	}

	@EventHandler
	public void onMenuClose(CustomMenuCloseEvent e) {
		String name = e.getCMenu().getName();

		if (name.equals(BookcraftMenu.MENU_NAME)) {
			BookcraftMenu.removeBookCraftMenu(e.getCPlayer().getPlayer()).returnItems();
		} else if (name.equals(TinkererMenu.MENU_NAME)) {
			TinkererMenu.removeTinkererMenu(e.getCPlayer().getPlayer()).returnItems();
		} else if (name.equals(CEAnvilMenu.MENU_NAME)) {
			CEAnvilMenu.removeCEAnvilMenu(e.getCPlayer().getPlayer()).returnItems();
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
		if (cMenuView.getCMenu().getName().equals(BookcraftMenu.MENU_NAME)) {
			onBookCraftItemClick(e);
		} else if (cMenuView.getCMenu().getName().equals(TinkererMenu.MENU_NAME)) {
			onTinkererItemClick(e);
		} else if (cMenuView.getCMenu().getName().equals(CEAnvilMenu.MENU_NAME)) {
			onCEAnvilItemClick(e);
		}
	}

	public void onBookCraftItemClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory clickedInventory = e.getClickedInventory();
		ItemStack clickedItem = e.getCurrentItem();
		if (clickedInventory == null || clickedItem == null || clickedInventory.getType() != InventoryType.PLAYER) {
			return;
		}

		CEItem ceItem = CEAPI.getCEItem(clickedItem);

		if (ceItem == null || !(ceItem instanceof CEBook)) {
			return;
		}

		BookcraftMenu bookCraftMenu = BookcraftMenu.getBookCraftMenu(player);
		BookAddReason reason = bookCraftMenu.addBook(clickedItem, ((CEBook) ceItem).getData().getCESimple());

		if (reason == BookAddReason.SUCCESS) {
			e.setCurrentItem(null);
		}

		CustomEnchantmentMessage.send(player, "menu.bookcraft.add-book." + EnumUtils.toConfigStyle(reason));
	}

	public void onTinkererItemClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory clickedInventory = e.getClickedInventory();
		ItemStack clickedItem = e.getCurrentItem();
		if (clickedInventory == null || clickedItem == null || clickedInventory.getType() != InventoryType.PLAYER) {
			return;
		}

		CEItem ceItem = CEAPI.getCEItem(clickedItem);
		if (ceItem == null) {
			return;
		}

		TinkererMenu menu = TinkererMenu.getTinkererMenu(player);

		TinkererAddReason reason = menu.addItem(clickedItem, ceItem);

		if (reason == TinkererAddReason.SUCCESS) {
			e.setCurrentItem(null);
		}

		CustomEnchantmentMessage.send(player, "menu.tinkerer.add-tinkerer." + EnumUtils.toConfigStyle(reason));
	}

	public void onCEAnvilItemClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory clickedInventory = e.getClickedInventory();
		ItemStack clickedItem = e.getCurrentItem();
		if (clickedInventory == null || clickedItem == null || clickedInventory.getType() != InventoryType.PLAYER) {
			return;
		}

		CEItem ceItem = CEAPI.getCEItem(clickedItem);
		if (ceItem == null) {
			return;
		}

		CEAnvilMenu menu = CEAnvilMenu.getCEAnvilMenu(player);

		CEAnvilAddReason reason = menu.addItem(clickedItem, ceItem);

		if (reason == CEAnvilAddReason.SUCCESS) {
			e.setCurrentItem(null);
		}
		
		CustomEnchantmentMessage.send(player, "menu.ce-anvil.add-item." + EnumUtils.toConfigStyle(reason));
	}

	@EventHandler
	public void onMenuClick(CustomMenuClickEvent e) {
		if (e.getCMenu().getName().equals(BookcraftMenu.MENU_NAME)) {
			onBookCraftCustomItemClick(e);
		} else if (e.getCMenu().getName().equals(TinkererMenu.MENU_NAME)) {
			onTinkererCustomItemClick(e);
		} else if (e.getCMenu().getName().equals(CEAnvilMenu.MENU_NAME)) {
			onCEAnvilCustomItemClick(e);
		}
	}

	public void onBookCraftCustomItemClick(CustomMenuClickEvent e) {
		Player player = e.getCPlayer().getPlayer();
		String name = e.getClickedCItem().getName();

		BookcraftMenu bookCraftMenu = BookcraftMenu.getBookCraftMenu(player);
		if (name.equals("book1") || name.equals("book2")) {
			bookCraftMenu.returnBook(e.getClickedCItem().getName());
		} else if (name.equals("remind")) {
			BookcraftConfirmReason reason = bookCraftMenu.confirmUpgrade();
			CustomEnchantmentMessage.send(player, "menu.bookcraft.confirm." + EnumUtils.toConfigStyle(reason));
		}
	}

	public void onTinkererCustomItemClick(CustomMenuClickEvent e) {
		Player player = e.getCPlayer().getPlayer();
		String name = e.getClickedCItem().getName();

		TinkererMenu tinkererMenu = TinkererMenu.getTinkererMenu(player);
		if (name.equals("tinker")) {
			tinkererMenu.returnItem(e.getSlot());
		} else if (name.equals("accept")) {
			TinkererConfirmReason reason = tinkererMenu.confirmTinkerer();
			CustomEnchantmentMessage.send(player, "menu.tinkerer.confirm." + EnumUtils.toConfigStyle(reason));
		}
	}

	public void onCEAnvilCustomItemClick(CustomMenuClickEvent e) {
		Player player = e.getCPlayer().getPlayer();
		String name = e.getClickedCItem().getName();

		CEAnvilMenu menu = CEAnvilMenu.getCEAnvilMenu(player);
		menu.clickProcess(name);
	}
}
