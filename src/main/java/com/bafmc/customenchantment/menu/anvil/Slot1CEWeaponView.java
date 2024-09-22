package com.bafmc.customenchantment.menu.anvil;

import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.menu.CEAnvilMenu;

public class Slot1CEWeaponView extends AnvilSlot1View<Slot1CEWeaponView> {

	public Slot1CEWeaponView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public Slot1CEWeaponView instance(CEAnvilMenu anvilMenu) {
		return new Slot1CEWeaponView(anvilMenu);
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CEWeapon;
	}
	
}
