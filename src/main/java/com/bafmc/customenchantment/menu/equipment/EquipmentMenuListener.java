package com.bafmc.customenchantment.menu.equipment;

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

public class EquipmentMenuListener extends MenuListenerAbstract {
    @Override
    public String getMenuName() {
        return EquipmentMenu.MENU_NAME;
    }

    @Override
    public void onMenuOpen(CustomMenuOpenEvent e) {
        EquipmentMenu.putMenu(e.getCPlayer().getPlayer(), e.getCMenuView());
    }

    @Override
    public void onMenuClose(CustomMenuCloseEvent e) {
        EquipmentMenu.removeMenu(e.getCPlayer().getPlayer()).returnItems();
    }

    @Override
    public void onMenuClick(CustomMenuClickEvent e) {
        Player player = e.getCPlayer().getPlayer();
        String name = e.getClickedCItem().getName();

        EquipmentMenu menu = EquipmentMenu.getMenu(player);
        if (name.equals("artifact")) {
            menu.returnItem(e.getSlot());
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

        EquipmentMenu menu = EquipmentMenu.getMenu(player);

        EquipmentMenu.EquipmentAddReason reason = menu.addItem(clickedItem, ceItem);

        if (reason == EquipmentMenu.EquipmentAddReason.ADD_ARTIFACT) {
            e.setCurrentItem(null);
        }

        CustomEnchantmentMessage.send(player, "menu.equipment.add-equipment." + EnumUtils.toConfigStyle(reason));
    }
}
