package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.utils.MaterialUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.item.banner.CEBanner;
import com.bafmc.customenchantment.item.mask.CEMask;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CEWeapon extends CEWeaponAbstract<CEWeaponData> {
	private static MaterialList materialWhitelist = new MaterialList();

	public CEWeapon(ItemStack itemStack) {
		super(CEItemType.WEAPON, itemStack);
	}

	public static void setWhitelist(List<MaterialData> list) {
		if (list == null) {
			return;
		}
		CEWeapon.materialWhitelist = new MaterialList(list);
	}
	
	public void importFrom(ItemStack itemStack) {
		super.importFrom(itemStack);
		
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CEWeapon item = (CEWeapon) CustomEnchantment.instance().getCeItemStorageMap().get(type).get(pattern);

		if (item != null) {
			setData(item.getData());
		}
	}
	
	public ItemStack exportTo() {
		ItemStack itemStack = super.exportTo();
		
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(itemStack);
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		if (data != null) {
			tag.setString(CENBT.PATTERN, data.getPattern());
		}

		itemStackNMS.setCETag(tag);
		itemStack = itemStackNMS.getNewItemStack();

		if (CustomEnchantment.instance().getMainConfig().isUnbreakableArmorEnable()) {
			if (!itemStack.isUnbreakable()) {
				itemStack.setUnbreakable(true);
				itemStack.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			}
		}

		return itemStack;
	}

	public boolean isMatchType(String type) {
		ItemStack itemStack = getCurrentItemStack();
		if (materialWhitelist.contains(new MaterialData(itemStack))) {
			return true;
		}
		CECraftItemStackNMS craftItemStack = new CECraftItemStackNMS(itemStack);
		return craftItemStack.getCECompound().getString(CENBT.TYPE).equals(getType());
	}

	public ApplyReason applyTo(CEItem ceItem) {
		if (ceItem instanceof CEBanner && MaterialUtils.isSimilar(this.getDefaultItemStack().getType(), "HELMET")
				&& ((CEBanner) ceItem).isHelmetEnable()) {
			return ceItem.applyTo(this);
		}

		if (ceItem instanceof CEMask) {
			return ceItem.applyTo(this);
		}

		return ApplyReason.CANCEL;
	}

}
