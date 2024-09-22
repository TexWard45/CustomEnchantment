package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.*;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEBanner;
import com.bafmc.customenchantment.item.CEItem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandEnableHelmet implements AbstractCommand {

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("enablehelmet")
				.permission("customenchantment.enablehelmet.other")
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
							if (!(ceItem instanceof CEBanner)) {
								return true;
							}

							CEBanner ceBanner = new CEBanner(itemStack);
							ceBanner.setHelmetEnable(true);
							player.setItemInHand(ceBanner.exportTo());
							return true;
						}
					})
				.end()
			.end();
	}
	
}