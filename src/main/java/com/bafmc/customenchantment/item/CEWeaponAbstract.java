package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagList;
import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

public abstract class CEWeaponAbstract<T extends CEItemData> extends CEItem<T> {
	@Getter
    private WeaponEnchant weaponEnchant;
	@Getter
    private WeaponDisplay weaponDisplay;
	@Getter
	private WeaponGem weaponGem;
	@Getter
    private WeaponData weaponData;
	@Getter
    private WeaponAttribute weaponAttribute;
	@Getter
    private long lastTimeModifier;
	private long newTimeModifier;
	@Setter
    @Getter
    protected String weaponSettingsName;
	private int repairCost;
	private boolean removePattern;
	@Getter
	@Setter
	private CEWeaponType weaponType;

	public CEWeaponAbstract(String type, ItemStack itemStack) {
		super(type, itemStack);
	}

	public static CEWeaponAbstract getCEWeapon(ItemStack itemStack) {
		try {
			return (CEWeaponAbstract) CEAPI.getCEItem(itemStack);
		} catch (Exception e) {
			return null;
		}
	}

	public void importFrom(ItemStack itemStack) {
		weaponAbstractImportFrom(itemStack);
	}
	
	public void weaponAbstractImportFrom(ItemStack itemStack) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(itemStack);
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		this.weaponSettingsName = tag.getString(CENBT.SETTINGS);
		if (this.weaponSettingsName.isEmpty()) {
			this.weaponSettingsName = CENBT.DEFAULT;
		}

		this.weaponEnchant = new WeaponEnchant(this);
		this.weaponEnchant.importFrom(tag.getList("enchant"));

		this.weaponGem = new WeaponGem(this);
		this.weaponGem.importFrom(tag.getCompound("gem"));

		this.weaponDisplay = new WeaponDisplay(this);
		this.weaponDisplay.importFrom(tag.getCompound("lore"));

		this.weaponData = new WeaponData(this);
		this.weaponData.importFrom(tag.getCompound("data"));
		
		this.weaponAttribute = new WeaponAttribute(this);
		
		NMSNBTTagCompound compound = itemStackNMS.getCompound();
		this.weaponAttribute.importFrom(compound.getList("AttributeModifiers"));
		this.repairCost = itemStackNMS.getRepairCost();
		
		this.lastTimeModifier = this.newTimeModifier = tag.getLong("time");

		if (tag.hasKey(CENBT.CUSTOM_TYPE)) {
			this.weaponType = EnumUtils.valueOf(CEWeaponType.class, tag.getString(CENBT.CUSTOM_TYPE));
		} else {
			this.weaponType = null;
		}
	}
	
	public ItemStack exportTo() {
		return weaponAbstractExportTo();
	}

	public ItemStack weaponAbstractExportTo() {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();
		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.SETTINGS, weaponSettingsName);

		if (this.weaponType != null) {
			tag.setString(CENBT.CUSTOM_TYPE, this.weaponType.name());
		}else {
			tag.remove(CENBT.CUSTOM_TYPE);
		}

		NMSNBTTagList enchantTag = weaponEnchant.exportTo();
		if (enchantTag != null) {
			tag.set("enchant", enchantTag);
		}else {
			tag.remove("enchant");
		}

		NMSNBTTagCompound gemTag = weaponGem.exportTo();
		if (gemTag != null) {
			tag.set("gem", gemTag);
		}else {
			tag.remove("gem");
		}

		NMSNBTTagCompound loreTag = weaponDisplay.exportTo();
		if (loreTag != null) {
			tag.set("lore", loreTag);
		}else {
			tag.remove("lore");
		}

		NMSNBTTagCompound dataTag = weaponData.exportTo();
		if (dataTag != null) {
			tag.set("data", dataTag);
		}else {
			tag.remove("data");
		}

		if (removePattern) {
			tag.remove(CENBT.PATTERN);
		}

		updateTimeModifierTag(tag);

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		tag = itemStackNMS.getCompound();
		NMSNBTTagList attributeTag = weaponAttribute.exportTo();
		if (attributeTag != null) {
			tag.set("AttributeModifiers", attributeTag);
		} else {
			tag.remove("AttributeModifiers");
		}

		itemStackNMS.setCompound(tag);
		itemStackNMS.setRepairCost(repairCost);
		if (attributeTag != null) {
			itemStackNMS.setAttribute(attributeTag);
		}

		ItemStack itemStack = itemStackNMS.getNewItemStack();

		// Apply new display item
		weaponDisplay.apply(itemStack);
		return itemStack;
	}
	
	public void updateTimeModifierTag(NMSNBTTagCompound tag) {
		if (this.newTimeModifier > 0 && newTimeModifier != this.lastTimeModifier) {
			tag.setLong("time", this.newTimeModifier);
		}
	}

	public void updateTimeModifier() {
		this.newTimeModifier = System.currentTimeMillis();
	}
	
	public void clearTimeModifier() {
		this.newTimeModifier = 0;
	}
	
	public void clearRepairCost() {
		this.repairCost = 0;
	}
	
	public void clearAttribute() {
		this.weaponAttribute.clearAttribute();
	}

	public void clearPattern() {
		this.removePattern = true;
	}

	public void setType(String type) {
		this.type = type;
	}

    public WeaponSettings getWeaponSettings() {
		return WeaponSettings.getSettings(getWeaponSettingsName());
	}

	public String getWeaponTypeName() {
		return weaponType != null ? weaponType.name() : null;
	}
}
