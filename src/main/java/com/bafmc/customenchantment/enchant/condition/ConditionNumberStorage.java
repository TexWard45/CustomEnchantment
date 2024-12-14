package com.bafmc.customenchantment.enchant.condition;

import java.util.Map;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.CompareOperation;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.enchant.ConditionHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.bukkit.utils.MathUtils;

public class ConditionNumberStorage extends ConditionHook {
	private String compare1;
	private CompareOperation operation;
	private String compare2;

	public String getIdentify() {
		return "NUMBER_STORAGE";
	}

	public void setup(String[] args) {
		this.compare1 = args[0];
		this.operation = CompareOperation.getOperation(args[1]);
		this.compare2 = args[2];
	}

	@Override
	public boolean match(CEFunctionData data) {
		String compare1 = this.compare1;
		String compare2 = this.compare2;

		CEPlayer cePlayer = data.getPlayer() != null ? CEAPI.getCEPlayer(data.getPlayer()) : null;
		Map<String, String> storagePlaceholder = CEPlaceholder
				.getTemporaryStoragePlaceholder(cePlayer.getTemporaryStorage());

		if (storagePlaceholder.containsKey(compare1)) {
			compare1 = storagePlaceholder.get(compare1);
		}else {
			Map<String, String> map1 = CEPlaceholder.getCEFunctionDataPlaceholder(compare1, data);
			map1.putAll(storagePlaceholder);
			compare1 = CEPlaceholder.setPlaceholder(compare1, map1);
		}

		if (storagePlaceholder.containsKey(compare2)) {
			compare2 = storagePlaceholder.get(compare2);
		}else {
			Map<String, String> map2 = CEPlaceholder.getCEFunctionDataPlaceholder(compare2, data);
			map2.putAll(storagePlaceholder);
			compare2 = CEPlaceholder.setPlaceholder(compare2, map2);
		}

		// Compare
		double compareNumber1 = 0;
		double compareNumber2 = 0;

		try {
			compareNumber1 = MathUtils.evalDouble(compare1);
		} catch (Exception e) {
			compareNumber1 = 0;
		}
		try {
			compareNumber2 = MathUtils.evalDouble(compare2);
		} catch (Exception e) {
			compareNumber2 = 0;
		}

		return CompareOperation.compare(compareNumber1, compareNumber2, operation);
	}

}