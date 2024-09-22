package com.bafmc.customenchantment.execute;

import java.util.List;

import com.bafmc.bukkit.feature.execute.ExecuteHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.utils.StringUtils;

public class GiveItemExecute extends ExecuteHook {
	public String getIdentify() {
		return "GIVE_ITEM";
	}

	public void execute(Player player, String value) {
		List<String> list = StringUtils.split(value, ":", 0);

		List<ItemStack> itemStacks = null;

		if (list.get(0).equals("storage")) {
			itemStacks = CEAPI.getVanillaItemStacks(list.get(1));
		} else {
			itemStacks = CustomEnchantment.instance().getCEItemStorageMap().get(list.get(0))
					.getItemStacksByParameter(new Parameter(list.subList(1, list.size())));
		}

		InventoryUtils.addItem(player, itemStacks);
	}
}
