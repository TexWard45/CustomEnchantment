package com.bafmc.customenchantment.menu.anvil;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.enchantpoint.CEEnchantPointSimple;
import com.bafmc.customenchantment.item.removeenchantpoint.CERemoveEnchantPoint;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Slot2CERemoveEnchantPointView extends AnvilSlot2ListView<Slot2CERemoveEnchantPointView, CEEnchantPointSimple> {

	public Slot2CERemoveEnchantPointView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public String getConfirmRemoveName() {
		return "confirm-remove-enchant-point";
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CERemoveEnchantPoint;
	}

	@Override
	public ItemStack getDisplayItem(CEEnchantPointSimple ceEnchantPointSimple) {
		CEItem ceItem = CEAPI.getCEItemByStorage(CEItemType.ENCHANT_POINT, ceEnchantPointSimple.getPattern());
		if (ceItem == null) {
			return null;
		}
		return ceItem.exportTo();
	}

	@Override
	public ApplyReason getApplyReason(CEItem ceItem1, CEItem ceItem2, CEEnchantPointSimple ceEnchantPointSimple) {
		if (!(ceItem2 instanceof CERemoveEnchantPoint removeEnchantPoint)) {
			return ApplyReason.NOTHING;
		}

		return removeEnchantPoint.applyByMenuTo(ceItem1, ceEnchantPointSimple);
	}

	@Override
	public List<CEEnchantPointSimple> getList(CEItem ceItem1, CEItem ceItem2) {
		if (!(ceItem1 instanceof CEWeapon weapon)) {
			return new ArrayList<>();
		}

		if (!(ceItem2 instanceof CERemoveEnchantPoint removeEnchantPoint)) {
			return new ArrayList<>();
		}

		return removeEnchantPoint.getList(weapon.getWeaponData().getExtraEnchantPointList());
	}

	@Override
	public Slot2CERemoveEnchantPointView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CERemoveEnchantPointView(anvilMenu);
	}
}
