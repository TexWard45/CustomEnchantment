package com.bafmc.customenchantment.attribute;

import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;

public class CENMSAttributeType {
    public static final NMSAttributeType DODGE_TYPE = (new NMSAttributeType("DODGE_CHANCE", "custom:player.dodge_chance")).register();
    public static final NMSAttributeType CRITICAL_CHANCE = (new NMSAttributeType("CRITICAL_CHANCE", "custom:player.critical_chance")).register();
    public static final NMSAttributeType CRITICAL_DAMAGE = (new NMSAttributeType("CRITICAL_DAMAGE", "custom:player.critical_damage")).register();
    public static final NMSAttributeType HEALTH_REGENERATION = (new NMSAttributeType("HEALTH_REGENERATION", "custom:player.health_regeneration")).register();
    public static final NMSAttributeType DAMAGE_REDUCTION = (new NMSAttributeType("DAMAGE_REDUCTION", "custom:player.damage_reduction")).register();
    public static final NMSAttributeType LIFE_STEAL = (new NMSAttributeType("LIFE_STEAL", "custom:player.life_steal")).register();

    public static void init() {
    }
}
