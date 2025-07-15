package com.bafmc.customenchantment.feature.other;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class FlashFeature {
    public static boolean flash(Player player, double power, String particle, boolean smart) {
        Location from = player.getLocation();
        float originalYaw = from.getYaw();

        Vector direction = from.getDirection().normalize().multiply(power);
        if (direction.getY() < 0) {
            direction.setY(0); // Prevent going down
        }

        Location to = from.clone().add(direction);
        Location refineTo = getLegitLocation(from, to);

        double distance = from.distance(refineTo);
        if (distance < 1) {
            return false;
        }

        if (smart) {
            Entity target = getLookingTarget(player, distance);

            if (target != null) {
                float reversedYaw = (originalYaw + 180F) % 360F;
                refineTo.setYaw(reversedYaw);
            }
        }

        player.teleport(refineTo);
        return true;
    }

    private static Entity getLookingTarget(Player player, double range) {
        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection();

        for (Entity nearby : player.getNearbyEntities(range, range, range)) {
            if (!(nearby instanceof LivingEntity)) continue;

            Location targetLoc = nearby.getLocation().add(0, nearby.getHeight() / 2.0, 0);
            Vector toTarget = targetLoc.toVector().subtract(eye.toVector()).normalize();

            double dot = dir.dot(toTarget);
            if (dot > 0.8) {
                return nearby;
            }
        }
        return null;
    }

    private static final List<Material> TRANSPARENT = Arrays.asList(
            Material.AIR, Material.WATER, Material.LAVA, Material.TALL_GRASS, Material.SHORT_GRASS
    );

    private static boolean isSafe(Location loc) {
        Location feet = loc.clone();
        Location head = loc.clone().add(0, 1, 0);

        if (!TRANSPARENT.contains(feet.getBlock().getType()) && !TRANSPARENT.contains(head.getBlock().getType())) return false;

        return true;
    }

    private static boolean isDiagonalBlocked(Location loc) {
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return (world.getBlockAt(x + 1, y, z).getType().isSolid() && world.getBlockAt(x, y, z + 1).getType().isSolid())
                || (world.getBlockAt(x + 1, y, z).getType().isSolid() && world.getBlockAt(x, y, z - 1).getType().isSolid())
                || (world.getBlockAt(x - 1, y, z).getType().isSolid() && world.getBlockAt(x, y, z + 1).getType().isSolid())
                || (world.getBlockAt(x - 1, y, z).getType().isSolid() && world.getBlockAt(x, y, z - 1).getType().isSolid());
    }

    public static Location getLegitLocation(Location from, Location to) {
        if (!from.getWorld().equals(to.getWorld())) return from;

        Vector dir = to.toVector().subtract(from.toVector()).normalize();
        double distance = from.distance(to);
        World world = from.getWorld();
        Vector current = from.toVector().clone();

        Location lastSafe = from.clone();

        for (double d = 0; d <= distance; d += 0.25) {
            Location check = current.toLocation(world).add(0, 0.01, 0);
            if (isSafe(check) && !isDiagonalBlocked(check)) {
                lastSafe = check.getBlock().getLocation().add(0.5, 0, 0.5);
            } else {
                break;
            }
            current.add(dir.clone().multiply(0.25));
        }

        lastSafe.setYaw(from.getYaw());
        lastSafe.setPitch(from.getPitch());
        return lastSafe;
    }
}
