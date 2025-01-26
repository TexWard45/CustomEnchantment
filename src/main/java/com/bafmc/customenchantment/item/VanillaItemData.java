package com.bafmc.customenchantment.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VanillaItemData extends CEItemData {
    private boolean weapon;
    private boolean origin;

    public VanillaItemData(String pattern, boolean updateItem, boolean origin) {
        super(pattern);
        this.weapon = updateItem;
        this.origin = origin;
    }
}
