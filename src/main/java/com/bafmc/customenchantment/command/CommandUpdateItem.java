package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.*;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandUpdateItem implements AbstractCommand {
	
	private AdvancedCommandExecutor updateExecutor = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Player player = arg.getPlayer();
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

			player.setItemInHand(ceItem.exportTo());
			return true;
		}
	};
	
	@Override
	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("updateitem")
				.permission("customenchantment.updateitem.other")
				.subCommand(ArgumentType.PLAYER)
					.commandExecutor(updateExecutor)
				.end()
			.end();		
	}
	
}
