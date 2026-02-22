package com.bafmc.customenchantment.constant;

import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeAddReason;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeConfirmReason;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftExtraData.BookAddReason;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftExtraData.BookConfirmReason;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeAddReason;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeConfirmReason;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilExtraData.CEAnvilAddReason;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu.EquipmentAddReason;
import com.bafmc.customenchantment.menu.tinkerer.TinkererExtraData.TinkererAddReason;
import com.bafmc.customenchantment.menu.tinkerer.TinkererExtraData.TinkererConfirmReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("MessageKey Reason Enums")
class MessageKeyReasonTest {

	@Nested
	@DisplayName("BookAddReason")
	class BookAddReasonTest {
		@Test
		void success() {
			assertEquals("menu.book-craft.add-book.success", BookAddReason.SUCCESS.getKey());
		}

		@Test
		void fullSlot() {
			assertEquals("menu.book-craft.add-book.full-slot", BookAddReason.FULL_SLOT.getKey());
		}

		@Test
		void notMatchBook() {
			assertEquals("menu.book-craft.add-book.not-match-book", BookAddReason.NOT_MATCH_BOOK.getKey());
		}

		@Test
		void maxLevel() {
			assertEquals("menu.book-craft.add-book.max-level", BookAddReason.MAX_LEVEL.getKey());
		}
	}

	@Nested
	@DisplayName("BookConfirmReason")
	class BookConfirmReasonTest {
		@Test
		void success() {
			assertEquals("menu.book-craft.confirm.success", BookConfirmReason.SUCCESS.getKey());
		}

		@Test
		void nothing() {
			assertEquals("menu.book-craft.confirm.nothing", BookConfirmReason.NOTHING.getKey());
		}

		@Test
		void notEnoughMoney() {
			assertEquals("menu.book-craft.confirm.not-enough-money", BookConfirmReason.NOT_ENOUGH_MONEY.getKey());
		}
	}

	@Nested
	@DisplayName("TinkererAddReason")
	class TinkererAddReasonTest {
		@Test
		void success() {
			assertEquals("menu.tinkerer.add-tinkerer.success", TinkererAddReason.SUCCESS.getKey());
		}

		@Test
		void notSupportItem() {
			assertEquals("menu.tinkerer.add-tinkerer.not-support-item", TinkererAddReason.NOT_SUPPORT_ITEM.getKey());
		}

		@Test
		void fullSlot() {
			assertEquals("menu.tinkerer.add-tinkerer.full-slot", TinkererAddReason.FULL_SLOT.getKey());
		}
	}

	@Nested
	@DisplayName("TinkererConfirmReason")
	class TinkererConfirmReasonTest {
		@Test
		void success() {
			assertEquals("menu.tinkerer.confirm.success", TinkererConfirmReason.SUCCESS.getKey());
		}

		@Test
		void nothing() {
			assertEquals("menu.tinkerer.confirm.nothing", TinkererConfirmReason.NOTHING.getKey());
		}
	}

	@Nested
	@DisplayName("CEAnvilAddReason")
	class CEAnvilAddReasonTest {
		@Test
		void success() {
			assertEquals("menu.ce-anvil.add-item.success", CEAnvilAddReason.SUCCESS.getKey());
		}

		@Test
		void alreadyHasSlot1() {
			assertEquals("menu.ce-anvil.add-item.already-has-slot1", CEAnvilAddReason.ALREADY_HAS_SLOT1.getKey());
		}

		@Test
		void alreadyHasSlot2() {
			assertEquals("menu.ce-anvil.add-item.already-has-slot2", CEAnvilAddReason.ALREADY_HAS_SLOT2.getKey());
		}

		@Test
		void notSuitable() {
			assertEquals("menu.ce-anvil.add-item.not-suitable", CEAnvilAddReason.NOT_SUITABLE.getKey());
		}
	}

	@Nested
	@DisplayName("EquipmentAddReason")
	class EquipmentAddReasonTest {
		@Test
		void success() {
			assertEquals("menu.equipment.add-equipment.success", EquipmentAddReason.SUCCESS.getKey());
		}

		@Test
		void notSupportItem() {
			assertEquals("menu.equipment.add-equipment.not-support-item", EquipmentAddReason.NOT_SUPPORT_ITEM.getKey());
		}

		@Test
		void undressFirst() {
			assertEquals("menu.equipment.add-equipment.undress-first", EquipmentAddReason.UNDRESS_FIRST.getKey());
		}

		@Test
		void addExtraSlot() {
			assertEquals("menu.equipment.add-equipment.add-extra-slot", EquipmentAddReason.ADD_EXTRA_SLOT.getKey());
		}

		@Test
		void maxExtraSlot() {
			assertEquals("menu.equipment.add-equipment.max-extra-slot", EquipmentAddReason.MAX_EXTRA_SLOT.getKey());
		}

		@Test
		void duplicateExtraSlot() {
			assertEquals("menu.equipment.add-equipment.duplicate-extra-slot", EquipmentAddReason.DUPLICATE_EXTRA_SLOT.getKey());
		}

		@Test
		void nothing() {
			assertEquals("menu.equipment.add-equipment.nothing", EquipmentAddReason.NOTHING.getKey());
		}

		@Test
		void noExtraSlot() {
			assertEquals("menu.equipment.add-equipment.no-extra-slot", EquipmentAddReason.NO_EXTRA_SLOT.getKey());
		}

		@Test
		void addProtectDead() {
			assertEquals("menu.equipment.add-equipment.add-protect-dead", EquipmentAddReason.ADD_PROTECT_DEAD.getKey());
		}

		@Test
		void exceedProtectDead() {
			assertEquals("menu.equipment.add-equipment.exceed-protect-dead", EquipmentAddReason.EXCEED_PROTECT_DEAD.getKey());
		}

		@Test
		void differentProtectDead() {
			assertEquals("menu.equipment.add-equipment.different-protect-dead", EquipmentAddReason.DIFFERENT_PROTECT_DEAD.getKey());
		}
	}

	@Nested
	@DisplayName("BookUpgradeAddReason")
	class BookUpgradeAddReasonTest {
		@Test
		void success() {
			assertEquals("menu.bookupgrade.add-book.success", BookUpgradeAddReason.SUCCESS.getKey());
		}

		@Test
		void alreadyHasBook() {
			assertEquals("menu.bookupgrade.add-book.already-has-book", BookUpgradeAddReason.ALREADY_HAS_BOOK.getKey());
		}

		@Test
		void notUpgradeBook() {
			assertEquals("menu.bookupgrade.add-book.not-upgrade-book", BookUpgradeAddReason.NOT_UPGRADE_BOOK.getKey());
		}

		@Test
		void nothing() {
			assertEquals("menu.bookupgrade.add-book.nothing", BookUpgradeAddReason.NOTHING.getKey());
		}

		@Test
		void differentEnchant() {
			assertEquals("menu.bookupgrade.add-book.different-enchant", BookUpgradeAddReason.DIFFERENT_ENCHANT.getKey());
		}

		@Test
		void notPerfectBook() {
			assertEquals("menu.bookupgrade.add-book.not-perfect-book", BookUpgradeAddReason.NOT_PERFECT_BOOK.getKey());
		}

		@Test
		void notXpBook() {
			assertEquals("menu.bookupgrade.add-book.not-xp-book", BookUpgradeAddReason.NOT_XP_BOOK.getKey());
		}
	}

	@Nested
	@DisplayName("BookUpgradeConfirmReason")
	class BookUpgradeConfirmReasonTest {
		@Test
		void successXp() {
			assertEquals("menu.bookupgrade.confirm.success-xp", BookUpgradeConfirmReason.SUCCESS_XP.getKey());
		}

		@Test
		void nothing() {
			assertEquals("menu.bookupgrade.confirm.nothing", BookUpgradeConfirmReason.NOTHING.getKey());
		}

		@Test
		void failChance() {
			assertEquals("menu.bookupgrade.confirm.fail-chance", BookUpgradeConfirmReason.FAIL_CHANCE.getKey());
		}

		@Test
		void success() {
			assertEquals("menu.bookupgrade.confirm.success", BookUpgradeConfirmReason.SUCCESS.getKey());
		}
	}

	@Nested
	@DisplayName("ArtifactUpgradeAddReason")
	class ArtifactUpgradeAddReasonTest {
		@Test
		void addSelectedArtifact() {
			assertEquals("menu.artifactupgrade.add-item.add-selected-artifact", ArtifactUpgradeAddReason.ADD_SELECTED_ARTIFACT.getKey());
		}

		@Test
		void nothing() {
			assertEquals("menu.artifactupgrade.add-item.nothing", ArtifactUpgradeAddReason.NOTHING.getKey());
		}

		@Test
		void maxLevel() {
			assertEquals("menu.artifactupgrade.add-item.max-level", ArtifactUpgradeAddReason.MAX_LEVEL.getKey());
		}

		@Test
		void addIngredient() {
			assertEquals("menu.artifactupgrade.add-item.add-ingredient", ArtifactUpgradeAddReason.ADD_INGREDIENT.getKey());
		}

		@Test
		void maxIngredient() {
			assertEquals("menu.artifactupgrade.add-item.max-ingredient", ArtifactUpgradeAddReason.MAX_INGREDIENT.getKey());
		}

		@Test
		void notArtifact() {
			assertEquals("menu.artifactupgrade.add-item.not-artifact", ArtifactUpgradeAddReason.NOT_ARTIFACT.getKey());
		}

		@Test
		void notIngredient() {
			assertEquals("menu.artifactupgrade.add-item.not-ingredient", ArtifactUpgradeAddReason.NOT_INGREDIENT.getKey());
		}
	}

	@Nested
	@DisplayName("ArtifactUpgradeConfirmReason")
	class ArtifactUpgradeConfirmReasonTest {
		@Test
		void nothing() {
			assertEquals("menu.artifactupgrade.confirm.nothing", ArtifactUpgradeConfirmReason.NOTHING.getKey());
		}

		@Test
		void failChance() {
			assertEquals("menu.artifactupgrade.confirm.fail-chance", ArtifactUpgradeConfirmReason.FAIL_CHANCE.getKey());
		}

		@Test
		void success() {
			assertEquals("menu.artifactupgrade.confirm.success", ArtifactUpgradeConfirmReason.SUCCESS.getKey());
		}

		@Test
		void noIngredient() {
			assertEquals("menu.artifactupgrade.confirm.no-ingredient", ArtifactUpgradeConfirmReason.NO_INGREDIENT.getKey());
		}

		@Test
		void noSelectedArtifact() {
			assertEquals("menu.artifactupgrade.confirm.no-selected-artifact", ArtifactUpgradeConfirmReason.NO_SELECTED_ARTIFACT.getKey());
		}
	}
}
