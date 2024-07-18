package me.texward.customenchantment.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.texward.customenchantment.CEEnchantMap;
import me.texward.customenchantment.CEItemStorageMap;
import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.enchant.CEEnchant;
import me.texward.customenchantment.enchant.CESimple;
import me.texward.customenchantment.item.CEBook;
import me.texward.customenchantment.item.CEBookStorage;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.item.CEItemStorage;
import me.texward.customenchantment.item.CEItemType;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;
import me.texward.texwardlib.command.AdvancedCommandExecutor;
import me.texward.texwardlib.command.AdvancedTabCompleter;
import me.texward.texwardlib.command.Argument;
import me.texward.texwardlib.command.ArgumentType;
import me.texward.texwardlib.util.ItemStackUtils;

public class CommandOpen extends AbstractCommand {
	
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

	public void setup(AdvancedCommandBuilder builder) {
		builder
			.start()
				.nextArgument("open")
				.setPermission("customenchantment.open.other")
				.nextArgument(ArgumentType.PLAYER)
				.start()
					.nextArgument("storage").setTabCompleter(storageTab)
					.nextArgument("<page>")
					.setCommandExecutor(storageExecutor)
				.finish()
				.start()
					.nextArgument("mask").setTabCompleter(getOpenTab(CEItemType.MASK))
					.nextArgument("<page>")
					.setCommandExecutor(getOpenExecutor(CEItemType.MASK))
				.finish()
				.start()
					.nextArgument("weapon").setTabCompleter(getOpenTab(CEItemType.WEAPON))
					.nextArgument("<page>")
					.setCommandExecutor(getOpenExecutor(CEItemType.WEAPON))
				.finish()
				.start()
					.nextArgument("book").setTabCompleter(groupTab)
					.nextArgument("<group>")
					.setCommandExecutor(bookExecutor)
				.finish()
			.finish();
	}
	
}