package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.mining.VeinSpecialMine;
import org.bukkit.entity.Player;

public class EffectAddVeinMining extends EffectHook {
	private String name;
	private VeinSpecialMine.Vein vein;

	public String getIdentify() {
		return "ADD_VEIN_MINING";
	}

	public void setup(String[] args) {
		this.name = args[0];
		this.vein = new VeinSpecialMine.Vein(Integer.parseInt(args[1]), Double.parseDouble(args[2]), MaterialList.getMaterialList(StringUtils.split(args[3], ",", 0)));
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		cePlayer.getSpecialMining().getVeinSpecialMine().getVein().addVein(name, vein);
	}
}
