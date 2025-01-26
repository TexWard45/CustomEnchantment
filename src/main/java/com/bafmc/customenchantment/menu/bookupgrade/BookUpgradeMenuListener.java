package com.bafmc.customenchantment.menu.bookupgrade;

import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.book.CEBook;
import com.bafmc.customenchantment.menu.MenuListenerAbstract;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeAddReason;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeConfirmReason;
import com.bafmc.custommenu.event.CustomMenuClickEvent;
import com.bafmc.custommenu.event.CustomMenuCloseEvent;
import com.bafmc.custommenu.event.CustomMenuOpenEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BookUpgradeMenuListener extends MenuListenerAbstract {
    @Override
    public String getMenuName() {
        return BookUpgradeMenu.MENU_NAME;
    }

    @Override
    public void onMenuOpen(CustomMenuOpenEvent e) {
        BookUpgradeMenuOpener.putBookUpgradeMenu(e.getCPlayer().getPlayer(), e.getCMenuView());
    }

    @Override
    public void onMenuClose(CustomMenuCloseEvent e) {
        BookUpgradeMenuOpener.removeBookUpgradeMenu(e.getCPlayer().getPlayer()).returnItems();
    }

    @Override
    public void onMenuClick(CustomMenuClickEvent e) {
        Player player = e.getCPlayer().getPlayer();
        String name = e.getClickedCItem().getName();

        BookUpgradeMenu menu = BookUpgradeMenuOpener.getBookUpgradeMenu(player);
        if (name.equals("book-upgrade")) {
            menu.returnBook();
            menu.updateMenu();
        } else if (name.equals("ingredient-preview")) {
            menu.returnBookIngredients(e.getSlot());
            menu.updateMenu();
        } else if (name.startsWith("remind")) {
            BookUpgradeConfirmReason reason = menu.confirmUpgrade();
            CustomEnchantmentMessage.send(player, "menu.bookupgrade.confirm." + EnumUtils.toConfigStyle(reason));
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

        BookUpgradeMenu menu = BookUpgradeMenuOpener.getBookUpgradeMenu(player);
        BookUpgradeAddReason reason = menu.addBook(clickedItem, ((CEBook) ceItem).getData().getCESimple());

        if (reason == BookUpgradeAddReason.SUCCESS) {
            e.setCurrentItem(null);
        }

        CustomEnchantmentMessage.send(player, "menu.bookupgrade.add-book." + EnumUtils.toConfigStyle(reason));
    }
}
