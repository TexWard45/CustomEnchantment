package com.bafmc.customenchantment.menu.anvil;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.removeenchant.CERemoveEnchant;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.menu.CEAnvilMenu;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Slot2CERemoveEnchantView extends AnvilSlot2ListView<Slot2CERemoveEnchantView, CEEnchantSimple> {

	public Slot2CERemoveEnchantView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public String getConfirmRemoveName() {
		return "confirm-remove-enchant";
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CERemoveEnchant;
	}

	@Override
	public ItemStack getDisplayItem(CEEnchantSimple ceEnchantSimple) {
		return CEAPI.getCEBookItemStack(ceEnchantSimple);
	}

	@Override
	public ApplyReason getApplyReason(CEItem ceItem1, CEItem ceItem2, CEEnchantSimple ceEnchantSimple) {
		if (!(ceItem2 instanceof CERemoveEnchant removeEnchant)) {
			return ApplyReason.NOTHING;
		}

		return removeEnchant.applyByMenuTo(ceItem1, ceEnchantSimple);
	}

	@Override
	public List<CEEnchantSimple> getList(CEItem ceItem1, CEItem ceItem2) {
		if (!(ceItem1 instanceof CEWeapon weapon)) {
			return new ArrayList<>();
		}

        if (!(ceItem2 instanceof CERemoveEnchant removeEnchant)) {
			return new ArrayList<>();
		}

        return removeEnchant.getRemoveEnchantList(weapon.getWeaponEnchant().getCESimpleList());
	}

	@Override
	public Slot2CERemoveEnchantView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CERemoveEnchantView(anvilMenu);
	}
}
