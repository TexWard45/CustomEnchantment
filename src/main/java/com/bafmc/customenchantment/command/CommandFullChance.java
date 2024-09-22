package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.*;
import com.bafmc.bukkit.utils.MessageUtils;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFullChance implements AbstractCommand {
	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("fullchance")
				.permission("customenchantment.command.fullchance")
				.subCommand(ArgumentType.PLAYER)
					.subCommand("<toggle>")
						.commandExecutor(new AdvancedCommandExecutor() {
							public boolean onCommand(CommandSender sender, Argument arg) {
								Player player = Bukkit.getPlayer(arg.get(ArgumentType.PLAYER));
								if (player == null) {
									return true;
								}

								CEPlayer cePlayer = CEAPI.getCEPlayer(player);

								String toggle = arg.get("<toggle>");
								boolean isToggle = toggle.equalsIgnoreCase("on") || toggle.equalsIgnoreCase("true")
										|| toggle.equalsIgnoreCase("yes");

								cePlayer.setFullChance(isToggle);

								if (isToggle) {
									MessageUtils.send(sender, "&eFULL CHANCE MODE (" + player.getName() + "): ON");
								} else {
									MessageUtils.send(sender, "&cFULL CHANCE MODE (" + player.getName() + "): OFF");
								}
								return true;
							}
						})
					.end()
				.end()
			.end();
	}
}
