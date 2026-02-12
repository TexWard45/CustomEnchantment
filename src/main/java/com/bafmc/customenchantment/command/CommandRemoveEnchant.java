package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.*;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandRemoveEnchant implements CommandRegistrar {
	
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
	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("removeenchant")
				.permission("customenchantment.removeenchant.other")
				.subCommand(ArgumentType.PLAYER)
					.subCommand("<enchant>")
						.commandExecutor(removeExecutor)
					.end()
				.end()
			.end();		
	}
}
