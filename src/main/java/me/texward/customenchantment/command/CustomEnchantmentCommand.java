package me.texward.customenchantment.command;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;

public class CustomEnchantmentCommand extends AbstractCommand {
	private CustomEnchantment plugin;

	public CustomEnchantmentCommand(CustomEnchantment plugin) {
		this.plugin = plugin;
	}

	public void setup(AdvancedCommandBuilder builder) {
		new CommandAddEnchant().setup(builder);
		new CommandAddItem().setup(builder);
		new CommandAdmin().setup(builder);
		new CommandClearTime().setup(builder);
		new CommandDisableHelmet().setup(builder);
		new CommandGiveItem().setup(builder);
		new CommandInfo().setup(builder);
		new CommandOpen().setup(builder);
		new CommandReload().setup(builder);
		new CommandRemoveEnchant().setup(builder);
		new CommandRemoveItem().setup(builder);
		new CommandUpdateItem().setup(builder);
		new CommandUseItem().setup(builder);
		new CommandDebug().setup(builder);
	}
}
