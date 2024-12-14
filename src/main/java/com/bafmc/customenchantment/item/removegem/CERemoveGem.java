package com.bafmc.customenchantment.item.removegem;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.gem.CEGem;
import com.bafmc.customenchantment.item.gem.CEGemSimple;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CERemoveGem extends CEItem<CERemoveGemData> {

	public CERemoveGem(ItemStack itemStack) {
		super(CEItemType.REMOVE_GEM, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CERemoveGem item = (CERemoveGem) CustomEnchantment.instance().getCeItemStorageMap()
				.get(CEItemType.REMOVE_GEM).get(pattern);

		if (item != null) {
			setData(item.getData());
		}
	}

	public ItemStack exportTo(CERemoveGemData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		return getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
	}

	public Map<String, String> getPlaceholder(CERemoveGemData data) {
		Map<String, String> map = new HashMap<>();
		return map;
	}

	public ApplyReason applyByMenuTo(CEItem ceItem, CEGemSimple ceGemSimple) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}

		if (ceGemSimple == null) {
			return new ApplyReason("not-choose", ApplyResult.CANCEL);
		}

		CEWeapon weapon = (CEWeapon) ceItem;
		WeaponGem weaponGem = weapon.getWeaponGem();
		weaponGem.removeCEGemSimple(ceGemSimple.getIndex());

		CEGem removeCEGemSimple = ceGemSimple.getCEGem();
		removeCEGemSimple.getData().setLevel(ceGemSimple.getLevel());
		ItemStack itemStack = removeCEGemSimple.exportTo();

		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		Map<String, String> placeholder = new LinkedHashMap<>();
		reason.setPlaceholder(placeholder);
		reason.setRewards(Arrays.asList(itemStack));
		reason.setWriteLogs(true);
		reason.putData("pattern", getData().getPattern());
		reason.putData("enchant", ceGemSimple.getName());
		reason.putData("level", ceGemSimple.getLevel());
		return reason;
	}


	public List<CEGemSimple> getList(List<String> extraGemList) {
        List<CEGemSimple> list = new ArrayList<>();

        int index = 0;
        for (String pattern : extraGemList) {
            list.add(new CEGemSimple(pattern, index));
            index++;
        }

        return list;
    }
}
