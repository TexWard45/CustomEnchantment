package com.bafmc.customenchantment.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CEConstants")
class CEConstantsTest {

	@Nested
	@DisplayName("Sentinel")
	class SentinelTest {
		@Test
		void remove() {
			assertEquals("__REMOVE__", CEConstants.Sentinel.REMOVE);
		}

		@Test
		void loreRemover() {
			assertEquals("__LORE_REMOVER__", CEConstants.Sentinel.LORE_REMOVER);
		}

		@Test
		void blankLore() {
			assertEquals("blank_lore", CEConstants.Sentinel.BLANK_LORE);
		}

		@Test
		void lowerBlankLore() {
			assertEquals("lower_blank_lore", CEConstants.Sentinel.LOWER_BLANK_LORE);
		}

		@Test
		void upperBlankLore() {
			assertEquals("upper_blank_lore", CEConstants.Sentinel.UPPER_BLANK_LORE);
		}
	}

	@Nested
	@DisplayName("RewardType")
	class RewardTypeTest {
		@Test
		void exp() {
			assertEquals("EXP", CEConstants.RewardType.EXP);
		}

		@Test
		void money() {
			assertEquals("MONEY", CEConstants.RewardType.MONEY);
		}

		@Test
		void mobSlayerExp() {
			assertEquals("MOB_SLAYER_EXP", CEConstants.RewardType.MOB_SLAYER_EXP);
		}

		@Test
		void msExp() {
			assertEquals("MS_EXP", CEConstants.RewardType.MS_EXP);
		}

		@Test
		void duplicate() {
			assertEquals("DUPLICATE", CEConstants.RewardType.DUPLICATE);
		}
	}

	@Nested
	@DisplayName("EquipmentSuffix")
	class EquipmentSuffixTest {
		@Test
		void swap() {
			assertEquals("-swap", CEConstants.EquipmentSuffix.SWAP);
		}

		@Test
		void noSkin() {
			assertEquals("-no-skin", CEConstants.EquipmentSuffix.NO_SKIN);
		}

		@Test
		void equip() {
			assertEquals("-equip", CEConstants.EquipmentSuffix.EQUIP);
		}

		@Test
		void unify() {
			assertEquals("-unify", CEConstants.EquipmentSuffix.UNIFY);
		}
	}

	@Nested
	@DisplayName("ItemPrefix")
	class ItemPrefixTest {
		@Test
		void mask() {
			assertEquals("mask-", CEConstants.ItemPrefix.MASK);
		}

		@Test
		void skin() {
			assertEquals("skin-", CEConstants.ItemPrefix.SKIN);
		}

		@Test
		void artifact() {
			assertEquals("artifact-", CEConstants.ItemPrefix.ARTIFACT);
		}

		@Test
		void banner() {
			assertEquals("banner-", CEConstants.ItemPrefix.BANNER);
		}

		@Test
		void outfit() {
			assertEquals("outfit-", CEConstants.ItemPrefix.OUTFIT);
		}

		@Test
		void sigil() {
			assertEquals("sigil-", CEConstants.ItemPrefix.SIGIL);
		}
	}

	@Nested
	@DisplayName("EquipmentType")
	class EquipmentTypeTest {
		@Test
		void wings() {
			assertEquals("WINGS", CEConstants.EquipmentType.WINGS);
		}
	}

	@Nested
	@DisplayName("ArmorType")
	class ArmorTypeTest {
		@Test
		void auto() {
			assertEquals("AUTO", CEConstants.ArmorType.AUTO);
		}
	}

	@Nested
	@DisplayName("Placeholder")
	class PlaceholderTest {
		@Test
		void playerName() {
			assertEquals("%player_name%", CEConstants.Placeholder.PLAYER_NAME);
		}

		@Test
		void player() {
			assertEquals("%player%", CEConstants.Placeholder.PLAYER);
		}

		@Test
		void playerHealth() {
			assertEquals("%player_health%", CEConstants.Placeholder.PLAYER_HEALTH);
		}

		@Test
		void playerValue() {
			assertEquals("%player_value%", CEConstants.Placeholder.PLAYER_VALUE);
		}

		@Test
		void playerMaxValue() {
			assertEquals("%player_max_value%", CEConstants.Placeholder.PLAYER_MAX_VALUE);
		}

		@Test
		void enemyName() {
			assertEquals("%enemy_name%", CEConstants.Placeholder.ENEMY_NAME);
		}

		@Test
		void enemy() {
			assertEquals("%enemy%", CEConstants.Placeholder.ENEMY);
		}

		@Test
		void enemyHealth() {
			assertEquals("%enemy_health%", CEConstants.Placeholder.ENEMY_HEALTH);
		}

		@Test
		void enemyValue() {
			assertEquals("%enemy_value%", CEConstants.Placeholder.ENEMY_VALUE);
		}

		@Test
		void enemyMaxValue() {
			assertEquals("%enemy_max_value%", CEConstants.Placeholder.ENEMY_MAX_VALUE);
		}

		@Test
		void entityName() {
			assertEquals("%entity_name%", CEConstants.Placeholder.ENTITY_NAME);
		}

		@Test
		void distance() {
			assertEquals("%distance%", CEConstants.Placeholder.DISTANCE);
		}

		@Test
		void enchantDisplay() {
			assertEquals("%enchant_display%", CEConstants.Placeholder.ENCHANT_DISPLAY);
		}

		@Test
		void enchantDisplayHalf1() {
			assertEquals("%enchant_display_half_1%", CEConstants.Placeholder.ENCHANT_DISPLAY_HALF_1);
		}

		@Test
		void enchantDisplayHalf2() {
			assertEquals("%enchant_display_half_2%", CEConstants.Placeholder.ENCHANT_DISPLAY_HALF_2);
		}

		@Test
		void enchantLevel() {
			assertEquals("%enchant_level%", CEConstants.Placeholder.ENCHANT_LEVEL);
		}

		@Test
		void enchantSuccess() {
			assertEquals("%enchant_success%", CEConstants.Placeholder.ENCHANT_SUCCESS);
		}

		@Test
		void enchantDestroy() {
			assertEquals("%enchant_destroy%", CEConstants.Placeholder.ENCHANT_DESTROY);
		}

		@Test
		void enchantXp() {
			assertEquals("%enchant_xp%", CEConstants.Placeholder.ENCHANT_XP);
		}

		@Test
		void enchantRequiredXp() {
			assertEquals("%enchant_required_xp%", CEConstants.Placeholder.ENCHANT_REQUIRED_XP);
		}

		@Test
		void enchantDescription() {
			assertEquals("%enchant_description%", CEConstants.Placeholder.ENCHANT_DESCRIPTION);
		}

		@Test
		void enchantDetailDescription() {
			assertEquals("%enchant_detail_description%", CEConstants.Placeholder.ENCHANT_DETAIL_DESCRIPTION);
		}

		@Test
		void enchantAppliesDescription() {
			assertEquals("%enchant_applies_description%", CEConstants.Placeholder.ENCHANT_APPLIES_DESCRIPTION);
		}

		@Test
		void enchantProgress() {
			assertEquals("%enchant_progress%", CEConstants.Placeholder.ENCHANT_PROGRESS);
		}

		@Test
		void enchantFutureXp() {
			assertEquals("%enchant_future_xp%", CEConstants.Placeholder.ENCHANT_FUTURE_XP);
		}

		@Test
		void enchantFutureXp1() {
			assertEquals("%enchant_future_xp1%", CEConstants.Placeholder.ENCHANT_FUTURE_XP1);
		}

		@Test
		void enchantFutureXp2() {
			assertEquals("%enchant_future_xp2%", CEConstants.Placeholder.ENCHANT_FUTURE_XP2);
		}

		@Test
		void enchantFutureProgress() {
			assertEquals("%enchant_future_progress%", CEConstants.Placeholder.ENCHANT_FUTURE_PROGRESS);
		}

		@Test
		void enchantList() {
			assertEquals("%enchant_list%", CEConstants.Placeholder.ENCHANT_LIST);
		}

		@Test
		void groupEnchantDisplay() {
			assertEquals("%group_enchant_display%", CEConstants.Placeholder.GROUP_ENCHANT_DISPLAY);
		}

		@Test
		void groupBookDisplay() {
			assertEquals("%group_book_display%", CEConstants.Placeholder.GROUP_BOOK_DISPLAY);
		}

		@Test
		void groupBookDisplayMaxLevel() {
			assertEquals("%group_book_display_max_level%", CEConstants.Placeholder.GROUP_BOOK_DISPLAY_MAX_LEVEL);
		}

		@Test
		void groupDisplay() {
			assertEquals("%group_display%", CEConstants.Placeholder.GROUP_DISPLAY);
		}

		@Test
		void groupPrefix() {
			assertEquals("%group_prefix%", CEConstants.Placeholder.GROUP_PREFIX);
		}

		@Test
		void gemDisplay() {
			assertEquals("%gem_display%", CEConstants.Placeholder.GEM_DISPLAY);
		}

		@Test
		void display() {
			assertEquals("%display%", CEConstants.Placeholder.DISPLAY);
		}

		@Test
		void amount() {
			assertEquals("%amount%", CEConstants.Placeholder.AMOUNT);
		}

		@Test
		void operation() {
			assertEquals("%operation%", CEConstants.Placeholder.OPERATION);
		}

		@Test
		void nametag() {
			assertEquals("%nametag%", CEConstants.Placeholder.NAMETAG);
		}

		@Test
		void damage() {
			assertEquals("%damage%", CEConstants.Placeholder.DAMAGE);
		}

		@Test
		void time() {
			assertEquals("%time%", CEConstants.Placeholder.TIME);
		}

		@Test
		void success() {
			assertEquals("%success%", CEConstants.Placeholder.SUCCESS);
		}

		@Test
		void destroy() {
			assertEquals("%destroy%", CEConstants.Placeholder.DESTROY);
		}

		@Test
		void money() {
			assertEquals("%money%", CEConstants.Placeholder.MONEY);
		}

		@Test
		void randomId() {
			assertEquals("%random_id%", CEConstants.Placeholder.RANDOM_ID);
		}
	}

	@Nested
	@DisplayName("ItemPlaceholder")
	class ItemPlaceholderTest {
		@Test
		void itemDisplay() {
			assertEquals("{item_display}", CEConstants.ItemPlaceholder.ITEM_DISPLAY);
		}

		@Test
		void itemLore() {
			assertEquals("{item_lore}", CEConstants.ItemPlaceholder.ITEM_LORE);
		}

		@Test
		void display() {
			assertEquals("{display}", CEConstants.ItemPlaceholder.DISPLAY);
		}

		@Test
		void chance() {
			assertEquals("{chance}", CEConstants.ItemPlaceholder.CHANCE);
		}

		@Test
		void level() {
			assertEquals("{level}", CEConstants.ItemPlaceholder.LEVEL);
		}

		@Test
		void levelColor() {
			assertEquals("{level_color}", CEConstants.ItemPlaceholder.LEVEL_COLOR);
		}

		@Test
		void levelColorBold() {
			assertEquals("{level_color_bold}", CEConstants.ItemPlaceholder.LEVEL_COLOR_BOLD);
		}

		@Test
		void gemAppliesDescription() {
			assertEquals("{gem_applies_description}", CEConstants.ItemPlaceholder.GEM_APPLIES_DESCRIPTION);
		}

		@Test
		void gemLevel() {
			assertEquals("{gem_level}", CEConstants.ItemPlaceholder.GEM_LEVEL);
		}

		@Test
		void limit() {
			assertEquals("{limit}", CEConstants.ItemPlaceholder.LIMIT);
		}

		@Test
		void requirementDescription() {
			assertEquals("{requirement_description}", CEConstants.ItemPlaceholder.REQUIREMENT_DESCRIPTION);
		}

		@Test
		void playerName() {
			assertEquals("{player_name}", CEConstants.ItemPlaceholder.PLAYER_NAME);
		}

		@Test
		void artifactDisplay() {
			assertEquals("{artifact_display}", CEConstants.ItemPlaceholder.ARTIFACT_DISPLAY);
		}

		@Test
		void bookDisplay() {
			assertEquals("{book_display}", CEConstants.ItemPlaceholder.BOOK_DISPLAY);
		}

		@Test
		void timeLeft() {
			assertEquals("{time_left}", CEConstants.ItemPlaceholder.TIME_LEFT);
		}

		@Test
		void amount() {
			assertEquals("{amount}", CEConstants.ItemPlaceholder.AMOUNT);
		}
	}

	@Nested
	@DisplayName("FilterDirective")
	class FilterDirectiveTest {
		@Test
		void putGroup() {
			assertEquals("PUT_GROUP", CEConstants.FilterDirective.PUT_GROUP);
		}

		@Test
		void removeGroup() {
			assertEquals("REMOVE_GROUP", CEConstants.FilterDirective.REMOVE_GROUP);
		}

		@Test
		void addEnchant() {
			assertEquals("ADD_ENCHANT", CEConstants.FilterDirective.ADD_ENCHANT);
		}

		@Test
		void removeEnchant() {
			assertEquals("REMOVE_ENCHANT", CEConstants.FilterDirective.REMOVE_ENCHANT);
		}

		@Test
		void minLevel() {
			assertEquals("MIN_LEVEL", CEConstants.FilterDirective.MIN_LEVEL);
		}

		@Test
		void maxLevel() {
			assertEquals("MAX_LEVEL", CEConstants.FilterDirective.MAX_LEVEL);
		}

		@Test
		void success() {
			assertEquals("SUCCESS", CEConstants.FilterDirective.SUCCESS);
		}

		@Test
		void destroy() {
			assertEquals("DESTROY", CEConstants.FilterDirective.DESTROY);
		}

		@Test
		void levelSigma() {
			assertEquals("LEVEL_SIGMA", CEConstants.FilterDirective.LEVEL_SIGMA);
		}
	}

	@Nested
	@DisplayName("DataKey")
	class DataKeyTest {
		@Test
		void pattern() {
			assertEquals("pattern", CEConstants.DataKey.PATTERN);
		}

		@Test
		void enchant() {
			assertEquals("enchant", CEConstants.DataKey.ENCHANT);
		}

		@Test
		void level() {
			assertEquals("level", CEConstants.DataKey.LEVEL);
		}

		@Test
		void success() {
			assertEquals("success", CEConstants.DataKey.SUCCESS);
		}

		@Test
		void destroy() {
			assertEquals("destroy", CEConstants.DataKey.DESTROY);
		}

		@Test
		void xp() {
			assertEquals("xp", CEConstants.DataKey.XP);
		}

		@Test
		void weapon() {
			assertEquals("weapon", CEConstants.DataKey.WEAPON);
		}

		@Test
		void world() {
			assertEquals("world", CEConstants.DataKey.WORLD);
		}

		@Test
		void x() {
			assertEquals("x", CEConstants.DataKey.X);
		}

		@Test
		void y() {
			assertEquals("y", CEConstants.DataKey.Y);
		}

		@Test
		void z() {
			assertEquals("z", CEConstants.DataKey.Z);
		}

		@Test
		void oldSuccess() {
			assertEquals("old-success", CEConstants.DataKey.OLD_SUCCESS);
		}

		@Test
		void oldDestroy() {
			assertEquals("old-destroy", CEConstants.DataKey.OLD_DESTROY);
		}

		@Test
		void newSuccess() {
			assertEquals("new-success", CEConstants.DataKey.NEW_SUCCESS);
		}

		@Test
		void newDestroy() {
			assertEquals("new-destroy", CEConstants.DataKey.NEW_DESTROY);
		}

		@Test
		void display() {
			assertEquals("display", CEConstants.DataKey.DISPLAY);
		}

		@Test
		void displayAllLevel() {
			assertEquals("display_all_level", CEConstants.DataKey.DISPLAY_ALL_LEVEL);
		}

		@Test
		void displayLevel1() {
			assertEquals("display_level_1", CEConstants.DataKey.DISPLAY_LEVEL_1);
		}

		@Test
		void description() {
			assertEquals("description", CEConstants.DataKey.DESCRIPTION);
		}

		@Test
		void randomDescription() {
			assertEquals("random_description", CEConstants.DataKey.RANDOM_DESCRIPTION);
		}

		@Test
		void name() {
			assertEquals("name", CEConstants.DataKey.NAME);
		}

		@Test
		void citemName() {
			assertEquals("citem_name", CEConstants.DataKey.CITEM_NAME);
		}

		@Test
		void oldDisplay() {
			assertEquals("old-display", CEConstants.DataKey.OLD_DISPLAY);
		}

		@Test
		void newDisplay() {
			assertEquals("new-display", CEConstants.DataKey.NEW_DISPLAY);
		}
	}

	@Nested
	@DisplayName("MenuItemType")
	class MenuItemTypeTest {
		@Test
		void tinker() {
			assertEquals("tinker", CEConstants.MenuItemType.TINKER);
		}

		@Test
		void accept() {
			assertEquals("accept", CEConstants.MenuItemType.ACCEPT);
		}

		@Test
		void book() {
			assertEquals("book", CEConstants.MenuItemType.BOOK);
		}

		@Test
		void wingsEquipment() {
			assertEquals("wings-equipment", CEConstants.MenuItemType.WINGS_EQUIPMENT);
		}

		@Test
		void protectDeadEquipment() {
			assertEquals("protect-dead-equipment", CEConstants.MenuItemType.PROTECT_DEAD_EQUIPMENT);
		}

		@Test
		void playerInfoEquipment() {
			assertEquals("player-info-equipment", CEConstants.MenuItemType.PLAYER_INFO_EQUIPMENT);
		}
	}

	@Nested
	@DisplayName("ItemDisplayType")
	class ItemDisplayTypeTest {
		@Test
		void storage() {
			assertEquals("storage", CEConstants.ItemDisplayType.STORAGE);
		}

		@Test
		void gem() {
			assertEquals("gem", CEConstants.ItemDisplayType.GEM);
		}

		@Test
		void book() {
			assertEquals("book", CEConstants.ItemDisplayType.BOOK);
		}
	}
}
