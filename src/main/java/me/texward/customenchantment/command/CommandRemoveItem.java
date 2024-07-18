package me.texward.customenchantment.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.item.CEItemType;
import me.texward.customenchantment.item.VanillaItemStorage;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;
import me.texward.texwardlib.command.AdvancedCommandExecutor;
import me.texward.texwardlib.command.Argument;
import me.texward.texwardlib.command.ArgumentType;

public class CommandRemoveItem extends AbstractCommand {
	
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
	public void setup(AdvancedCommandBuilder builder) {
		builder
			.start()
				.nextArgument("removeitem")
				.setPermission("customenchantment.removeitem.other")
				.nextArgument(ArgumentType.PLAYER)
				.nextArgument("<item>")
				.setCommandExecutor(removeExecutor)
			.finish();		
	}
}
