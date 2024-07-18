package me.texward.customenchantment.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.item.CEBanner;
import me.texward.customenchantment.item.CEItem;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;
import me.texward.texwardlib.command.AdvancedCommandExecutor;
import me.texward.texwardlib.command.Argument;
import me.texward.texwardlib.command.ArgumentType;

public class CommandDisableHelmet extends AbstractCommand {

	public void setup(AdvancedCommandBuilder builder) {
		builder
			.start()
				.nextArgument("disablehelmet")
				.setPermission("customenchantment.disablehelmet.other")
				.nextArgument(ArgumentType.PLAYER)
				.setCommandExecutor(new AdvancedCommandExecutor() {
					public boolean onCommand(CommandSender sender, Argument arg) {
						Player player = Bukkit.getPlayer(arg.get(ArgumentType.PLAYER));
						if (player == null) {
							return true;
						}

						ItemStack itemStack = player.getInventory().getItemInHand();
						if (itemStack == null) {
							return true;
						}
						
						CEItem ceItem = CEAPI.getCEItem(itemStack);
						if (!(ceItem instanceof CEBanner)) {
							return true;
						}

						CEBanner ceBanner = new CEBanner(itemStack);
						ceBanner.setHelmetEnable(false);
						player.setItemInHand(ceBanner.exportTo());
						return true;
					}
				})
			.finish();
	}
	
}