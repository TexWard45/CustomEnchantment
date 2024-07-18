package me.texward.customenchantment.player.bonus;

import java.util.ArrayList;
import java.util.List;

import me.texward.customenchantment.api.MaterialData;
import me.texward.customenchantment.api.MaterialList;
import me.texward.customenchantment.api.Pair;
import me.texward.customenchantment.attribute.AttributeCalculate;
import me.texward.customenchantment.attribute.AttributeData;

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
