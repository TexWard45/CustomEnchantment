package com.bafmc.customenchantment.menu.ceanvil.handler;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;

public interface Slot2Handler {

    boolean isSuitable(CEItem ceItem);

    void updateView(CEAnvilCustomMenu menu);

    void updateConfirm(CEAnvilCustomMenu menu);

    void clickProcess(CEAnvilCustomMenu menu, String itemName);

    ApplyReason apply(CEItem ceItem1, CEItem ceItem2);

    void clearPreviews(CEAnvilCustomMenu menu);
}
