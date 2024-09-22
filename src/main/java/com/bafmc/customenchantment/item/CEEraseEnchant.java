package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.utils.EnchantmentUtils;
import com.bafmc.bukkit.utils.NumberUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CEEraseEnchant extends CEItem<CEEraseEnchantData> {

	public CEEraseEnchant(ItemStack itemStack) {
		super(CEItemType.EARSE_ENCHANT, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CEEraseEnchant item = (CEEraseEnchant) CustomEnchantment.instance().getCEItemStorageMap()
				.get(CEItemType.EARSE_ENCHANT).get(pattern);

		if (item != null) {
			setData(item.getData());
		}
	}

	public ItemStack exportTo(CEEraseEnchantData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		return getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
	}

	public Map<String, String> getPlaceholder(CEEraseEnchantData data) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}
	
	public List<Enchantment> getEraseEnchantList(Map<Enchantment, Integer> enchantMap) {
		List<Enchantment> list = new ArrayList<Enchantment>();
		
		for (Enchantment enchant : enchantMap.keySet()) {
			if (!data.getBlacklistEnchantments().contains(enchant.getName())) {
				list.add(enchant);
			}
		}

		return list;
	}
	
	public ApplyReason applyByMenuTo(CEItem ceItem, Enchantment enchantment) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}
		
		if (enchantment == null) {
			return new ApplyReason("not-choose", ApplyResult.CANCEL);
		}
		
		ItemStack itemStack = ceItem.getDefaultItemStack();
		int level = itemStack.removeEnchantment(enchantment);
		
		ceItem.setCraftItemStack(new CECraftItemStackNMS(itemStack));
		
		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		Map<String, String> placeholder = new LinkedHashMap<String, String>();
		placeholder.put("%enchant_display%", EnchantmentUtils.getDisplayName(enchantment));
		placeholder.put("%enchant_level%", "" + NumberUtils.toRomanNumber(level));
		reason.setPlaceholder(placeholder);
		reason.setWriteLogs(true);
		reason.putData("pattern", getData().getPattern());
		reason.putData("enchant", enchantment.getName());
		reason.putData("level", level);
		return reason;
	}
	
	public ApplyReason applyTo(CEItem ceItem) {
		return ApplyReason.NOTHING;
	}

}
