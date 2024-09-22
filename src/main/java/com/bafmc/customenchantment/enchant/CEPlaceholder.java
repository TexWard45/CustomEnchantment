package com.bafmc.customenchantment.enchant;

import com.bafmc.bukkit.utils.NumberUtils;
import com.bafmc.customenchantment.menu.BookUpgradeMenu;
import com.bafmc.customenchantment.menu.data.BookUpgradeData;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.bukkit.utils.StringUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CEPlaceholder {
	public static String setPlaceholder(String string, Map<String, String> map) {
		for (String key : map.keySet()) {
			string = string.replace(key, map.get(key));
		}
		return string;
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
        int xp = ceSimple.getXp();
        int requiredXp = getEnchantRequiredXp(ceSimple);

        CEGroup ceGroup = ce.getCEGroup();

        String enchantDisplayGroup = ceGroup.getEnchantDisplay();
        String bookDisplayGroup = ceGroup.getBookDisplay();
        String displayGroup = ceGroup.getDisplay();
        String prefixGroup = ceGroup.getPrefix();
		String display = ce.getCEDisplay().getDefaultDisplay();
		String description = StringUtils.toString(ce.getCEDisplay().getDescription());
		String detailDescription = StringUtils.toString(ce.getCEDisplay().getDetailDescription());
		String appliesDescription = StringUtils.toString(ce.getCEDisplay().getAppliesDescription());
		String levelStr = NumberUtils.toRomanNumber(level);
        String groupBookDisplayMaxLevel = null;

        if (ce.getMaxLevel() > 1) {
            groupBookDisplayMaxLevel = bookDisplayGroup.replace("%enchant_level%", NumberUtils.toRomanNumber(1) + "-" + NumberUtils.toRomanNumber(ce.getMaxLevel()));
        }else {
            groupBookDisplayMaxLevel = bookDisplayGroup.replace("%enchant_level%", NumberUtils.toRomanNumber(1));
        }

		Map<String, String> map = new LinkedHashMap<>();
        map.put("%group_enchant_display%", enchantDisplayGroup);
        map.put("%group_book_display%", bookDisplayGroup);
        map.put("%group_book_display_max_level%", groupBookDisplayMaxLevel);
        map.put("%group_display%", displayGroup);
        map.put("%group_prefix%", prefixGroup);
		map.put("%enchant_display%", display);
        map.put("%enchant_display_half_1%", display.substring(0, display.length() / 2));
        map.put("%enchant_display_half_2%", display.substring(display.length() / 2));
		map.put("%enchant_level%", levelStr);
		map.put("%enchant_success%", String.valueOf((int) success));
		map.put("%enchant_destroy%", String.valueOf((int) destroy));
        map.put("%enchant_xp%", StringUtils.formatNumber(Math.min(xp, requiredXp)));
        map.put("%enchant_required_xp%", StringUtils.formatNumber(requiredXp));
		map.put("%enchant_description%", description);
		map.put("%enchant_detail_description%", detailDescription);
		map.put("%enchant_applies_description%", appliesDescription);
        map.put("%enchant_progress%", getEnchantProgress(ceSimple));
		return map;
	}

    public static int getEnchantRequiredXp(CESimple ceSimple) {
        BookUpgradeData bookUpgradeData = BookUpgradeMenu.getSettings().getBookUpgradeData(ceSimple.getName(), ceSimple.getLevel());
        if (bookUpgradeData == null) {
            return 0;
        }

        return bookUpgradeData.getRequiredXp();
    }

    public static String getEnchantProgress(CESimple ceSimple) {
        BookUpgradeData bookUpgradeData = BookUpgradeMenu.getSettings().getBookUpgradeData(ceSimple.getName(), ceSimple.getLevel());
        if (bookUpgradeData == null) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                builder.append("&7▌");
            }
            return builder.toString();
        }

        int xp = ceSimple.getXp();
        int requiredXp = bookUpgradeData.getRequiredXp();
        int progress = (int) Math.floor((double) xp / requiredXp * 10);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i < progress) {
                builder.append("&a▌");
            } else {
                builder.append("&7▌");
            }
        }

        return builder.toString();
    }
}
