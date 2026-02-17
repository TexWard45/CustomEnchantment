package com.bafmc.customenchantment.menu;

import com.bafmc.bukkit.bafframework.custommenu.menu.MenuRegister;
import com.bafmc.bukkit.bafframework.custommenu.menu.plugin.AbstractMenuPlugin;
import com.bafmc.bukkit.bafframework.custommenu.menu.plugin.MenuPluginRegister;
import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.listener.CMenuListener;
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
import com.bafmc.customenchantment.menu.tinkerer.TinkererCustomMenu;
import com.bafmc.customenchantment.menu.anvil.*;
import com.bafmc.customenchantment.menu.artifactupgrade.ArtifactUpgradeMenuListener;
import com.bafmc.customenchantment.menu.bookcraft.BookCraftMenuListener;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeMenuListener;
import com.bafmc.customenchantment.menu.equipment.EquipmentMenuListener;
import com.bafmc.customenchantment.menu.tinkerer.TinkererMenuListener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Map;

public class MenuModule extends PluginModule<CustomEnchantment> {
    public MenuModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        getPlugin().getLogger().info("[MenuModule] onEnable() called");

        // Register plugin folder with MenuPluginRegister
        registerFolder();

        // Register new CustomMenu BafFramework menus
        registerMenu();

        // Call onReload to load menus from folder
        onReload();

        // Legacy CE Anvil view registration
        CEAnvilMenu.registerView1(CEItemType.WEAPON, Slot1CEWeaponView.class);
        CEAnvilMenu.registerView2(CEItemType.DEFAULT, Slot2CEDefaultView.class);
        CEAnvilMenu.registerView2(CEItemType.REMOVE_ENCHANT, Slot2CERemoveEnchantView.class);
        CEAnvilMenu.registerView2(CEItemType.ENCHANT_POINT, Slot2CEEnchantPointView.class);
        CEAnvilMenu.registerView2(CEItemType.BOOK, Slot2CEBookView.class);
        CEAnvilMenu.registerView2(CEItemType.PROTECT_DEAD, Slot2CEProtectDeadView.class);
        CEAnvilMenu.registerView2(CEItemType.REMOVE_PROTECT_DEAD, Slot2CERemoveProtectDeadView.class);
        CEAnvilMenu.registerView2(CEItemType.LORE_FORMAT, Slot2CELoreFormatView.class);
        CEAnvilMenu.registerView2(CEItemType.REMOVE_ENCHANT_POINT, Slot2CERemoveEnchantPointView.class);
        CEAnvilMenu.registerView2(CEItemType.REMOVE_GEM, Slot2CERemoveGemView.class);
        CEAnvilMenu.registerView2(CEItemType.PROTECT_DESTROY, Slot2CEProtectDestroyView.class);
        CEAnvilMenu.registerView2(CEItemType.EARSE_ENCHANT, Slot2CEEraseEnchantView.class);
        CEAnvilMenu.registerView2(CEItemType.GEM, Slot2CEGemView.class);
        CEAnvilMenu.registerView2(CEItemType.GEM_DRILL, Slot2CEGemDrillView.class);

        CMenuListener.registerMenuListener(new BookCraftMenuListener());
        CMenuListener.registerMenuListener(new TinkererMenuListener());
        CMenuListener.registerMenuListener(new CEAnvilMenuListener());
        CMenuListener.registerMenuListener(new BookUpgradeMenuListener());
        CMenuListener.registerMenuListener(new EquipmentMenuListener());
        CMenuListener.registerMenuListener(new ArtifactUpgradeMenuListener());
    }

    /**
     * Register plugin with MenuPluginRegister using custom menu-new folder
     */
    private void registerFolder() {
        getPlugin().getLogger().info("[MenuModule] Registering plugin with MenuPluginRegister (using menu-new folder)...");

        // Create custom menu plugin that uses menu-new folder instead of menu
        AbstractMenuPlugin menuPlugin = new AbstractMenuPlugin<CustomEnchantment>(getPlugin()) {
            @Override
            public File getMenuFolder() {
                return new File(getPlugin().getDataFolder(), "menu-new");
            }
        };

        MenuPluginRegister.instance().registerMenuPlugin(menuPlugin);
        getPlugin().getLogger().info("[MenuModule] Plugin registered with MenuPluginRegister (folder: menu-new)");
    }

    /**
     * Register menu classes with MenuRegister
     */
    private void registerMenu() {
        try {
            getPlugin().getLogger().info("[MenuModule] Registering TinkererCustomMenu with MenuRegister...");
            MenuRegister.instance().registerStrategy(TinkererCustomMenu.class);
            getPlugin().getLogger().info("[MenuModule] TinkererCustomMenu registered successfully");

            getPlugin().getLogger().info("[MenuModule] Registering BookCraftCustomMenu with MenuRegister...");
            MenuRegister.instance().registerStrategy(BookCraftCustomMenu.class);
            getPlugin().getLogger().info("[MenuModule] BookCraftCustomMenu registered successfully");

            getPlugin().getLogger().info("[MenuModule] Registering CEAnvilCustomMenu with MenuRegister...");
            MenuRegister.instance().registerStrategy(CEAnvilCustomMenu.class);
            registerCEAnvilHandlers();
            getPlugin().getLogger().info("[MenuModule] CEAnvilCustomMenu registered successfully");
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
        getPlugin().getLogger().info("[MenuModule] onReload() called");

        // Get menu plugin from register
        Map<Plugin, AbstractMenuPlugin> menuMap = MenuPluginRegister.instance().getMap();
        AbstractMenuPlugin menuPlugin = menuMap.get(getPlugin());

        if (menuPlugin != null) {
            getPlugin().getLogger().info("[MenuModule] Reloading menus from folder: " + menuPlugin.getMenuFolder());
            menuPlugin.clearMenu();
            menuPlugin.registerMenu(menuPlugin.getMenuFolder());
            getPlugin().getLogger().info("[MenuModule] Menus reloaded successfully. Loaded menus: " + menuPlugin.getMenuNameList());
        } else {
            getPlugin().getLogger().warning("[MenuModule] MenuPlugin not found in register!");
        }
    }
}
