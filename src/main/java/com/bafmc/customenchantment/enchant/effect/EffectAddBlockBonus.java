package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.bukkit.bafframework.nms.NMSAttributeOperation;
import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.RandomRange;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.attribute.RangeAttribute;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;

public class EffectAddBlockBonus extends EffectHook {
	// EXP, MONEY
	private String type;
	private String name;
	private MaterialList list;
	private RangeAttribute attributeData;

	public String getIdentify() {
		return "ADD_BLOCK_BONUS";
	}

	public void setup(String[] args) {
		this.type = args[0];
		this.name = args[1];
		this.list = MaterialList.getMaterialList(args[2]);
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
		if (type.equals("EXP")) {
			cePlayer.getBlockBonus().getExpBonus().put(name, list, attributeData);
		} else if (type.equals("MONEY")) {
			cePlayer.getBlockBonus().getMoneyBonus().put(name, list, attributeData);
		}
	}
}
