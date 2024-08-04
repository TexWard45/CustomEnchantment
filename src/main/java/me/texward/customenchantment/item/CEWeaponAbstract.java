package me.texward.customenchantment.item;

import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.nms.CECraftItemStackNMS;
import me.texward.texwardlib.util.nms.NMSNBTTagCompound;
import me.texward.texwardlib.util.nms.NMSNBTTagList;

public abstract class CEWeaponAbstract<T extends CEItemData> extends CEItem<T> {
	private WeaponEnchant weaponEnchant;
	private WeaponDisplay weaponLore;
	private WeaponData weaponData;
	private WeaponAttribute weaponAttribute;
	private long lastTimeModifier;
	private long newTimeModifier;
	protected String weaponSettingsName;
	private int repairCost;

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

		this.weaponLore = new WeaponDisplay(this);
		this.weaponLore.importFrom(tag.getCompound("lore"));

		this.weaponData = new WeaponData(this);
		this.weaponData.importFrom(tag.getCompound("data"));
		
		this.weaponAttribute = new WeaponAttribute(this);
		
		NMSNBTTagCompound compound = itemStackNMS.getCompound();
		this.weaponAttribute.importFrom(compound.getList("AttributeModifiers"));
		this.repairCost = compound.getInt("RepairCost", 0);
		
		this.lastTimeModifier = this.newTimeModifier = tag.getLong("time");
	}
	
	public ItemStack exportTo() {
		return weaponAbstractExportTo();
	}

	public ItemStack weaponAbstractExportTo() {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		
		NMSNBTTagCompound tag = new NMSNBTTagCompound();
		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.SETTINGS, weaponSettingsName);

		NMSNBTTagList enchantTag = weaponEnchant.exportTo();
		if (enchantTag != null) {
			tag.set("enchant", enchantTag);
		}

		NMSNBTTagCompound loreTag = weaponLore.exportTo();
		if (loreTag != null) {
			tag.set("lore", loreTag);
		}

		NMSNBTTagCompound dataTag = weaponData.exportTo();
		if (dataTag != null) {
			tag.set("data", dataTag);
		}

		updateTimeModifierTag(tag);

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}
		
		tag = itemStackNMS.getCompound();
		if (repairCost > 0) {
			tag.setInt("RepairCost", repairCost);
		}else {
			tag.remove("RepairCost");
		}
		NMSNBTTagList attributeTag = weaponAttribute.exportTo();
		if (attributeTag != null) {
			tag.set("AttributeModifiers", attributeTag);
		}else {
			tag.remove("AttributeModifiers");
		}
		
		itemStackNMS.setCompound(tag);

		ItemStack itemStack = itemStackNMS.getNewItemStack();
//		if (!itemStack.isSimilar(getDefaultItemStack())) {
//			tag.setLong("time", updateTimeModifier());
//			itemStackNMS.setCETag(tag);
//			itemStack = itemStackNMS.getCurrentVanillaItemStack();
//		}

		// Apply new display item
		weaponLore.apply(itemStack);
		return itemStack;
	}
	
	public void updateTimeModifierTag(NMSNBTTagCompound tag) {
		if (this.newTimeModifier > 0 && newTimeModifier != this.lastTimeModifier) {
			tag.setLong("time", this.newTimeModifier);
		}
	}

	public long updateTimeModifier() {
		return this.newTimeModifier = System.currentTimeMillis();
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

	public long getLastTimeModifier() {
		return lastTimeModifier;
	}

	public long getNewTimeModifier() {
		return lastTimeModifier;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWeaponSettingsName() {
		return weaponSettingsName;
	}

    public void setWeaponSettingsName(String weaponSettingsName) {
        this.weaponSettingsName = weaponSettingsName;
    }

	public WeaponSettings getWeaponSettings() {
		return WeaponSettings.getSettings(getWeaponSettingsName());
	}

	public WeaponEnchant getWeaponEnchant() {
		return weaponEnchant;
	}

	public WeaponAttribute getWeaponAttribute() {
		return weaponAttribute;
	}

	public WeaponDisplay getWeaponDisplay() {
		return weaponLore;
	}

	public WeaponData getWeaponData() {
		return weaponData;
	}
}
