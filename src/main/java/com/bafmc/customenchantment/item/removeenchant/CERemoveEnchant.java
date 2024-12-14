package com.bafmc.customenchantment.item.removeenchant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.bafmc.customenchantment.item.*;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;

public class CERemoveEnchant extends CEItem<CERemoveEnchantData> {
	private static Random random = new Random();

	public CERemoveEnchant(ItemStack itemStack) {
		super(CEItemType.REMOVE_ENCHANT, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CERemoveEnchant item = (CERemoveEnchant) CustomEnchantment.instance().getCeItemStorageMap()
				.get(CEItemType.REMOVE_ENCHANT).get(pattern);

		if (item != null) {
			setData(item.getData());
		}
	}

	public ItemStack exportTo(CERemoveEnchantData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		return getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
	}

	public Map<String, String> getPlaceholder(CERemoveEnchantData data) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}
	
	public List<CEEnchantSimple> getRemoveEnchantList(List<CEEnchantSimple> list) {
		List<CEEnchantSimple> removeList = new ArrayList<CEEnchantSimple>();

		for (CEEnchantSimple ceEnchantSimple : list) {
			if (data.getGroups().contains(ceEnchantSimple.getCEEnchant().getGroupName())) {
				removeList.add(ceEnchantSimple);
			}
		}

		return removeList;
	}
	
	public ApplyReason applyByMenuTo(CEItem ceItem, CEEnchantSimple ceEnchantSimple) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}
		
		if (ceEnchantSimple == null) {
			return new ApplyReason("not-choose", ApplyResult.CANCEL);
		}
		
		CEWeapon weapon = (CEWeapon) ceItem;
		WeaponEnchant enchant = weapon.getWeaponEnchant();

		int success = ceEnchantSimple.getSuccess().getValue();
		int destroy = ceEnchantSimple.getDestroy().getValue();

		success = Math.min(success, 100);
		destroy = Math.max(destroy, 0);

		enchant.removeCESimple(ceEnchantSimple.getName());

		CEEnchantSimple removeCEEnchantSimple = new CEEnchantSimple(ceEnchantSimple.getName(), ceEnchantSimple.getLevel(), success, destroy, ceEnchantSimple.getXp());
		
		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		Map<String, String> placeholder = CEPlaceholder.getCESimplePlaceholder(removeCEEnchantSimple);
		reason.setPlaceholder(placeholder);
		reason.setRewards(Arrays.asList(CEAPI.getCEBookItemStack(removeCEEnchantSimple)));
		reason.setWriteLogs(true);
		reason.putData("pattern", getData().getPattern());
		reason.putData("enchant", removeCEEnchantSimple.getName());
		reason.putData("level", removeCEEnchantSimple.getLevel());
		reason.putData("success", removeCEEnchantSimple.getSuccess().getValue());
		reason.putData("destroy", removeCEEnchantSimple.getDestroy().getValue());
		return reason;
	}
	
	public ApplyReason applyTo(CEItem ceItem) {
		return ApplyReason.NOTHING;
//		if (!(ceItem instanceof CEWeapon)) {
//			return ApplyReason.NOTHING;
//		}
//
//		CEWeaponAbstract weapon = (CEWeaponAbstract) ceItem;
//		WeaponEnchant enchant = weapon.getWeaponEnchant();
//		CERemoveEnchantData data = getData();
//
//		List<CESimple> removeList = new ArrayList<CESimple>();
//		List<CESimple> list = enchant.getCESimpleList();
//		if (list.isEmpty()) {
//			return new ApplyReason("empty", ApplyResult.CANCEL);
//		}
//
//		for (CESimple ceSimple : list) {
//			if (data.getGroups().contains(ceSimple.getCEEnchant().getGroupName())) {
//				removeList.add(ceSimple);
//			}
//		}
//
//		if (removeList.isEmpty()) {
//			return new ApplyReason("not-support-group", ApplyResult.CANCEL);
//		}
//
//		CESimple randomCESimple = removeList.get(random.nextInt(removeList.size()));
//
//		int success = data.getSuccess() < 0 ? randomCESimple.getSuccess().getValue() : data.getSuccess();
//		int destroy = data.getDestroy() < 0 ? randomCESimple.getDestroy().getValue() : data.getDestroy();
//
//		success = Math.min(success, 100);
//		destroy = Math.max(destroy, 0);
//
//		enchant.removeCESimple(randomCESimple.getName());
//
//		CESimple removeCESimple = new CESimple(randomCESimple.getName(), randomCESimple.getLevel(), success, destroy);
//		
//		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
//		Map<String, String> placeholder = CEPlaceholder.getCESimplePlaceholder(removeCESimple);
//		placeholder.putAll(CEPlaceholder.getCEGroupPlaceholder(removeCESimple.getCEEnchant().getCEGroup()));
//		reason.setPlaceholder(placeholder);
//		reason.setRewards(Arrays.asList(CEAPI.getCEBookItemStack(removeCESimple)));
//		reason.setWriteLogs(true);
//		reason.putData("pattern", getData().getPattern());
//		reason.putData("enchant", removeCESimple.getName());
//		reason.putData("level", removeCESimple.getLevel());
//		reason.putData("success", removeCESimple.getSuccess().getValue());
//		reason.putData("destroy", removeCESimple.getDestroy().getValue());
//		return reason;
	}

}
