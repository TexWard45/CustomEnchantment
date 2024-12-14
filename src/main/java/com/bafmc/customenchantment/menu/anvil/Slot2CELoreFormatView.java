package com.bafmc.customenchantment.menu.anvil;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.loreformat.CELoreFormat;
import com.bafmc.customenchantment.menu.CEAnvilMenu;

public class Slot2CELoreFormatView extends AnvilSlot2View<Slot2CELoreFormatView> {

	public Slot2CELoreFormatView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public Slot2CELoreFormatView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CELoreFormatView(anvilMenu);
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CELoreFormat;
	}

	public void updateView() {
		CEAnvilMenu menu = getAnvilMenu();

		CEItem ceItem1 = menu.getItemData1().getCEItem();
		CEItem ceItem2 = menu.getItemData2().getCEItem();
		CELoreFormat enchantPoint = (CELoreFormat) ceItem2;

		ApplyReason reason = enchantPoint.testApplyByMenuTo(ceItem1);

		if (reason.getResult() == ApplyResult.SUCCESS) {
			menu.updateSlots("preview3", reason.getSource().exportTo());
		}else {
			menu.updateSlots("preview3", null);
		}
	}

	public void updateConfirm() {
		CEAnvilMenu menu = getAnvilMenu();

		menu.updateSlots("confirm", menu.getItemStack(null, "confirm-lore-format"));
	}

	public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
		if (ceItem2 instanceof CELoreFormat) {
			CELoreFormat loreFormat = (CELoreFormat) ceItem2;

			return loreFormat.applyByMenuTo(ceItem1);
		}
		return ApplyReason.NOTHING;
	}
}
