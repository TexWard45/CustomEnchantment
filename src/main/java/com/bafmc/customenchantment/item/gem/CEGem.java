package com.bafmc.customenchantment.item.gem;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrillSimple;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CEGem extends CEItem<CEGemData> {
	public CEGem(ItemStack itemStack) {
		super(CEItemType.GEM, itemStack);
	}

	public CEGemSimple getCEGemSimple() {
		return new CEGemSimple(getData().getPattern(), getData().getLevel());
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);
		int level = Math.max(tag.getInt(CENBT.LEVEL), 1);

		CEGem item = (CEGem) CustomEnchantment.instance().getCeItemStorageMap()
				.get(CEItemType.GEM).get(pattern);

		if (item != null) {
			CEGemData data = item.getData().clone();
			data.setLevel(level);
			setData(data);
		}
	}

	public ItemStack exportTo(CEGemData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());
		tag.setInt(CENBT.LEVEL, data.getLevel());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		ItemStack itemStack = setItemStack(itemStackNMS.getNewItemStack(), getPlaceholder(data));
		itemStack = ItemStackUtils.updateColorToItemStack(itemStack);
		return itemStack;
	}

	public static ItemStack setItemStack(ItemStack itemStack, Map<String, String> placeholder) {
		if (!itemStack.hasItemMeta()) {
			return itemStack;
		} else {
			ItemMeta meta = itemStack.getItemMeta();
			if (meta.hasDisplayName()) {
				String displayName = meta.getDisplayName();
				meta.setDisplayName(StringUtils.replaceToList(displayName, placeholder).get(0));
			}

			if (meta.hasLore()) {
				List<String> lore = meta.getLore();
				meta.setLore(StringUtils.replaceToList(lore, placeholder));
			}

			itemStack.setItemMeta(meta);
			return itemStack;
		}
	}

	public Map<String, String> getPlaceholder(CEGemData data) {
		Map<String, String> map = new HashMap<>();

		if (data.getLevel() <= 0) {
			return map;
		}

		map.put("{gem_applies_description}", StringUtils.toString(data.getConfigData().getAppliesDescription()));
		map.put("{gem_level}", String.valueOf(data.getLevel()));
		map.put("{level_color}", CEGemSettings.getSettings().getGemLevelSettings(data.getLevel()).getColor());
		// Fix auto replace bold color
		map.put("{level_color_bold}", CEGemSettings.getSettings().getGemLevelSettings(data.getLevel()).getColor() + "&l");
		return map;
	}

	public ApplyReason applyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}

		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
		WeaponGem data = ceWeapon.getWeaponGem();

		boolean canApplyMaterial = getData().getConfigData().getAppliesMaterialList()
				.contains(new MaterialData(ceItem.getDefaultItemStack()));
		if (!canApplyMaterial) {
			return new ApplyReason("not-support-type", ApplyResult.CANCEL);
		}

		CEGemDrillSimple gemDrillSimple = data.getSuitableGemDrill(getCEGemSimple());
		if (gemDrillSimple == null) {
			return new ApplyReason("no-suitable-slot", ApplyResult.CANCEL);
		}

		int gemTypeCount = data.getGemTypeCount(getData().getPattern());
		if (gemTypeCount >= getData().getConfigData().getLimitPerItem()) {
			PlaceholderBuilder builder = PlaceholderBuilder.builder();
			builder.put("{limit}", String.valueOf(getData().getConfigData().getLimitPerItem()));
			ApplyReason applyReason = new ApplyReason("reach-limit-per-item", ApplyResult.CANCEL);
			applyReason.setPlaceholder(builder.build());
			return applyReason;
		}

		data.addCEGemSimple(getCEGemSimple());

		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setWriteLogs(true);
		reason.setSource(ceWeapon);
		reason.putData("pattern", this.data.getPattern());
		reason.putData("weapon", ItemStackUtils.toString(ceWeapon.getDefaultItemStack()));
		return reason;
	}
	
	public ApplyReason testApplyByMenuTo(CEItem ceItem) {
		if (!(ceItem instanceof CEWeapon)) {
			return ApplyReason.NOTHING;
		}
		
		ceItem = CEWeapon.getCEWeapon(ceItem.getDefaultItemStack());

		CEWeaponAbstract ceWeapon = (CEWeaponAbstract) ceItem;
		WeaponGem data = ceWeapon.getWeaponGem();

		boolean canApplyMaterial = getData().getConfigData().getAppliesMaterialList()
				.contains(new MaterialData(ceItem.getDefaultItemStack()));
		if (!canApplyMaterial) {
			return new ApplyReason("not-support-type", ApplyResult.CANCEL);
		}

		CEGemDrillSimple gemDrillSimple = data.getSuitableGemDrill(getCEGemSimple());
		if (gemDrillSimple == null) {
			return new ApplyReason("no-suitable-slot", ApplyResult.CANCEL);
		}

		int gemTypeCount = data.getGemTypeCount(getData().getPattern());
		if (gemTypeCount >= getData().getConfigData().getLimitPerItem()) {
			PlaceholderBuilder builder = PlaceholderBuilder.builder();
			builder.put("{limit}", String.valueOf(getData().getConfigData().getLimitPerItem()));
			ApplyReason applyReason = new ApplyReason("reach-limit-per-item", ApplyResult.CANCEL);
			applyReason.setPlaceholder(builder.build());
			return applyReason;
		}

		data.addCEGemSimple(getCEGemSimple());

		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setSource(ceWeapon);
		return reason;
	}

}
