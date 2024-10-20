package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AbstractCommand;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.ArgumentType;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEGemSimple;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class CommandAddGem implements AbstractCommand {

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("addgem")
				.permission("customenchantment.addgem.other")
				.subCommand(ArgumentType.PLAYER)
					.subCommand("<gem>")
						.tabCompleter((arg0, arg) -> CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.GEM).getKeys())
						.subCommand("<level>")
							.tabCompleter((arg0, arg) -> Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))
							.commandExecutor((sender, arg) -> {
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
                                CEGemSimple gemSimple = null;

                                try {
                                    gemSimple = new CEGemSimple(arg.get("<gem>"), Integer.valueOf(arg.get("<level>")));
                                } catch (Exception e) {
                                    gemSimple = new CEGemSimple(arg.get("<gem>"), 1);
                                }

                                if (gemSimple.getCEGem() == null) {
                                    return true;
                                }

                                weapon.getWeaponGem().forceAddCEGemSimple(gemSimple);
                                player.setItemInHand(weapon.exportTo());
                                return true;
                            })
						.end()
					.end()
				.end()
			.end();
	}
	
}