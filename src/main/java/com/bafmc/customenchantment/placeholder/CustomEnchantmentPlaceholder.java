package com.bafmc.customenchantment.placeholder;

import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.FormatUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.attribute.AttributeCalculate;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.config.MainConfig;
import com.bafmc.customenchantment.enchant.EffectUtil;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.CEWeaponType;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerVanillaAttribute;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public class CustomEnchantmentPlaceholder extends PlaceholderExpansion {
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

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
        double level = itemStack.getEnchantmentLevel(Enchantment.SHARPNESS);
        //bonus damage by sharpness
        result += (level == 0.0) ? 0 : (0.5 * level + 0.5);
        return result;
    }

    public String onRequest(OfflinePlayer player, String params) {
        if (params.startsWith("attribute_value_")) {
            String[] split = params.split("_");

            String type = split[2].toUpperCase().replace("-", "_");
            String format = "%-7s";
            if (split.length > 3) {
                String last = split[split.length - 1];
                // Nếu last là số, thay số trong format, giữ nguyên %- và s, nhưng phải > 0
                boolean isNumber = true;
                for (int i = 0; i < last.length(); i++) {
                    if (!Character.isDigit(last.charAt(i))) {
                        isNumber = false;
                        break;
                    }
                }
                if (isNumber) {
                    int width = Integer.parseInt(last);
                    if (width > 0) {
                        format = "%-" + width + "s";
                    }else {
                        format = "%s";
                    }
                }
            }

            Attribute attributePlayer = EffectUtil.getAttributeType(type);

            CEPlayer cePlayer = CEAPI.getCEPlayer(player.getPlayer());
            PlayerVanillaAttribute attribute = cePlayer.getVanillaAttribute();
            List<AttributeModifier> attributeModifiers = attribute.getAttributeModifiers(attributePlayer);
            Player playerServer = Bukkit.getServer().getPlayer(player.getUniqueId());
            if(playerServer == null) return null;

            if (attributePlayer == Attribute.GENERIC_ATTACK_DAMAGE) {
                return String.format(format, FormatUtils.format(
                        AttributeCalculate.calculateAttributeModifier(
                                1,
                                attributeModifiers)));
            }

            if (attributePlayer == Attribute.GENERIC_ATTACK_SPEED) {
                String mode = split[3];

                // attack speed each second = 4.0 / attack speed
                if (mode.equals("0")) {
                    return String.format(format, FormatUtils.format(
                            AttributeCalculate.calculateAttributeModifier(
                                    4.0,
                                    attributeModifiers)));
                }
                // time take for each attack = 1 / attack speed
                else if (mode.equals("1")) {
                    return String.format(format, decimalFormat.format(1 /
                            AttributeCalculate.calculateAttributeModifier(
                                    4.0,
                                    attributeModifiers)));
                }
            }

            if (attributePlayer == Attribute.GENERIC_MOVEMENT_SPEED) {
                double value = AttributeCalculate.calculateAttributeModifier(
                        0.1,
                        attributeModifiers);

                if (value == 0.1) {
                    return String.format(format, "100%");
                }

                if (value > 0.1) {
                    String positiveColor = split[3];
                    return String.format(format, positiveColor + FormatUtils.format((value / 0.1) * 100.0) + "%");
                } else {
                    String negativeColor = split[4];
                    return String.format(format, negativeColor + FormatUtils.format((value / 0.1) * 100.0) + "%");
                }
            }

            if (attributePlayer == Attribute.GENERIC_MAX_HEALTH) {
                return String.format(format, FormatUtils.format(
                        AttributeCalculate.calculateAttributeModifier(
                                20.0,
                                attributeModifiers)));
            }

            if (attributePlayer == Attribute.GENERIC_SCALE) {
                return String.format(format, FormatUtils.format(
                        AttributeCalculate.calculateAttributeModifier(
                                1.0,
                                attributeModifiers)));
            }

            if (attributePlayer == Attribute.PLAYER_ENTITY_INTERACTION_RANGE) {
                return String.format(format, FormatUtils.format(
                        AttributeCalculate.calculateAttributeModifier(
                                3.0,
                                attributeModifiers)));
            }

            if (attributePlayer == Attribute.PLAYER_BLOCK_INTERACTION_RANGE) {
                return String.format(format, FormatUtils.format(
                        AttributeCalculate.calculateAttributeModifier(
                                4.5,
                                attributeModifiers)));
            }

            return String.format(format, FormatUtils.format(AttributeCalculate.calculateAttributeModifier(
                    0.0,
                    attributeModifiers)));
        }

        if (params.startsWith("custom_attribute_value_")) {
            String[] split = params.split("_");
            String type = split[3].toUpperCase().replace("-", "_");
            String format = "%-7s";
            if (split.length > 4) {
                String last = split[split.length - 1];
                boolean isNumber = true;
                for (int i = 0; i < last.length(); i++) {
                    if (!Character.isDigit(last.charAt(i))) {
                        isNumber = false;
                        break;
                    }
                }
                if (isNumber) {
                    int width = Integer.parseInt(last);
                    if (width > 0) {
                        format = "%-" + width + "s";
                    }else {
                        format = "%s";
                    }
                }
            }

            NMSAttributeType customAttributeType = CustomAttributeType.valueOf(type);

            CEPlayer cePlayer = CEAPI.getCEPlayer(player.getPlayer());

            if (customAttributeType instanceof CustomAttributeType customAttributeType1 && customAttributeType1.isPercent()) {
                return String.format(format, FormatUtils.format(cePlayer.getCustomAttribute().getValue(customAttributeType)) + "%");
            }

            return String.format(format, FormatUtils.format(cePlayer.getCustomAttribute().getValue(customAttributeType)));
        }

        if (params.startsWith("custom_attribute_value_percent_")) {
            String type = params.substring(31).toUpperCase().replace("-", "_");

            NMSAttributeType customAttributeType = CustomAttributeType.valueOf(type);

            CEPlayer cePlayer = CEAPI.getCEPlayer(player.getPlayer());
            return String.format("%-7s", FormatUtils.format(cePlayer.getCustomAttribute().getValue(customAttributeType) * 100.0) + "%");
        }
        if (params.startsWith("mainhand_icon")) {
            CEPlayer cePlayer = CEAPI.getCEPlayer(player.getPlayer());
            if (cePlayer == null) {
                return "";
            }

            CEWeaponAbstract ceWeaponAbstract = cePlayer.getEquipment().getSlot(EquipSlot.MAINHAND);
            if (ceWeaponAbstract == null) {
                return "";
            }

            CEWeaponType ceWeaponType = ceWeaponAbstract.getWeaponType();
            if (ceWeaponType == null) {
                return "";
            }

            MainConfig mainConfig = CustomEnchantment.instance().getMainConfig();
            return mainConfig.getWeaponIconMap().getOrDefault(ceWeaponType, "");
        }
        return null;
    }
}
