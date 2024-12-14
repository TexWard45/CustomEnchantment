package com.bafmc.customenchantment.item.artifact;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.CENBT;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import org.bukkit.inventory.ItemStack;

public class CEArtifact extends CEWeaponAbstract<CEArtifactData> {
    public CEArtifact(ItemStack itemStack) {
        super(CEItemType.ARTIFACT, itemStack);
    }

    public void importFrom(ItemStack itemStack) {
        super.importFrom(itemStack);

        CECraftItemStackNMS itemStackNMS = getCraftItemStack();
        NMSNBTTagCompound tag = itemStackNMS.getCECompound();

        String pattern = tag.getString(CENBT.PATTERN);

        CEArtifact item = (CEArtifact) CustomEnchantment.instance().getCeItemStorageMap().get(type).get(pattern);

        if (item != null) {
            setData(item.getData());
        }
    }

    public ItemStack exportTo() {
        ItemStack itemStack = super.exportTo();

        CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(itemStack);
        NMSNBTTagCompound tag = itemStackNMS.getCECompound();

        if (data != null) {
            tag.setString(CENBT.PATTERN, data.getPattern());
        }

        itemStackNMS.setCETag(tag);
        return itemStackNMS.getNewItemStack();
    }

    public String getWeaponSettingsName() {
        return "artifact-" + super.getWeaponSettingsName();
    }
}
