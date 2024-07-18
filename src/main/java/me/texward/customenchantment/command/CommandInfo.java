package me.texward.customenchantment.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.customenchantment.player.PlayerVanillaAttribute;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;
import me.texward.texwardlib.command.AdvancedCommandExecutor;
import me.texward.texwardlib.command.Argument;
import me.texward.texwardlib.command.ArgumentType;

public class CommandInfo extends AbstractCommand {

	public void setup(AdvancedCommandBuilder builder) {
		builder
			.start()
				.nextArgument("info")
				.setPermission("customenchantment.info.other")
				.nextArgument(ArgumentType.PLAYER)
				.setCommandExecutor(new AdvancedCommandExecutor() {
					public boolean onCommand(CommandSender sender, Argument arg) {
						Player player = arg.getPlayer();
						if (player == null) {
							return true;
						}

						CEPlayer cePlayer = CEAPI.getCEPlayer(player);

						PlayerVanillaAttribute attribute = cePlayer.getVanillaAttribute();

						sender.sendMessage("== Info " + player.getName() + " ==");
						for (Attribute attr : PlayerVanillaAttribute.ATTRIBUTE_LIST) {
							List<AttributeModifier> attributeModifiers = attribute.getAttributeModifiers(attr);

							for (AttributeModifier modifier : attributeModifiers) {
								sender.sendMessage(modifier.getName() + " " + modifier.getAmount() + " " + modifier.getOperation());
							}
						}
						sender.sendMessage("== End ==");
						return true;
					}
				})
			.finish();
	}
	
}