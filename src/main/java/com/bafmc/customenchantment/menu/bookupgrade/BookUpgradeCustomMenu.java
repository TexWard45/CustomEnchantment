package com.bafmc.customenchantment.menu.bookupgrade;

import com.bafmc.bukkit.api.PlaceholderAPI;
import com.bafmc.bukkit.bafframework.api.BafFrameworkAPI;
import com.bafmc.bukkit.bafframework.custommenu.menu.AbstractMenu;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.list.DefaultItem;
import com.bafmc.bukkit.bafframework.feature.requirement.RequirementManager;
import com.bafmc.bukkit.feature.execute.Execute;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.feature.requirement.AbstractRequirement;
import com.bafmc.bukkit.feature.requirement.RequirementList;
import com.bafmc.bukkit.bafframework.feature.requirement.ItemRequirement;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.utils.RandomRangeInt;
import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.book.CEBook;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeAddReason;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeConfirmReason;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeData;
import com.bafmc.customenchantment.menu.bookupgrade.item.BookUpgradeRemindItem;
import com.bafmc.customenchantment.menu.bookupgrade.item.BookUpgradeResultItem;
import com.bafmc.customenchantment.menu.bookupgrade.item.BookUpgradeSlotItem;
import com.bafmc.customenchantment.menu.bookupgrade.item.IngredientPreviewItem;
import com.bafmc.customenchantment.menu.data.BookData;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.bukkit.utils.EnumUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookUpgradeCustomMenu extends AbstractMenu<MenuData, BookUpgradeExtraData> {

    public static final String MENU_NAME = "book-upgrade";

    @Override
    public String getType() {
        return MENU_NAME;
    }

    @Override
    public void registerItems() {
        registerItem(DefaultItem.class);
        registerItem(BookUpgradeSlotItem.class);
        registerItem(BookUpgradeResultItem.class);
        registerItem(IngredientPreviewItem.class);
        registerItem(BookUpgradeRemindItem.class);
    }

    @Override
    public void handlePlayerInventoryClick(ClickData data) {
        ItemStack clickedItem = data.getEvent().getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir() || clickedItem.getAmount() > 1) {
            return;
        }

        CEItem ceItem = CEAPI.getCEItem(clickedItem);
        if (ceItem == null || !(ceItem instanceof CEBook)) {
            return;
        }

        BookUpgradeAddReason reason = addBook(clickedItem, ((CEBook) ceItem).getData().getCESimple());

        if (reason == BookUpgradeAddReason.SUCCESS) {
            data.getEvent().setCurrentItem(null);
        }

        CustomEnchantmentMessage.send(data.getPlayer(),
                "menu.bookupgrade.add-book." + EnumUtils.toConfigStyle(reason));
    }

    @Override
    public void handleClose() {
        returnAllItems();
    }

    // ==================== Business Logic ====================

    public BookUpgradeAddReason addBook(ItemStack clickedItem, CEEnchantSimple ceEnchantSimple) {
        BookUpgradeSettings settings = extraData.getSettings();

        if (extraData.hasMainBook()) {
            if (extraData.isReadyToUpgrade()) {
                return BookUpgradeAddReason.NOTHING;
            }

            RandomRangeInt newXpIngredients = settings.getXp(ceEnchantSimple);
            if (newXpIngredients == null) {
                return BookUpgradeAddReason.NOT_XP_BOOK;
            }

            if (!ceEnchantSimple.getName().equals(extraData.getMainBook().getCESimple().getName())) {
                BookUpgradeData thisBookUpgradeData = getBookUpgradeData();
                if (!thisBookUpgradeData.getXpEnchantWhitelist().contains(ceEnchantSimple.getName())) {
                    return BookUpgradeAddReason.DIFFERENT_ENCHANT;
                }
            }

            addBookIngredient(clickedItem, ceEnchantSimple);
            updateMenu();
            return BookUpgradeAddReason.SUCCESS;
        }

        BookUpgradeData bookUpgradeData = settings.getBookUpgradeData(ceEnchantSimple.getName(), ceEnchantSimple.getLevel());
        if (bookUpgradeData == null) {
            return BookUpgradeAddReason.NOT_UPGRADE_BOOK;
        }

        if (ceEnchantSimple.getSuccess().getValue() != 100 || ceEnchantSimple.getDestroy().getValue() != 0) {
            return BookUpgradeAddReason.NOT_PERFECT_BOOK;
        }

        extraData.setMainBook(new BookData(clickedItem.clone(), ceEnchantSimple));
        updateMenu();
        return BookUpgradeAddReason.SUCCESS;
    }

    public BookUpgradeConfirmReason confirmUpgrade() {
        if (extraData.hasMainBook() && extraData.hasBookIngredients() && !extraData.isReadyToUpgrade()) {
            CEEnchantSimple ceEnchantSimple = extraData.getMainBook().getCESimple();
            BookUpgradeData bookUpgradeData = getBookUpgradeData();

            int newXp = Math.min(ceEnchantSimple.getXp() + extraData.getRandomXp().getValue(), bookUpgradeData.getRequiredXp());
            ceEnchantSimple.setXp(newXp);

            ItemStack itemStack = CEAPI.getCEBookItemStack(ceEnchantSimple);
            extraData.setMainBook(new BookData(itemStack.clone(), ceEnchantSimple));
            extraData.clearBookIngredients();
            extraData.resetXp();
            updateMenu();
            return BookUpgradeConfirmReason.SUCCESS_XP;
        }

        if (extraData.hasMainBook() && extraData.isReadyToUpgrade()) {
            BookUpgradeData bookUpgradeData = getBookUpgradeData();

            if (RequirementManager.checkFailedRequirement(owner, bookUpgradeData.getRequirementList())) {
                return BookUpgradeConfirmReason.NOTHING;
            }

            CEPlayer cePlayer = CEAPI.getCEPlayer(owner);

            if (!cePlayer.isFullChance() && !bookUpgradeData.getSuccessChance().work()) {
                com.bafmc.bukkit.feature.requirement.RequirementManager.instance().payRequirements(owner, bookUpgradeData.getRequirementList());
                int currentXp = extraData.getMainBook().getCESimple().getXp();
                int losePercent = bookUpgradeData.getLoseXpPercent().getValue();
                int xpToLose = (int) Math.floor(currentXp * losePercent / 100.0);
                extraData.getMainBook().getCESimple().setXp(Math.max(0, currentXp - xpToLose));
                extraData.getMainBook().setItemStack(CEAPI.getCEBookItemStack(extraData.getMainBook().getCESimple()).clone());
                returnMainBook();
                updateMenu();
                return BookUpgradeConfirmReason.FAIL_CHANCE;
            }

            com.bafmc.bukkit.feature.requirement.RequirementManager.instance().payRequirements(owner, bookUpgradeData.getRequirementList());
            ItemStack resultItem = CEAPI.getCEBookItemStack(bookUpgradeData.getNextEnchant());
            broadcastUpgradeSuccess(resultItem);
            InventoryUtils.addItem(owner, resultItem);
            extraData.clearMainBook();
            extraData.setReadyToUpgrade(false);
            updateMenu();
            return BookUpgradeConfirmReason.SUCCESS;
        }

        return BookUpgradeConfirmReason.NOTHING;
    }

    // ==================== Item Management ====================

    private void addBookIngredient(ItemStack clickedItem, CEEnchantSimple ceEnchantSimple) {
        extraData.getBookIngredients().add(new BookData(clickedItem.clone(), ceEnchantSimple));

        RandomRangeInt newXpIngredients = extraData.getSettings().getXp(ceEnchantSimple);
        RandomRangeInt currentXp = extraData.getRandomXp();
        extraData.setRandomXp(new RandomRangeInt(
                newXpIngredients.getMin() + currentXp.getMin(),
                newXpIngredients.getMax() + currentXp.getMax()));
    }

    public void returnIngredient(int slotIndex) {
        if (!extraData.hasBookIngredients()) {
            return;
        }

        if (slotIndex < 0 || slotIndex >= extraData.getBookIngredients().size()) {
            return;
        }

        BookData bookData = extraData.getBookIngredients().get(slotIndex);
        extraData.getBookIngredients().remove(slotIndex);

        InventoryUtils.addItem(owner, bookData.getItemStack());
        RandomRangeInt xpIngredients = extraData.getSettings().getXp(bookData.getCESimple());
        RandomRangeInt currentXp = extraData.getRandomXp();
        extraData.setRandomXp(new RandomRangeInt(
                currentXp.getMin() - xpIngredients.getMin(),
                currentXp.getMax() - xpIngredients.getMax()));

        updateMenu();
    }

    public void returnMainBook() {
        if (extraData.hasMainBook()) {
            InventoryUtils.addItem(owner, extraData.getMainBook().getItemStack());
            extraData.clearMainBook();
            extraData.resetXp();

            // Return ingredients only if not yet consolidated (readyToUpgrade == false means not consolidated)
            if (!extraData.isReadyToUpgrade()) {
                returnBookIngredients();
            }
            extraData.setReadyToUpgrade(false);
        }
    }

    private void returnBookIngredients() {
        if (extraData.hasBookIngredients()) {
            List<ItemStack> items = new ArrayList<>();
            for (BookData bookData : extraData.getBookIngredients()) {
                items.add(bookData.getItemStack());
            }
            InventoryUtils.addItem(owner, items);
            extraData.clearBookIngredients();
        }
    }

    private void returnAllItems() {
        if (extraData.hasMainBook()) {
            InventoryUtils.addItem(owner, extraData.getMainBook().getItemStack());
        }
        if (!extraData.isReadyToUpgrade() && extraData.hasBookIngredients()) {
            List<ItemStack> items = new ArrayList<>();
            for (BookData bookData : extraData.getBookIngredients()) {
                items.add(bookData.getItemStack());
            }
            InventoryUtils.addItem(owner, items);
        }
    }

    // ==================== Menu Rendering ====================

    public void updateMenu() {
        updateMainBookSlot();
        updateBookIngredientSlots();
    }

    private void updateMainBookSlot() {
        if (extraData.hasMainBook()) {
            ItemStack displayItem;
            if (!extraData.isReadyToUpgrade()) {
                displayItem = CEAPI.getCEBookItemStack("upgrade-preview1", extraData.getMainBook().getCESimple());
            } else {
                displayItem = extraData.getMainBook().getItemStack();
            }
            updateSlots("book-upgrade", displayItem);
            processMainBook();
        } else {
            updateSlots("book-upgrade", null);
            updateSlots("book-result", null);
            updateSlots("remind", null);
        }
    }

    private void processMainBook() {
        CEEnchantSimple ceEnchantSimple = extraData.getMainBook().getCESimple();
        BookUpgradeData bookUpgradeData = extraData.getSettings().getBookUpgradeData(
                ceEnchantSimple.getName(), ceEnchantSimple.getLevel());

        if (bookUpgradeData == null) {
            return;
        }

        int currentXp = ceEnchantSimple.getXp();
        int requiredXp = bookUpgradeData.getRequiredXp();

        extraData.setReadyToUpgrade(currentXp >= requiredXp);

        if (extraData.isReadyToUpgrade()) {
            ItemStack resultItem = CEAPI.getCEBookItemStack(bookUpgradeData.getNextEnchant());
            updateSlots("book-result", resultItem);
            updateBookConfirm();
        } else {
            String upgradePreview = "upgrade-preview1";

            if (extraData.hasBookIngredients()) {
                if (getMinFutureXp() != getMaxFutureXp()) {
                    upgradePreview = "upgrade-preview2";
                } else {
                    upgradePreview = "upgrade-preview3";
                }
            }

            ItemStack previewItem = CEAPI.getCEBookItemStack(upgradePreview, ceEnchantSimple);
            ItemStackUtils.setItemStack(previewItem, getPreviewBookPlaceholder(ceEnchantSimple));
            updateSlots("book-result", previewItem);
            updateSlots("remind", getTemplateItemStack("remind-xp"));
        }
    }

    private void updateBookIngredientSlots() {
        if (extraData.isReadyToUpgrade() && extraData.hasMainBook()) {
            // Show requirement items in ingredient slots
            RequirementList requirementList = getBookUpgradeData().getRequirementList();

            List<ItemStack> requirementItems = new ArrayList<>();
            for (AbstractRequirement requirement : requirementList) {
                if (requirement instanceof ItemRequirement) {
                    ItemRequirement.Data data = ((ItemRequirement) requirement).getData();
                    ItemStack itemStack = data.getTemplateItemStack().clone();
                    itemStack = ItemStackUtils.getSimpleItemStackWithPlaceholder(itemStack, owner);
                    itemStack.setAmount(data.getAmount());
                    requirementItems.add(itemStack);
                }
            }

            List<Integer> slots = getSlotsByName("ingredient-preview");
            slots = reorderSlots(slots);

            for (int i = 0; i < slots.size(); i++) {
                if (i < requirementItems.size()) {
                    inventory.setItem(slots.get(i), requirementItems.get(i));
                } else {
                    inventory.setItem(slots.get(i), getTemplateItemStack("ingredient-preview"));
                }
            }
            return;
        }

        if (extraData.hasBookIngredients()) {
            List<Integer> slots = getSlotsByName("ingredient-preview");
            slots = reorderSlots(slots);

            for (int i = 0; i < slots.size(); i++) {
                if (i < extraData.getBookIngredients().size()) {
                    BookData bookData = extraData.getBookIngredients().get(i);
                    inventory.setItem(slots.get(i), bookData.getItemStack());
                } else {
                    inventory.setItem(slots.get(i), getTemplateItemStack("ingredient-preview"));
                }
            }

            updateSlots("remind", getTemplateItemStack("remind-xp-confirm"));
        } else {
            updateSlots("ingredient-preview", null);
            if (extraData.hasMainBook()) {
                updateSlots("remind", getTemplateItemStack("remind-xp"));
            }
        }
    }

    private void updateBookConfirm() {
        BookUpgradeData bookUpgradeData = getBookUpgradeData();

        List<String> requirementLore = BafFrameworkAPI.getRequirementLore(owner, bookUpgradeData.getRequirementList());
        requirementLore = PlaceholderAPI.setPlaceholders(owner, requirementLore);

        ItemStack confirmItem = getTemplateItemStack("remind-book-confirm");
        if (confirmItem == null) {
            return;
        }

        PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
        placeholderBuilder.put("{requirement_description}", requirementLore);
        placeholderBuilder.put("{chance}", StringUtils.formatNumber(bookUpgradeData.getSuccessChance().getChance() * 100));

        ItemStackUtils.setItemStack(confirmItem, placeholderBuilder.build());
        updateSlots("remind", confirmItem);
    }

    // ==================== XP Calculations ====================

    public int getMinFutureXp() {
        return Math.min(
                extraData.getMainBook().getCESimple().getXp() + extraData.getRandomXp().getMin(),
                getBookUpgradeData().getRequiredXp());
    }

    public int getMaxFutureXp() {
        return Math.min(
                extraData.getMainBook().getCESimple().getXp() + extraData.getRandomXp().getMax(),
                getBookUpgradeData().getRequiredXp());
    }

    private String getEnchantFutureProgress() {
        BookUpgradeData bookUpgradeData = getBookUpgradeData();
        if (bookUpgradeData == null) {
            return "";
        }

        int xp = extraData.getMainBook().getCESimple().getXp();
        int requiredXp = bookUpgradeData.getRequiredXp();
        int progress = (int) Math.floor((double) xp / requiredXp * 10);
        int maxXpBonus = getMaxFutureXp();
        int bonusProgress = (int) Math.floor((double) maxXpBonus / requiredXp * 10) - progress;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i < progress) {
                builder.append("&a");
            } else if (i < progress + bonusProgress) {
                builder.append("&#99ff90");
            } else {
                builder.append("&7");
            }
            builder.append("▌");
        }

        return builder.toString();
    }

    private Map<String, String> getPreviewBookPlaceholder(CEEnchantSimple ceEnchantSimple) {
        Map<String, String> placeholder = new LinkedHashMap<>();

        if (extraData.hasBookIngredients()) {
            placeholder.put("%enchant_future_xp%", StringUtils.formatNumber(getMinFutureXp()));
            placeholder.put("%enchant_future_xp1%", StringUtils.formatNumber(getMinFutureXp()));
            placeholder.put("%enchant_future_xp2%", StringUtils.formatNumber(getMaxFutureXp()));
            placeholder.put("%enchant_future_progress%", getEnchantFutureProgress());
        }

        return placeholder;
    }

    // ==================== Helpers ====================

    public BookUpgradeData getBookUpgradeData() {
        return extraData.getSettings().getBookUpgradeData(
                extraData.getMainBook().getCESimple().getName(),
                extraData.getMainBook().getCESimple().getLevel());
    }

    private void broadcastUpgradeSuccess(ItemStack itemStack) {
        Execute execute = extraData.getSettings().getBroadcastUpgradeSuccessExecute();
        if (execute == null) {
            return;
        }

        PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
        placeholderBuilder.put("{player_name}", owner.getName());
        placeholderBuilder.put("{book_display}", itemStack.getItemMeta().getDisplayName());

        execute.execute(owner, placeholderBuilder.build());
    }

    public static List<Integer> reorderSlots(List<Integer> slots) {
        List<Integer> reorderedSlots = new ArrayList<>();
        int midpoint = slots.size() / 2;

        reorderedSlots.add(slots.get(midpoint));

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

}
