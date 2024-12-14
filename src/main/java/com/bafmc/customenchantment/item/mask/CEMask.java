package com.bafmc.customenchantment.item.mask;

import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.banner.CEBanner;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.bukkit.utils.MaterialUtils;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;

public class CEMask extends CEUnify<CEMaskData> {

	public CEMask(ItemStack itemStack) {
		super(CEItemType.MASK, itemStack);
	}
	
	public ApplyReason applyTo(CEItem ceItem) {
		if (ceItem instanceof CEWeapon) {
			if (!MaterialUtils.isSimilar(ceItem.getDefaultItemStack().getType(), "HELMET")) {
				return ApplyReason.NOTHING;
			}
			
			return super.applyTo(ceItem);
		}
		
		if (ceItem instanceof CEBanner) {
			return ceItem.applyTo(this);
		}

		return ApplyReason.NOTHING;
	}
	
	public void updateTimeModifierTag(NMSNBTTagCompound tag) {
	}


	public String getWeaponSettingsName() {
		return getUnifyWeapon().isSet() ? "mask-" + super.getWeaponSettingsName() + "-unify" : "mask-" + super.getWeaponSettingsName();
	}

	public String getDisplay(String display) {
		CEUnify item = (CEUnify) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.MASK)
				.get(getData().getPattern());
		CEUnifyData data = (CEUnifyData) item.getData();

		if (display.contains(ChatColor.BOLD.toString())) {
			return data.getBoldDisplay().replace("%display%", display);
		} else {
			return data.getNormalDisplay().replace("%display%", display);
		}
	}
}
