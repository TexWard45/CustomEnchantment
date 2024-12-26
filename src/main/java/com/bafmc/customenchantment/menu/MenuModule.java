package com.bafmc.customenchantment.menu;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.listener.CMenuListener;
import com.bafmc.customenchantment.menu.anvil.*;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftMenuListener;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeMenuListener;
import com.bafmc.customenchantment.menu.equipment.EquipmentMenuListener;
import com.bafmc.customenchantment.menu.tinkerer.TinkererMenuListener;

public class MenuModule extends PluginModule<CustomEnchantment> {
    public MenuModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        CEAnvilMenu.registerView1(CEItemType.WEAPON, Slot1CEWeaponView.class);
        CEAnvilMenu.registerView2(CEItemType.DEFAULT, Slot2CEDefaultView.class);
        CEAnvilMenu.registerView2(CEItemType.REMOVE_ENCHANT, Slot2CERemoveEnchantView.class);
        CEAnvilMenu.registerView2(CEItemType.ENCHANT_POINT, Slot2CEEnchantPointView.class);
        CEAnvilMenu.registerView2(CEItemType.BOOK, Slot2CEBookView.class);
        CEAnvilMenu.registerView2(CEItemType.PROTECT_DEAD, Slot2CEProtectDeadView.class);
        CEAnvilMenu.registerView2(CEItemType.REMOVE_PROTECT_DEAD, Slot2CERemoveProtectDeadView.class);
        CEAnvilMenu.registerView2(CEItemType.LORE_FORMAT, Slot2CELoreFormatView.class);
        CEAnvilMenu.registerView2(CEItemType.REMOVE_ENCHANT_POINT, Slot2CERemoveEnchantPointView.class);
        CEAnvilMenu.registerView2(CEItemType.REMOVE_GEM, Slot2CERemoveGemView.class);
        CEAnvilMenu.registerView2(CEItemType.PROTECT_DESTROY, Slot2CEProtectDestroyView.class);
        CEAnvilMenu.registerView2(CEItemType.EARSE_ENCHANT, Slot2CEEraseEnchantView.class);
        CEAnvilMenu.registerView2(CEItemType.GEM, Slot2CEGemView.class);
        CEAnvilMenu.registerView2(CEItemType.GEM_DRILL, Slot2CEGemDrillView.class);

        CMenuListener.registerMenuListener(new BookCraftMenuListener());
        CMenuListener.registerMenuListener(new TinkererMenuListener());
        CMenuListener.registerMenuListener(new CEAnvilMenuListener());
        CMenuListener.registerMenuListener(new BookUpgradeMenuListener());
        CMenuListener.registerMenuListener(new EquipmentMenuListener());

    }
}
