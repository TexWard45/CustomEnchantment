package me.texward.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.api.EntityTypeList;
import me.texward.customenchantment.attribute.AttributeData;
import me.texward.customenchantment.attribute.AttributeData.Operation;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.texwardlib.util.Chance;
import me.texward.texwardlib.util.RandomRange;

public class EffectAddMobBonus extends EffectHook {
	// EXP, MONEY
	private String type;
	private String name;
	private EntityTypeList list;
	private AttributeData attributeData;

	public String getIdentify() {
		return "ADD_MOB_BONUS";
	}

	public void setup(String[] args) {
		this.type = args[0];
		this.name = args[1];
		this.list = EntityTypeList.getEntityTypeList(args[2]);
		this.attributeData = new AttributeData(null, new RandomRange(args[3]),
				Operation.fromId(args.length > 4 ? Integer.valueOf(args[4]) : 0),
				new Chance(args.length > 5 ? Integer.valueOf(args[5]) : 100));
	}
	
	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		if (type.equals("EXP")) {
			cePlayer.getMobBonus().getExpBonus().put(name, list, attributeData);
		} else if (type.equals("MONEY")) {
			cePlayer.getMobBonus().getMoneyBonus().put(name, list, attributeData);
		}else if (type.equals("MOB_SLAYER_EXP")) {
			cePlayer.getMobBonus().getMobSlayerExpBonus().put(name, list, attributeData);
		}
	}
}
