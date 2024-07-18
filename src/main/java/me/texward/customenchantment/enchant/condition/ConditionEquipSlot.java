package me.texward.customenchantment.enchant.condition;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;
import me.texward.texwardlib.util.EquipSlot;

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

		return current == equipSlotCompare;
	}

}
