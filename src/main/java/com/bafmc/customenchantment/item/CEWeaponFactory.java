package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.api.MaterialList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CEWeaponFactory extends CEItemFactory<CEWeapon> {
    private static MaterialList materialWhitelist = new MaterialList();

    public static void setWhitelist(List<MaterialData> list) {
        if (list == null) {
            return;
        }
        CEWeaponFactory.materialWhitelist = new MaterialList(list);
    }

    public CEWeapon create(ItemStack itemStack) {
        return new CEWeapon(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.WEAPON);
    }

    public boolean isMatchType(ItemStack itemStack) {
        return materialWhitelist.contains(new MaterialData(itemStack));
    }

    public boolean isAutoGenerateNewItem() {
        return true;
    }
}
