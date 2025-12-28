package com.bafmc.customenchantment.api;

import com.github.fierioziy.particlenativeapi.api.particle.ParticleList_1_13;
import com.github.fierioziy.particlenativeapi.api.particle.ParticleList_1_8;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import com.github.fierioziy.particlenativeapi.plugin.ParticleNativePlugin;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    public static List<ParticleOptions> getDustColor(String colorStr, float size) {
        List<ParticleOptions> particles = new ArrayList<>();

        String[] colorArgs = colorStr.split(",");

        for (int i = 0; i < colorArgs.length; i++) {
            Particle.DustOptions data = null;
            java.awt.Color color = java.awt.Color.decode(colorArgs[i]);
            try {
                data = new Particle.DustOptions(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()), size);
            } catch (Exception e) {

            }
            Vector3f vector = new Vector3f(data.getColor().getRed() / 255f, data.getColor().getGreen() / 255f, data.getColor().getBlue() / 255f);
            particles.add(new DustParticleOptions(vector, data.getSize()));
        }
        return particles;
    }
}
