package me.texward.customenchantment.api;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.texward.texwardlib.util.EquipSlot;

public class EquipSlotAPI {
	public static EquipSlot getBowShoowSlot(Player player) {
		ItemStack itemStack = player.getItemInHand();

		if (itemStack.getType() == Material.BOW) {
			return EquipSlot.MAINHAND;
		}

		return EquipSlot.OFFHAND;
	}
}
