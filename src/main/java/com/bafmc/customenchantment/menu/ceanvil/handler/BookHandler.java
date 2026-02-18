package com.bafmc.customenchantment.menu.ceanvil.handler;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.book.CEBook;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;
import org.bukkit.inventory.ItemStack;

public class BookHandler implements Slot2Handler {

    @Override
    public boolean isSuitable(CEItem ceItem) {
        return ceItem instanceof CEBook;
    }

    @Override
    public void updateView(CEAnvilCustomMenu menu) {
        CEItem ceItem1 = menu.getExtraData().getItemData1().getCeItem();
        CEItem ceItem2 = menu.getExtraData().getItemData2().getCeItem();
        CEBook book = (CEBook) ceItem2;

        ApplyReason reason = book.testApplyTo(ceItem1);

        if (reason.getResult() == ApplyResult.SUCCESS) {
            menu.updateSlots("preview3", reason.getSource().exportTo());
        } else {
            menu.updateSlots("preview3", null);
        }
    }

    @Override
    public void updateConfirm(CEAnvilCustomMenu menu) {
        ItemStack confirmItem = menu.getTemplateItemStack("confirm-book");
        menu.updateSlots("confirm", confirmItem);
    }

    @Override
    public void clickProcess(CEAnvilCustomMenu menu, String itemName) {
        // No click processing for book handler
    }

    @Override
    public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
        if (ceItem2 instanceof CEBook book) {
            return book.applyByMenuTo(ceItem1);
        }
        return ApplyReason.NOTHING;
    }

    @Override
    public void clearPreviews(CEAnvilCustomMenu menu) {
        menu.updateSlots("preview3", null);
    }
}
