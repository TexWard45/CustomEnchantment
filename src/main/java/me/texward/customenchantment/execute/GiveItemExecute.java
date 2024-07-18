package me.texward.customenchantment.execute;

import java.util.List;

import com._3fmc.bukkit.feature.execute.ExecuteHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.api.Parameter;
import me.texward.texwardlib.util.InventoryUtils;
import me.texward.texwardlib.util.StringUtils;

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
