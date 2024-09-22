package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.TemporaryKey;
import org.bukkit.entity.Player;

public class EffectActiveDoubleJump extends EffectHook {
    private double power;
    private long cooldown;
    private String particleVersion;
    private String particleName;
    private String cooldownMessage;

	public String getIdentify() {
		return "ACTIVE_DOUBLE_JUMP";
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
        storage.set(TemporaryKey.DOUBLE_JUMP_ENABLE, true);
        storage.set(TemporaryKey.DOUBLE_JUMP_POWER, power);
        storage.set(TemporaryKey.DOUBLE_JUMP_COOLDOWN, cooldown);
        storage.set(TemporaryKey.DOUBLE_JUMP_PARTICLE, particleVersion + ":" + particleName);
        storage.set(TemporaryKey.DOUBLE_JUMP_COOLDOWN_MESSAGE, cooldownMessage);
	}
}
