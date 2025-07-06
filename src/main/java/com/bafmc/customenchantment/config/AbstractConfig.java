package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

public abstract class AbstractConfig {
    protected AdvancedFileConfiguration config;

    public AbstractConfig() {
    }

    public void loadConfig(File file) {
        if (file.isDirectory()) {
            File[] var2 = file.listFiles();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                File f = var2[var4];
                this.loadConfig(f);
            }
        } else if (FileUtils.getExtension(file.getName()).equals("yml")) {
            this.config = new AdvancedFileConfiguration(file);
            this.loadConfig();
        }
    }

    public void loadConfig(AdvancedFileConfiguration config) {
        this.config = config;
        this.loadConfig();
    }

    protected abstract void loadConfig();
}
