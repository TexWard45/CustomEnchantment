package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.*;
import com.bafmc.bukkit.utils.MessageUtils;

import com.bafmc.customenchantment.api.CEAPI;

import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.TemporaryKey;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CommandTest implements CommandRegistrar {

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("test")
				.permission("customenchantment.test.target")
				.subCommand(ArgumentType.PLAYER)
					.subCommand("<parameter>")
						.tabCompleter(new AdvancedTabCompleter() {
							public List<String> onTabComplete(CommandSender sender, Argument arg) {
								return Arrays.asList("incombat");
							}
						})
						.commandExecutor(new AdvancedCommandExecutor() {
							public boolean onCommand(CommandSender sender, Argument arg) {
								Player player = Bukkit.getPlayer(arg.get(ArgumentType.PLAYER));
								if (player == null) {
									return true;
								}

								CEPlayer cePlayer = CEAPI.getCEPlayer((Player) sender);

								String parameter = arg.get("<parameter>");
								if (parameter.equals("incombat")) {
									cePlayer.getTemporaryStorage().set(TemporaryKey.LAST_COMBAT_TIME, System.currentTimeMillis());
									MessageUtils.send(player, "&2Set in combat time to now.");
								}
								return true;
							}
						})
					.end()
				.end()
			.end();
	}
}
