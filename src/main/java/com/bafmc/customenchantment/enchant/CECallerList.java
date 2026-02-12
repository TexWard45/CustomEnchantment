package com.bafmc.customenchantment.enchant;

import java.util.ArrayList;
import java.util.List;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
public class CECallerList extends ArrayList<CECaller> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<CECallerResult> getResults() {
		List<CECallerResult> results = new ArrayList<CECallerResult>();

		for (CECaller caller : this) {
			results.add(caller.getResult());
		}

		return results;
	}
	
	public List<NMSAttribute> getOptionDataList() {
		List<NMSAttribute> optionDataList = new ArrayList<>();

		for (CECaller caller : this) {
			optionDataList.addAll(caller.getResult().getOptionDataList());
		}

		return optionDataList;
	}
}
