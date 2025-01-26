package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.*;
import com.bafmc.customenchantment.api.ITrade;
import com.bafmc.customenchantment.utils.AttributeUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WeaponAttribute extends CEItemExpansion implements ITrade<NMSNBTTagList> {
	private List<NMSAttribute> attributeList;

	public WeaponAttribute(CEWeaponAbstract ceItem) {
		super(ceItem);
	}

	public void addAttribute(String format) {
		addAttribute(AttributeUtils.createAttribute(format));
	}

	public void addAttribute(NMSAttribute attribute) {
		attributeList.add(attribute);
	}

	public void removeAttribute(int index) {
		attributeList.remove(index);
	}

	public void clearAttribute() {
		attributeList.clear();
	}

	public List<NMSAttribute> getAttributeList() {
		return attributeList;
	}

	public void importFrom(NMSNBTTagList source) {
		if (source == null) {
			return;
		}

		List<NMSNBTBase> compoundList = source.getList();

		this.attributeList = new ArrayList<NMSAttribute>();

		for (NMSNBTBase base : compoundList) {
			NMSAttribute attribute = new NMSAttribute((NMSNBTTagCompound) base);
			attributeList.add(attribute);
		}
	}

	public NMSNBTTagList exportTo() {
		NMSNBTTagList list = new NMSNBTTagList();

		for (NMSAttribute attribute : this.attributeList) {
			list.add(attribute.getData());
		}

		if (list.isEmpty()) {
			this.attributeList = new ArrayList<NMSAttribute>();
			ItemStack itemStack = ceItem.getDefaultItemStack();

			INMSAttributeItem attributeItem = NMSManager.getAttributesProvider().getNMSAttributeItem();

			for (NMSNBTBase base : attributeItem.getNMSNBTTagList(itemStack).getList()) {
				NMSAttribute attribute = new NMSAttribute((NMSNBTTagCompound) base);
				this.attributeList.add(attribute);
				list.add(attribute.getData());
			}
		}

		return list.isEmpty() ? null : list;
	}

	public boolean equals(WeaponAttribute weaponAttribute) {
		if (this.getAttributeList().size() != weaponAttribute.getAttributeList().size()) {
			return false;
		}

		for (int i = 0; i < this.getAttributeList().size(); i++) {
			NMSAttribute thisAttribute = this.getAttributeList().get(i);
			NMSAttribute thatAttribute = weaponAttribute.getAttributeList().get(i);

			if (thisAttribute.getAmount() != thatAttribute.getAmount()
					|| thisAttribute.getAttributeType() != thatAttribute.getAttributeType()
					|| !thisAttribute.getName().equals(thatAttribute.getName())
					|| thisAttribute.getOperation() != thatAttribute.getOperation()
					|| thisAttribute.getSlot() != thatAttribute.getSlot()) {
				return false;
			}
		}

		return true;
	}
}
