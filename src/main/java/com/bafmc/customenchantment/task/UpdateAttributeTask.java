package com.bafmc.customenchantment.task;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.enchant.ModifyType;
import com.bafmc.customenchantment.event.CEPlayerStatsModifyEvent;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class UpdateAttributeTask extends BukkitRunnable {
    private CustomEnchantment plugin;

    public UpdateAttributeTask(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            try {
                run(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run(Player player) {
        if (player.isDead()) {
            return;
        }

        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        double aoeRange = cePlayer.getCustomAttribute().getValue(CustomAttributeType.AOE_RANGE);
        double aoeDamageRatio = cePlayer.getCustomAttribute().getValue(CustomAttributeType.AOE_DAMAGE_RATIO);

        net.minecraft.world.entity.player.Player nmsPlayer = ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle();

        if (aoeRange > 0) {
            nmsPlayer.aoeForceAttack = true;
            nmsPlayer.aoeAttackRangeScale = aoeRange;
        }else {
            nmsPlayer.aoeForceAttack = null;
            nmsPlayer.aoeAttackRangeScale = 1.0f;
        }

        if (aoeDamageRatio > 0) {
            nmsPlayer.aoeDamageRatio = (float) aoeDamageRatio / 100.0f;
        } else {
            nmsPlayer.aoeDamageRatio = null;
        }
    }
}
