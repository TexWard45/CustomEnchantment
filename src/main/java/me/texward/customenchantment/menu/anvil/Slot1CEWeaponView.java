package me.texward.customenchantment.menu.anvil;

import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.item.CEWeapon;
import me.texward.customenchantment.menu.CEAnvilMenu;

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
