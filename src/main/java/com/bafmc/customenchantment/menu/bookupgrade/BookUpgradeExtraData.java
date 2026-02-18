package com.bafmc.customenchantment.menu.bookupgrade;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ExtraData;
import com.bafmc.bukkit.utils.RandomRangeInt;
import com.bafmc.customenchantment.menu.data.BookData;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BookUpgradeExtraData extends ExtraData {

    private BookUpgradeSettings settings;

    private BookData mainBook;
    private boolean readyToUpgrade;

    private List<BookData> bookIngredients = new ArrayList<>();
    private RandomRangeInt randomXp = new RandomRangeInt(0);

    public BookUpgradeExtraData(BookUpgradeSettings settings) {
        this.settings = settings;
    }

    public boolean hasMainBook() {
        return mainBook != null;
    }

    public boolean hasBookIngredients() {
        return !bookIngredients.isEmpty();
    }

    public void clearMainBook() {
        mainBook = null;
    }

    public void clearBookIngredients() {
        bookIngredients.clear();
    }

    public void resetXp() {
        randomXp = new RandomRangeInt(0);
    }
}
