package com.bafmc.customenchantment.api;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemPacketAPI {
    public static void sendItem(Player player, int windowId, int vanillaSlot, ItemStack itemStack) {
        try {
            if (vanillaSlot >= 0 && vanillaSlot <= 8) {
                vanillaSlot += 36;
            }else if (vanillaSlot >= 36 && vanillaSlot <= 39) {
                vanillaSlot = 44 - vanillaSlot;
            }else if (vanillaSlot == 40) {
                vanillaSlot = 45;
            }

            ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

            // Send packet
            ClientboundContainerSetSlotPacket packet = new ClientboundContainerSetSlotPacket(
                    windowId, // container id: 0 = player inventory
                    0,        // stateId (can be 0 if you don't track)
                    vanillaSlot,
                    CraftItemStack.asNMSCopy(itemStack)
            );

            nmsPlayer.connection.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}