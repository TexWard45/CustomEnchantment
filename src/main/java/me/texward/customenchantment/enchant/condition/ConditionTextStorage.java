package me.texward.customenchantment.enchant.condition;

import java.util.Map;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.api.CompareOperation;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.CEPlaceholder;
import me.texward.customenchantment.enchant.ConditionHook;
import me.texward.customenchantment.player.CEPlayer;

public class ConditionTextStorage extends ConditionHook {
	private String compare1;
	private CompareOperation operation;
	private String compare2;

	public String getIdentify() {
		return "TEXT_STORAGE";
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

		Map<String, String> map1 = CEPlaceholder.getCEFunctionDataPlaceholder(compare1, data);
		map1.putAll(storagePlaceholder);
		compare1 = CEPlaceholder.setPlaceholder(compare1, map1);

		Map<String, String> map2 = CEPlaceholder.getCEFunctionDataPlaceholder(compare2, data);
		map2.putAll(storagePlaceholder);
		compare2 = CEPlaceholder.setPlaceholder(compare2, map2);

		// Compare
		if (operation == CompareOperation.EQUALS) {
			return compare1.equals(compare2);
		}
		if (operation == CompareOperation.NOT_EQUALS) {
			return !compare1.equals(compare2);
		}
		if (operation == CompareOperation.EQUALSIGNORECASE) {
			return compare1.equalsIgnoreCase(compare2);
		}
		return false;
	}

}