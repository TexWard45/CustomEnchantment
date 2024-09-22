package com.bafmc.customenchantment.api;

import com.github.fierioziy.particlenativeapi.api.particle.ParticleList_1_13;
import com.github.fierioziy.particlenativeapi.api.particle.ParticleList_1_8;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import com.github.fierioziy.particlenativeapi.plugin.ParticleNativePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.lang.reflect.Field;

public class ParticleAPI {
    private static ParticleType getParticleType(String name) {
        com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI api = ParticleNativePlugin.getAPI();

        String version = "1_8";

        if (name.contains(":")) {
            version = name.split(":")[0];
            name = name.split(":")[1];
        }

        if (version.equals("1_13")) {
            ParticleList_1_13 particles = api.LIST_1_13;

            try {
                Field field = particles.getClass().getField(name);
                return (ParticleType) field.get(particles);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
                return null;
            }
        }

        try {
            ParticleList_1_8 particles = api.LIST_1_8;
            Field field = particles.getClass().getField(name);
            return (ParticleType) field.get(particles);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Send a particle to a location
     *
     * @param location
     * @param particle
     * @return
     */
    public static void sendParticle(Location location, String particle) {
        ParticleType particleType = getParticleType(particle);

        if (particleType == null) {
            return;
        }

        particleType.packet(true, location, 0, 0, 0, 0.1, 1).sendInRadiusTo(Bukkit.getOnlinePlayers(), 32);
    }
}
