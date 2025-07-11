package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.task.PlayerPerTickTask;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CECallerBuilder;
import com.bafmc.customenchantment.enchant.CEType;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerArtifact;
import org.bukkit.entity.Player;

import java.util.*;

public class CEArtifactTask extends PlayerPerTickTask {
    private CustomEnchantment plugin;
    private Set<String> inDisableArtifactSet = new HashSet<>();

    public CEArtifactTask(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getPlayerPerTick() {
        return 20;
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

        for (EquipSlot equipSlot : EquipSlot.EXTRA_SLOT_ARRAY) {
            CEItem ceItem = cePlayer.getEquipment().getSlot(equipSlot, false);

            if (!(ceItem instanceof CEArtifact)) {
                continue;
            }

            if (ceItem.getData() == null) {
                continue;
            }

            hotbarSlotMap.put(equipSlot, (CEArtifact) ceItem);
            if (!uniqueArtifactList.contains(ceItem.getData().getPattern())) {
                uniqueArtifactList.add(ceItem.getData().getPattern());
            }
        }

        if (hotbarSlotMap.isEmpty()) {
            handleArtifactDeactivation(cePlayer, hotbarSlotMap);
            inDisableArtifactSet.remove(player.getName());
            return false;
        }

        if (hotbarSlotMap.size() <= CustomEnchantment.instance().getMainConfig().getMaxExtraSlotUseCount()) {
            if (uniqueArtifactList.size() != hotbarSlotMap.size()) {
                handleArtifactDeactivation(cePlayer, hotbarSlotMap);
                if (!inDisableArtifactSet.contains(player.getName())) {
                    CustomEnchantmentMessage.send(player, "ce-item.artifact.duplicate");
                    inDisableArtifactSet.add(player.getName());
                }
                return false;
            }

            handleArtifactActivation(cePlayer, hotbarSlotMap);
            inDisableArtifactSet.remove(player.getName());
            return true;
        }else {
            handleArtifactDeactivation(cePlayer, hotbarSlotMap);
            if (!inDisableArtifactSet.contains(player.getName())) {
                CustomEnchantmentMessage.send(player, "ce-item.artifact.exceed-use-amount");
                inDisableArtifactSet.add(player.getName());
            }
            return false;
        }
    }

    public void handleArtifactActivation(CEPlayer cePlayer, Map<EquipSlot, CEWeaponAbstract> map) {
        PlayerArtifact playerArtifact = cePlayer.getArtifact();
        Player player = cePlayer.getPlayer();

        PlayerArtifact.ArtifactDiff artifactDiff = playerArtifact.getDifferentArtifactMap(map);

        boolean change = false;

        if (!artifactDiff.getNotExistsArtifactMap().isEmpty()) {
            CECallerBuilder
                    .build(player)
                    .setCEType(CEType.EXTRA_SLOT_UNEQUIP)
                    .setWeaponMap(artifactDiff.getNotExistsArtifactMap())
                    .call();

            sendDeactiveArtifactMessage(player, artifactDiff.getNotExistsArtifactMap());

            change = true;
        }

        if (artifactDiff.isDifferent()) {
            CECallerBuilder
                .build(player)
                .setCEType(CEType.EXTRA_SLOT_EQUIP)
                .setWeaponMap(artifactDiff.getOnlyDifferentArtifactMap())
                .call();

            change = true;
            sendActiveArtifactMessage(player, artifactDiff.getOnlyDifferentArtifactMap());
        }

        if (change) {
            playerArtifact.updateArtifactDiff(artifactDiff);
        }
    }

    private void sendActiveArtifactMessage(Player player, Map<EquipSlot, CEWeaponAbstract> map) {
        for (EquipSlot equipSlot : map.keySet()) {
            CEArtifact ceArtifact = (CEArtifact) map.get(equipSlot);

            PlaceholderBuilder builder = PlaceholderBuilder.builder();
            builder.put("{display}", ItemStackUtils.getDisplayName(ceArtifact.getDefaultItemStack()));

            CustomEnchantmentMessage.send(player, "ce-item.artifact.active", builder.build());
        }
    }

    private void sendDeactiveArtifactMessage(Player player, Map<EquipSlot, CEWeaponAbstract> map) {
        for (EquipSlot equipSlot : map.keySet()) {
            CEArtifact ceArtifact = (CEArtifact) map.get(equipSlot);

            PlaceholderBuilder builder = PlaceholderBuilder.builder();
            builder.put("{display}", ItemStackUtils.getDisplayName(ceArtifact.getDefaultItemStack()));

            CustomEnchantmentMessage.send(player, "ce-item.artifact.deactive", builder.build());
        }
    }

    public void handleArtifactDeactivation(CEPlayer cePlayer, Map<EquipSlot, CEWeaponAbstract> map) {
        PlayerArtifact playerArtifact = cePlayer.getArtifact();
        Player player = cePlayer.getPlayer();

        Map<EquipSlot, CEWeaponAbstract> artifactMap = playerArtifact.getPreviousArtifactActivateMap();

        if (!artifactMap.isEmpty()) {
            CECallerBuilder
                    .build(player)
                    .setCEType(CEType.EXTRA_SLOT_UNEQUIP)
                    .setWeaponMap(artifactMap)
                    .call();

            sendDeactiveArtifactMessage(player, artifactMap);

            playerArtifact.clear();
        }
    }
}
