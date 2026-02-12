package com.bafmc.customenchantment.command;

import lombok.Getter;
import com.bafmc.bukkit.command.*;
import com.bafmc.bukkit.utils.MessageUtils;
import org.bukkit.command.CommandSender;

public class CommandDebugAll implements CommandRegistrar {
	@Getter
	public static boolean debugMode = false;

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("debugall")
				.permission("customenchantment.debugall")
				.commandExecutor(new AdvancedCommandExecutor() {
					public boolean onCommand(CommandSender sender, Argument arg) {
						boolean isToggle = !isDebugMode();
						debugMode = isToggle;

						if (isToggle) {
							MessageUtils.send(sender, "&2DEBUG MODE: ON");
						} else {
							MessageUtils.send(sender, "&2DEBUG MODE: OFF");
						}
						return true;
					}
				})
			.end();
	}
}
