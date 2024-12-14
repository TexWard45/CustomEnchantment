package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.bukkit.utils.FileUtils;
import com.bafmc.customenchantment.*;

public class ConfigModule extends PluginModule<CustomEnchantment> {
    public ConfigModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        onReload();
    }

    public void onReload() {
        FileUtils.createFolder(getPlugin().getArtifactFolder());
        FileUtils.createFolder(getPlugin().getBookUpgradeFolder());
        FileUtils.createFolder(getPlugin().getStorageItemFolder());
        FileUtils.createFolder(getPlugin().getEnchantFolder());
        FileUtils.createFolder(getPlugin().getMenuFolder());
        FileUtils.createFolder(getPlugin().getGeneralDataFolder());
        FileUtils.createFolder(getPlugin().getPlayerDataFolder());
        FileUtils.createFile(getPlugin().getGroupFile());
        FileUtils.createFile(getPlugin().getItemFile());
        FileUtils.createFile(getPlugin().getSaveItemFile());
        FileUtils.createFile(getPlugin().getBookCraftFile());
        FileUtils.createFile(getPlugin().getBookUpgradeFile());
        FileUtils.createFile(getPlugin().getTinkererFile());
        FileUtils.createFile(getPlugin().getMessagesFile());

        getPlugin().setCeEnchantMap(new CEEnchantMap());
        getPlugin().setCeGroupMap(new CEGroupMap());
        getPlugin().setCeItemStorageMap(new CEItemStorageMap());

        CustomEnchantmentMessage.setConfig(new AdvancedFileConfiguration(getPlugin().getMessagesFile()));

        getPlugin().saveDefaultConfig();
        getPlugin().reloadConfig();

        AdvancedFileConfiguration mainConfig = new AdvancedFileConfiguration(getPlugin().getConfigFile());
        getPlugin().setMainConfig(mainConfig.get(MainConfig.class));

        CEGroupConfig groupConfig = new CEGroupConfig();
        groupConfig.loadConfig(getPlugin().getGroupFile());

        CEEnchantConfig enchantConfig = new CEEnchantConfig();
        enchantConfig.loadConfig(getPlugin().getEnchantFolder());

        CEItemConfig ceItemConfig = new CEItemConfig();
        ceItemConfig.loadConfig(getPlugin().getItemFile());

        VanillaItemConfig vanillaItemConfig = new VanillaItemConfig();
        vanillaItemConfig.loadConfig();

        AdvancedFileConfiguration bookCraftConfig = new AdvancedFileConfiguration(getPlugin().getBookCraftFile());
        getPlugin().setBookCraftConfig(bookCraftConfig.get(BookCraftConfig.class));

        BookUpgradeConfig bookUpgradeConfig = new BookUpgradeConfig();
        bookUpgradeConfig.loadConfig(getPlugin().getBookUpgradeFile());
        bookUpgradeConfig.loadConfig(getPlugin().getBookUpgradeFolder());

        TinkererConfig tinkererConfig = new TinkererConfig();
        tinkererConfig.loadConfig(getPlugin().getTinkererFile());
    }
}
