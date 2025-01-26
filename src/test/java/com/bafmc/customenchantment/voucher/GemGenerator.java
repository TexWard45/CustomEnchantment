package com.bafmc.customenchantment.voucher;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;

public class GemGenerator {
    @Test
    public void onTest() {
        AdvancedFileConfiguration gemConfig = getGemConfig();
    }

    public AdvancedFileConfiguration getGemConfig() {
        File file = new File("src/test/resources/voucher/gem-config.yml");
        return new AdvancedFileConfiguration(file);
    }
}
