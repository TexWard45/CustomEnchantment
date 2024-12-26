package com.bafmc.customenchantment.menu;

import com.bafmc.custommenu.event.CustomMenuClickEvent;
import com.bafmc.custommenu.event.CustomMenuCloseEvent;
import com.bafmc.custommenu.event.CustomMenuOpenEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class MenuListenerAbstract {
    public abstract String getMenuName();
    public abstract void onMenuOpen(CustomMenuOpenEvent e);
    public abstract void onMenuClose(CustomMenuCloseEvent e);
    public abstract void onMenuClick(CustomMenuClickEvent e);
    public abstract void onInventoryClick(InventoryClickEvent e);
}
