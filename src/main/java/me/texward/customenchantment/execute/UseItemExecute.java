package me.texward.customenchantment.execute;

import java.util.List;

import com._3fmc.bukkit.feature.execute.ExecuteHook;
import org.bukkit.entity.Player;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.Parameter;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.item.CEItemUsable;
import me.texward.texwardlib.util.StringUtils;

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
