package com.bafmc.customenchantment.execute;

import java.util.Arrays;
import java.util.List;

import com.bafmc.bukkit.feature.execute.ExecuteHook;
import org.bukkit.entity.Player;

import com.badbones69.vouchers.api.CrazyManager;
import com.badbones69.vouchers.api.objects.Voucher;

import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.utils.StringUtils;

public class GiveVoucherItemExecute extends ExecuteHook {
	public String getIdentify() {
		return "GIVE_VOUCHER_ITEM";
	}

	public void execute(Player player, String value) {
		List<String> list = StringUtils.split(value, ":", 0);

		Voucher voucher = CrazyManager.getVoucher(list.get(0));
		if (voucher == null) {
			return;
		}

		int amount = 1;
		if (list.size() > 1) {
			amount = Integer.valueOf(list.get(1));
		}
		
		InventoryUtils.addItem(player, Arrays.asList(voucher.buildItem(amount)));
	}
}
