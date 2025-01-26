package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.bukkit.utils.EquipSlot;

public class EffectDeactiveEquipSlot extends EffectHook {
	private EquipSlot slot;
	private String name;
	private long duration;

	public String getIdentify() {
		return "DEACTIVE_EQUIP_SLOT";
	}

	public void setup(String[] args) {
		this.slot = EquipSlot.valueOf(args[0]);
		this.name = args[1];
		if (args.length > 2) {
			this.duration = Long.parseLong(args[2]);
		}
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		cePlayer.getCEManager().setCancelSlot(slot, name, true, duration);
	}
}
