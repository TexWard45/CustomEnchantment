package com.bafmc.customenchantment.feature.other;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.ParticleAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DashFeature {
    public static void dash(Player player, double power, String particleForward, String particleBackward) {
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        boolean movingForward = cePlayer.isMovingForward();

        Vector direction = player.getLocation().getDirection().clone();
        direction.setY(0);
        direction.normalize();

        String particle = particleForward;

        if (!movingForward) {
            direction.multiply(-1);
            particle = particleBackward;
        }

        direction.multiply(1.25 * power);

        player.setVelocity(direction);

        new DashParticle(player, particle).runTaskTimer(CustomEnchantment.instance(), 0, 2);
    }

    public static class DashParticle extends BukkitRunnable {
        private Player player;
        private String particle;

        public DashParticle(Player player, String particle) {
            this.player = player;
            this.particle = particle;
        }

        private int count = 0;
        @Override
        public void run() {
            if (count >= 10) {
                cancel();
                return;
            }

            double total = player.getVelocity().getX() * player.getVelocity().getZ();
            if (total == 0) {
                cancel();
                return;
            }

            ParticleAPI.sendParticle(player.getLocation(), particle);
            count++;
        }
    }

}
