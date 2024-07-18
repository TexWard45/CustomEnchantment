package me.texward.customenchantment.enchant;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.texward.customenchantment.player.PlayerTemporaryStorage;
import me.texward.texwardlib.util.RomanNumber;
import me.texward.texwardlib.util.StringUtils;

public class CEPlaceholder {
	public static String setPlaceholder(String s, Map<String, String> map) {
		for (String key : map.keySet()) {
			s = s.replace(key, map.get(key));
		}
		return s;
	}

	public static Map<String, String> getTemporaryStoragePlaceholder(PlayerTemporaryStorage storage) {
		HashMap<String, String> map = new HashMap<String, String>();

		if (storage == null) {
			return map;
		}

		for (String key : storage.getKeys()) {
			map.put(key, storage.getString(key));
		}

		return map;
	}

	public static Map<String, String> getCEFunctionDataPlaceholder(String text, CEFunctionData data) {
		HashMap<String, String> map = new HashMap<String, String>();

		if (data.getPlayer() != null) {
			Player player = data.getPlayer();
			if (text.indexOf("%player_name%") != -1) {
				map.put("%player_name%", player.getName());
				map.put("%player%", player.getName());
			}
		}

		if (data.getLivingEntity() != null) {
			LivingEntity entity = data.getLivingEntity();
			if (text.indexOf("%player_health%") != -1) {
				map.put("%player_health%", String.valueOf(entity.getHealth()));
			}
		}

		if (data.getEnemyPlayer() != null) {
			Player enemy = data.getEnemyPlayer();
			if (text.indexOf("%enemy_name%") != -1) {
				map.put("%enemy_name%", enemy.getName());
				map.put("%enemy%", enemy.getName());
			}
		}

		if (data.getEnemyLivingEntity() != null) {
			LivingEntity entity = data.getEnemyLivingEntity();
			if (text.indexOf("%enemy_health%") != -1) {
				map.put("%enemy_health%", String.valueOf(entity.getHealth()));
			}
		}

		if (data.getLivingEntity() != null && data.getEnemyLivingEntity() != null && text.indexOf("%distance%") != -1) {
			if (!data.getLivingEntity().getWorld().equals(data.getEnemyLivingEntity().getWorld())) {
				map.put("%distance%", String.valueOf(99999));
			} else {
				map.put("%distance%", String.valueOf(
						data.getLivingEntity().getLocation().distance(data.getEnemyLivingEntity().getLocation())));
			}
		}

		if (text.indexOf("%damage%") != -1) {
			map.put("%damage%", String.valueOf(data.get("damage")));
		}

		if (text.indexOf("%time%") != -1) {
			map.put("%time%", String.valueOf(System.currentTimeMillis()));
		}

		return map;
	}

	public static Map<String, String> getCESimplePlaceholder(CESimple ceSimple) {
		CEEnchant ce = ceSimple.getCEEnchant();
		int level = ceSimple.getLevel();
		double success = ceSimple.getSuccess().getValue();
		double destroy = ceSimple.getDestroy().getValue();

		String display = ce.getCEDisplay().getDefaultDisplay();
		String description = StringUtils.toString(ce.getCEDisplay().getDescription());
		String detailDescription = StringUtils.toString(ce.getCEDisplay().getDetailDescription());
		String appliesDescription = StringUtils.toString(ce.getCEDisplay().getAppliesDescription());
		String levelStr = RomanNumber.toRoman(level);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("%enchant_display%", display);
		map.put("%enchant_level%", levelStr);
		map.put("%enchant_success%", String.valueOf((int) success));
		map.put("%enchant_destroy%", String.valueOf((int) destroy));
		map.put("%enchant_description%", description);
		map.put("%enchant_detail_description%", detailDescription);
		map.put("%enchant_applies_description%", appliesDescription);
		return map;
	}

	public static Map<String, String> getCEGroupPlaceholder(CEGroup ceGroup) {
		String display = ceGroup.getDisplay();
		String prefix = ceGroup.getPrefix();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("%group_display%", display);
		map.put("%group_prefix%", prefix);
		return map;
	}
}
