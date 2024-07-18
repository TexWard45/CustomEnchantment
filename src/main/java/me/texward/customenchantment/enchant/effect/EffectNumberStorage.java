package me.texward.customenchantment.enchant.effect;

import java.util.Map;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.CEPlaceholder;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.customenchantment.player.PlayerTemporaryStorage;
import me.texward.texwardlib.util.MathUtils;

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
			Map<String, String> placeholder = CEPlaceholder.getCEFunctionDataPlaceholder(value, data);
			placeholder.putAll(CEPlaceholder.getTemporaryStoragePlaceholder(cePlayer.getTemporaryStorage()));
			value = CEPlaceholder.setPlaceholder(value, placeholder);
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
