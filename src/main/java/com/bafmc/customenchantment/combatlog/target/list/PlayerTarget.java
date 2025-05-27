package com.bafmc.customenchantment.combatlog.target.list;

import com.bafmc.customenchantment.combatlog.target.AbstractTarget;
import org.bukkit.entity.Player;

public class PlayerTarget extends AbstractTarget<Player> {
    public PlayerTarget(Player target) {
        super(target);
    }

    @Override
    public String getType() {
        return "PLAYER";
    }

    @Override
    public String getName() {
        return getTarget().getName();
    }
}
