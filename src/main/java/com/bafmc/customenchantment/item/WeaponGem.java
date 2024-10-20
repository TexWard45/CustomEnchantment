package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.NMSNBTBase;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagList;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagString;
import com.bafmc.customenchantment.api.ITrade;

import java.util.ArrayList;
import java.util.List;

public class WeaponGem extends CEItemExpansion implements ITrade<NMSNBTTagList> {
	private List<CEGemSimple> gemList = new ArrayList<>();

	public WeaponGem(CEWeaponAbstract ceItem) {
		super(ceItem);
	}

	public boolean containsCEGemSimple(String name) {
		return getCEGemSimple(name) != null;
	}

	public void addCEGemSimple(CEGemSimple gemSimple) {
		if (!containsCEGemSimple(gemSimple.getName())) {
			gemList.add(gemSimple);
		}
	}

	public void forceAddCEGemSimple(CEGemSimple gemSimple) {
		removeCEGemSimple(gemSimple.getName());
		addCEGemSimple(gemSimple);
	}

	public void removeCEGemSimple(String name) {
		int index = indexOf(name);

		if (index != -1) {
			gemList.remove(index);
		}
	}

	public CEGemSimple getCEGemSimple(String name) {
		for (CEGemSimple gemSimple : gemList) {
			if (gemSimple.getName().equals(name)) {
				return gemSimple;
			}
		}
		return null;
	}

	public int indexOf(String name) {
		int i = 0;
		for (CEGemSimple gemSimple : gemList) {
			if (gemSimple.getName().equals(name)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public int getTotalGemPoint() {
		return gemList.size();
	}

	public boolean isEnoughGemPointForNextGem(CEGemSimple gemSimple) {
		return getTotalGemPoint() <= ceItem.getWeaponSettings().getGemPoint(ceItem.getDefaultItemStack());
	}

	public List<CEGemSimple> getCEGemSimpleList() {
		return new ArrayList<>(gemList);
	}

	public List<String> getCEGemSimpleNameList() {
		List<String> list = new ArrayList<String>();
		for (CEGemSimple gemSimple : gemList) {
			list.add(gemSimple.getName());
		}
		return list;
	}

	public void importFrom(NMSNBTTagList source) {
		if (source == null) {
			return;
		}

		for (NMSNBTBase nbtBaseNMS : source.getList()) {
			Object value = nbtBaseNMS;
			if (!(value instanceof NMSNBTTagString)) {
				continue;
			}

			value = ((NMSNBTTagString) value).getData();

			CEGemSimple gemSimple = new CEGemSimple(value.toString());
			gemList.add(gemSimple);
		}
	}

	public NMSNBTTagList exportTo() {
		if (gemList.isEmpty()) {
			return null;
		}

		NMSNBTTagList list = new NMSNBTTagList();

		for (CEGemSimple gemSimple : gemList) {
			list.addString(gemSimple.toLine());
		}

		return list;
	}
}
