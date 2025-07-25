package com.bafmc.customenchantment;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.utils.MessageUtils;
import com.bafmc.bukkit.utils.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomEnchantmentMessage {
	private static AdvancedFileConfiguration config;
	private static HashMap<String, String> placeholder = new LinkedHashMap<String, String>();
	static {
		placeholder.put("SUCCESS ", "&2[&a&l!&2] &a");
		placeholder.put("INFO ", "&3[&b&l!&3] &b");
		placeholder.put("DANGER ", "&4[&c&l!&4] &c");
		placeholder.put("EVENT ", "&5[&d&l!&5] &d");
		placeholder.put("WARN ", "&6[&e&l!&6] &e");
		placeholder.put("DEPRECATED ", "&8[&7&l!&8] &7");
		placeholder.put("MESSAGE ", "");
	}

	public static List<String> getMessageConfig(String path) {
		path = path.toLowerCase();
		return config.getStringColorList(path);
	}

	public static void setConfig(AdvancedFileConfiguration config) {
		CustomEnchantmentMessage.config = config;
	}

	public static void send(CommandSender player, String path) {
		path = path.toLowerCase();

		List<String> messages = config.getStringColorList(path);

		for (String message : messages) {
			MessageUtils.send(player, StringUtils.replace(message, placeholder));
		}
	}

	public static void send(CommandSender player, String path, Map<String, String> placeholder) {
		if (placeholder == null) {
			send(player, path);
		} else {
			placeholder = new LinkedHashMap<>(placeholder);
			placeholder.put("\\%", "%");
			placeholder.putAll(CustomEnchantmentMessage.placeholder);
			path = path.toLowerCase();

			List<String> messages = config.getStringColorList(path);
			for (String message : messages) {
				MessageUtils.send(player, StringUtils.replace(message, placeholder));
			}
		}
	}
}
