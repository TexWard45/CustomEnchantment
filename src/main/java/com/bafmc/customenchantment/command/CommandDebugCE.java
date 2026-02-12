package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.*;
import com.bafmc.bukkit.utils.MessageUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandDebugCE implements CommandRegistrar {
	@Getter
	private static List<String> togglePlayers = new ArrayList<>();

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("debugce")
				.permission("customenchantment.debugce.target")
				.subCommand(ArgumentType.PLAYER)
					.commandExecutor(new AdvancedCommandExecutor() {
						public boolean onCommand(CommandSender sender, Argument arg) {
							Player player = Bukkit.getPlayer(arg.get(ArgumentType.PLAYER));
							if (player == null) {
								return true;
							}

							boolean isToggle = !togglePlayers.contains(player.getName());
							if (isToggle) {
								togglePlayers.add(player.getName());
							} else {
								togglePlayers.remove(player.getName());
							}

							if (isToggle) {
								MessageUtils.send(sender, "&2DEBUG CE MODE: ON");
								MessageUtils.send(sender, Arrays.toString(togglePlayers.toArray()));
							} else {
								MessageUtils.send(sender, "&2DEBUG CE MODE: OFF");
								MessageUtils.send(sender, Arrays.toString(togglePlayers.toArray()));
							}
							return true;
						}
					})
				.end()
			.end();
	}
}
