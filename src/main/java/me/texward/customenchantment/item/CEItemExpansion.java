package me.texward.customenchantment.item;

public abstract class CEItemExpansion {
	protected CEWeaponAbstract ceItem;

	public CEItemExpansion(CEWeaponAbstract ceItem) {
		this.ceItem = ceItem;
	}

	public CEWeaponAbstract getCEItem() {
		return ceItem;
	}
}