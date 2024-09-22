package com.bafmc.customenchantment.utils;

import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class McMMOUtils {
    public static List<ItemStack> getMcMMOBonusDrop(Block block, List<ItemStack> drops) {
        List<ItemStack> sellDrops = new ArrayList<>(drops);

        if (block.hasMetadata("mcMMO: Double Drops")) {
            sellDrops = new ArrayList<>();

            BonusDropMeta bonusDropMeta = (BonusDropMeta) block.getMetadata("mcMMO: Double Drops").get(0);
            int bonusCount = bonusDropMeta.asInt();

            if (drops.isEmpty()) {
                return drops;
            }

            if (drops.get(0).getType() == Material.SUGAR_CANE) {
                List<ItemStack> newDrops = new ArrayList<>();
                newDrops.add(new ItemStack(Material.SUGAR_CANE, bonusCount));
                return newDrops;
            }

            for (ItemStack drop : drops) {
                ItemStack clone = drop.clone();
                clone.setAmount(clone.getAmount() * (bonusCount + 1));

                sellDrops.add(clone);
            }
        }

        return sellDrops;
    }
}
