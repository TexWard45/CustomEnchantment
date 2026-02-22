package com.bafmc.customenchantment.item.enchantpoint;

import java.util.HashMap;
import java.util.Map;

import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.customenchantment.item.*;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;

public class CEEnchantPoint extends CEItem<CEEnchantPointData> {

	public CEEnchantPoint(ItemStack itemStack) {
		super(CEItemType.ENCHANT_POINT, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CEEnchantPoint item = (CEEnchantPoint) CustomEnchantment.instance().getCeItemStorageMap()
				.get(CEItemType.ENCHANT_POINT).get(pattern);

		if (item != null) {
			setData(item.getData());
		}
	}

	@Override
	public ItemStack exportTo(CEEnchantPointData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		return getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
	}

	public Map<String, String> getPlaceholder(CEEnchantPointData data) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}

	public ApplyReason applyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}

		if (!getData().getApplies().contains(new MaterialData(ceItem.getDefaultItemStack()))) {
			return new ApplyReason("not-support-type", ApplyResult.CANCEL);
		}

		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
		WeaponData data = ceWeapon.getWeaponData();

		int extraPoint = getData().getExtraPoint();
		int newPoint = data.getTotalEnchantPoint() + extraPoint;
		if (newPoint > getData().getMaxPoint()) {
			return new ApplyReason("max-enchant-point", ApplyResult.CANCEL);
		}
		
		data.setExtraEnchantPoint(data.getExtraEnchantPoint() + extraPoint);
		data.getExtraEnchantPointList().add(getData().getPattern());
		
		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setWriteLogs(true);
		reason.putData(CEConstants.DataKey.PATTERN, this.data.getPattern());
		reason.putData(CEConstants.DataKey.WEAPON, ItemStackUtils.toString(ceWeapon.getDefaultItemStack()));
		return reason;
	}
	
	public ApplyReason testApplyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}
		
		ceItem = CEWeapon.getCEWeapon(ceItem.getDefaultItemStack());

		if (!getData().getApplies().contains(new MaterialData(ceItem.getDefaultItemStack()))) {
			return new ApplyReason("not-support-type", ApplyResult.CANCEL);
		}

		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
		WeaponData data = ceWeapon.getWeaponData();

		int extraPoint = getData().getExtraPoint();
		int newPoint = data.getTotalEnchantPoint() + extraPoint;
		if (newPoint > getData().getMaxPoint()) {
			return new ApplyReason("max-enchant-point", ApplyResult.CANCEL);
		}
		
		data.setExtraEnchantPoint(data.getExtraEnchantPoint() + extraPoint);
		data.getExtraEnchantPointList().add(getData().getPattern());
		
		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setSource(ceItem);
		return reason;
	}
}
