package com.bafmc.customenchantment.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.bukkit.command.AbstractCommand;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.command.ArgumentType;
import com.bafmc.bukkit.utils.MessageUtils;

public class CommandAdmin implements AbstractCommand {

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("admin")
				.permission("customenchantment.admin.target")
				.subCommand(ArgumentType.PLAYER)
					.subCommand("<toggle>")
						.commandExecutor(new AdvancedCommandExecutor() {
							public boolean onCommand(CommandSender sender, Argument arg) {
								Player player = Bukkit.getPlayer(arg.get(ArgumentType.PLAYER));
								if (player == null) {
									return true;
								}
		
								CEPlayer cePlayer = CEAPI.getCEPlayer((Player) sender);
		
								String toggle = arg.get("<toggle>");
								boolean isToggle = toggle.equalsIgnoreCase("on") || toggle.equalsIgnoreCase("true")
										|| toggle.equalsIgnoreCase("yes");
		
								cePlayer.setAdminMode(isToggle);
		
								if (isToggle) {
									MessageUtils.send(player, "&2ADMIN MODE: ON");
								} else {
									MessageUtils.send(player, "&2ADMIN MODE: OFF");
								}
								return true;
							}
						})
					.end()
				.end()
			.end();
	}
}
