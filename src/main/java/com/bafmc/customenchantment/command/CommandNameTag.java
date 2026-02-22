package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.*;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.customenchantment.constant.CEMessageKey;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.nametag.CENameTag;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandNameTag implements CommandRegistrar {

	private AdvancedTabCompleter typeTab = new AdvancedTabCompleter() {
		public List<String> onTabComplete(CommandSender arg0, Argument arg1) {
			return CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.NAME_TAG).getKeys();
		}
	};

	private AdvancedCommandExecutor help1Command = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			CustomEnchantmentMessage.send(sender, CEMessageKey.COMMAND_NAMETAG_HELP1);
			return true;
		}
	};

	private AdvancedCommandExecutor help2Command = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			CustomEnchantmentMessage.send(sender, CEMessageKey.COMMAND_NAMETAG_HELP2);
			return true;
		}
	};

	private AdvancedCommandExecutor help3Command = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			CustomEnchantmentMessage.send(sender, CEMessageKey.COMMAND_NAMETAG_HELP3);
			return true;
		}
	};

	private AdvancedCommandExecutor showCommand = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Player player = (Player) sender;
			if (player == null) {
				return true;
			}
			CEPlayer cePlayer = CEAPI.getCEPlayer(player);
			String display = cePlayer.getNameTag().getDisplay();

			Map<String, String> placeholder = new LinkedHashMap<String, String>();
			if (display == null) {
				CustomEnchantmentMessage.send(player, CEMessageKey.COMMAND_NAMETAG_NOT_SET, placeholder);
				return true;
			}

			CENameTag nameTag = (CENameTag) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.NAME_TAG).get(arg.get("<type>"));
			display = nameTag.getNewDisplay(display);

			String stripDisplay = ChatColor.stripColor(display);
			if (stripDisplay.length() > 35) {
				CustomEnchantmentMessage.send(player, CEMessageKey.COMMAND_NAMETAG_MAX_LENGTH, placeholder);
				return true;
			}

			placeholder.put(CEConstants.Placeholder.DISPLAY, display);
			CustomEnchantmentMessage.send(player, CEMessageKey.COMMAND_NAMETAG_SHOW, placeholder);
			return true;
		}
	};

	private AdvancedCommandExecutor setCommand = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Player player = (Player) sender;
			if (player == null) {
				return true;
			}
			CENameTag nameTag = (CENameTag) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.NAME_TAG).get(arg.get("<type>"));
			String display = nameTag.getNewDisplay(arg.getToEnd("<display>"));

			String stripDisplay = ChatColor.stripColor(display);

			Map<String, String> placeholder = new LinkedHashMap<String, String>();
			if (stripDisplay.length() > 35) {
				CustomEnchantmentMessage.send(player, CEMessageKey.COMMAND_NAMETAG_MAX_LENGTH, placeholder);
				return true;
			}

			CEPlayer cePlayer = CEAPI.getCEPlayer(player);
			cePlayer.getNameTag().setDisplay(arg.getToEnd("<display>"));

			placeholder.put(CEConstants.Placeholder.DISPLAY, display);
			CustomEnchantmentMessage.send(player, CEMessageKey.COMMAND_NAMETAG_SET, placeholder);
			return true;
		}
	};

	private AdvancedCommandExecutor previewCommand = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Player player = (Player) sender;
			if (player == null) {
				return true;
			}
			CENameTag nameTag = (CENameTag) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.NAME_TAG).get(arg.get("<type>"));
			String display = nameTag.getNewDisplay(arg.getToEnd("<display>"));

			String stripDisplay = ChatColor.stripColor(display);

			Map<String, String> placeholder = new LinkedHashMap<String, String>();
			if (stripDisplay.length() > 35) {
				CustomEnchantmentMessage.send(player, CEMessageKey.COMMAND_NAMETAG_MAX_LENGTH, placeholder);
				return true;
			}

			placeholder.put(CEConstants.Placeholder.DISPLAY, display);
			CustomEnchantmentMessage.send(player, CEMessageKey.COMMAND_NAMETAG_PREVIEW, placeholder);
			return true;
		}
	};

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.commandExecutor(help1Command)
			.subCommand("help")
				.commandExecutor(help1Command)
					.subCommand("1")
						.commandExecutor(help1Command)
					.end()
				.end()
			.subCommand("help")
				.subCommand("2")
					.commandExecutor(help2Command)
				.end()
			.end()
			.subCommand("help")
				.subCommand("3")
					.commandExecutor(help3Command)
				.end()
			.end()
				.subCommand("show")
					.subCommand("<type>")
						.tabCompleter(typeTab)
						.commandExecutor(showCommand)
					.end()
				.end()
			.end()
				.subCommand("preview")
					.subCommand("<type>")
						.tabCompleter(typeTab)
						.subCommand("<display>")
							.commandExecutor(previewCommand)
						.end()
					.end()
				.end()
			.subCommand("set")
				.subCommand("<type>")
					.subCommand("<display>")
						.tabCompleter(typeTab)
						.commandExecutor(setCommand)
					.end()
				.end()
			.end();
	}

}
