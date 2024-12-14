package com.bafmc.customenchantment.item.increaseratebook;

import com.bafmc.customenchantment.item.CEItemData;

import java.util.List;

public class CEIncreaseRateBookData extends CEItemData implements Cloneable {

	private List<String> groups;
	private int success;
	private int destroy;

	public CEIncreaseRateBookData() {
	}

	public CEIncreaseRateBookData(String pattern, List<String> groups, int success, int destroy) {
		super(pattern);
		this.groups = groups;
		this.success = success;
		this.destroy = destroy;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public int getDestroy() {
		return destroy;
	}

	public void setDestroy(int destroy) {
		this.destroy = destroy;
	}
	
	public CEIncreaseRateBookData clone() {
		try {
			return (CEIncreaseRateBookData) super.clone();
		}catch(Exception e) {
			return new CEIncreaseRateBookData(getPattern(), groups, success, destroy);
		}
	}

}
