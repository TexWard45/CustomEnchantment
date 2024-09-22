package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.utils.MaterialUtils;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import org.bukkit.inventory.ItemStack;

public class CEBanner extends CEUnify<CEBannerData> {
	private boolean helmetEnable;

	public CEBanner(ItemStack itemStack) {
		super(CEItemType.BANNER, itemStack);
	}

	public void importFrom(ItemStack itemStack) {
		super.importFrom(itemStack);

		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		this.unifyWeapon = new CEUnifyWeapon(this);
		this.unifyWeapon.importFrom(tag.getCompound("unify-data"));
		
		this.helmetEnable = tag.getBoolean("helmet-enable");
	}

	public ItemStack exportTo() {
		ItemStack itemStack = super.exportTo();

		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(itemStack);
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		if (helmetEnable) {
			tag.setBoolean("helmet-enable", true);
		}

		itemStackNMS.setCETag(tag);
		return itemStackNMS.getNewItemStack();
	}

	public boolean isMatchType(String type) {
		ItemStack itemStack = getCurrentItemStack();
		if (MaterialUtils.isSimilar(itemStack.getType(), "BANNER")) {
			return true;
		}
		return super.isMatchType(type);
	}

	public ApplyReason applyTo(CEItem ceItem) {
		// Helmet
		if (helmetEnable) {
			if (ceItem instanceof CEWeapon) {
				if (MaterialUtils.isSimilar(ceItem.getDefaultItemStack().getType(), "HELMET")) {
					return ApplyReason.NOTHING;
				}

				return super.applyTo(ceItem);
			}
		}

		// Mask
		if (!(ceItem instanceof CEMask)) {
			return ApplyReason.NOTHING;
		}

		if (!((CEMask) ceItem).getUnifyWeapon().isSet()) {
			return ApplyReason.NOTHING;
		}

		return super.applyTo(ceItem);
	}

	public boolean isHelmetEnable() {
		return helmetEnable;
	}

	public void setHelmetEnable(boolean helmetEnable) {
		this.helmetEnable = helmetEnable;
	}

	public String getWeaponSettingsName() {
		return getUnifyWeapon().isSet() ? "banner-" + super.getWeaponSettingsName() : super.getWeaponSettingsName();
	}

	public String getDisplay(String display) {
		return display;
	}
}
