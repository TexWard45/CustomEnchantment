package com.bafmc.customenchantment.enchant.condition;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;
import com.bafmc.bukkit.utils.StringUtils;

public class ConditionHold extends ConditionHook {
	private MaterialList list;

	public String getIdentify() {
		return "HOLD";
	}

	public void setup(String[] args) {
		this.list = MaterialList.getMaterialList(StringUtils.split(args[0], ",", 0));
	}

	@Override
	public boolean match(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return false;
		}
		
		ItemStack item = player.getPlayer().getItemInHand();

		return list.contains(new MaterialData(item));
	}

}