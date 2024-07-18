package me.texward.customenchantment.item;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.texward.customenchantment.api.ITrade;
import me.texward.customenchantment.enchant.CEEnchant;
import me.texward.customenchantment.enchant.CEPlaceholder;
import me.texward.customenchantment.enchant.CESimple;
import me.texward.texwardlib.util.ColorUtils;
import me.texward.texwardlib.util.EnchantmentUtils;
import me.texward.texwardlib.util.RomanNumber;
import me.texward.texwardlib.util.StringListReplace;
import me.texward.texwardlib.util.StringReplace;
import me.texward.texwardlib.util.nms.INMSAttributeItem;
import me.texward.texwardlib.util.nms.NMSAttributeOperation;
import me.texward.texwardlib.util.nms.NMSAttributeType;
import me.texward.texwardlib.util.nms.NMSManager;
import me.texward.texwardlib.util.nms.NMSNBTTagCompound;
import me.texward.texwardlib.util.nms.NMSNBTTagList;

public class WeaponDisplay extends CEItemExpansion implements ITrade<NMSNBTTagCompound> {
	private List<String> beginLore = new ArrayList<String>();
	private List<String> middleLore = new ArrayList<String>();
	private List<String> endLore = new ArrayList<String>();

	public WeaponDisplay(CEWeaponAbstract ceItem) {
		super(ceItem);
	}

	public List<String> getNewLore(ItemStack itemStack, List<String> loreStyle) {
		WeaponSettings settings = ceItem.getWeaponSettings();
		WeaponData data = ceItem.getWeaponData();
		
		StringListReplace replace = new StringListReplace(loreStyle);

		replace.add(new StringReplace() {
			public List<String> getStringList(String line) {
				if (!line.equals("begin_default_lore")) {
					return null;
				}

				if (beginLore.isEmpty()) {
					setHeaderContinue(true);
					return null;
				} else {
					setFooterContinue(true);
					return beginLore;
				}
			}
		});

		replace.add(new StringReplace() {
			public List<String> getStringList(String line) {
				if (!line.equals("normal_enchant_lore")) {
					return null;
				}

				List<String> lore = new ArrayList<String>();

				Map<Enchantment, Integer> enchantMap = itemStack.getEnchantments();

				for (Enchantment enchantment : enchantMap.keySet()) {
					String vanillaLore = settings.getVanillaEnchantLore();
					vanillaLore = vanillaLore.replace("%enchant_display%", EnchantmentUtils.getDisplayName(enchantment));
					vanillaLore = vanillaLore.replace("%enchant_level%",
							RomanNumber.toRoman(enchantMap.get(enchantment)));
					lore.add(vanillaLore);
				}

				if (lore.isEmpty()) {
					setHeaderContinue(true);
					return null;
				} else {
					setFooterContinue(true);
					return lore;
				}
			}
		});

		replace.add(new StringReplace() {
			public List<String> getStringList(String line) {
				if (!line.equals("custom_enchant_lore")) {
					return null;
				}

				List<String> lore = new ArrayList<String>();
				List<CESimple> ceSimpleList = ceItem.getWeaponEnchant().getCESimpleListByPriority();
				for (CESimple ceSimple : ceSimpleList) {
					Map<String, String> placeholder = CEPlaceholder.getCESimplePlaceholder(ceSimple);
					placeholder.putAll(CEPlaceholder.getCEGroupPlaceholder(ceSimple.getCEEnchant().getCEGroup()));

					CEEnchant ceEnchant = ceSimple.getCEEnchant();
					
					if (ceEnchant.getCEDisplay().isDisableEnchantLore()) {
						continue;
					}

					String customDisplay = ceEnchant.getCEDisplay().getCustomDisplayFormat();
					if (customDisplay == null) {
						customDisplay = settings.getCustomEnchantLore();
					}

					lore.add(CEPlaceholder.setPlaceholder(customDisplay, placeholder));
				}

				if (lore.isEmpty()) {
					setHeaderContinue(true);
					return null;
				} else {
					setFooterContinue(true);
					return lore;
				}
			}
		});

		replace.add(new StringReplace() {
			public List<String> getStringList(String line) {
				if (!line.equals("middle_default_lore")) {
					return null;
				}

				if (middleLore.isEmpty()) {
					setHeaderContinue(true);
					return null;
				} else {
					setFooterContinue(true);
					return middleLore;
				}
			}
		});

		replace.add(new StringReplace() {
			public List<String> getStringList(String line) {
				if (!line.equals("extra_enchant_point")) {
					return null;
				}

				int point = data.getExtraEnchantPoint();
				String lore = settings.getEnchantPointLore(point);
				if (lore != null) {
					lore = lore.replace("%amount%", String.valueOf(point));
				}

				if (lore.isEmpty()) {
					setHeaderContinue(true);
					return null;
				} else {
					setFooterContinue(true);
					return Arrays.asList(lore);
				}
			}
		});

		replace.add(new StringReplace() {
			public List<String> getStringList(String line) {
				if (!line.equals("extra_protect_dead")) {
					return null;
				}

				int point = data.getExtraProtectDead();
				String lore = settings.getProtectDeadLore(point);
				if (lore != null) {
					lore = lore.replace("%amount%", String.valueOf(point));
				}

				if (lore.isEmpty()) {
					setHeaderContinue(true);
					return null;
				} else {
					setFooterContinue(true);
					return Arrays.asList(lore);
				}
			}
		});

		replace.add(new StringReplace() {
			public List<String> getStringList(String line) {
				if (!line.equals("extra_protect_destroy")) {
					return null;
				}

				int point = data.getExtraProtectDestroy();
				String lore = settings.getProtectDestroyLore(point);
				if (lore != null) {
					lore = lore.replace("%amount%", String.valueOf(point));
				}

				if (lore.isEmpty()) {
					setHeaderContinue(true);
					return null;
				} else {
					setFooterContinue(true);
					return Arrays.asList(lore);
				}
			}
		});

		replace.add(new StringReplace() {
			public List<String> getStringList(String line) {
				if (!line.equals("attribute_lore")) {
					return null;
				}

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
							currentLore.add(typeMap.get(type).replace("%amount%", format.format(amount)));
						}
						
						if (attributes.hasAttributeType(type, slot, NMSAttributeOperation.MULTIPLY_PERCENTAGE)) {
							double amount = attributes.getValue(type, slot, NMSAttributeOperation.MULTIPLY_PERCENTAGE);
							currentLore.add(typeMap.get(type).replace("%amount%", format.format(amount * 100) + "%"));
						}
						
						if (attributes.hasAttributeType(type, slot, NMSAttributeOperation.ADD_PERCENTAGE)) {
							double amount = attributes.getValue(type, slot, NMSAttributeOperation.ADD_PERCENTAGE);
							currentLore.add(typeMap.get(type).replace("%amount%", format.format(amount * 100) + "%"));
						}
					}
					
					if (!currentLore.isEmpty()) {
						lore.add(slotMap.get(slot));
						lore.addAll(currentLore);
						
						// Add empty line between slot
						lore.add("");
					}
				}
				
				// Remove last empty line
				if (!lore.isEmpty() && lore.get(lore.size() - 1).isEmpty()) {
					lore.remove(lore.size() - 1);
				}

				if (lore.isEmpty()) {
					setHeaderContinue(true);
					return null;
				} else {
					setFooterContinue(true);
					return lore;
				}
			}
		});

		replace.add(new StringReplace() {
			public List<String> getStringList(String line) {
				if (!line.equals("end_default_lore")) {
					return null;
				}

				if (endLore.isEmpty()) {
					setHeaderContinue(true);
					return null;
				} else {
					setFooterContinue(true);
					return endLore;
				}
			}
		});

		List<String> newLore = replace.build();
		for (int i = 0; i < newLore.size() - 1; i++) {
			if (newLore.get(i).isEmpty() && newLore.get(i + 1).isEmpty()) {
				newLore.remove(i);
			}

			if (newLore.get(i).isEmpty() && newLore.get(i + 1).indexOf("blank_lore") != -1) {
				newLore.remove(i);
			}
		}
		
		for (int i = 0; i < newLore.size() - 1; i++) {
			if (newLore.get(i).indexOf("blank_lore") != -1 && newLore.get(i + 1).isEmpty()) {
				newLore.remove(i + 1);
			}
			
			if (newLore.get(i).indexOf("lower_blank_lore") != -1 && newLore.get(i + 1).indexOf("lower_blank_lore") != -1) {
				newLore.remove(i + 1);
			}
			
			if (newLore.get(i).indexOf("upper_blank_lore") != -1 && newLore.get(i + 1).indexOf("upper_blank_lore") != -1) {
				newLore.remove(i + 1);
			}
		}
		
		// Trim
		while (!newLore.isEmpty() && (newLore.get(0).isEmpty() || newLore.get(0).equals("lower_blank_lore"))) {
			newLore.remove(0);
		}

		while (!newLore.isEmpty() && (newLore.get(newLore.size() - 1).isEmpty()
				|| newLore.get(newLore.size() - 1).equals("upper_blank_lore"))) {
			newLore.remove(newLore.size() - 1);
		}

		ListIterator<String> ite = newLore.listIterator();
		while (ite.hasNext()) {
			String next = ite.next();

			if (next.indexOf("blank_lore") != -1) {
				ite.set("");
			}
		}

		return ColorUtils.t(newLore);
	}

	public void apply(ItemStack itemStack) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		applyNewLore(itemStack, itemMeta);
		applyItemFlag(itemMeta);

		itemStack.setItemMeta(itemMeta);
	}

	public void applyNewLore(ItemStack itemStack, ItemMeta itemMeta) {
		List<String> newLore = getNewLore(itemStack, ceItem.getWeaponSettings().getLoreStyle());

		itemMeta.setLore(newLore);
	}

	public void applyItemFlag(ItemMeta itemMeta) {
		List<ItemFlag> itemFlag = ceItem.getWeaponSettings().getItemFlags();

		itemMeta.removeItemFlags(ItemFlag.values());
		itemMeta.addItemFlags(itemFlag.toArray(new ItemFlag[itemFlag.size()]));
	}

	public void importFrom(NMSNBTTagCompound source) {
		if (source == null || source.isEmpty()) {
			ItemStack itemStack = ceItem.getCurrentItemStack();
			
			if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) {
				return;
			}
			List<String> lore = itemStack.getItemMeta().getLore();

			for (String line : lore) {
				if (line.startsWith("§b§r")) {
					this.beginLore.add(line);
				} else if (line.startsWith("§e§r")) {
					this.endLore.add(line);
				} else {
					this.middleLore.add(line);
				}
			}
		} else {
			this.beginLore = source.getList("begin").getStringList();
			this.middleLore = source.getList("middle").getStringList();
			this.endLore = source.getList("end").getStringList();
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
