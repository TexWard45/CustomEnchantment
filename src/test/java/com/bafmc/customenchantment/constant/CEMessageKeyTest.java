package com.bafmc.customenchantment.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CEMessageKey")
class CEMessageKeyTest {

	@Nested
	@DisplayName("Command: NameTag")
	class CommandNameTagTest {
		@Test
		void help1() {
			assertEquals("command.nametag.help1", CEMessageKey.COMMAND_NAMETAG_HELP1.getKey());
		}

		@Test
		void help2() {
			assertEquals("command.nametag.help2", CEMessageKey.COMMAND_NAMETAG_HELP2.getKey());
		}

		@Test
		void help3() {
			assertEquals("command.nametag.help3", CEMessageKey.COMMAND_NAMETAG_HELP3.getKey());
		}

		@Test
		void notSet() {
			assertEquals("command.nametag.not-set", CEMessageKey.COMMAND_NAMETAG_NOT_SET.getKey());
		}

		@Test
		void maxLength() {
			assertEquals("command.nametag.max-length", CEMessageKey.COMMAND_NAMETAG_MAX_LENGTH.getKey());
		}

		@Test
		void show() {
			assertEquals("command.nametag.show", CEMessageKey.COMMAND_NAMETAG_SHOW.getKey());
		}

		@Test
		void set() {
			assertEquals("command.nametag.set", CEMessageKey.COMMAND_NAMETAG_SET.getKey());
		}

		@Test
		void preview() {
			assertEquals("command.nametag.preview", CEMessageKey.COMMAND_NAMETAG_PREVIEW.getKey());
		}
	}

	@Nested
	@DisplayName("Command: CEFilter")
	class CommandCEFilterTest {
		@Test
		void addNotFound() {
			assertEquals("command.cefilter.add.not-found", CEMessageKey.COMMAND_CEFILTER_ADD_NOT_FOUND.getKey());
		}

		@Test
		void addSuccess() {
			assertEquals("command.cefilter.add.success", CEMessageKey.COMMAND_CEFILTER_ADD_SUCCESS.getKey());
		}

		@Test
		void removeNotFound() {
			assertEquals("command.cefilter.remove.not-found", CEMessageKey.COMMAND_CEFILTER_REMOVE_NOT_FOUND.getKey());
		}

		@Test
		void removeSuccess() {
			assertEquals("command.cefilter.remove.success", CEMessageKey.COMMAND_CEFILTER_REMOVE_SUCCESS.getKey());
		}

		@Test
		void clearEmpty() {
			assertEquals("command.cefilter.clear.empty", CEMessageKey.COMMAND_CEFILTER_CLEAR_EMPTY.getKey());
		}

		@Test
		void clearSuccess() {
			assertEquals("command.cefilter.clear.success", CEMessageKey.COMMAND_CEFILTER_CLEAR_SUCCESS.getKey());
		}

		@Test
		void listEmpty() {
			assertEquals("command.cefilter.list.empty", CEMessageKey.COMMAND_CEFILTER_LIST_EMPTY.getKey());
		}

		@Test
		void listSuccess() {
			assertEquals("command.cefilter.list.success", CEMessageKey.COMMAND_CEFILTER_LIST_SUCCESS.getKey());
		}

		@Test
		void help() {
			assertEquals("command.cefilter.help", CEMessageKey.COMMAND_CEFILTER_HELP.getKey());
		}

		@Test
		void notify_() {
			assertEquals("command.cefilter.notify", CEMessageKey.COMMAND_CEFILTER_NOTIFY.getKey());
		}
	}

	@Nested
	@DisplayName("Menu: BookCraft")
	class MenuBookCraftTest {
		@Test
		void mustBeOne() {
			assertEquals("menu.book-craft.must-be-one", CEMessageKey.MENU_BOOK_CRAFT_MUST_BE_ONE.getKey());
		}

		@Test
		void notSupportItem() {
			assertEquals("menu.book-craft.not-support-item", CEMessageKey.MENU_BOOK_CRAFT_NOT_SUPPORT_ITEM.getKey());
		}
	}

	@Nested
	@DisplayName("Menu: Tinkerer")
	class MenuTinkererTest {
		@Test
		void notSupportItem() {
			assertEquals("menu.tinkerer.not-support-item", CEMessageKey.MENU_TINKERER_NOT_SUPPORT_ITEM.getKey());
		}
	}

	@Nested
	@DisplayName("Menu: Equipment")
	class MenuEquipmentTest {
		@Test
		void returnItemNoEmptySlot() {
			assertEquals("menu.equipment.return-item.no-empty-slot", CEMessageKey.MENU_EQUIPMENT_RETURN_ITEM_NO_EMPTY_SLOT.getKey());
		}
	}

	@Nested
	@DisplayName("Combat / Attribute")
	class CombatAttributeTest {
		@Test
		void combatRequireWeapon() {
			assertEquals("combat.require-weapon", CEMessageKey.COMBAT_REQUIRE_WEAPON.getKey());
		}

		@Test
		void attributeCriticalSuccess() {
			assertEquals("attribute.critical.success", CEMessageKey.ATTRIBUTE_CRITICAL_SUCCESS.getKey());
		}

		@Test
		void attributeDodgeSuccess() {
			assertEquals("attribute.dodge.success", CEMessageKey.ATTRIBUTE_DODGE_SUCCESS.getKey());
		}
	}

	@Nested
	@DisplayName("CE-Item: Extra Slot")
	class CEItemExtraSlotTest {
		@Test
		void duplicate() {
			assertEquals("ce-item.extra-slot.duplicate", CEMessageKey.CE_ITEM_EXTRA_SLOT_DUPLICATE.getKey());
		}

		@Test
		void exceedUseAmount() {
			assertEquals("ce-item.extra-slot.exceed-use-amount", CEMessageKey.CE_ITEM_EXTRA_SLOT_EXCEED_USE_AMOUNT.getKey());
		}

		@Test
		void active() {
			assertEquals("ce-item.extra-slot.active", CEMessageKey.CE_ITEM_EXTRA_SLOT_ACTIVE.getKey());
		}

		@Test
		void deactive() {
			assertEquals("ce-item.extra-slot.deactive", CEMessageKey.CE_ITEM_EXTRA_SLOT_DEACTIVE.getKey());
		}
	}

	@Nested
	@DisplayName("CE-Item: Protect Dead")
	class CEItemProtectDeadTest {
		@Test
		void useAdvanced() {
			assertEquals("ce-item.protectdead.use-advanced", CEMessageKey.CE_ITEM_PROTECTDEAD_USE_ADVANCED.getKey());
		}
	}

	@Nested
	@DisplayName("ceItem() factory")
	class CeItemFactoryTest {
		@Test
		void randomBookFull() {
			MessageKey key = CEMessageKey.ceItem("random-book", "full");
			assertEquals("ce-item.random-book.full", key.getKey());
		}

		@Test
		void randomBookSuccess() {
			MessageKey key = CEMessageKey.ceItem("random-book", "success");
			assertEquals("ce-item.random-book.success", key.getKey());
		}

		@Test
		void randomBookSuccessTinker() {
			MessageKey key = CEMessageKey.ceItem("random-book", "success-tinker");
			assertEquals("ce-item.random-book.success-tinker", key.getKey());
		}

		@Test
		void bookSuccess() {
			MessageKey key = CEMessageKey.ceItem("book", "success");
			assertEquals("ce-item.book.success", key.getKey());
		}
	}
}
