package com.bafmc.customenchantment.item.artifact;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
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
        int level = Math.max(tag.getInt(CENBT.LEVEL), 1);

        CEArtifact item = (CEArtifact) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.ARTIFACT).get(pattern);

        if (item != null) {
            CEArtifactData data = item.getData().clone();
            data.setLevel(level);
            setData(data);
        }
    }

    public ItemStack exportTo() {
        return exportTo(getData());
    }

    public ItemStack exportTo(CEArtifactData data) {
        getWeaponEnchant().forceAddCESimple(new CEEnchantSimple(getData().getConfigData().getEnchant(), data.getLevel()));

        ItemStack itemStack = super.exportTo();

        CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(itemStack);
        NMSNBTTagCompound tag = itemStackNMS.getCECompound();

        tag.setString(CENBT.TYPE, getType());
        tag.setString(CENBT.PATTERN, data.getPattern());
        tag.setInt(CENBT.LEVEL, data.getLevel());

        if (!tag.isEmpty()) {
            itemStackNMS.setCETag(tag);
        }

        itemStack = itemStackNMS.getNewItemStack();
        return ItemStackUtils.updateColorToItemStack(itemStack);
    }

    public String getWeaponSettingsName() {
        return "artifact-" + super.getWeaponSettingsName();
    }
}
