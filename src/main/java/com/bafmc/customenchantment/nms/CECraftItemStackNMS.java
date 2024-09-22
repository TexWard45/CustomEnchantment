package com.bafmc.customenchantment.nms;

import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.item.CENBT;
import com.bafmc.bukkit.bafframework.nms.NMSItemStack;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;

public class CECraftItemStackNMS extends NMSItemStack {
	public CECraftItemStackNMS(ItemStack itemStack) {
		super(itemStack);
	}

	/**
	 * Save NBT to itemStack (NMS) and update itemStack (current)
	 */
	public void setCETag(NMSNBTTagCompound ceTag) {
		NMSNBTTagCompound tag = getCompound();

		if (ceTag != null) {
			tag.set(CENBT.CE, ceTag);
		} else {
			tag.remove(CENBT.CE);
		}

		setCompound(tag);
	}

	public NMSNBTTagCompound getCECompound() {
		NMSNBTTagCompound ceTag = getCompound().getCompound(CENBT.CE);
		return ceTag != null ? ceTag : new NMSNBTTagCompound();
	}

	public boolean isSetCECompound() {
		NMSNBTTagCompound ceTag = getCompound().getCompound(CENBT.CE);
		return ceTag != null;
	}
}
