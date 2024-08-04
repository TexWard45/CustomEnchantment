package me.texward.customenchantment.menu.anvil;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CESimple;
import me.texward.customenchantment.item.ApplyReason;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.item.CEWeapon;
import me.texward.customenchantment.menu.CEAnvilMenu;
import me.texward.texwardlib.configuration.AdvancedConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Slot2CEDefaultView extends AnvilSlot2View<Slot2CEDefaultView> {

	private EnchantData enchantData = new EnchantData();

	public class EnchantData {
		private int page = 1;
		private int maxPage = 1;
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

		public List<CESimple> getRemoveEnchantList() {
			return removeEnchantList;
		}

		public void setEnchantList(List<CESimple> removeEnchantList) {
			this.removeEnchantList = removeEnchantList;
		}
	}


	public Slot2CEDefaultView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public Slot2CEDefaultView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CEDefaultView(anvilMenu);
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem == null;
	}

	public void updateView() {
		firstUpdateEnchant();
	}
	
	public void updateConfirm() {
		CEAnvilMenu menu = getAnvilMenu();
		
		menu.updateSlots("confirm", menu.getItemStack(null, "confirm-remove-enchant"));
	}
	
	public void firstUpdateEnchant() {
		CEAnvilMenu menu = getAnvilMenu();
        AdvancedConfigurationSection dataConfig = menu.getMenuView().getCMenu().getDataConfig();

		CEItem ceItem1 = menu.getItemData1().getCEItem();

		CEWeapon weapon = (CEWeapon) ceItem1;
		List<CESimple> list = getEnchantList(weapon.getWeaponEnchant().getCESimpleList(), dataConfig.getStringList("default-view.enchant-group"));

		enchantData.setEnchantList(list);
		enchantData.setMaxPage((int) Math.ceil(list.size() / 5d));

		updateEnchant();
	}

    public List<CESimple> getEnchantList(List<CESimple> list, List<String> groups) {
        List<CESimple> newList = new ArrayList<CESimple>();

        for (CESimple ceSimple : list) {
            if (groups.contains(ceSimple.getCEEnchant().getGroupName())) {
                newList.add(ceSimple);
            }
        }

        return newList;
    }

	public void updatePreview(int index, ItemStack itemStack) {
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

	public void updateEnchant() {
		int page = enchantData.getPage();
		List<CESimple> list = enchantData.getRemoveEnchantList();

		for (int i = 0 + (page - 1) * 5; i < (page - 1) * 5 + 5; i++) {
			if (i >= list.size()) {
				updatePreview(i % 5, null);
			} else {
				ItemStack book = CEAPI.getCEBookItemStack(list.get(i));

				updatePreview(i % 5, book);
			}
		}
		updatePagePreview();
	}
	
	public void nextPage() {
		int page = enchantData.getPage();
		int maxPage = enchantData.getMaxPage();
		if (page < maxPage) {
			enchantData.setPage(page + 1);
			updateEnchant();
		}
	}

	public void previousPage() {
		int page = enchantData.getPage();
		if (page > 1) {
			enchantData.setPage(page - 1);
			updateEnchant();
		}
	}

	public void updatePagePreview() {
		updateNextPagePreview();
		updatePreviousPagePreview();
	}

	public void updateNextPagePreview() {
		CEAnvilMenu menu = getAnvilMenu();
		int page = enchantData.getPage();
		int maxPage = enchantData.getMaxPage();
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
		int page = enchantData.getPage();
		if (page > 1) {
			ItemStack itemStack = menu.getItemStack(null, "has-previous-page");
			itemStack.setAmount(page - 1);
			menu.updateSlots("previous-page", itemStack);
		} else {
			menu.updateSlots("previous-page", null);
		}
	}

	public void chooseRemoveEnchant(String name) {
		updateEnchant();
	}
	
	public void clickProcess(String name) {
		if (name.equals("next-page")) {
			nextPage();
		}else if (name.equals("previous-page")) {
			previousPage();
		}
	}
	
	public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
		return ApplyReason.NOTHING;
	}

    public void updateAllPreviewNull() {
        for (int i = 0; i < 5; i++) {
            updatePreview(i, null);
        }

        getAnvilMenu().updateSlots("next-page", null);
        getAnvilMenu().updateSlots("previous-page", null);
    }
}
