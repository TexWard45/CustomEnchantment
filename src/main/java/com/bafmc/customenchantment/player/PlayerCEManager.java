package com.bafmc.customenchantment.player;

import java.util.concurrent.ConcurrentHashMap;

import com.bafmc.bukkit.utils.EquipSlot;

public class PlayerCEManager extends CEPlayerExpansion {
	private ConcurrentHashMap<EquipSlot, CancelManager> map = new ConcurrentHashMap<EquipSlot, CancelManager>();

	public PlayerCEManager(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {
		for (EquipSlot equipSlot : EquipSlot.ALL_ARRAY) {
			map.put(equipSlot, new CancelManager());
		}
	}

	public void onQuit() {

	}

	public void setCancelSlot(EquipSlot slot, String unique, boolean cancel) {
		map.get(slot).setCancel(unique, cancel);
	}

	public boolean isCancelSlot(EquipSlot slot) {
		try {
			return map.get(slot).isCancel();
		} catch (Exception e) {
			System.out.println("Error at slot:" + slot);
			e.printStackTrace();
			return false;
		}
	}
}
