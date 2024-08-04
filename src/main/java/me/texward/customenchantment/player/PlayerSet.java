package me.texward.customenchantment.player;

import me.texward.customenchantment.enchant.CEEnchant;
import me.texward.customenchantment.enchant.CESimple;
import me.texward.customenchantment.item.CEWeaponAbstract;
import me.texward.texwardlib.util.EquipSlot;

import java.util.*;

public class PlayerSet extends CEPlayerExpansion {
	public PlayerSet(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {
        PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
        storage.removeStartsWith("set_");
	}

	public void onQuit() {
        PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
        storage.removeStartsWith("set_");
	}

    public void onUpdate() {
        Map<EquipSlot, CEWeaponAbstract> slotMap = cePlayer.getSlotMap();

        PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();

        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Integer> highestSetLevel = new LinkedHashMap<>();

        for (Map.Entry<EquipSlot, CEWeaponAbstract> entry : slotMap.entrySet()) {
            EquipSlot slot = entry.getKey();

            CEWeaponAbstract weapon = entry.getValue();
            if (weapon == null) {
                continue;
            }

            List<CESimple> ceSimples = weapon.getWeaponEnchant().getCESimpleList();
            for (CESimple ceSimple : ceSimples) {
                CEEnchant ceEnchant = ceSimple.getCEEnchant();

                String setId = ceEnchant.getSet();
                if (setId == null) {
                    continue;
                }

                int level = ceSimple.getLevel();

                addSet(map, slot, setId, level);
                highestSetLevel.put(setId, Math.max(highestSetLevel.getOrDefault(setId, 0), level));
            }
        }

        updateHighestSetArmorLevel(map, highestSetLevel);

        storage.removeStartsWith("set_");
        storage.setAll(map);
    }

    public void updateHighestSetArmorLevel(Map<String, Object> map, Map<String, Integer> highestSetLevel) {
        for (Map.Entry<String, Integer> entry : highestSetLevel.entrySet()) {
            String setId = entry.getKey();
            int level = entry.getValue();

            for (int i = level; i >= 1; i--) {
                if ((int) map.getOrDefault(getSetPrefix(setId, "armor_point_" + i), 0) == 4) {
                    map.put(getSetPrefix(setId, "level"), i);
                    break;
                }
            }
        }
    }

    public void addSet(Map<String, Object> map, EquipSlot slot, String setId, int level) {
        if (slot == EquipSlot.HELMET || slot == EquipSlot.CHESTPLATE || slot == EquipSlot.LEGGINGS || slot == EquipSlot.BOOTS) {
            addSetToLevel(map, getSetPrefix(setId, "armor_point"), level);
            addSetPoint(map, getSetPrefix(setId, "armor_point"));
        }

        if (slot == EquipSlot.OFFHAND || slot == EquipSlot.MAINHAND) {
            addSetToLevel(map, getSetPrefix(setId, "weapon_point"), level);
            addSetPoint(map, getSetPrefix(setId, "weapon_point"));

            if (slot == EquipSlot.MAINHAND) {
                addSetToLevel(map, getSetPrefix(setId, "mainweapon_point"), level);
                addSetPoint(map, getSetPrefix(setId, "mainweapon_point"));
            }

            if (slot == EquipSlot.OFFHAND) {
                addSetToLevel(map, getSetPrefix(setId, "offweapon_point"), level);
                addSetPoint(map, getSetPrefix(setId, "offweapon_point"));
            }
        }

        addSetPoint(map, getSetPrefix(setId, "point"));
    }

    public void addSetToLevel(Map<String, Object> map, String path, int level) {
        for (int i = 1; i <= level; i++) {
            addSetPoint(map, path + "_" + i);
        }
    }

    public void addSetPoint(Map<String, Object> map, String path) {
        map.put(path, (int) map.getOrDefault(path, 0) + 1);
    }

    public void setSetPoint(Map<String, Object> map, String path, int point) {
        map.put(path, point);
    }

    public String getSetPrefix(String set, String path) {
        return "set_" + set + "_" + path;
    }
}
