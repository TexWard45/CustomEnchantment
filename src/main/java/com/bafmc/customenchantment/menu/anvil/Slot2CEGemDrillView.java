package com.bafmc.customenchantment.menu.anvil;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrill;
import com.bafmc.customenchantment.menu.CEAnvilMenu;

public class Slot2CEGemDrillView extends AnvilSlot2View<Slot2CEGemDrillView> {

	public Slot2CEGemDrillView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public Slot2CEGemDrillView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CEGemDrillView(anvilMenu);
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CEGemDrill;
	}

	public void updateView() {
		CEAnvilMenu menu = getAnvilMenu();

		CEItem ceItem1 = menu.getItemData1().getCEItem();
		CEItem ceItem2 = menu.getItemData2().getCEItem();
		CEGemDrill gemDrill = (CEGemDrill) ceItem2;

		ApplyReason reason = gemDrill.testApplyByMenuTo(ceItem1);

		if (reason.getResult() == ApplyResult.SUCCESS) {
			menu.updateSlots("preview3", reason.getSource().exportTo());
		}else {
			menu.updateSlots("preview3", null);
		}
	}

	public void updateConfirm() {
		CEAnvilMenu menu = getAnvilMenu();

		menu.updateSlots("confirm", menu.getItemStack(null, "confirm-gem-drill"));
	}

	public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
		if (ceItem2 instanceof CEGemDrill gemDrill) {
            return gemDrill.applyByMenuTo(ceItem1);
		}
		return ApplyReason.NOTHING;
	}
}
