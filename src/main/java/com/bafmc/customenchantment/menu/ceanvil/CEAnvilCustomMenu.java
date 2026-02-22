package com.bafmc.customenchantment.menu.ceanvil;

import com.bafmc.bukkit.bafframework.custommenu.menu.AbstractMenu;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.list.DefaultItem;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.CustomEnchantmentLog;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.constant.CEMessageKey;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.menu.ceanvil.handler.DefaultHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.Slot2Handler;
import com.bafmc.customenchantment.menu.ceanvil.item.AnvilConfirmItem;
import com.bafmc.customenchantment.menu.ceanvil.item.AnvilPageItem;
import com.bafmc.customenchantment.menu.ceanvil.item.AnvilPreviewItem;
import com.bafmc.customenchantment.menu.ceanvil.item.AnvilSlotItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CEAnvilCustomMenu extends AbstractMenu<MenuData, CEAnvilExtraData> {

    public static final String MENU_NAME = "ce-anvil";

    private static final Map<String, Supplier<Slot2Handler>> handlerFactoryMap = new HashMap<>();

    public static void registerHandler(String type, Supplier<Slot2Handler> factory) {
        handlerFactoryMap.put(type, factory);
    }

    @Override
    public String getType() {
        return MENU_NAME;
    }

    @Override
    public void registerItems() {
        registerItem(DefaultItem.class);
        registerItem(AnvilSlotItem.class);
        registerItem(AnvilConfirmItem.class);
        registerItem(AnvilPreviewItem.class);
        registerItem(AnvilPageItem.class);
    }

    @Override
    public void setupItems() {
        super.setupItems();
        extraData.getSettings().initialize(getMenuData());
    }

    @Override
    public void handlePlayerInventoryClick(ClickData data) {
        ItemStack clickedItem = data.getEvent().getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        CEItem ceItem = CEAPI.getCEItem(clickedItem);
        if (ceItem == null) {
            return;
        }

        CEAnvilExtraData.CEAnvilAddReason reason = addItem(clickedItem, ceItem);

        if (reason == CEAnvilExtraData.CEAnvilAddReason.SUCCESS) {
            data.getEvent().setCurrentItem(null);
        }

        CustomEnchantmentMessage.send(data.getPlayer(), reason);
    }

    @Override
    public void handleClose() {
        returnItems();
    }

    // ==================== Public API for handlers ====================

    public CEAnvilExtraData getExtraData() {
        return extraData;
    }

    // ==================== Business Logic ====================

    public CEAnvilExtraData.CEAnvilAddReason addItem(ItemStack itemStack, CEItem ceItem) {
        // Check if item is suitable for slot1 (weapon)
        if (ceItem instanceof CEWeapon) {
            if (extraData.getItemData1() == null) {
                extraData.setItemData1(new AnvilItemData(itemStack, ceItem));

                // Create DefaultHandler when slot1 is filled with no slot2
                if (extraData.getActiveHandler() == null) {
                    extraData.setActiveHandler(new DefaultHandler());
                }

                updateMenu();
                return CEAnvilExtraData.CEAnvilAddReason.SUCCESS;
            } else {
                return CEAnvilExtraData.CEAnvilAddReason.ALREADY_HAS_SLOT1;
            }
        }

        // Check handlerFactoryMap for slot2 items - create NEW instance per use (prototype pattern)
        Supplier<Slot2Handler> foundFactory = null;
        for (Map.Entry<String, Supplier<Slot2Handler>> entry : handlerFactoryMap.entrySet()) {
            Slot2Handler prototype = entry.getValue().get();
            if (prototype.isSuitable(ceItem)) {
                foundFactory = entry.getValue();
                break;
            }
        }

        if (foundFactory != null) {
            if (extraData.getItemData2() == null) {
                // Clear old handler previews before switching
                if (extraData.getActiveHandler() != null) {
                    extraData.getActiveHandler().clearPreviews(this);
                }

                // Create NEW handler instance for this menu (avoids state sharing between players)
                extraData.setActiveHandler(foundFactory.get());
                extraData.setItemData2(new AnvilItemData(itemStack, ceItem));
                updateMenu();
                return CEAnvilExtraData.CEAnvilAddReason.SUCCESS;
            } else {
                return CEAnvilExtraData.CEAnvilAddReason.ALREADY_HAS_SLOT2;
            }
        }

        return CEAnvilExtraData.CEAnvilAddReason.NOT_SUITABLE;
    }

    public void confirm() {
        if (extraData.getItemData1() == null || extraData.getItemData2() == null) {
            return;
        }

        CEItem ceItem1 = extraData.getItemData1().getCeItem();
        CEItem ceItem2 = extraData.getItemData2().getCeItem();

        ItemStack itemStack1 = extraData.getItemData1().getItemStack();
        ItemStack itemStack2 = extraData.getItemData2().getItemStack();

        int amount1 = itemStack1.getAmount();
        int amount2 = itemStack2.getAmount();

        ApplyReason reason = extraData.getActiveHandler().apply(ceItem1, ceItem2);

        boolean useItem2 = false;

        switch (reason.getResult()) {
            case FAIL:
                useItem2 = true;
                break;
            case FAIL_AND_UPDATE:
                if (amount1 > 1) {
                    InventoryUtils.addItem(owner, ItemStackUtils.getItemStack(ceItem1.exportTo(), 1));
                    extraData.getItemData1().updateItemStack(ItemStackUtils.getItemStack(itemStack1, amount1 - 1));
                } else {
                    extraData.getItemData1().updateItemStack(ceItem1.exportTo());
                }
                useItem2 = true;
                break;
            case DESTROY:
                if (amount1 > 1) {
                    extraData.getItemData1().updateItemStack(ItemStackUtils.getItemStack(itemStack1, amount1 - 1));
                } else {
                    extraData.setItemData1(null);
                }
                useItem2 = true;
                break;
            case NOTHING:
                break;
            case CANCEL:
                break;
            case SUCCESS:
                if (amount1 > 1) {
                    InventoryUtils.addItem(owner, ItemStackUtils.getItemStack(ceItem1.exportTo(), 1));
                    extraData.getItemData1().updateItemStack(ItemStackUtils.getItemStack(itemStack1, amount1 - 1));
                } else {
                    extraData.getItemData1().updateItemStack(ceItem1.exportTo());
                }
                useItem2 = true;
                break;
            default:
                break;
        }

        if (useItem2) {
            extraData.getItemData2().updateItemStack(ItemStackUtils.getItemStack(itemStack2, amount2 - 1));
        }

        if (reason.isWriteLogs()) {
            reason.setPlayer(owner);
            reason.setCEItem1(ceItem2);
            reason.setCEItem2(ceItem1);
            CustomEnchantmentLog.writeItemActionLogs(reason);
        }

        if (reason.getRewards() != null) {
            InventoryUtils.addItem(owner, reason.getRewards());
        }

        CustomEnchantmentMessage.send(owner, CEMessageKey.ceItem(ceItem2.getType(), reason.getReason().toLowerCase()),
                reason.getPlaceholder());

        updateMenu();
    }

    public void returnItem(String name) {
        ItemStack itemStack = null;

        if ("slot1".equals(name) && extraData.getItemData1() != null) {
            itemStack = extraData.getItemData1().getItemStack();

            if (extraData.getActiveHandler() != null) {
                extraData.getActiveHandler().clearPreviews(this);
            }

            extraData.setItemData1(null);

            // Only null handler if slot2 is also empty.
            // If slot2 has an item, preserve the handler so it restores
            // when slot1 is re-added (matches legacy behavior).
            if (extraData.getItemData2() == null) {
                extraData.setActiveHandler(null);
            }
        } else if ("slot2".equals(name) && extraData.getItemData2() != null) {
            itemStack = extraData.getItemData2().getItemStack();

            if (extraData.getActiveHandler() != null) {
                extraData.getActiveHandler().clearPreviews(this);
            }

            // Revert to DefaultHandler if slot1 still has an item
            if (extraData.getItemData1() != null) {
                extraData.setActiveHandler(new DefaultHandler());
            } else {
                extraData.setActiveHandler(null);
            }

            extraData.setItemData2(null);
        }

        if (itemStack == null) {
            return;
        }

        InventoryUtils.addItem(owner, itemStack);
        updateMenu();
    }

    private void returnItems() {
        List<ItemStack> items = new ArrayList<>();
        if (extraData.getItemData1() != null) {
            items.add(extraData.getItemData1().getItemStack());
        }
        if (extraData.getItemData2() != null) {
            items.add(extraData.getItemData2().getItemStack());
        }
        InventoryUtils.addItem(owner, items);
    }

    private void updateMenu() {
        updateItemSlot1();
        updateItemSlot2();

        if (extraData.getItemData1() != null && extraData.getActiveHandler() != null) {
            extraData.getActiveHandler().updateView(this);
            updateConfirm();
        } else {
            clearAllPreviews();
        }
    }

    private void updateItemSlot1() {
        AnvilItemData itemData1 = extraData.getItemData1();
        if (itemData1 == null) {
            updateSlots("slot1", null);
            return;
        }

        ItemStack itemStack = itemData1.getItemStack();
        if (itemStack == null || itemStack.getAmount() == 0 || itemStack.getType() == Material.AIR) {
            extraData.setItemData1(null);
            extraData.setActiveHandler(null);
            updateSlots("slot1", null);
        } else {
            updateSlots("slot1", itemStack);
        }
    }

    private void updateItemSlot2() {
        AnvilItemData itemData2 = extraData.getItemData2();
        if (itemData2 == null) {
            updateSlots("slot2", null);
            return;
        }

        ItemStack itemStack = itemData2.getItemStack();
        if (itemStack == null || itemStack.getAmount() == 0 || itemStack.getType() == Material.AIR) {
            extraData.setItemData2(null);
            if (extraData.getItemData1() != null) {
                extraData.setActiveHandler(new DefaultHandler());
            } else {
                extraData.setActiveHandler(null);
            }
            updateSlots("slot2", null);
        } else {
            updateSlots("slot2", itemStack);
        }
    }

    private void updateConfirm() {
        if (extraData.getItemData1() == null || extraData.getItemData2() == null) {
            updateSlots("confirm", null);
            return;
        }

        extraData.getActiveHandler().updateConfirm(this);
    }

    private void clearAllPreviews() {
        int[] previewOrder = extraData.getSettings().getPreviewIndexOrder();
        for (int previewNum : previewOrder) {
            updateSlots("preview" + previewNum, null);
        }
        updateSlots("confirm", null);
        updateSlots("next-page", null);
        updateSlots("previous-page", null);
    }
}
