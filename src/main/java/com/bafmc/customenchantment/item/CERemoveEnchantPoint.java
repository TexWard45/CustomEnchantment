package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CERemoveEnchantPoint extends CEItem<CERemoveEnchantPointData> {

	public CERemoveEnchantPoint(ItemStack itemStack) {
		super(CEItemType.REMOVE_ENCHANT_POINT, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CERemoveEnchantPoint item = (CERemoveEnchantPoint) CustomEnchantment.instance().getCEItemStorageMap()
				.get(CEItemType.REMOVE_ENCHANT_POINT).get(pattern);

		if (item != null) {
			setData(item.getData());
		}
	}

	public ItemStack exportTo(CERemoveEnchantPointData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		return getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
	}

	public Map<String, String> getPlaceholder(CERemoveEnchantPointData data) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}

	public ApplyReason applyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}

        CERemoveEnchantPointData.Data removeEnchantPointData = findSuitableData((CEWeapon) ceItem);
        if (removeEnchantPointData == null) {
            return new ApplyReason("empty", ApplyResult.CANCEL);
        }

        CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
        WeaponData data = ceWeapon.getWeaponData();
        int point = data.getExtraEnchantPoint();
        if (point <= 0) {
            return new ApplyReason("empty", ApplyResult.CANCEL);
        }

        int maxEnchantSlot = ceWeapon.getWeaponSettings().getEnchantPoint() + ceWeapon.getWeaponData().getExtraEnchantPoint();
        if (ceWeapon.getWeaponEnchant().getTotalEnchantPoint() >= maxEnchantSlot) {
            return new ApplyReason("remove-book-first", ApplyResult.CANCEL);
        }

		ceWeapon.updateTimeModifier();
		data.setExtraEnchantPoint(point - 1);

		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setWriteLogs(true);
		reason.setSource(ceWeapon);
		
		CEEnchantPoint protectDead = (CEEnchantPoint) CustomEnchantment.instance().getCEItemStorageMap()
				.get(CEItemType.ENCHANT_POINT).get(removeEnchantPointData.getEnchantPointType());
		
		if (protectDead != null) {
			reason.setRewards(Arrays.asList(protectDead.exportTo()));
		}
		
		reason.putData("pattern", this.data.getPattern());
		reason.putData("weapon", ItemStackUtils.toString(ceWeapon.getDefaultItemStack()));
		return reason;
	}

    public CERemoveEnchantPointData.Data findSuitableData(CEWeapon ceWeapon) {
        ItemStack itemStack = ceWeapon.getDefaultItemStack();

        for (CERemoveEnchantPointData.Data data : this.data.getDataList()) {
            if (ceWeapon.getWeaponData().getExtraEnchantPoint() < data.getExtraPointRequired()) {
                continue;
            }

            if (data.getAppliesMaterialList().contains(new MaterialData(itemStack))) {
                return data;
            }
        }

        return null;
    }

    public ItemStack getEnchantPointItem(CEItem ceItem) {
        CERemoveEnchantPointData.Data removeEnchantPointData = findSuitableData((CEWeapon) ceItem);

        CEEnchantPoint protectDead = (CEEnchantPoint) CustomEnchantment.instance().getCEItemStorageMap()
                .get(CEItemType.ENCHANT_POINT).get(removeEnchantPointData.getEnchantPointType());

        if (protectDead != null) {
            return protectDead.exportTo();
        }
        return new ItemStack(Material.AIR);
    }

    public ApplyReason testApplyByMenuTo(CEItem ceItem) {
        if (!(ceItem instanceof CEWeapon)) {
            return ApplyReason.NOTHING;
        }

        ceItem = CEWeapon.getCEWeapon(ceItem.getDefaultItemStack());

        CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
        CERemoveEnchantPointData.Data removeEnchantPointData = findSuitableData((CEWeapon) ceItem);
        if (removeEnchantPointData == null) {
            return new ApplyReason("empty", ApplyResult.CANCEL);
        }

        WeaponData data = ceWeapon.getWeaponData();
        int point = data.getExtraEnchantPoint();
        if (point <= 0) {
            return new ApplyReason("empty", ApplyResult.CANCEL);
        }

        int maxEnchantSlot = ceWeapon.getWeaponSettings().getEnchantPoint() + ceWeapon.getWeaponData().getExtraEnchantPoint();
        if (ceWeapon.getWeaponEnchant().getTotalEnchantPoint() >= maxEnchantSlot) {
            return new ApplyReason("remove-book-first", ApplyResult.CANCEL);
        }

        ceWeapon.updateTimeModifier();
        data.setExtraEnchantPoint(point - 1);

        ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
        reason.setSource(ceWeapon);
        return reason;
    }

}
