package com.bafmc.customenchantment.menu.anvil;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.menu.CEAnvilMenu;

public abstract class AnvilSlot2View<T extends AnvilSlot2View> {
	private CEAnvilMenu anvilMenu;

	public AnvilSlot2View(CEAnvilMenu anvilMenu) {
		this.anvilMenu = anvilMenu;
	}

	public abstract T instance(CEAnvilMenu anvilMenu);

	public abstract boolean isSuitable(CEItem ceItem);

	public abstract void updateView();
	
	public abstract void updateConfirm();

    public void updateAllPreviewNull() {}
	
	public void clickProcess(String name) {

	}

	public abstract ApplyReason apply(CEItem ceItem1, CEItem ceItem2);
	
	public CEAnvilMenu getAnvilMenu() {
		return anvilMenu;
	}
}
