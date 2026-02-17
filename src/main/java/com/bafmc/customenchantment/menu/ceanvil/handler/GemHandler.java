package com.bafmc.customenchantment.menu.ceanvil.handler;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.gem.CEGem;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;
import org.bukkit.inventory.ItemStack;

public class GemHandler implements Slot2Handler {

    @Override
    public boolean isSuitable(CEItem ceItem) {
        return ceItem instanceof CEGem;
    }

    @Override
    public void updateView(CEAnvilCustomMenu menu) {
        CEItem ceItem1 = menu.getExtraData().getItemData1().getCeItem();
        CEItem ceItem2 = menu.getExtraData().getItemData2().getCeItem();
        CEGem gem = (CEGem) ceItem2;

        ApplyReason reason = gem.testApplyByMenuTo(ceItem1);

        if (reason.getResult() == ApplyResult.SUCCESS) {
            menu.updateSlots("preview3", reason.getSource().exportTo());
        } else {
            menu.updateSlots("preview3", null);
        }
    }

    @Override
    public void updateConfirm(CEAnvilCustomMenu menu) {
        menu.updateSlots("confirm", menu.getTemplateItemStack("confirm-gem"));
    }

    @Override
    public void clickProcess(CEAnvilCustomMenu menu, String itemName) {
    }

    @Override
    public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
        if (ceItem2 instanceof CEGem gem) {
            return gem.applyByMenuTo(ceItem1);
        }
        return ApplyReason.NOTHING;
    }

    @Override
    public void clearPreviews(CEAnvilCustomMenu menu) {
        menu.updateSlots("preview3", null);
    }
}
