package com.bafmc.customenchantment.menu.tinkerer;

import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.menu.MenuListenerAbstract;
import com.bafmc.custommenu.event.CustomMenuClickEvent;
import com.bafmc.custommenu.event.CustomMenuCloseEvent;
import com.bafmc.custommenu.event.CustomMenuOpenEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TinkererMenuListener extends MenuListenerAbstract {
    @Override
    public String getMenuName() {
        return TinkererMenu.MENU_NAME;
    }

    @Override
    public void onMenuOpen(CustomMenuOpenEvent e) {
        TinkererMenu.putTinkererMenu(e.getCPlayer().getPlayer(), e.getCMenuView());
    }

    @Override
    public void onMenuClose(CustomMenuCloseEvent e) {
        TinkererMenu.removeTinkererMenu(e.getCPlayer().getPlayer()).returnItems();
    }

    @Override
    public void onMenuClick(CustomMenuClickEvent e) {
        Player player = e.getCPlayer().getPlayer();
        String name = e.getClickedCItem().getName();

        TinkererMenu tinkererMenu = TinkererMenu.getTinkererMenu(player);
        if (name.equals("tinker")) {
            tinkererMenu.returnItem(e.getSlot());
        } else if (name.equals("accept")) {
            TinkererMenu.TinkererConfirmReason reason = tinkererMenu.confirmTinkerer();
            CustomEnchantmentMessage.send(player, "menu.tinkerer.confirm." + EnumUtils.toConfigStyle(reason));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
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

        TinkererMenu.TinkererAddReason reason = menu.addItem(clickedItem, ceItem);

        if (reason == TinkererMenu.TinkererAddReason.SUCCESS) {
            e.setCurrentItem(null);
        }

        CustomEnchantmentMessage.send(player, "menu.tinkerer.add-tinkerer." + EnumUtils.toConfigStyle(reason));
    }
}
