package com.bafmc.customenchantment.item.skin;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.*;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class CESkin extends CEUnify<CESkinData> {

	public CESkin(ItemStack itemStack) {
		super(CEItemType.SKIN, itemStack);
	}
	
	public ApplyReason applyTo(CEItem ceItem) {
		if (ceItem instanceof CEWeapon) {
			return super.applyTo(ceItem);
		}
		
		return ApplyReason.NOTHING;
	}
	
	public void updateTimeModifierTag(NMSNBTTagCompound tag) {
	}

	public String getWeaponSettingsName() {
		return getUnifyWeapon().isSet() ? "skin-" + super.getWeaponSettingsName() + "-unify" : "skin-" + super.getWeaponSettingsName();
	}

	public String getDisplay(String display) {
		CEUnify item = (CEUnify) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.SKIN)
				.get(getData().getPattern());
		CEUnifyData data = (CEUnifyData) item.getData();

		if (display.contains(ChatColor.BOLD.toString())) {
			return data.getBoldDisplay().replace("%display%", display);
		} else {
			return data.getNormalDisplay().replace("%display%", display);
		}
	}
}
