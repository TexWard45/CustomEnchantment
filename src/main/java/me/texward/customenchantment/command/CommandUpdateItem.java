package me.texward.customenchantment.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.item.CEItem;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;
import me.texward.texwardlib.command.AdvancedCommandExecutor;
import me.texward.texwardlib.command.Argument;
import me.texward.texwardlib.command.ArgumentType;

public class CommandUpdateItem extends AbstractCommand {
	
	private AdvancedCommandExecutor updateExecutor = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Player player = arg.getPlayer();
			if (player == null) {
				return true;
			}

			ItemStack itemStack = player.getInventory().getItemInHand();
			if (itemStack == null) {
				return true;
			}

			CEItem ceItem = CEAPI.getCEItem(itemStack);
			if (ceItem == null) {
				return true;
			}

			player.setItemInHand(ceItem.exportTo());
			return true;
		}
	};
	
	@Override
	public void setup(AdvancedCommandBuilder builder) {
		builder
			.start()
				.nextArgument("updateitem")
				.setPermission("customenchantment.updateitem.other")
				.nextArgument(ArgumentType.PLAYER)
				.setCommandExecutor(updateExecutor)
			.finish();		
	}
	
}
