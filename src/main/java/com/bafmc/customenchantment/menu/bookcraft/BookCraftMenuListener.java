package com.bafmc.customenchantment.menu.bookcraft;

import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.book.CEBook;
import com.bafmc.customenchantment.menu.MenuListenerAbstract;
import com.bafmc.custommenu.event.CustomMenuClickEvent;
import com.bafmc.custommenu.event.CustomMenuCloseEvent;
import com.bafmc.custommenu.event.CustomMenuOpenEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BookCraftMenuListener extends MenuListenerAbstract {
    @Override
    public String getMenuName() {
        return BookCraftMenu.MENU_NAME;
    }

    @Override
    public void onMenuOpen(CustomMenuOpenEvent e) {
        BookCraftMenu.putBookCraftMenu(e.getCPlayer().getPlayer(), e.getCMenuView());
    }

    @Override
    public void onMenuClose(CustomMenuCloseEvent e) {
        BookCraftMenu.removeBookCraftMenu(e.getCPlayer().getPlayer()).returnItems();
    }

    @Override
    public void onMenuClick(CustomMenuClickEvent e) {
        Player player = e.getCPlayer().getPlayer();
        String name = e.getClickedCItem().getName();

        BookCraftMenu bookCraftMenu = BookCraftMenu.getBookCraftMenu(player);
        if (name.equals("book1") || name.equals("book2")) {
            bookCraftMenu.returnBook(e.getClickedCItem().getName());
        } else if (name.equals("remind")) {
            BookCraftMenu.BookcraftConfirmReason reason = bookCraftMenu.confirmUpgrade();
            CustomEnchantmentMessage.send(player, "menu.bookcraft.confirm." + EnumUtils.toConfigStyle(reason));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
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
        BookCraftMenu.BookAddReason reason = bookCraftMenu.addBook(clickedItem, ((CEBook) ceItem).getData().getCESimple());

        if (reason == BookCraftMenu.BookAddReason.SUCCESS) {
            e.setCurrentItem(null);
        }

        CustomEnchantmentMessage.send(player, "menu.bookcraft.add-book." + EnumUtils.toConfigStyle(reason));

        bookCraftMenu.fastCraft(player);
    }
}
