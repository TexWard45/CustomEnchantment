package com.bafmc.customenchantment.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEItemUsable;
import com.bafmc.bukkit.command.AbstractCommand;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.command.ArgumentType;

public class CommandUseItem implements AbstractCommand {
	
	private AdvancedCommandExecutor useExecutor = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Player player = arg.getPlayer();
			if (player == null) {
				return true;
			}

			List<String> list = new ArrayList<String>(Arrays.asList(arg.getToEnd(3)));

			CEItem ceItem = CustomEnchantment.instance().getCEItemStorageMap().get(arg.get("$type"))
					.getByParameter(new Parameter(list));
			
			if (ceItem == null || !(ceItem instanceof CEItemUsable)) {
				return true;
			}

			CEItemUsable usable = (CEItemUsable) ceItem;
			usable.useBy(player);
			return true;
		}
	};
	
	@Override
	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("useitem")
				.permission("customenchantment.useitem.other")
				.subCommand(ArgumentType.PLAYER)
					.subCommand("<type>")
						.subCommand("<name>")
							.commandExecutor(useExecutor)
						.end()
					.end()
				.end()
			.end();		
	}
}