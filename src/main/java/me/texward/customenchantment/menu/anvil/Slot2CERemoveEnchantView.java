package me.texward.customenchantment.menu.anvil;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CESimple;
import me.texward.customenchantment.item.ApplyReason;
import me.texward.customenchantment.item.ApplyResult;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.item.CERemoveEnchant;
import me.texward.customenchantment.item.CEWeapon;
import me.texward.customenchantment.menu.CEAnvilMenu;
import me.texward.texwardlib.util.ItemStackUtils;

public class Slot2CERemoveEnchantView extends AnvilSlot2View<Slot2CERemoveEnchantView> {

	private RemoveEnchantData removeEnchantData = new RemoveEnchantData();
	
	public class RemoveEnchantData {
		private int page = 1;
		private int maxPage = 1;
		private int chooseIndex = -1;
		private List<CESimple> removeEnchantList = new ArrayList<CESimple>();

		public int getPage() {
			return page;
		}

		public void setPage(int page) {
			this.page = page;

            if (page < 1) {
                this.page = 1;
            }
		}

		public int getMaxPage() {
			return maxPage;
		}

		public void setMaxPage(int maxPage) {
			this.maxPage = maxPage;
		}

		public int getChooseIndex() {
			return chooseIndex;
		}

		public void setChooseIndex(int chooseIndex) {
			this.chooseIndex = chooseIndex;
		}

		public List<CESimple> getRemoveEnchantList() {
			return removeEnchantList;
		}

		public void setRemoveEnchantList(List<CESimple> removeEnchantList) {
			this.removeEnchantList = removeEnchantList;
		}
	}

	
	public Slot2CERemoveEnchantView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public Slot2CERemoveEnchantView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CERemoveEnchantView(anvilMenu);
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CERemoveEnchant;
	}

	public void updateView() {
		firstUpdateRemoveEnchant();
	}
	
	public void updateConfirm() {
		CEAnvilMenu menu = getAnvilMenu();
		
		menu.updateSlots("confirm", menu.getItemStack(null, "confirm-remove-enchant"));
	}
	
	public void firstUpdateRemoveEnchant() {
		CEAnvilMenu menu = getAnvilMenu();
		
		CEItem ceItem1 = menu.getItemData1().getCEItem();
		CEItem ceItem2 = menu.getItemData2().getCEItem();
		CERemoveEnchant removeEnchant = (CERemoveEnchant) ceItem2;

		CEWeapon weapon = (CEWeapon) ceItem1;
		List<CESimple> list = removeEnchant.getRemoveEnchantList(weapon.getWeaponEnchant().getCESimpleList());
		removeEnchantData.setRemoveEnchantList(list);
		removeEnchantData.setMaxPage((int) Math.ceil(list.size() / 5d));

		updateRemoveEnchant();
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

	public void updateRemoveEnchant() {
		int page = removeEnchantData.getPage();
		List<CESimple> list = removeEnchantData.getRemoveEnchantList();

		for (int i = 0 + (page - 1) * 5; i < (page - 1) * 5 + 5; i++) {
			if (i >= list.size()) {
				updateRemovePreview(i % 5, null);
			} else {
				ItemStack book = CEAPI.getCEBookItemStack(list.get(i));

				if (i == removeEnchantData.getChooseIndex()) {
					ItemStackUtils.setGlowingItemStack(book);
				}

				updateRemovePreview(i % 5, book);
			}
		}
		updatePagePreview();
	}
	
	public void nextPage() {
		int page = removeEnchantData.getPage();
		int maxPage = removeEnchantData.getMaxPage();
		if (page < maxPage) {
			removeEnchantData.setPage(page + 1);
			updateRemoveEnchant();
		}
	}

	public void previousPage() {
		int page = removeEnchantData.getPage();
		if (page > 1) {
			removeEnchantData.setPage(page - 1);
			updateRemoveEnchant();
		}
	}

	public void updatePagePreview() {
		updateNextPagePreview();
		updatePreviousPagePreview();
	}

	public void updateNextPagePreview() {
		CEAnvilMenu menu = getAnvilMenu();
		int page = removeEnchantData.getPage();
		int maxPage = removeEnchantData.getMaxPage();
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
		int page = removeEnchantData.getPage();
		if (page > 1) {
			ItemStack itemStack = menu.getItemStack(null, "has-previous-page");
			itemStack.setAmount(page - 1);
			menu.updateSlots("previous-page", itemStack);
		} else {
			menu.updateSlots("previous-page", null);
		}
	}

	public void chooseRemoveEnchant(String name) {
		int page = removeEnchantData.getPage();
		int slot = Integer.valueOf(name.replace("preview", ""));
		int index = slotToIndex(slot);

		removeEnchantData.setChooseIndex(index + (page - 1) * 5);
		updateRemoveEnchant();
	}
	
	public void clickProcess(String name) {
		if (name.equals("next-page")) {
			nextPage();
		}else if (name.equals("previous-page")) {
			previousPage();
		}else if (name.startsWith("preview")) {
			chooseRemoveEnchant(name);
		}
	}
	
	public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
		if (ceItem2 instanceof CERemoveEnchant) {
            CEWeapon weapon = (CEWeapon) ceItem1;
			CERemoveEnchant removeEnchant = (CERemoveEnchant) ceItem2;

			if (removeEnchantData.getRemoveEnchantList().isEmpty()) {
				return new ApplyReason("empty", ApplyResult.CANCEL);
			}
			
			int index = removeEnchantData.getChooseIndex();
			CESimple ceSimple = null;
			if (index != -1) {
                if (index >= removeEnchantData.getRemoveEnchantList().size()) {
                    return ApplyReason.NOTHING;
                }

				ceSimple = removeEnchantData.getRemoveEnchantList().get(index);
				removeEnchantData.setChooseIndex(-1);
			}

			ApplyReason reason = removeEnchant.applyByMenuTo(ceItem1, ceSimple);

            if (reason.getResult() == ApplyResult.SUCCESS) {
                List<CESimple> list = removeEnchant.getRemoveEnchantList(weapon.getWeaponEnchant().getCESimpleList());
                removeEnchantData.setRemoveEnchantList(list);
                removeEnchantData.setMaxPage((int) Math.ceil(list.size() / 5d));

                if (removeEnchantData.getPage() > removeEnchantData.getMaxPage()) {
                    removeEnchantData.setPage(removeEnchantData.getPage() - 1);
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
