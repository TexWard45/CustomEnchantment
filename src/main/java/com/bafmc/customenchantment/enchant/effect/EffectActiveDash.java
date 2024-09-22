package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.TemporaryKey;
import org.bukkit.entity.Player;

public class EffectActiveDash extends EffectHook {
    private double power;
    private long cooldown;
    private String particleVersion;
    private String particleName;
    private String cooldownMessage;

	public String getIdentify() {
		return "ACTIVE_DASH";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
        this.power = Double.parseDouble(args[0]);
        this.cooldown = Long.parseLong(args[1]);
        this.particleVersion = args[2];
        this.particleName = args[3];

        this.cooldownMessage = "";
        for (int i = 4; i < args.length; i++) {
            this.cooldownMessage += args[i] + " ";
        }
	}

	public void execute(CEFunctionData data) {
        Player player = data.getPlayer();
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
        storage.set(TemporaryKey.DASH_ENABLE, true);
        storage.set(TemporaryKey.DASH_POWER, power);
        storage.set(TemporaryKey.DASH_COOLDOWN, cooldown);
        storage.set(TemporaryKey.DASH_PARTICLE, particleVersion + ":" + particleName);
        storage.set(TemporaryKey.DASH_COOLDOWN_MESSAGE, cooldownMessage);
	}
}
