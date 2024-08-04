package me.texward.customenchantment.menu;

import java.util.*;

import me.texward.customenchantment.api.Pair;
import me.texward.customenchantment.item.CEBook;
import me.texward.customenchantment.item.CEItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CESimple;
import me.texward.custommenu.menu.CItem;
import me.texward.custommenu.menu.CMenuView;
import me.texward.texwardlib.util.InventoryUtils;
import me.texward.texwardlib.util.ItemStackUtils;

public class BookcraftMenu extends MenuAbstract {
	public static final String MENU_NAME = "bookcraft";
	private static HashMap<String, BookcraftMenu> map = new HashMap<String, BookcraftMenu>();
	private static BookcraftSettings settings;

	public static void setSettings(BookcraftSettings settings) {
		BookcraftMenu.settings = settings;
	}

	public static BookcraftMenu putBookCraftMenu(Player player, CMenuView cMenuView) {
		BookcraftMenu menu = map.get(player.getName());

		if (menu == null) {
			menu = new BookcraftMenu(cMenuView, player);
			map.put(player.getName(), menu);
		}
		return menu;
	}

	public static BookcraftMenu getBookCraftMenu(Player player) {
		return map.get(player.getName());
	}

	public static BookcraftMenu removeBookCraftMenu(Player player) {
		return map.remove(player.getName());
	}

	public enum BookAddReason {
		SUCCESS, DIFFERENT_ENCHANT, DIFFERENT_LEVEL, MAX_LEVEL, ENOUGH_BOOK;
	}

	public enum BookcraftConfirmReason {
		SUCCESS, NOT_ENOUGH_MONEY, NOT_ENOUGH_BOOK;
	}

	public class BookData {
		private ItemStack itemStack;
		private CESimple ceSimple;

		public BookData(ItemStack itemStack, CESimple ceSimple) {
			this.itemStack = itemStack;
			this.ceSimple = ceSimple;
		}

		public ItemStack getItemStack() {
			return itemStack;
		}

		public void setItemStack(ItemStack itemStack) {
			this.itemStack = itemStack;
		}

		public CESimple getCESimple() {
			return ceSimple;
		}

		public void setCESimple(CESimple ceSimple) {
			this.ceSimple = ceSimple;
		}
	}

	private List<BookData> list = new ArrayList<BookData>();

	//For FastCraft
	private BookData bookHighLevel = null;
	private HashMap<Integer, List<Pair<BookData, Integer>>>  array = new HashMap<>();
	private HashMap<Integer, List<BookData>> demoBook = new HashMap<>();
	private HashMap<Integer, List<BookData>> usedBook = new HashMap<>();
	private List<Integer> cntBook = new ArrayList<>();
	private int amountBook;
	private int minLevel, maxLevel, pos;
	//

	public BookcraftMenu(CMenuView menuView, Player player) {
		super(menuView, player);
	}

	public BookAddReason addBook(ItemStack itemStack, CESimple ceSimple) {
		if (ceSimple.getLevel() == ceSimple.getCEEnchant().getMaxLevel()) {
			return BookAddReason.MAX_LEVEL;
		}

		if (this.list.size() >= 2) {
			return BookAddReason.ENOUGH_BOOK;
		}

		if (!this.list.isEmpty()) {
			for (BookData bookData : list) {
				CESimple ceSimpleCompare = bookData.getCESimple();
				if (!ceSimpleCompare.getName().equals(ceSimple.getName())) {
					return BookAddReason.DIFFERENT_ENCHANT;
				}
				if (ceSimpleCompare.getLevel() != ceSimple.getLevel()) {
					return BookAddReason.DIFFERENT_LEVEL;
				}
			}
		}

		this.list.add(new BookData(itemStack, ceSimple));
		updateMenu();
		return BookAddReason.SUCCESS;
	}

	public void returnBook(String itemName) {
		if (itemName.equals("book1")) {
			returnBook(0);
		} else if (itemName.equals("book2")) {
			returnBook(1);
		}
	}

	public void returnBook(int index) {
		if (index >= list.size()) {
			return;
		}

		BookData data = this.list.remove(index);
		InventoryUtils.addItem(player, Arrays.asList(data.getItemStack()));
		updateMenu();
	}

	public void updateMenu() {
		updateBookIndex(0);
		updateBookIndex(1);
		updateBookPreview();
		updateAcceptSlot();
	}

	public void updateBookIndex(int index) {
		if (index < list.size()) {
			updateSlots("book" + (index + 1), list.get(index).getItemStack());
		} else {
			updateSlots("book" + (index + 1), null);
		}
	}

	public void updateAcceptSlot() {
		if (list.size() < 2) {
			updateSlots("remind", null);
			return;
		}

		CItem cItem = menuView.getCMenu().getMenuItem().getCItemByName("accept");

		if (cItem == null) {
			return;
		}
		ItemStack itemStack = cItem.getItemStack(player);

		String groupName = list.get(0).getCESimple().getCEEnchant().getGroupName();
		HashMap<String, String> placeholder = new HashMap<String, String>();
		placeholder.put("%money%", String.valueOf(settings.getMoneyRequire(groupName)));
		itemStack = ItemStackUtils.setItemStack(itemStack, placeholder);

		updateSlots("accept", itemStack);
	}

	public void updateBookPreview() {
		if (list.size() < 2) {
			updateSlots("preview", null);
			return;
		}

		CESimple ceSimple = getCESimpleResult();
		updateSlots("preview", CEAPI.getCEBookItemStack(ceSimple));
	}

	public CESimple getCESimpleResult() {
		if (list.size() < 2) {
			return null;
		}

		CESimple ceSimple1 = list.get(0).getCESimple();
		CESimple ceSimple2 = list.get(1).getCESimple();

		String name = ceSimple1.getName();
		int level = ceSimple1.getLevel() + 1;
		int success = Math.max(ceSimple1.getSuccess().getValue(), ceSimple2.getSuccess().getValue());
		int destroy = Math.min(ceSimple1.getDestroy().getValue(), ceSimple2.getDestroy().getValue());

		return new CESimple(name, level, success, destroy);
	}

	public BookcraftConfirmReason confirmUpgrade() {
		if (list.size() < 2) {
			if(this.list.size() == 1 && this.bookHighLevel != null) {
				return confirmUpgradeFastCraft();
			} else {
				return BookcraftConfirmReason.NOT_ENOUGH_BOOK;
			}
		}

		String groupName = list.get(0).getCESimple().getCEEnchant().getGroupName();

		if (!settings.payMoney(player, groupName)) {
			return BookcraftConfirmReason.NOT_ENOUGH_MONEY;
		}

		CESimple ceSimpleResult = getCESimpleResult();
		ItemStack result = CEAPI.getCEBookItemStack(ceSimpleResult);
		InventoryUtils.addItem(player, Arrays.asList(result));

		clearBooks();
		return BookcraftConfirmReason.SUCCESS;
	}

	public void returnItems() {
		List<ItemStack> itemStacks = new ArrayList<ItemStack>();
		for (BookData bookData : list) {
			itemStacks.add(bookData.getItemStack());
		}
		InventoryUtils.addItem(player, itemStacks);
	}

	public void clearBooks() {
		list.clear();
		updateMenu();
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

		if(!this.demoBook.get(level).isEmpty()){
			for(int i = 0 ; i < this.usedBook.get(level).size() ; ++i){
				BookData bookData = this.usedBook.get(level).get(i);
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

	public BookcraftConfirmReason confirmUpgradeFastCraft(){
		String groupName = list.get(0).getCESimple().getCEEnchant().getGroupName();

		if (!settings.payMoney(player, groupName, (double)(this.cntBook.get(this.pos)))) {
			return BookcraftConfirmReason.NOT_ENOUGH_MONEY;
		}

		//Return CE in book1-slot and update menu
		returnBook(0);
		updateMenu();

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

		return BookcraftConfirmReason.SUCCESS;
	}

	private void addBookFastCraft(int level, int amount){
		//Sort bring high success ce to first
		Comparator<Pair<BookData, Integer>> compare1 = new Comparator<Pair<BookData, Integer>>() {
			public int compare(Pair<BookData, Integer> book1, Pair<BookData, Integer> book2) {
				if(book1.getKey().getCESimple().getSuccess().getValue() < book2.getKey().getCESimple().getSuccess().getValue()){
					return 1;
				} else {
					return -1;
				}
			}
		};

		//Sort bring low destroy ce to first
		Comparator<Pair<BookData, Integer>> compare2 = new Comparator<Pair<BookData, Integer>>() {
			public int compare(Pair<BookData, Integer> book1, Pair<BookData, Integer> book2) {
				if(book1.getKey().getCESimple().getDestroy().getValue() > book2.getKey().getCESimple().getDestroy().getValue()){
					return 1;
				} else {
					return -1;
				}
			}
		};

		//Sort choose high success %
		Collections.sort(this.array.get(level), compare1);
		BookData bookData = this.array.get(level).get(0).getKey();
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
			this.array.get(level + 1).add(new Pair<BookData, Integer>(new BookData(CEAPI.getCEBookItemStack(ceSimpleResult), ceSimpleResult), 1));
		}
	}

	private void addUsedBookFastCraft(int level, int minlevel, int amount){
		if(amount > 0 && level >= minlevel){
			int quantity = amount;
			this.amountBook += (amount/2);
			BookData bookData1 = this.demoBook.get(level).get(0);
			BookData bookData2 = this.demoBook.get(level).get(1);

			Comparator<Pair<BookData, Integer>> compare1 = new Comparator<Pair<BookData, Integer>>() {
				public int compare(Pair<BookData, Integer> book1, Pair<BookData, Integer> book2) {
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

			for(int i = 0 ; i < this.array.get(level).size() ; ++i){
				BookData bookData = this.array.get(level).get(i).getKey();
				int value = this.array.get(level).get(i).getValue();
				if(!visited.get(i) && compareCeBook(bookData.getCESimple(), bookData1.getCESimple())){
					if(value == 1){
						amount += 2;
					} else {
						this.usedBook.get(level).add(bookData);
					}
					--quantity;
					visited.replace(i, true);
					continue;
				}
				if(!visited.get(i) && compareCeBook(bookData.getCESimple(), bookData2.getCESimple())){
					if(value == 1){
						amount += 2;
					} else {
						this.usedBook.get(level).add(bookData);
					}
					--quantity;
					visited.replace(i, true);
				}
			}

			for(int i = 0 ; i < this.array.get(level).size() ; ++i){

				if(visited.get(i)){
					continue;
				}

				if(quantity <= 0){
					break;
				}

				BookData bookData = this.array.get(level).get(i).getKey();
				int value = this.array.get(level).get(i).getValue();

				if(value == 1){
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
					this.bookHighLevel = this.array.get(i).get(0).getKey();
					break;
				}
			}
		}

		return pos;
	}

	public void fastCraft(Player player){
		if(this.list.size() == 1){
			this.bookHighLevel = null;
			BookData book1 = this.list.get(0);
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
					add(new Pair<BookData, Integer>(new BookData(CEAPI.getCEBookItemStack(book1.getCESimple()), book1.getCESimple()),
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
						add(new Pair<BookData, Integer>(new BookData(item, ((CEBook)ceItem).getData().getCESimple()), 0));
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
				updateSlots("preview", CEAPI.getCEBookItemStack(this.bookHighLevel.getCESimple()));

				//Update price to upgrade CE in accept-slot
				CItem cItem = menuView.getCMenu().getMenuItem().getCItemByName("accept");

				if (cItem == null) {
					return;
				}

				ItemStack itemStack = cItem.getItemStack(player);

				String groupName = this.list.get(0).getCESimple().getCEEnchant().getGroupName();
				HashMap<String, String> placeholder = new HashMap<String, String>();
				placeholder.put("%money%", String.valueOf(settings.getMoneyRequire(groupName)*(double)(this.cntBook.get(this.pos))));
				itemStack = ItemStackUtils.setItemStack(itemStack, placeholder);

				updateSlots("accept", itemStack);
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
