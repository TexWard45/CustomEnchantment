package com.bafmc.customenchantment.item.artifact.group;

import com.bafmc.bukkit.utils.SparseMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CEArtifactGroup {
    private String name;
    private String display;
    private String itemDisplay;
    private List<String> itemLore;
    private SparseMap<String> levelColors;
}
