package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.ConfigVariable;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CECallerBuilder;
import com.bafmc.customenchantment.enchant.CEType;
import com.bafmc.customenchantment.item.CEArtifact;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CEArtifactTask extends BukkitRunnable {
    private CustomEnchantment plugin;

    public CEArtifactTask(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            run(player);
        }
    }

    public void run(Player player) {
        updateArtifact(player);
    }

    /**
     * Update artifact
     *
     * @param player
     * @return true if artifact is not exceed use amount
     */
    public boolean updateArtifact(Player player) {
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);

        Map<EquipSlot, CEWeaponAbstract> hotbarSlotMap = new LinkedHashMap<>();
        List<String> uniqueArtifactList = new ArrayList<>();

        for (EquipSlot equipSlot : EquipSlot.HOTBAR_ARRAY) {
            CEItem ceItem = cePlayer.getSlot(equipSlot);

            if (!(ceItem instanceof CEArtifact)) {
                continue;
            }

            hotbarSlotMap.put(equipSlot, (CEArtifact) ceItem);
            if (!uniqueArtifactList.contains(ceItem.getData().getPattern())) {
                uniqueArtifactList.add(ceItem.getData().getPattern());
            }
        }

        if (hotbarSlotMap.isEmpty()) {
            return false;
        }

        if (hotbarSlotMap.size() <= ConfigVariable.MAX_ARTIFACT_USE_COUNT) {
            if (uniqueArtifactList.size() != hotbarSlotMap.size()) {
                if (handleArtifactDeactivation(cePlayer, hotbarSlotMap)) {
                    CustomEnchantmentMessage.send(player, "ce-item.artifact.duplicate");
                }
                return false;
            }

            if (handleArtifactActivation(cePlayer, hotbarSlotMap)) {
                CustomEnchantmentMessage.send(player, "ce-item.artifact.active");
            }
            return true;
        }else {
            if (handleArtifactDeactivation(cePlayer, hotbarSlotMap)) {
                CustomEnchantmentMessage.send(player, "ce-item.artifact.exceed-use-amount");
            }
            return false;
        }
    }

    public boolean handleArtifactActivation(CEPlayer cePlayer, Map<EquipSlot, CEWeaponAbstract> map) {
        Player player = cePlayer.getPlayer();

        if (!cePlayer.getCEManager().isCancelSlot(EquipSlot.HOTBAR_1)) {
            return false;
        }

        for (EquipSlot equipSlot : EquipSlot.HOTBAR_ARRAY) {
            cePlayer.getCEManager().setCancelSlot(equipSlot, "ArtifactTask", false);
        }

        CECallerBuilder
                .build(player)
                .setCEType(CEType.HOTBAR_HOLD)
                .setWeaponMap(map)
                .call();

        return true;
    }

    public boolean handleArtifactDeactivation(CEPlayer cePlayer, Map<EquipSlot, CEWeaponAbstract> map) {
        Player player = cePlayer.getPlayer();

        if (cePlayer.getCEManager().isCancelSlot(EquipSlot.HOTBAR_1)) {
            return false;
        }

        CECallerBuilder
                .build(player)
                .setCEType(CEType.HOTBAR_CHANGE)
                .setWeaponMap(map)
                .call();

        for (EquipSlot equipSlot : EquipSlot.HOTBAR_ARRAY) {
            cePlayer.getCEManager().setCancelSlot(equipSlot, "ArtifactTask", true);
        }

        return true;
    }
}
