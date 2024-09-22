package com.bafmc.customenchantment.execute;

import java.util.List;

import com.bafmc.bukkit.feature.execute.ExecuteHook;
import org.bukkit.entity.Player;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEItemUsable;
import com.bafmc.bukkit.utils.StringUtils;

public class UseItemExecute extends ExecuteHook {
	public String getIdentify() {
		return "USE_ITEM";
	}

	public void execute(Player player, String value) {
		List<String> list = StringUtils.split(value, ":", 0);

		CEItem ceItem = CustomEnchantment.instance().getCEItemStorageMap().get(list.get(0))
				.getByParameter(new Parameter(list.subList(1, list.size())));

		if (ceItem == null || !(ceItem instanceof CEItemUsable)) {
			return;
		}

		CEItemUsable usable = (CEItemUsable) ceItem;
		usable.useBy(player);
	}
}
