package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AbstractCommand;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.ArgumentType;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRemoveGem implements AbstractCommand {

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("removegem")
				.permission("customenchantment.removegem.other")
				.subCommand(ArgumentType.PLAYER)
					.subCommand("<index>")
						.tabCompleter((arg0, arg) -> {
                            Player player = arg.getPlayer();
                            if (player == null) {
                                return Arrays.asList("");
                            }

                            CEPlayer cePlayer = CEAPI.getCEPlayer(player);
                            CEWeaponAbstract weapon = cePlayer.getSlot(EquipSlot.MAINHAND);
                            if (weapon == null) {
                                return Arrays.asList("");
                            }

                            List<String> list = new ArrayList<>();

                            for (int i = 0; i < weapon.getWeaponGem().getCEGemSimpleList().size(); i++) {
                                list.add(String.valueOf(i));
                            }

                            return list;
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

                            int index = -1;
                            try {
                                index = Integer.valueOf(arg.get("<index>"));
                            } catch (Exception e) {
                                return true;
                            }

                            CEWeaponAbstract weapon = cePlayer.getSlot(EquipSlot.MAINHAND);
                            if (weapon == null) {
                                return true;
                            }

                            if (index < 0 || index >= weapon.getWeaponGem().getCEGemSimpleList().size()) {
                                return true;
                            }

                            weapon.getWeaponGem().removeCEGemSimple(Integer.valueOf(arg.get("<index>")));
                            player.setItemInHand(weapon.exportTo());
                            return true;
                        })
					.end()
				.end()
			.end();
	}
	
}