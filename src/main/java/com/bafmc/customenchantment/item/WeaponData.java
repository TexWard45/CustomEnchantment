package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.NMSNBTBase;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagList;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagString;
import com.bafmc.customenchantment.api.ITrade;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WeaponData extends CEItemExpansion implements ITrade<NMSNBTTagCompound> {
	private int extraEnchantPoint;
	private int extraProtectDead;
	private int extraProtectDestroy;
	private List<String> extraEnchantPointList = new ArrayList<>();
	private List<String> extraProtectDeadList = new ArrayList<>();
	private List<String> extraProtectDestroyList = new ArrayList<>();

	public WeaponData(CEWeaponAbstract ceItem) {
		super(ceItem);
	}

	public void setExtraEnchantPoint(int extraEnchantPoint) {
		if (extraEnchantPoint < 0) {
			extraEnchantPoint = 0;
		}
		this.extraEnchantPoint = extraEnchantPoint;
	}

	public void setExtraProtectDead(int extraProtectDead) {
		if (extraProtectDead < 0) {
			extraProtectDead = 0;
		}
		this.extraProtectDead = extraProtectDead;
	}

	public void setExtraProtectDestroy(int extraProtectDestroy) {
		if (extraProtectDestroy < 0) {
			extraProtectDestroy = 0;
		}
		this.extraProtectDestroy = extraProtectDestroy;
	}
	
	public int getTotalEnchantPoint() {
		return getExtraEnchantPoint() + ceItem.getWeaponSettings().getEnchantPoint();
	}

	public void removeEnchantPointIndex(int index) {
		if (index < 0 || index >= extraEnchantPointList.size()) {
			return;
		}

		extraEnchantPointList.remove(index);
	}

	public void removeProtectDeadIndex(int index) {
		if (index < 0 || index >= extraProtectDeadList.size()) {
			return;
		}

		extraProtectDeadList.remove(index);
	}

	public void removeProtectDestroyIndex(int index) {
		if (index < 0 || index >= extraProtectDestroyList.size()) {
			return;
		}

		extraProtectDestroyList.remove(index);
	}

	public void importFrom(NMSNBTTagCompound source) {
		if (source == null) {
			return;
		}

		this.extraEnchantPoint = source.getInt("extra_enchant_point");
		this.extraProtectDead = source.getInt("extra_protect_dead");
		this.extraProtectDestroy = source.getInt("extra_protect_destroy");

		if (source.hasKey("extra_enchant_point_list")) {
			for (NMSNBTBase nbtBaseNMS : source.getList("extra_enchant_point_list").getList()) {
                if (!(nbtBaseNMS instanceof NMSNBTTagString)) {
					continue;
				}

				extraEnchantPointList.add(((NMSNBTTagString) nbtBaseNMS).getData());
			}
		}

		if (source.hasKey("extra_protect_dead_list")) {
			for (NMSNBTBase nbtBaseNMS : source.getList("extra_protect_dead_list").getList()) {
                if (!(nbtBaseNMS instanceof NMSNBTTagString)) {
					continue;
				}

				extraProtectDeadList.add(((NMSNBTTagString) nbtBaseNMS).getData());
			}
		}

		if (source.hasKey("extra_protect_destroy_list")) {
			for (NMSNBTBase nbtBaseNMS : source.getList("extra_protect_destroy_list").getList()) {
                if (!(nbtBaseNMS instanceof NMSNBTTagString)) {
					continue;
				}

				extraProtectDestroyList.add(((NMSNBTTagString) nbtBaseNMS).getData());
			}
		}
	}

	public NMSNBTTagCompound exportTo() {
		NMSNBTTagCompound compound = new NMSNBTTagCompound();

		if (extraEnchantPoint > 0) {
			compound.setInt("extra_enchant_point", extraEnchantPoint);
		}else {
			compound.setInt("extra_enchant_point", 0);
		}

		if (extraProtectDead > 0) {
			compound.setInt("extra_protect_dead", extraProtectDead);
		}else {
			compound.setInt("extra_protect_dead", 0);
		}

		if (extraProtectDestroy > 0) {
			compound.setInt("extra_protect_destroy", extraProtectDestroy);
		}else {
			compound.setInt("extra_protect_destroy", 0);
		}

		if (!extraEnchantPointList.isEmpty()) {
			NMSNBTTagList list = new NMSNBTTagList();
			for (String value : extraEnchantPointList) {
				list.addString(value);
			}
			compound.set("extra_enchant_point_list", list);
		}else {
			compound.remove("extra_enchant_point_list");
		}

		if (!extraProtectDeadList.isEmpty()) {
			NMSNBTTagList list = new NMSNBTTagList();
			for (String value : extraProtectDeadList) {
				list.addString(value);
			}
			compound.set("extra_protect_dead_list", list);
		}else {
			compound.remove("extra_protect_dead_list");
		}

		if (!extraProtectDestroyList.isEmpty()) {
			NMSNBTTagList list = new NMSNBTTagList();
			for (String value : extraProtectDestroyList) {
				list.addString(value);
			}
			compound.set("extra_protect_destroy_list", list);
		}else {
			compound.remove("extra_protect_destroy_list");
		}

		return compound.isEmpty() ? null : compound;
	}
}
