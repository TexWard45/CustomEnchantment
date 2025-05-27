package com.bafmc.customenchantment.enchant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CEDisplay {
	private String display;
    private String bookDisplay;
	private Map<String, String> customDisplayLore;
	private boolean disableEnchantLore;
	private List<String> description;
	private List<String> detailDescription;
	private List<String> appliesDescription;
	private HashMap<Integer, List<String>> descriptionMap;

	public CEDisplay(String display, String bookDisplay, Map<String, String> customDisplayLore, boolean disableEnchantLore, List<String> description, List<String> detailDescription, List<String> appliesDescription) {
		this.display = display;
        this.bookDisplay = bookDisplay;
		this.customDisplayLore = customDisplayLore;
		this.disableEnchantLore = disableEnchantLore;
		this.description = description;
		this.detailDescription = detailDescription;
		this.appliesDescription = appliesDescription;
	}

	public String getDefaultDisplay() {
		return display;
	}

	public Map<String, String> getCustomDisplayFormat() {
		return customDisplayLore;
	}
	
	public String getDisplay() {
		return customDisplayLore != null ? customDisplayLore.get("default") : display;
	}

    public String getBookDisplay() {
        return bookDisplay;
    }
	
	public boolean isDisableEnchantLore() {
		return disableEnchantLore;
	}

	public List<String> getDescription() {
		return description;
	}
	
	public List<String> getDetailDescription() {
		return detailDescription.isEmpty() ? description : detailDescription;
	}

	public List<String> getAppliesDescription() {
		return appliesDescription;
	}

	public HashMap<Integer, List<String>> getDescriptionMap() {
		return descriptionMap;
	}

	public void setDescriptionMap(HashMap<Integer, List<String>> descriptionMap) {
		this.descriptionMap = descriptionMap;
	}
}
