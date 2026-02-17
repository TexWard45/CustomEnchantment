package com.bafmc.customenchantment.menu.ceanvil.handler;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.item.gem.CEGemSimple;
import com.bafmc.customenchantment.item.removegem.CERemoveGem;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RemoveGemHandler extends AbstractListHandler<CEGemSimple> {

    @Override
    public boolean isSuitable(CEItem ceItem) {
        return ceItem instanceof CERemoveGem;
    }

    @Override
    public List<CEGemSimple> getList(CEItem ceItem1, CEItem ceItem2) {
        if (!(ceItem1 instanceof CEWeapon weapon)) {
            return new ArrayList<>();
        }
        if (!(ceItem2 instanceof CERemoveGem)) {
            return new ArrayList<>();
        }

        List<CEGemSimple> list = weapon.getWeaponGem().getCEGemSimpleList();
        List<CEGemSimple> result = new ArrayList<>();

        int index = 0;
        for (CEGemSimple ceGemSimple : list) {
            CEGemSimple newCEGemSimple = ceGemSimple.clone();
            newCEGemSimple.setIndex(index);
            result.add(newCEGemSimple);
            index++;
        }

        return result;
    }

    @Override
    public ItemStack getDisplayItem(CEGemSimple ceGemSimple) {
        var ceGem = ceGemSimple.getCEGem();
        if (ceGem == null) {
            return null;
        }
        ceGem.getData().setLevel(ceGemSimple.getLevel());
        return ceGem.exportTo();
    }

    @Override
    public ApplyReason getApplyReason(CEItem ceItem1, CEItem ceItem2, CEGemSimple ceGemSimple) {
        if (!(ceItem2 instanceof CERemoveGem removeGem)) {
            return ApplyReason.NOTHING;
        }
        return removeGem.applyByMenuTo(ceItem1, ceGemSimple);
    }

    @Override
    public String getConfirmTemplateName() {
        return "confirm-remove-gem";
    }
}
