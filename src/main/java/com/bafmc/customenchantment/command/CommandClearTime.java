package com.bafmc.customenchantment.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.bukkit.command.AbstractCommand;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.command.ArgumentType;

public class CommandClearTime implements AbstractCommand {

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("cleartime")
				.permission("customenchantment.cleartime.other")
				.subCommand(ArgumentType.PLAYER)
					.commandExecutor(new AdvancedCommandExecutor() {
						public boolean onCommand(CommandSender sender, Argument arg) {
							Player player = Bukkit.getPlayer(arg.get(ArgumentType.PLAYER));
							if (player == null) {
								return true;
							}

							ItemStack itemStack = player.getInventory().getItemInHand();
							if (itemStack == null) {
								return true;
							}

							CEItem ceItem = CEAPI.getCEItem(itemStack);
							if (ceItem == null) {
								return true;
							}

							if (!(ceItem instanceof CEWeaponAbstract)) {
								return true;
							}

							CEWeaponAbstract weapon = (CEWeaponAbstract) ceItem;
							weapon.clearTimeModifier();
							player.setItemInHand(weapon.exportTo());
							return true;
						}
					})
				.end()
			.end();
	}
	
}