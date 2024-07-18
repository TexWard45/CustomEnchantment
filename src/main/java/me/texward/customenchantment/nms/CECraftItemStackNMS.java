package me.texward.customenchantment.nms;

import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.item.CENBT;
import me.texward.texwardlib.util.nms.NMSItemStack;
import me.texward.texwardlib.util.nms.NMSNBTTagCompound;

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
