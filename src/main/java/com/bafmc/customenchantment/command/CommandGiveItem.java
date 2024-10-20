package com.bafmc.customenchantment.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import com.bafmc.bukkit.utils.ItemStackUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.enchant.CEEnchant;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.bukkit.command.AbstractCommand;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.AdvancedTabCompleter;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.command.ArgumentType;
import com.bafmc.bukkit.utils.InventoryUtils;

public class CommandGiveItem implements AbstractCommand {
	
	private AdvancedCommandExecutor giveExecutor = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Player player = arg.getPlayer();
			if (player == null) {
				return true;
			}

			List<String> list = new ArrayList<>(Arrays.asList(arg.getToEnd(3).split(" ")));

			List<ItemStack> itemStacks = null;

			itemStacks = CustomEnchantment.instance().getCEItemStorageMap().get(arg.get(2))
					.getItemStacksByParameter(new Parameter(list));
			
			if (itemStacks == null) {
				return true;
			}
			
			ListIterator<ItemStack> ite = itemStacks.listIterator();
			while(ite.hasNext()) {
				ItemStack itemStack = ite.next();
				itemStack = ItemStackUtils.getItemStackWithPlaceholder(itemStack, player);
                itemStack = ItemStackUtils.updateColorToItemStack(itemStack);
				ite.set(itemStack);
			}
			
			if (itemStacks != null) {
				InventoryUtils.addItem(player, itemStacks);
			}
			return true;
		}
	};
	
	private AdvancedTabCompleter nameTab = new AdvancedTabCompleter() {
		public List<String> onTabComplete(CommandSender sender, Argument arg) {
			String type = arg.get(2);

			if (type.equals("book")) {
				return CustomEnchantment.instance().getCEEnchantMap().getKeys();
			}

			CEItemStorage<?> storage = CustomEnchantment.instance().getCEItemStorageMap().get(arg.get(2));
			return storage != null ? storage.getKeys() : Arrays.asList("");
		}
	};
	
	private AdvancedTabCompleter bookLevelTab = new AdvancedTabCompleter() {
		public List<String> onTabComplete(CommandSender sender, Argument arg) {
			String name = arg.get("<name>");

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
	};

	private AdvancedTabCompleter gemLevelTab = new AdvancedTabCompleter() {
		public List<String> onTabComplete(CommandSender sender, Argument arg) {
			return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
		}
	};
	
	private AdvancedTabCompleter successTab = new AdvancedTabCompleter() {
		public List<String> onTabComplete(CommandSender sender, Argument arg) {
			return Arrays.asList("100");
		}
	};
	
	private AdvancedTabCompleter destroyTab = new AdvancedTabCompleter() {
		public List<String> onTabComplete(CommandSender sender, Argument arg) {
			return Arrays.asList("0");
		}
	};

    private AdvancedTabCompleter xpTab = new AdvancedTabCompleter() {
        public List<String> onTabComplete(CommandSender sender, Argument arg) {
            return Arrays.asList("0");
        }
    };
	
	private AdvancedTabCompleter amountTab = new AdvancedTabCompleter() {
		public List<String> onTabComplete(CommandSender sender, Argument arg) {
			return Arrays.asList("1");
		}
	};
	
	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("give")
				.permission("customenchantment.reload")
				.subCommand(ArgumentType.PLAYER)
					.commandExecutorUntilNextCommand(giveExecutor)
					.subCommand("book")
						.subCommand("<name>")
							.tabCompleter(nameTab)
							.subCommand("<level>")
								.tabCompleter(bookLevelTab)
								.subCommand("<success>")
									.tabCompleter(successTab)
									.subCommand("<destroy>")
										.tabCompleter(destroyTab)
										.subCommand("<xp>")
											.tabCompleter(xpTab)
											.subCommand("<amount>")
												.tabCompleter(amountTab)
											.end()
										.end()
									.end()
								.end()
							.end()
						.end()
					.end()
					.subCommand("protectdead")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("removeprotectdead")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("removeenchantpoint")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("loreformat")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("protectdestroy")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("gem")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<level>").tabCompleter(gemLevelTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end().end()
					.end()
					.subCommand("nametag")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("enchantpoint")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("increaseratebook")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<success>").tabCompleter(successTab)
						.subCommand("<destroy>").tabCompleter(destroyTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end().end().end()
					.end()
					.subCommand("randombook")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("voucher")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("storage")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("removeenchant")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("eraseenchant")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("mask")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("weapon")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("artifact")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
					.subCommand("banner")
						.subCommand("<name>").tabCompleter(nameTab)
						.subCommand("<amount>").tabCompleter(amountTab)
						.end().end()
					.end()
				.end()
			.end();
	}
	
}
