package com.bafmc.customenchantment.item.sigil;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.feature.placeholder.Placeholder;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.utils.SparseMap;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.CENBT;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.artifact.group.CEArtifactGroup;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CESigil extends CEWeaponAbstract<CESigilData> {
    public CESigil(ItemStack itemStack) {
        super(CEItemType.SIGIL, itemStack);
    }

    public void importFrom(ItemStack itemStack) {
        super.importFrom(itemStack);

        CECraftItemStackNMS itemStackNMS = getCraftItemStack();
        NMSNBTTagCompound tag = itemStackNMS.getCECompound();

        String pattern = tag.getString(CENBT.PATTERN);
        int level = Math.max(tag.getInt(CENBT.LEVEL), 1);
        String id = tag.getString(CENBT.ID);

        CESigil item = (CESigil) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.SIGIL).get(pattern);

        if (item != null) {
            CESigilData data = item.getData().clone();
            data.setLevel(level);
            data.setId(id);
            setData(data);
        }
    }

    public ItemStack exportTo() {
        return exportTo(getData());
    }

    public ItemStack exportTo(CESigilData data) {
        getWeaponEnchant().forceAddCESimple(new CEEnchantSimple(getData().getConfigData().getEnchant(), data.getLevel()));

        ItemStack itemStack = super.exportTo().clone();

        CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(itemStack);
        NMSNBTTagCompound tag = itemStackNMS.getCECompound();

        tag.setString(CENBT.TYPE, getType());
        tag.setString(CENBT.PATTERN, data.getPattern());
        tag.setInt(CENBT.LEVEL, data.getLevel());
        if (data.getId() == null || data.getId().isEmpty()) {
            tag.setString(CENBT.ID, UUID.randomUUID().toString());
        }else {
            tag.setString(CENBT.ID, data.getId());
        }

        if (!tag.isEmpty()) {
            itemStackNMS.setCETag(tag);
        }

        Placeholder placeholder = Placeholder.of(getPlaceholder(data));

        itemStack = itemStackNMS.getNewItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        // Fix duplicate display name
        if (getData().getConfigData().getItemDisplay() != null) {
            itemMeta.setDisplayName(placeholder.apply(getData().getConfigData().getItemDisplay()));
        }

        itemStack.setItemMeta(itemMeta);
        itemStack = ItemStackUtils.setItemStack(itemStack, placeholder);
        itemStack = ItemStackUtils.updateColorToItemStack(itemStack);
        return itemStack;
    }

    public Map<String, String> getPlaceholder(CESigilData data) {
        Map<String, String> map = new HashMap<>();

        if (data.getLevel() <= 0) {
            return map;
        }

        CESigilSettings settings = CESigilSettings.getSettings();
        map.put("{level}", String.valueOf(data.getLevel()));

        SparseMap<String> levelColors = settings.getLevelColors();
        map.put("{level_color}", levelColors.containsKey(data.getLevel()) ? levelColors.get(data.getLevel()) : "");
        // Fix auto replace bold color
        map.put("{level_color_bold}", levelColors.containsKey(data.getLevel()) ? levelColors.get(data.getLevel()) + "&l" : "");
        return map;
    }


    public String getWeaponSettingsName() {
        return "sigil-" + super.getWeaponSettingsName();
    }
}
