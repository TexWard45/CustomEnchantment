package com.bafmc.customenchantment.menu;

import com.bafmc.bukkit.api.PlaceholderAPI;
import com.bafmc.bukkit.bafframework.api.BafFrameworkAPI;
import com.bafmc.bukkit.bafframework.feature.requirement.ItemRequirement;
import com.bafmc.bukkit.bafframework.feature.requirement.RequirementManager;
import com.bafmc.bukkit.feature.execute.Execute;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.feature.requirement.AbstractRequirement;
import com.bafmc.bukkit.feature.requirement.RequirementList;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.custommenu.menu.CMenuView;
import lombok.Getter;
import lombok.Setter;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.menu.data.BookData;
import com.bafmc.customenchantment.menu.data.BookUpgradeAddReason;
import com.bafmc.customenchantment.menu.data.BookUpgradeConfirmReason;
import com.bafmc.customenchantment.menu.data.BookUpgradeData;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.bukkit.utils.RandomRangeInt;
import com.bafmc.bukkit.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookUpgradeMenu extends MenuAbstract {
    public static final String MENU_NAME = "book-upgrade";
    public static final String BOOK_UPGRADE_ITEM = "book-upgrade";
    public static final String BOOK_RESULT_ITEM = "book-result";
    public static final String REMIND_ITEM = "remind";
    public static final String REMIND_XP_ITEM = "remind-xp";
    public static final String REMIND_XP_CONFIRM_ITEM = "remind-xp-confirm";
    public static final String REMIND_BOOK_CONFIRM_ITEM = "remind-book-confirm";
    public static final String INGREDIENT_PREVIEW = "ingredient-preview";
    @Getter
    @Setter
    private static BookUpgradeSettings settings;

    private BookData mainBook;
    private boolean readyToUpgrade;

    private List<BookData> bookIngredients = new ArrayList<>();
    private RandomRangeInt randomXp = new RandomRangeInt(0);

    public BookUpgradeMenu(CMenuView menuView, Player player) {
        super(menuView, player);
    }

    public BookUpgradeAddReason addBook(ItemStack clickedItem, CEEnchantSimple ceEnchantSimple) {
        if (hasMainBook()) {
            if (readyToUpgrade) {
                return BookUpgradeAddReason.NOTHING;
            }else {
                RandomRangeInt newXpIngredients = settings.getXp(ceEnchantSimple);

                if (newXpIngredients == null) {
                    return BookUpgradeAddReason.NOT_XP_BOOK;
                }

                if (!ceEnchantSimple.getName().equals(mainBook.getCESimple().getName())) {
                    BookUpgradeData thisBookUpgradeData = getBookUpgradeData();
                    if (!thisBookUpgradeData.getXpEnchantWhitelist().contains(ceEnchantSimple.getName())) {
                        return BookUpgradeAddReason.DIFFERENT_ENCHANT;
                    }
                }

                addBookIngredient(clickedItem, ceEnchantSimple);
                updateMenu();
                return BookUpgradeAddReason.SUCCESS;
            }
        }

        BookUpgradeData bookUpgradeData = settings.getBookUpgradeData(ceEnchantSimple.getName(), ceEnchantSimple.getLevel());
        if (bookUpgradeData == null) {
            return BookUpgradeAddReason.NOT_UPGRADE_BOOK;
        }

        if (ceEnchantSimple.getSuccess().getValue() != 100 || ceEnchantSimple.getDestroy().getValue() != 0) {
            return BookUpgradeAddReason.NOT_PERFECT_BOOK;
        }

        setMainBook(clickedItem, ceEnchantSimple);
        updateMenu();
        return BookUpgradeAddReason.SUCCESS;
    }

    public BookUpgradeConfirmReason confirmUpgrade() {
        if (hasMainBook() && hasBookIngredients() && !readyToUpgrade) {
            CEEnchantSimple ceEnchantSimple = mainBook.getCESimple();
            BookUpgradeData bookUpgradeData = getBookUpgradeData();

            int newXp = Math.min(ceEnchantSimple.getXp() + randomXp.getValue(), bookUpgradeData.getRequiredXp());
            ceEnchantSimple.setXp(newXp);

            ItemStack itemStack = CEAPI.getCEBookItemStack(ceEnchantSimple);

            this.mainBook = new BookData(itemStack, ceEnchantSimple);
            this.clearBookIngredients();
            this.resetXp();
            this.updateMenu();
            return BookUpgradeConfirmReason.SUCCESS_XP;
        }

        if (hasMainBook() && readyToUpgrade) {
            if (RequirementManager.checkFailedRequirement(player, getBookUpgradeData().getRequirementList())) {
                return BookUpgradeConfirmReason.NOTHING;
            }

            CEPlayer cePlayer = CEAPI.getCEPlayer(player);

            BookUpgradeData bookUpgradeData = getBookUpgradeData();
            if (!cePlayer.isFullChance() && !bookUpgradeData.getSuccessChance().work()) {
                com.bafmc.bukkit.feature.requirement.RequirementManager.instance().payRequirements(player, bookUpgradeData.getRequirementList());
                mainBook.getCESimple().setXp(Math.max(0, mainBook.getCESimple().getXp() - bookUpgradeData.getLoseXpPercent().getValue()));
                mainBook.setItemStack(CEAPI.getCEBookItemStack(mainBook.getCESimple()));
                returnItems();
                return BookUpgradeConfirmReason.FAIL_CHANCE;
            }

            com.bafmc.bukkit.feature.requirement.RequirementManager.instance().payRequirements(player, bookUpgradeData.getRequirementList());
            ItemStack itemStack = CEAPI.getCEBookItemStack(bookUpgradeData.getNextEnchant());
            broadcastUpgradeSuccess(player, itemStack);
            InventoryUtils.addItem(player, itemStack);
            clearMainBook();
            updateMenu();
            return BookUpgradeConfirmReason.SUCCESS;
        }

        return BookUpgradeConfirmReason.NOTHING;
    }

    public void broadcastUpgradeSuccess(Player player, ItemStack itemStack) {
        Execute execute = settings.getBroadcastUpgradeSuccessExecute();

        PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
        placeholderBuilder.put("{player_name}", player.getName());
        placeholderBuilder.put("{book_display}", itemStack.getItemMeta().getDisplayName());

        execute.execute(player, placeholderBuilder.build());
    }

    public void returnBook() {
        returnMainBook(player);
    }

    public void updateMenu() {
        updateMainBook();
        updateBookIngredients();
    }

    public void updateMainBook() {
        if (hasMainBook()) {
            ItemStack itemStack = getItemStack();

            if (!readyToUpgrade) {
                itemStack = CEAPI.getCEBookItemStack("upgrade-preview1", mainBook.getCESimple());
            }

            this.updateSlots(BOOK_UPGRADE_ITEM, itemStack);
            this.processMainBook();
        }else {
            this.updateSlots(BOOK_UPGRADE_ITEM, null);
            this.updateSlots(BOOK_RESULT_ITEM, null);
            this.updateSlots(REMIND_ITEM, null);
        }
    }

    public void updateBookIngredients() {
        if (readyToUpgrade && hasMainBook()) {
            RequirementList requirementList = getBookUpgradeData().getRequirementList();

            List<ItemStack> itemStacks = new ArrayList<>();

            for (AbstractRequirement requirement : requirementList) {
                if (requirement instanceof ItemRequirement) {
                    ItemRequirement.Data data = ((ItemRequirement) requirement).getData();

                    ItemStack itemStack = data.getTemplateItemStack().clone();
                    itemStack = ItemStackUtils.getSimpleItemStackWithPlaceholder(itemStack, player);
                    itemStack.setAmount(data.getAmount());
                    itemStacks.add(itemStack);
                }
            }

            List<Integer> slots = getSlots(INGREDIENT_PREVIEW);
            slots = reorderSlots(slots);

            for (int i = 0; i < slots.size(); i++) {
                if (i < itemStacks.size()) {
                    menuView.setTemporaryItem(slots.get(i), itemStacks.get(i));
                }else {
                    menuView.removeTemporaryItem(slots.get(i));
                }
            }

            return;
        }

        if (hasBookIngredients()) {
            List<Integer> slots = getSlots(INGREDIENT_PREVIEW);
            slots = reorderSlots(slots);

            for (int i = 0; i < slots.size(); i++) {
                if (i < bookIngredients.size()) {
                    BookData bookData = bookIngredients.get(i);

                    menuView.setTemporaryItem(slots.get(i), bookData.getItemStack());
                }else {
                    menuView.removeTemporaryItem(slots.get(i));
                }
            }

            this.updateSlots(REMIND_ITEM, getItemStack(player, REMIND_XP_CONFIRM_ITEM));
        }else {
            this.updateSlots(INGREDIENT_PREVIEW, null);
            this.updateSlots(REMIND_ITEM, getItemStack(player, REMIND_XP_ITEM));
        }
    }

    public static List<Integer> reorderSlots(List<Integer> slots) {
        List<Integer> reorderedSlots = new ArrayList<>();
        int midpoint = slots.size() / 2;

        // Add the middle element
        reorderedSlots.add(slots.get(midpoint));

        // Add elements in alternating order
        for (int i = 1; i <= midpoint; i++) {
            if (midpoint - i >= 0) {
                reorderedSlots.add(slots.get(midpoint - i));
            }
            if (midpoint + i < slots.size()) {
                reorderedSlots.add(slots.get(midpoint + i));
            }
        }

        return reorderedSlots;
    }

    public void processMainBook() {
        CEEnchantSimple ceEnchantSimple = mainBook.getCESimple();
        BookUpgradeData bookUpgradeData = settings.getBookUpgradeData(ceEnchantSimple.getName(), ceEnchantSimple.getLevel());

        int currentMainBookXp = ceEnchantSimple.getXp();
        int requiredMainBookXp = bookUpgradeData.getRequiredXp();

        readyToUpgrade = currentMainBookXp >= requiredMainBookXp;

        if (readyToUpgrade) {
            ItemStack itemStack = CEAPI.getCEBookItemStack(bookUpgradeData.getNextEnchant());
            this.updateSlots(BOOK_RESULT_ITEM, itemStack);
            this.updateBookConfirm();
        }else {
            String upgradePreview = "upgrade-preview1";

            if (hasBookIngredients()) {
                if (getMinFutureXp() != getMaxFutureXp()) {
                    upgradePreview = "upgrade-preview2";
                }else {
                    upgradePreview = "upgrade-preview3";
                }
            }

            ItemStack itemStack = CEAPI.getCEBookItemStack(upgradePreview, ceEnchantSimple);
            ItemStackUtils.setItemStack(itemStack, getPreviewBookPlaceholder(ceEnchantSimple));
            this.updateSlots(BOOK_RESULT_ITEM, itemStack);
            this.updateSlots(REMIND_ITEM, getItemStack(player, REMIND_XP_ITEM));
        }
    }

    public void updateBookConfirm() {
        BookUpgradeData bookUpgradeData = getBookUpgradeData();

        List<String> requirementLore = BafFrameworkAPI.getRequirementLore(player, bookUpgradeData.getRequirementList());
        requirementLore = PlaceholderAPI.setPlaceholders(player, requirementLore);

        ItemStack itemStack = getItemStack(player, REMIND_BOOK_CONFIRM_ITEM);

        PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
        placeholderBuilder.put("{requirement_description}", requirementLore);
        placeholderBuilder.put("{chance}", StringUtils.formatNumber(bookUpgradeData.getSuccessChance().getChance() * 100));

        ItemStackUtils.setItemStack(itemStack, placeholderBuilder.build());
        this.updateSlots(REMIND_ITEM, itemStack);
    }

    public Map<String, String> getPreviewBookPlaceholder(CEEnchantSimple ceEnchantSimple) {
        Map<String, String> placeholder = new LinkedHashMap<>();

        if (hasBookIngredients()) {
            placeholder.put("%enchant_future_xp%", StringUtils.formatNumber(getMinFutureXp()));
            placeholder.put("%enchant_future_xp1%", StringUtils.formatNumber(getMinFutureXp()));
            placeholder.put("%enchant_future_xp2%", StringUtils.formatNumber(getMaxFutureXp()));
            placeholder.put("%enchant_future_progress%", getEnchantFutureProgress());
        }

        return placeholder;
    }

    public int getMinFutureXp() {
        return Math.min(mainBook.getCESimple().getXp() + randomXp.getMin(), getBookUpgradeData().getRequiredXp());
    }

    public int getMaxFutureXp() {
        return Math.min(mainBook.getCESimple().getXp() + randomXp.getMax(), getBookUpgradeData().getRequiredXp());
    }

    public String getEnchantFutureProgress() {
        BookUpgradeData bookUpgradeData = getBookUpgradeData();
        if (bookUpgradeData == null) {
            return "";
        }

        int xp = mainBook.getCESimple().getXp();
        int requiredXp = bookUpgradeData.getRequiredXp();
        int progress = (int) Math.floor((double) xp / requiredXp * 10);

        int maxXpBonus = getMaxFutureXp();

        int bonusProgress = (int) Math.floor((double) maxXpBonus / requiredXp * 10) - progress;

        // It will plus min xp bonus and max xp bonus to xp
        // And it will be yellow &e

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i < progress) {
                builder.append("&a");
            }else if (i < progress + bonusProgress) {
                builder.append("&#99ff90");
            }else {
                builder.append("&7");
            }
            // Larger character
            builder.append("▌");
        }

        return builder.toString();
    }

    public void addBookIngredient(ItemStack clickedItem, CEEnchantSimple ceEnchantSimple) {
        bookIngredients.add(new BookData(clickedItem, ceEnchantSimple));

        RandomRangeInt newXpIngredients = settings.getXp(ceEnchantSimple);
        this.randomXp = new RandomRangeInt(newXpIngredients.getMin() + this.randomXp.getMin(), newXpIngredients.getMax() + this.randomXp.getMax());
    }

    public void returnItems() {
        returnBook();
        updateMenu();
    }

    public void setMainBook(ItemStack itemStack, CEEnchantSimple ceEnchantSimple) {
        this.mainBook = new BookData(itemStack, ceEnchantSimple);
    }

    public boolean hasMainBook() {
        return mainBook != null;
    }

    public void clearMainBook() {
        mainBook = null;
    }

    public void returnMainBook(Player player) {
        if (hasMainBook()) {
            InventoryUtils.addItem(player, getItemStack());
            clearMainBook();
            resetXp();

            returnBookIngredients(player);
        }
    }

    public void returnBookIngredients(Player player) {
        if (readyToUpgrade) {
            return;
        }

        if (hasBookIngredients()) {
            List<ItemStack> itemStacks = new ArrayList<>();
            for (BookData bookData : bookIngredients) {
                itemStacks.add(bookData.getItemStack());
            }

            InventoryUtils.addItem(player, itemStacks);
            clearBookIngredients();
        }
    }

    public void returnBookIngredients(int slot) {
        if (hasBookIngredients()) {
            List<Integer> slots = getSlots(INGREDIENT_PREVIEW);

            int slotIndex = -1;
            for (int i = 0; i < slots.size(); i++) {
                if (slots.get(i) == slot) {
                    slotIndex = i;
                    break;
                }
            }

            if (slotIndex < bookIngredients.size()) {
                BookData bookData = bookIngredients.get(slotIndex);
                bookIngredients.remove(slotIndex);

                InventoryUtils.addItem(player, bookData.getItemStack());
                RandomRangeInt xpIngredients = settings.getXp(bookData.getCESimple());
                this.randomXp = new RandomRangeInt(this.randomXp.getMin() - xpIngredients.getMin(), this.randomXp.getMax() - xpIngredients.getMax());
            }
        }
    }

    public void clearBookIngredients() {
        bookIngredients.clear();
    }

    public boolean hasBookIngredients() {
        return !bookIngredients.isEmpty();
    }

    public ItemStack getItemStack() {
        return mainBook.getItemStack();
    }

    public void resetXp() {
        randomXp = new RandomRangeInt(0);
    }

    public BookUpgradeData getBookUpgradeData() {
        return settings.getBookUpgradeData(mainBook.getCESimple().getName(), mainBook.getCESimple().getLevel());
    }

    public boolean hasXpIngredients() {
        return randomXp.getMin() > 0 || randomXp.getMax() > 0;
    }
}