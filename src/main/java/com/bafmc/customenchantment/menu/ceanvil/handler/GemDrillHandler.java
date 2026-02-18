package com.bafmc.customenchantment.menu.ceanvil.handler;

import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.item.WeaponGem;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrill;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class GemDrillHandler implements Slot2Handler {

    @Override
    public boolean isSuitable(CEItem ceItem) {
        return ceItem instanceof CEGemDrill;
    }

    @Override
    public void updateView(CEAnvilCustomMenu menu) {
        CEItem ceItem1 = menu.getExtraData().getItemData1().getCeItem();
        CEItem ceItem2 = menu.getExtraData().getItemData2().getCeItem();
        CEGemDrill gemDrill = (CEGemDrill) ceItem2;

        ApplyReason reason = gemDrill.testApplyByMenuTo(ceItem1);

        if (reason.getResult() == ApplyResult.SUCCESS) {
            menu.updateSlots("preview3", reason.getSource().exportTo());
        } else {
            menu.updateSlots("preview3", null);
        }
    }

    @Override
    public void updateConfirm(CEAnvilCustomMenu menu) {
        CEWeapon ceWeapon = (CEWeapon) menu.getExtraData().getItemData1().getCeItem();
        CEGemDrill gemDrill = (CEGemDrill) menu.getExtraData().getItemData2().getCeItem();

        WeaponGem weaponGem = ceWeapon.getWeaponGem();

        int drillSize = weaponGem.getDrillSize();
        if (drillSize >= gemDrill.getData().getConfigData().getMaxDrill()) {
            menu.updateSlots("confirm", menu.getTemplateItemStack("confirm-gem-drill-max"));
        } else {
            int nextDrillSlot = drillSize + 1;

            Map<Integer, Double> slotChance = gemDrill.getData().getConfigData().getSlotChance();
            double chance = slotChance.getOrDefault(nextDrillSlot, 100.0);

            if (chance < 100) {
                ItemStack itemStack = menu.getTemplateItemStack("confirm-gem-drill-with-chance");
                itemStack = ItemStackUtils.getItemStack(itemStack,
                        PlaceholderBuilder.builder().put("{chance}", StringUtils.formatNumber(chance)).build());
                menu.updateSlots("confirm", itemStack);
            } else {
                menu.updateSlots("confirm", menu.getTemplateItemStack("confirm-gem-drill"));
            }
        }
    }

    @Override
    public void clickProcess(CEAnvilCustomMenu menu, String itemName) {
    }

    @Override
    public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
        if (ceItem2 instanceof CEGemDrill gemDrill) {
            return gemDrill.applyByMenuTo(ceItem1);
        }
        return ApplyReason.NOTHING;
    }

    @Override
    public void clearPreviews(CEAnvilCustomMenu menu) {
        menu.updateSlots("preview3", null);
    }
}
