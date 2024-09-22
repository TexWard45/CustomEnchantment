package com.bafmc.customenchantment.menu;

import lombok.Getter;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CESimple;
import com.bafmc.customenchantment.menu.bookcraft.FastCraft;
import com.bafmc.customenchantment.menu.data.BookData;
import com.bafmc.custommenu.menu.CItem;
import com.bafmc.custommenu.menu.CMenuView;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.utils.ItemStackUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BookcraftMenu extends MenuAbstract {
    public static final String MENU_NAME = "bookcraft";
    private static HashMap<String, BookcraftMenu> map = new HashMap<String, BookcraftMenu>();
    @Getter
    private static BookcraftSettings settings;

    public static void setSettings(BookcraftSettings settings) {
        BookcraftMenu.settings = settings;
    }

    public static BookcraftMenu putBookCraftMenu(Player player, CMenuView cMenuView) {
        BookcraftMenu menu = map.get(player.getName());

        if (menu == null) {
            menu = new BookcraftMenu(cMenuView, player);
            map.put(player.getName(), menu);
        }
        return menu;
    }

    public static BookcraftMenu getBookCraftMenu(Player player) {
        return map.get(player.getName());
    }

    public static BookcraftMenu removeBookCraftMenu(Player player) {
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

    public BookcraftMenu(CMenuView menuView, Player player) {
        super(menuView, player);

        this.fastCraft = new FastCraft(this);
    }

    public BookAddReason addBook(ItemStack itemStack, CESimple ceSimple) {
        if (!ceSimple.getCEEnchant().getCEGroup().isCraft()) {
            return BookAddReason.NOT_CRAFT_ENCHANT;
        }

        if (ceSimple.getLevel() == ceSimple.getCEEnchant().getMaxLevel()) {
            return BookAddReason.MAX_LEVEL;
        }

        if (this.list.size() >= 2) {
            return BookAddReason.ENOUGH_BOOK;
        }

        if (!this.list.isEmpty()) {
            for (BookData bookData : list) {
                CESimple ceSimpleCompare = bookData.getCESimple();
                if (!ceSimpleCompare.getName().equals(ceSimple.getName())) {
                    return BookAddReason.DIFFERENT_ENCHANT;
                }
                if (ceSimpleCompare.getLevel() != ceSimple.getLevel()) {
                    return BookAddReason.DIFFERENT_LEVEL;
                }
            }
        }

        this.list.add(new BookData(itemStack, ceSimple));
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
        placeholder.put("%money%", String.valueOf(settings.getMoneyRequire(groupName)));
        itemStack = ItemStackUtils.setItemStack(itemStack, placeholder);

        updateSlots("accept", itemStack);
    }

    public void updateBookPreview() {
        if (list.size() < 2) {
            updateSlots("preview", null);
            return;
        }

        CESimple ceSimple = getCESimpleResult();
        updateSlots("preview", CEAPI.getCEBookItemStack(ceSimple));
    }

    public CESimple getCESimpleResult() {
        if (list.size() < 2) {
            return null;
        }

        CESimple ceSimple1 = list.get(0).getCESimple();
        CESimple ceSimple2 = list.get(1).getCESimple();

        String name = ceSimple1.getName();
        int level = ceSimple1.getLevel() + 1;
        int success = Math.max(ceSimple1.getSuccess().getValue(), ceSimple2.getSuccess().getValue());
        int destroy = Math.min(ceSimple1.getDestroy().getValue(), ceSimple2.getDestroy().getValue());

        return new CESimple(name, level, success, destroy);
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

        if (!settings.payMoney(player, groupName)) {
            return BookcraftConfirmReason.NOT_ENOUGH_MONEY;
        }

        CESimple ceSimpleResult = getCESimpleResult();
        ItemStack result = CEAPI.getCEBookItemStack(ceSimpleResult);
        InventoryUtils.addItem(player, Arrays.asList(result));

        clearBooks();
        return BookcraftConfirmReason.SUCCESS;
    }

    public void returnItems() {
        List<ItemStack> itemStacks = new ArrayList<ItemStack>();
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