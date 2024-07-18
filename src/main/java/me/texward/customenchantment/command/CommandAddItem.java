package me.texward.customenchantment.command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.item.CEItemType;
import me.texward.customenchantment.item.VanillaItemStorage;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;
import me.texward.texwardlib.command.AdvancedCommandExecutor;
import me.texward.texwardlib.command.Argument;
import me.texward.texwardlib.command.ArgumentType;

public class CommandAddItem extends AbstractCommand {

	public void setup(AdvancedCommandBuilder builder) {
		builder
			.start()
				.nextArgument("additem")
				.setPermission("customenchantment.additem.other")
				.nextArgument(ArgumentType.PLAYER)
				.nextArgument("<item>")
				.setCommandExecutor(new AdvancedCommandExecutor() {
					public boolean onCommand(CommandSender sender, Argument arg) {
						Player player = arg.getPlayer();
						if (player == null) {
							return true;
						}

						ItemStack itemStack = player.getItemInHand();
						if (itemStack == null || itemStack.getType() == Material.AIR) {
							return true;
						}

						VanillaItemStorage storage = (VanillaItemStorage) CustomEnchantment.instance().getCEItemStorageMap()
								.get(CEItemType.STORAGE);
						storage.putItem(arg.get("<item>"), itemStack);
						return true;
					}
				})
			.finish();
	}
	
}
