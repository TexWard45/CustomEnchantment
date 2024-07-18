package me.texward.customenchantment.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.CustomEnchantmentDebug;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;
import me.texward.texwardlib.command.AdvancedCommandExecutor;
import me.texward.texwardlib.command.Argument;

public class CommandReload extends AbstractCommand {
	
	private AdvancedCommandExecutor reloadExecutor = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Bukkit.getScheduler().runTaskAsynchronously(CustomEnchantment.instance(), () -> {
				CustomEnchantment.instance().setupConfig();
				CustomEnchantmentDebug.log(sender, "The configuration has been reloaded!");
			});
			return true;
		}
	};
	
	@Override
	public void setup(AdvancedCommandBuilder builder) {
		builder
			.start()
				.nextArgument("reload")
				.setPermission("customenchantment.reload")
				.setCommandExecutor(reloadExecutor)
			.finish();		
	}
}
