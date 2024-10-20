package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.utils.EquipSlot;
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

import java.util.*;

public class CEArtifactTask extends BukkitRunnable {
    private CustomEnchantment plugin;
    private Set<String> inDisableArtifactSet = new HashSet<>();

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
            CEItem ceItem = cePlayer.getSlot(equipSlot, false);

            if (!(ceItem instanceof CEArtifact)) {
                cePlayer.setDisableSlot(equipSlot, false);
                continue;
            }

            hotbarSlotMap.put(equipSlot, (CEArtifact) ceItem);
            if (!uniqueArtifactList.contains(ceItem.getData().getPattern())) {
                uniqueArtifactList.add(ceItem.getData().getPattern());
            }
        }

        if (hotbarSlotMap.isEmpty()) {
            inDisableArtifactSet.remove(player.getName());
            return false;
        }

        if (hotbarSlotMap.size() <= CustomEnchantment.instance().getMainConfig().getMaxArtifactUseCount()) {
            if (uniqueArtifactList.size() != hotbarSlotMap.size()) {
                if (handleArtifactDeactivation(cePlayer, hotbarSlotMap) && !inDisableArtifactSet.contains(player.getName())) {
                    CustomEnchantmentMessage.send(player, "ce-item.artifact.duplicate");
                    inDisableArtifactSet.add(player.getName());
                }
                return false;
            }

            if (handleArtifactActivation(cePlayer, hotbarSlotMap)) {
                CustomEnchantmentMessage.send(player, "ce-item.artifact.active");
                inDisableArtifactSet.remove(player.getName());
            }
            return true;
        }else {
            if (handleArtifactDeactivation(cePlayer, hotbarSlotMap) && !inDisableArtifactSet.contains(player.getName())) {
                CustomEnchantmentMessage.send(player, "ce-item.artifact.exceed-use-amount");
                inDisableArtifactSet.add(player.getName());
            }
            return false;
        }
    }

    public boolean handleArtifactActivation(CEPlayer cePlayer, Map<EquipSlot, CEWeaponAbstract> map) {
        Player player = cePlayer.getPlayer();

        Map<EquipSlot, CEWeaponAbstract> oldDisableMap = new LinkedHashMap<>();
        for (EquipSlot equipSlot : EquipSlot.HOTBAR_ARRAY) {
            if (cePlayer.isDisableSlot(equipSlot)) {
                oldDisableMap.put(equipSlot, cePlayer.getSlot(equipSlot, false));
            }

            cePlayer.setDisableSlot(equipSlot, false);
        }

        if (oldDisableMap.isEmpty()) {
            return false;
        }

        CECallerBuilder
                .build(player)
                .setCEType(CEType.HOTBAR_HOLD)
                .setWeaponMap(oldDisableMap)
                .call();

        return true;
    }

    public boolean handleArtifactDeactivation(CEPlayer cePlayer, Map<EquipSlot, CEWeaponAbstract> map) {
        Player player = cePlayer.getPlayer();

        Map<EquipSlot, CEWeaponAbstract> newDisableMap = new LinkedHashMap<>();
        for (EquipSlot equipSlot : map.keySet()) {
            if (cePlayer.isDisableSlot(equipSlot)) {
                continue;
            }
            newDisableMap.put(equipSlot, cePlayer.getSlot(equipSlot, false));
        }

        CECallerBuilder
                .build(player)
                .setCEType(CEType.HOTBAR_CHANGE)
                .setWeaponMap(newDisableMap)
                .call();

        for (EquipSlot equipSlot : map.keySet()) {
            cePlayer.setDisableSlot(equipSlot, true);
        }

        return true;
    }
}
