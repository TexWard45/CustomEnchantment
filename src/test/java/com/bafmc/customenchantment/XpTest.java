package com.bafmc.customenchantment;

import com.bafmc.bukkit.utils.ExpUtils;
import com.bafmc.bukkit.utils.SoundUtils;
import com.google.common.base.Function;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

public class XpTest {
    @Test
    public void main() {
//        for (int i = 1; i <= 100; i++) {
//            int xp = ExpUtils.getExpToLevel(i * 20);
//
//            System.out.println(xp);
//        }

        Map<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> overriddenFunctions = new EnumMap<>(EntityDamageEvent.DamageModifier.class);

        overriddenFunctions.put(EntityDamageEvent.DamageModifier.ABSORPTION, (f) -> -(f - 1.0));
        overriddenFunctions.put(EntityDamageEvent.DamageModifier.RESISTANCE, (f) -> -(f - 1.0));
        overriddenFunctions.put(EntityDamageEvent.DamageModifier.BLOCKING, (f) -> -(f - 1.0));

        System.out.println(overriddenFunctions.keySet());
    }
}
