package me.texward.customenchantment;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.texward.texwardlib.util.MessageUtils;

public class CustomEnchantmentDebug {
	private static final String LOG = "&6[&eCustomEnchantment&6] &e";
	private static final String WARN = "&4[&cCustomEnchantment&4] &c";

	public static void log(String message) {
		MessageUtils.send(Bukkit.getConsoleSender(), LOG + message);
	}

	public static void warn(String message) {
		MessageUtils.send(Bukkit.getConsoleSender(), WARN + message);
	}

	public static void log(CommandSender sender, String message) {
		MessageUtils.send(sender, LOG + message);
	}

	public static void warn(CommandSender sender, String message) {
		MessageUtils.send(sender, WARN + message);
	}
}
