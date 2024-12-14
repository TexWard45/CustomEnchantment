package com.bafmc.customenchantment;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;

public class EnchantmentMergeFile {
    @Test
    public void onMerge() {
        File folder = new File("src/main/resources/enchantment");
        File mergeFile = new File("src/main/resources/enchantment/all.yml");

        AdvancedFileConfiguration mergeConfig = new AdvancedFileConfiguration(mergeFile);

        for (File file : folder.listFiles()) {
            AdvancedFileConfiguration config = new AdvancedFileConfiguration(file);

            Set<String> keys = config.getKeys(false);
            for (String key : keys) {
                mergeConfig.set(key, config.getConfigurationSection(key));
            }

            mergeConfig.save();
        }

        mergeConfig.save();
    }
}
