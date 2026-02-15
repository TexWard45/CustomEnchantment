package com.bafmc.customenchantment.menu.bookcraft;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ExtraData;
import com.bafmc.customenchantment.menu.data.BookData;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Extra data for BookCraft menu - migrated to new CustomMenu BafFramework API
 *
 * Manages state for:
 * - Book slots (up to 2 books for combining)
 * - FastCraft mode result
 * - Preview state
 */
@Getter
@Setter
public class BookCraftExtraData extends ExtraData {

    /**
     * List of books placed in book slots (max 2)
     */
    private List<BookData> bookList = new ArrayList<>();

    /**
     * FastCraft instance for auto-upgrade logic (refactored version)
     */
    private FastCraftRefactored fastCraft;

    /**
     * Enum for add book result reasons
     */
    public enum BookAddReason {
        SUCCESS,
        FULL_SLOT,
        NOT_MATCH_BOOK
    }

    /**
     * Enum for confirm result reasons
     */
    public enum BookConfirmReason {
        SUCCESS,
        NOTHING,
        NOT_ENOUGH_MONEY
    }
}
