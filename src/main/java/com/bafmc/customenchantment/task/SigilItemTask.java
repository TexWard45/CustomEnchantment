package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.task.PlayerPerTickTask;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.ItemPacketAPI;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.sigil.CESigil;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class SigilItemTask extends PlayerPerTickTask {
    private Map<String, Map<Integer, Boolean>> cacheUpdateMap = new HashMap<>();

    public int getPlayerPerTick() {
        return 5;
    }

    public void run(Player player) {
        PlayerInventory inventory = player.getInventory();
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);

        EquipSlot sigilExtraSlot = CustomEnchantment.instance().getMainConfig().getSigilExtraSlot();

        CEWeaponAbstract ceWeaponAbstract = cePlayer.getEquipment().getSlot(sigilExtraSlot);
        CESigil ceSigil = (CESigil) ceWeaponAbstract;
        boolean existsSigil = ceSigil != null;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null) {
                continue;
            }

            boolean updateOriginal = false;
            if (!existsSigil) {
                updateOriginal = true;
            }else {
                if (itemStack.hasDisplayName()) {
                    updateOriginal = true;
                } else {
                    String newDisplayName = ceSigil.getSpecialDisplay(itemStack);
                    if (newDisplayName != null && !newDisplayName.isEmpty()) {
                        ItemStack newItemStack = itemStack.clone();
                        newItemStack.setDisplayName(newDisplayName);

                        ItemPacketAPI.sendItem(player, 0, i, newItemStack);
                        putCacheUpdate(player.getName(), i, true);
                    } else {
                        updateOriginal = true;
                    }
                }
            }

            if (updateOriginal && isCacheUpdate(player.getName(), i)) {
                ItemPacketAPI.sendItem(player, 0, i, itemStack);
                putCacheUpdate(player.getName(), i, false);
            }
        }
    }

    public Map<Integer, Boolean> getCacheUpdateMap(String playerName) {
        return cacheUpdateMap.computeIfAbsent(playerName, k -> new HashMap<>());
    }

    public void putCacheUpdate(String playerName, int slot, boolean update) {
        getCacheUpdateMap(playerName).put(slot, update);
    }

    public boolean isCacheUpdate(String playerName, int slot) {
        Map<Integer, Boolean> map = getCacheUpdateMap(playerName);
        return map.getOrDefault(slot, false);
    }
}
