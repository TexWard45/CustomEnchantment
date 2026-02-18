package com.bafmc.customenchantment.menu.tinkerer;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import com.bafmc.bukkit.bafframework.custommenu.menu.AbstractMenu;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.list.DefaultItem;
import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.menu.tinkerer.item.TinkerAcceptItem;
import com.bafmc.customenchantment.menu.tinkerer.item.TinkerSlotItem;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tinkerer menu - migrated to new CustomMenu BafFramework API
 */
public class TinkererCustomMenu extends AbstractMenu<MenuData, TinkererExtraData> {

    public static final String MENU_NAME = "tinkerer";

    @Getter
    private static TinkererSettings settings;

    public TinkererCustomMenu() {
        // Constructor - menu instance created
    }

    public static void setSettings(TinkererSettings settings) {
        TinkererCustomMenu.settings = settings;
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
        registerItem(TinkerSlotItem.class);
        registerItem(TinkerAcceptItem.class);
    }

    @Override
    public void setupItems() {
        // First, set up all YAML-defined items (borders, placeholders, buttons)
        super.setupItems();

        // Then, overlay the dynamic tinkerer items on top
        if (extraData != null && extraData.getTinkererDataList() != null) {
            updateMenu();
        }
    }

    /**
     * Handle clicks in player's inventory - add CE items to tinkerer
     * This method is called automatically by the framework for player inventory clicks
     *
     * Each click only takes 1 item from the stack, regardless of stack size.
     * This prevents reward commands from executing multiple times.
     */
    @Override
    public void handlePlayerInventoryClick(ClickData data) {
        Player player = data.getPlayer();
        ItemStack clickedItem = data.getEvent().getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        // Check if it's a CE item
        CEItem ceItem = CEAPI.getCEItem(clickedItem);

        if (ceItem == null) {
            CustomEnchantmentMessage.send(player, "menu.tinkerer.not-support-item");
            return;
        }

        // Clone the item with amount=1 (take only 1 from the stack)
        ItemStack singleItem = clickedItem.clone();
        singleItem.setAmount(1);

        // Try to add the single item
        TinkererExtraData.TinkererAddReason reason = addItem(singleItem, ceItem);

        // If successful, decrease stack by 1 (or remove if last item)
        if (reason == TinkererExtraData.TinkererAddReason.SUCCESS) {
            int newAmount = clickedItem.getAmount() - 1;
            if (newAmount <= 0) {
                data.getEvent().setCurrentItem(null);
            } else {
                clickedItem.setAmount(newAmount);
            }
        }

        // Send feedback message
        CustomEnchantmentMessage.send(player, "menu.tinkerer.add-tinkerer." + EnumUtils.toConfigStyle(reason));
    }

    @Override
    public void handleClose() {
        // Return all items to player when menu closes
        returnItems();
    }

    // ==================== Business Logic (migrated from TinkererMenu) ====================

    /**
     * Add a CE item to the tinkerer
     */
    public TinkererExtraData.TinkererAddReason addItem(ItemStack itemStack, CEItem ceItem) {
        List<TinkererExtraData.TinkererData> list = extraData.getTinkererDataList();

        if (list.size() >= settings.getSize()) {
            return TinkererExtraData.TinkererAddReason.FULL_SLOT;
        }

        TinkererReward reward = settings.getReward(ceItem);

        if (reward == null) {
            return TinkererExtraData.TinkererAddReason.NOT_SUPPORT_ITEM;
        }

        list.add(new TinkererExtraData.TinkererData(itemStack, reward));
        updateMenu();
        return TinkererExtraData.TinkererAddReason.SUCCESS;
    }

    /**
     * Update menu display - show items and rewards
     */
    public void updateMenu() {
        List<TinkererExtraData.TinkererData> list = extraData.getTinkererDataList();
        int size = settings.getSize();

        for (int i = 0; i < size; i++) {
            int tinkerSlot = settings.getTinkerSlot(i);
            int rewardSlot = settings.getRewardSlot(i);

            if (i < list.size()) {
                TinkererExtraData.TinkererData data = list.get(i);
                ItemStack inputItem = data.getItemStack();

                // Clone reward ItemStack and set amount to match input item
                ItemStack rewardItem = data.getReward().getItemStack().clone();
                rewardItem.setAmount(inputItem.getAmount());

                // Set items in inventory
                inventory.setItem(tinkerSlot, inputItem);
                inventory.setItem(rewardSlot, rewardItem);
            } else {
                // Clear empty slots
                inventory.setItem(tinkerSlot, null);
                inventory.setItem(rewardSlot, null);
            }
        }
    }

    /**
     * Return item at clicked slot to player
     */
    public void returnItem(int slot) {
        List<TinkererExtraData.TinkererData> list = extraData.getTinkererDataList();
        int index = settings.getTinkerIndex(slot);

        if (index >= list.size()) {
            return;
        }

        TinkererExtraData.TinkererData data = list.remove(index);
        InventoryUtils.addItem(owner, Arrays.asList(data.getItemStack()));
        updateMenu();
    }

    /**
     * Return all items to player
     */
    public void returnItems() {
        List<TinkererExtraData.TinkererData> list = extraData.getTinkererDataList();
        List<ItemStack> itemStacks = new ArrayList<>();

        for (TinkererExtraData.TinkererData data : list) {
            itemStacks.add(data.getItemStack());
        }

        InventoryUtils.addItem(owner, itemStacks);
    }

    /**
     * Confirm tinkerer - give rewards and consume items
     */
    public TinkererExtraData.TinkererConfirmReason confirmTinkerer() {
        List<TinkererExtraData.TinkererData> list = extraData.getTinkererDataList();

        if (list.isEmpty()) {
            return TinkererExtraData.TinkererConfirmReason.NOTHING;
        }

        // Execute rewards
        for (TinkererExtraData.TinkererData data : list) {
            data.getReward().getExecute().execute(owner);
        }

        // Clear list and update display
        list.clear();
        updateMenu();

        return TinkererExtraData.TinkererConfirmReason.SUCCESS;
    }
}
