package com.bafmc.customenchantment.constant;

public class CEConstants {

	public static class Sentinel {
		public static final String REMOVE = "__REMOVE__";
		public static final String LORE_REMOVER = "__LORE_REMOVER__";
		public static final String BLANK_LORE = "blank_lore";
		public static final String LOWER_BLANK_LORE = "lower_blank_lore";
		public static final String UPPER_BLANK_LORE = "upper_blank_lore";
	}

	public static class RewardType {
		public static final String EXP = "EXP";
		public static final String MONEY = "MONEY";
		public static final String MOB_SLAYER_EXP = "MOB_SLAYER_EXP";
		public static final String MS_EXP = "MS_EXP";
		public static final String DUPLICATE = "DUPLICATE";
	}

	public static class EquipmentSuffix {
		public static final String SWAP = "-swap";
		public static final String NO_SKIN = "-no-skin";
		public static final String EQUIP = "-equip";
		public static final String UNIFY = "-unify";
	}

	public static class ItemPrefix {
		public static final String MASK = "mask-";
		public static final String SKIN = "skin-";
		public static final String ARTIFACT = "artifact-";
		public static final String BANNER = "banner-";
		public static final String OUTFIT = "outfit-";
		public static final String SIGIL = "sigil-";
	}

	public static class EquipmentType {
		public static final String WINGS = "WINGS";
	}

	public static class ArmorType {
		public static final String AUTO = "AUTO";
	}

	public static class Placeholder {
		// Player
		public static final String PLAYER_NAME = "%player_name%";
		public static final String PLAYER = "%player%";
		public static final String PLAYER_HEALTH = "%player_health%";
		public static final String PLAYER_VALUE = "%player_value%";
		public static final String PLAYER_MAX_VALUE = "%player_max_value%";

		// Enemy
		public static final String ENEMY_NAME = "%enemy_name%";
		public static final String ENEMY = "%enemy%";
		public static final String ENEMY_HEALTH = "%enemy_health%";
		public static final String ENEMY_VALUE = "%enemy_value%";
		public static final String ENEMY_MAX_VALUE = "%enemy_max_value%";

		// Entity
		public static final String ENTITY_NAME = "%entity_name%";
		public static final String DISTANCE = "%distance%";

		// Enchant
		public static final String ENCHANT_DISPLAY = "%enchant_display%";
		public static final String ENCHANT_DISPLAY_HALF_1 = "%enchant_display_half_1%";
		public static final String ENCHANT_DISPLAY_HALF_2 = "%enchant_display_half_2%";
		public static final String ENCHANT_LEVEL = "%enchant_level%";
		public static final String ENCHANT_SUCCESS = "%enchant_success%";
		public static final String ENCHANT_DESTROY = "%enchant_destroy%";
		public static final String ENCHANT_XP = "%enchant_xp%";
		public static final String ENCHANT_REQUIRED_XP = "%enchant_required_xp%";
		public static final String ENCHANT_DESCRIPTION = "%enchant_description%";
		public static final String ENCHANT_DETAIL_DESCRIPTION = "%enchant_detail_description%";
		public static final String ENCHANT_APPLIES_DESCRIPTION = "%enchant_applies_description%";
		public static final String ENCHANT_PROGRESS = "%enchant_progress%";
		public static final String ENCHANT_FUTURE_XP = "%enchant_future_xp%";
		public static final String ENCHANT_FUTURE_XP1 = "%enchant_future_xp1%";
		public static final String ENCHANT_FUTURE_XP2 = "%enchant_future_xp2%";
		public static final String ENCHANT_FUTURE_PROGRESS = "%enchant_future_progress%";
		public static final String ENCHANT_LIST = "%enchant_list%";

		// Group
		public static final String GROUP_ENCHANT_DISPLAY = "%group_enchant_display%";
		public static final String GROUP_BOOK_DISPLAY = "%group_book_display%";
		public static final String GROUP_BOOK_DISPLAY_MAX_LEVEL = "%group_book_display_max_level%";
		public static final String GROUP_DISPLAY = "%group_display%";
		public static final String GROUP_PREFIX = "%group_prefix%";

		// Item display
		public static final String GEM_DISPLAY = "%gem_display%";
		public static final String DISPLAY = "%display%";
		public static final String AMOUNT = "%amount%";
		public static final String OPERATION = "%operation%";
		public static final String NAMETAG = "%nametag%";

		// Combat
		public static final String DAMAGE = "%damage%";
		public static final String TIME = "%time%";

		// Utility
		public static final String SUCCESS = "%success%";
		public static final String DESTROY = "%destroy%";
		public static final String MONEY = "%money%";
		public static final String RANDOM_ID = "%random_id%";
	}

	public static class ItemPlaceholder {
		public static final String ITEM_DISPLAY = "{item_display}";
		public static final String ITEM_LORE = "{item_lore}";
		public static final String DISPLAY = "{display}";
		public static final String CHANCE = "{chance}";
		public static final String LEVEL = "{level}";
		public static final String LEVEL_COLOR = "{level_color}";
		public static final String LEVEL_COLOR_BOLD = "{level_color_bold}";
		public static final String GEM_APPLIES_DESCRIPTION = "{gem_applies_description}";
		public static final String GEM_LEVEL = "{gem_level}";
		public static final String LIMIT = "{limit}";
		public static final String REQUIREMENT_DESCRIPTION = "{requirement_description}";
		public static final String PLAYER_NAME = "{player_name}";
		public static final String ARTIFACT_DISPLAY = "{artifact_display}";
		public static final String BOOK_DISPLAY = "{book_display}";
		public static final String TIME_LEFT = "{time_left}";
		public static final String AMOUNT = "{amount}";
	}

	public static class FilterDirective {
		public static final String PUT_GROUP = "PUT_GROUP";
		public static final String REMOVE_GROUP = "REMOVE_GROUP";
		public static final String ADD_ENCHANT = "ADD_ENCHANT";
		public static final String REMOVE_ENCHANT = "REMOVE_ENCHANT";
		public static final String MIN_LEVEL = "MIN_LEVEL";
		public static final String MAX_LEVEL = "MAX_LEVEL";
		public static final String SUCCESS = "SUCCESS";
		public static final String DESTROY = "DESTROY";
		public static final String LEVEL_SIGMA = "LEVEL_SIGMA";
	}

	public static class DataKey {
		public static final String PATTERN = "pattern";
		public static final String ENCHANT = "enchant";
		public static final String LEVEL = "level";
		public static final String SUCCESS = "success";
		public static final String DESTROY = "destroy";
		public static final String XP = "xp";
		public static final String WEAPON = "weapon";
		public static final String WORLD = "world";
		public static final String X = "x";
		public static final String Y = "y";
		public static final String Z = "z";
		public static final String OLD_SUCCESS = "old-success";
		public static final String OLD_DESTROY = "old-destroy";
		public static final String NEW_SUCCESS = "new-success";
		public static final String NEW_DESTROY = "new-destroy";
		public static final String DISPLAY = "display";
		public static final String DISPLAY_ALL_LEVEL = "display_all_level";
		public static final String DISPLAY_LEVEL_1 = "display_level_1";
		public static final String DESCRIPTION = "description";
		public static final String RANDOM_DESCRIPTION = "random_description";
		public static final String NAME = "name";
		public static final String CITEM_NAME = "citem_name";
		public static final String OLD_DISPLAY = "old-display";
		public static final String NEW_DISPLAY = "new-display";
	}

	public static class MenuItemType {
		public static final String TINKER = "tinker";
		public static final String ACCEPT = "accept";
		public static final String BOOK = "book";
		public static final String WINGS_EQUIPMENT = "wings-equipment";
		public static final String PROTECT_DEAD_EQUIPMENT = "protect-dead-equipment";
		public static final String PLAYER_INFO_EQUIPMENT = "player-info-equipment";
	}

	public static class ItemDisplayType {
		public static final String STORAGE = "storage";
		public static final String GEM = "gem";
		public static final String BOOK = "book";
	}
}
