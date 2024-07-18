package me.texward.customenchantment.command;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.CustomEnchantmentMessage;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.item.CEItemType;
import me.texward.customenchantment.item.CENameTag;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;
import me.texward.texwardlib.command.AdvancedCommandExecutor;
import me.texward.texwardlib.command.AdvancedTabCompleter;
import me.texward.texwardlib.command.Argument;

public class CommandNameTag extends AbstractCommand {

	private AdvancedTabCompleter typeTab = new AdvancedTabCompleter() {
		public List<String> onTabComplete(CommandSender arg0, Argument arg1) {
			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.NAME_TAG).getKeys();
		}
	}; 
	
	private AdvancedCommandExecutor help1Command = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			CustomEnchantmentMessage.send(sender, "command.nametag.help1");
			return true;
		}
	};
	
	private AdvancedCommandExecutor help2Command = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			CustomEnchantmentMessage.send(sender, "command.nametag.help2");
			return true;
		}
	};
	
	private AdvancedCommandExecutor help3Command = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			CustomEnchantmentMessage.send(sender, "command.nametag.help3");
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
				CustomEnchantmentMessage.send(player, "command.nametag.not-set", placeholder);
				return true;
			}
			
			CENameTag nameTag = (CENameTag) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.NAME_TAG).get(arg.get("<type>"));
			display = nameTag.getNewDisplay(display);
			
			String stripDisplay = ChatColor.stripColor(display);
			if (stripDisplay.length() > 35) {
				CustomEnchantmentMessage.send(player, "command.nametag.max-length", placeholder);
				return true;
			}
			
			placeholder.put("%display%", display);
			CustomEnchantmentMessage.send(player, "command.nametag.show", placeholder);
			return true;
		}
	};
	
	private AdvancedCommandExecutor setCommand = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Player player = (Player) sender;
			if (player == null) {
				return true;
			}
			CENameTag nameTag = (CENameTag) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.NAME_TAG).get(arg.get("<type>"));
			String display = nameTag.getNewDisplay(arg.getToEnd("<display>"));
			
			String stripDisplay = ChatColor.stripColor(display);
			
			Map<String, String> placeholder = new LinkedHashMap<String, String>();
			if (stripDisplay.length() > 35) {
				CustomEnchantmentMessage.send(player, "command.nametag.max-length", placeholder);
				return true;
			}
			
			CEPlayer cePlayer = CEAPI.getCEPlayer(player);
			cePlayer.getNameTag().setDisplay(arg.getToEnd("<display>"));
			
			placeholder.put("%display%", display);
			CustomEnchantmentMessage.send(player, "command.nametag.set", placeholder);
			return true;
		}
	};
	
	private AdvancedCommandExecutor previewCommand = new AdvancedCommandExecutor() {
		public boolean onCommand(CommandSender sender, Argument arg) {
			Player player = (Player) sender;
			if (player == null) {
				return true;
			}
			CENameTag nameTag = (CENameTag) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.NAME_TAG).get(arg.get("<type>"));
			String display = nameTag.getNewDisplay(arg.getToEnd("<display>"));
			
			String stripDisplay = ChatColor.stripColor(display);
			
			Map<String, String> placeholder = new LinkedHashMap<String, String>();
			if (stripDisplay.length() > 35) {
				CustomEnchantmentMessage.send(player, "command.nametag.max-length", placeholder);
				return true;
			}
			
			placeholder.put("%display%", display);
			CustomEnchantmentMessage.send(player, "command.nametag.preview", placeholder);
			return true;
		}
	};
	
	public void setup(AdvancedCommandBuilder builder) {
		builder
			.setCommandExecutor(help1Command)
			.start()
				.nextArgument("help").setCommandExecutor(help1Command)
				.nextArgument("1").setCommandExecutor(help1Command)
			.finish()
			.start()
				.nextArgument("help")
				.nextArgument("2").setCommandExecutor(help2Command)
			.finish()
			.start()
				.nextArgument("help")
				.nextArgument("3").setCommandExecutor(help3Command)
			.finish()
			.start()
				.nextArgument("show").setTabCompleter(typeTab)
				.nextArgument("<type>").setCommandExecutor(showCommand)
			.finish()
			.start()
				.nextArgument("preview").setTabCompleter(typeTab)
				.nextArgument("<type>")
				.nextArgument("<display>").setCommandExecutor(previewCommand)
			.finish()
			.start()
				.nextArgument("set").setTabCompleter(typeTab)
				.nextArgument("<type>")
				.nextArgument("<display>").setCommandExecutor(setCommand)
			.finish();
	}
	
}