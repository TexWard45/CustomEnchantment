package com.bafmc.customenchantment.listener;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.RandomUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.ParticleSupport;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.CEWeaponType;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.TemporaryKey;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.entity.CraftPlayer;
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

import java.util.List;

public class StaffMechanicListener implements Listener {
    /* =========================
     * CONSTANTS
     * ========================= */
    private static final double HAND_OFFSET = 0.5;
    private static final double SPEED_PER_TICK = 1.0;
    private static final double HITBOX_EXPAND = 0.15;
    private static final double PARTICLE_VIEW_DISTANCE_SQ = 64 * 64;
    private static boolean magicShot = false;

    private static final ParticleOptions CYAN_PARTICLE =
            new DustParticleOptions(new Vector3f(0f, 182f / 255f, 182f / 255f), 1.0f);

    /* ========================= */

    private final CustomEnchantment plugin;
    private final ParticleSupport particleSupport = new ParticleSupport();

    public StaffMechanicListener(CustomEnchantment plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /* =========================
     * EVENTS
     * ========================= */

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {}
            default -> { return; }
        }

        Player player = event.getPlayer();
        if (!isStaff(player)) return;
        if (!canAttackNow(player)) {
            event.setCancelled(true);
            return;
        }

        shoot(player);
        event.setCancelled(true);
    }

    @EventHandler
    public void onMelee(EntityDamageByEntityEvent event) {
        if (magicShot) {
            return;
        }

        if (!(event.getDamager() instanceof Player player)) return;
        if (!isStaff(player)) return;

        event.setCancelled(true);
        if (!canAttackNow(player)) return;

        shoot(player);
    }

    /* =========================
     * CORE LOGIC
     * ========================= */

    private void shoot(Player player) {
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);

        double range = player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).getValue();
        double baseDamage = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
        float cooldown = getAttackCooldown(player);

        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        Vector right = dir.clone()
                .crossProduct(new Vector(0, 1, 0))
                .normalize()
                .multiply(HAND_OFFSET);

        double startOffset = RandomUtils.random(0.5, 1.25);

        Location start = eye.clone()
                .add(right)
                .add(dir.clone().multiply(startOffset));

        Location end = eye.clone().add(dir.multiply(range));

        runProjectile(player, cePlayer, start, end, baseDamage, cooldown);
    }

    private void runProjectile(Player shooter, CEPlayer cePlayer, Location start, Location end,
                               double baseDamage, float cooldown) {
        List<ParticleOptions> staffParticles = CustomEnchantment.instance().getMainConfig().getStaffParticles();

        double distance = start.distance(end);
        int totalTicks = (int) Math.ceil(distance / SPEED_PER_TICK);

        new BukkitRunnable() {
            int tick = 0;
            Location prev = start.clone();

            @Override
            public void run() {
                if (tick > totalTicks) {
                    cancel();
                    return;
                }

                double progress = (double) tick / totalTicks;

                Vector pos = end.toVector()
                        .subtract(start.toVector())
                        .multiply(progress)
                        .add(start.toVector());

                Location current = pos.toLocation(start.getWorld());

                spawnParticle(current, cePlayer, staffParticles);

                if (hitEntity(shooter, prev, current, baseDamage, cooldown)) {
                    cancel();
                    return;
                }

                if (current.getBlock().getType().isSolid()) {
                    cancel();
                    return;
                }

                prev = current;
                tick++;
            }

        }.runTaskTimer(plugin, 0L, 1L);
    }

    /* =========================
     * HIT DETECTION
     * ========================= */

    private boolean hitEntity(Player shooter, Location from, Location to,
                              double baseDamage, float cooldown) {

        Vector dir = to.toVector().subtract(from.toVector());
        double length = dir.length();
        if (length <= 0) return false;

        Vector ray = dir.normalize();

        for (Entity e : from.getWorld().getNearbyEntities(
                from.clone().add(ray.multiply(length / 2)),
                length / 2 + 1,
                2,
                length / 2 + 1
        )) {

            if (!(e instanceof LivingEntity target)) continue;
            if (target.equals(shooter)) continue;

            if (target.getBoundingBox()
                    .expand(HITBOX_EXPAND)
                    .rayTrace(from.toVector(), ray, length) == null) continue;

            magicShot = true;
            target.damage(calculateDamage(shooter, baseDamage, cooldown), shooter);
            magicShot = false;
            return true;
        }

        return false;
    }

    /* =========================
     * DAMAGE & COOLDOWN
     * ========================= */

    private double calculateDamage(Player player, double baseDamage, float cooldown) {
        var nms = ((CraftPlayer) player).getHandle();
        return baseDamage * (
                1.0F - Math.sqrt(
                        1.0F - Math.pow(cooldown, nms.level().purpurConfig.newDamageCurvePower)
                )
        );
    }

    private float getAttackCooldown(Player player) {
        return ((CraftPlayer) player).getHandle().getAttackStrengthScale(0.5F);
    }

    private boolean canAttackNow(Player player) {
        return getAttackCooldown(player)
                >= CustomEnchantment.instance()
                .getMainConfig()
                .getCombatStaffMinRequiredAttackStrengthScale();
    }

    /* =========================
     * UTILS
     * ========================= */

    private boolean isStaff(Player player) {
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        CEWeaponAbstract weapon =
                cePlayer.getEquipment().getSlot(EquipSlot.MAINHAND, true, true);
        return weapon != null && weapon.getWeaponType() == CEWeaponType.STAFF;
    }

    private void spawnParticle(Location loc, CEPlayer cePlayer, List<ParticleOptions> staffParticles) {
        Object obj = cePlayer.getTemporaryStorage().get(TemporaryKey.STAFF_PARTICLE);
        if (obj != null) {
            staffParticles = (List<ParticleOptions>) obj;
        }

        if (staffParticles.isEmpty()) {
            return;
        }

        int turn = cePlayer.getTemporaryStorage().getInt(TemporaryKey.STAFF_PARTICLE_TURN, 0);
        if (turn >= staffParticles.size()) {
            turn = 0;
        }

        ParticleOptions particle = staffParticles.get(turn % staffParticles.size());

        cePlayer.getTemporaryStorage().set(TemporaryKey.STAFF_PARTICLE_TURN, turn + 1);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getWorld().equals(loc.getWorld())) continue;
            if (p.getLocation().distanceSquared(loc) > PARTICLE_VIEW_DISTANCE_SQ) continue;

            particleSupport.send(
                    p, particle,
                    (float) loc.getX(),
                    (float) loc.getY(),
                    (float) loc.getZ(),
                    0, 0, 0,
                    1,
                    false
            );
        }
    }
}
