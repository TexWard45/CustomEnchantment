package com.bafmc.customenchantment.menu;

import com.bafmc.bukkit.bafframework.custommenu.menu.MenuRegister;
import com.bafmc.bukkit.bafframework.custommenu.menu.plugin.AbstractMenuPlugin;
import com.bafmc.bukkit.bafframework.custommenu.menu.plugin.MenuPluginRegister;
import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftCustomMenu;
import com.bafmc.customenchantment.menu.ceanvil.CEAnvilCustomMenu;
import com.bafmc.customenchantment.menu.ceanvil.handler.BookHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.DefaultHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.EnchantPointHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.GemHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.GemDrillHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.ProtectDeadHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.ProtectDestroyHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.LoreFormatHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.EraseEnchantHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.RemoveEnchantHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.RemoveEnchantPointHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.RemoveGemHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.RemoveProtectDeadHandler;
import com.bafmc.customenchantment.menu.artifactupgrade.ArtifactUpgradeCustomMenu;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeCustomMenu;
import com.bafmc.customenchantment.menu.equipment.EquipmentCustomMenu;
import com.bafmc.customenchantment.menu.tinkerer.TinkererCustomMenu;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Map;

public class MenuModule extends PluginModule<CustomEnchantment> {
    public MenuModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        registerFolder();
        registerMenu();
        onReload();
    }

    /**
     * Register plugin folder with MenuPluginRegister using custom menu-new folder
     */
    private void registerFolder() {
        AbstractMenuPlugin menuPlugin = new AbstractMenuPlugin<CustomEnchantment>(getPlugin()) {
            @Override
            public File getMenuFolder() {
                return new File(getPlugin().getDataFolder(), "menu-new");
            }
        };

        MenuPluginRegister.instance().registerMenuPlugin(menuPlugin);
    }

    /**
     * Register menu classes with MenuRegister
     */
    private void registerMenu() {
        try {
            MenuRegister.instance().registerStrategy(TinkererCustomMenu.class);
            MenuRegister.instance().registerStrategy(BookCraftCustomMenu.class);
            MenuRegister.instance().registerStrategy(CEAnvilCustomMenu.class);
            registerCEAnvilHandlers();
            MenuRegister.instance().registerStrategy(BookUpgradeCustomMenu.class);
            MenuRegister.instance().registerStrategy(ArtifactUpgradeCustomMenu.class);
            MenuRegister.instance().registerStrategy(EquipmentCustomMenu.class);
        } catch (Exception e) {
            getPlugin().getLogger().severe("[MenuModule] Failed to register menus: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void registerCEAnvilHandlers() {
        CEAnvilCustomMenu.registerHandler(CEItemType.BOOK, BookHandler::new);
        CEAnvilCustomMenu.registerHandler(CEItemType.ENCHANT_POINT, EnchantPointHandler::new);
        CEAnvilCustomMenu.registerHandler(CEItemType.GEM, GemHandler::new);
        CEAnvilCustomMenu.registerHandler(CEItemType.GEM_DRILL, GemDrillHandler::new);
        CEAnvilCustomMenu.registerHandler(CEItemType.PROTECT_DEAD, ProtectDeadHandler::new);
        CEAnvilCustomMenu.registerHandler(CEItemType.PROTECT_DESTROY, ProtectDestroyHandler::new);
        CEAnvilCustomMenu.registerHandler(CEItemType.LORE_FORMAT, LoreFormatHandler::new);
        CEAnvilCustomMenu.registerHandler(CEItemType.EARSE_ENCHANT, EraseEnchantHandler::new);
        CEAnvilCustomMenu.registerHandler(CEItemType.REMOVE_ENCHANT, RemoveEnchantHandler::new);
        CEAnvilCustomMenu.registerHandler(CEItemType.REMOVE_ENCHANT_POINT, RemoveEnchantPointHandler::new);
        CEAnvilCustomMenu.registerHandler(CEItemType.REMOVE_GEM, RemoveGemHandler::new);
        CEAnvilCustomMenu.registerHandler(CEItemType.REMOVE_PROTECT_DEAD, RemoveProtectDeadHandler::new);
    }

    @Override
    public void onReload() {
        Map<Plugin, AbstractMenuPlugin> menuMap = MenuPluginRegister.instance().getMap();
        AbstractMenuPlugin menuPlugin = menuMap.get(getPlugin());

        if (menuPlugin != null) {
            menuPlugin.clearMenu();
            menuPlugin.registerMenu(menuPlugin.getMenuFolder());
        } else {
            getPlugin().getLogger().warning("[MenuModule] MenuPlugin not found in register!");
        }
    }
}
