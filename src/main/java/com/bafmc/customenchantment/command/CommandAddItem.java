package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.*;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.VanillaItemStorage;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandAddItem implements AbstractCommand {

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("additem")
				.permission("customenchantment.additem.other")
				.subCommand(ArgumentType.PLAYER)
					.subCommand("<item>")
						.commandExecutor((sender, arg) -> {
                            Player player = arg.getPlayer();
                            if (player == null) {
                                return true;
                            }

                            ItemStack itemStack = player.getItemInHand();
                            if (itemStack == null || itemStack.getType() == Material.AIR) {
                                return true;
                            }

                            VanillaItemStorage storage = (VanillaItemStorage) CustomEnchantment.instance().getCeItemStorageMap()
                                    .get(CEItemType.STORAGE);
                            storage.putItem(arg.get("<item>"), itemStack);
                            return true;
                        })
					.end()
				.end()
			.end();
	}
	
}
