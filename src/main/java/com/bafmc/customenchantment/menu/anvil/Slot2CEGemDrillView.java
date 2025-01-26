package com.bafmc.customenchantment.menu.anvil;

import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrill;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Slot2CEGemDrillView extends AnvilSlot2View<Slot2CEGemDrillView> {

	public Slot2CEGemDrillView(CEAnvilMenu anvilMenu) {
		super(anvilMenu);
	}

	public Slot2CEGemDrillView instance(CEAnvilMenu anvilMenu) {
		return new Slot2CEGemDrillView(anvilMenu);
	}

	public boolean isSuitable(CEItem ceItem) {
		return ceItem instanceof CEGemDrill;
	}

	public void updateView() {
		CEAnvilMenu menu = getAnvilMenu();

		CEItem ceItem1 = menu.getItemData1().getCEItem();
		CEItem ceItem2 = menu.getItemData2().getCEItem();
		CEGemDrill gemDrill = (CEGemDrill) ceItem2;

		ApplyReason reason = gemDrill.testApplyByMenuTo(ceItem1);

		if (reason.getResult() == ApplyResult.SUCCESS) {
			menu.updateSlots("preview3", reason.getSource().exportTo());
		}else {
			menu.updateSlots("preview3", null);
		}
	}

	public void updateConfirm() {
		CEAnvilMenu menu = getAnvilMenu();

		CEWeapon ceWeapon = (CEWeapon) getAnvilMenu().getItemData1().getCEItem();
		CEGemDrill gemDrill = (CEGemDrill) getAnvilMenu().getItemData2().getCEItem();

		WeaponGem weaponGem = ceWeapon.getWeaponGem();

		int drillSize = weaponGem.getDrillSize();
		if (drillSize >= gemDrill.getData().getConfigData().getMaxDrill()) {
			menu.updateSlots("confirm", menu.getItemStack(null, "confirm-gem-drill-max"));
		}else {
			int nextDrillSlot = drillSize + 1;

			boolean maxChance = true;
			Map<Integer, Double> slotChance = gemDrill.getData().getConfigData().getSlotChance();


			double chance = slotChance.getOrDefault(nextDrillSlot, 100.0);
			if (chance < 100) {
				maxChance = false;
			}

			if (chance < 100) {
				PlaceholderBuilder builder = PlaceholderBuilder.builder().put("{chance}", StringUtils.formatNumber(chance));

				ItemStack itemStack = menu.getItemStack(null, "confirm-gem-drill-with-chance");
				itemStack = ItemStackUtils.getItemStack(itemStack, builder.build());

				menu.updateSlots("confirm", itemStack);
			}

			if (maxChance) {
				menu.updateSlots("confirm", menu.getItemStack(null, "confirm-gem-drill"));
			}
		}
	}

	public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
		if (ceItem2 instanceof CEGemDrill gemDrill) {
            return gemDrill.applyByMenuTo(ceItem1);
		}
		return ApplyReason.NOTHING;
	}
}
