package me.texward.customenchantment.menu.anvil;

import me.texward.customenchantment.item.ApplyReason;
import me.texward.customenchantment.item.ApplyResult;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.item.CERemoveEnchantPoint;
import me.texward.customenchantment.menu.CEAnvilMenu;

public class Slot2CERemoveEnchantPointView extends AnvilSlot2View<Slot2CERemoveEnchantPointView> {

	public Slot2CERemoveEnchantPointView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public Slot2CERemoveEnchantPointView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CERemoveEnchantPointView(anvilMenu);
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CERemoveEnchantPoint;
	}

	public void updateView() {
		CEAnvilMenu menu = getAnvilMenu();

		CEItem ceItem1 = menu.getItemData1().getCEItem();
		CEItem ceItem2 = menu.getItemData2().getCEItem();
		CERemoveEnchantPoint removeEnchantPoint = (CERemoveEnchantPoint) ceItem2;

		ApplyReason reason = removeEnchantPoint.testApplyByMenuTo(ceItem1);

		if (reason.getResult() == ApplyResult.SUCCESS) {
			menu.updateSlots("preview3", reason.getSource().exportTo());
			menu.updateSlots("preview4", removeEnchantPoint.getEnchantPointItem(ceItem1));
		}else {
			menu.updateSlots("preview3", null);
			menu.updateSlots("preview4", null);
		}
	}

	public void updateConfirm() {
		CEAnvilMenu menu = getAnvilMenu();

		menu.updateSlots("confirm", menu.getItemStack(null, "confirm-remove-enchant-point"));
	}

	public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
		if (ceItem2 instanceof CERemoveEnchantPoint) {
			CERemoveEnchantPoint removeEnchantPoint = (CERemoveEnchantPoint) ceItem2;

			return removeEnchantPoint.applyByMenuTo(ceItem1);
		}
		return ApplyReason.NOTHING;
	}
}
