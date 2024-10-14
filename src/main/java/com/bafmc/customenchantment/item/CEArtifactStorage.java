package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;

public class CEArtifactStorage extends CEItemStorage<CEArtifact> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEArtifact getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEArtifact) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.ARTIFACT).get(name);
	}

}
