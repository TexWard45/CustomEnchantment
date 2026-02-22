package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.api.PlaceholderAPI;
import com.bafmc.bukkit.bafframework.nms.*;
import com.bafmc.bukkit.bafframework.utils.EnchantmentUtils;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.utils.ColorUtils;
import com.bafmc.bukkit.utils.NumberUtils;
import com.bafmc.customenchantment.api.ITrade;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.customenchantment.enchant.CEEnchant;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.item.gem.CEGem;
import com.bafmc.customenchantment.item.gem.CEGemData;
import com.bafmc.customenchantment.item.gem.CEGemSettings;
import com.bafmc.customenchantment.item.gem.CEGemSimple;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrill;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrillData;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrillSimple;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;

@Getter
@Setter
public class WeaponDisplay extends CEItemExpansion implements ITrade<NMSNBTTagCompound> {
	public static final String REMOVE = CEConstants.Sentinel.REMOVE;
	public static final String BEGIN_LABEL = "Ⓑ";
	public static final String END_LABEL = "Ⓔ";
	private List<String> beginLore = new ArrayList<String>();
	private List<String> middleLore = new ArrayList<String>();
	private List<String> endLore = new ArrayList<String>();
	private String displayName;
	private Map<Enchantment, Integer> enchantMap;

	public WeaponDisplay(CEWeaponAbstract ceItem) {
		super(ceItem);
	}

	public List<String> getNewLore(ItemStack itemStack, List<String> loreStyle) {
		WeaponSettings settings = ceItem.getWeaponSettings();
		WeaponData data = ceItem.getWeaponData();

//		StringListReplace replace = new StringListReplace(loreStyle);
		PlaceholderBuilder builder = PlaceholderBuilder.builder();

		BeginLore beginLore = new BeginLore(this.beginLore);
		builder.put("begin_default_lore", beginLore.buildLores());

		NormalEnchantLore normalEnchantLore = new NormalEnchantLore(settings, itemStack);
		builder.put("normal_enchant_lore", normalEnchantLore.buildLores());

		CustomEnchantLore customEnchantLore = new CustomEnchantLore(settings, ceItem);
		builder.put("custom_enchant_lore", customEnchantLore.buildLores());

		GemLore gemLore = new GemLore(settings, ceItem);
		builder.put("gem_lore", gemLore.buildLores());

		MiddleDefaultLore middleDefaultLore = new MiddleDefaultLore(middleLore);
		builder.put("middle_default_lore", middleDefaultLore.buildLores());

		ExtraEnchantPoint extraEnchantPoint = new ExtraEnchantPoint(data, settings);
		builder.put("extra_enchant_point", extraEnchantPoint.buildLores());

		ExtraProtectDead extraProtectDead = new ExtraProtectDead(data, settings);
		builder.put("extra_protect_dead", extraProtectDead.buildLores());

		ExtraProtectDestroy extraProtectDestroy = new ExtraProtectDestroy(data, settings);
		builder.put("extra_protect_destroy", extraProtectDestroy.buildLores());

		AttributeLore attributeLore = new AttributeLore(settings, itemStack);
		builder.put("attribute_lore", attributeLore.buildLores());

		EndDefaultLores endDefaultLores = new EndDefaultLores(endLore);
		builder.put("end_default_lore", endDefaultLores.buildLores());

		List<String> newLore = builder.build().apply(loreStyle);

		ListIterator<String> iterator = newLore.listIterator();
		while (iterator.hasNext()) {
			String next = iterator.next();
			if (next.equals(REMOVE)) {
				iterator.remove();
			}
		}

		for (int i = 0; i < newLore.size() - 1; i++) {
			if (newLore.get(i).isEmpty() && newLore.get(i + 1).isEmpty()) {
				newLore.remove(i);
			}

			if (newLore.get(i).isEmpty() && newLore.get(i + 1).contains(CEConstants.Sentinel.BLANK_LORE)) {
				newLore.remove(i);
			}
		}

		for (int i = 0; i < newLore.size() - 1; i++) {
			if (newLore.get(i).contains(CEConstants.Sentinel.BLANK_LORE) && newLore.get(i + 1).isEmpty()) {
				newLore.remove(i + 1);
				i--;
				continue;
			}
			
			if (newLore.get(i).contains(CEConstants.Sentinel.LOWER_BLANK_LORE) && newLore.get(i + 1).contains(CEConstants.Sentinel.LOWER_BLANK_LORE)) {
				newLore.remove(i + 1);
				i--;
				continue;
			}
			
			if (newLore.get(i).contains(CEConstants.Sentinel.UPPER_BLANK_LORE) && newLore.get(i + 1).contains(CEConstants.Sentinel.UPPER_BLANK_LORE)) {
				newLore.remove(i + 1);
				i--;
				continue;
			}
		}

		// Trim
		while (!newLore.isEmpty() && (newLore.getFirst().isEmpty() || newLore.getFirst().equals(CEConstants.Sentinel.LOWER_BLANK_LORE))) {
			newLore.removeFirst();
		}

		while (!newLore.isEmpty() && (newLore.getLast().isEmpty()
				|| newLore.getLast().equals(CEConstants.Sentinel.UPPER_BLANK_LORE))) {
			newLore.removeLast();
		}


		ListIterator<String> ite = newLore.listIterator();
		while (ite.hasNext()) {
			String next = ite.next();

			if (next.contains(CEConstants.Sentinel.BLANK_LORE)) {
				ite.set("");
			}
		}

		return ColorUtils.t(newLore);
	}

	public void apply(ItemStack itemStack) {
		ItemMeta itemMeta = itemStack.getItemMeta();

		applyNewLore(itemStack, itemMeta);
		applyDisplayName(itemMeta);
		applyItemFlag(itemMeta);

		itemStack.setItemMeta(itemMeta);
	}

	public void applyNewLore(ItemStack itemStack, ItemMeta itemMeta) {
		List<String> newLore = getNewLore(itemStack, ceItem.getWeaponSettings().getLoreStyle());
		itemMeta.setLore(newLore);
	}

	public void applyDisplayName(ItemMeta itemMeta) {
		itemMeta.setDisplayName(this.displayName);
	}

	public void applyItemFlag(ItemMeta itemMeta) {
		List<ItemFlag> itemFlag = ceItem.getWeaponSettings().getItemFlags();
		itemMeta.addItemFlags(itemFlag.toArray(new ItemFlag[itemFlag.size()]));
	}

	public void applyEnchant(ItemStack itemStack) {
		if (enchantMap == null) {
			return;
		}

		for (Map.Entry<Enchantment, Integer> entry : enchantMap.entrySet()) {
			itemStack.addUnsafeEnchantment(entry.getKey(), entry.getValue());
		}
	}

	public void importFrom(NMSNBTTagCompound source) {
		ItemStack itemStack = ceItem.getCurrentItemStack();
		if (source == null || source.isEmpty()) {
			if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
				List<String> lore = itemStack.getItemMeta().getLore();

				for (String line : lore) {
					if (line.startsWith(BEGIN_LABEL)) {
						this.beginLore.add(line.substring(1));
					} else if (line.startsWith(END_LABEL)) {
						this.endLore.add(line.substring(1));
					} else {
						this.middleLore.add(line);
					}
				}
			}
		} else {
			this.beginLore = source.getList("begin").getStringList();
			this.middleLore = source.getList("middle").getStringList();
			this.endLore = source.getList("end").getStringList();
		}

		if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
			this.displayName = itemStack.getItemMeta().getDisplayName();
		}
	}

	public NMSNBTTagCompound exportTo() {
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		NMSNBTTagList begin = new NMSNBTTagList();
		for (String line : beginLore) {
			begin.addString(line);
		}

		NMSNBTTagList middle = new NMSNBTTagList();
		for (String line : middleLore) {
			middle.addString(line);
		}

		NMSNBTTagList end = new NMSNBTTagList();
		for (String line : endLore) {
			end.addString(line);
		}

		tag.set("begin", begin);
		tag.set("middle", middle);
		tag.set("end", end);

		return tag;
	}

}

@AllArgsConstructor
class BeginLore {
	private List<String> beginLore;

	public List<String> buildLores() {
		if (beginLore.isEmpty()) {
			return Arrays.asList(WeaponDisplay.REMOVE);
		}

		return beginLore;
	}
}

@AllArgsConstructor
class NormalEnchantLore {
	private WeaponSettings settings;
	private ItemStack itemStack;

	public List<String> buildLores() {
		List<String> lore = new ArrayList<String>();

		if (!itemStack.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
			Map<Enchantment, Integer> enchantMap = itemStack.getEnchantments();

			for (Enchantment enchantment : enchantMap.keySet()) {
				String vanillaLore = settings.getVanillaEnchantLore();
				vanillaLore = vanillaLore.replace(CEConstants.Placeholder.ENCHANT_DISPLAY, PlaceholderAPI.setPlaceholders(null, EnchantmentUtils.getDisplayName(enchantment)));
				vanillaLore = vanillaLore.replace(CEConstants.Placeholder.ENCHANT_LEVEL, NumberUtils.toRomanNumber(enchantMap.get(enchantment)));
				lore.add(vanillaLore);
			}
		}

		if (lore.isEmpty()) {
			return Arrays.asList(WeaponDisplay.REMOVE);
		}

		return lore;
	}
}

@AllArgsConstructor
class CustomEnchantLore {
	private WeaponSettings settings;
	private CEWeaponAbstract ceItem;

	public List<String> buildLores() {
		List<String> lore = new ArrayList<String>();
		List<CEEnchantSimple> ceEnchantSimpleList = ceItem.getWeaponEnchant().getCESimpleListByPriority();
		for (CEEnchantSimple ceEnchantSimple : ceEnchantSimpleList) {
			Map<String, String> placeholder = CEPlaceholder.getCESimplePlaceholder(ceEnchantSimple);

			CEEnchant ceEnchant = ceEnchantSimple.getCEEnchant();
			if (ceEnchant.getCEDisplay().isDisableEnchantLore()) {
				continue;
			}


			String customDisplay = null;
			Map<String, String> customDisplayMap = ceEnchant.getCEDisplay().getCustomDisplayFormat();

			if (customDisplayMap.containsKey(settings.getKey())) {
				customDisplay = customDisplayMap.get(settings.getKey());
			}else {
				customDisplay = customDisplayMap.get("default");
			}

			if (customDisplay == null) {
				customDisplay = settings.getCustomEnchantLore();
			}

			String display = CEPlaceholder.setPlaceholder(customDisplay, placeholder);
			display = PlaceholderAPI.setPlaceholders(null, display);
			lore.add(display);
		}

		if (lore.isEmpty()) {
			return Arrays.asList(WeaponDisplay.REMOVE);
		}

		return lore;
	}
}

@AllArgsConstructor
class GemLore {
	private WeaponSettings settings;
	private CEWeaponAbstract ceItem;

	public List<String> buildLores() {
		List<String> lore = new ArrayList<>();
		List<CEGemSimple> gemSimpleList = ceItem.getWeaponGem().getCEGemSimpleList();
		List<CEGemDrillSimple> availableDrillList = ceItem.getWeaponGem().getAvailableGemDrillList();
		for (CEGemSimple gemSimple : gemSimpleList) {
			CEGem ceGem = gemSimple.getCEGem();
			CEGemData ceGemData = gemSimple.getCEGemData();

			Map<String, String> placeholder = ceGem.getPlaceholder(ceGemData);

			String display = ceGem.getData().getConfigData().getDisplay();

			PlaceholderBuilder builder = PlaceholderBuilder.builder().putAll(placeholder);
			display = builder.build().apply(display);

			String customDisplay = settings.getCustomGemLore();
			builder = PlaceholderBuilder.builder().put(CEConstants.Placeholder.GEM_DISPLAY, display);

			display = builder.build().apply(customDisplay);

			lore.add(display);

			CEGemDrillSimple suitableSlot = ceItem.getWeaponGem().getMinimumSuitableGemDrill(gemSimple, availableDrillList);
			if (suitableSlot != null) {
				availableDrillList.remove(suitableSlot);
			}
		}

		for (CEGemDrillSimple gemDrillSimple : availableDrillList) {
			CEGemDrill ceGemDrill = gemDrillSimple.getCEGemDrill();
			if (ceGemDrill == null) {
				continue;
			}

			CEGemDrillData.ConfigData configData = ceGemDrill.getData().getConfigData();

			String slot = configData.getSlot();
			CEGemSettings.SlotSettings slotSettings = CEGemSettings.getSettings().getSlotSettings(slot);
			if (slotSettings == null) {
				continue;
			}

			String customDisplay = settings.getCustomGemLore();
			PlaceholderBuilder builder = PlaceholderBuilder.builder().put(CEConstants.Placeholder.GEM_DISPLAY, slotSettings.getDisplay());

			String display = builder.build().apply(customDisplay);
			lore.add(display);
		}

		if (lore.isEmpty()) {
			return Arrays.asList(WeaponDisplay.REMOVE);
		}

		return lore;
	}
}

@AllArgsConstructor
class MiddleDefaultLore {
	private List<String> middleLore;

	public List<String> buildLores() {
		if (middleLore.isEmpty()) {
			return Arrays.asList(WeaponDisplay.REMOVE);
		}

		return middleLore;
	}
}

@AllArgsConstructor
class ExtraEnchantPoint {
	private WeaponData data;
	private WeaponSettings settings;

	public List<String> buildLores() {
		int point = data.getExtraEnchantPoint();
		String lore = settings.getEnchantPointLore(point);
		if (lore != null) {
			lore = lore.replace(CEConstants.Placeholder.AMOUNT, String.valueOf(point));
		}

		if (lore.isEmpty()) {
			return Arrays.asList(WeaponDisplay.REMOVE);
		}

		return Arrays.asList(lore);
	}
}

@AllArgsConstructor
class ExtraProtectDead {
	private WeaponData data;
	private WeaponSettings settings;

	public List<String> buildLores() {
		int point = data.getExtraProtectDead();
		String lore = settings.getProtectDeadLore(point);
		if (lore != null) {
			lore = lore.replace(CEConstants.Placeholder.AMOUNT, String.valueOf(point));
		}

		if (lore.isEmpty()) {
			return Arrays.asList(WeaponDisplay.REMOVE);
		}

		return Arrays.asList(lore);
	}
}

@AllArgsConstructor
class ExtraProtectDestroy {
	private WeaponData data;
	private WeaponSettings settings;

	public List<String> buildLores() {
		int point = data.getExtraProtectDestroy();
		String lore = settings.getProtectDestroyLore(point);
		if (lore != null) {
			lore = lore.replace(CEConstants.Placeholder.AMOUNT, String.valueOf(point));
		}

		if (lore.isEmpty()) {
			return Arrays.asList(WeaponDisplay.REMOVE);
		}

		return Arrays.asList(lore);
	}
}

@AllArgsConstructor
class AttributeLore {
	private WeaponSettings settings;
	private ItemStack itemStack;

	public List<String> buildLores() {
		List<String> lore = new ArrayList<String>();

		Map<NMSAttributeType, String> typeMap = settings.getAttributeTypeMap();
		Map<String, String> slotMap = settings.getAttributeSlotMap();

		INMSAttributeItem attributes = NMSManager.getAttributesProvider().getNMSAttributeItem().setItemStack(itemStack);
		DecimalFormat format = new DecimalFormat("#.##");

		for (String slot : slotMap.keySet()) {
			List<String> currentLore = new ArrayList<String>();

			for (NMSAttributeType type : typeMap.keySet()) {
				if (attributes.hasAttributeType(type, slot, NMSAttributeOperation.ADD_NUMBER)) {
					double amount = attributes.getValue(type, slot, NMSAttributeOperation.ADD_NUMBER);
					String operation = amount >= 0 ? "+" : "-";
					if (amount == 0) {
						continue;
					}

					if (type instanceof CustomAttributeType customAttributeType && customAttributeType.isPercent()) {
						currentLore.add(typeMap.get(type).replace(CEConstants.Placeholder.AMOUNT, format.format(Math.abs(amount)) + "%")
								.replace(CEConstants.Placeholder.OPERATION, operation));
					} else {
						currentLore.add(typeMap.get(type).replace(CEConstants.Placeholder.AMOUNT, format.format(Math.abs(amount)))
								.replace(CEConstants.Placeholder.OPERATION, operation));
					}
				}

				if (attributes.hasAttributeType(type, slot, NMSAttributeOperation.MULTIPLY_PERCENTAGE)) {
					double amount = attributes.getValue(type, slot, NMSAttributeOperation.MULTIPLY_PERCENTAGE);
					String operation = amount >= 0 ? "+" : "-";
					currentLore.add(typeMap.get(type).replace(CEConstants.Placeholder.AMOUNT, format.format(Math.abs(amount) * 100) + "%")
							.replace(CEConstants.Placeholder.OPERATION, operation));
				}

				if (attributes.hasAttributeType(type, slot, NMSAttributeOperation.ADD_PERCENTAGE)) {
					double amount = attributes.getValue(1, type, slot, NMSAttributeOperation.ADD_PERCENTAGE);
					amount -= 1; // Convert to percentage
					String operation = amount >= 0 ? "+" : "-";
					currentLore.add(typeMap.get(type).replace(CEConstants.Placeholder.AMOUNT, format.format(Math.abs(amount) * 100) + "%")
							.replace(CEConstants.Placeholder.OPERATION, operation));
				}
			}

			if (!currentLore.isEmpty()) {
				lore.add(slotMap.get(slot));
				lore.addAll(currentLore);

				// Add empty line between slot
				lore.add("");
			}

			// Remove last empty line
			if (!lore.isEmpty() && lore.get(lore.size() - 1).isEmpty()) {
				lore.remove(lore.size() - 1);
			}
		}

		if (lore.isEmpty()) {
			return Arrays.asList(WeaponDisplay.REMOVE);
		}

		return lore;
	}
}

@AllArgsConstructor
class EndDefaultLores {
	private List<String> endLore;

	public List<String> buildLores() {
		if (endLore.isEmpty()) {
			return Arrays.asList(WeaponDisplay.REMOVE);
		}

		return endLore;
	}
}