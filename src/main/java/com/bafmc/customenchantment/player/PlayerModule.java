package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.player.mining.*;

public class PlayerModule extends PluginModule<CustomEnchantment> {
    public PlayerModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        setupPlayerExpansions();
        setupPlayerSpecialMining();
    }

    public void setupPlayerExpansions() {
        CEPlayerExpansionRegister.register(PlayerStorage.class);
        CEPlayerExpansionRegister.register(PlayerEquipment.class);
        CEPlayerExpansionRegister.register(PlayerTemporaryStorage.class);
        CEPlayerExpansionRegister.register(PlayerVanillaAttribute.class);
        CEPlayerExpansionRegister.register(PlayerCustomAttribute.class);
        CEPlayerExpansionRegister.register(PlayerPotion.class);
        CEPlayerExpansionRegister.register(PlayerSet.class);
        CEPlayerExpansionRegister.register(PlayerCECooldown.class);
        CEPlayerExpansionRegister.register(PlayerCEManager.class);
        CEPlayerExpansionRegister.register(PlayerAbility.class);
        CEPlayerExpansionRegister.register(PlayerExtraSlot.class);
        CEPlayerExpansionRegister.register(PlayerMobBonus.class);
        CEPlayerExpansionRegister.register(PlayerBlockBonus.class);
        CEPlayerExpansionRegister.register(PlayerSpecialMining.class);
        CEPlayerExpansionRegister.register(PlayerNameTag.class);
        CEPlayerExpansionRegister.register(PlayerGem.class);
    }

    public void setupPlayerSpecialMining() {
        PlayerSpecialMiningRegister.register(BlockDropBonusSpecialMine.class);
        PlayerSpecialMiningRegister.register(ExplosionSpecialMine.class);
        PlayerSpecialMiningRegister.register(VeinSpecialMine.class);
        PlayerSpecialMiningRegister.register(FurnaceSpecialMine.class);
        PlayerSpecialMiningRegister.register(TelepathySpecialMine.class);
        PlayerSpecialMiningRegister.register(AutoSellSpecialMine.class);
    }
}
