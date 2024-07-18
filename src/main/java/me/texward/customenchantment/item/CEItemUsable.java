package me.texward.customenchantment.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class CEItemUsable<T extends CEItemData> extends CEItem<T>{

	public CEItemUsable(String type, ItemStack itemStack) {
		super(type, itemStack);
	}
	
	public abstract void useBy(Player player);
	
	public ApplyReason applyTo(CEItem ceItem) {
		return ApplyReason.NOTHING;
	}
}
