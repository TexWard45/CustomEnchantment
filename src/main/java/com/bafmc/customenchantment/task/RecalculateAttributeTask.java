package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.task.PlayerPerTickTask;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;

public class RecalculateAttributeTask extends PlayerPerTickTask {
    private CustomEnchantment plugin;

    public RecalculateAttributeTask(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getPlayerPerTick() {
        return 20;
    }

    @Override
    public void run(Player player) {
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        cePlayer.getCustomAttribute().recalculateAttribute();
    }
}
