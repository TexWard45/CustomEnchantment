package me.texward.customenchantment.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEPlaceholder;
import me.texward.customenchantment.enchant.CESimple;
import me.texward.customenchantment.nms.CECraftItemStackNMS;
import me.texward.texwardlib.util.nms.NMSNBTTagCompound;

public class CERemoveEnchant extends CEItem<CERemoveEnchantData> {
	private static Random random = new Random();

	public CERemoveEnchant(ItemStack itemStack) {
		super(CEItemType.REMOVE_ENCHANT, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CERemoveEnchant item = (CERemoveEnchant) CustomEnchantment.instance().getCEItemStorageMap()
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
	
	public List<CESimple> getRemoveEnchantList(List<CESimple> list) {
		List<CESimple> removeList = new ArrayList<CESimple>();

		for (CESimple ceSimple : list) {
			if (data.getGroups().contains(ceSimple.getCEEnchant().getGroupName())) {
				removeList.add(ceSimple);
			}
		}

		return removeList;
	}
	
	public ApplyReason applyByMenuTo(CEItem ceItem, CESimple ceSimple) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}
		
		if (ceSimple == null) {
			return new ApplyReason("not-choose", ApplyResult.CANCEL);
		}
		
		CEWeapon weapon = (CEWeapon) ceItem;
		WeaponEnchant enchant = weapon.getWeaponEnchant();

		int success = ceSimple.getSuccess().getValue();
		int destroy = ceSimple.getDestroy().getValue();

		success = Math.min(success, 100);
		destroy = Math.max(destroy, 0);

		enchant.removeCESimple(ceSimple.getName());

		CESimple removeCESimple = new CESimple(ceSimple.getName(), ceSimple.getLevel(), success, destroy);
		
		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		Map<String, String> placeholder = CEPlaceholder.getCESimplePlaceholder(removeCESimple);
		placeholder.putAll(CEPlaceholder.getCEGroupPlaceholder(removeCESimple.getCEEnchant().getCEGroup()));
		reason.setPlaceholder(placeholder);
		reason.setRewards(Arrays.asList(CEAPI.getCEBookItemStack(removeCESimple)));
		reason.setWriteLogs(true);
		reason.putData("pattern", getData().getPattern());
		reason.putData("enchant", removeCESimple.getName());
		reason.putData("level", removeCESimple.getLevel());
		reason.putData("success", removeCESimple.getSuccess().getValue());
		reason.putData("destroy", removeCESimple.getDestroy().getValue());
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
