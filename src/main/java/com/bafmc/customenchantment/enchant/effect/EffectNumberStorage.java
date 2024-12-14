package com.bafmc.customenchantment.enchant.effect;

import java.util.Map;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.bukkit.utils.MathUtils;

public class EffectNumberStorage extends EffectHook {
	public enum Type {
		ADD, SUB, REMOVE, SET, CLEAR;
	}

	private Type type;
	private String key;
	private String value;

	public String getIdentify() {
		return "NUMBER_STORAGE";
	}

	public void setup(String[] args) {
		this.type = Type.valueOf(args[0]);
		this.key = args[1];
		if (!isRemove())
			this.value = args[2];
	}

	public boolean isRemove() {
		return type == Type.REMOVE;
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();

		String key = this.key;
		String value = this.value;

		// Replace default placeholder
		key = CEPlaceholder.setPlaceholder(key, CEPlaceholder.getCEFunctionDataPlaceholder(key, data));

		if (!isRemove()) {
			Map<String, String> temporaryPlaceholder = CEPlaceholder.getTemporaryStoragePlaceholder(cePlayer.getTemporaryStorage());

			if (temporaryPlaceholder.containsKey(value)) {
				value = temporaryPlaceholder.get(value);
			}else {
				Map<String, String> placeholder = CEPlaceholder.getCEFunctionDataPlaceholder(value, data);
				placeholder.putAll(temporaryPlaceholder);
				value = CEPlaceholder.setPlaceholder(value, placeholder);
			}
		}

		double number = MathUtils.evalDouble(value);
		
		switch (type) {
		case ADD:
			storage.set(key, (double) storage.getDouble(key) + number);
			break;
		case SUB:
			storage.set(key, (double) storage.getDouble(key) - number);
			break;
		case REMOVE:
			storage.unset(key);
			break;
		case SET:
			storage.set(key, number);
			break;
		default:
			break;
		}
	}
}
