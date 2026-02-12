package com.bafmc.customenchantment.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.banner.CEBanner;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.bukkit.command.CommandRegistrar;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.command.ArgumentType;

public class CommandDisableHelmet implements CommandRegistrar {

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("disablehelmet")
				.permission("customenchantment.disablehelmet.other")
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
							ceBanner.setHelmetEnable(false);
							player.setItemInHand(ceBanner.exportTo());
							return true;
						}
					})
				.end()
			.end();
	}
	
}