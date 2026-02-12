package com.bafmc.customenchantment.config.item;

import com.bafmc.customenchantment.config.AbstractConfig;
import com.bafmc.customenchantment.item.skin.CESkin;
import com.bafmc.customenchantment.item.skin.CESkinData;
import com.bafmc.customenchantment.item.skin.CESkinStorage;
import org.bukkit.inventory.ItemStack;

public class CESkinConfig extends AbstractConfig {
    private CESkinStorage storage;

    public CESkinConfig(CESkinStorage storage) {
        this.storage = storage;
    }

    @Override
    protected void loadConfig() {
        for (String pattern : config.getKeySection("skin", false)) {
            String path = "skin." + pattern;

            ItemStack itemStack = config.getItemStack(path + ".item", true, true);
            CESkin ceItem = new CESkin(itemStack);

            String normalDisplay = config.getStringColor(path + ".display.normal");
            String boldDisplay = config.getStringColor(path + ".display.bold");

            CESkinData data = new CESkinData();
            data.setNormalDisplay(normalDisplay);
            data.setBoldDisplay(boldDisplay);
            data.setPattern(pattern);
            ceItem.setData(data);

            storage.put(pattern, ceItem);
        }
    }
}
