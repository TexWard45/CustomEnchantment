package com.bafmc.customenchantment.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.api.ITrade;
import com.bafmc.bukkit.bafframework.nms.NMSItemStack;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;

public class CEUnifyWeapon implements ITrade<NMSNBTTagCompound> {
	public enum Target {
		WEAPON, UNIFY;
	}

	private CEUnify ceUnify;
	private NMSNBTTagCompound weaponTag;
	private NMSNBTTagCompound unifyTag;

	public CEUnifyWeapon(CEUnify ceUnify) {
		this.ceUnify = ceUnify;
	}

	public boolean isSet() {
		return weaponTag != null && unifyTag != null && !weaponTag.isEmpty() && !unifyTag.isEmpty();
	}

	public void importFrom(NMSNBTTagCompound source) {
		if (source.isEmpty()) {
			return;
		}
		
		this.unifyTag = source.getCompound("unify-item");
		this.weaponTag = source.getCompound("weapon-item");
	}

	public NMSNBTTagCompound exportTo() {
		NMSNBTTagCompound tag = new NMSNBTTagCompound();
		if (unifyTag != null && !unifyTag.isEmpty()) {
			tag.set("unify-item", unifyTag);
		}

		if (weaponTag != null && !weaponTag.isEmpty()) {
			tag.set("weapon-item", weaponTag);
		}

		return tag;
	}

	public void setItemStack(Target target, ItemStack itemStack) {
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString("type", itemStack.getType().name());
		tag.setByte("amount", (byte) 1);

		NMSItemStack craftItemStack = new NMSItemStack(itemStack);

		tag.set("tag", craftItemStack.getCompound());

		if (target == Target.WEAPON) {
			this.weaponTag = tag;
		} else {
			this.unifyTag = tag;
		}
	}

	public ItemStack getItemStack(Target target) {
		NMSNBTTagCompound tag = target == Target.WEAPON ? weaponTag : unifyTag;

		if (tag == null || tag.isEmpty()) {
			return null;
		}

		Material type = Material.valueOf(tag.getString("type"));
		byte amount = tag.getByte("amount");
		short damage = tag.getShort("damage");

		NMSItemStack craftItemStack = new NMSItemStack(new ItemStack(type, amount, damage));
		craftItemStack.setCompound(tag.getCompound("tag"));
		
		if (target == Target.WEAPON) {
			return ceUnify.updateProtectDead(craftItemStack.getNewItemStack());
		}
		
		return craftItemStack.getNewItemStack();
	}
}