package me.texward.customenchantment.item;

import me.texward.customenchantment.api.ITrade;
import me.texward.texwardlib.util.nms.NMSNBTTagCompound;

public class WeaponData extends CEItemExpansion implements ITrade<NMSNBTTagCompound> {
	private int extraEnchantPoint;
	private int extraProtectDead;
	private int extraProtectDestroy;

	public WeaponData(CEWeaponAbstract ceItem) {
		super(ceItem);
	}
	
	public int getTotalEnchantPoint() {
		return getExtraEnchantPoint() + ceItem.getWeaponSettings().getEnchantPoint();
	}

	public int getExtraEnchantPoint() {
		return extraEnchantPoint;
	}

	public void setExtraEnchantPoint(int extraEnchantPoint) {
		this.extraEnchantPoint = extraEnchantPoint;
	}

	public int getExtraProtectDead() {
		return extraProtectDead;
	}

	public void setExtraProtectDead(int extraProtectDead) {
		this.extraProtectDead = extraProtectDead;
	}

	public int getExtraProtectDestroy() {
		return extraProtectDestroy;
	}

	public void setExtraProtectDestroy(int extraProtectDestroy) {
		this.extraProtectDestroy = extraProtectDestroy;
	}

	public void importFrom(NMSNBTTagCompound source) {
		if (source == null) {
			return;
		}

		this.extraEnchantPoint = source.getInt("extra_enchant_point");
		this.extraProtectDead = source.getInt("extra_protect_dead");
		this.extraProtectDestroy = source.getInt("extra_protect_destroy");
	}

	public NMSNBTTagCompound exportTo() {
		NMSNBTTagCompound compound = new NMSNBTTagCompound();

		if (extraEnchantPoint > 0) {
			compound.setInt("extra_enchant_point", extraEnchantPoint);
		}

		if (extraProtectDead > 0) {
			compound.setInt("extra_protect_dead", extraProtectDead);
		}

		if (extraProtectDestroy > 0) {
			compound.setInt("extra_protect_destroy", extraProtectDestroy);
		}

		return compound.isEmpty() ? null : compound;
	}
}
