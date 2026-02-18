package com.bafmc.customenchantment.menu.ceanvil.handler;

import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilSettings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListHandler<L> implements Slot2Handler {

    @Getter
    @Setter
    private int page = 1;

    @Getter
    @Setter
    private int maxPage = 1;

    @Getter
    @Setter
    private int chooseIndex = -1;

    @Getter
    @Setter
    private List<L> list = new ArrayList<>();

    public abstract List<L> getList(CEItem ceItem1, CEItem ceItem2);

    public abstract ItemStack getDisplayItem(L object);

    public abstract ApplyReason getApplyReason(CEItem ceItem1, CEItem ceItem2, L object);

    public abstract String getConfirmTemplateName();

    @Override
    public void updateView(CEAnvilCustomMenu menu) {
        CEItem ceItem1 = menu.getExtraData().getItemData1().getCeItem();
        CEItem ceItem2 = menu.getExtraData().getItemData2().getCeItem();

        List<L> newList = getList(ceItem1, ceItem2);
        this.list = newList;
        this.maxPage = Math.max(1, (int) Math.ceil(newList.size() / 5d));

        updateListDisplay(menu);
    }

    @Override
    public void updateConfirm(CEAnvilCustomMenu menu) {
        ItemStack confirmItem = menu.getTemplateItemStack(getConfirmTemplateName());
        menu.updateSlots("confirm", confirmItem);
    }

    @Override
    public void clickProcess(CEAnvilCustomMenu menu, String itemName) {
        if ("next-page".equals(itemName)) {
            nextPage(menu);
        } else if ("previous-page".equals(itemName)) {
            previousPage(menu);
        } else if (itemName.startsWith("preview")) {
            chooseItem(menu, itemName);
        }
    }

    @Override
    public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
        if (!isSuitable(ceItem2)) {
            return ApplyReason.NOTHING;
        }

        if (list.isEmpty()) {
            return new ApplyReason("empty", ApplyResult.CANCEL);
        }

        int index = chooseIndex;
        L obj = null;
        if (index != -1) {
            if (index >= list.size()) {
                return ApplyReason.NOTHING;
            }
            obj = list.get(index);
            chooseIndex = -1;
        }

        ApplyReason reason = getApplyReason(ceItem1, ceItem2, obj);

        if (reason.getResult() == ApplyResult.SUCCESS) {
            List<L> newList = getList(ceItem1, ceItem2);
            this.list = newList;
            this.maxPage = Math.max(1, (int) Math.ceil(newList.size() / 5d));

            if (page > maxPage) {
                page = Math.max(1, page - 1);
            }
        }

        return reason;
    }

    @Override
    public void clearPreviews(CEAnvilCustomMenu menu) {
        int[] previewOrder = menu.getExtraData().getSettings().getPreviewIndexOrder();
        for (int previewNum : previewOrder) {
            menu.updateSlots("preview" + previewNum, null);
        }
        menu.updateSlots("next-page", null);
        menu.updateSlots("previous-page", null);
    }

    private void updateListDisplay(CEAnvilCustomMenu menu) {
        CEAnvilSettings settings = menu.getExtraData().getSettings();
        int[] previewOrder = settings.getPreviewIndexOrder();

        for (int i = 0; i < 5; i++) {
            int listIndex = i + (page - 1) * 5;
            int previewNum = previewOrder[i];

            if (listIndex >= list.size()) {
                menu.updateSlots("preview" + previewNum, null);
            } else {
                ItemStack displayItem = getDisplayItem(list.get(listIndex));

                if (listIndex == chooseIndex) {
                    ItemStackUtils.setGlowingItemStack(displayItem);
                }

                menu.updateSlots("preview" + previewNum, displayItem);
            }
        }
        updatePageIndicators(menu);
    }

    private void nextPage(CEAnvilCustomMenu menu) {
        if (page < maxPage) {
            page++;
            updateListDisplay(menu);
        }
    }

    private void previousPage(CEAnvilCustomMenu menu) {
        if (page > 1) {
            page--;
            updateListDisplay(menu);
        }
    }

    private void chooseItem(CEAnvilCustomMenu menu, String previewName) {
        int previewNum = Integer.parseInt(previewName.replace("preview", ""));
        int[] previewOrder = menu.getExtraData().getSettings().getPreviewIndexOrder();

        int localIndex = -1;
        for (int i = 0; i < previewOrder.length; i++) {
            if (previewOrder[i] == previewNum) {
                localIndex = i;
                break;
            }
        }

        if (localIndex == -1) {
            return;
        }

        chooseIndex = localIndex + (page - 1) * 5;
        updateListDisplay(menu);
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
