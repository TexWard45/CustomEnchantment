package com.bafmc.customenchantment.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.bafmc.customenchantment.CEEnchantMap;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.enchant.CEEnchant;
import com.bafmc.customenchantment.enchant.CESimple;
import com.bafmc.customenchantment.item.CEBook;
import com.bafmc.customenchantment.item.CEBookStorage;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.bukkit.command.AbstractCommand;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.AdvancedTabCompleter;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.command.ArgumentType;
import com.bafmc.bukkit.utils.ItemStackUtils;

public class CommandOpen implements AbstractCommand {
	
	private AdvancedCommandExecutor storageExecutor = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Player player = arg.getPlayer();
			if (player == null) {
				return true;
			}
			
			int page = 1;
			
			try {
				page = Integer.valueOf(arg.get("<page>"));
			}catch(Exception e) {
				
			}

			Inventory inventory = Bukkit.createInventory(null, 54, "Storage");

			CEItemStorage storage = CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.STORAGE);
			CEItem[] ceItemArr = (CEItem[]) storage.values().toArray(new CEItem[storage.size()]);

			int from = (page - 1) * 54;
			int len = storage.size() - from > 54 ? 54 : storage.size() - from;

			for (int i = 0; i < len; i++) {
				inventory.setItem(i, ItemStackUtils.getItemStackWithPlaceholder(ceItemArr[from + i].exportTo(), player));
			}

			player.openInventory(inventory);
			return true;
		}
	};
	
	public AdvancedCommandExecutor getOpenExecutor(String type) {
		return new AdvancedCommandExecutor() {
			public boolean onCommand(CommandSender sender, Argument arg) {
				Player player = arg.getPlayer();
				if (player == null) {
					return true;
				}
				
				int page = 1;
				
				try {
					page = Integer.valueOf(arg.get("<page>"));
				}catch(Exception e) {
					
				}

				Inventory inventory = Bukkit.createInventory(null, 54, type);

				CEItemStorage storage = CustomEnchantment.instance().getCEItemStorageMap().get(type);
				CEItem[] ceItemArr = (CEItem[]) storage.values().toArray(new CEItem[storage.size()]);

				int from = (page - 1) * 54;
				int len = storage.size() - from > 54 ? 54 : storage.size() - from;

				for (int i = 0; i < len; i++) {
					inventory.setItem(i, ItemStackUtils.getItemStackWithPlaceholder(ceItemArr[from + i].exportTo(), player));
				}

				player.openInventory(inventory);
				return true;
			}
		};
	}
	
	private AdvancedCommandExecutor bookExecutor = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Player player = arg.getPlayer();
			if (player == null) {
				return true;
			}
			
			String group = arg.get("<group>");

			Inventory inventory = Bukkit.createInventory(null, 54, "Storage");

			CEEnchantMap map = CustomEnchantment.instance().getCEEnchantMap();

			CEBookStorage storage = (CEBookStorage) CustomEnchantment.instance().getCEItemStorageMap()
					.get(CEItemType.BOOK);

			List<String> ceList = new ArrayList<String>(map.keySet());

			String[] ceArr = ceList.toArray(new String[ceList.size()]);

			Arrays.sort(ceArr);

			int i = 0;
			for (String name : ceArr) {
				CEEnchant enchant = map.get(name);
				
				if (!enchant.getGroupName().equals(group)) {
					continue;
				}

				ceList.add(enchant.getName());

				CEBook book = storage.getCEBook(new CESimple(enchant.getName(), 1, 100, 0));

				inventory.setItem(i, ItemStackUtils.getItemStackWithPlaceholder(book.exportTo(), player));
				i++;
			}

			player.openInventory(inventory);
			return true;
		}
	};
	
	private AdvancedTabCompleter storageTab = new AdvancedTabCompleter() {
		public List<String> onTabComplete(CommandSender arg0, Argument arg1) {
			CEItemStorage storage = CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.STORAGE);
			int maxPage = (int) Math.ceil(storage.size() / 54d);

			List<String> list = new ArrayList<String>();
			for (int i = 1; i <= maxPage; i++) {
				list.add("" + i);
			}

			return list;
		}
	};
	
	public AdvancedTabCompleter getOpenTab(String type) {
		return new AdvancedTabCompleter() {
			public List<String> onTabComplete(CommandSender arg0, Argument arg1) {
				CEItemStorage<? extends CEItem>  storage = CustomEnchantment.instance().getCEItemStorageMap()
						.get(type);
				int maxPage = (int) Math.ceil(storage.size() / 54d);

				List<String> list = new ArrayList<String>();
				for (int i = 1; i <= maxPage; i++) {
					list.add("" + i);
				}

				return list;
			}
		};
	}

	private AdvancedTabCompleter groupTab = new  AdvancedTabCompleter() {
		public List<String> onTabComplete(CommandSender arg0, Argument arg1) {
			return new ArrayList<String>(CustomEnchantment.instance().getCEGroupMap().keySet());
		}
	};

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("open")
				.permission("customenchantment.open.other")
				.subCommand(ArgumentType.PLAYER)
					.subCommand("storage")
						.subCommand("<page>")
							.tabCompleter(storageTab)
							.commandExecutor(storageExecutor)
						.end()
					.end()
					.subCommand("mask")
						.subCommand("<page>")
							.tabCompleter(getOpenTab(CEItemType.MASK))
							.commandExecutor(getOpenExecutor(CEItemType.MASK))
						.end()
					.end()
					.subCommand("weapon")
						.subCommand("<page>")
							.tabCompleter(getOpenTab(CEItemType.WEAPON))
							.commandExecutor(getOpenExecutor(CEItemType.WEAPON))
						.end()
					.end()
					.subCommand("artifact")
						.subCommand("<page>")
							.tabCompleter(getOpenTab(CEItemType.ARTIFACT))
							.commandExecutor(getOpenExecutor(CEItemType.ARTIFACT))
						.end()
					.end()
					.subCommand("book")
						.subCommand("<group>")
							.tabCompleter(groupTab)
							.commandExecutor(bookExecutor)
						.end()
					.end()
				.end()
			.end();
	}
	
}