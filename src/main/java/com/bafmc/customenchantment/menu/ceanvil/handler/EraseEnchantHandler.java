package com.bafmc.customenchantment.menu.ceanvil.handler;

import com.bafmc.bukkit.bafframework.utils.EnchantmentUtils;
import com.bafmc.bukkit.utils.ColorUtils;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.utils.NumberUtils;
import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.ApplyResult;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.item.eraseenchant.CEEraseEnchant;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilSettings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EraseEnchantHandler implements Slot2Handler {

    @Getter @Setter
    private int page = 1;

    @Getter @Setter
    private int maxPage = 1;

    @Getter @Setter
    private int chooseIndex = -1;

    @Getter @Setter
    private List<Enchantment> eraseEnchantList = new ArrayList<>();

    @Getter @Setter
    private Map<Enchantment, Integer> enchantmentMap = new HashMap<>();

    @Override
    public boolean isSuitable(CEItem ceItem) {
        return ceItem instanceof CEEraseEnchant;
    }

    @Override
    public void updateView(CEAnvilCustomMenu menu) {
        CEItem ceItem1 = menu.getExtraData().getItemData1().getCeItem();
        CEItem ceItem2 = menu.getExtraData().getItemData2().getCeItem();
        CEEraseEnchant eraseEnchant = (CEEraseEnchant) ceItem2;

        CEWeapon weapon = (CEWeapon) ceItem1;
        Map<Enchantment, Integer> enchantMap = weapon.getDefaultItemStack().getEnchantments();
        this.enchantmentMap = enchantMap;
        this.eraseEnchantList = eraseEnchant.getEraseEnchantList(enchantMap);
        this.maxPage = Math.max(1, (int) Math.ceil(eraseEnchantList.size() / 5d));

        updateEraseDisplay(menu);
    }

    @Override
    public void updateConfirm(CEAnvilCustomMenu menu) {
        menu.updateSlots("confirm", menu.getTemplateItemStack("confirm-remove-enchant"));
    }

    @Override
    public void clickProcess(CEAnvilCustomMenu menu, String itemName) {
        if ("next-page".equals(itemName)) {
            if (page < maxPage) {
                page++;
                updateEraseDisplay(menu);
            }
        } else if ("previous-page".equals(itemName)) {
            if (page > 1) {
                page--;
                updateEraseDisplay(menu);
            }
        } else if (itemName.startsWith("preview")) {
            chooseEraseEnchant(menu, itemName);
        }
    }

    @Override
    public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
        if (!(ceItem2 instanceof CEEraseEnchant eraseEnchant)) {
            return ApplyReason.NOTHING;
        }

        if (enchantmentMap.isEmpty()) {
            return new ApplyReason("empty", ApplyResult.CANCEL);
        }

        int index = chooseIndex;
        Enchantment enchant = null;
        if (index != -1) {
            enchant = eraseEnchantList.get(index);
            chooseIndex = -1;
        }

        return eraseEnchant.applyByMenuTo(ceItem1, enchant);
    }

    @Override
    public void clearPreviews(CEAnvilCustomMenu menu) {
        CEAnvilSettings settings = menu.getExtraData().getSettings();
        int[] previewOrder = settings.getPreviewIndexOrder();
        for (int previewNum : previewOrder) {
            menu.updateSlots("preview" + previewNum, null);
        }
        menu.updateSlots("next-page", null);
        menu.updateSlots("previous-page", null);
    }

    private void updateEraseDisplay(CEAnvilCustomMenu menu) {
        CEAnvilSettings settings = menu.getExtraData().getSettings();
        int[] previewOrder = settings.getPreviewIndexOrder();

        for (int i = 0; i < 5; i++) {
            int listIndex = i + (page - 1) * 5;
            int previewNum = previewOrder[i];

            if (listIndex >= eraseEnchantList.size()) {
                menu.updateSlots("preview" + previewNum, null);
            } else {
                Enchantment enchantment = eraseEnchantList.get(listIndex);
                int level = enchantmentMap.get(enchantment);

                ItemStack book = getEnchantBook(enchantment, level);

                if (listIndex == chooseIndex) {
                    ItemStackUtils.setGlowingItemStack(book);
                }

                menu.updateSlots("preview" + previewNum, book);
            }
        }
        updatePageIndicators(menu);
    }

    private ItemStack getEnchantBook(Enchantment enchantment, int level) {
        ItemStack itemStack = new ItemStack(Material.BOOK);
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("&7" + EnchantmentUtils.getDisplayName(enchantment) + " " + NumberUtils.toRomanNumber(level));
        meta.setLore(ColorUtils.t(lore));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private void chooseEraseEnchant(CEAnvilCustomMenu menu, String previewName) {
        int previewNum = Integer.parseInt(previewName.replace("preview", ""));
        int[] previewOrder = menu.getExtraData().getSettings().getPreviewIndexOrder();

        int localIndex = -1;
        for (int i = 0; i < previewOrder.length; i++) {
            if (previewOrder[i] == previewNum) {
                localIndex = i;
                break;
            }
        }

        if (localIndex == -1) {
            return;
        }

        chooseIndex = localIndex + (page - 1) * 5;
        updateEraseDisplay(menu);
    }

    private void updatePageIndicators(CEAnvilCustomMenu menu) {
        if (page < maxPage) {
            ItemStack nextItem = menu.getTemplateItemStack("has-next-page");
            if (nextItem != null) {
                nextItem.setAmount(page + 1);
            }
            menu.updateSlots("next-page", nextItem);
        } else {
            menu.updateSlots("next-page", null);
        }

        if (page > 1) {
            ItemStack prevItem = menu.getTemplateItemStack("has-previous-page");
            if (prevItem != null) {
                prevItem.setAmount(page - 1);
            }
            menu.updateSlots("previous-page", prevItem);
        } else {
            menu.updateSlots("previous-page", null);
        }
    }
}
