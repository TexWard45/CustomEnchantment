package me.texward.customenchantment;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

public class ConfigVariable {
	public static long MOVE_EVENT_PERIOD;
	public static List<String> ENCHANT_DISABLE_WORLDS;

	public static synchronized boolean isEnchantDisableLocation(Location location) {
		return ENCHANT_DISABLE_WORLDS.contains(location.getWorld().getName());
	}

	public static synchronized boolean isEnchantDisableLocation(World world) {
		return ENCHANT_DISABLE_WORLDS.contains(world.getName());
	}
	
	public static synchronized boolean isEnchantDisableLocation(String world) {
		return ENCHANT_DISABLE_WORLDS.contains(world);
	}
}
