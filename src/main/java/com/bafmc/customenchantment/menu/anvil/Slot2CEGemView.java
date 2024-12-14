package com.bafmc.customenchantment.menu.anvil;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.gem.CEGem;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.menu.CEAnvilMenu;

public class Slot2CEGemView extends AnvilSlot2View<Slot2CEGemView> {

	public Slot2CEGemView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public Slot2CEGemView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CEGemView(anvilMenu);
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CEGem;
	}

	public void updateView() {
		CEAnvilMenu menu = getAnvilMenu();

		CEItem ceItem1 = menu.getItemData1().getCEItem();
		CEItem ceItem2 = menu.getItemData2().getCEItem();
		CEGem gem = (CEGem) ceItem2;

		ApplyReason reason = gem.testApplyByMenuTo(ceItem1);

		if (reason.getResult() == ApplyResult.SUCCESS) {
			menu.updateSlots("preview3", reason.getSource().exportTo());
		}else {
			menu.updateSlots("preview3", null);
		}
	}

	public void updateConfirm() {
		CEAnvilMenu menu = getAnvilMenu();

		menu.updateSlots("confirm", menu.getItemStack(null, "confirm-gem"));
	}

	public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
		if (ceItem2 instanceof CEGem gem) {
            return gem.applyByMenuTo(ceItem1);
		}
		return ApplyReason.NOTHING;
	}
}
