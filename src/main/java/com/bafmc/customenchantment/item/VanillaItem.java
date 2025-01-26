package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import org.bukkit.inventory.ItemStack;

public class VanillaItem extends CEItem<VanillaItemData> {
	public VanillaItem(ItemStack itemStack) {
		super(CEItemType.STORAGE, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		VanillaItem item = (VanillaItem) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.STORAGE).get(pattern);

		if (item != null) {
			setData(item.getData());
		}
	}

	public ItemStack exportTo(VanillaItemData data) {
		if (data.isOrigin()) {
			return getDefaultItemStack().clone();
		}

		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		if (getData().isWeapon()) {
			ItemStack itemStack = getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
			return new CEWeapon(itemStack).exportTo();
		}

		return itemStackNMS.getNewItemStack();
	}
}
