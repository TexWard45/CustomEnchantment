package com.bafmc.customenchantment.utils;

import com.google.common.base.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.LivingEntity;

public class DamageUtils {
    public static float getDamageAfterAbsorb(LivingEntity livingEntityBukkit, float damageAmount, DamageSource damageSourceBukkit, int armorPenetration) {
        net.minecraft.world.entity.LivingEntity armorWearer = ((org.bukkit.craftbukkit.entity.CraftLivingEntity) livingEntityBukkit).getHandle();
        net.minecraft.world.damagesource.DamageSource damageSource = ((org.bukkit.craftbukkit.damage.CraftDamageSource) damageSourceBukkit).getHandle();
        float armor = armorWearer.getArmorValue() * (1 - armorPenetration / 100.0F);
        float armorToughness =  (float) armorWearer.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        float f = 2.0F + armorToughness / 4.0F;
        float g = Mth.clamp(armor - damageAmount / f, armor * 0.2F, org.purpurmc.purpur.PurpurConfig.limitArmor ? 20F : Float.MAX_VALUE); // Purpur
        float h = g / 25.0F;
        ItemStack itemStack = damageSource.getWeaponItem();
        float i;
        if (itemStack != null && armorWearer.level() instanceof ServerLevel serverLevel) {
            i = Mth.clamp(EnchantmentHelper.modifyArmorEffectiveness(serverLevel, itemStack, armorWearer, damageSource, h), 0.0F, 1.0F);
        } else {
            i = h;
        }

        float k = 1.0F - i;
        if (k < 0.0F) {
            return 0.0F;
        }
        return damageAmount * k;
    }

    public static Function<Double, Double> getDamageAfterAbsorbFunction(LivingEntity livingEntityBukkit, float damageAmount, DamageSource damageSourceBukkit, int armorPenetration) {
        return (f) -> -(f - getDamageAfterAbsorb(livingEntityBukkit, damageAmount, damageSourceBukkit, armorPenetration));
    }
}
