package com.bafmc.customenchantment;

import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

public class MobDamageTrackerListener implements Listener {
    private static class MobStats {
        long firstHitTime;
        int hitCount;
        double totalDamage;
    }

    private final HashMap<UUID, MobStats> mobData = new HashMap<>();

    @EventHandler
    public void onMobDamaged(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) return;

        UUID mobId = mob.getUniqueId();
        MobStats stats = mobData.getOrDefault(mobId, new MobStats());

        if (stats.hitCount == 0) {
            stats.firstHitTime = System.currentTimeMillis();
        }

        stats.hitCount++;
        stats.totalDamage += event.getFinalDamage();
        mobData.put(mobId, stats);
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Mob mob)) return;

        UUID mobId = mob.getUniqueId();

        if (!mobData.containsKey(mobId)) return;

        MobStats stats = mobData.get(mobId);
        long currentTime = System.currentTimeMillis();
        long survivalTimeMs = currentTime - stats.firstHitTime;
        double survivalTimeSec = survivalTimeMs / 1000.0;

        Bukkit.getLogger().info("=== Mob Death Stats ===");
        Bukkit.getLogger().info("Loại mob: " + mob.getType().toString());
        Bukkit.getLogger().info("Thời gian sống sau lần bị đánh đầu tiên: " + String.format("%.2f", survivalTimeSec) + " giây");
        Bukkit.getLogger().info("Số lần bị đánh: " + stats.hitCount);
        Bukkit.getLogger().info("Tổng sát thương nhận: " + String.format("%.2f", stats.totalDamage));
        Bukkit.getLogger().info("========================");

        mobData.remove(mobId);
    }
}