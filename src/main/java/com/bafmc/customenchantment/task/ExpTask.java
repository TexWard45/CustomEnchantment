package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.utils.ExpUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ExpTask extends BukkitRunnable {
    private static ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap();
    private static ConcurrentHashMap<String, Player> playerMap = new ConcurrentHashMap();

    public ExpTask() {
    }

    public void run() {
        giveAll();
    }

    public static void giveAll() {
        if (!map.isEmpty()) {
            Iterator var0 = map.keySet().iterator();

            while(var0.hasNext()) {
                String key = (String)var0.next();

                Player player = playerMap.get(key);
                ExpUtils.setTotalExperience(player, ExpUtils.getTotalExperience(player) + map.get(key));
            }

            map.clear();
            playerMap.clear();
        }
    }

    public static void giveExp(Player player, int amount) {
        Integer oldAmount = map.get(player.getName());
        oldAmount = oldAmount != null ? oldAmount : 0;
        map.put(player.getName(), oldAmount + amount);
        playerMap.put(player.getName(), player);
    }
}
