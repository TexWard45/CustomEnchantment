package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.bukkit.utils.FileUtils;
import com.bafmc.customenchantment.*;
import com.bafmc.customenchantment.item.artifact.CEArtifactGroupMap;
import com.bafmc.customenchantment.item.outfit.CEOutfitGroupMap;
import com.bafmc.customenchantment.item.randombook.CERandomBookPlayerFilter;
import com.bafmc.customenchantment.item.sigil.CESigilGroupMap;

import java.util.ArrayList;

public class ConfigModule extends PluginModule<CustomEnchantment> {
    public ConfigModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        onReload();
    }

    public void onReload() {
        CustomEnchantment.instance().setInReload(true);

        FileUtils.createFolder(getPlugin().getArtifactFolder());
        FileUtils.createFolder(getPlugin().getBookUpgradeFolder());
        FileUtils.createFolder(getPlugin().getStorageItemFolder());
        FileUtils.createFolder(getPlugin().getEnchantFolder());
        FileUtils.createFolder(getPlugin().getMenuFolder());
        FileUtils.createFolder(getPlugin().getGeneralDataFolder());
        FileUtils.createFolder(getPlugin().getPlayerDataFolder());
        FileUtils.createFolder(getPlugin().getWeaponFolder());
        FileUtils.createFolder(getPlugin().getSigilGroupFile());
        FileUtils.createFolder(getPlugin().getOutfitGroupFile());
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
        getPlugin().setCeSigilGroupMap(new CESigilGroupMap());
        getPlugin().setCeOutfitGroupMap(new CEOutfitGroupMap());
        getPlugin().setCeItemStorageMap(new CEItemStorageMap());

        CERandomBookPlayerFilter.setFilterCEList(new ArrayList<>());

        CustomEnchantmentMessage.setConfig(new AdvancedFileConfiguration(getPlugin().getMessagesFile()));

        getPlugin().saveDefaultConfig();
        getPlugin().reloadConfig();

        AdvancedFileConfiguration mainConfig = new AdvancedFileConfiguration(getPlugin().getConfigFile());
        getPlugin().setMainConfig(mainConfig.get(MainConfig.class));

        CEEnchantGroupConfig groupConfig = new CEEnchantGroupConfig();
        groupConfig.loadConfig(getPlugin().getGroupFile());

        CEArtifactGroupConfig ceArtifactGroupConfig = new CEArtifactGroupConfig();
        ceArtifactGroupConfig.loadConfig(getPlugin().getArtifactGroupFile());

        CESigilGroupConfig ceSigilGroupConfig = new CESigilGroupConfig();
        ceSigilGroupConfig.loadConfig(getPlugin().getSigilGroupFile());

        CEOutfitGroupConfig ceOutfitGroupConfig = new CEOutfitGroupConfig();
        ceOutfitGroupConfig.loadConfig(getPlugin().getOutfitGroupFile());

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

        CustomEnchantment.instance().setInReload(false);
    }
}
