package me.texward.customenchantment.enchant;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.MaterialList;
import me.texward.texwardlib.util.SparseMap;

public class CEEnchant {
	private String name;
	private String groupName;
	private int maxLevel;
	private int valuable;
	private int enchantPoint;
	private CEDisplay ceDisplay;
	private SparseMap<CELevel> ceLevelMap;
	private MaterialList appliesMaterialList;
    private String set;

	public CEEnchant(String name, String groupName, int maxLevel, int valuable, int enchantPoint, CEDisplay ceDisplay,
			SparseMap<CELevel> ceLevelMap, MaterialList appliesMaterialList, String set) {
		this.name = name;
		this.groupName = groupName;
		this.maxLevel = maxLevel;
		this.valuable = valuable;
		this.enchantPoint = enchantPoint;
		this.ceDisplay = ceDisplay;
		this.ceLevelMap = ceLevelMap;
		this.appliesMaterialList = appliesMaterialList;
        this.set = set;
	}

	public String getName() {
		return name;
	}

	public String getGroupName() {
		return groupName;
	}

	public CEGroup getCEGroup() {
		return CustomEnchantment.instance().getCEGroupMap().get(groupName);
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public int getValuable() {
		return valuable;
	}

	public int getEnchantPoint() {
		return enchantPoint;
	}

	public CEDisplay getCEDisplay() {
		return ceDisplay;
	}

	public CELevel getCELevel(int level) {
		return ceLevelMap.get(level);
	}

	public SparseMap<CELevel> getCELevelMap() {
		return ceLevelMap;
	}

	public MaterialList getAppliesMaterialList() {
		return appliesMaterialList;
	}

    public String getSet() {
        return set;
    }
}
