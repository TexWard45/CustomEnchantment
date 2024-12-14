package com.bafmc.customenchantment.menu.anvil;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.book.CEBook;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.menu.CEAnvilMenu;

public class Slot2CEBookView extends AnvilSlot2View<Slot2CEBookView> {
	
	public Slot2CEBookView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public Slot2CEBookView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CEBookView(anvilMenu);
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CEBook;
	}

	public void updateView() {
		CEAnvilMenu menu = getAnvilMenu();
		
		CEItem ceItem1 = menu.getItemData1().getCEItem();
		CEItem ceItem2 = menu.getItemData2().getCEItem();
		CEBook book = (CEBook) ceItem2;

		ApplyReason reason = book.testApplyTo(ceItem1);
		
		if (reason.getResult() == ApplyResult.SUCCESS) {
			menu.updateSlots("preview3", reason.getSource().exportTo());
		}else {
			menu.updateSlots("preview3", null);
		}
	}

	public void updateConfirm() {
		CEAnvilMenu menu = getAnvilMenu();
		
		menu.updateSlots("confirm", menu.getItemStack(null, "confirm-book"));
	}
	
	public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
		if (ceItem2 instanceof CEBook) {
			CEBook book = (CEBook) ceItem2;

			return book.applyByMenuTo(ceItem1);
		}
		return ApplyReason.NOTHING;
	}
}
