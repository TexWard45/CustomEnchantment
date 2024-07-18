package me.texward.customenchantment.item;

import java.util.ArrayList;
import java.util.List;

import me.texward.customenchantment.api.ITrade;
import me.texward.customenchantment.enchant.CEEnchant;
import me.texward.customenchantment.enchant.CESimple;
import me.texward.customenchantment.enchant.Priority;
import me.texward.texwardlib.util.nms.NMSNBTBase;
import me.texward.texwardlib.util.nms.NMSNBTTagList;
import me.texward.texwardlib.util.nms.NMSNBTTagString;

public class WeaponEnchant extends CEItemExpansion implements ITrade<NMSNBTTagList> {
	private List<CESimple> ceSimpleList = new ArrayList<CESimple>();

	public WeaponEnchant(CEWeaponAbstract ceItem) {
		super(ceItem);
	}

	public boolean containsCESimple(String name) {
		return getCESimple(name) != null;
	}

	public void addCESimple(CESimple ceSimple) {
		if (!containsCESimple(ceSimple.getName())) {
			ceSimpleList.add(ceSimple);
		}
	}

	public void forceAddCESimple(CESimple ceSimple) {
		removeCESimple(ceSimple.getName());
		addCESimple(ceSimple);
	}

	public void removeCESimple(String name) {
		int index = indexOf(name);

		if (index != -1) {
			ceSimpleList.remove(index);
		}
	}

	public CESimple getCESimple(String name) {
		for (CESimple ceSimple : ceSimpleList) {
			if (ceSimple.getName().equals(name)) {
				return ceSimple;
			}
		}
		return null;
	}

	public int indexOf(String name) {
		int i = 0;
		for (CESimple ceSimple : ceSimpleList) {
			if (ceSimple.getName().equals(name)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public int getTotalEnchantPoint() {
		int point = 0;
		for (CESimple ceSimple : ceSimpleList) {
			point += ceSimple.getCEEnchant().getEnchantPoint();
		}
		return point;
	}

	public boolean isEnoughEnchantPointForNextCE(CESimple ceSimple) {
		return getTotalEnchantPoint() + ceSimple.getCEEnchant().getEnchantPoint() <= ceItem.getWeaponData().getTotalEnchantPoint();
	}

	public List<CESimple> getCESimpleList() {
		return new ArrayList<CESimple>(ceSimpleList);
	}

	public List<CESimple> getCESimpleListByPriority() {
		List<CESimple> ceSimpleList = getCESimpleList();
		List<CESimple> newCESimpleList = new ArrayList<CESimple>();

		for (Priority priority : Priority.HIGH_TO_LOW) {
			for (int i = 0; i < ceSimpleList.size(); i++) {
				CESimple ceSimple = ceSimpleList.get(i);
				CEEnchant ceEnchant = ceSimple.getCEEnchant();
				
				if (ceEnchant != null && ceEnchant.getCEGroup().getPriority() == priority) {
					newCESimpleList.add(ceSimpleList.remove(i--));
				}
			}
		}

		return newCESimpleList;
	}

	public List<String> getCESimpleNameList() {
		List<String> list = new ArrayList<String>();
		for (CESimple ceSimple : ceSimpleList) {
			list.add(ceSimple.getName());
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

			CESimple ceSimple = new CESimple(value.toString());
			ceSimpleList.add(ceSimple);
		}
	}

	public NMSNBTTagList exportTo() {
		if (ceSimpleList.isEmpty()) {
			return null;
		}

		NMSNBTTagList list = new NMSNBTTagList();

		for (CESimple ceSimple : ceSimpleList) {
			list.addString(ceSimple.toLine());
		}

		return list;
	}
}
