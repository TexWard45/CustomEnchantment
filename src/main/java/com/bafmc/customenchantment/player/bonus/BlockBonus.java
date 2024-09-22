package com.bafmc.customenchantment.player.bonus;

import java.util.ArrayList;
import java.util.List;

import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.api.Pair;
import com.bafmc.customenchantment.attribute.AttributeCalculate;
import com.bafmc.customenchantment.attribute.AttributeData;

public class BlockBonus extends Bonus<MaterialList> {
	public double getBonus(MaterialData materialNMS) {
		List<AttributeData> list = new ArrayList<AttributeData>();
		
		for (Pair<MaterialList, AttributeData> bonus : getBonusList()) {
			if (bonus.getKey().contains(materialNMS)) {
				list.add(bonus.getValue());
			}
		}

		return AttributeCalculate.calculate(0, list);
	}
}
