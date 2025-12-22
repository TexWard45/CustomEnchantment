package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.config.data.ExtraSlotSettingsData;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.outfit.CEOutfit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerEquipment extends CEPlayerExpansion {
    private ConcurrentHashMap<EquipSlot, CEWeaponAbstract> slotMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<EquipSlot, Boolean> disableSlotMap = new ConcurrentHashMap<>();
    private Map<Integer, CEItem> ceItemCacheMap = new HashMap<>();
    @Getter
    @Setter
    private CEWeaponAbstract wings;
    @Getter
    @Setter
    private ItemStack offhandItemStack;

    public PlayerEquipment(CEPlayer cePlayer) {
        super(cePlayer);
    }

    public void onJoin() {
        this.updateSlot();
        this.sortExtraSlot();
    }

    public void onQuit() {
        this.saveSlot();
        this.slotMap.clear();
    }

    public void saveSlot() {
//        List<EquipSlot> playerEquipSlot = new ArrayList<>();
//        playerEquipSlot.addAll(List.of(EquipSlot.HAND_ARRAY));
//        playerEquipSlot.addAll(List.of(EquipSlot.ARMOR_ARRAY));
//        playerEquipSlot.addAll(List.of(EquipSlot.HOTBAR_ARRAY));

        List<EquipSlot> configEquipSlot = new ArrayList<>();
        configEquipSlot.addAll(List.of(EquipSlot.EXTRA_SLOT_ARRAY));
        for (EquipSlot slot : configEquipSlot) {
            CEWeaponAbstract weapon = getSlot(slot);
            if (weapon == null) {
                getCEPlayer().getStorage().getConfig().set("equipment." + slot.name(), null);
            }else {
                getCEPlayer().getStorage().getConfig().set("equipment." + slot.name(), weapon.getDefaultItemStack());
            }
        }

        if (offhandItemStack != null && offhandItemStack.getType() != Material.AIR) {
            getCEPlayer().getStorage().getConfig().set("equipment.OFFHAND", offhandItemStack);
        } else {
            getCEPlayer().getStorage().getConfig().set("equipment.OFFHAND", null);
        }
    }

    public void updateSlot() {
        List<EquipSlot> playerEquipSlot = new ArrayList<>();
        playerEquipSlot.addAll(List.of(EquipSlot.HAND_ARRAY));
        playerEquipSlot.addAll(List.of(EquipSlot.ARMOR_ARRAY));
        playerEquipSlot.addAll(List.of(EquipSlot.HOTBAR_ARRAY));

        for (EquipSlot slot : playerEquipSlot) {
            if (slot == EquipSlot.OFFHAND) {
                ItemStack offhand = getCEPlayer().getStorage().getConfig().getItemStack("equipment.OFFHAND");
                if (offhand != null && offhand.getType() != Material.AIR) {
                    this.offhandItemStack = offhand;
                }else {
                    this.offhandItemStack = null;
                }
                continue;
            }

            CEWeaponAbstract weapon = CEWeapon.getCEWeapon(slot.getItemStack(player));
            if (weapon == null) {
                continue;
            }
            setSlot(slot, weapon);
        }

        List<EquipSlot> configEquipSlot = new ArrayList<>();
        configEquipSlot.addAll(List.of(EquipSlot.EXTRA_SLOT_ARRAY));
        for (EquipSlot slot : configEquipSlot) {
            ItemStack itemStack = getCEPlayer().getStorage().getConfig().getItemStack("equipment." + slot.name());
            if (itemStack == null) {
                continue;
            }

            CEWeaponAbstract weapon = CEWeapon.getCEWeapon(itemStack);
            if (weapon == null) {
                continue;
            }
            setSlot(slot, weapon);
        }
    }

    public void sortExtraSlot() {
        Map<String, ExtraSlotSettingsData> artifactSettingsDataMap = CustomEnchantment.instance().getMainConfig().getExtraSlotSettingMap();

        for (ExtraSlotSettingsData extraSlotSettingsData : artifactSettingsDataMap.values()) {
            List<EquipSlot> extraSlotList = new ArrayList<>();
            extraSlotList.addAll(extraSlotSettingsData.getSlots());
            for (int i = 0; i < extraSlotList.size(); i++) {
                EquipSlot slot = extraSlotList.get(i);
                if (getSlot(slot) != null) {
                    continue;
                }
                for (int j = i + 1; j < extraSlotList.size(); j++) {
                    EquipSlot nextSlot = extraSlotList.get(j);
                    CEWeaponAbstract weapon = getSlot(nextSlot);
                    if (weapon == null) {
                        continue;
                    }
                    setSlot(slot, weapon);
                    setSlot(nextSlot, null);
                    break;
                }
            }
        }
    }

    public CEWeaponAbstract getSlot(EquipSlot slot) {
        return getSlot(slot, true);
    }

    public CEWeaponAbstract getSlot(EquipSlot slot, boolean check) {
        if (!slotMap.containsKey(slot)) {
            return null;
        }
        if (check && disableSlotMap.containsKey(slot)) {
            return null;
        }
        return slotMap.get(slot);
    }

    public void setSlot(EquipSlot slot, CEWeaponAbstract weapon) {
        setSlot(slot, weapon, false);
    }

    public void setSlot(EquipSlot slot, CEWeaponAbstract weapon, boolean updateConfig) {
        if (weapon != null) {
            slotMap.put(slot, weapon);
            if (updateConfig) {
                getCEPlayer().getStorage().getConfig().set("equipment." + slot.name(), weapon.getDefaultItemStack());
            }
        } else {
            slotMap.remove(slot);
            if (updateConfig) {
                getCEPlayer().getStorage().getConfig().set("equipment." + slot.name(), null);
            }
        }

        getCEPlayer().getSet().onUpdate();
    }

    public void setSkinIndex(String outfit, String customType, int index) {
        getCEPlayer().getStorage().getConfig().set("outfit." + outfit + ".skin." + customType + ".index", index);
    }

    public int getSkinIndex(String outfit, String customType) {
        return getCEPlayer().getStorage().getConfig().getInt("outfit." + outfit + ".skin." + customType + ".index", 0);
    }

    public void setDisableSlot(EquipSlot slot, boolean disable) {
        if (disable) {
            disableSlotMap.put(slot, true);
        } else {
            disableSlotMap.remove(slot);
        }
    }

    public boolean isDisableSlot(EquipSlot slot) {
        return disableSlotMap.containsKey(slot);
    }

    public Map<EquipSlot, CEWeaponAbstract> getSlotMap() {
        return getSlotMap(true);
    }

    public Map<EquipSlot, CEWeaponAbstract> getSlotMap(boolean check) {
        if (!check) {
            return new LinkedHashMap<>(slotMap);
        }
        Map<EquipSlot, CEWeaponAbstract> map = new LinkedHashMap<>();
        for (EquipSlot slot : slotMap.keySet()) {
            if (disableSlotMap.containsKey(slot)) {
                continue;
            }
            map.put(slot, slotMap.get(slot));
        }
        return map;
    }

    public void setCEItemCache(int slotIndex, CEItem ceItem) {
        ceItemCacheMap.put(slotIndex, ceItem);
    }

    public CEItem getCEItemCache(int slotIndex) {
        return ceItemCacheMap.get(slotIndex);
    }

    public void removeCEItemCache(int slotIndex) {
        ceItemCacheMap.remove(slotIndex);
    }

    public CEOutfit getCEOutfit() {
        EquipSlot outfitExtraSlot = CustomEnchantment.instance().getMainConfig().getOutfitExtraSlot();
        return (CEOutfit) cePlayer.getEquipment().getSlot(outfitExtraSlot);
    }

    public boolean hasWings() {
        return wings != null;
    }

    public boolean hasOffhandItemStack() {
        return this.offhandItemStack != null && this.offhandItemStack.getType() != Material.AIR;
    }
}