package com.bafmc.customenchantment.menu.bookcraft;

import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.book.CEBook;
import com.bafmc.customenchantment.menu.data.BookData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FastCraft - Simplified and refactored version
 *
 * Automatically combines multiple books from player inventory to reach highest level.
 * Algorithm:
 * 1. Scan inventory for books matching the enchantment
 * 2. Group books by level
 * 3. Combine pairs at each level (choosing best success % and lowest destroy %)
 * 4. Repeat until max level or insufficient books
 */
public class FastCraftRefactored {

    private final BookCraftCustomMenu menu;

    @Getter
    private BookData finalResult;

    @Getter
    private List<BookData> leftoverBooks;  // Leftover books that should be returned to player

    @Getter
    private int totalBooksUsed;

    private List<Integer> slotsToRemove;  // Track slot indices instead of ItemStacks

    /**
     * Get the number of inventory slots that will be removed during FastCraft
     * This is used to validate that actual combining occurred (not just returning an unchanged book)
     */
    public int getSlotsToRemoveCount() {
        return slotsToRemove.size();
    }

    public FastCraftRefactored(BookCraftCustomMenu menu) {
        this.menu = menu;
        this.slotsToRemove = new ArrayList<>();
        this.leftoverBooks = new ArrayList<>();
    }

    /**
     * Calculate the optimal upgrade path from player's inventory
     */
    public void calculate(Player player) {
        // Reset state
        this.finalResult = null;
        this.totalBooksUsed = 0;
        this.slotsToRemove.clear();
        this.leftoverBooks.clear();

        // Get the base book from menu (LAST book - the one just added/clicked)
        List<BookData> menuBooks = menu.getList();
        if (menuBooks.isEmpty()) {
            return;
        }

        BookData baseBook = menuBooks.get(menuBooks.size() - 1);  // Use LAST book, not first!
        CEEnchantSimple baseEnchant = baseBook.getCESimple();
        int maxLevel = baseEnchant.getCEEnchant().getMaxLevel();

        // Collect all matching books from inventory (SAME LEVEL as base only)
        Map<Integer, List<BookCandidate>> booksByLevel = collectMatchingBooks(player, baseEnchant.getName(), baseEnchant.getLevel(), maxLevel);

        // Add books from menu that match the base enchantment
        // Collect ALL levels (will combine them together)
        for (BookData menuBook : menuBooks) {
            CEEnchantSimple enchant = menuBook.getCESimple();

            // Only add books that match the base enchantment name
            if (!enchant.getName().equals(baseEnchant.getName())) {
                continue;
            }

            // Skip books at or above max level (can't be combined)
            if (enchant.getLevel() >= maxLevel) {
                continue;
            }

            booksByLevel.computeIfAbsent(enchant.getLevel(), k -> new ArrayList<>())
                    .add(new BookCandidate(menuBook.getItemStack(), enchant, false));
        }

        // Count total ORIGINAL input books BEFORE combining (don't count intermediate results)
        int menuBookCount = (int) booksByLevel.values().stream()
                .flatMap(List::stream)
                .filter(b -> !b.fromInventory)
                .count();
        int inventoryBookCount = (int) booksByLevel.values().stream()
                .flatMap(List::stream)
                .filter(b -> b.fromInventory)
                .count();
        totalBooksUsed = menuBookCount + inventoryBookCount;

        // Combine books level by level
        BookCandidate result = combineBooks(booksByLevel, maxLevel);

        // Collect all leftover books that weren't combined
        // ONLY return books created through combining, NOT original inventory books that weren't used
        for (int level = 1; level <= maxLevel; level++) {
            List<BookCandidate> leftovers = booksByLevel.getOrDefault(level, new ArrayList<>());
            for (BookCandidate leftover : leftovers) {
                // Don't include the final result as a leftover
                if (result != null && leftover.enchant.getLevel() == result.enchant.getLevel() &&
                    leftover.enchant.getSuccess().getValue() == result.enchant.getSuccess().getValue() &&
                    leftover.enchant.getDestroy().getValue() == result.enchant.getDestroy().getValue()) {
                    continue;  // This is the final result, not a leftover
                }

                // CRITICAL: Only give back books that were CREATED through combining
                // Original inventory books that weren't used should stay in inventory (not given back)
                if (leftover.fromInventory) {
                    continue;  // Skip - this book is still in player's inventory
                }

                BookData leftoverBook = new BookData(
                    CEAPI.getCEBookItemStack(leftover.enchant),
                    leftover.enchant
                );
                leftoverBooks.add(leftoverBook);
            }
        }

        if (result != null) {
            // CRITICAL: Only accept result if it reached AT LEAST the base level
            // If result is below base, it means we didn't have enough books to reach the target
            if (result.enchant.getLevel() < baseEnchant.getLevel()) {
                this.finalResult = null;
            } else {
                this.finalResult = new BookData(
                    CEAPI.getCEBookItemStack(result.enchant),
                    result.enchant
                );
            }
        }
    }

    /**
     * Collect books from inventory that match the enchant name
     * Collects books at ALL levels (will combine them all together)
     * Validation at the end ensures only valid results are shown
     */
    private Map<Integer, List<BookCandidate>> collectMatchingBooks(Player player, String enchantName, int baseLevel, int maxLevel) {
        Map<Integer, List<BookCandidate>> booksByLevel = new HashMap<>();

        // Use indexed loop to track slot positions for reliable removal later
        ItemStack[] contents = player.getInventory().getContents();
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            if (item == null || item.getType().isAir()) continue;

            CEItem ceItem = CEAPI.getCEItem(item);
            if (!(ceItem instanceof CEBook)) continue;

            CEBook ceBook = (CEBook) ceItem;
            CEEnchantSimple enchant = ceBook.getData().getCESimple();

            // Must match enchant name
            if (!enchant.getName().equals(enchantName)) continue;

            // Must be below max level (can't combine books at max level)
            if (enchant.getLevel() >= maxLevel) {
                continue;
            }

            booksByLevel.computeIfAbsent(enchant.getLevel(), k -> new ArrayList<>())
                    .add(new BookCandidate(item, enchant, true, slot));
        }

        return booksByLevel;
    }

    /**
     * Combine books from lowest to highest level
     */
    private BookCandidate combineBooks(Map<Integer, List<BookCandidate>> booksByLevel, int maxLevel) {
        BookCandidate bestResult = null;

        // Process each level from lowest to highest
        for (int level = 1; level < maxLevel; level++) {
            List<BookCandidate> currentLevel = booksByLevel.getOrDefault(level, new ArrayList<>());

            if (currentLevel.isEmpty()) continue;

            // Combine pairs at this level
            while (currentLevel.size() >= 2) {
                // Find best pair (highest success, lowest destroy)
                BookCandidate best1 = findBestSuccess(currentLevel);
                currentLevel.remove(best1);

                BookCandidate best2 = findBestDestroy(currentLevel);
                currentLevel.remove(best2);

                // Combine them
                BookCandidate combined = combine(best1, best2);

                // Track slot indices to remove from inventory (only inventory books, not menu books)
                if (best1.fromInventory && best1.inventorySlot >= 0) {
                    slotsToRemove.add(best1.inventorySlot);
                }
                if (best2.fromInventory && best2.inventorySlot >= 0) {
                    slotsToRemove.add(best2.inventorySlot);
                }

                // Add to next level
                booksByLevel.computeIfAbsent(level + 1, k -> new ArrayList<>()).add(combined);
                bestResult = combined;
            }
        }

        return bestResult;
    }

    /**
     * Find book with highest success %
     */
    private BookCandidate findBestSuccess(List<BookCandidate> books) {
        return books.stream()
                .max(Comparator.comparingInt(b -> b.enchant.getSuccess().getValue()))
                .orElse(books.get(0));
    }

    /**
     * Find book with lowest destroy %
     */
    private BookCandidate findBestDestroy(List<BookCandidate> books) {
        return books.stream()
                .min(Comparator.comparingInt(b -> b.enchant.getDestroy().getValue()))
                .orElse(books.get(0));
    }

    /**
     * Combine two books into next level
     */
    private BookCandidate combine(BookCandidate book1, BookCandidate book2) {
        CEEnchantSimple enchant1 = book1.enchant;
        CEEnchantSimple enchant2 = book2.enchant;

        CEEnchantSimple result = new CEEnchantSimple(
            enchant1.getName(),
            enchant1.getLevel() + 1,
            Math.max(enchant1.getSuccess().getValue(), enchant2.getSuccess().getValue()),
            Math.min(enchant1.getDestroy().getValue(), enchant2.getDestroy().getValue())
        );

        return new BookCandidate(CEAPI.getCEBookItemStack(result), result, false);
    }

    /**
     * Execute the FastCraft - remove books and give result
     */
    public BookCraftExtraData.BookConfirmReason confirm(Player player) {
        if (finalResult == null) {
            return BookCraftExtraData.BookConfirmReason.NOTHING;
        }

        // Check money requirement
        // CRITICAL: Price is based on inventory books consumed, NOT menu book
        // Menu book is removed but result is given back, so net cost = inventory books only
        String groupName = finalResult.getCESimple().getCEEnchant().getGroupName();
        int booksConsumed = slotsToRemove.size();  // Only count inventory books being removed

        if (!CustomEnchantment.instance().getBookCraftConfig().payMoney(player, groupName, (double) booksConsumed)) {
            return BookCraftExtraData.BookConfirmReason.NOT_ENOUGH_MONEY;
        }

        // Remove the base book from menu (consume it, don't return it!)
        List<BookData> menuBooks = menu.getList();
        if (!menuBooks.isEmpty()) {
            menuBooks.clear();  // Remove all books from menu (consume them)
            menu.updateMenu();
        }

        // Remove books from inventory by slot index (RELIABLE - no ItemStack matching issues!)
        for (int slot : slotsToRemove) {
            player.getInventory().setItem(slot, null);  // Direct slot removal - always works!
        }

        // Give result to player
        InventoryUtils.addItem(player, Arrays.asList(CEAPI.getCEBookItemStack(finalResult.getCESimple())));

        // Give leftover books to player
        if (!leftoverBooks.isEmpty()) {
            for (BookData leftover : leftoverBooks) {
                InventoryUtils.addItem(player, Arrays.asList(leftover.getItemStack()));
            }
        }

        return BookCraftExtraData.BookConfirmReason.SUCCESS;
    }

    /**
     * Internal class to track book candidates
     */
    private static class BookCandidate {
        final ItemStack item;
        final CEEnchantSimple enchant;
        final boolean fromInventory; // true if from inventory, false if combined result
        final int inventorySlot; // slot index in inventory (-1 if not from inventory)

        BookCandidate(ItemStack item, CEEnchantSimple enchant, boolean fromInventory) {
            this(item, enchant, fromInventory, -1);
        }

        BookCandidate(ItemStack item, CEEnchantSimple enchant, boolean fromInventory, int inventorySlot) {
            this.item = item;
            this.enchant = enchant;
            this.fromInventory = fromInventory;
            this.inventorySlot = inventorySlot;
        }
    }
}
