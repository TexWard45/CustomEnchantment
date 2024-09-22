package com.bafmc.customenchantment.enchant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CELevel {
	private LinkedHashMap<String, CEFunction> functionMap;

	public CELevel(LinkedHashMap<String, CEFunction> functionMap) {
		this.functionMap = functionMap;
	}

	public List<CEFunction> getFunctionList() {
		return new ArrayList<CEFunction>(functionMap.values());
	}

	public LinkedHashMap<String, CEFunction> getFunctionMap() {
		return functionMap;
	}
}
