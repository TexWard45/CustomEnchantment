package com.bafmc.customenchantment.execute;

import ca.tweetzy.vouchers.Vouchers;
import ca.tweetzy.vouchers.api.voucher.Voucher;
import com.bafmc.bukkit.feature.execute.ExecuteHook;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GiveVoucherItemExecute extends ExecuteHook {
	public String getIdentify() {
		return "GIVE_VOUCHER_ITEM";
	}

	public void execute(Player player, String value) {
		List<String> list = StringUtils.split(value, ":", 0);

		String id = list.get(0);
		Voucher voucherFound = Vouchers.getVoucherManager().find(id);
		if (voucherFound == null) {
			return;
		}

		int amount = 1;
		if (list.size() > 1) {
			amount = Integer.valueOf(list.get(1));
		}

		ItemStack item = voucherFound.buildItem(player);
		InventoryUtils.addItem(player, ItemStackUtils.getItemStacks(item, amount));
	}
}
