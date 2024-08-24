package me.texward.customenchantment.menu.bookcraft;

import lombok.Getter;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.api.Pair;
import me.texward.customenchantment.enchant.CESimple;
import me.texward.customenchantment.item.CEBook;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.menu.BookcraftMenu;
import me.texward.custommenu.menu.CItem;
import me.texward.texwardlib.util.InventoryUtils;
import me.texward.texwardlib.util.ItemStackUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class FastCraft {
    //For FastCraft
    @Getter
    private BookcraftMenu.BookData bookHighLevel = null;
    private HashMap<Integer, List<Pair<BookcraftMenu.BookData, Integer>>>  array = new HashMap<>();
    private HashMap<Integer, List<BookcraftMenu.BookData>> demoBook = new HashMap<>();
    private HashMap<Integer, List<BookcraftMenu.BookData>> usedBook = new HashMap<>();
    private List<Integer> cntBook = new ArrayList<>();
    private int amountBook;
    private int minLevel, maxLevel, pos;
    private Player player;
    //
    private BookcraftMenu bookcraftMenu;

    public FastCraft(BookcraftMenu bookcraftMenu) {
        this.bookcraftMenu = bookcraftMenu;
    }

    //FastCraft Zone
    private boolean compareCeBook(CESimple ceSimple1, CESimple ceSimple2) {
        //true -> equal, false -> different
        if(!ceSimple1.getName().equals(ceSimple2.getName())){
            return false;
        }

        if(ceSimple1.getLevel() != ceSimple2.getLevel()){
            return false;
        }

        if(ceSimple1.getSuccess().getValue() != ceSimple2.getSuccess().getValue()){
            return false;
        }

        if(ceSimple1.getDestroy().getValue() != ceSimple2.getDestroy().getValue()){
            return false;
        }

        return true;
    }

    private void clearBookFastCraft(int level){
        if(level < 1){
            return;
        }

        Player player = bookcraftMenu.getPlayer();

        if(!this.demoBook.get(level).isEmpty()){
            for(int i = 0 ; i < this.usedBook.get(level).size() ; ++i){
                BookcraftMenu.BookData bookData = this.usedBook.get(level).get(i);
                for(ItemStack item : player.getInventory().getContents()) {
                    CEItem ceItem = CEAPI.getCEItem(item);
                    if (ceItem == null || !(ceItem instanceof CEBook)) {
                        continue;
                    }
                    if(compareCeBook(bookData.getCESimple(), ((CEBook) ceItem).getData().getCESimple())) {
                        item.setAmount(0);
                        player.updateInventory();
                        break;
                    }
                }
                player.updateInventory();
            }
            clearBookFastCraft(level-1);
        }
    }

    public BookcraftMenu.BookcraftConfirmReason confirmUpgradeFastCraft() {
        Player player = bookcraftMenu.getPlayer();
        List<BookcraftMenu.BookData> list = bookcraftMenu.getList();
        String groupName = list.get(0).getCESimple().getCEEnchant().getGroupName();

        if (!BookcraftMenu.getSettings().payMoney(player, groupName, (double)(this.cntBook.get(this.pos)))) {
            return BookcraftMenu.BookcraftConfirmReason.NOT_ENOUGH_MONEY;
        }

        //Return CE in book1-slot and update menu
        bookcraftMenu.returnBook(0);
        bookcraftMenu.updateMenu();

        //Remove CE-used
        clearBookFastCraft(bookHighLevel.getCESimple().getLevel()-1);

        //Return CE-result
        ItemStack result = CEAPI.getCEBookItemStack(bookHighLevel.getCESimple());
        InventoryUtils.addItem(player, Arrays.asList(result));

        //Clear data
        this.usedBook.clear();
        this.array.clear();
        this.demoBook.clear();
        this.bookHighLevel = null;
        this.cntBook.clear();

        return BookcraftMenu.BookcraftConfirmReason.SUCCESS;
    }

    private BookcraftMenu.BookData upgradeBookData(BookcraftMenu.BookData book1, BookcraftMenu.BookData book2){
        CESimple ceSimple1 = book1.getCESimple();
        int success1 = ceSimple1.getSuccess().getValue();
        int destroy1 = ceSimple1.getDestroy().getValue();

        CESimple ceSimple2 = book2.getCESimple();
        int success2 = ceSimple2.getSuccess().getValue();
        int destroy2 = ceSimple2.getDestroy().getValue();

        CESimple ceSimpleResult = new CESimple(ceSimple1.getName(), ceSimple1.getLevel() + 1,
                Math.max(success1, success2), Math.min(destroy1, destroy2));

        return new BookcraftMenu.BookData(CEAPI.getCEBookItemStack(ceSimpleResult), ceSimpleResult);
    }

    private void addBookFastCraft(int level, int amount){
        //Sort bring high success ce to first
        Comparator<Pair<BookcraftMenu.BookData, Integer>> compare1 = new Comparator<Pair<BookcraftMenu.BookData, Integer>>() {
            public int compare(Pair<BookcraftMenu.BookData, Integer> book1, Pair<BookcraftMenu.BookData, Integer> book2) {
                if(book1.getKey().getCESimple().getSuccess().getValue() < book2.getKey().getCESimple().getSuccess().getValue()){
                    return 1;
                } else {
                    return -1;
                }
            }
        };

        //Sort bring low destroy ce to first
        Comparator<Pair<BookcraftMenu.BookData, Integer>> compare2 = new Comparator<Pair<BookcraftMenu.BookData, Integer>>() {
            public int compare(Pair<BookcraftMenu.BookData, Integer> book1, Pair<BookcraftMenu.BookData, Integer> book2) {
                if(book1.getKey().getCESimple().getDestroy().getValue() > book2.getKey().getCESimple().getDestroy().getValue()){
                    return 1;
                } else {
                    return -1;
                }
            }
        };

        //Sort choose high success %
        Collections.sort(this.array.get(level), compare1);
        BookcraftMenu.BookData bookData = this.array.get(level).get(0).getKey();
        CESimple ceSimple = bookData.getCESimple();
        this.demoBook.get(level).add(bookData);
        int success1 = ceSimple.getSuccess().getValue();
        int destroy1 = ceSimple.getDestroy().getValue();

        //Sort choose low destroy %
        Collections.sort(this.array.get(level), compare2);
        bookData = this.array.get(level).get(0).getKey();
        ceSimple = bookData.getCESimple();
        this.demoBook.get(level).add(bookData);
        int success2 = ceSimple.getSuccess().getValue();
        int destroy2 = ceSimple.getDestroy().getValue();

        //Create and push CE result into array
        CESimple ceSimpleResult = new CESimple(ceSimple.getName(),level + 1,
                Math.max(success1, success2), Math.min(destroy1, destroy2));
        for(int i = 0 ; i < amount ; ++i){
            this.array.get(level + 1).add(new Pair<BookcraftMenu.BookData, Integer>(new BookcraftMenu.BookData(CEAPI.getCEBookItemStack(ceSimpleResult), ceSimpleResult), 1));
        }
    }

    private void addUsedBookFastCraft(int level, int minlevel, int amount){
        if(amount > 0 && level >= minlevel){
            int quantity = amount;
            this.amountBook += (amount/2);
            BookcraftMenu.BookData bookData1 = this.demoBook.get(level).get(0);
            BookcraftMenu.BookData bookData2 = this.demoBook.get(level).get(1);

            Comparator<Pair<BookcraftMenu.BookData, Integer>> compare1 = new Comparator<Pair<BookcraftMenu.BookData, Integer>>() {
                public int compare(Pair<BookcraftMenu.BookData, Integer> book1, Pair<BookcraftMenu.BookData, Integer> book2) {
                    if(book1.getValue() > book2.getValue()){
                        return 1;
                    } else {
                        return -1;
                    }
                }
            };

            Collections.sort(this.array.get(level), compare1);
            amount = 0;

            HashMap<Integer, Boolean> visited = new HashMap<>();
            for(int i = 0 ; i < this.array.get(level).size() ; ++i){
                visited.put(i, false);
            }

            boolean bookData1Flag = false, bookData2Flag = false;

            for(int i = 0 ; i < this.array.get(level).size() && (!bookData1Flag || !bookData2Flag) ; ++i){
                BookcraftMenu.BookData bookData = this.array.get(level).get(i).getKey();
                int value = this.array.get(level).get(i).getValue();
                if(!bookData1Flag && compareCeBook(bookData.getCESimple(), bookData1.getCESimple())){
                    if(value == 1){
                        amount += 2;
                    } else {
                        this.usedBook.get(level).add(bookData);
                    }
                    --quantity;
                    visited.replace(i, true);
                    bookData1Flag = true;
                    continue;
                }
                if(!bookData2Flag && compareCeBook(bookData.getCESimple(), bookData2.getCESimple())){
                    if(value == 1){
                        amount += 2;
                    } else {
                        this.usedBook.get(level).add(bookData);
                    }
                    --quantity;
                    visited.replace(i, true);
                    bookData2Flag = true;
                }
            }
            for(int i = 0 ; i < this.array.get(level).size() && quantity > 0; ++i) {
                if (visited.get(i)) {
                    continue;
                }

                BookcraftMenu.BookData bookData = this.array.get(level).get(i).getKey();
                int value = this.array.get(level).get(i).getValue();
                if (value == 1) {
                    amount += 2;
                } else {
                    this.usedBook.get(level).add(bookData);
                }
                --quantity;
                visited.replace(i, true);
            }
            addUsedBookFastCraft(level-1, minlevel, amount);
        }
    }

    private int getPos(){
        int pos = -1;
        boolean start = false, end = false, flag = false;

        for(int i = 1 ; i <= maxLevel ; ++i){
            if(minLevel == i){
                flag = true;
            }
            if(!start && !this.demoBook.get(i).isEmpty()){
                start = true;
            }
            if(!end && start && this.demoBook.get(i).isEmpty()){
                end = true;
            }
            if(start && end){
                ++pos;
                start = end = false;
                if(flag){
                    if(!this.array.get(i).isEmpty()){
                        BookcraftMenu.BookData bookData1 = this.demoBook.get(i-1).get(0), bookData2 = this.demoBook.get(i-1).get(1);
                        this.bookHighLevel = upgradeBookData(bookData1, bookData2);
                    } else {
                        this.bookHighLevel = this.array.get(i).get(0).getKey();
                    }
                    break;
                }
            }
        }

        return pos;
    }

    public void fastCraft(Player player){
        this.player = player;
        if(this.bookcraftMenu.getList().size() == 1){
            this.bookHighLevel = null;
            BookcraftMenu.BookData book1 = bookcraftMenu.getList().get(0);
            String nameBook1 = book1.getCESimple().getName();
            this.minLevel = book1.getCESimple().getLevel();
            this.maxLevel = book1.getCESimple().getCEEnchant().getMaxLevel();

            //initial value
            for(int i = 1 ; i <= this.maxLevel ; i++){
                this.array.put(i, new ArrayList<>());
                this.demoBook.put(i, new ArrayList<>());
                this.usedBook.put(i, new ArrayList<>());
            }

            //Add CE in book1-slot to array
            this.array.get(this.minLevel).
                    add(new Pair<BookcraftMenu.BookData, Integer>(new BookcraftMenu.BookData(CEAPI.getCEBookItemStack(book1.getCESimple()), book1.getCESimple()),
                            0));

            for(ItemStack item : player.getInventory().getContents()){
                CEItem ceItem = CEAPI.getCEItem(item);
                if(ceItem == null || !(ceItem instanceof CEBook)){
                    continue;
                }
                String nameCEHave = ((CEBook)ceItem).getData().getCESimple().getName();
                int levelBookHave = ((CEBook)ceItem).getData().getCESimple().getLevel();
                if(!nameBook1.equals(nameCEHave)){
                    continue;
                }
                this.array.get(levelBookHave).
                        add(new Pair<BookcraftMenu.BookData, Integer>(new BookcraftMenu.BookData(item, ((CEBook)ceItem).getData().getCESimple()), 0));
            }

            int amount = 0;
            for(int i = 1 ; i <= this.maxLevel ; i++){
                int countBook = 0, soluong = this.array.get(i).size();
                if(soluong < 2 || i == this.maxLevel){
                    if((soluong == 1 || i == this.maxLevel) && amount > 0){
                        this.bookHighLevel = this.array.get(i).get(0).getKey();
                        this.amountBook = 0;
                        addUsedBookFastCraft(i-1, 1, 2);
                        this.cntBook.add(this.amountBook);
                    }
                    amount = 0;
                    continue;
                }
                if(this.array.get(i+1).size() == 0){
                    countBook = soluong/2;
                    addBookFastCraft(i, countBook);
                } else {
                    if(((soluong / 2) + this.array.get(i+1).size()) % 2 == 0){
                        countBook = (soluong / 2);
                        addBookFastCraft(i, countBook);
                    } else {
                        countBook = (soluong / 2) - 1;
                        addBookFastCraft(i, countBook);
                    }
                }
                amount += countBook;
            }

            this.pos = -1;
            if(this.bookHighLevel == null || this.demoBook.get(minLevel).isEmpty()){
                this.bookHighLevel = null;
            } else {
                this.pos = getPos();
            }

            if(this.bookHighLevel != null){
                //Update preview-slot
                bookcraftMenu.updateSlots("preview", CEAPI.getCEBookItemStack(this.bookHighLevel.getCESimple()));

                //Update price to upgrade CE in accept-slot
                CItem cItem = bookcraftMenu.getMenuView().getCMenu().getMenuItem().getCItemByName("accept");

                if (cItem == null) {
                    return;
                }

                ItemStack itemStack = cItem.getItemStack(player);

                String groupName = bookcraftMenu.getList().get(0).getCESimple().getCEEnchant().getGroupName();
                HashMap<String, String> placeholder = new HashMap<String, String>();
                placeholder.put("%money%", String.valueOf(BookcraftMenu.getSettings().getMoneyRequire(groupName)*(double)(this.cntBook.get(this.pos))));
                itemStack = ItemStackUtils.setItemStack(itemStack, placeholder);
                bookcraftMenu.updateSlots("accept", itemStack);
            }
        } else {
            this.array.clear();
            this.usedBook.clear();
            this.bookHighLevel = null;
            this.demoBook.clear();
            this.cntBook.clear();
        }
    }
}
