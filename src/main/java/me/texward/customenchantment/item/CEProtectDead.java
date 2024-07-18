package me.texward.customenchantment.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.nms.CECraftItemStackNMS;
import me.texward.texwardlib.util.ItemStackUtils;
import me.texward.texwardlib.util.nms.NMSNBTTagCompound;

public class CEProtectDead extends CEItem<CEProtectDeadData> {

	public CEProtectDead(ItemStack itemStack) {
		super(CEItemType.PROTECT_DEAD, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CEProtectDead item = (CEProtectDead) CustomEnchantment.instance().getCEItemStorageMap()
				.get(CEItemType.PROTECT_DEAD).get(pattern);

		if (item != null) {
			setData(item.getData());
		}
	}

	public ItemStack exportTo(CEProtectDeadData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		return getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
	}

	public Map<String, String> getPlaceholder(CEProtectDeadData data) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}

	public ApplyReason applyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}

		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
		WeaponData data = ceWeapon.getWeaponData();

		int newPoint = data.getExtraProtectDead() + getData().getExtraPoint();
		if (newPoint > getData().getMaxPoint()) {
			return new ApplyReason("enchanted", ApplyResult.CANCEL);
		}

		ceWeapon.updateTimeModifier();
		data.setExtraProtectDead(newPoint);

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

		int newPoint = data.getExtraProtectDead() + getData().getExtraPoint();
		if (newPoint > getData().getMaxPoint()) {
			return new ApplyReason("enchanted", ApplyResult.CANCEL);
		}

		ceWeapon.updateTimeModifier();
		data.setExtraProtectDead(newPoint);

		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setSource(ceWeapon);
		return reason;
	}

}
