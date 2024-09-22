package com.bafmc.customenchantment.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.attribute.AttributeCalculate;
import me.texward.customenchantment.enchant.EffectUtil;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.customenchantment.player.PlayerVanillaAttribute;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomEnchantmentPlaceholder extends PlaceholderExpansion {
    @NotNull
    public String getAuthor() {
        return "TexWard";
    }

    @NotNull
    public String getIdentifier() {
        return "customenchantment";
    }

    @NotNull
    public String getVersion() {
        return "1.0.0";
    }

    private double getAttackDamage(Player player){
        double result = 1.0;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        double level = itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        //bonus damage by sharpness
        result += (level == 0.0) ? 0 : (0.5 * level + 0.5);
        return result;
    }

    public String onRequest(OfflinePlayer player, String params) {
        if(params.startsWith("attribute_")){
            String cast = params.substring(10);
            if(cast.startsWith("value_")){
                String value = cast.substring(6);
                CEPlayer cePlayer = CEAPI.getCEPlayer(player.getPlayer());
                PlayerVanillaAttribute attribute = cePlayer.getVanillaAttribute();
                Attribute attributePlayer = EffectUtil.getAttributeType(value);
                List<AttributeModifier> attributeModifiers = attribute.getAttributeModifiers(attributePlayer);
                Player playerServer = Bukkit.getServer().getPlayer(player.getUniqueId());
                if(playerServer == null) return null;

                return switch (value) {
                    case "ATTACK_DAMAGE" -> String.format("%.2f",
                            AttributeCalculate.calculateAttributeModifier(
                                    getAttackDamage(playerServer),
                                    attributeModifiers));
                    case "ATTACK_SPEED" -> String.format("%.2f",
                            AttributeCalculate.calculateAttributeModifier(
                                    4.0,
                                    attributeModifiers));
                    case "KNOCKBACK_RESISTANCE", "LUCK", "ARMOR", "ARMOR_TOUGHNESS" -> String.format("%.2f",
                            AttributeCalculate.calculateAttributeModifier(
                                    0.0,
                                    attributeModifiers));
                    case "MAX_HEALTH" -> String.format("%.2f",
                            AttributeCalculate.calculateAttributeModifier(
                                    20.0,
                                    attributeModifiers));
                    case "MOVEMENT_SPEED" -> String.format("%.2f",
                            AttributeCalculate.calculateAttributeModifier(
                                    0.1,
                                    attributeModifiers));
                    default -> null;
                };
            }
        }
        return null;
    }
}
