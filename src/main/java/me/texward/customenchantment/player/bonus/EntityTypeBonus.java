package me.texward.customenchantment.player.bonus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;

import me.texward.customenchantment.api.EntityTypeList;
import me.texward.customenchantment.api.Pair;
import me.texward.customenchantment.attribute.AttributeCalculate;
import me.texward.customenchantment.attribute.AttributeData;

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
