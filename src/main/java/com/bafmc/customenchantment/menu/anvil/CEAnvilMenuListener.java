package com.bafmc.customenchantment.menu.anvil;

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

public class CEAnvilMenuListener extends MenuListenerAbstract {
    @Override
    public String getMenuName() {
        return CEAnvilMenu.MENU_NAME;
    }

    @Override
    public void onMenuOpen(CustomMenuOpenEvent e) {
        CEAnvilMenu.putCEAnvilMenu(e.getCPlayer().getPlayer(), e.getCMenuView());
    }

    @Override
    public void onMenuClose(CustomMenuCloseEvent e) {
        CEAnvilMenu.removeCEAnvilMenu(e.getCPlayer().getPlayer()).returnItems();
    }

    @Override
    public void onMenuClick(CustomMenuClickEvent e) {
        Player player = e.getCPlayer().getPlayer();
        String name = e.getClickedCItem().getName();

        CEAnvilMenu menu = CEAnvilMenu.getCEAnvilMenu(player);
        menu.clickProcess(name);
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

        CEAnvilMenu menu = CEAnvilMenu.getCEAnvilMenu(player);

        CEAnvilMenu.CEAnvilAddReason reason = menu.addItem(clickedItem, ceItem);

        if (reason == CEAnvilMenu.CEAnvilAddReason.SUCCESS) {
            e.setCurrentItem(null);
        }

        CustomEnchantmentMessage.send(player, "menu.ce-anvil.add-item." + EnumUtils.toConfigStyle(reason));
    }
}
