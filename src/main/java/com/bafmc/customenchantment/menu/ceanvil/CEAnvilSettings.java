package com.bafmc.customenchantment.menu.ceanvil;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import lombok.Getter;

import java.util.List;

@Getter
public class CEAnvilSettings {

    private int[] previewIndexOrder;

    public void initialize(MenuData menuData) {
        if (menuData == null) {
            previewIndexOrder = new int[]{3, 2, 4, 1, 5};
            return;
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
}
