package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.api.ITrade;
import org.bukkit.inventory.ItemStack;

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
		tag.setString("item-stack", ItemStackUtils.toString(itemStack));

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

		ItemStack itemStack = ItemStackUtils.fromString(tag.getString("item-stack"));
		if (target == Target.WEAPON) {
			return ceUnify.updateProtectDead(itemStack);
		}
		
		return itemStack;
	}
}