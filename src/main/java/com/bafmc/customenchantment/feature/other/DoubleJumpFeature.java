package com.bafmc.customenchantment.feature.other;

import com.bafmc.customenchantment.api.ParticleAPI;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class DoubleJumpFeature {
    public static void jump(Player player, double power, String particle) {
        Vector direction = player.getLocation().getDirection();

        int level = 0;
        PotionEffect effect = player.getPotionEffect(PotionEffectType.JUMP_BOOST);
        if (effect != null) {
            level = effect.getAmplifier();
        }

        direction.setY((0.5 + level * 0.1) * power);
        direction.setX(direction.getX() * 0.5);
        direction.setZ(direction.getZ() * 0.5);

        player.setVelocity(direction);

        ParticleAPI.sendParticle(player.getLocation(), particle);
    }
}
