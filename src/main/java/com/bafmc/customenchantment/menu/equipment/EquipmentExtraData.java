package com.bafmc.customenchantment.menu.equipment;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ExtraData;
import com.bafmc.bukkit.utils.EquipSlot;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class EquipmentExtraData extends ExtraData {

	private boolean inUpdateMenu = false;
	private boolean removed = false;
	private Map<EquipSlot, Long> lastClickTime = new HashMap<>();

	public void addLastClickTime(EquipSlot equipSlot, long time) {
		lastClickTime.put(equipSlot, time);
	}

	public boolean isInLastClickCooldown(EquipSlot equipSlot) {
		if (!lastClickTime.containsKey(equipSlot)) {
			return false;
		}
		long lastTime = lastClickTime.get(equipSlot);
		return System.currentTimeMillis() - lastTime < 500;
	}
}
