package com.bafmc.customenchantment.task;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RecalculateAttributeTask extends BukkitRunnable {
    private CustomEnchantment plugin;

    public RecalculateAttributeTask(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            CEPlayer cePlayer = CEAPI.getCEPlayer(player);
            cePlayer.getCustomAttribute().recalculateAttribute();
        }
    }
}
