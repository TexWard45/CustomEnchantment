package com.bafmc.customenchantment;

import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.RandomUtils;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.ParticleSupport;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.CEWeaponType;
import com.bafmc.customenchantment.player.CEPlayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public class SlowLineAttackListener implements Listener {

    private final CustomEnchantment plugin;

    private final ParticleSupport particleSupport = new ParticleSupport();

    // Particle #00B6B6
    private static final ParticleOptions CYAN_PARTICLE =
            new DustParticleOptions(
                    new Vector3f(0f / 255f, 182f / 255f, 182f / 255f),
                    1.0f
            );

    public SlowLineAttackListener(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAttack(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {}
            default -> { return; }
        }

        Player player = event.getPlayer();
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        CEWeaponAbstract ceWeaponAbstract = cePlayer.getEquipment().getSlot(EquipSlot.MAINHAND);
        if (ceWeaponAbstract == null) return;
        if (ceWeaponAbstract.getWeaponType() == CEWeaponType.STAFF) {
            double range = cePlayer.getPlayer().getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).getValue();
            double attackDamage = cePlayer.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();

            shootSlowLine(player, range, attackDamage);
            event.setCancelled(true);
        }
    }

    public static boolean magicShot = false;

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!magicShot) {
            Player player = (Player) event.getDamager();
            CEPlayer cePlayer = CEAPI.getCEPlayer(player);
            CEWeaponAbstract ceWeaponAbstract = cePlayer.getEquipment().getSlot(EquipSlot.MAINHAND);
            if (ceWeaponAbstract == null) return;
            if (ceWeaponAbstract.getWeaponType() == CEWeaponType.STAFF) {
                double range = cePlayer.getPlayer().getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).getValue();
                double attackDamage = cePlayer.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
                shootSlowLine(player, range, attackDamage);
                event.setCancelled(true);
            }
        }else {
            System.out.println(event.getDamager().getLocation().distance(event.getEntity().getLocation()));
        }
    }

    private void shootSlowLine(Player player, double range, double attackDamage) {
        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection().normalize();

        Vector right = direction.clone()
                .crossProduct(new Vector(0, 1, 0))
                .normalize()
                .multiply(0.5);

        double startOffset = RandomUtils.random(0.5, 1.25);

        Location start = eye.clone()
                .add(right)
                .add(direction.clone().multiply(startOffset));

         double maxDistance = range - startOffset;
        final double speedPerTick = 1.0;

        Location end = eye.clone().add(direction.clone().multiply(maxDistance));

        double totalDistance = start.distance(end);
        int totalTicks = (int) Math.ceil(totalDistance / speedPerTick);

        new BukkitRunnable() {

            int tick = 0;
            Location prevLoc = start.clone();

            @Override
            public void run() {
                double progress = (double) tick / totalTicks;
                if (progress >= 1.0) {
                    cancel();
                    return;
                }

                // Nội suy vị trí
                Vector current = end.toVector()
                        .subtract(start.toVector())
                        .multiply(progress)
                        .add(start.toVector());

                Location currLoc = current.toLocation(start.getWorld());

                spawnParticle(currLoc);
                if (checkHitLine(player, prevLoc, currLoc, attackDamage)) {
                    cancel();
                    return;
                }

                if (currLoc.getBlock().getType().isSolid()) {
                    cancel();
                    return;
                }

                prevLoc = currLoc;
                tick++;
            }

        }.runTaskTimer(plugin, 0L, 1L);
    }

    /* =========================
     * PARTICLE
     * ========================= */
    private void spawnParticle(Location loc) {
        String worldName = loc.getWorld().getName();

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.getWorld().getName().equals(worldName)) continue;
            if (target.getLocation().distanceSquared(loc) > 64 * 64) continue;

            particleSupport.send(
                    target,
                    CYAN_PARTICLE,
                    (float) loc.getX(),
                    (float) loc.getY(),
                    (float) loc.getZ(),
                    0f, 0f, 0f,   // vector
                    1,            // count
                    false         // distance > 255 ?
            );
        }
    }

    private boolean checkHitLine(Player shooter, Location from, Location to, double attackDamage) {
        Vector direction = to.toVector().subtract(from.toVector());
        double length = direction.length();

        if (length <= 0) return false;

        Vector rayDir = direction.normalize();

        for (Entity e : from.getWorld().getNearbyEntities(
                from.clone().add(rayDir.multiply(length / 2)),
                length / 2 + 1,
                2,
                length / 2 + 1
        )) {

            if (!(e instanceof LivingEntity target)) continue;
            if (target.equals(shooter)) continue;

            // BoundingBox chuẩn hitbox entity
            var box = target.getBoundingBox().expand(0.15);

            if (box.rayTrace(from.toVector(), rayDir, length) != null) {

                magicShot = true;
                target.damage(attackDamage, shooter);
                magicShot = false;

                return true; // trúng
            }
        }

        return false;
    }
}