package com.bafmc.customenchantment.menu.ceanvil;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ExtraData;
import com.bafmc.customenchantment.constant.MessageKey;
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

    public enum CEAnvilAddReason implements MessageKey {
        SUCCESS, ALREADY_HAS_SLOT1, ALREADY_HAS_SLOT2, NOT_SUITABLE;

        private static final String PREFIX = "menu.ce-anvil.add-item.";

        @Override
        public String getKey() {
            return PREFIX + name().toLowerCase().replace("_", "-");
        }
    }

    public CEAnvilExtraData(CEAnvilSettings settings) {
        this.settings = settings;
    }
}
