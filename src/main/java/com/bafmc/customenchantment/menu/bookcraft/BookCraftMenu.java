package com.bafmc.customenchantment.menu.bookcraft;

import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.menu.MenuAbstract;
import com.bafmc.customenchantment.menu.data.BookData;
import com.bafmc.custommenu.menu.CItem;
import com.bafmc.custommenu.menu.CMenuView;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BookCraftMenu extends MenuAbstract {
    public static final String MENU_NAME = "book-craft";
    private static HashMap<String, BookCraftMenu> map = new HashMap<String, BookCraftMenu>();

    public static BookCraftMenu putBookCraftMenu(Player player, CMenuView cMenuView) {
        BookCraftMenu menu = map.get(player.getName());

        if (menu == null) {
            menu = new BookCraftMenu(cMenuView, player);
            map.put(player.getName(), menu);
        }
        return menu;
    }

    public static BookCraftMenu getBookCraftMenu(Player player) {
        return map.get(player.getName());
    }

    public static BookCraftMenu removeBookCraftMenu(Player player) {
        return map.remove(player.getName());
    }

    public enum BookAddReason {
        SUCCESS, DIFFERENT_ENCHANT, DIFFERENT_LEVEL, MAX_LEVEL, ENOUGH_BOOK, NOT_CRAFT_ENCHANT;
    }

    public enum BookcraftConfirmReason {
        SUCCESS, NOT_ENOUGH_MONEY, NOT_ENOUGH_BOOK;
    }

    @Getter
    private List<BookData> list = new ArrayList<BookData>();
    private FastCraft fastCraft;

    public BookCraftMenu(CMenuView menuView, Player player) {
        super(menuView, player);

        this.fastCraft = new FastCraft(this);
    }

    public BookAddReason addBook(ItemStack itemStack, CEEnchantSimple ceEnchantSimple) {
        if (!ceEnchantSimple.getCEEnchant().getCEGroup().isCraft()) {
            return BookAddReason.NOT_CRAFT_ENCHANT;
        }

        if (ceEnchantSimple.getLevel() == ceEnchantSimple.getCEEnchant().getMaxLevel()) {
            return BookAddReason.MAX_LEVEL;
        }

        if (this.list.size() >= 2) {
            return BookAddReason.ENOUGH_BOOK;
        }

        if (!this.list.isEmpty()) {
            for (BookData bookData : list) {
                CEEnchantSimple ceEnchantSimpleCompare = bookData.getCESimple();
                if (!ceEnchantSimpleCompare.getName().equals(ceEnchantSimple.getName())) {
                    return BookAddReason.DIFFERENT_ENCHANT;
                }
                if (ceEnchantSimpleCompare.getLevel() != ceEnchantSimple.getLevel()) {
                    return BookAddReason.DIFFERENT_LEVEL;
                }
            }
        }

        this.list.add(new BookData(itemStack, ceEnchantSimple));
        updateMenu();
        return BookAddReason.SUCCESS;
    }

    public void returnBook(String itemName) {
        if (itemName.equals("book1")) {
            returnBook(0);
        } else if (itemName.equals("book2")) {
            returnBook(1);
        }
    }

    public void returnBook(int index) {
        if (index >= list.size()) {
            return;
        }

        BookData data = this.list.remove(index);
        InventoryUtils.addItem(player, Arrays.asList(data.getItemStack()));
        updateMenu();
    }

    public void updateMenu() {
        updateBookIndex(0);
        updateBookIndex(1);
        updateBookPreview();
        updateAcceptSlot();
    }

    public void updateBookIndex(int index) {
        if (index < list.size()) {
            updateSlots("book" + (index + 1), list.get(index).getItemStack());
        } else {
            updateSlots("book" + (index + 1), null);
        }
    }

    public void updateAcceptSlot() {
        if (list.size() < 2) {
            updateSlots("remind", null);
            return;
        }

        CItem cItem = menuView.getCMenu().getMenuItem().getCItemByName("accept");

        if (cItem == null) {
            return;
        }
        ItemStack itemStack = cItem.getItemStack(player);

        String groupName = list.get(0).getCESimple().getCEEnchant().getGroupName();
        HashMap<String, String> placeholder = new HashMap<String, String>();
        placeholder.put("%money%", String.valueOf(CustomEnchantment.instance().getBookCraftConfig().getMoneyRequire(groupName)));
        itemStack = ItemStackUtils.setItemStack(itemStack, placeholder);

        updateSlots("accept", itemStack);
    }

    public void updateBookPreview() {
        if (list.size() < 2) {
            updateSlots("preview", null);
            return;
        }

        CEEnchantSimple ceEnchantSimple = getCESimpleResult();
        updateSlots("preview", CEAPI.getCEBookItemStack(ceEnchantSimple));
    }

    public CEEnchantSimple getCESimpleResult() {
        if (list.size() < 2) {
            return null;
        }

        CEEnchantSimple ceEnchantSimple1 = list.get(0).getCESimple();
        CEEnchantSimple ceEnchantSimple2 = list.get(1).getCESimple();

        String name = ceEnchantSimple1.getName();
        int level = ceEnchantSimple1.getLevel() + 1;
        int success = Math.max(ceEnchantSimple1.getSuccess().getValue(), ceEnchantSimple2.getSuccess().getValue());
        int destroy = Math.min(ceEnchantSimple1.getDestroy().getValue(), ceEnchantSimple2.getDestroy().getValue());

        return new CEEnchantSimple(name, level, success, destroy);
    }

    public BookcraftConfirmReason confirmUpgrade() {
        if (list.size() < 2) {
            if(this.list.size() == 1 && fastCraft.getBookHighLevel() != null) {
                return fastCraft.confirmUpgradeFastCraft();
            } else {
                return BookcraftConfirmReason.NOT_ENOUGH_BOOK;
            }
        }

        String groupName = list.get(0).getCESimple().getCEEnchant().getGroupName();

        if (!CustomEnchantment.instance().getBookCraftConfig().payMoney(player, groupName)) {
            return BookcraftConfirmReason.NOT_ENOUGH_MONEY;
        }

        CEEnchantSimple ceEnchantSimpleResult = getCESimpleResult();
        ItemStack result = CEAPI.getCEBookItemStack(ceEnchantSimpleResult);
        InventoryUtils.addItem(player, Arrays.asList(result));

        clearBooks();
        return BookcraftConfirmReason.SUCCESS;
    }

    public void returnItems() {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (BookData bookData : list) {
            itemStacks.add(bookData.getItemStack());
        }
        InventoryUtils.addItem(player, itemStacks);
    }

    public void clearBooks() {
        list.clear();
        updateMenu();
    }

    public void fastCraft(Player player) {
        this.fastCraft.fastCraft(player);
    }

    public Player getPlayer() {
        return player;
    }

    public CMenuView getMenuView() {
        return menuView;
    }
}