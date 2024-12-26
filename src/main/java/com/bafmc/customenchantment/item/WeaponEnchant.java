package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.api.ITrade;
import com.bafmc.customenchantment.enchant.CEEnchant;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.enchant.Priority;
import com.bafmc.bukkit.bafframework.nms.NMSNBTBase;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagList;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagString;

import java.util.ArrayList;
import java.util.List;

public class WeaponEnchant extends CEItemExpansion implements ITrade<NMSNBTTagList> {
	private List<CEEnchantSimple> ceEnchantSimpleList = new ArrayList<CEEnchantSimple>();

	public WeaponEnchant(CEWeaponAbstract ceItem) {
		super(ceItem);
	}

	public boolean containsCESimple(String name) {
		return getCESimple(name) != null;
	}

    public boolean hasEnchantBlacklist(CEEnchantSimple ceEnchantSimple) {
        List<String> enchantBlacklist = ceEnchantSimple.getCEEnchant().getEnchantBlacklist();

        for (CEEnchantSimple otherCEEnchantSimple : ceEnchantSimpleList) {
            CEEnchant otherCEEnchant = otherCEEnchantSimple.getCEEnchant();

            if (otherCEEnchant.getEnchantBlacklist().contains(ceEnchantSimple.getName())) {
                return true;
            }

            if (enchantBlacklist.contains(otherCEEnchantSimple.getName())) {
                return true;
            }
        }

        return false;
    }

    public CEEnchantSimple getEnchantBlacklist(CEEnchantSimple ceEnchantSimple) {
        List<String> enchantBlacklist = ceEnchantSimple.getCEEnchant().getEnchantBlacklist();
        for (CEEnchantSimple otherCEEnchantSimple : ceEnchantSimpleList) {
            CEEnchant otherCEEnchant = otherCEEnchantSimple.getCEEnchant();

            if (otherCEEnchant.getEnchantBlacklist().contains(ceEnchantSimple.getName())) {
                return otherCEEnchantSimple;
            }

            if (enchantBlacklist.contains(otherCEEnchantSimple.getName())) {
                return otherCEEnchantSimple;
            }
        }

        return null;
    }

	public void addCESimple(CEEnchantSimple ceEnchantSimple) {
		if (ceEnchantSimple.getName() == null) {
			return;
		}

		if (!containsCESimple(ceEnchantSimple.getName())) {
			ceEnchantSimpleList.add(ceEnchantSimple);
		}
	}

	public void forceAddCESimple(CEEnchantSimple ceEnchantSimple) {
		removeCESimple(ceEnchantSimple.getName());
		addCESimple(ceEnchantSimple);
	}

	public void removeCESimple(String name) {
		int index = indexOf(name);

		if (index != -1) {
			ceEnchantSimpleList.remove(index);
		}
	}

	public CEEnchantSimple getCESimple(String name) {
		for (CEEnchantSimple ceEnchantSimple : ceEnchantSimpleList) {
			if (ceEnchantSimple.getName().equals(name)) {
				return ceEnchantSimple;
			}
		}
		return null;
	}

	public int indexOf(String name) {
		int i = 0;
		for (CEEnchantSimple ceEnchantSimple : ceEnchantSimpleList) {
			if (ceEnchantSimple.getName().equals(name)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public int getTotalEnchantPoint() {
		int point = 0;
		for (CEEnchantSimple ceEnchantSimple : ceEnchantSimpleList) {
			point += ceEnchantSimple.getCEEnchant().getEnchantPoint();
		}
		return point;
	}

	public boolean isEnoughEnchantPointForNextCE(CEEnchantSimple ceEnchantSimple) {
		return getTotalEnchantPoint() + ceEnchantSimple.getCEEnchant().getEnchantPoint() <= ceItem.getWeaponData().getTotalEnchantPoint();
	}

	public List<CEEnchantSimple> getCESimpleList() {
		return new ArrayList<>(ceEnchantSimpleList);
	}

	public List<CEEnchantSimple> getCESimpleListByPriority() {
		List<CEEnchantSimple> ceEnchantSimpleList = getCESimpleList();
		List<CEEnchantSimple> newCEEnchantSimpleList = new ArrayList<CEEnchantSimple>();

		for (Priority priority : Priority.HIGH_TO_LOW) {
			for (int i = 0; i < ceEnchantSimpleList.size(); i++) {
				CEEnchantSimple ceEnchantSimple = ceEnchantSimpleList.get(i);
				CEEnchant ceEnchant = ceEnchantSimple.getCEEnchant();
				
				if (ceEnchant != null && ceEnchant.getCEGroup().getPriority() == priority) {
					newCEEnchantSimpleList.add(ceEnchantSimpleList.remove(i--));
				}
			}
		}

		return newCEEnchantSimpleList;
	}

	public List<String> getCESimpleNameList() {
		List<String> list = new ArrayList<String>();
		for (CEEnchantSimple ceEnchantSimple : ceEnchantSimpleList) {
			list.add(ceEnchantSimple.getName());
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

			CEEnchantSimple ceEnchantSimple = new CEEnchantSimple(value.toString());
			ceEnchantSimpleList.add(ceEnchantSimple);
		}
	}

	public NMSNBTTagList exportTo() {
		if (ceEnchantSimpleList.isEmpty()) {
			return null;
		}

		NMSNBTTagList list = new NMSNBTTagList();

		for (CEEnchantSimple ceEnchantSimple : ceEnchantSimpleList) {
			list.addString(ceEnchantSimple.toLine());
		}

		return list;
	}
}
