package com.bafmc.customenchantment.menu.ceanvil.handler;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.item.enchantpoint.CEEnchantPointSimple;
import com.bafmc.customenchantment.item.removeenchantpoint.CERemoveEnchantPoint;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RemoveEnchantPointHandler extends AbstractListHandler<CEEnchantPointSimple> {

    @Override
    public boolean isSuitable(CEItem ceItem) {
        return ceItem instanceof CERemoveEnchantPoint;
    }

    @Override
    public List<CEEnchantPointSimple> getList(CEItem ceItem1, CEItem ceItem2) {
        if (!(ceItem1 instanceof CEWeapon weapon)) {
            return new ArrayList<>();
        }
        if (!(ceItem2 instanceof CERemoveEnchantPoint removeEnchantPoint)) {
            return new ArrayList<>();
        }
        return removeEnchantPoint.getList(weapon.getWeaponData().getExtraEnchantPointList());
    }

    @Override
    public ItemStack getDisplayItem(CEEnchantPointSimple ceEnchantPointSimple) {
        CEItem ceItem = CEAPI.getCEItemByStorage(CEItemType.ENCHANT_POINT, ceEnchantPointSimple.getPattern());
        if (ceItem == null) {
            return null;
        }
        return ceItem.exportTo();
    }

    @Override
    public ApplyReason getApplyReason(CEItem ceItem1, CEItem ceItem2, CEEnchantPointSimple ceEnchantPointSimple) {
        if (!(ceItem2 instanceof CERemoveEnchantPoint removeEnchantPoint)) {
            return ApplyReason.NOTHING;
        }
        return removeEnchantPoint.applyByMenuTo(ceItem1, ceEnchantPointSimple);
    }

    @Override
    public String getConfirmTemplateName() {
        return "confirm-remove-enchant-point";
    }
}
