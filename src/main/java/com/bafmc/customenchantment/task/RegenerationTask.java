package com.bafmc.customenchantment.task;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.enchant.ModifyType;
import com.bafmc.customenchantment.event.CEPlayerStatsModifyEvent;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class RegenerationTask extends BukkitRunnable {
    private CustomEnchantment plugin;
    private Map<String, Long> lastRegeneration = new HashMap<>();

    public RegenerationTask(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            try {
                run(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run(Player player) {
        if (lastRegeneration.containsKey(player.getUniqueId().toString())) {
            long lastRegen = lastRegeneration.get(player.getUniqueId().toString());
            long currentTime = System.currentTimeMillis();

            double ratio = (currentTime - lastRegen) / 1000.0;
            if (ratio > 1) {
                ratio = 1;
            }

            CEPlayer cePlayer = CEAPI.getCEPlayer(player);

            double healRegeneration = cePlayer.getCustomAttribute().getValue(CustomAttributeType.HEALTH_REGENERATION) * ratio;
            if (healRegeneration > 0) {
                double defaultValue = player.getHealth();
                double currentValue = player.getHealth() + healRegeneration;

                CEPlayerStatsModifyEvent event = new CEPlayerStatsModifyEvent(cePlayer, CustomAttributeType.STAT_HEALTH, ModifyType.ADD,
                        defaultValue, currentValue, false);
                Bukkit.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    player.setHealth(Math.max(Math.min(player.getMaxHealth(), event.getCurrentValue()), 0));
                }
            }

            double healthRegenerationPercent = cePlayer.getCustomAttribute().getValue(CustomAttributeType.HEALTH_REGENERATION_PERCENT) * ratio;
            if (healthRegenerationPercent > 0) {
                double defaultValue = player.getHealth();
                double currentValue = player.getHealth() + player.getMaxHealth() * healthRegenerationPercent / 100;

                CEPlayerStatsModifyEvent event = new CEPlayerStatsModifyEvent(cePlayer, CustomAttributeType.STAT_HEALTH, ModifyType.ADD,
                        defaultValue, currentValue, false);
                Bukkit.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    player.setHealth(Math.max(Math.min(player.getMaxHealth(), event.getCurrentValue()), 0));
                }
            }

            lastRegeneration.put(player.getUniqueId().toString(), currentTime);
        } else {
            lastRegeneration.put(player.getUniqueId().toString(), System.currentTimeMillis());
        }

    }
}
