package com.bafmc.customenchantment.command;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerVanillaAttribute;
import com.bafmc.bukkit.command.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandInfo implements AbstractCommand {

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("info")
				.permission("customenchantment.info.other")
				.subCommand(ArgumentType.PLAYER)
					.commandExecutor(new AdvancedCommandExecutor() {
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
				.end()
			.end();
	}
	
}