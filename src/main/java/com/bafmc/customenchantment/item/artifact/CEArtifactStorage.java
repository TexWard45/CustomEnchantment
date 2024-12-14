package com.bafmc.customenchantment.item.artifact;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CEArtifactStorage extends CEItemStorage<CEArtifact> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEArtifact getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEArtifact) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.ARTIFACT).get(name);
	}

}
