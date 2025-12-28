package com.bafmc.customenchantment.enchant;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.enchant.condition.*;
import com.bafmc.customenchantment.enchant.effect.*;

public class EnchantModule extends PluginModule<CustomEnchantment> {
    public EnchantModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        setupCondition();
        setupEffect();
    }

    public void setupCondition() {
        new ConditionEquipSlot().register();
        new ConditionEntityType().register();
        new ConditionExp().register();
        new ConditionFood().register();
        new ConditionFoodPercent().register();
        new ConditionHasEnemy().register();
        new ConditionHealth().register();
        new ConditionHealthPercent().register();
        new ConditionHold().register();
        new ConditionLevel().register();
        new ConditionOxygen().register();
        new ConditionOxygenPercent().register();
        new ConditionNumberStorage().register();
        new ConditionTextStorage().register();
        new ConditionWorldTime().register();
        new ConditionDamageCause().register();
        new ConditionHasDamageCause().register();
        new ConditionOnFire().register();
        new ConditionItemConsume().register();
        new ConditionOutOfSight().register();
        new ConditionCanAttack().register();
        new ConditionFactionRelation().register();
        new ConditionAllowFlight().register();
        new ConditionInFactionTerriority().register();
        new ConditionHasNearbyEnemy().register();
        new ConditionInCombat().register();
        new ConditionOnGround().register();
        new ConditionActiveEquipSlot().register();
        new ConditionOnlyActiveEquip().register();
        new ConditionFakeSource().register();
    }

    public void setupEffect() {
        new EffectAddForeverPotion().register();
        new EffectRemovePotion().register();
        new EffectAddPotion().register();
        new EffectRemoveForeverPotion().register();
        new EffectMessage().register();
        new EffectRemoveTask().register();
        new EffectRemoveTaskAsync().register();
        new EffectEnableMultipleArrow().register();
        new EffectDisableMultipleArrow().register();
        new EffectAddAttribute().register();
        new EffectRemoveAttribute().register();
        new EffectAddAutoAttribute().register();
        new EffectRemoveAutoAttribute().register();
        new EffectHealth().register();
        new EffectTrueDamage().register();
        new EffectFood().register();
        new EffectExp().register();
        new EffectOxygen().register();
        new EffectAbsorptionHeart().register();
        new EffectDurability().register();
        new EffectOnFire().register();
        new EffectPull().register();
        new EffectLightning().register();
        new EffectTeleport().register();
        new EffectPacketParticle().register();
        new EffectActiveAbility().register();
        new EffectDeactiveAbility().register();
        new EffectActiveDash().register();
        new EffectDeactiveDash().register();
        new EffectActiveDoubleJump().register();
        new EffectDeactiveDoubleJump().register();
        new EffectActiveFlash().register();
        new EffectDeactiveFlash().register();
        new EffectActiveEquipSlot().register();
        new EffectDeactiveEquipSlot().register();
        new EffectAddMobBonus().register();
        new EffectRemoveMobBonus().register();
        new EffectAddBlockBonus().register();
        new EffectRemoveBlockBonus().register();
        new EffectPacketRedstoneParticle().register();
        new EffectPacketSpiralRedstoneParticle().register();
        new EffectPacketCircleRedstoneParticle().register();
        new EffectExplosion().register();
        new EffectPlaySound().register();
        new EffectNumberStorage().register();
        new EffectTextStorage().register();
        new EffectAddCustomAttribute().register();
        new EffectRemoveCustomAttribute().register();
        new EffectAddFurnaceMining().register();
        new EffectRemoveFurnaceMining().register();
        new EffectAddExplosionMining().register();
        new EffectRemoveExplosionMining().register();
        new EffectAddVeinMining().register();
        new EffectRemoveVeinMining().register();
        new EffectAddRandomPotion().register();
        new EffectRemoveRandomPotion().register();
        new EffectAddBlockDropBonusMining().register();
        new EffectRemoveBlockDropBonusMining().register();
        new EffectEnableTelepathy().register();
        new EffectDisableTelepathy().register();
        new EffectFixedPull().register();
        new EffectSummonGuard().register();
        new EffectSummonBabyZombieGuard().register();
        new EffectRemoveGuard().register();
        new EffectSetBlock().register();
        new EffectAdvancedMessage().register();
        new EffectDealDamage().register();
        new EffectSetFlight().register();
        new EffectShootArrow().register();
        new EffectWindCharge().register();
        new EffectSummonCustomGuard().register();
        new EffectEnableAutoSell().register();
        new EffectDisableAutoSell().register();
        new EffectBlockForeverPotion().register();
        new EffectUnblockForeverPotion().register();
        new EffectSetStaffParticle().register();
        new EffectRemoveStaffParticle().register();
    }
}
