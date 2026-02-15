package com.bafmc.customenchantment.menu.bookcraft;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Settings for BookCraft menu - slot mappings and configuration
 * Loaded from book-craft.yml config file
 */
@Getter
@Setter
public class BookCraftSettings {

    /**
     * Book slot positions (2 slots for combining books)
     * Loaded from config: book-slots
     */
    private int[] bookSlots;

    /**
     * Preview slot position (shows result)
     * Loaded from config: preview-slot
     */
    private int previewSlot;

    /**
     * Accept button slot
     * Loaded from config: accept-slot
     */
    private int acceptSlot;

    /**
     * Constructor accepting values from config
     */
    public BookCraftSettings(List<Integer> bookSlotsList, int previewSlot, int acceptSlot) {
        this.bookSlots = bookSlotsList.stream().mapToInt(Integer::intValue).toArray();
        this.previewSlot = previewSlot;
        this.acceptSlot = acceptSlot;
    }

    /**
     * Number of book slots
     */
    public int getSize() {
        return bookSlots.length;
    }

    /**
     * Get book slot by index
     */
    public int getBookSlot(int index) {
        if (index < 0 || index >= bookSlots.length) {
            return -1;
        }
        return bookSlots[index];
    }

    /**
     * Get index from book slot number
     */
    public int getBookIndex(int slot) {
        for (int i = 0; i < bookSlots.length; i++) {
            if (bookSlots[i] == slot) {
                return i;
            }
        }
        return -1;
    }
}
