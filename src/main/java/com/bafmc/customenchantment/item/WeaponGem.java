package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.NMSNBTBase;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagList;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagString;
import com.bafmc.customenchantment.api.ITrade;
import com.bafmc.customenchantment.item.gem.CEGemData;
import com.bafmc.customenchantment.item.gem.CEGemSimple;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrill;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrillData;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrillSimple;

import java.util.ArrayList;
import java.util.List;

public class WeaponGem extends CEItemExpansion implements ITrade<NMSNBTTagCompound> {
	private List<CEGemSimple> gemList = new ArrayList<>();
	private List<CEGemDrillSimple> gemDrillList = new ArrayList<>();

	public WeaponGem(CEWeaponAbstract ceItem) {
		super(ceItem);
	}

	public void addCEGemSimple(CEGemSimple gemSimple) {
		gemList.add(gemSimple);
	}

	public void addCEGemDrillSimple(CEGemDrillSimple gemDrillSimple) {
		gemDrillList.add(gemDrillSimple);
	}

	public void removeCEGemDrillSimple(int index) {
		if (index < 0 || index >= gemDrillList.size()) {
			return;
		}

		gemDrillList.remove(index);
	}

	public void removeCEGemSimple(int index) {
		if (index < 0 || index >= gemList.size()) {
			return;
		}

		gemList.remove(index);
	}

	public int getGemTypeCount(String name) {
		int count = 0;
		for (CEGemSimple gemSimple : gemList) {
			if (gemSimple.getName().equals(name)) {
				count++;
			}
		}
		return count;
	}

	public List<CEGemDrillSimple> getAvailableGemDrillList() {
		return new ArrayList<>(gemDrillList);
	}

	public CEGemDrillSimple getSuitableGemDrill(CEGemSimple ceGemSimple) {
		List<CEGemDrillSimple> availableGemDrillList = getAvailableGemDrillList();

		for (CEGemSimple gemSimple : gemList) {
			CEGemDrillSimple gemDrillSimple = getMinimumSuitableGemDrill(gemSimple, availableGemDrillList);
			if (gemDrillSimple == null) {
				return null;
			}
			availableGemDrillList.remove(gemDrillSimple);
		}

		return getMinimumSuitableGemDrill(ceGemSimple, availableGemDrillList);
	}

	public CEGemDrillSimple getMinimumSuitableGemDrill(CEGemSimple ceGemSimple, List<CEGemDrillSimple> availableGemDrillList) {
		CEGemData.ConfigData gemConfigData = ceGemSimple.getCEGemData().getConfigData();
		List<String> slotList = gemConfigData.getAppliesSlot();

		for (String gemSlot : slotList) {
			for (CEGemDrillSimple gemDrillSimple : availableGemDrillList) {
				CEGemDrill gemDrill = gemDrillSimple.getCEGemDrill();
				CEGemDrillData.ConfigData configData = gemDrill.getData().getConfigData();

				if (gemSlot.equals(configData.getSlot())) {
					return gemDrillSimple;
				}
			}
		}

		return null;
	}

	public CEGemSimple getCEGemSimple(String name) {
		for (CEGemSimple gemSimple : gemList) {
			if (gemSimple.getName().equals(name)) {
				return gemSimple;
			}
		}
		return null;
	}

	public int getTotalGemPoint() {
		return gemList.size();
	}

	public int getDrillSize() {
		return gemDrillList.size();
	}

	public boolean isEnoughGemPointForNextGem(CEGemSimple gemSimple) {
		return getTotalGemPoint() <= ceItem.getWeaponSettings().getGemPoint(ceItem.getDefaultItemStack()) + gemDrillList.size();
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

	public void importFrom(NMSNBTTagCompound source) {
		if (source == null) {
			return;
		}

		if (source.hasKey("gem_drill_list")) {
			for (NMSNBTBase nbtBaseNMS : source.getList("gem_drill_list").getList()) {
				if (!(nbtBaseNMS instanceof NMSNBTTagString)) {
					continue;
				}

				CEGemDrillSimple gemDrill = new CEGemDrillSimple();
				gemDrill.fromLine(((NMSNBTTagString) nbtBaseNMS).getData());
				gemDrillList.add(gemDrill);
			}
		}

		if (source.hasKey("gem_list")) {
			for (NMSNBTBase nbtBaseNMS : source.getList("gem_list").getList()) {
				if (!(nbtBaseNMS instanceof NMSNBTTagString)) {
					continue;
				}

				gemList.add(new CEGemSimple(((NMSNBTTagString) nbtBaseNMS).getData()));
			}
		}
	}

	public NMSNBTTagCompound exportTo() {
		NMSNBTTagCompound compound = new NMSNBTTagCompound();

		if (!gemDrillList.isEmpty()) {
			NMSNBTTagList list = new NMSNBTTagList();
			for (CEGemDrillSimple gemDrill : gemDrillList) {
				list.addString(gemDrill.toLine());
			}
			compound.set("gem_drill_list", list);
		}else {
			compound.remove("gem_drill_list");
		}

		if (!gemList.isEmpty()) {
			NMSNBTTagList list = new NMSNBTTagList();
			for (CEGemSimple gemSimple : gemList) {
				list.addString(gemSimple.toLine());
			}
			compound.set("gem_list", list);
		}else {
			compound.remove("gem_list");
		}

		return compound;
	}
}
