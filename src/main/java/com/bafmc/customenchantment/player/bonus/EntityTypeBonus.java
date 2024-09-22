package com.bafmc.customenchantment.player.bonus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;

import com.bafmc.customenchantment.api.EntityTypeList;
import com.bafmc.customenchantment.api.Pair;
import com.bafmc.customenchantment.attribute.AttributeCalculate;
import com.bafmc.customenchantment.attribute.AttributeData;

public class EntityTypeBonus extends Bonus<EntityTypeList> {
	public double getBonus(EntityType entityType, int size) {
		List<AttributeData> list = new ArrayList<AttributeData>();
		
		for (Pair<EntityTypeList, AttributeData> bonus : getBonusList()) {
			if (bonus.getKey().contains(entityType)) {
				list.add(bonus.getValue());
			}
		}
		
		return AttributeCalculate.calculate(0, list) * size;
	}
}
