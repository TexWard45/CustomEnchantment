package com.bafmc.customenchantment.database;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.bukkit.utils.FileUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentDebug;
import lombok.Getter;

@Getter
public class DatabaseModule extends PluginModule<CustomEnchantment> {
    private Database database;

    public DatabaseModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        FileUtils.createFile(getPlugin().getDatabaseFile());

        this.database = new Database(getPlugin().getDatabaseFile());
        this.database.connect();
        this.database.init();

        CustomEnchantmentDebug.log("Success connect and init database!");
    }

    public void onDisable() {
        this.database.disconnect();
    }
}
