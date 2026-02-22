package com.bafmc.customenchantment.item.removeprotectdead;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.protectdead.CEProtectDead;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;

public class CERemoveProtectDead extends CEItem<CERemoveProtectDeadData> {

	public CERemoveProtectDead(ItemStack itemStack) {
		super(CEItemType.REMOVE_PROTECT_DEAD, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CERemoveProtectDead item = (CERemoveProtectDead) CustomEnchantment.instance().getCeItemStorageMap()
				.get(CEItemType.REMOVE_PROTECT_DEAD).get(pattern);

		if (item != null) {
			setData(item.getData());
		}
	}

	public ItemStack exportTo(CERemoveProtectDeadData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		return getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
	}

	public Map<String, String> getPlaceholder(CERemoveProtectDeadData data) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}

	public ApplyReason applyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}

		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
		WeaponData data = ceWeapon.getWeaponData();

		int point = data.getExtraProtectDead();
		if (point <= 0) {
			return new ApplyReason("empty", ApplyResult.CANCEL);
		}

		ceWeapon.updateTimeModifier();
		data.setExtraProtectDead(point - 1);
		data.removeProtectDeadIndex(data.getExtraProtectDeadList().size() - 1);

		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setWriteLogs(true);
		reason.setSource(ceWeapon);
		
		CEProtectDead protectDead = (CEProtectDead) CustomEnchantment.instance().getCeItemStorageMap()
				.get(CEItemType.PROTECT_DEAD).get(this.data.getProtectDeadType());
		
		if (protectDead != null) {
			reason.setRewards(Arrays.asList(protectDead.exportTo()));
		}
		
		reason.putData(CEConstants.DataKey.PATTERN, this.data.getPattern());
		reason.putData(CEConstants.DataKey.WEAPON, ItemStackUtils.toString(ceWeapon.getDefaultItemStack()));
		return reason;
	}
	
	public ItemStack getProtectDeadItem() {
		CEProtectDead protectDead = (CEProtectDead) CustomEnchantment.instance().getCeItemStorageMap()
				.get(CEItemType.PROTECT_DEAD).get(this.data.getProtectDeadType());
		
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
		WeaponData data = ceWeapon.getWeaponData();

		int point = data.getExtraProtectDead();
		if (point <= 0) {
			return new ApplyReason("empty", ApplyResult.CANCEL);
		}
		
		ceWeapon.updateTimeModifier();
		data.setExtraProtectDead(point - 1);
		data.removeProtectDeadIndex(data.getExtraProtectDeadList().size() - 1);

		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setSource(ceWeapon);
		return reason;
	}

}
