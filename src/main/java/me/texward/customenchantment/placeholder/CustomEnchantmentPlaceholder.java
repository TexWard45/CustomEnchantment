package me.texward.customenchantment.placeholder;

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

    private double getArmor(Player player){
        double result = 0.0;

        String helmet = (player.getInventory().getHelmet() != null)
                ? player.getInventory().getHelmet().getType().toString()
                : null;
        String chestplate = (player.getInventory().getChestplate() != null)
                ? player.getInventory().getChestplate().getType().toString()
                : null;
        String leggings = (player.getInventory().getLeggings() != null)
                ? player.getInventory().getLeggings().getType().toString()
                : null;
        String boots = (player.getInventory().getBoots() != null)
                ? player.getInventory().getBoots().getType().toString()
                : null;

        if(helmet != null){
            int index = helmet.indexOf('_');
            String type = helmet.substring(0, index);
            result += switch (type){
                case "TURTLE", "GOLDEN", "CHAINMAIL", "IRON" -> 2.0;
                case "LEATHER" -> 1.0;
                case "DIAMOND", "NETHERITE" -> 3.0;
                default -> 0.0;
            };
        }

        if(chestplate != null){
            int index = chestplate.indexOf('_');
            String type = chestplate.substring(0, index);
            result += switch (type){
                case "LEATHER" -> 3.0;
                case "GOLDEN", "CHAINMAIL" -> 5.0;
                case "IRON" -> 6.0;
                case "DIAMOND", "NETHERITE" -> 8.0;
                default -> 0.0;
            };
        }

        if(leggings != null){
            int index = leggings.indexOf('_');
            String type = leggings.substring(0, index);
            result += switch (type){
                case "LEATHER" -> 2.0;
                case "GOLDEN" -> 3.0;
                case "CHAINMAIL" -> 4.0;
                case "IRON" -> 5.0;
                case "DIAMOND", "NETHERITE" -> 6.0;
                default -> 0.0;
            };
        }

        if(boots != null){
            int index = boots.indexOf('_');
            String type = boots.substring(0, index);
            result += switch (type){
                case "LEATHER", "GOLDEN", "CHAINMAIL" -> 1.0;
                case "IRON" -> 2.0;
                case "DIAMOND", "NETHERITE" -> 3.0;
                default -> 0.0;
            };
        }

        return result;
    }

    private double getArmorToughness(Player player){
        double result = 0.0;

        String helmet = (player.getInventory().getHelmet() != null)
                ? player.getInventory().getHelmet().getType().toString()
                : null;
        String chestplate = (player.getInventory().getChestplate() != null)
                ? player.getInventory().getChestplate().getType().toString()
                : null;
        String leggings = (player.getInventory().getLeggings() != null)
                ? player.getInventory().getLeggings().getType().toString()
                : null;
        String boots = (player.getInventory().getBoots() != null)
                ? player.getInventory().getBoots().getType().toString()
                : null;

        if(helmet != null){
            int index = helmet.indexOf('_');
            String type = helmet.substring(0, index);
            result += switch (type){
                case "DIAMOND" -> 2.0;
                case "NETHERITE" -> 3.0;
                default -> 0.0;
            };
        }

        if(chestplate != null){
            int index = chestplate.indexOf('_');
            String type = chestplate.substring(0, index);
            result += switch (type){
                case "DIAMOND" -> 2.0;
                case "NETHERITE" -> 3.0;
                default -> 0.0;
            };
        }

        if(leggings != null){
            int index = leggings.indexOf('_');
            String type = leggings.substring(0, index);
            result += switch (type){
                case "DIAMOND" -> 2.0;
                case "NETHERITE" -> 3.0;
                default -> 0.0;
            };
        }

        if(boots != null){
            int index = boots.indexOf('_');
            String type = boots.substring(0, index);
            result += switch (type){
                case "DIAMOND" -> 2.0;
                case "NETHERITE" -> 3.0;
                default -> 0.0;
            };
        }

        return result;
    }

    private double getAttackSpeed(Player player){
        double result = 0.0;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        String itemName = itemStack.getType().toString();

        if(itemName.equals("TRIDENT")){
            return 1.1;
        }

        int index = itemName.indexOf('_');
        //Not found => other
        if(index == -1){
            return 4.0;
        }

        String type = itemName.substring(0, index);
        itemName = itemName.substring(index+1);

        result = switch (itemName){
            case "SWORD" -> 1.6;
            case "SHOVEL" -> 1.0;
            case "PICKAXE" -> 1.2;
            case "AXE" -> switch (type){
                case "WOOD", "STONE" -> 0.8;
                case "IRON" -> 0.9;
                case "GOLDEN", "DIAMOND", "NETHERITE" -> 1.0;
                default -> result;
            };
            case "HOE" -> switch (type){
                case "WOOD", "GOLDEN" -> 1.0;
                case "STONE" -> 2.0;
                case "IRON" -> 3.0;
                case "DIAMOND", "NETHERITE" -> 4.0;
                default -> result;
            };
            default -> result;
        };
        return result;
    }

    private double getAttackDamage(Player player){
        double result = 0.0;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        String itemName = itemStack.getType().toString();

        if(itemName.equals("TRIDENT")){
            return 9.0;
        }

        int index = itemName.indexOf('_');
        //Not find => other
        if(index == -1){
            return 1.0;
        }

        String type = itemName.substring(0, index);
        itemName = itemName.substring(index+1);
        double bonusDamage = 0.5 * itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) + 0.5;

        result = switch (itemName) {
            case "SWORD" -> switch (type) {
                case "WOOD", "GOLDEN" -> 4.0 + bonusDamage;
                case "STONE" -> 5.0 + bonusDamage;
                case "IRON" -> 6.0 + bonusDamage;
                case "DIAMOND" -> 7.0 + bonusDamage;
                case "NETHERITE" -> 8.0 + bonusDamage;
                default -> result;
            };
            case "SHOVEL" -> switch (type) {
                case "WOOD", "GOLDEN" -> 2.5 + bonusDamage;
                case "STONE" -> 3.5 + bonusDamage;
                case "IRON" -> 4.5 + bonusDamage;
                case "DIAMOND" -> 5.5 + bonusDamage;
                case "NETHERITE" -> 6.5 + bonusDamage;
                default -> result;
            };
            case "PICKAXE" -> switch (type) {
                case "WOOD", "GOLDEN" -> 2.0 + bonusDamage;
                case "STONE" -> 3.0 + bonusDamage;
                case "IRON" -> 4.0 + bonusDamage;
                case "DIAMOND" -> 5.0 + bonusDamage;
                case "NETHERITE" -> 6.0 + bonusDamage;
                default -> result;
            };
            case "AXE" -> switch (type) {
                case "WOOD", "GOLDEN" -> 7.0 + bonusDamage;
                case "STONE", "IRON", "DIAMOND" -> 9.0 + bonusDamage;
                case "NETHERITE" -> 10.0 + bonusDamage;
                default -> result;
            };
            default -> 1.0 + bonusDamage;
        };
        return result;
    }

    public String onRequest(OfflinePlayer player, String params) {
//        if (params.equals("player")) {
//            return player.getName();
//        }
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
                switch (value){
                    case "ATTACK_DAMAGE":
                        return String.format("%.2f",
                                AttributeCalculate.calculateAttributeModifier(
                                        getAttackDamage(playerServer),
                                        attributeModifiers));
                    case "ATTACK_SPEED":
                        return String.format("%.2f",
                                AttributeCalculate.calculateAttributeModifier(
                                        getAttackSpeed(playerServer),
                                        attributeModifiers));
                    //Null
                    case "FOLLOW_RANGE":
                        return String.format("%.2f",
                                AttributeCalculate.calculateAttributeModifier(
                                        32.0,
                                        attributeModifiers));
                    case "KNOCKBACK_RESISTANCE", "LUCK":
                        return String.format("%.2f",
                                AttributeCalculate.calculateAttributeModifier(
                                        0.0,
                                        attributeModifiers));
                    case "ARMOR":
                        return String.format("%.2f",
                                AttributeCalculate.calculateAttributeModifier(
                                        getArmor(playerServer),
                                        attributeModifiers));
                    case "ARMOR_TOUGHNESS":
                        return String.format("%.2f",
                                AttributeCalculate.calculateAttributeModifier(
                                        getArmorToughness(playerServer),
                                        attributeModifiers));
                    case "MAX_HEALTH":
                        return String.format("%.2f",
                                AttributeCalculate.calculateAttributeModifier(
                                        20.0,
                                        attributeModifiers));
                    case "MOVEMENT_SPEED":
                        return String.format("%.2f",
                                AttributeCalculate.calculateAttributeModifier(
                                        0.1,
                                        attributeModifiers));
                    //Null
                    case "FLYING_SPEED":
                        return String.format("%.2f",
                                AttributeCalculate.calculateAttributeModifier(
                                        0.4,
                                        attributeModifiers));
                }
            }
        }
        return null;
    }
}
