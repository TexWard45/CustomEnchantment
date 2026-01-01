package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.api.FactionAPI;
import com.bafmc.bukkit.task.PlayerPerTickTask;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.TemporaryKey;
import org.bukkit.entity.Player;

public class UpdateJumpTask extends PlayerPerTickTask {
    private CustomEnchantment plugin;

    public UpdateJumpTask(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getPlayerPerTick() {
        return 20;
    }

    @Override
    public void run(Player player) {
        if (player.getGameMode() != org.bukkit.GameMode.SURVIVAL) {
            return;
        }

        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
        if (storage.isBoolean(TemporaryKey.DOUBLE_JUMP_ENABLE)) {
            double doubleJumpCooldown = storage.getDouble(TemporaryKey.DOUBLE_JUMP_COOLDOWN);
            long doubleJumpLastUse = storage.getLong(TemporaryKey.DOUBLE_JUMP_LAST_USE);
            if (System.currentTimeMillis() - doubleJumpLastUse < doubleJumpCooldown) {
                player.setAllowFlight(false);
            }else {
                player.setAllowFlight(true);
            }
        }else {
            if (FactionAPI.isFactionSupport() && FactionAPI.isFlying(player)) {
                return;
            }

            player.setAllowFlight(false);
        }
    }
}
