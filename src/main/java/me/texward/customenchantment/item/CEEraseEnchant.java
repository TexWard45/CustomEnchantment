package me.texward.customenchantment.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.nms.CECraftItemStackNMS;
import me.texward.texwardlib.util.EnchantmentUtils;
import me.texward.texwardlib.util.RomanNumber;
import me.texward.texwardlib.util.nms.NMSNBTTagCompound;

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
		placeholder.put("%enchant_level%", "" + RomanNumber.toRoman(level));
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
