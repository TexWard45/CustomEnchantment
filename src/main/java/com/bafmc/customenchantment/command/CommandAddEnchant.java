package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.*;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEEnchant;
import com.bafmc.customenchantment.enchant.CESimple;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandAddEnchant implements AbstractCommand {

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("addenchant")
				.permission("customenchantment.addenchant.other")
				.subCommand(ArgumentType.PLAYER)
					.subCommand("<enchant>")
						.tabCompleter(new AdvancedTabCompleter() {
							public List<String> onTabComplete(CommandSender arg0, Argument arg) {
								return CustomEnchantment.instance().getCEEnchantMap().getKeys();
							}
						})
						.subCommand("<level>")
							.tabCompleter(new AdvancedTabCompleter() {
								public List<String> onTabComplete(CommandSender arg0, Argument arg) {
									String name = arg.get("<enchant>");

									CEEnchant enchant = CEAPI.getCEEnchant(name);

									if (enchant != null) {
										List<String> levels = new ArrayList<String>();

										for (Integer level : enchant.getCELevelMap().keySet()) {
											levels.add(level.toString());
										}

										return levels;
									}

									return Arrays.asList("");
								}
							})
							.commandExecutor(new AdvancedCommandExecutor() {
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
									if (!(ceItem instanceof CEWeaponAbstract)) {
										return true;
									}

									CEWeaponAbstract weapon = (CEWeaponAbstract) ceItem;
									CESimple ceSimple = null;

									try {
										ceSimple = new CESimple(arg.get("<enchant>"), Integer.valueOf(arg.get("<level>")));
									} catch (Exception e) {
										ceSimple = new CESimple(arg.get("<enchant>"), 1);
									}

									if (ceSimple.getCEEnchant() == null) {
										return true;
									}

									weapon.getWeaponEnchant().forceAddCESimple(ceSimple);
									player.setItemInHand(weapon.exportTo());
									return true;
								}
							})
						.end()
					.end()
				.end()
			.end();
	}
	
}