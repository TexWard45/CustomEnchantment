package me.texward.customenchantment.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;
import me.texward.texwardlib.command.AdvancedCommandExecutor;
import me.texward.texwardlib.command.Argument;
import me.texward.texwardlib.command.ArgumentType;
import me.texward.texwardlib.util.MessageUtils;

public class CommandAdmin extends AbstractCommand {

	public void setup(AdvancedCommandBuilder builder) {
		builder
			.start()
				.nextArgument("admin")
				.setPermission("customenchantment.admin.target")
				.nextArgument(ArgumentType.PLAYER)
				.nextArgument("<toggle>")
				.setCommandExecutor(new AdvancedCommandExecutor() {
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
			.finish();
	}
}
