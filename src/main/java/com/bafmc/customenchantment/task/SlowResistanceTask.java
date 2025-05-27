package com.bafmc.customenchantment.task;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.attribute.AttributeCalculate;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerCustomAttribute;
import com.bafmc.customenchantment.player.PlayerVanillaAttribute;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlowResistanceTask extends BukkitRunnable {
    private CustomEnchantment plugin;

    public SlowResistanceTask(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            try {
                update(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Map<String, Double> previousSpeedRatio = new HashMap<>();
    public static void update(Player player) {
        if (player.isDead()) {
            return;
        }

        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        PlayerCustomAttribute attribute = cePlayer.getCustomAttribute();
        PlayerVanillaAttribute vanillaAttribute = cePlayer.getVanillaAttribute();

        double slowResistancePercent = attribute.getValue(CustomAttributeType.SLOW_RESISTANCE) + attribute.getValue(CustomAttributeType.MAGIC_RESISTANCE);
        if (slowResistancePercent == 0) {
            vanillaAttribute.removeAttribute(Attribute.GENERIC_MOVEMENT_SPEED, CustomAttributeType.SLOW_RESISTANCE.getType());
            previousSpeedRatio.remove(player.getName());
            return;
        }

        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        List<AttributeModifier> list = new ArrayList<>(instance.getModifiers());

        AttributeModifier modifier = vanillaAttribute.getAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, CustomAttributeType.SLOW_RESISTANCE.getType());
        if (modifier != null) {
            list.remove(modifier);
        }

        // Example
        // current = 1.5
        // noNegative = 2.0
        // currentSlowRatio = 1.5 / 2.0 = 0.75
        double current = AttributeCalculate.calculateAttributeModifier(1, list);
        double noNegative = AttributeCalculate.calculateAttributeModifier(1, list, true);
        double currentSlowRatio = current / noNegative;
        double previousSlowRatio = previousSpeedRatio.getOrDefault(player.getName(), 1.0);

        if (currentSlowRatio == 1) {
            vanillaAttribute.removeAttribute(Attribute.GENERIC_MOVEMENT_SPEED, CustomAttributeType.SLOW_RESISTANCE.getType());
            previousSpeedRatio.remove(player.getName());
            return;
        }

        // Remove small difference
        if (Math.abs(currentSlowRatio - previousSlowRatio) < 0.01) {
            BigDecimal previousSlowRatioBigDecimal = new BigDecimal(previousSlowRatio).setScale(2, RoundingMode.FLOOR);
            if (previousSlowRatioBigDecimal.toString().equals("1.00")) {
                vanillaAttribute.removeAttribute(Attribute.GENERIC_MOVEMENT_SPEED, CustomAttributeType.SLOW_RESISTANCE.getType());
                previousSpeedRatio.remove(player.getName());
                return;
            }
            BigDecimal currentSlowRatioBigDecimal = new BigDecimal(currentSlowRatio).setScale(2, RoundingMode.FLOOR);
            if (currentSlowRatioBigDecimal.toString().equals("1.00")) {
                vanillaAttribute.removeAttribute(Attribute.GENERIC_MOVEMENT_SPEED, CustomAttributeType.SLOW_RESISTANCE.getType());
                previousSpeedRatio.remove(player.getName());
                return;
            }
            return;
        }

        // New slow ratio
        // slowResistancePercent = 50
        // newSlowRatio = 1 - 0.75 = 0.25 * 50 / 100 = 0.125 => 0.75 + 0.125 = 0.875
        double newSlowRatio = currentSlowRatio + (1 - currentSlowRatio) * slowResistancePercent / 100;
        // Add speed
        double addSpeed = newSlowRatio / currentSlowRatio - 1;

        // Update speed
        if (modifier == null || modifier.getAmount() != addSpeed) {
            vanillaAttribute.removeAttribute(Attribute.GENERIC_MOVEMENT_SPEED, CustomAttributeType.SLOW_RESISTANCE.getType());
            vanillaAttribute.addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, CustomAttributeType.SLOW_RESISTANCE.getType(), addSpeed, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

            previousSpeedRatio.put(player.getName(), newSlowRatio);
        }
    }
}
