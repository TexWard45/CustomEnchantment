package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AbstractCommand;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.ArgumentType;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.gem.CEGem;
import com.bafmc.customenchantment.item.gem.CEGemSimple;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandAddGem implements AbstractCommand {

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("addgem")
				.permission("customenchantment.addgem.other")
				.subCommand(ArgumentType.PLAYER)
					.subCommand("<gem>")
						.tabCompleter((arg0, arg) -> CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.GEM).getKeys())
						.subCommand("<level>")
							.tabCompleter((arg0, arg) -> {
                                CEGem ceGem = (CEGem) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.GEM).get(arg.get("<gem>"));

                                if (ceGem != null) {
                                    return ceGem.getData().getConfigData().getLevelMap().keySet().stream().map(String::valueOf).collect(Collectors.toUnmodifiableList());
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

                                CEWeaponAbstract weapon = cePlayer.getEquipment().getSlot(EquipSlot.MAINHAND);
                                if (weapon == null) {
                                    return true;
                                }

                                CEGemSimple gemSimple = null;
                                try {
                                    gemSimple = new CEGemSimple(arg.get("<gem>"), Integer.valueOf(arg.get("<level>")));
                                } catch (Exception e) {
                                    gemSimple = new CEGemSimple(arg.get("<gem>"), 1);
                                }

                                if (gemSimple.getCEGem() == null) {
                                    return true;
                                }

                                weapon.getWeaponGem().addCEGemSimple(gemSimple);
                                player.setItemInHand(weapon.exportTo());
                                return true;
                            })
						.end()
					.end()
				.end()
			.end();
	}
	
}