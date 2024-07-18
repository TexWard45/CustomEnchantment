package me.texward.customenchantment.menu.anvil;

import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.menu.CEAnvilMenu;

public abstract class AnvilSlot1View<T extends AnvilSlot1View> {
	private CEAnvilMenu anvilMenu;

	public AnvilSlot1View(CEAnvilMenu anvilMenu) {
		this.anvilMenu = anvilMenu;
	}

	public abstract T instance(CEAnvilMenu anvilMenu);

	public abstract boolean isSuitable(CEItem ceItem);
	
	public void clickProcess(String name) {
		
	}
	
	public CEAnvilMenu getAnvilMenu() {
		return anvilMenu;
	}
}
