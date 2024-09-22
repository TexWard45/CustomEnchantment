package com.bafmc.customenchantment.item;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.enchant.CESimple;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;

public class CEBook extends CEItem<CEBookData> {

	public CEBook(ItemStack itemStack) {
		super(CEItemType.BOOK, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);
		CESimple ceSimple = new CESimple(tag.getString(CENBT.ENCHANT));

		CEBookData data = new CEBookData();
		data.setPattern(pattern);
		data.setCESimple(ceSimple);
		setData(data);
	}

	public ItemStack exportTo(CEBookData data) {
		CEBook book = (CEBook) CEAPI.getCEItemByStorage(CEItemType.BOOK, data.getPattern());

        if (book == null) {
            book = (CEBook) CEAPI.getCEItemByStorage(CEItemType.BOOK, "default-default");
        }

		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(book.getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());
		tag.setString(CENBT.ENCHANT, data.getCESimple().toLine());
		tag.setLong(CENBT.TIME, System.currentTimeMillis());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		return getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
	}

	public Map<String, String> getPlaceholder(CEBookData data) {
		Map<String, String> map = new HashMap<String, String>();
		map.putAll(CEPlaceholder.getCESimplePlaceholder(data.getCESimple()));
        if (data.getCESimple().getCEEnchant().getCEDisplay().getBookDisplay() != null) {
            map.put("%group_book_display%", data.getCESimple().getCEEnchant().getCEDisplay().getBookDisplay());
        }
		return map;
	}

	public ApplyReason applyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}

		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
		WeaponEnchant enchant = ceWeapon.getWeaponEnchant();

		CESimple ceSimple = this.getData().getCESimple();
		if (!enchant.isEnoughEnchantPointForNextCE(ceSimple)) {
			return new ApplyReason("not-enough-enchant-point", ApplyResult.CANCEL);
		}

		boolean canApplyMaterial = ceSimple.getCEEnchant().getAppliesMaterialList()
				.contains(new MaterialData(ceItem.getDefaultItemStack()));
		if (!canApplyMaterial) {
			return new ApplyReason("not-support-type", ApplyResult.CANCEL);
		}

		if (enchant.containsCESimple(ceSimple.getName())
				&& enchant.getCESimple(ceSimple.getName()).getLevel() >= ceSimple.getLevel()) {
			return new ApplyReason("conflict-enchant", ApplyResult.CANCEL);
		}

        if (enchant.hasEnchantBlacklist(ceSimple)) {
            ApplyReason reason = new ApplyReason("enchant-blacklist", ApplyResult.CANCEL);

            CESimple blacklistEnchant = enchant.getEnchantBlacklist(ceSimple);

            Map<String, String> placeholder = new LinkedHashMap<>();
            placeholder.put("%enchant_display%", blacklistEnchant.getCEEnchant().getCEDisplay().getDisplay());
            reason.setPlaceholder(placeholder);
            return reason;
        }

		Chance success = new Chance(ceSimple.getSuccess().getValue());

		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		Map<String, String> placeholder = CEPlaceholder.getCESimplePlaceholder(ceSimple);
		reason.setPlaceholder(placeholder);

		reason.setWriteLogs(true);
		reason.putData("enchant", ceSimple.getName());
		reason.putData("level", ceSimple.getLevel());
		reason.putData("success", ceSimple.getSuccess().getValue());
		reason.putData("destroy", ceSimple.getDestroy().getValue());
        reason.putData("xp", ceSimple.getXp());
		reason.putData("weapon", ItemStackUtils.toString(ceWeapon.getDefaultItemStack()));

		if (success.work()) {
			enchant.removeCESimple(ceSimple.getName());
			enchant.addCESimple(ceSimple);

			ceWeapon.updateTimeModifier();
			return reason;
		} else {
			Chance destroy = new Chance(ceSimple.getDestroy().getValue());

			if (destroy.work()) {
				int protectDestroy = ceWeapon.getWeaponData().getExtraProtectDestroy();

				if (protectDestroy > 0) {
					ceWeapon.getWeaponData().setExtraProtectDestroy(protectDestroy - 1);
					ceWeapon.updateTimeModifier();

					reason.setReason("protect-destroy");
					reason.setResult(ApplyResult.FAIL_AND_UPDATE);
					return reason;
				}
				reason.setReason("destroy");
				reason.setResult(ApplyResult.DESTROY);
				return reason;
			} else {
				reason.setReason("fail");
				reason.setResult(ApplyResult.FAIL);
				return reason;
			}
		}
	}

	public ApplyReason testApplyTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}

		ceItem = CEWeapon.getCEWeapon(ceItem.getDefaultItemStack());
		
		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
		WeaponEnchant enchant = ceWeapon.getWeaponEnchant();

		CESimple ceSimple = this.getData().getCESimple();
		if (!enchant.isEnoughEnchantPointForNextCE(ceSimple)) {
			return new ApplyReason("not-enough-enchant-point", ApplyResult.CANCEL);
		}

		boolean canApplyMaterial = ceSimple.getCEEnchant().getAppliesMaterialList()
				.contains(new MaterialData(ceItem.getDefaultItemStack()));
		if (!canApplyMaterial) {
			return new ApplyReason("not-support-type", ApplyResult.CANCEL);
		}

		if (enchant.containsCESimple(ceSimple.getName())
				&& enchant.getCESimple(ceSimple.getName()).getLevel() >= ceSimple.getLevel()) {
			return new ApplyReason("conflict-enchant", ApplyResult.CANCEL);
		}

        if (enchant.hasEnchantBlacklist(ceSimple)) {
            ApplyReason reason = new ApplyReason("enchant-blacklist", ApplyResult.CANCEL);

            CESimple blacklistEnchant = enchant.getEnchantBlacklist(ceSimple);

            Map<String, String> placeholder = new LinkedHashMap<>();
            placeholder.put("%enchant_display%", blacklistEnchant.getCEEnchant().getCEDisplay().getDisplay());
            reason.setPlaceholder(placeholder);
            return reason;
        }

		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		Map<String, String> placeholder = CEPlaceholder.getCESimplePlaceholder(ceSimple);
		reason.setPlaceholder(placeholder);
		reason.setSource(ceItem);

		enchant.removeCESimple(ceSimple.getName());
		enchant.addCESimple(ceSimple);

		ceWeapon.updateTimeModifier();
		return reason;
	}
}
