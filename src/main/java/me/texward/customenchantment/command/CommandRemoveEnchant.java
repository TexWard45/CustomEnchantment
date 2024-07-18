package me.texward.customenchantment.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.item.CEWeaponAbstract;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;
import me.texward.texwardlib.command.AdvancedCommandExecutor;
import me.texward.texwardlib.command.Argument;
import me.texward.texwardlib.command.ArgumentType;

public class CommandRemoveEnchant extends AbstractCommand {
	
	private AdvancedCommandExecutor removeExecutor = new AdvancedCommandExecutor() {
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
			if (ceItem == null || !(ceItem instanceof CEWeaponAbstract)) {
				return true;
			}

			CEWeaponAbstract weapon = (CEWeaponAbstract) ceItem;
			weapon.getWeaponEnchant().removeCESimple(arg.get("<enchant>"));
			player.setItemInHand(weapon.exportTo());
			return true;
		}
	};
	
	@Override
	public void setup(AdvancedCommandBuilder builder) {
		builder
			.start()
				.nextArgument("removeenchant")
				.setPermission("customenchantment.removeenchant.other")
				.nextArgument(ArgumentType.PLAYER)
				.nextArgument("<enchant>")
				.setCommandExecutor(removeExecutor)
			.finish();		
	}
}
