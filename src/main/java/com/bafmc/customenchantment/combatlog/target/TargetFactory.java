package com.bafmc.customenchantment.combatlog.target;

import com.bafmc.customenchantment.combatlog.target.list.PlayerTarget;
import org.bukkit.entity.Player;

public class TargetFactory {
    public static AbstractTarget<?> createTarget(Object target) {
        if (target instanceof Player) {
            return new PlayerTarget((Player) target);
        } else {
            return null;
        }
    }
}
