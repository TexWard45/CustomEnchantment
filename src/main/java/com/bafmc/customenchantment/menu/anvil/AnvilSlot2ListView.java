package com.bafmc.customenchantment.menu.anvil;

import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.CEItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class AnvilSlot2ListView<T extends AnvilSlot2View, L> extends AnvilSlot2View<T>{

    @Getter
    @Setter
    public class Data {
        private int page = 1;
        private int maxPage = 1;
        private int chooseIndex = -1;
        private List<L> list = new ArrayList<>();

        public void setPage(int page) {
            this.page = Math.max(page, 1);
        }
    }

    private Data data = new Data();

    public AnvilSlot2ListView(CEAnvilMenu anvilMenu) {
        super(anvilMenu);
    }

    public abstract ItemStack getDisplayItem(L object);

    public abstract ApplyReason getApplyReason(CEItem ceItem1, CEItem ceItem2, L object);

    public abstract List<L> getList(CEItem ceItem1, CEItem ceItem2);

    public abstract String getConfirmRemoveName();

    public void updateView() {
        firstUpdateRemove();
    }

    public void updateConfirm() {
        CEAnvilMenu menu = getAnvilMenu();

        menu.updateSlots("confirm", menu.getItemStack(null, getConfirmRemoveName()));
    }

    public void firstUpdateRemove() {
        CEAnvilMenu menu = getAnvilMenu();

        CEItem ceItem1 = menu.getItemData1().getCEItem();
        CEItem ceItem2 = menu.getItemData2().getCEItem();

        List<L> list = getList(ceItem1, ceItem2);
        data.setList(list);
        data.setMaxPage((int) Math.ceil(list.size() / 5d));

        updateRemove();
    }

    public void updateRemovePreview(int index, ItemStack itemStack) {
        CEAnvilMenu menu = getAnvilMenu();

        int slot = indexToSlot(index);
        menu.updateSlots("preview" + slot, itemStack);
    }

    public int indexToSlot(int index) {
        switch (index) {
            case 0:
                return 3;
            case 1:
                return 2;
            case 2:
                return 4;
            case 3:
                return 1;
            case 4:
                return 5;
        }
        return 0;
    }

    public int slotToIndex(int slot) {
        switch (slot) {
            case 1:
                return 3;
            case 2:
                return 1;
            case 3:
                return 0;
            case 4:
                return 2;
            case 5:
                return 4;
        }
        return -1;
    }

    public void updateRemove() {
        int page = data.getPage();
        List<L> list = data.getList();

        for (int i = (page - 1) * 5; i < (page - 1) * 5 + 5; i++) {
            if (i >= list.size()) {
                updateRemovePreview(i % 5, null);
            } else {
                ItemStack book = getDisplayItem(list.get(i));

                if (i == data.getChooseIndex()) {
                    ItemStackUtils.setGlowingItemStack(book);
                }

                updateRemovePreview(i % 5, book);
            }
        }
        updatePagePreview();
    }

    public void nextPage() {
        int page = data.getPage();
        int maxPage = data.getMaxPage();
        if (page < maxPage) {
            data.setPage(page + 1);
            updateRemove();
        }
    }

    public void previousPage() {
        int page = data.getPage();
        if (page > 1) {
            data.setPage(page - 1);
            updateRemove();
        }
    }

    public void updatePagePreview() {
        updateNextPagePreview();
        updatePreviousPagePreview();
    }

    public void updateNextPagePreview() {
        CEAnvilMenu menu = getAnvilMenu();
        int page = data.getPage();
        int maxPage = data.getMaxPage();
        if (page < maxPage) {
            ItemStack itemStack = menu.getItemStack(null, "has-next-page");
            itemStack.setAmount(page + 1);
            menu.updateSlots("next-page", itemStack);
        } else {
            menu.updateSlots("next-page", null);
        }
    }

    public void updatePreviousPagePreview() {
        CEAnvilMenu menu = getAnvilMenu();
        int page = data.getPage();
        if (page > 1) {
            ItemStack itemStack = menu.getItemStack(null, "has-previous-page");
            itemStack.setAmount(page - 1);
            menu.updateSlots("previous-page", itemStack);
        } else {
            menu.updateSlots("previous-page", null);
        }
    }

    public void chooseRemove(String name) {
        int page = data.getPage();
        int slot = Integer.valueOf(name.replace("preview", ""));
        int index = slotToIndex(slot);

        data.setChooseIndex(index + (page - 1) * 5);
        updateRemove();
    }

    public void clickProcess(String name) {
        if (name.equals("next-page")) {
            nextPage();
        }else if (name.equals("previous-page")) {
            previousPage();
        }else if (name.startsWith("preview")) {
            chooseRemove(name);
        }
    }

    public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
        if (isSuitable(ceItem2)) {
            if (data.getList().isEmpty()) {
                return new ApplyReason("empty", ApplyResult.CANCEL);
            }

            int index = data.getChooseIndex();
            L obj = null;
            if (index != -1) {
                if (index >= data.getList().size()) {
                    return ApplyReason.NOTHING;
                }

                obj = data.getList().get(index);
                data.setChooseIndex(-1);
            }

            ApplyReason reason = getApplyReason(ceItem1, ceItem2, obj);

            if (reason.getResult() == ApplyResult.SUCCESS) {
                List<L> list = getList(ceItem1, ceItem2);
                data.setList(list);
                data.setMaxPage((int) Math.ceil(list.size() / 5d));

                if (data.getPage() > data.getMaxPage()) {
                    data.setPage(data.getPage() - 1);
                }
            }

            return reason;
        }
        return ApplyReason.NOTHING;
    }

    public void updateAllPreviewNull() {
        for (int i = 0; i < 5; i++) {
            updateRemovePreview(i, null);
        }

        getAnvilMenu().updateSlots("next-page", null);
        getAnvilMenu().updateSlots("previous-page", null);
    }

}
