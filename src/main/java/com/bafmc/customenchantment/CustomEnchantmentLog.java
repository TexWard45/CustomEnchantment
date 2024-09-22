package com.bafmc.customenchantment;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.database.Database;
import com.bafmc.customenchantment.item.ApplyReason;

public class CustomEnchantmentLog {
	public static void writeItemActionLogs(ApplyReason reason) {
		Database database = CustomEnchantment.instance().getDatabase();

		Player player = reason.getPlayer();
		String ceItem1 = reason.getCEItem1() != null ? reason.getCEItem1().getType() : null;
		String ceItem2 = reason.getCEItem2() != null ? reason.getCEItem2().getType() : null;

		database.insertLogs(player, ceItem1, ceItem2, reason.getResult().name(), reason.getData());
	}
}
