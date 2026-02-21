package com.bafmc.customenchantment.enchant;

import com.bafmc.bukkit.utils.NumberUtils;
import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeCustomMenu;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeData;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CEPlaceholder {
	public static String setPlaceholder(String string, Map<String, String> map) {
		for (String key : map.keySet()) {
			String value = map.get(key);

			if (value == null) {
				CustomEnchantment.instance().getLogger().warning("Placeholder value is null: " + key + " in " + string);
				return null;
			}

			string = string.replace(key, map.get(key));
		}
		return string;
	}

	public static Map<String, String> getTemporaryStoragePlaceholder(PlayerTemporaryStorage storage) {
		Map<String, String> map = new LinkedHashMap<>();

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
			if (text.contains(CEConstants.Placeholder.PLAYER_NAME)) {
				map.put(CEConstants.Placeholder.PLAYER_NAME, player.getName());
				map.put(CEConstants.Placeholder.PLAYER, player.getName());
			}
		}

		if (data.getLivingEntity() != null) {
			LivingEntity entity = data.getLivingEntity();
			if (text.contains(CEConstants.Placeholder.PLAYER_HEALTH)) {
				map.put(CEConstants.Placeholder.PLAYER_HEALTH, String.valueOf(entity.getHealth()));
			}
		}

		if (data.getEnemyPlayer() != null) {
			Player enemy = data.getEnemyPlayer();
			if (text.contains(CEConstants.Placeholder.ENEMY_NAME)) {
				map.put(CEConstants.Placeholder.ENEMY_NAME, enemy.getName());
				map.put(CEConstants.Placeholder.ENEMY, enemy.getName());
			}
		}

		if (data.getEnemyLivingEntity() != null) {
			LivingEntity entity = data.getEnemyLivingEntity();
			if (text.contains(CEConstants.Placeholder.ENTITY_NAME)) {
				map.put(CEConstants.Placeholder.ENTITY_NAME, entity.getName());
			}
		}


		if (data.getEnemyLivingEntity() != null) {
			LivingEntity entity = data.getEnemyLivingEntity();
			if (text.contains(CEConstants.Placeholder.ENEMY_HEALTH)) {
				map.put(CEConstants.Placeholder.ENEMY_HEALTH, String.valueOf(entity.getHealth()));
			}
		}

		if (data.getLivingEntity() != null && data.getEnemyLivingEntity() != null && text.contains(CEConstants.Placeholder.DISTANCE)) {
			if (!data.getLivingEntity().getWorld().equals(data.getEnemyLivingEntity().getWorld())) {
				map.put(CEConstants.Placeholder.DISTANCE, String.valueOf(99999));
			} else {
				map.put(CEConstants.Placeholder.DISTANCE, String.valueOf(
						data.getLivingEntity().getLocation().distance(data.getEnemyLivingEntity().getLocation())));
			}
		}

		if (text.contains(CEConstants.Placeholder.DAMAGE)) {
			map.put(CEConstants.Placeholder.DAMAGE, String.valueOf(data.get("damage")));
		}

		if (text.contains("%damage_")) {
			// Pattern to match placeholders like %damage_x%, where x is any number
			Pattern pattern = Pattern.compile("%damage_(\\d+)%");
			Matcher matcher = pattern.matcher(text);

			while (matcher.find()) {
				// Extract the number from the placeholder (e.g., "5" in "%damage_5%")
				int percentage = Integer.parseInt(matcher.group(1));

				// Calculate the damage based on the percentage
				double calculatedDamage = (double) data.get("damage") * percentage / 100;

				// Create the placeholder string and store the formatted damage in the map
				String placeholder = matcher.group(0); // e.g., "%damage_5%"
				map.put(placeholder, StringUtils.formatNumber(calculatedDamage));
			}
		}

		if (text.contains(CEConstants.Placeholder.TIME)) {
			map.put(CEConstants.Placeholder.TIME, String.valueOf(System.currentTimeMillis()));
		}

		return map;
	}

	public static Map<String, String> getCESimplePlaceholder(CEEnchantSimple ceEnchantSimple) {
		CEEnchant ce = ceEnchantSimple.getCEEnchant();
		int level = ceEnchantSimple.getLevel();
		double success = ceEnchantSimple.getSuccess().getValue();
		double destroy = ceEnchantSimple.getDestroy().getValue();
        int xp = ceEnchantSimple.getXp();
        int requiredXp = getEnchantRequiredXp(ceEnchantSimple);

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
            groupBookDisplayMaxLevel = bookDisplayGroup.replace(CEConstants.Placeholder.ENCHANT_LEVEL, NumberUtils.toRomanNumber(1) + "-" + NumberUtils.toRomanNumber(ce.getMaxLevel()));
        }else {
            groupBookDisplayMaxLevel = bookDisplayGroup.replace(CEConstants.Placeholder.ENCHANT_LEVEL, NumberUtils.toRomanNumber(1));
        }

		Map<String, String> map = new LinkedHashMap<>();
        map.put(CEConstants.Placeholder.GROUP_ENCHANT_DISPLAY, enchantDisplayGroup);
        map.put(CEConstants.Placeholder.GROUP_BOOK_DISPLAY, bookDisplayGroup);
        map.put(CEConstants.Placeholder.GROUP_BOOK_DISPLAY_MAX_LEVEL, groupBookDisplayMaxLevel);
        map.put(CEConstants.Placeholder.GROUP_DISPLAY, displayGroup);
        map.put(CEConstants.Placeholder.GROUP_PREFIX, prefixGroup);
		map.put(CEConstants.Placeholder.ENCHANT_DISPLAY, display);
        map.put(CEConstants.Placeholder.ENCHANT_DISPLAY_HALF_1, display.substring(0, display.length() / 2));
        map.put(CEConstants.Placeholder.ENCHANT_DISPLAY_HALF_2, display.substring(display.length() / 2));
		map.put(CEConstants.Placeholder.ENCHANT_LEVEL, levelStr);
		map.put(CEConstants.Placeholder.ENCHANT_SUCCESS, String.valueOf((int) success));
		map.put(CEConstants.Placeholder.ENCHANT_DESTROY, String.valueOf((int) destroy));
        map.put(CEConstants.Placeholder.ENCHANT_XP, StringUtils.formatNumber(Math.min(xp, requiredXp)));
        map.put(CEConstants.Placeholder.ENCHANT_REQUIRED_XP, StringUtils.formatNumber(requiredXp));
		map.put(CEConstants.Placeholder.ENCHANT_DESCRIPTION, description);
		map.put(CEConstants.Placeholder.ENCHANT_DETAIL_DESCRIPTION, detailDescription);
		map.put(CEConstants.Placeholder.ENCHANT_APPLIES_DESCRIPTION, appliesDescription);
        map.put(CEConstants.Placeholder.ENCHANT_PROGRESS, getEnchantProgress(ceEnchantSimple));
		return map;
	}

    public static int getEnchantRequiredXp(CEEnchantSimple ceEnchantSimple) {
        BookUpgradeData bookUpgradeData = BookUpgradeCustomMenu.getSettings().getBookUpgradeData(ceEnchantSimple.getName(), ceEnchantSimple.getLevel());
        if (bookUpgradeData == null) {
            return 0;
        }

        return bookUpgradeData.getRequiredXp();
    }

    public static String getEnchantProgress(CEEnchantSimple ceEnchantSimple) {
        BookUpgradeData bookUpgradeData = BookUpgradeCustomMenu.getSettings().getBookUpgradeData(ceEnchantSimple.getName(), ceEnchantSimple.getLevel());
        if (bookUpgradeData == null) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                builder.append("&7▌");
            }
            return builder.toString();
        }

        int xp = ceEnchantSimple.getXp();
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
