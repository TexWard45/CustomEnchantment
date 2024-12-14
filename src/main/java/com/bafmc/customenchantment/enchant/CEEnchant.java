package com.bafmc.customenchantment.enchant;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.MaterialList;

import java.util.List;

public class CEEnchant {
	private String name;
	private String groupName;
	private int maxLevel;
	private int valuable;
	private int enchantPoint;
	private CEDisplay ceDisplay;
	private CELevelMap ceLevelMap;
	private MaterialList appliesMaterialList;
    private String set;
    private String bookType;
    private List<String> enchantBlacklist;

	public CEEnchant(String name, String groupName, int maxLevel, int valuable, int enchantPoint, CEDisplay ceDisplay,
			CELevelMap ceLevelMap, MaterialList appliesMaterialList, String set, String bookType, List<String> enchantBlacklist) {
		this.name = name;
		this.groupName = groupName;
		this.maxLevel = maxLevel;
		this.valuable = valuable;
		this.enchantPoint = enchantPoint;
		this.ceDisplay = ceDisplay;
		this.ceLevelMap = ceLevelMap;
		this.appliesMaterialList = appliesMaterialList;
        this.set = set;
        this.bookType = bookType;
        this.enchantBlacklist = enchantBlacklist;
	}

	public String getName() {
		return name;
	}

	public String getGroupName() {
		return groupName;
	}

	public CEGroup getCEGroup() {
		return CustomEnchantment.instance().getCeGroupMap().get(groupName);
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

	public CELevelMap getCELevelMap() {
		return ceLevelMap;
	}

	public MaterialList getAppliesMaterialList() {
		return appliesMaterialList;
	}

    public String getSet() {
        return set;
    }

    public String getBookType() {
        return bookType;
    }

    public List<String> getEnchantBlacklist() {
        return enchantBlacklist;
    }
}
