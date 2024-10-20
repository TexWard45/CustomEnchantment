package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.NMSManager;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagList;
import com.bafmc.bukkit.bafframework.utils.MaterialUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CESimple;
import com.bafmc.customenchantment.item.CEUnifyWeapon.Target;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class CEUnify<T extends CEUnifyData> extends CEWeaponAbstract<T> {
	protected CEUnifyWeapon unifyWeapon;

	public CEUnify(String type, ItemStack itemStack) {
		super(type, itemStack);
	}

	public void importFrom(ItemStack itemStack) {
		unifyImportFrom(itemStack);
	}
	
	@SuppressWarnings("unchecked")
	public void unifyImportFrom(ItemStack itemStack) {
		super.importFrom(itemStack);

		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		this.unifyWeapon = new CEUnifyWeapon(this);
		this.unifyWeapon.importFrom(tag.getCompound("unify-data"));

		String pattern = tag.getString(CENBT.PATTERN);

		CEUnify item = (CEUnify) CustomEnchantment.instance().getCEItemStorageMap().get(type).get(pattern);

		if (item != null) {
			setData((T) item.getData());
		}
	}

	public ItemStack exportTo() {
		return unifyExportTo();
	}
	
	public ItemStack unifyExportTo() {
		ItemStack itemStack = super.exportTo();
		
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(itemStack);
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		NMSNBTTagCompound unifyWeaponTag = unifyWeapon.exportTo();
		if (!unifyWeaponTag.isEmpty()) {
			tag.set("unify-data", unifyWeapon.exportTo());
		}
		
		if (data != null) {
			tag.setString(CENBT.PATTERN, data.getPattern());
		}

		itemStackNMS.setCETag(tag);
		return itemStackNMS.getNewItemStack();
	}

	public CEUnify convertWeaponToMask(CEWeaponAbstract weapon) {
		WeaponEnchant enchant = this.getWeaponEnchant();

		ItemStack defaultItemStack = weapon.getDefaultItemStack();
		ItemStack unifyItemStack = this.getDefaultItemStack();

		// Update NBT from weapon to unify
		NMSNBTTagCompound weaponTag = weapon.getCraftItemStack().getCompound();
		this.getCraftItemStack().setCompound(weaponTag);

		this.importFrom(this.getCurrentItemStack());
		unifyWeapon.setItemStack(Target.WEAPON, defaultItemStack);
		unifyWeapon.setItemStack(Target.UNIFY, unifyItemStack);

		for (CESimple ceSimple : enchant.getCESimpleList()) {
			this.getWeaponEnchant().addCESimple(ceSimple);
		}

		updateArmorAttribute(defaultItemStack, this.getCraftItemStack());
		updateDisplay(defaultItemStack.getType(), this.getCraftItemStack(), weapon.getCraftItemStack());
		return this;
	}

	private void updateDisplay(Material armorType, CECraftItemStackNMS craftItemStack, CECraftItemStackNMS weaponCraftItemStack) {
		ItemStack itemStack = craftItemStack.getNewItemStack();
		ItemStack weaponItemStack = weaponCraftItemStack.getNewItemStack();
		ItemMeta meta = itemStack.getItemMeta();
		ItemMeta weaponMeta = weaponItemStack.getItemMeta();
		
		if (!weaponMeta.hasDisplayName()) {
			String display = getDisplay(MaterialUtils.getDisplayName(armorType));
			meta.setDisplayName(display);
		}else {
			String display = getDisplay(weaponItemStack.getItemMeta().getDisplayName());
			meta.setDisplayName(display);
		}
		
		itemStack.setItemMeta(meta);

		CECraftItemStackNMS nms = new CECraftItemStackNMS(itemStack);
		setCraftItemStack(nms);
	}
	
	public String getDisplay(String display) {
		return display;
	}

	private void updateArmorAttribute(ItemStack itemStack, CECraftItemStackNMS craftItemStack) {
		NMSNBTTagCompound tag = craftItemStack.getCompound();
		NMSNBTTagList list = tag.getList("AttributeModifiers");
		if (list != null && !list.isEmpty()) {
			return;
		}
		list = NMSManager.getAttributesProvider().getNMSAttributeItem().getNMSNBTTagList(itemStack);

		tag.set("AttributeModifiers", list);
		craftItemStack.setCompound(tag);
	}

	public ApplyReason applyTo(CEItem ceItem) {
		if (this.unifyWeapon.isSet()) {
			return ApplyReason.CANCEL;
		}
		
		this.getCraftItemStack().getNewItemStack().setAmount(1);
		ceItem.getCraftItemStack().getNewItemStack().setAmount(1);
		
		CEWeaponAbstract weaponAbstract = (CEWeaponAbstract) ceItem;
		CEUnify unifyTarget = convertWeaponToMask(weaponAbstract);
		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		
		reason.setSource(unifyTarget);
		return reason;
	}
	
	public ItemStack updateProtectDead(ItemStack itemStack) {
		CEItem ceItem = CEAPI.getCEItem(itemStack);
		
		if (!(ceItem instanceof CEWeaponAbstract)) {
			return itemStack;
		}
		
		CEWeaponAbstract weapon = (CEWeaponAbstract) ceItem;
		weapon.getWeaponData().setExtraProtectDead(getWeaponData().getExtraProtectDead());
		
		return weapon.exportTo();
	}

	public CEUnifyWeapon getUnifyWeapon() {
		return unifyWeapon;
	}

}
