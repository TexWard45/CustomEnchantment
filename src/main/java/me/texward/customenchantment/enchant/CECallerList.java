package me.texward.customenchantment.enchant;

import java.util.ArrayList;
import java.util.List;

import me.texward.customenchantment.attribute.AttributeData;

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
	
	public List<AttributeData> getOptionDataList() {
		List<AttributeData> optionDataList = new ArrayList<AttributeData>();

		for (CECaller caller : this) {
			optionDataList.addAll(caller.getResult().getOptionDataList());
		}

		return optionDataList;
	}
}
