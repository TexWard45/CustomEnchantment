package com.bafmc.customenchantment.menu.anvil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bafmc.bukkit.bafframework.utils.EnchantmentUtils;
import com.bafmc.bukkit.utils.NumberUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.eraseenchant.CEEraseEnchant;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.menu.CEAnvilMenu;
import com.bafmc.bukkit.utils.ColorUtils;
import com.bafmc.bukkit.utils.ItemStackUtils;

public class Slot2CEEraseEnchantView extends AnvilSlot2View<Slot2CEEraseEnchantView> {

	private EraseEnchantData eraseEnchantData = new EraseEnchantData();

	public class EraseEnchantData {
		private int page = 1;
		private int maxPage = 1;
		private int chooseIndex = -1;
		private List<Enchantment> removeEnchantList = new ArrayList<Enchantment>();
		private Map<Enchantment, Integer> enchantmentMap = new HashMap<Enchantment, Integer>();

		public int getPage() {
			return page;
		}

		public void setPage(int page) {
			this.page = page;
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

		public List<Enchantment> getEraseEnchantList() {
			return removeEnchantList;
		}

		public void setEraseEnchantList(List<Enchantment> removeEnchantList) {
			this.removeEnchantList = removeEnchantList;
		}

		public Map<Enchantment, Integer> getEnchantmentMap() {
			return enchantmentMap;
		}

		public void setEnchantmentMap(Map<Enchantment, Integer> enchantmentMap) {
			this.enchantmentMap = enchantmentMap;
		}
	}

	public Slot2CEEraseEnchantView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public Slot2CEEraseEnchantView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CEEraseEnchantView(anvilMenu);
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CEEraseEnchant;
	}

	public void updateView() {
		firstUpdateEraseEnchant();
	}

	public void updateConfirm() {
		CEAnvilMenu menu = getAnvilMenu();

		menu.updateSlots("confirm", menu.getItemStack(null, "confirm-remove-enchant"));
	}

	public void firstUpdateEraseEnchant() {
		CEAnvilMenu menu = getAnvilMenu();

		CEItem ceItem1 = menu.getItemData1().getCEItem();
		CEItem ceItem2 = menu.getItemData2().getCEItem();
		CEEraseEnchant removeEnchant = (CEEraseEnchant) ceItem2;

		CEWeapon weapon = (CEWeapon) ceItem1;
		List<Enchantment> list = eraseEnchantData.getEraseEnchantList();
		Map<Enchantment, Integer> enchantMap = weapon.getDefaultItemStack().getEnchantments();
		eraseEnchantData.setEnchantmentMap(enchantMap);
		eraseEnchantData.setEraseEnchantList(removeEnchant.getEraseEnchantList(enchantMap));
		eraseEnchantData.setMaxPage((int) Math.ceil(list.size() / 5d));
		updateEraseEnchant();
	}

	public void updateErasePreview(int index, ItemStack itemStack) {
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

	public void updateEraseEnchant() {
		int page = eraseEnchantData.getPage();
		List<Enchantment> list = eraseEnchantData.getEraseEnchantList();

		for (int i = 0 + (page - 1) * 5; i < (page - 1) * 5 + 5; i++) {
			if (i >= list.size()) {
				updateErasePreview(i % 5, null);
			} else {
				Enchantment enchantment = list.get(i);
				int level = eraseEnchantData.getEnchantmentMap().get(enchantment);

				ItemStack book = getEnchantBook(enchantment, level);

				if (i == eraseEnchantData.getChooseIndex()) {
					ItemStackUtils.setGlowingItemStack(book);
				}

				updateErasePreview(i % 5, book);
			}
		}
		updatePagePreview();
	}

	public ItemStack getEnchantBook(Enchantment enchantment, int level) {
		ItemStack itemStack = new ItemStack(Material.BOOK);
		ItemMeta meta = itemStack.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add("&7" + EnchantmentUtils.getDisplayName(enchantment) + " " + NumberUtils.toRomanNumber(level));
		meta.setLore(ColorUtils.t(lore));
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public void nextPage() {
		int page = eraseEnchantData.getPage();
		int maxPage = eraseEnchantData.getMaxPage();
		if (page < maxPage) {
			eraseEnchantData.setPage(page + 1);
			updateEraseEnchant();
		}
	}

	public void previousPage() {
		int page = eraseEnchantData.getPage();
		if (page > 1) {
			eraseEnchantData.setPage(page - 1);
			updateEraseEnchant();
		}
	}

	public void updatePagePreview() {
		updateNextPagePreview();
		updatePreviousPagePreview();
	}

	public void updateNextPagePreview() {
		CEAnvilMenu menu = getAnvilMenu();
		int page = eraseEnchantData.getPage();
		int maxPage = eraseEnchantData.getMaxPage();
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
		int page = eraseEnchantData.getPage();
		if (page > 1) {
			ItemStack itemStack = menu.getItemStack(null, "has-previous-page");
			itemStack.setAmount(page - 1);
			menu.updateSlots("previous-page", itemStack);
		} else {
			menu.updateSlots("previous-page", null);
		}
	}

	public void chooseEraseEnchant(String name) {
		int page = eraseEnchantData.getPage();
		int slot = Integer.valueOf(name.replace("preview", ""));
		int index = slotToIndex(slot);

		eraseEnchantData.setChooseIndex(index + (page - 1) * 5);
		updateEraseEnchant();
	}

	public void clickProcess(String name) {
		if (name.equals("next-page")) {
			nextPage();
		} else if (name.equals("previous-page")) {
			previousPage();
		} else if (name.startsWith("preview")) {
			chooseEraseEnchant(name);
		}
	}

	public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
		if (ceItem2 instanceof CEEraseEnchant) {
			CEEraseEnchant removeEnchant = (CEEraseEnchant) ceItem2;

			if (eraseEnchantData.getEnchantmentMap().isEmpty()) {
				return new ApplyReason("empty", ApplyResult.CANCEL);
			}
			
			int index = eraseEnchantData.getChooseIndex();
			Enchantment enchant = null;
			if (index != -1) {
				enchant = eraseEnchantData.getEraseEnchantList().get(index);
				eraseEnchantData.setChooseIndex(-1);
			}

			return removeEnchant.applyByMenuTo(ceItem1, enchant);
		}
		return ApplyReason.NOTHING;
	}
}
