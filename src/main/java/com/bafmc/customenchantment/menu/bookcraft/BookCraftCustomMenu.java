package com.bafmc.customenchantment.menu.bookcraft;

import com.bafmc.bukkit.bafframework.custommenu.menu.AbstractMenu;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.list.DefaultItem;
import com.bafmc.bukkit.feature.placeholder.Placeholder;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.book.CEBook;
import com.bafmc.customenchantment.menu.bookcraft.item.BookAcceptItem;
import com.bafmc.customenchantment.menu.bookcraft.item.BookPreviewItem;
import com.bafmc.customenchantment.menu.bookcraft.item.BookReturnItem;
import com.bafmc.customenchantment.menu.bookcraft.item.BookSlotItem;
import com.bafmc.customenchantment.menu.data.BookData;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Book Craft menu - migrated to new CustomMenu BafFramework API
 *
 * Allows players to combine two identical CE books to upgrade level.
 * Supports FastCraft mode for auto-combining multiple books from inventory.
 */
public class BookCraftCustomMenu extends AbstractMenu<MenuData, BookCraftExtraData> {

    public static final String MENU_NAME = "book-craft";

    @Getter
    private static BookCraftSettings settings;

    public BookCraftCustomMenu() {
        // Constructor - menu instance created
    }

    public static void setSettings(BookCraftSettings settings) {
        BookCraftCustomMenu.settings = settings;
    }

    @Override
    public String getType() {
        return MENU_NAME;
    }

    @Override
    public void registerItems() {
        // Register default item for border/decorative items
        registerItem(DefaultItem.class);

        // Register custom items
        registerItem(BookSlotItem.class);
        registerItem(BookPreviewItem.class);
        registerItem(BookAcceptItem.class);
        registerItem(BookReturnItem.class);
    }

    @Override
    public void setupItems() {
        super.setupItems();

        // Then, overlay the dynamic book items on top
        if (extraData != null && extraData.getBookList() != null) {
            updateMenu();
        }
    }

    /**
     * Handle clicks in player's inventory - add CE books to craft slots
     * This method is called automatically by the framework for player inventory clicks
     *
     * Only accepts books with amount=1 to prevent duplicate processing
     */
    @Override
    public void handlePlayerInventoryClick(ClickData data) {
        Player player = data.getPlayer();
        ItemStack clickedItem = data.getEvent().getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        // Must be amount=1 (legacy requirement)
        if (clickedItem.getAmount() != 1) {
            CustomEnchantmentMessage.send(player, "menu.book-craft.must-be-one");
            return;
        }

        // Check if it's a CE book
        CEItem ceItem = CEAPI.getCEItem(clickedItem);

        if (ceItem == null || !(ceItem instanceof CEBook)) {
            CustomEnchantmentMessage.send(player, "menu.book-craft.not-support-item");
            return;
        }

        CEBook ceBook = (CEBook) ceItem;

        // Clone the item BEFORE removing from inventory
        ItemStack itemToAdd = clickedItem.clone();
        BookData bookData = new BookData(itemToAdd, ceBook.getData().getCESimple());

        // Remove from player inventory FIRST (before triggering FastCraft)
        // This prevents double-counting: the book won't be in inventory when FastCraft scans
        data.getEvent().setCurrentItem(null);

        // Try to add the book (this will trigger FastCraft with correct inventory count)
        BookCraftExtraData.BookAddReason reason = addBook(itemToAdd, bookData);

        // If failed, return the book to inventory
        if (reason != BookCraftExtraData.BookAddReason.SUCCESS) {
            data.getEvent().setCurrentItem(itemToAdd);
        }

        // Send feedback message
        CustomEnchantmentMessage.send(player, "menu.book-craft.add-book." + EnumUtils.toConfigStyle(reason));
    }

    @Override
    public void handleClose() {
        // Return all books to player when menu closes
        returnAllBooks();
    }

    // ==================== Business Logic (migrated from BookCraftMenu) ====================

    /**
     * Add a CE book to the craft slots
     */
    public BookCraftExtraData.BookAddReason addBook(ItemStack itemStack, BookData bookData) {
        List<BookData> list = extraData.getBookList();

        if (list.size() >= settings.getSize()) {
            return BookCraftExtraData.BookAddReason.FULL_SLOT;
        }

        // Check if books match (if there's already one book)
        if (!list.isEmpty()) {
            BookData existingBook = list.get(0);
            if (!compareBooks(existingBook.getCESimple(), bookData.getCESimple())) {
                return BookCraftExtraData.BookAddReason.NOT_MATCH_BOOK;
            }
        }

        list.add(bookData);
        updateMenu();

        // Handle different modes based on book count
        if (list.size() == 1) {
            // Single book - trigger FastCraft mode
            triggerFastCraft();
        } else if (list.size() == 2) {
            // Two books - use regular combine mode
            extraData.setFastCraft(null);  // Clear FastCraft data when switching to regular mode
            updatePreview();
        } else {
            // More than 2 books - shouldn't happen, but handle gracefully
            extraData.setFastCraft(null);  // Clear FastCraft data
            showRemindButton();
        }

        return BookCraftExtraData.BookAddReason.SUCCESS;
    }

    /**
     * Compare two CE books for matching (name, level, success %, destroy %)
     */
    private boolean compareBooks(CEEnchantSimple book1, CEEnchantSimple book2) {
        if (!book1.getName().equals(book2.getName())) {
            return false;
        }

        if (book1.getLevel() != book2.getLevel()) {
            return false;
        }

        if (book1.getSuccess().getValue() != book2.getSuccess().getValue()) {
            return false;
        }

        if (book1.getDestroy().getValue() != book2.getDestroy().getValue()) {
            return false;
        }

        return true;
    }

    /**
     * Update menu display - show books in slots
     */
    public void updateMenu() {
        List<BookData> list = extraData.getBookList();
        int size = settings.getSize();

        for (int i = 0; i < size; i++) {
            int bookSlot = settings.getBookSlot(i);

            if (i < list.size()) {
                BookData data = list.get(i);
                inventory.setItem(bookSlot, data.getItemStack());
            } else {
                // Restore default display for empty slots
                inventory.setItem(bookSlot, getTemplateItemStack("book" + (i + 1)));
            }
        }

        // Clear preview and show remind button when less than 2 books
        if (list.size() < 2) {
            inventory.setItem(settings.getPreviewSlot(), getTemplateItemStack("preview"));
        }

        // Reset to remind button when menu is empty
        if (list.isEmpty()) {
            showRemindButton();
        }
    }

    /**
     * Update preview slot with result book
     */
    private void updatePreview() {
        List<BookData> list = extraData.getBookList();

        if (list.size() == 2) {
            BookData book1 = list.get(0);
            BookData book2 = list.get(1);

            // Calculate result book
            CEEnchantSimple result = upgradeBook(book1.getCESimple(), book2.getCESimple());
            ItemStack resultItem = CEAPI.getCEBookItemStack(result);

            // Set preview
            inventory.setItem(settings.getPreviewSlot(), resultItem);

            // Show accept button (hide remind) and update price for regular 2-book mode
            showAcceptButton(1.0);  // Regular mode = 1x price (not FastCraft multiplier)
        } else {
            // Clear preview
            inventory.setItem(settings.getPreviewSlot(), getTemplateItemStack("preview"));

            // Show remind button (hide accept)
            showRemindButton();
        }
    }

    /**
     * Upgrade two books to next level
     */
    private CEEnchantSimple upgradeBook(CEEnchantSimple book1, CEEnchantSimple book2) {
        int success1 = book1.getSuccess().getValue();
        int destroy1 = book1.getDestroy().getValue();

        int success2 = book2.getSuccess().getValue();
        int destroy2 = book2.getDestroy().getValue();

        return new CEEnchantSimple(
            book1.getName(),
            book1.getLevel() + 1,
            Math.max(success1, success2),
            Math.min(destroy1, destroy2)
        );
    }

    /**
     * Show remind button (when less than 2 books)
     */
    private void showRemindButton() {
        int slot = settings.getAcceptSlot();
        ItemStack remindItem = getTemplateItemStack("remind");
        inventory.setItem(slot, remindItem);
    }

    /**
     * Show accept button with calculated price
     * @param multiplier Price multiplier (1.0 for regular mode, totalBooksUsed for FastCraft)
     */
    private void showAcceptButton(double multiplier) {
        List<BookData> list = extraData.getBookList();
        if (list.isEmpty()) return;

        int slot = settings.getAcceptSlot();

        // Calculate money requirement
        String groupName = list.get(0).getCESimple().getCEEnchant().getGroupName();
        double basePrice = CustomEnchantment.instance().getBookCraftConfig().getMoneyRequire(groupName);
        double totalPrice = basePrice * multiplier;

        // Build Placeholder with money value
        Placeholder placeholder = PlaceholderBuilder.builder()
                .put("%money%", String.valueOf((long)totalPrice))
                .build();

        ItemStack acceptItem = getTemplateItemStack("accept", placeholder);
        inventory.setItem(slot, acceptItem);
    }

    /**
     * Return book at clicked slot to player
     */
    public void returnBook(int slot) {
        List<BookData> list = extraData.getBookList();
        int index = settings.getBookIndex(slot);

        if (index < 0 || index >= list.size()) {
            return;
        }

        BookData data = list.remove(index);
        InventoryUtils.addItem(owner, Arrays.asList(data.getItemStack()));
        updateMenu();

        // Update mode based on remaining book count
        if (list.size() == 1) {
            // One book left - trigger FastCraft mode
            triggerFastCraft();
        } else if (list.size() == 2) {
            // Two books left - switch to regular combine mode
            extraData.setFastCraft(null);
            updatePreview();
        } else {
            // No books or unexpected state - clear FastCraft and show remind
            extraData.setFastCraft(null);
            showRemindButton();
        }
    }

    /**
     * Return all books to player
     */
    public void returnAllBooks() {
        List<BookData> list = extraData.getBookList();
        List<ItemStack> itemStacks = new ArrayList<>();

        for (BookData data : list) {
            itemStacks.add(data.getItemStack());
        }

        InventoryUtils.addItem(owner, itemStacks);
        list.clear();
    }

    /**
     * Confirm book craft - combine books and give result
     */
    public BookCraftExtraData.BookConfirmReason confirmBookCraft() {
        List<BookData> list = extraData.getBookList();

        if (list.isEmpty()) {
            return BookCraftExtraData.BookConfirmReason.NOTHING;
        }

        // Check if FastCraft mode is active AND valid
        if (extraData.getFastCraft() != null && extraData.getFastCraft().getFinalResult() != null) {
            // Validate that FastCraft actually combined books (not just 1 book, no result)
            // Must have 2+ books AND at least one slot marked for removal (actual combining occurred)
            if (extraData.getFastCraft().getTotalBooksUsed() < 2 || extraData.getFastCraft().getSlotsToRemoveCount() == 0) {
                // Clear the invalid FastCraft result from display
                inventory.setItem(settings.getPreviewSlot(), getTemplateItemStack("preview"));
                inventory.setItem(settings.getAcceptSlot(), null);
                showRemindButton();
                extraData.setFastCraft(null);
                return BookCraftExtraData.BookConfirmReason.NOTHING;
            }

            // Use FastCraft logic
            return extraData.getFastCraft().confirm(owner);
        }

        // Regular mode: need 2 books
        if (list.size() < 2) {
            return BookCraftExtraData.BookConfirmReason.NOTHING;
        }

        // Check money requirement
        String groupName = list.get(0).getCESimple().getCEEnchant().getGroupName();
        if (!CustomEnchantment.instance().getBookCraftConfig().payMoney(owner, groupName, 1.0)) {
            return BookCraftExtraData.BookConfirmReason.NOT_ENOUGH_MONEY;
        }

        // Calculate result
        BookData book1 = list.get(0);
        BookData book2 = list.get(1);
        CEEnchantSimple result = upgradeBook(book1.getCESimple(), book2.getCESimple());

        // Give result to player
        ItemStack resultItem = CEAPI.getCEBookItemStack(result);
        InventoryUtils.addItem(owner, Arrays.asList(resultItem));

        // Clear books
        list.clear();
        updateMenu();

        return BookCraftExtraData.BookConfirmReason.SUCCESS;
    }

    /**
     * Trigger FastCraft mode - scan inventory for matching books
     * Uses the LAST ADDED book as the base (the one player just clicked)
     * ONLY works when there's exactly 1 book in menu (not for regular 2-book crafting)
     */
    private void triggerFastCraft() {
        List<BookData> list = extraData.getBookList();

        if (list.isEmpty()) {
            return;
        }

        // CRITICAL: FastCraft only works with EXACTLY 1 book in menu
        // With 2 books, use regular crafting mode instead
        if (list.size() != 1) {
            return;
        }

        // Use the LAST book in the list as base (the one just added/clicked)
        BookData baseBook = list.get(list.size() - 1);
        CEEnchantSimple baseEnchant = baseBook.getCESimple();
        int maxLevel = baseEnchant.getCEEnchant().getMaxLevel();

        // Check if base book is already at max level - can't combine further
        if (baseEnchant.getLevel() >= maxLevel) {
            showRemindButton();
            return;
        }

        // Initialize FastCraft with new refactored version
        FastCraftRefactored fastCraft = new FastCraftRefactored(this);
        fastCraft.calculate(owner);

        // Store in extraData
        extraData.setFastCraft(fastCraft);

        // Update preview with FastCraft result if available
        // CRITICAL: Only show if actually combining books (not just returning the same single book)
        if (fastCraft.getFinalResult() != null && fastCraft.getTotalBooksUsed() >= 2) {
            // Additional check: result must be HIGHER level than input OR actual combining occurred
            // Actual combining means slots were marked for removal (not just incompatible books)
            boolean validResult = fastCraft.getFinalResult().getCESimple().getLevel() > baseBook.getCESimple().getLevel()
                               || (fastCraft.getTotalBooksUsed() > 1 && fastCraft.getSlotsToRemoveCount() > 0);

            if (validResult) {
                // Show FastCraft result
                ItemStack resultItem = CEAPI.getCEBookItemStack(fastCraft.getFinalResult().getCESimple());
                inventory.setItem(settings.getPreviewSlot(), resultItem);
                // Use inventory books consumed for price (not total books including menu)
                int inventoryBooksConsumed = fastCraft.getSlotsToRemoveCount();
                showAcceptButton(inventoryBooksConsumed);
            } else {
                // Result is same level as input with only 1 book - don't show
                showRemindButton();
                inventory.setItem(settings.getPreviewSlot(), getTemplateItemStack("preview"));
                extraData.setFastCraft(null);  // Clear invalid FastCraft
            }
        } else {
            // Not enough books for FastCraft, show remind button
            showRemindButton();
            inventory.setItem(settings.getPreviewSlot(), getTemplateItemStack("preview"));
        }
    }

    /**
     * Get book list (for FastCraft compatibility)
     */
    public List<BookData> getList() {
        return extraData.getBookList();
    }

}
