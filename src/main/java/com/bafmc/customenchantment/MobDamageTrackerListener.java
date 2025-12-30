package com.bafmc.customenchantment;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobDamageTrackerListener implements Listener {
//    private static class MobStats {
//        long firstHitTime;
//        int hitCount;
//        double totalDamage;
//    }
//
//    private static class HitDelayTracker {
//        private static final long RESET_THRESHOLD_MS = 5_000;
//
//        private long lastHitTime = -1;
//        private long totalDelay;
//        private int delayCount;
//
//        /**
//         * Ghi nhận hit và trả về delay giữa hit này với hit trước (ms)
//         * Nếu là hit đầu hoặc bị reset → trả về -1
//         */
//        public long recordHitAndGetDelay() {
//            long now = System.currentTimeMillis();
//
//            if (lastHitTime == -1) {
//                lastHitTime = now;
//                return -1;
//            }
//
//            long delay = now - lastHitTime;
//            lastHitTime = now;
//
//            if (delay > RESET_THRESHOLD_MS) {
//                reset();
//                return -1;
//            }
//
//            totalDelay += delay;
//            delayCount++;
//            return delay;
//        }
//
//        public double getAverageDelaySeconds() {
//            if (delayCount == 0) return 0.0;
//            return (totalDelay / (double) delayCount) / 1000.0;
//        }
//
//        public void reset() {
//            lastHitTime = -1;
//            totalDelay = 0;
//            delayCount = 0;
//        }
//    }
//
//    private final Map<UUID, HitDelayTracker> trackers = new HashMap<>();
//
//    @EventHandler
//    public void onMobDamaged2(EntityDamageEvent event) {
//        if (!(event.getEntity() instanceof Mob mob)) return;
//
//        UUID id = mob.getUniqueId();
//
//        HitDelayTracker tracker =
//                trackers.computeIfAbsent(id, k -> new HitDelayTracker());
//
//        long delayMs = tracker.recordHitAndGetDelay();
//        double avgDelay = tracker.getAverageDelaySeconds();
//
//        // ===== QUĂNG RA NGAY MỖI HIT =====
//        Bukkit.getLogger().info("=== HIT ===");
//        Bukkit.getLogger().info("Mob: " + mob.getType());
//
//        if (delayMs != -1) {
//            Bukkit.getLogger().info("Delay hit trước: " +
//                    String.format("%.2f", delayMs / 1000.0) + " giây");
//        } else {
//            Bukkit.getLogger().info("Delay hit trước: N/A (hit đầu hoặc reset)");
//        }
//
//        Bukkit.getLogger().info("AVG hit delay: " +
//                String.format("%.2f", avgDelay) + " giây");
//        Bukkit.getLogger().info("================");
//    }
//
//    private final HashMap<UUID, MobStats> mobData = new HashMap<>();
//
//    @EventHandler
//    public void onMobDamaged(EntityDamageEvent event) {
//        if (!(event.getEntity() instanceof Mob mob)) return;
//
//        UUID mobId = mob.getUniqueId();
//        MobStats stats = mobData.getOrDefault(mobId, new MobStats());
//
//        if (stats.hitCount == 0) {
//            stats.firstHitTime = System.currentTimeMillis();
//        }
//
//        stats.hitCount++;
//        stats.totalDamage += event.getFinalDamage();
//        mobData.put(mobId, stats);
//    }
//
//    @EventHandler
//    public void onMobDeath(EntityDeathEvent event) {
//        LivingEntity entity = event.getEntity();
//        if (!(entity instanceof Mob mob)) return;
//
//        UUID mobId = mob.getUniqueId();
//
//        if (!mobData.containsKey(mobId)) return;
//
//        MobStats stats = mobData.get(mobId);
//        long currentTime = System.currentTimeMillis();
//        long survivalTimeMs = currentTime - stats.firstHitTime;
//        double survivalTimeSec = survivalTimeMs / 1000.0;
//
//        Bukkit.getLogger().info("=== Mob Death Stats ===");
//        Bukkit.getLogger().info("Loại mob: " + mob.getType().toString());
//        Bukkit.getLogger().info("Thời gian sống sau lần bị đánh đầu tiên: " + String.format("%.2f", survivalTimeSec) + " giây");
//        Bukkit.getLogger().info("Số lần bị đánh: " + stats.hitCount);
//        Bukkit.getLogger().info("Tổng sát thương nhận: " + String.format("%.2f", stats.totalDamage));
//        Bukkit.getLogger().info("========================");
//
//        mobData.remove(mobId);
//    }
}