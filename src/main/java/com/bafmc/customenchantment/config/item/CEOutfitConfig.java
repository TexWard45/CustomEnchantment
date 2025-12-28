package com.bafmc.customenchantment.config.item;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.config.AbstractConfig;
import com.bafmc.customenchantment.item.outfit.CEOutfit;
import com.bafmc.customenchantment.item.outfit.CEOutfitData;
import com.bafmc.customenchantment.item.outfit.CEOutfitGroup;
import com.bafmc.customenchantment.item.outfit.CEOutfitStorage;
import com.bafmc.customenchantment.item.skin.CESkin;
import com.bafmc.customenchantment.item.skin.CESkinData;
import com.bafmc.customenchantment.item.skin.CESkinStorage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CEOutfitConfig extends AbstractConfig {
    private CEOutfitStorage storage;

    public CEOutfitConfig(CEOutfitStorage storage) {
        this.storage = storage;
    }

    @Override
    protected void loadConfig() {
        for (String pattern : config.getKeySection("outfit", false)) {
            String path = "outfit." + pattern;

            String group = config.getStringColor(path + ".group");
            String enchant = config.getStringColor(path + ".enchant");

            CEOutfitGroup outfitGroup = CustomEnchantment.instance().getCeOutfitGroupMap().get(group);

            ItemStack itemStack = config.getItemStack(path + ".item", true, true);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (outfitGroup.getItemDisplay() != null) {
                PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
                placeholderBuilder.put("{item_display}", itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : "");

                String itemDisplay = outfitGroup.getItemDisplay();
                itemMeta.setDisplayName(placeholderBuilder.build().apply(itemDisplay));
            }

            if (outfitGroup.getItemLore() != null) {
                PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
                placeholderBuilder.put("{item_lore}", itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>());
                itemMeta.setLore(placeholderBuilder.build().apply(outfitGroup.getItemLore()));
            }
            itemStack.setItemMeta(itemMeta);

            CEOutfit ceItem = new CEOutfit(itemStack);

            for (String attributeFormat : config.getStringList(path + ".attributes")) {
                ceItem.getWeaponAttribute().addAttribute(attributeFormat);
            }

            int maxLevel = config.getInt(path + ".max-level");

            List<CEOutfitData.SpecialDisplayData> specialDisplayDataList = loadCEOutfitSpecialDisplayData(config.getAdvancedConfigurationSection(path + ".special-display"));

            Map<Integer, CEOutfitData.ConfigByLevelData> levelMap = loadOutfitConfigByLevelMap(config.getAdvancedConfigurationSection(path + ".levels"));

            CEOutfitData.ConfigData configData = new CEOutfitData.ConfigData(group, enchant, maxLevel, itemMeta.getDisplayName(), new ArrayList<>(itemMeta.getLore()), specialDisplayDataList, levelMap);

            CEOutfitData data = new CEOutfitData(pattern, configData);
            ceItem.setData(data);

            storage.put(pattern, ceItem);
        }
    }

    public List<CEOutfitData.SpecialDisplayData> loadCEOutfitSpecialDisplayData(AdvancedConfigurationSection config) {
        List<CEOutfitData.SpecialDisplayData> list = new ArrayList<>();

        for (String key : config.getKeys(false)) {
            try {
                MaterialList materialList = MaterialList.getMaterialList(config.getStringList(key + ".type"));
                String display = config.getString(key + ".display");

                CEOutfitData.SpecialDisplayData data = new CEOutfitData.SpecialDisplayData(materialList, display);
                list.add(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    public Map<Integer, CEOutfitData.ConfigByLevelData> loadOutfitConfigByLevelMap(AdvancedConfigurationSection config) {
        Map<Integer, CEOutfitData.ConfigByLevelData> map = new HashMap<>();

        for (String key : config.getKeys(false)) {
            try {
                int level = Integer.parseInt(key);

                Map<String, String> skinMap = new HashMap<>();

                AdvancedConfigurationSection skinConfig = config.getAdvancedConfigurationSection(key + ".skins");
                for (String skinKey : skinConfig.getKeys(false)) {
                    skinMap.put(skinKey, skinConfig.getString(skinKey));
                }

                Map<String, CEOutfitData.ConfigCustomTypeData> equipSlotDataMap = new HashMap<>();
                AdvancedConfigurationSection customTypeConfig = config.getAdvancedConfigurationSection(key + ".custom-types");
                for (String customType : customTypeConfig.getKeys(false)) {
                    List<String> list = customTypeConfig.getStringList(customType + ".list");
                    CEOutfitData.ConfigCustomTypeData data = new CEOutfitData.ConfigCustomTypeData(list);
                    equipSlotDataMap.put(customType, data);
                }

                CEOutfitData.ConfigByLevelData configByLevelData = CEOutfitData.ConfigByLevelData.builder()
                        .skinMap(skinMap)
                        .customTypeMap(equipSlotDataMap)
                        .build();

                map.put(level, configByLevelData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return map;
    }
}
