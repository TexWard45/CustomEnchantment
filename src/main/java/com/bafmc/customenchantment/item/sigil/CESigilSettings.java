package com.bafmc.customenchantment.item.sigil;

import com.bafmc.bukkit.utils.SparseMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CESigilSettings {
    @Getter
    private static CESigilSettings settings;

    public static void setSettings(CESigilSettings settings) {
        CESigilSettings.settings = settings;
    }

    private String itemDisplay;
    private List<String> itemLore;
    private SparseMap<String> levelColors;
}