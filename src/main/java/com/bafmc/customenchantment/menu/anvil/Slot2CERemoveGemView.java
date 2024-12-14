package com.bafmc.customenchantment.menu.anvil;

import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.item.gem.CEGem;
import com.bafmc.customenchantment.item.gem.CEGemSimple;
import com.bafmc.customenchantment.item.removegem.CERemoveGem;
import com.bafmc.customenchantment.menu.CEAnvilMenu;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Slot2CERemoveGemView extends AnvilSlot2ListView<Slot2CERemoveGemView, CEGemSimple> {

	public Slot2CERemoveGemView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public String getConfirmRemoveName() {
		return "confirm-remove-gem";
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CERemoveGem;
	}

	@Override
	public ItemStack getDisplayItem(CEGemSimple ceGemSimple) {
		CEGem ceGem = ceGemSimple.getCEGem();
		if (ceGem == null) {
			return null;
		}
		ceGem.getData().setLevel(ceGemSimple.getLevel());
		return ceGem.exportTo();
	}

	@Override
	public ApplyReason getApplyReason(CEItem ceItem1, CEItem ceItem2, CEGemSimple ceGemSimple) {
		if (!(ceItem2 instanceof CERemoveGem removeGem)) {
			return ApplyReason.NOTHING;
		}

		return removeGem.applyByMenuTo(ceItem1, ceGemSimple);
	}

	@Override
	public List<CEGemSimple> getList(CEItem ceItem1, CEItem ceItem2) {
		if (!(ceItem1 instanceof CEWeapon weapon)) {
			return new ArrayList<>();
		}

		if (!(ceItem2 instanceof CERemoveGem removeGem)) {
			return new ArrayList<>();
		}

		List<CEGemSimple> list = weapon.getWeaponGem().getCEGemSimpleList();
		List<CEGemSimple> result = new ArrayList<>();

		int index = 0;

		for (CEGemSimple ceGemSimple : list) {
			CEGemSimple newCEGemSimple = ceGemSimple.clone();
			newCEGemSimple.setIndex(index);
			result.add(newCEGemSimple);
			index++;
		}

		return result;
	}

	@Override
	public Slot2CERemoveGemView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CERemoveGemView(anvilMenu);
	}
}
