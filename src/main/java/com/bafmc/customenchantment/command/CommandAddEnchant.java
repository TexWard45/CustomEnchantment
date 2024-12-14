package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AbstractCommand;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.ArgumentType;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEEnchant;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.player.CEPlayer;
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
						.tabCompleter((arg0, arg) -> CustomEnchantment.instance().getCeEnchantMap().getKeys())
						.subCommand("<level>")
							.tabCompleter((arg0, arg) -> {
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
                            })
							.commandExecutor((sender, arg) -> {
                                Player player = arg.getPlayer();
                                if (player == null) {
                                    return true;
                                }

                                ItemStack itemStack = player.getInventory().getItemInHand();
                                if (itemStack == null) {
                                    return true;
                                }

                                CEPlayer cePlayer = CEAPI.getCEPlayer(player);
                                CEWeaponAbstract weapon = cePlayer.getSlot(EquipSlot.MAINHAND);
                                if (weapon == null) {
                                    return true;
                                }

                                CEEnchantSimple ceEnchantSimple = null;

                                try {
                                    ceEnchantSimple = new CEEnchantSimple(arg.get("<enchant>"), Integer.valueOf(arg.get("<level>")));
                                } catch (Exception e) {
                                    ceEnchantSimple = new CEEnchantSimple(arg.get("<enchant>"), 1);
                                }

                                if (ceEnchantSimple.getCEEnchant() == null) {
                                    return true;
                                }

                                weapon.getWeaponEnchant().forceAddCESimple(ceEnchantSimple);
                                player.setItemInHand(weapon.exportTo());
                                return true;
                            })
						.end()
					.end()
				.end()
			.end();
	}
	
}