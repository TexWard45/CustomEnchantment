package me.texward.customenchantment.enchant.condition;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.api.MaterialData;
import me.texward.customenchantment.api.MaterialList;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;
import me.texward.texwardlib.util.StringUtils;

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