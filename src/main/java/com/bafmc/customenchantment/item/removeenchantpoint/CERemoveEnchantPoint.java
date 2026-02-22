package com.bafmc.customenchantment.item.removeenchantpoint;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.enchantpoint.CEEnchantPoint;
import com.bafmc.customenchantment.item.enchantpoint.CEEnchantPointSimple;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CERemoveEnchantPoint extends CEItem<CERemoveEnchantPointData> {

	public CERemoveEnchantPoint(ItemStack itemStack) {
		super(CEItemType.REMOVE_ENCHANT_POINT, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CERemoveEnchantPoint item = (CERemoveEnchantPoint) CustomEnchantment.instance().getCeItemStorageMap()
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

	public ApplyReason applyByMenuTo(CEItem ceItem, CEEnchantPointSimple ceEnchantPointSimple) {
		if (!(ceItem instanceof CEWeapon) || ceEnchantPointSimple == null) {
			return ApplyReason.NOTHING;
		}

        CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
        WeaponData data = ceWeapon.getWeaponData();
        int point = data.getExtraEnchantPoint();
        if (point <= 0) {
            return new ApplyReason("empty", ApplyResult.CANCEL);
        }

        int maxEnchantSlot = ceWeapon.getWeaponSettings().getEnchantPoint() + ceWeapon.getWeaponData().getExtraEnchantPoint();
        if (ceWeapon.getWeaponEnchant().getTotalEnchantPoint() > maxEnchantSlot - 1) {
            return new ApplyReason("remove-book-first", ApplyResult.CANCEL);
        }

		ceWeapon.updateTimeModifier();
		data.setExtraEnchantPoint(point - 1);
        data.removeEnchantPointIndex(ceEnchantPointSimple.getIndex());

		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setWriteLogs(true);
		reason.setSource(ceWeapon);
		
		CEEnchantPoint enchantPoint = (CEEnchantPoint) CustomEnchantment.instance().getCeItemStorageMap()
				.get(CEItemType.ENCHANT_POINT).get(ceEnchantPointSimple.getPattern());
		
		if (enchantPoint != null) {
			reason.setRewards(Arrays.asList(enchantPoint.exportTo()));
		}
		
		reason.putData(CEConstants.DataKey.PATTERN, this.data.getPattern());
		reason.putData(CEConstants.DataKey.WEAPON, ItemStackUtils.toString(ceWeapon.getDefaultItemStack()));
		return reason;
	}

    public List<CEEnchantPointSimple> getList(List<String> extraEnchantPointList) {
        List<CEEnchantPointSimple> list = new ArrayList<>();

        int index = 0;
        for (String pattern : extraEnchantPointList) {
            list.add(new CEEnchantPointSimple(pattern, index));
            index++;
        }

        return list;
    }
}
