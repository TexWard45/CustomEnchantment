package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AbstractCommand;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentDebug;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandReload implements AbstractCommand {
	
	private AdvancedCommandExecutor reloadExecutor = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Bukkit.getScheduler().runTaskAsynchronously(CustomEnchantment.instance(), () -> {
				CustomEnchantment.instance().getConfigModule().onReload();
				CustomEnchantment.instance().getTaskModule().getPowerAsyncTask().setReloading(true);
				CustomEnchantmentDebug.log(sender, "The configuration has been reloaded!");
			});
			return true;
		}
	};
	
	@Override
	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("reload")
				.permission("customenchantment.reload")
				.commandExecutor(reloadExecutor)
			.end();		
	}
}
