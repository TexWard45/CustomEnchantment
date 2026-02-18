package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.bafframework.custommenu.menu.builder.MenuOpener;
import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu;
import com.bafmc.customenchantment.menu.equipment.EquipmentExtraData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class EquipmentCommand implements AdvancedCommandExecutor {

	private final CustomEnchantment plugin;

	public EquipmentCommand(CustomEnchantment plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Argument arg) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) {
			player.closeInventory();
		}

		EquipmentExtraData extraData = new EquipmentExtraData();

		MenuOpener.builder()
				.player(player)
				.menuData(plugin, EquipmentCustomMenu.MENU_NAME)
				.extraData(extraData)
				.async(false)
				.build();

		return true;
	}
}
