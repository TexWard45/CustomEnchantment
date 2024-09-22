package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.*;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.VanillaItemStorage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRemoveItem implements AbstractCommand {
	
	private AdvancedCommandExecutor removeExecutor = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Player player = arg.getPlayer();
			if (player == null) {
				return true;
			}

			VanillaItemStorage storage = (VanillaItemStorage) CustomEnchantment.instance().getCEItemStorageMap()
					.get(CEItemType.STORAGE);
			storage.removeItem(arg.get("<name>"));
			return true;
		}
	};
	
	@Override
	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("removeitem")
				.permission("customenchantment.removeitem.other")
				.subCommand(ArgumentType.PLAYER)
					.subCommand("<item>")
						.commandExecutor(removeExecutor)
					.end()
				.end()
			.end();		
	}
}
