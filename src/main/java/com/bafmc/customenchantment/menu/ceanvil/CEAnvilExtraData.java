package com.bafmc.customenchantment.menu.ceanvil;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ExtraData;
import com.bafmc.customenchantment.menu.ceanvil.handler.Slot2Handler;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CEAnvilExtraData extends ExtraData {

    private Slot2Handler activeHandler;
    private AnvilItemData itemData1;
    private AnvilItemData itemData2;
    private CEAnvilSettings settings;

    public enum CEAnvilAddReason {
        SUCCESS, ALREADY_HAS_SLOT1, ALREADY_HAS_SLOT2, NOT_SUITABLE
    }

    public CEAnvilExtraData(CEAnvilSettings settings) {
        this.settings = settings;
    }
}
