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
import com.bafmc.customenchantment.item.outfit.CEOutfit;
import com.bafmc.customenchantment.item.sigil.CESigil;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerExtraSlot;
import org.bukkit.entity.Player;

import java.util.*;

public class CEExtraSlotTask extends PlayerPerTickTask {
    private CustomEnchantment plugin;
    private Set<String> inDisableExtraSlotSet = new HashSet<>();

    public CEExtraSlotTask(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getPlayerPerTick() {
        return 20;
    }

    public void run(Player player) {
        updateExtraSlot(player);
    }

    /**
     * Update artifact
     *
     * @param player
     * @return true if artifact is not exceed use amount
     */
    public boolean updateExtraSlot(Player player) {
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);

        Map<EquipSlot, CEWeaponAbstract> hotbarSlotMap = new LinkedHashMap<>();
        List<String> uniqueItemList = new ArrayList<>();

        for (EquipSlot equipSlot : EquipSlot.EXTRA_SLOT_ARRAY) {
            CEItem ceItem = cePlayer.getEquipment().getSlot(equipSlot, false);

            if (!(ceItem instanceof CEArtifact) && !(ceItem instanceof CESigil) && !(ceItem instanceof CEOutfit)) {
                continue;
            }

            if (ceItem.getData() == null) {
                continue;
            }

            hotbarSlotMap.put(equipSlot, (CEWeaponAbstract) ceItem);
            if (!uniqueItemList.contains(ceItem.getData().getPattern())) {
                uniqueItemList.add(ceItem.getData().getPattern());
            }
        }

        if (hotbarSlotMap.isEmpty()) {
            handleExtraSlotDeactivation(cePlayer);
            inDisableExtraSlotSet.remove(player.getName());
            return false;
        }

        if (hotbarSlotMap.size() <= CustomEnchantment.instance().getMainConfig().getMaxExtraSlotUseCount()) {
            if (uniqueItemList.size() != hotbarSlotMap.size()) {
                handleExtraSlotDeactivation(cePlayer);
                if (!inDisableExtraSlotSet.contains(player.getName())) {
                    CustomEnchantmentMessage.send(player, "ce-item.extra-slot.duplicate");
                    inDisableExtraSlotSet.add(player.getName());
                }
                return false;
            }

            handleExtraSlotActivation(cePlayer, hotbarSlotMap);
            inDisableExtraSlotSet.remove(player.getName());
            return true;
        }else {
            handleExtraSlotDeactivation(cePlayer);
            if (!inDisableExtraSlotSet.contains(player.getName())) {
                CustomEnchantmentMessage.send(player, "ce-item.extra-slot.exceed-use-amount");
                inDisableExtraSlotSet.add(player.getName());
            }
            return false;
        }
    }

    public void handleExtraSlotActivation(CEPlayer cePlayer, Map<EquipSlot, CEWeaponAbstract> map) {
        PlayerExtraSlot playerExtraSlot = cePlayer.getArtifact();
        Player player = cePlayer.getPlayer();

        PlayerExtraSlot.ExtraSlotDiff extraSlotDiff = playerExtraSlot.getDifferentExtraSlotMap(map);

        boolean change = false;

        if (!extraSlotDiff.getNotExistsExtraSlotMap().isEmpty()) {
            CECallerBuilder
                    .build(player)
                    .setCEType(CEType.EXTRA_SLOT_UNEQUIP)
                    .setWeaponMap(extraSlotDiff.getNotExistsExtraSlotMap())
                    .call();

            sendDeactiveExtraSlotMessage(player, extraSlotDiff.getNotExistsExtraSlotMap());

            change = true;
        }

        if (extraSlotDiff.isDifferent()) {
            CECallerBuilder
                .build(player)
                .setCEType(CEType.EXTRA_SLOT_EQUIP)
                .setWeaponMap(extraSlotDiff.getOnlyDifferentArtifactMap())
                .call();

            change = true;
            sendActiveExtraSlotMessage(player, extraSlotDiff.getOnlyDifferentArtifactMap());
        }

        if (change) {
            playerExtraSlot.updateArtifactDiff(extraSlotDiff);
        }
    }

    private void sendActiveExtraSlotMessage(Player player, Map<EquipSlot, CEWeaponAbstract> map) {
        for (EquipSlot equipSlot : map.keySet()) {
            CEWeaponAbstract ceArtifact = map.get(equipSlot);

            PlaceholderBuilder builder = PlaceholderBuilder.builder();
            builder.put("{display}", ItemStackUtils.getDisplayName(ceArtifact.getDefaultItemStack()));

            CustomEnchantmentMessage.send(player, "ce-item.extra-slot.active", builder.build());
        }
    }

    private void sendDeactiveExtraSlotMessage(Player player, Map<EquipSlot, CEWeaponAbstract> map) {
        for (EquipSlot equipSlot : map.keySet()) {
            CEWeaponAbstract ceArtifact = map.get(equipSlot);

            PlaceholderBuilder builder = PlaceholderBuilder.builder();
            builder.put("{display}", ItemStackUtils.getDisplayName(ceArtifact.getDefaultItemStack()));

            CustomEnchantmentMessage.send(player, "ce-item.extra-slot.deactive", builder.build());
        }
    }

    public void handleExtraSlotDeactivation(CEPlayer cePlayer) {
        PlayerExtraSlot playerExtraSlot = cePlayer.getArtifact();
        Player player = cePlayer.getPlayer();

        Map<EquipSlot, CEWeaponAbstract> artifactMap = playerExtraSlot.getPreviousExtraSlotActivateMap();

        if (!artifactMap.isEmpty()) {
            CECallerBuilder
                    .build(player)
                    .setCEType(CEType.EXTRA_SLOT_UNEQUIP)
                    .setWeaponMap(artifactMap)
                    .call();

            sendDeactiveExtraSlotMessage(player, artifactMap);

            playerExtraSlot.clear();
        }
    }
}
