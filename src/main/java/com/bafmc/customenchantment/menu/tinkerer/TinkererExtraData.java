package com.bafmc.customenchantment.menu.tinkerer;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ExtraData;
import com.bafmc.customenchantment.constant.MessageKey;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Runtime data for Tinkerer menu - replaces static HashMap pattern
 */
@Getter
@Setter
public class TinkererExtraData extends ExtraData {

    /**
     * List of items and their corresponding rewards
     */
    private List<TinkererData> tinkererDataList = new ArrayList<>();

    /**
     * Tinkerer configuration settings
     */
    private TinkererSettings settings;

    /**
     * Inner class to hold item + reward pair
     */
    @Getter
    @Setter
    public static class TinkererData {
        private ItemStack itemStack;
        private TinkererReward reward;

        public TinkererData(ItemStack itemStack, TinkererReward reward) {
            this.itemStack = itemStack;
            this.reward = reward;
        }
    }

    /**
     * Result codes for adding items
     */
    public enum TinkererAddReason implements MessageKey {
        SUCCESS, NOT_SUPPORT_ITEM, FULL_SLOT;

        private static final String PREFIX = "menu.tinkerer.add-tinkerer.";

        @Override
        public String getKey() {
            return PREFIX + name().toLowerCase().replace("_", "-");
        }
    }

    /**
     * Result codes for confirming tinkerer
     */
    public enum TinkererConfirmReason implements MessageKey {
        SUCCESS, NOTHING;

        private static final String PREFIX = "menu.tinkerer.confirm.";

        @Override
        public String getKey() {
            return PREFIX + name().toLowerCase().replace("_", "-");
        }
    }
}
