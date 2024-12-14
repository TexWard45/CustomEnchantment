package com.bafmc.customenchantment.listener;

import com.bafmc.customenchantment.menu.*;
import com.bafmc.customenchantment.menu.data.BookUpgradeAddReason;
import com.bafmc.customenchantment.menu.data.BookUpgradeConfirmReason;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.book.CEBook;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.menu.BookCraftMenu.BookAddReason;
import com.bafmc.customenchantment.menu.BookCraftMenu.BookcraftConfirmReason;
import com.bafmc.customenchantment.menu.CEAnvilMenu.CEAnvilAddReason;
import com.bafmc.customenchantment.menu.TinkererMenu.TinkererAddReason;
import com.bafmc.customenchantment.menu.TinkererMenu.TinkererConfirmReason;
import com.bafmc.custommenu.api.CustomMenuAPI;
import com.bafmc.custommenu.event.CustomMenuClickEvent;
import com.bafmc.custommenu.event.CustomMenuCloseEvent;
import com.bafmc.custommenu.event.CustomMenuOpenEvent;
import com.bafmc.custommenu.menu.CMenuView;
import com.bafmc.custommenu.player.CPlayer;
import com.bafmc.bukkit.utils.EnumUtils;

public class CMenuListener implements Listener {
	private CustomEnchantment plugin;

	public CMenuListener(CustomEnchantment plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMenuOpen(CustomMenuOpenEvent e) {
		String name = e.getCMenu().getName();

		if (name.equals(BookCraftMenu.MENU_NAME)) {
			BookCraftMenu.putBookCraftMenu(e.getCPlayer().getPlayer(), e.getCMenuView());
		} else if (name.equals(TinkererMenu.MENU_NAME)) {
			TinkererMenu.putTinkererMenu(e.getCPlayer().getPlayer(), e.getCMenuView());
		} else if (name.equals(CEAnvilMenu.MENU_NAME)) {
			CEAnvilMenu.putCEAnvilMenu(e.getCPlayer().getPlayer(), e.getCMenuView());
		} else if (name.equals(BookUpgradeMenu.MENU_NAME)) {
            BookUpgradeMenuOpener.putBookUpgradeMenu(e.getCPlayer().getPlayer(), e.getCMenuView());
        }
	}

	@EventHandler
	public void onMenuClose(CustomMenuCloseEvent e) {
		String name = e.getCMenu().getName();

		if (name.equals(BookCraftMenu.MENU_NAME)) {
			BookCraftMenu.removeBookCraftMenu(e.getCPlayer().getPlayer()).returnItems();
		} else if (name.equals(TinkererMenu.MENU_NAME)) {
			TinkererMenu.removeTinkererMenu(e.getCPlayer().getPlayer()).returnItems();
		} else if (name.equals(CEAnvilMenu.MENU_NAME)) {
			CEAnvilMenu.removeCEAnvilMenu(e.getCPlayer().getPlayer()).returnItems();
		} else if (name.equals(BookUpgradeMenu.MENU_NAME)) {
            BookUpgradeMenuOpener.removeBookUpgradeMenu(e.getCPlayer().getPlayer()).returnItems();
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
		if (cMenuView.getCMenu().getName().equals(BookCraftMenu.MENU_NAME)) {
			onBookCraftItemClick(e);
		} else if (cMenuView.getCMenu().getName().equals(TinkererMenu.MENU_NAME)) {
			onTinkererItemClick(e);
		} else if (cMenuView.getCMenu().getName().equals(CEAnvilMenu.MENU_NAME)) {
			onCEAnvilItemClick(e);
		} else if (cMenuView.getCMenu().getName().equals(BookUpgradeMenu.MENU_NAME)) {
            onBookUpgradeItemClick(e);
        }
	}

    public void onBookUpgradeItemClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory clickedInventory = e.getClickedInventory();
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedInventory == null || clickedItem == null || clickedInventory.getType() != InventoryType.PLAYER || clickedItem.getAmount() > 1) {
            return;
        }

        CEItem ceItem = CEAPI.getCEItem(clickedItem);
        if (ceItem == null || !(ceItem instanceof CEBook)) {
            return;
        }

        BookUpgradeMenu bookUpgradeMenu = BookUpgradeMenuOpener.getBookUpgradeMenu(player);
        BookUpgradeAddReason reason = bookUpgradeMenu.addBook(clickedItem, ((CEBook) ceItem).getData().getCESimple());

        if (reason == BookUpgradeAddReason.SUCCESS) {
            e.setCurrentItem(null);
        }

        CustomEnchantmentMessage.send(player, "menu.bookupgrade.add-book." + EnumUtils.toConfigStyle(reason));
    }

	public void onBookCraftItemClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory clickedInventory = e.getClickedInventory();
		ItemStack clickedItem = e.getCurrentItem();
		if (clickedInventory == null || clickedItem == null || clickedInventory.getType() != InventoryType.PLAYER || clickedItem.getAmount() > 1) {
			return;
		}

		CEItem ceItem = CEAPI.getCEItem(clickedItem);

		if (ceItem == null || !(ceItem instanceof CEBook)) {
			return;
		}

		BookCraftMenu bookCraftMenu = BookCraftMenu.getBookCraftMenu(player);
		BookAddReason reason = bookCraftMenu.addBook(clickedItem, ((CEBook) ceItem).getData().getCESimple());

		if (reason == BookAddReason.SUCCESS) {
			e.setCurrentItem(null);
		}

		CustomEnchantmentMessage.send(player, "menu.bookcraft.add-book." + EnumUtils.toConfigStyle(reason));

        bookCraftMenu.fastCraft(player);
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
		if (e.getCMenu().getName().equals(BookCraftMenu.MENU_NAME)) {
			onBookCraftCustomItemClick(e);
		} else if (e.getCMenu().getName().equals(TinkererMenu.MENU_NAME)) {
			onTinkererCustomItemClick(e);
		} else if (e.getCMenu().getName().equals(CEAnvilMenu.MENU_NAME)) {
			onCEAnvilCustomItemClick(e);
		} else if (e.getCMenu().getName().equals(BookUpgradeMenu.MENU_NAME)) {
            onBookUpgradeCustomItemClick(e);
        }
	}

    public void onBookUpgradeCustomItemClick(CustomMenuClickEvent e) {
        Player player = e.getCPlayer().getPlayer();
        String name = e.getClickedCItem().getName();

        BookUpgradeMenu bookUpgradeMenu = BookUpgradeMenuOpener.getBookUpgradeMenu(player);
        if (name.equals("book-upgrade")) {
            bookUpgradeMenu.returnBook();
            bookUpgradeMenu.updateMenu();
        } else if (name.equals("ingredient-preview")) {
            bookUpgradeMenu.returnBookIngredients(e.getSlot());
            bookUpgradeMenu.updateMenu();
        } else if (name.startsWith("remind")) {
            BookUpgradeConfirmReason reason = bookUpgradeMenu.confirmUpgrade();
            CustomEnchantmentMessage.send(player, "menu.bookupgrade.confirm." + EnumUtils.toConfigStyle(reason));
        }
    }

    public void onBookCraftCustomItemClick(CustomMenuClickEvent e) {
		Player player = e.getCPlayer().getPlayer();
		String name = e.getClickedCItem().getName();

		BookCraftMenu bookCraftMenu = BookCraftMenu.getBookCraftMenu(player);
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
