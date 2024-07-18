package me.texward.customenchantment;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import me.texward.texwardlib.configuration.AdvancedFileConfiguration;
import me.texward.texwardlib.util.MessageUtils;
import me.texward.texwardlib.util.StringUtils;

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
	}

	public static void setConfig(AdvancedFileConfiguration config) {
		CustomEnchantmentMessage.config = config;
	}

	public static void send(CommandSender player, String path) {
		path = path.toLowerCase();

		List<String> messages = config.getStringColorList(path);

		for (String message : messages) {
			MessageUtils.send(player, StringUtils.replace(message, placeholder).get(0));
		}
	}

	public static void send(CommandSender player, String path, Map<String, String> placeholder) {
		if (placeholder == null) {
			send(player, path);
		} else {
			placeholder = new LinkedHashMap<String, String>(placeholder);
			placeholder.put("\\%", "%");
			placeholder.putAll(CustomEnchantmentMessage.placeholder);
			path = path.toLowerCase();

			List<String> messages = config.getStringColorList(path);
			for (String message : messages) {
				MessageUtils.send(player, StringUtils.replace(message, placeholder).get(0));
			}
		}
	}
}
