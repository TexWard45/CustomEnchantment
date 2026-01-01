package com.bafmc.customenchantment.listener;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.RandomUtils;
import com.bafmc.bukkit.utils.SoundUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.ParticleSupport;
import com.bafmc.customenchantment.config.MainConfig;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.CEWeaponType;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.TemporaryKey;
import net.minecraft.core.particles.ParticleOptions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.entity.CraftEntity;
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

import java.util.List;

public class StaffMechanicListener implements Listener {
    /* =========================
     * CONSTANTS
     * ========================= */
    private static final double HAND_OFFSET = 0.5;
    private static final double HITBOX_EXPAND = 0.15;
    private static final double PARTICLE_VIEW_DISTANCE_SQ = 64 * 64;
    private static boolean magicShot = false;

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
        if (!isStaff(player)) {
            return;
        }
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

        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        if (!isStaff(player)) {
            return;
        }

        event.setCancelled(true);
        if (!canAttackNow(player)) {
            return;
        }

        shoot(player);
    }

    /* =========================
     * CORE LOGIC
     * ========================= */

    private void shoot(Player player) {
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);

        SoundUtils.playWorld(player, Sound.BLOCK_AMETHYST_BLOCK_HIT, 0.6f, 1.2f);

        double range = player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).getValue();
        double baseDamage = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
        float cooldown = getAttackCooldown(player);

        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        Vector right = dir.clone()
                .crossProduct(new Vector(0, 1, 0))
                .normalize()
                .multiply(HAND_OFFSET);

        double startOffset = RandomUtils.random(0.5, 1.0);

        Location start = eye.clone()
                .add(right)
                .add(dir.clone().multiply(startOffset));

        Location end = eye.clone().add(dir.multiply(range));

        runProjectile(player, cePlayer, start, end, cooldown);
    }

    private void runProjectile(Player shooter, CEPlayer cePlayer, Location start, Location end,
                               float cooldown) {
        List<ParticleOptions> staffParticles = CustomEnchantment.instance().getMainConfig().getStaffParticles();

        MainConfig config = CustomEnchantment.instance().getMainConfig();

        double distance = start.distance(end);

        double stepSize = config.getStaffStepSize();
        double speed = config.getStaffSpeed();

        Vector direction = end.toVector()
                .subtract(start.toVector())
                .normalize();

        int stepsPerTick = (int) Math.ceil(speed / stepSize);

        new BukkitRunnable() {
            Location current = start.clone();
            Location prev = start.clone();

            @Override
            public void run() {
                for (int i = 0; i < stepsPerTick; i++) {

                    prev = current.clone();
                    current.add(direction.clone().multiply(stepSize));

                    spawnParticle(current, cePlayer, staffParticles);

                    if (hitEntity(shooter, prev, current, cooldown)) {
                        cancel();
                        return;
                    }

                    if (current.getBlock().getType().isSolid()) {
                        SoundUtils.playWorld(
                                current.getBlock().getLocation(),
                                Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR,
                                0.6f,
                                1.2f
                        );
                        cancel();
                        return;
                    }

                    if (current.distanceSquared(start) >= distance * distance) {
                        cancel();
                        return;
                    }
                }
            }

        }.runTaskTimer(plugin, 0L, 1L);
    }

    /* =========================
     * HIT DETECTION
     * ========================= */

    private boolean hitEntity(Player shooter, Location from, Location to,
                              float cooldown) {

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
            ((CraftPlayer) shooter).getHandle().attack(((CraftEntity) target).getHandle(), cooldown, true, true);
            SoundUtils.playWorld(target, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
            magicShot = false;
            return true;
        }

        return false;
    }

    /* =========================
     * DAMAGE & COOLDOWN
     * ========================= */

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
