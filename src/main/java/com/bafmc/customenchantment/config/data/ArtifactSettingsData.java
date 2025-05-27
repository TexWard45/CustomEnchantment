package com.bafmc.customenchantment.config.data;

import com.bafmc.bukkit.config.annotation.Configuration;
import com.bafmc.bukkit.config.annotation.ListType;
import com.bafmc.bukkit.config.annotation.Path;
import com.bafmc.bukkit.utils.EquipSlot;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Configuration
@Getter
@ToString
public class ArtifactSettingsData {
    @Path
    private int maxCount;
    @Path
    private List<String> groups;
    @Path
    @ListType(EquipSlot.class)
    private List<EquipSlot> slots;

    public EquipSlot getSlot(int index) {
        return slots.get(index);
    }
}
