package me.texward.customenchantment.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.Parameter;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.item.CEItemUsable;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;
import me.texward.texwardlib.command.AdvancedCommandExecutor;
import me.texward.texwardlib.command.Argument;
import me.texward.texwardlib.command.ArgumentType;

public class CommandUseItem extends AbstractCommand {
	
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
	public void setup(AdvancedCommandBuilder builder) {
		builder
			.start()
				.nextArgument("useitem")
				.setPermission("customenchantment.useitem.other")
				.nextArgument(ArgumentType.PLAYER)
				.nextArgument("<type>")
				.nextArgument("<name>")
				.setCommandExecutor(useExecutor)
			.finish();		
	}
}