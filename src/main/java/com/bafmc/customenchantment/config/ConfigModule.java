package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.bukkit.utils.FileUtils;
import com.bafmc.customenchantment.*;
import com.bafmc.customenchantment.item.mask.group.CEArtifactGroupMap;

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
        FileUtils.createFile(getPlugin().getArtifactUpgradeFile());

        getPlugin().setCeEnchantMap(new CEEnchantMap());
        getPlugin().setCeGroupMap(new CEGroupMap());
        getPlugin().setCeArtifactGroupMap(new CEArtifactGroupMap());
        getPlugin().setCeItemStorageMap(new CEItemStorageMap());

        CustomEnchantmentMessage.setConfig(new AdvancedFileConfiguration(getPlugin().getMessagesFile()));

        getPlugin().saveDefaultConfig();
        getPlugin().reloadConfig();

        AdvancedFileConfiguration mainConfig = new AdvancedFileConfiguration(getPlugin().getConfigFile());
        getPlugin().setMainConfig(mainConfig.get(MainConfig.class));

        CEEnchantGroupConfig groupConfig = new CEEnchantGroupConfig();
        groupConfig.loadConfig(getPlugin().getGroupFile());

        CEArtifactGroupConfig maskConfig = new CEArtifactGroupConfig();
        maskConfig.loadConfig(getPlugin().getArtifactGroupFile());

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

        ArtifactUpgradeConfig artifactUpgradeConfig = new ArtifactUpgradeConfig();
        artifactUpgradeConfig.loadConfig(getPlugin().getArtifactUpgradeFile());
    }
}
