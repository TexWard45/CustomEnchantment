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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EquipmentMenuListener extends MenuListenerAbstract {
    private static final Map<String, Long> swapSkinCooldowns = new HashMap<>();
    private static final long SWAP_SKIN_COOLDOWN_MS = 1000; // 1 second

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
        if (menu.isInUpdateMenu()) {
            e.setCancelled(true);
            return;
        }

        ClickType clickType = e.getInventoryClickEvent().getClick();

        if (clickType == ClickType.LEFT) {
            menu.returnItem(name, e.getSlot());
        }else if (clickType == ClickType.RIGHT) {
            String playerName = player.getName();
            long currentTime = System.currentTimeMillis();
            Long lastSwapTime = swapSkinCooldowns.get(playerName);

            if (lastSwapTime != null && (currentTime - lastSwapTime) < SWAP_SKIN_COOLDOWN_MS) {
                return;
            }

            swapSkinCooldowns.put(playerName, currentTime);
            menu.swapSkin(name, e.getSlot());
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

        EquipmentMenu menu = EquipmentMenu.getMenu(player);
        if (menu.isInUpdateMenu()) {
            e.setCancelled(true);
            return;
        }

        CEItem ceItem = CEAPI.getCEItem(clickedItem);
        EquipmentMenu.EquipmentAddReason reason = menu.addItem(e, clickedItem, ceItem);

        CustomEnchantmentMessage.send(player, "menu.equipment.add-equipment." + EnumUtils.toConfigStyle(reason));
    }
}
