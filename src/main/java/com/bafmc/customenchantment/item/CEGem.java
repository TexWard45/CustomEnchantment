package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CEGem extends CEItem<CEGemData> {
	public CEGem(ItemStack itemStack) {
		super(CEItemType.GEM, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);
		int level = Math.max(tag.getInt(CENBT.LEVEL), 1);

		CEGem item = (CEGem) CustomEnchantment.instance().getCEItemStorageMap()
				.get(CEItemType.GEM).get(pattern);

		if (item != null) {
			CEGemData data = item.getData().clone();
			data.setLevel(level);
			setData(item.getData());
		}
	}

	public ItemStack exportTo(CEGemData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());
		tag.setInt(CENBT.LEVEL, data.getLevel());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		return setItemStack(itemStackNMS.getNewItemStack(), getPlaceholder(data));
	}

	public static ItemStack setItemStack(ItemStack itemStack, Map<String, String> placeholder) {
		if (!itemStack.hasItemMeta()) {
			return itemStack;
		} else {
			ItemMeta meta = itemStack.getItemMeta();
			if (meta.hasDisplayName()) {
				String displayName = meta.getDisplayName();
				System.out.println(displayName);
				meta.setDisplayName((String)StringUtils.replaceToList(displayName, placeholder).get(0));
			}

			if (meta.hasLore()) {
				List<String> lore = meta.getLore();
				meta.setLore(StringUtils.replaceToList(lore, placeholder));
			}

			itemStack.setItemMeta(meta);
			return itemStack;
		}
	}

	public Map<String, String> getPlaceholder(CEGemData data) {
		Map<String, String> map = new HashMap<>();
		map.put("{gem_applies_description}", StringUtils.toString(data.getAppliesDescription()));
		map.put("{gem_level}", String.valueOf(data.getLevel()));
		map.put("{level_color}", CEGemSettings.getSettings().getGemLevelSettings(data.getLevel()).getColor());
		return map;
	}

	public ApplyReason applyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}
//
//		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
//		WeaponData data = ceWeapon.getWeaponData();
//
//		int newPoint = data.getExtraGem() + getData().getExtraPoint();
//		if (newPoint > getData().getMaxPoint()) {
//			return new ApplyReason("enchanted", ApplyResult.CANCEL);
//		}
//
//		ceWeapon.updateTimeModifier();
//		data.setExtraGem(newPoint);
//
//		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
//		reason.setWriteLogs(true);
//		reason.setSource(ceWeapon);
//		reason.putData("pattern", this.data.getPattern());
//		reason.putData("weapon", ItemStackUtils.toString(ceWeapon.getDefaultItemStack()));
//		return reason;
		return null;
	}
	
	public ApplyReason testApplyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}
		
//		ceItem = CEWeapon.getCEWeapon(ceItem.getDefaultItemStack());
//
//		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
//		WeaponData data = ceWeapon.getWeaponData();
//
//		int newPoint = data.getExtraGem() + getData().getExtraPoint();
//		if (newPoint > getData().getMaxPoint()) {
//			return new ApplyReason("enchanted", ApplyResult.CANCEL);
//		}
//
//		ceWeapon.updateTimeModifier();
//		data.setExtraGem(newPoint);
//
//		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
//		reason.setSource(ceWeapon);
//		return reason;
		return null;
	}

}
