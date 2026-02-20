package com.bafmc.customenchantment;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class EnchantmentMergeFile {
    @Test
    public void onMerge() {
        assumeTrue(Bukkit.getServer() != null, "Bukkit server not available");
        File folder = new File("src/main/resources/enchantment");
        File mergeFile = new File("src/main/resources/enchantment/all.yml");

        AdvancedFileConfiguration mergeConfig = new AdvancedFileConfiguration(mergeFile);

        for (File file : folder.listFiles()) {
            if (file.getName().equals("all.yml")) {
                continue;
            }

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
