package com.bafmc.customenchantment.menu.artifactupgrade;

import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.menu.MenuListenerAbstract;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeAddReason;
import com.bafmc.custommenu.event.CustomMenuClickEvent;
import com.bafmc.custommenu.event.CustomMenuCloseEvent;
import com.bafmc.custommenu.event.CustomMenuOpenEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ArtifactUpgradeMenuListener extends MenuListenerAbstract {
    @Override
    public String getMenuName() {
        return ArtifactUpgradeMenu.MENU_NAME;
    }

    @Override
    public void onMenuOpen(CustomMenuOpenEvent e) {
        ArtifactUpgradeMenuOpener.putArtifactUpgradeMenu(e.getCPlayer().getPlayer(), e.getCMenuView());
    }

    @Override
    public void onMenuClose(CustomMenuCloseEvent e) {
        ArtifactUpgradeMenuOpener.removeArtifactUpgradeMenu(e.getCPlayer().getPlayer()).returnItems();
    }

    @Override
    public void onMenuClick(CustomMenuClickEvent e) {
        Player player = e.getCPlayer().getPlayer();
        String name = e.getClickedCItem().getName();

        ArtifactUpgradeMenu menu = ArtifactUpgradeMenuOpener.getArtifactUpgradeMenu(player);
        menu.returnItem(name, e.getSlot());
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
        if (ceItem == null) {
            return;
        }

        ArtifactUpgradeMenu menu = ArtifactUpgradeMenuOpener.getArtifactUpgradeMenu(player);
        ArtifactUpgradeAddReason reason = menu.addItem(clickedItem, ceItem, e);

        CustomEnchantmentMessage.send(player, "menu.artifactupgrade.add-item." + EnumUtils.toConfigStyle(reason));
    }
}
