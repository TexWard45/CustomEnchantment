package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AbstractCommand;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.customenchantment.CustomEnchantment;

public class CustomEnchantmentCommand implements AbstractCommand {
	private CustomEnchantment plugin;

	public CustomEnchantmentCommand(CustomEnchantment plugin) {
		this.plugin = plugin;
	}

	public void onRegister(AdvancedCommandBuilder builder) {
		new CommandAddEnchant().onRegister(builder);
		new CommandAddItem().onRegister(builder);
		new CommandAdmin().onRegister(builder);
		new CommandClearTime().onRegister(builder);
		new CommandDisableHelmet().onRegister(builder);
		new CommandGiveItem().onRegister(builder);
		new CommandInfo().onRegister(builder);
		new CommandOpen().onRegister(builder);
		new CommandReload().onRegister(builder);
		new CommandRemoveEnchant().onRegister(builder);
		new CommandRemoveItem().onRegister(builder);
		new CommandUpdateItem().onRegister(builder);
		new CommandUseItem().onRegister(builder);
		new CommandDebug().onRegister(builder);
		new CommandFullChance().onRegister(builder);
		new CommandDebugAll().onRegister(builder);
		new CommandDebugCE().onRegister(builder);
	}
}
