package com.bafmc.customenchantment.menu.tinkerer;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ExtraData;
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
    public enum TinkererAddReason {
        SUCCESS, NOT_SUPPORT_ITEM, FULL_SLOT
    }

    /**
     * Result codes for confirming tinkerer
     */
    public enum TinkererConfirmReason {
        SUCCESS, NOTHING
    }
}
