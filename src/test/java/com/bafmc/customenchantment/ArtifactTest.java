package com.bafmc.customenchantment;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.utils.ColorUtils;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class ArtifactTest {
    @Test
    public void test() {
        File file = new File("src/test/resources/artifact/_6ultimate.yml");
        AdvancedFileConfiguration config = new AdvancedFileConfiguration(file);

        for (String key : config.getKeys(false)) {
            String displayPath = key + ".item.display";
            String lorePath = key + ".item.lore";

            String display = config.getString(displayPath);
            List<String> loreList = config.getStringList(lorePath);
//            System.out.println(key);
//            System.out.println(ChatColor.stripColor(ColorUtils.t(display)));
//
            String lore = "";
            for (int i = 0; i < loreList.size(); i++) {
                lore += ChatColor.stripColor(ColorUtils.t(loreList.get(i)));

                if (i != loreList.size() - 1) {
                    lore += "\n";
                }
            }

            System.out.println(key);
            System.out.println(lore);
        }
    }
}
