package com.bafmc.customenchantment.menu.anvil;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.enchantpoint.CEEnchantPoint;
import com.bafmc.customenchantment.item.CEItem;

public class Slot2CEEnchantPointView extends AnvilSlot2View<Slot2CEEnchantPointView> {

	public Slot2CEEnchantPointView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public Slot2CEEnchantPointView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CEEnchantPointView(anvilMenu);
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CEEnchantPoint;
	}

	public void updateView() {
		CEAnvilMenu menu = getAnvilMenu();

		CEItem ceItem1 = menu.getItemData1().getCEItem();
		CEItem ceItem2 = menu.getItemData2().getCEItem();
		CEEnchantPoint enchantPoint = (CEEnchantPoint) ceItem2;

		ApplyReason reason = enchantPoint.testApplyByMenuTo(ceItem1);

		if (reason.getResult() == ApplyResult.SUCCESS) {
			menu.updateSlots("preview3", reason.getSource().exportTo());
		}else {
			menu.updateSlots("preview3", null);
		}
	}

	public void updateConfirm() {
		CEAnvilMenu menu = getAnvilMenu();

		menu.updateSlots("confirm", menu.getItemStack(null, "confirm-enchant-point"));
	}

	public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
		if (ceItem2 instanceof CEEnchantPoint) {
			CEEnchantPoint enchantPoint = (CEEnchantPoint) ceItem2;

			return enchantPoint.applyByMenuTo(ceItem1);
		}
		return ApplyReason.NOTHING;
	}
}
