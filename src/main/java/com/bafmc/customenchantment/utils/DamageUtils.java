package com.bafmc.customenchantment.utils;

import com.google.common.base.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
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

        boolean newArmorMechanic = armorWearer.level().purpurConfig.newArmorMechanic; // Purpur
        if (newArmorMechanic) {
            float effectiveArmor = Math.max(0.0F, armor);
            float damageMultiplier = 100.0F / (100.0F + effectiveArmor);
            return damageAmount * damageMultiplier;
        }

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

    public static Function<Double, Double> getArmorDamageModifier(LivingEntity livingEntityBukkit, float damageAmount, DamageSource damageSourceBukkit, int armorPenetration) {
        return (f) -> -(f - getDamageAfterAbsorb(livingEntityBukkit, damageAmount, damageSourceBukkit, armorPenetration));
    }

    /*
     * This method is used to calculate the damage after the resistance modifier is applied.
     * @param livingEntityBukkit The entity that has the resistance modifier.
     * @param damageSourceBukkit The source of the damage.
     * @param resistanceBonus The bonus resistance that the entity has. Values range from 0 to 1. 0.2 is 20% resistance bonus.
     */
    public static Function<Double, Double> getResistanceDamageModifier(LivingEntity livingEntityBukkit, DamageSource damageSourceBukkit, float resistanceBonus) {
        return (f) -> {
            net.minecraft.world.entity.LivingEntity livingEntity = ((org.bukkit.craftbukkit.entity.CraftLivingEntity) livingEntityBukkit).getHandle();
            net.minecraft.world.damagesource.DamageSource damageSource = ((org.bukkit.craftbukkit.damage.CraftDamageSource) damageSourceBukkit).getHandle();

            if (!damageSource.is(DamageTypeTags.BYPASSES_EFFECTS) && livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE) && !damageSource.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
                float i = (livingEntity.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1) * 5;
                float bonus = 25 * resistanceBonus;
                float j = Math.max(25 - i - bonus, 0);
                float f1 = f.floatValue() * (float) j;
                return -(f - (f1 / 25.0F));
            }else if (resistanceBonus > 0) {
                float bonus = 25 * resistanceBonus;
                float j = Math.max(25 - bonus, 0);
                float f1 = f.floatValue() * (float) j;
                return -(f - (f1 / 25.0F));
            }
            return -0.0;
        };
    }
}
