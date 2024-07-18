package me.texward.customenchantment.menu.anvil;

import me.texward.customenchantment.item.ApplyReason;
import me.texward.customenchantment.item.ApplyResult;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.item.CEProtectDestroy;
import me.texward.customenchantment.menu.CEAnvilMenu;

public class Slot2CEProtectDestroyView extends AnvilSlot2View<Slot2CEProtectDestroyView> {

	public Slot2CEProtectDestroyView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public Slot2CEProtectDestroyView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CEProtectDestroyView(anvilMenu);
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CEProtectDestroy;
	}

	public void updateView() {
		CEAnvilMenu menu = getAnvilMenu();

		CEItem ceItem1 = menu.getItemData1().getCEItem();
		CEItem ceItem2 = menu.getItemData2().getCEItem();
		CEProtectDestroy enchantPoint = (CEProtectDestroy) ceItem2;

		ApplyReason reason = enchantPoint.testApplyByMenuTo(ceItem1);

		if (reason.getResult() == ApplyResult.SUCCESS) {
			menu.updateSlots("preview3", reason.getSource().exportTo());
		}else {
			menu.updateSlots("preview3", null);
		}
	}

	public void updateConfirm() {
		CEAnvilMenu menu = getAnvilMenu();

		menu.updateSlots("confirm", menu.getItemStack(null, "confirm-protect-destroy"));
	}

	public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
		if (ceItem2 instanceof CEProtectDestroy) {
			CEProtectDestroy enchantPoint = (CEProtectDestroy) ceItem2;

			return enchantPoint.applyByMenuTo(ceItem1);
		}
		return ApplyReason.NOTHING;
	}
}
