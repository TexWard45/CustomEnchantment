package me.texward.customenchantment.enchant.effect;

import java.util.Map;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.CEPlaceholder;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.customenchantment.player.PlayerTemporaryStorage;

public class EffectTextStorage extends EffectHook {
	public enum Type {
		SET, REMOVE;
	}

	private Type type;
	private String key;
	private String value;

	public String getIdentify() {
		return "TEXT_STORAGE";
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

		switch (type) {
		case SET:
			storage.set(key, value);
			break;
		case REMOVE:
			storage.unset(key);
			break;
		default:
			break;
		}
	}
}
