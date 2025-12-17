package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.task.PlayerPerTickTask;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.outfit.CEOutfit;
import com.bafmc.customenchantment.item.outfit.CEOutfitData;
import com.bafmc.customenchantment.item.skin.CESkin;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class OutfitItemTask extends PlayerPerTickTask {
    public int getPlayerPerTick() {
        return 5;
    }

    public void run(Player player) {
        PlayerInventory inventory = player.getInventory();
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);

        EquipSlot outfitExtraSlot = CustomEnchantment.instance().getMainConfig().getOutfitExtraSlot();

        CEWeaponAbstract ceOutfitAbstract = cePlayer.getEquipment().getSlot(outfitExtraSlot);
        CEOutfit ceOutfit = (CEOutfit) ceOutfitAbstract;
        boolean existsOutfit = ceOutfit != null;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null) {
                continue;
            }

            CEItem ceItem = CEAPI.getCEItem(itemStack);
            if (!(ceItem instanceof CEWeaponAbstract ceWeaponAbstract)) {
                continue;
            }

            if (existsOutfit) {
                if (ceWeaponAbstract instanceof CEOutfit) {
                    continue;
                }

                if (ceWeaponAbstract instanceof CESkin) {
                    continue;
                }

                String customType = ceWeaponAbstract.getCustomType();
                if (customType == null || customType.isEmpty()) {
                    continue;
                }

                CEOutfitData.ConfigByLevelData level = ceOutfit.getData().getConfigByLevelData();
                String skin = level.getSkinByCustomType(customType);
                if (skin == null || skin.isEmpty()) {
                    continue;
                }
                List<String> list = new ArrayList<>();
                list.add(skin);

                Parameter parameter = new Parameter(list);
                ItemStack skinItemStack = (CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.SKIN)).getItemStacksByParameter(parameter).get(0);
                if (skinItemStack == null) {
                    continue;
                }
                CESkin ceSkin = (CESkin) CEAPI.getCEItem(skinItemStack);
                ApplyReason applyReason = ceSkin.applyTo(ceWeaponAbstract);
                CEItem source = applyReason.getSource();
                if (source != null) {
                    inventory.setItem(i, source.exportTo());
                }
                System.out.println("Applied skin " + skin + " to item in slot " + i + " for player " + player.getName());
            }else if (ceWeaponAbstract instanceof CESkin skin) {
                ItemStack weaponItemStack = skin.getUnifyWeapon().getItemStack(CEUnifyWeapon.Target.WEAPON);
                inventory.setItem(i, weaponItemStack);
                System.out.println("Removed skin from item in slot " + i + " for player " + player.getName());
            }
        }
    }
}
