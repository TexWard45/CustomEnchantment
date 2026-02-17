package com.bafmc.customenchantment.menu.ceanvil;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ItemData;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class CEAnvilSettings {

    private final Map<String, List<Integer>> slotMap = new HashMap<>();
    private final Map<String, ItemStack> defaultItemStackMap = new HashMap<>();
    private int[] previewIndexOrder;

    public void initialize(MenuData menuData) {
        if (menuData == null || menuData.getItemMap() == null) {
            return;
        }

        for (Map.Entry<String, ItemData> entry : menuData.getItemMap().entrySet()) {
            String name = entry.getKey();
            ItemData itemData = entry.getValue();

            List<Integer> slots = itemData.getSlots();
            if (slots != null && !slots.isEmpty()) {
                slotMap.put(name, new ArrayList<>(slots));
                defaultItemStackMap.put(name, itemData.getItemStackBuilder().getItemStack().clone());
            }
        }

        // Load preview-index-order from data config
        if (menuData.getDataConfig() != null) {
            List<Integer> orderList = menuData.getDataConfig().getIntegerList("preview-index-order");
            if (orderList != null && !orderList.isEmpty()) {
                previewIndexOrder = orderList.stream().mapToInt(Integer::intValue).toArray();
            } else {
                previewIndexOrder = new int[]{3, 2, 4, 1, 5};
            }
        } else {
            previewIndexOrder = new int[]{3, 2, 4, 1, 5};
        }
    }

    public List<Integer> getSlots(String itemName) {
        return slotMap.getOrDefault(itemName, new ArrayList<>());
    }

    public ItemStack getDefaultItemStack(String itemName) {
        ItemStack defaultItem = defaultItemStackMap.get(itemName);
        return defaultItem != null ? defaultItem.clone() : null;
    }
}
