package com.bafmc.customenchantment.item.gemdrill;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CEGemDrill extends CEItem<CEGemDrillData> {

	public CEGemDrill(ItemStack itemStack) {
		super(CEItemType.GEM_DRILL, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CEGemDrill item = (CEGemDrill) CustomEnchantment.instance().getCeItemStorageMap()
				.get(CEItemType.GEM_DRILL).get(pattern);

		if (item != null) {
			setData(item.getData());
		}
	}

	@Override
	public ItemStack exportTo(CEGemDrillData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		return getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
	}

	public Map<String, String> getPlaceholder(CEGemDrillData data) {
		return new HashMap<>();
	}

	public ApplyReason applyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}

		CEGemDrillData.ConfigData configData = getData().getConfigData();
		if (!configData.getApplies().contains(new MaterialData(ceItem.getDefaultItemStack()))) {
			return new ApplyReason("not-support-type", ApplyResult.CANCEL);
		}

		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
		WeaponGem weaponGem = ceWeapon.getWeaponGem();

		if (weaponGem.getDrillSize() >= configData.getMaxDrill()) {
			return new ApplyReason("max-drill", ApplyResult.CANCEL);
		}

		int nextDrillSlot = weaponGem.getDrillSize() + 1;

		if (configData.getSlotChance().containsKey(nextDrillSlot)) {
			double chanceValue = configData.getSlotChance().get(nextDrillSlot);

			Chance chance = new Chance(chanceValue);
			if (!chance.work()) {
				return new ApplyReason("fail-chance", ApplyResult.FAIL);
			}
		}

		weaponGem.addCEGemDrillSimple(new CEGemDrillSimple(getData().getPattern()));

		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setWriteLogs(true);
		reason.putData("pattern", this.data.getPattern());
		reason.putData("weapon", ItemStackUtils.toString(ceWeapon.getDefaultItemStack()));
		return reason;
	}
	
	public ApplyReason testApplyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon weapon)) {
			return ApplyReason.NOTHING;
		}

		ceItem = CEWeapon.getCEWeapon(ceItem.getDefaultItemStack());
		((CEWeapon) ceItem).setWeaponSettingsName("default");

		CEGemDrillData.ConfigData configData = getData().getConfigData();
		if (!configData.getApplies().contains(new MaterialData(ceItem.getDefaultItemStack()))) {
			return new ApplyReason("not-support-type", ApplyResult.CANCEL);
		}

		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
		WeaponGem weaponGem = ceWeapon.getWeaponGem();

		if (weaponGem.getDrillSize() >= configData.getMaxDrill()) {
			return new ApplyReason("max-drill", ApplyResult.CANCEL);
		}

		weaponGem.addCEGemDrillSimple(new CEGemDrillSimple(getData().getPattern()));

		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setSource(ceItem);
		return reason;
	}
}
