package com.bafmc.customenchantment.mask;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.feature.placeholder.Placeholder;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.bukkit.utils.RomanUtils;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

public class MaskItemConfigGenerate {
    @Test
    public void onTest() {
        File file = new File("src/test/resources/mask.yml");
        File exportedFile = new File("src/test/resources/mask-exported.yml");

        AdvancedFileConfiguration config = new AdvancedFileConfiguration(file);
        AdvancedFileConfiguration exportedConfig = new AdvancedFileConfiguration(exportedFile);

        for (String tierStr : config.getKeySection("list", false)) {
            String tierColor = config.getString("list." + tierStr + ".color");

            PlaceholderBuilder builder = PlaceholderBuilder.builder();
            builder.put("{tier_color}", tierColor);

            for (String mobType : config.getKeySection("list." + tierStr + ".list", false)) {
                String display = config.getString("list." + tierStr + ".list." + mobType + ".display");
                String color = config.getString("list." + tierStr + ".list." + mobType + ".color");
                String material  = config.getString("list." + tierStr + ".list." + mobType + ".material");
                EntityType entityType = null;

                builder.put("{display}", display);
                builder.put("{color}", color);
                builder.put("{enchant}", mobType.replace("-", ""));

                if (EnumUtils.valueOf(EntityType.class, mobType.toUpperCase().replace("-", "_")) != null) {
                    builder.put("{entity_type}", mobType.toUpperCase().replace("-", "_"));
                    entityType = EntityType.valueOf(mobType.toUpperCase().replace("-", "_"));
                }else {
//                    System.out.println(mobType + " is not a valid entity type. Found other entity type: " + config.get("list." + tierStr + ".list." + mobType + ".type"));
                    builder.put("{entity_type}", config.get("list." + tierStr + ".list." + mobType + ".type"));
                    entityType = EntityType.valueOf(config.getString("list." + tierStr + ".list." + mobType + ".type").replace("-", "_"));
                }

                for (int i = 1; i <= 5; i++) {
                    String level = RomanUtils.toRoman(i);
                    // &#9ca3af&l1 ★
                    //&#84cc16&l2 ★
                    //&#22c55e&l3 ★
                    //&#38bdf8&l4 ★
                    //&#fde047&l5 ★
                    //&#fb923c&l6 ★
                    //&#3b82f6&l7 ★
                    //&#a855f7&l8 ★
                    //&#f9158e&l9 ★
                    //&#e11d48&l10 ★
//                    if (i == 1) {
//                        builder.put("{level}", "&#9ca3af&l1 ★");
//                    }else if (i == 2) {
//                        builder.put("{level}", "&#84cc16&l2 ★");
//                    }else if (i == 3) {
//                        builder.put("{level}", "&#22c55e&l3 ★");
//                    }else if (i == 4) {
//                        builder.put("{level}", "&#38bdf8&l4 ★");
//                    }else if (i == 5) {
//                        builder.put("{level}", "&#fde047&l5 ★");
//                    }

                    builder.put("{level}", level);
                    builder.put("{level_number}", i);

                    Placeholder placeholder = builder.build();


                    if (material != null) {
                        exportedConfig.set("mask." + mobType + i + ".item.type", material);
                    }else {
                        exportedConfig.set("mask." + mobType + i + ".item.type", "PLAYER_HEAD");
                    }
                    exportedConfig.set("mask." +mobType + i+ ".item.display", placeholder.apply(config.getString("item-template.item.display")));
                    exportedConfig.set("mask." +mobType + i+ ".item.lore", placeholder.apply(config.getStringList("item-template.item.lore")));
                    exportedConfig.set("mask." +mobType + i+ ".item.skull-owner", placeholder.apply(config.getString("item-template.item.skull-owner")));
                    exportedConfig.set("mask." +mobType + i+ ".display.normal", placeholder.apply(config.getString("item-template.display.normal")));
                    exportedConfig.set("mask." +mobType + i+ ".display.bold", placeholder.apply(config.getString("item-template.display.bold")));
                    exportedConfig.set("mask." +mobType + i+ ".enchants", placeholder.apply(config.getStringList("item-template.enchants")));
                }

                //list:
                //  zombie-spawner:
                //    type: stackspawner
                //    item:
                //      type: default
                //      entity-type: ZOMBIE
                //    item-display:
                //      type: PLAYER_HEAD
                //      display: "{display}"
                //      lore:
                //        - "{lore}"
                //      skull-owner: "%baf_skull_owner_ZOMBIE%"
                //    sell-price: 5000.0
                //    buy-price: 10000.0
                //    max-stack-size: 64

                exportedConfig.set("list." + mobType + "-spawner.type", "stackspawner");
                exportedConfig.set("list." + mobType + "-spawner.item.type", "default");

                if (EnumUtils.valueOf(EntityType.class, mobType.toUpperCase()) != null) {
                    exportedConfig.set("list." + mobType + "-spawner.item.entity-type", mobType.toUpperCase());
                }else {
                    exportedConfig.set("list." + mobType + "-spawner.item.entity-type", config.get("list." + tierStr + ".list." + mobType + ".type"));
                }

                exportedConfig.set("list." + mobType + "-spawner.item-display.type", "PLAYER_HEAD");
                exportedConfig.set("list." + mobType + "-spawner.item-display.display", "{display}");
                exportedConfig.set("list." + mobType + "-spawner.item-display.lore", "{lore}");

                if (EnumUtils.valueOf(EntityType.class, mobType.toUpperCase()) != null) {
                    exportedConfig.set("list." + mobType + "-spawner.item-display.skull-owner", "%baf_skull_owner_" + mobType.toUpperCase() + "%");
                }else {
                    exportedConfig.set("list." + mobType + "-spawner.item-display.skull-owner", "%baf_skull_owner_" + config.get("list." + tierStr + ".list." + mobType + ".type") + "%");
                }

                exportedConfig.set("list." + mobType + "-spawner.sell-price", 5000.0);
                exportedConfig.set("list." + mobType + "-spawner.buy-price", 10000.0);
                exportedConfig.set("list." + mobType + "-spawner.max-stack-size", 64);

                // skull-slime:
                //  type: PLAYER_HEAD
                //  display: "&f&lĐầu Slime"
                //  lore:
                //    - "&#77ff00Nguyên liệu hiếm"
                //    - ""
                //    - "&7Có thể rơi ra khi giết quái"
                //  skull-owner: "%baf_skull_owner_SLIME%"
//
                Placeholder placeholder = builder.build();
//
                String id = "skull-" + EnumUtils.toConfigStyle(entityType);
                exportedConfig.set("storage." + id + ".type", "PLAYER_HEAD");
                exportedConfig.set("storage." + id + ".display", placeholder.apply(config.getString("skull-storage-template.item.display")));
                exportedConfig.set("storage." + id + ".lore", placeholder.apply(config.getStringList("skull-storage-template.item.lore")));
                exportedConfig.set("storage." + id + ".skull-owner", placeholder.apply(config.getString("skull-storage-template.item.skull-owner")));

                // mask-piece-template:
                //  item:
                //    type: POPPED_CHORUS_FRUIT
                //    display: "&d&lMảnh mặt nạ &{color}&l{display}"
                //    lore:
                //      - "&#00bbffNguyên liệu rất hiếm"
                //      - ""
                //      - "&7Dùng để chế tạo mặt nạ"
                //    enchants:
                //      - DURABILITY 1
                //    flags:
                //      - HIDE_ENCHANTS

                String maskPieceId = "mask-piece-" + EnumUtils.toConfigStyle(entityType);
                exportedConfig.set("mask-piece." + maskPieceId + ".type", "POPPED_CHORUS_FRUIT");
                exportedConfig.set("mask-piece." + maskPieceId + ".display", placeholder.apply(config.getString("mask-piece-template.item.display")));
                exportedConfig.set("mask-piece." + maskPieceId + ".lore", placeholder.apply(config.getStringList("mask-piece-template.item.lore")));
                exportedConfig.set("mask-piece." + maskPieceId + ".enchants", placeholder.apply(config.getStringList("mask-piece-template.item.enchants")));
                exportedConfig.set("mask-piece." + maskPieceId + ".flags", placeholder.apply(config.getStringList("mask-piece-template.item.flags")));
            }
        }

        exportedConfig.save();
    }
}
