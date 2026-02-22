package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.bukkit.bafframework.nms.NMSAttributeOperation;
import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.EntityTypeList;
import com.bafmc.customenchantment.attribute.RangeAttribute;
import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.RandomRange;

public class EffectAddMobBonus extends EffectHook {
	// EXP, MONEY
	private String type;
	private String name;
	private EntityTypeList list;
	private RangeAttribute attributeData;

	public String getIdentify() {
		return "ADD_MOB_BONUS";
	}

	public void setup(String[] args) {
		this.type = args[0];
		this.name = args[1];
		this.list = EntityTypeList.getEntityTypeList(args[2]);
		this.attributeData = new RangeAttribute(null, new RandomRange(args[3]),
				NMSAttributeOperation.fromId(args.length > 4 ? Integer.valueOf(args[4]) : 0),
				new Chance(args.length > 5 ? Integer.valueOf(args[5]) : 100));
	}
	
	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		if (type.equals(CEConstants.RewardType.EXP)) {
			cePlayer.getMobBonus().getExpBonus().put(name, list, attributeData);
		} else if (type.equals(CEConstants.RewardType.MONEY)) {
			cePlayer.getMobBonus().getMoneyBonus().put(name, list, attributeData);
		}else if (type.equals(CEConstants.RewardType.MOB_SLAYER_EXP)) {
			cePlayer.getMobBonus().getMobSlayerExpBonus().put(name, list, attributeData);
		}
	}
}
