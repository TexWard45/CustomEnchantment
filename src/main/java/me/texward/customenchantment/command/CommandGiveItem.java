package me.texward.customenchantment.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.api.Parameter;
import me.texward.customenchantment.enchant.CEEnchant;
import me.texward.customenchantment.item.CEItemStorage;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;
import me.texward.texwardlib.command.AdvancedCommandExecutor;
import me.texward.texwardlib.command.AdvancedTabCompleter;
import me.texward.texwardlib.command.Argument;
import me.texward.texwardlib.command.ArgumentType;
import me.texward.texwardlib.util.InventoryUtils;
import me.texward.texwardlib.util.ItemStackUtils;

public class CommandGiveItem extends AbstractCommand {
	
	private AdvancedCommandExecutor giveExecutor = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Player player = arg.getPlayer();
			if (player == null) {
				return true;
			}

			List<String> list = new ArrayList<String>(Arrays.asList(arg.getToEnd(3).split(" ")));

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
	
	private AdvancedTabCompleter amountTab = new AdvancedTabCompleter() {
		public List<String> onTabComplete(CommandSender sender, Argument arg) {
			return Arrays.asList("1");
		}
	};
	
	public void setup(AdvancedCommandBuilder builder) {
		builder
			.start()
				.nextArgument("give").setPermission("customenchantment.reload")
				.nextArgument(ArgumentType.PLAYER)
				.setCommandExecutorUntilNextCommand(giveExecutor)
				.start()
					.nextArgument("book").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(bookLevelTab)
					.nextArgument("<level>").setTabCompleter(successTab)
					.nextArgument("<success>").setTabCompleter(destroyTab)
					.nextArgument("<destroy>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("protectdead").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("removeprotectdead").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("protectdestroy").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("nametag").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("enchantpoint").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("increaseratebook").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(successTab)
					.nextArgument("<success>").setTabCompleter(destroyTab)
					.nextArgument("<destroy>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("randombook").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("voucher").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("storage").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("removeenchant").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("eraseenchant").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("mask").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("weapon").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
				.start()
					.nextArgument("banner").setTabCompleter(nameTab)
					.nextArgument("<name>").setTabCompleter(amountTab)
					.nextArgument("<amount>")
				.finish()
			.finish();
	}
	
}
