package com.bafmc.customenchantment.menu.ceanvil.handler;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilSettings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DefaultHandler implements Slot2Handler {

    @Getter
    @Setter
    private int page = 1;

    @Getter
    @Setter
    private int maxPage = 1;

    @Getter
    @Setter
    private List<CEEnchantSimple> enchantList = new ArrayList<>();

    @Override
    public boolean isSuitable(CEItem ceItem) {
        return ceItem == null;
    }

    @Override
    public void updateView(CEAnvilCustomMenu menu) {
        CEItem ceItem1 = menu.getExtraData().getItemData1().getCeItem();
        if (!(ceItem1 instanceof CEWeapon weapon)) {
            return;
        }

        List<String> enchantGroups = menu.getMenuData().getDataConfig()
                .getStringList("default-view.enchant-group");

        List<CEEnchantSimple> allEnchants = weapon.getWeaponEnchant().getCESimpleList();
        List<CEEnchantSimple> filtered = new ArrayList<>();

        for (CEEnchantSimple enchant : allEnchants) {
            if (enchantGroups.contains(enchant.getCEEnchant().getGroupName())) {
                filtered.add(enchant);
            }
        }

        this.enchantList = filtered;
        this.maxPage = Math.max(1, (int) Math.ceil(filtered.size() / 5d));

        updateEnchantDisplay(menu);
    }

    @Override
    public void updateConfirm(CEAnvilCustomMenu menu) {
        ItemStack confirmItem = menu.getTemplateItemStack("confirm-remove-enchant");
        menu.updateSlots("confirm", confirmItem);
    }

    @Override
    public void clickProcess(CEAnvilCustomMenu menu, String itemName) {
        if ("next-page".equals(itemName)) {
            if (page < maxPage) {
                page++;
                updateEnchantDisplay(menu);
            }
        } else if ("previous-page".equals(itemName)) {
            if (page > 1) {
                page--;
                updateEnchantDisplay(menu);
            }
        }
    }

    @Override
    public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
        return ApplyReason.NOTHING;
    }

    @Override
    public void clearPreviews(CEAnvilCustomMenu menu) {
        CEAnvilSettings settings = menu.getExtraData().getSettings();
        int[] previewOrder = settings.getPreviewIndexOrder();
        for (int previewNum : previewOrder) {
            menu.updateSlots("preview" + previewNum, null);
        }
        menu.updateSlots("next-page", null);
        menu.updateSlots("previous-page", null);
    }

    private void updateEnchantDisplay(CEAnvilCustomMenu menu) {
        CEAnvilSettings settings = menu.getExtraData().getSettings();
        int[] previewOrder = settings.getPreviewIndexOrder();

        for (int i = 0; i < 5; i++) {
            int listIndex = i + (page - 1) * 5;
            int previewNum = previewOrder[i];

            if (listIndex >= enchantList.size()) {
                menu.updateSlots("preview" + previewNum, null);
            } else {
                ItemStack book = CEAPI.getCEBookItemStack(enchantList.get(listIndex));
                menu.updateSlots("preview" + previewNum, book);
            }
        }
        updatePageIndicators(menu);
    }

    private void updatePageIndicators(CEAnvilCustomMenu menu) {
        if (page < maxPage) {
            ItemStack nextItem = menu.getTemplateItemStack("has-next-page");
            if (nextItem != null) {
                nextItem.setAmount(page + 1);
            }
            menu.updateSlots("next-page", nextItem);
        } else {
            menu.updateSlots("next-page", null);
        }

        if (page > 1) {
            ItemStack prevItem = menu.getTemplateItemStack("has-previous-page");
            if (prevItem != null) {
                prevItem.setAmount(page - 1);
            }
            menu.updateSlots("previous-page", prevItem);
        } else {
            menu.updateSlots("previous-page", null);
        }
    }
}
