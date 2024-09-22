package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;
import com.bafmc.bukkit.utils.EquipSlot;

public class ConditionEquipSlot extends ConditionHook {
	private EquipSlot equipSlotCompare;

	public String getIdentify() {
		return "EQUIP_SLOT";
	}

	public void setup(String[] args) {
		this.equipSlotCompare = EquipSlot.valueOf(args[0]);
	}

	@Override
	public boolean match(CEFunctionData data) {
		EquipSlot current = data.getEquipSlot();

		if (equipSlotCompare == EquipSlot.HAND && (current == EquipSlot.MAINHAND || current == EquipSlot.OFFHAND)) {
			return true;
		}

		if (equipSlotCompare == EquipSlot.ARMOR && (current == EquipSlot.HELMET || current == EquipSlot.CHESTPLATE
				|| current == EquipSlot.LEGGINGS || current == EquipSlot.BOOTS)) {
			return true;
		}

		if (equipSlotCompare == EquipSlot.HOTBAR && (current == EquipSlot.HOTBAR_1 || current == EquipSlot.HOTBAR_2
				|| current == EquipSlot.HOTBAR_3 || current == EquipSlot.HOTBAR_4 || current == EquipSlot.HOTBAR_5
				|| current == EquipSlot.HOTBAR_6 || current == EquipSlot.HOTBAR_7 || current == EquipSlot.HOTBAR_8
				|| current == EquipSlot.HOTBAR_9)) {
			return true;
		}

		return current == equipSlotCompare;
	}

}
