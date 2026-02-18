package com.bafmc.customenchantment.menu.ceanvil.handler;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.removeprotectdead.CERemoveProtectDead;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;
import org.bukkit.inventory.ItemStack;

public class RemoveProtectDeadHandler implements Slot2Handler {

    @Override
    public boolean isSuitable(CEItem ceItem) {
        return ceItem instanceof CERemoveProtectDead;
    }

    @Override
    public void updateView(CEAnvilCustomMenu menu) {
        CEItem ceItem1 = menu.getExtraData().getItemData1().getCeItem();
        CEItem ceItem2 = menu.getExtraData().getItemData2().getCeItem();
        CERemoveProtectDead removeProtectDead = (CERemoveProtectDead) ceItem2;

        ApplyReason reason = removeProtectDead.testApplyByMenuTo(ceItem1);

        if (reason.getResult() == ApplyResult.SUCCESS) {
            menu.updateSlots("preview3", reason.getSource().exportTo());
            menu.updateSlots("preview4", removeProtectDead.getProtectDeadItem());
        } else {
            menu.updateSlots("preview3", null);
            menu.updateSlots("preview4", null);
        }
    }

    @Override
    public void updateConfirm(CEAnvilCustomMenu menu) {
        menu.updateSlots("confirm", menu.getTemplateItemStack("confirm-remove-protect-dead"));
    }

    @Override
    public void clickProcess(CEAnvilCustomMenu menu, String itemName) {
    }

    @Override
    public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
        if (ceItem2 instanceof CERemoveProtectDead removeProtectDead) {
            return removeProtectDead.applyByMenuTo(ceItem1);
        }
        return ApplyReason.NOTHING;
    }

    @Override
    public void clearPreviews(CEAnvilCustomMenu menu) {
        menu.updateSlots("preview3", null);
        menu.updateSlots("preview4", null);
    }
}
