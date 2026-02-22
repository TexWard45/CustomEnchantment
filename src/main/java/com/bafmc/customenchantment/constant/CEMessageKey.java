package com.bafmc.customenchantment.constant;

/**
 * Centralized message key constants for {@link com.bafmc.customenchantment.CustomEnchantmentMessage}.
 *
 * <p>Static keys are defined as enum constants. Dynamic keys that depend on
 * {@code CEItem.getType()} are created via the {@link #ceItem(String, String)} factory.</p>
 */
public enum CEMessageKey implements MessageKey {

	// ── Command: NameTag ──
	COMMAND_NAMETAG_HELP1("command.nametag.help1"),
	COMMAND_NAMETAG_HELP2("command.nametag.help2"),
	COMMAND_NAMETAG_HELP3("command.nametag.help3"),
	COMMAND_NAMETAG_NOT_SET("command.nametag.not-set"),
	COMMAND_NAMETAG_MAX_LENGTH("command.nametag.max-length"),
	COMMAND_NAMETAG_SHOW("command.nametag.show"),
	COMMAND_NAMETAG_SET("command.nametag.set"),
	COMMAND_NAMETAG_PREVIEW("command.nametag.preview"),

	// ── Command: CEFilter ──
	COMMAND_CEFILTER_ADD_NOT_FOUND("command.cefilter.add.not-found"),
	COMMAND_CEFILTER_ADD_SUCCESS("command.cefilter.add.success"),
	COMMAND_CEFILTER_REMOVE_NOT_FOUND("command.cefilter.remove.not-found"),
	COMMAND_CEFILTER_REMOVE_SUCCESS("command.cefilter.remove.success"),
	COMMAND_CEFILTER_CLEAR_EMPTY("command.cefilter.clear.empty"),
	COMMAND_CEFILTER_CLEAR_SUCCESS("command.cefilter.clear.success"),
	COMMAND_CEFILTER_LIST_EMPTY("command.cefilter.list.empty"),
	COMMAND_CEFILTER_LIST_SUCCESS("command.cefilter.list.success"),
	COMMAND_CEFILTER_HELP("command.cefilter.help"),
	COMMAND_CEFILTER_NOTIFY("command.cefilter.notify"),

	// ── Menu: BookCraft ──
	MENU_BOOK_CRAFT_MUST_BE_ONE("menu.book-craft.must-be-one"),
	MENU_BOOK_CRAFT_NOT_SUPPORT_ITEM("menu.book-craft.not-support-item"),

	// ── Menu: Tinkerer ──
	MENU_TINKERER_NOT_SUPPORT_ITEM("menu.tinkerer.not-support-item"),

	// ── Menu: Equipment ──
	MENU_EQUIPMENT_RETURN_ITEM_NO_EMPTY_SLOT("menu.equipment.return-item.no-empty-slot"),

	// ── Combat / Attribute ──
	COMBAT_REQUIRE_WEAPON("combat.require-weapon"),
	ATTRIBUTE_CRITICAL_SUCCESS("attribute.critical.success"),
	ATTRIBUTE_DODGE_SUCCESS("attribute.dodge.success"),

	// ── CE-Item: Extra Slot ──
	CE_ITEM_EXTRA_SLOT_DUPLICATE("ce-item.extra-slot.duplicate"),
	CE_ITEM_EXTRA_SLOT_EXCEED_USE_AMOUNT("ce-item.extra-slot.exceed-use-amount"),
	CE_ITEM_EXTRA_SLOT_ACTIVE("ce-item.extra-slot.active"),
	CE_ITEM_EXTRA_SLOT_DEACTIVE("ce-item.extra-slot.deactive"),

	// ── CE-Item: Protect Dead ──
	CE_ITEM_PROTECTDEAD_USE_ADVANCED("ce-item.protectdead.use-advanced");

	private final String key;

	CEMessageKey(String key) {
		this.key = key;
	}

	@Override
	public String getKey() {
		return key;
	}

	/**
	 * Creates a dynamic {@link MessageKey} for CE-item messages that depend on
	 * {@code CEItem.getType()}.
	 *
	 * <p>Example: {@code ceItem("random-book", "full")} produces {@code "ce-item.random-book.full"}.</p>
	 *
	 * @param type   the CE-item type (from {@code CEItem.getType()})
	 * @param suffix the message suffix (e.g. "full", "success", "success-tinker")
	 * @return a {@link MessageKey} with key {@code "ce-item.<type>.<suffix>"}
	 */
	public static MessageKey ceItem(String type, String suffix) {
		return () -> "ce-item." + type + "." + suffix;
	}
}
