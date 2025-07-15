package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.TemporaryKey;
import org.bukkit.entity.Player;

public class EffectActiveFlash extends EffectHook {
    private double power;
    private long cooldown;
    private boolean smart;
    private String particleVersion;
    private String particleName;
    private String cooldownMessage;

	public String getIdentify() {
		return "ACTIVE_FLASH";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
        this.power = Double.parseDouble(args[0]);
        this.smart = Boolean.parseBoolean(args[1]);
        this.cooldown = Long.parseLong(args[2]);
        this.particleVersion = args[3];
        this.particleName = args[4];

        this.cooldownMessage = args[5];
	}

	public void execute(CEFunctionData data) {
        Player player = data.getPlayer();
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
        storage.set(TemporaryKey.FLASH_ENABLE, true);
        storage.set(TemporaryKey.FLASH_POWER, power);
        storage.set(TemporaryKey.FLASH_SMART, smart);
        storage.set(TemporaryKey.FLASH_COOLDOWN, cooldown);
        storage.set(TemporaryKey.FLASH_PARTICLE, particleVersion + ":" + particleName);
        storage.set(TemporaryKey.FLASH_COOLDOWN_MESSAGE, cooldownMessage);
	}
}
