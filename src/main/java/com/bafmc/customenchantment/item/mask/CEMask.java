package com.bafmc.customenchantment.item.mask;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.utils.MaterialUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.banner.CEBanner;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

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
		return getUnifyWeapon().isSet() ? CEConstants.ItemPrefix.MASK + super.getWeaponSettingsName() + CEConstants.EquipmentSuffix.UNIFY : CEConstants.ItemPrefix.MASK + super.getWeaponSettingsName();
	}

	public String getDisplay(String display) {
		CEUnify item = (CEUnify) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.MASK)
				.get(getData().getPattern());
		CEUnifyData data = (CEUnifyData) item.getData();

		if (display.contains(ChatColor.BOLD.toString())) {
			return data.getBoldDisplay().replace(CEConstants.Placeholder.DISPLAY, display);
		} else {
			return data.getNormalDisplay().replace(CEConstants.Placeholder.DISPLAY, display);
		}
	}
}
