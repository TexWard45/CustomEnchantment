package com.bafmc.customenchantment.menu.bookupgrade.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeCustomMenu;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeConfirmReason;

public class BookUpgradeRemindItem extends AbstractItem<BookUpgradeCustomMenu> {

    @Override
    public String getType() {
        return "book-upgrade-remind";
    }

    @Override
    public void handleClick(ClickData data) {
        BookUpgradeConfirmReason reason = menu.confirmUpgrade();
        CustomEnchantmentMessage.send(data.getPlayer(),
                "menu.bookupgrade.confirm." + EnumUtils.toConfigStyle(reason));
    }
}
