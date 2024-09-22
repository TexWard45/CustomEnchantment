package com.bafmc.customenchantment.task;

import com.bafmc.customenchantment.listener.EntityListener;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ArrowTask extends BukkitRunnable {
    private static List<Entity> entities = new ArrayList<Entity>();

    public void run() {
        for (Entity entity : entities) {
            EntityListener.removeMainArrowShootList(entity.getUniqueId().toString());

            entity.remove();
        }

        entities.clear();
    }

    public static void addEntity(Entity entity) {
        entities.add(entity);
    }
}
