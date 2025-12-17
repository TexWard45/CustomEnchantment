package com.bafmc.customenchantment.config.item;

import com.bafmc.customenchantment.config.AbstractConfig;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.item.CEWeaponData;
import com.bafmc.customenchantment.item.CEWeaponStorage;
import org.bukkit.inventory.ItemStack;

public class CEWeaponConfig extends AbstractConfig {
    private CEWeaponStorage storage;

    public CEWeaponConfig(CEWeaponStorage storage) {
        this.storage = storage;
    }

    @Override
    protected void loadConfig() {
        for (String pattern : config.getKeySection("weapon", false)) {
            String path = "weapon." + pattern;

            String customType = config.getString(path + ".custom-type");
            ItemStack itemStack = config.getItemStack(path + ".item", true, true);
            CEWeapon ceItem = new CEWeapon(itemStack);
            ceItem.setCustomType(customType);
            for (String enchantFormat : config.getStringList(path + ".enchants")) {
                String enchantName = null;
                int level = 1;

                int spaceIndex = enchantFormat.indexOf(" ");
                if (spaceIndex != -1) {
                    enchantName = enchantFormat.substring(0, spaceIndex);
                    level = Integer.parseInt(enchantFormat.substring(spaceIndex + 1, enchantFormat.length()));
                } else {
                    enchantName = enchantFormat;
                }

                ceItem.getWeaponEnchant().addCESimple(new CEEnchantSimple(enchantName, level));
            }

            for (String attributeFormat : config.getStringList(path + ".attributes")) {
                ceItem.getWeaponAttribute().addAttribute(attributeFormat);
            }

            CEWeaponData data = new CEWeaponData();
            data.setPattern(pattern);
            ceItem.setData(data);

            storage.put(pattern, ceItem);
        }
    }
}
