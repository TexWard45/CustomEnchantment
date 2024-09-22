package com.bafmc.customenchantment.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;

public class CEProtectDestroy extends CEItem<CEProtectDestroyData> {

	public CEProtectDestroy(ItemStack itemStack) {
		super(CEItemType.PROTECT_DESTROY, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CEProtectDestroy item = (CEProtectDestroy) CustomEnchantment.instance().getCEItemStorageMap()
				.get(CEItemType.PROTECT_DESTROY).get(pattern);

		if (item != null) {
			setData(item.getData());
		}
	}

	public ItemStack exportTo(CEProtectDestroyData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		return getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
	}

	public Map<String, String> getPlaceholder(CEProtectDestroyData data) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}

	public ApplyReason applyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}

		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
		WeaponData data = ceWeapon.getWeaponData();

		int newPoint = data.getExtraProtectDestroy() + getData().getExtraPoint();
		if (newPoint > getData().getMaxPoint()) {
			return new ApplyReason("enchanted", ApplyResult.CANCEL);
		}

		data.setExtraProtectDestroy(newPoint);
		
		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setWriteLogs(true);
		reason.setSource(ceWeapon);
		reason.putData("pattern", this.data.getPattern());
		reason.putData("weapon", ItemStackUtils.toString(ceWeapon.getDefaultItemStack()));
		return reason;
	}
	
	public ApplyReason testApplyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}
		
		ceItem = CEWeapon.getCEWeapon(ceItem.getDefaultItemStack());

		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
		WeaponData data = ceWeapon.getWeaponData();

		int newPoint = data.getExtraProtectDestroy() + getData().getExtraPoint();
		if (newPoint > getData().getMaxPoint()) {
			return new ApplyReason("enchanted", ApplyResult.CANCEL);
		}

		data.setExtraProtectDestroy(newPoint);
		
		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setSource(ceWeapon);
		return reason;
	}


}
