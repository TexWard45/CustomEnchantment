package com.bafmc.customenchantment.task;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveTask extends BukkitRunnable {
    private CustomEnchantment plugin;

    public SaveTask(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for (CEPlayer cePlayer : CEPlayer.getCePlayerMap().getCEPlayers()) {
            cePlayer.getStorage().getConfig().save();
        }
    }
}
