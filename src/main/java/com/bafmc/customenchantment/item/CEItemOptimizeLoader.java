package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import org.bukkit.inventory.ItemStack;

/**
 * CEItemOptimizeLoader - Optimized loader for Custom Enchantment items, specifically for weapon items.
 */
public class CEItemOptimizeLoader {
    private CECraftItemStackNMS craftItemStack;
    private ItemStack itemStack;

    public CEItemOptimizeLoader(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.craftItemStack = new CECraftItemStackNMS(itemStack);
    }

    public boolean isCESkin() {
        return CEItemType.SKIN.equals(craftItemStack.getCECompound().getString(CENBT.TYPE));
    }

    public boolean isOutfit() {
        return CEItemType.OUTFIT.equals(craftItemStack.getCECompound().getString(CENBT.TYPE));
    }

    public boolean isWeapon() {
        return CEItemType.WEAPON.equals(craftItemStack.getCECompound().getString(CENBT.TYPE));
    }

    public String getPattern() {
        return craftItemStack.getCECompound().getString(CENBT.PATTERN);
    }

    public ItemStack getWeaponItemStack() {
        NMSNBTTagCompound compound = craftItemStack.getCECompound();
        if (compound.hasKey("unify-data")) {
            NMSNBTTagCompound unifyData = compound.getCompound("unify-data");
            if (unifyData.hasKey("weapon-item")) {
                NMSNBTTagCompound weaponTag = unifyData.getCompound("weapon-item");
                if (weaponTag.hasKey("item-stack")) {
                    return ItemStackUtils.fromString(weaponTag.getString("item-stack"));
                }
            }
        }
        return null;
    }
}
