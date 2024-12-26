package com.bafmc.customenchantment.menu.anvil;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.removeprotectdead.CERemoveProtectDead;

public class Slot2CERemoveProtectDeadView extends AnvilSlot2View<Slot2CERemoveProtectDeadView> {

	public Slot2CERemoveProtectDeadView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public Slot2CERemoveProtectDeadView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CERemoveProtectDeadView(anvilMenu);
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CERemoveProtectDead;
	}

	public void updateView() {
		CEAnvilMenu menu = getAnvilMenu();

		CEItem ceItem1 = menu.getItemData1().getCEItem();
		CEItem ceItem2 = menu.getItemData2().getCEItem();
		CERemoveProtectDead removeProtectDead = (CERemoveProtectDead) ceItem2;

		ApplyReason reason = removeProtectDead.testApplyByMenuTo(ceItem1);

		if (reason.getResult() == ApplyResult.SUCCESS) {
			menu.updateSlots("preview3", reason.getSource().exportTo());
			menu.updateSlots("preview4", removeProtectDead.getProtectDeadItem());
		}else {
			menu.updateSlots("preview3", null);
			menu.updateSlots("preview4", null);
		}
	}

	public void updateConfirm() {
		CEAnvilMenu menu = getAnvilMenu();

		menu.updateSlots("confirm", menu.getItemStack(null, "confirm-remove-protect-dead"));
	}

	public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
		if (ceItem2 instanceof CERemoveProtectDead) {
			CERemoveProtectDead removeProtectDead = (CERemoveProtectDead) ceItem2;

			return removeProtectDead.applyByMenuTo(ceItem1);
		}
		return ApplyReason.NOTHING;
	}
}
