package me.texward.customenchantment.item;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CESimple;
import me.texward.customenchantment.item.CEUnifyWeapon.Target;
import me.texward.customenchantment.nms.CECraftItemStackNMS;
import me.texward.texwardlib.util.MaterialUtils;
import me.texward.texwardlib.util.nms.NMSManager;
import me.texward.texwardlib.util.nms.NMSNBTTagCompound;
import me.texward.texwardlib.util.nms.NMSNBTTagList;

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
		NMSNBTTagCompound unifyTag = this.getCraftItemStack().getCompound();
		this.updateWeaponTagFromUnifyTag(weaponTag, unifyTag);
		this.getCraftItemStack().setCompound(weaponTag);

		this.importFrom(this.getCurrentItemStack());
		unifyWeapon.setItemStack(Target.WEAPON, defaultItemStack);
		unifyWeapon.setItemStack(Target.UNIFY, unifyItemStack);

		for (CESimple ceSimple : enchant.getCESimpleList()) {
			this.getWeaponEnchant().addCESimple(ceSimple);
		}

		updateArmorAttribute(defaultItemStack, this.getCraftItemStack());
		updateDisplay(defaultItemStack.getType(), this.getCraftItemStack());
		return this;
	}

	private void updateDisplay(Material armorType, CECraftItemStackNMS craftItemStack) {
		ItemStack itemStack = craftItemStack.getNewItemStack();
		
		ItemMeta meta = itemStack.getItemMeta();
		
		if (!meta.hasDisplayName()) {
			String display = getDisplay(MaterialUtils.getDisplayName(armorType));
			meta.setDisplayName(display);
		}else {
			String display = getDisplay(meta.getDisplayName());
			meta.setDisplayName(display);
		}
		
		if (meta.hasLore()) {
			List<String> lore = new ArrayList<String>(meta.getLore());
			
			ListIterator<String> ite = lore.listIterator();
			
			while(ite.hasNext()) {
				String line = ite.next();
				
				if (line.startsWith("§r§r")) {
					ite.remove();
				}
			}
			meta.setLore(lore);
		}
		
		itemStack.setItemMeta(meta);
		craftItemStack.setCompound(new CECraftItemStackNMS(itemStack).getCompound());
	}
	
	public void updateWeaponTagFromUnifyTag(NMSNBTTagCompound weaponTag, NMSNBTTagCompound unifyTag) {
		if (unifyTag.hasKey("SkullOwner")) {
			weaponTag.set("SkullOwner", unifyTag.get("SkullOwner"));
		}
		
		if (unifyTag.hasKey("BlockEntityTag")) {
			weaponTag.set("BlockEntityTag", unifyTag.get("BlockEntityTag"));
		}
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
