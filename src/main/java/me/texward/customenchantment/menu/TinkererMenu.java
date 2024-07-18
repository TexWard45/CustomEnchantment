package me.texward.customenchantment.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.item.CEItem;
import me.texward.custommenu.menu.CMenuView;
import me.texward.texwardlib.util.InventoryUtils;

public class TinkererMenu extends MenuAbstract {
	public static final String MENU_NAME = "tinkerer";
	private static HashMap<String, TinkererMenu> map = new HashMap<String, TinkererMenu>();
	private static TinkererSettings settings;

	public static void setSettings(TinkererSettings settings) {
		TinkererMenu.settings = settings;
	}

	public static TinkererMenu putTinkererMenu(Player player, CMenuView cMenuView) {
		TinkererMenu menu = map.get(player.getName());

		if (menu == null) {
			menu = new TinkererMenu(cMenuView, player);
			map.put(player.getName(), menu);
		}

		return menu;
	}

	public static TinkererMenu getTinkererMenu(Player player) {
		return map.get(player.getName());
	}

	public static TinkererMenu removeTinkererMenu(Player player) {
		return map.remove(player.getName());
	}

	public enum TinkererAddReason {
		SUCCESS, NOT_SUPPORT_ITEM, FULL_SLOT;
	}

	public enum TinkererConfirmReason {
		SUCCESS, NOTHING;
	}

	public class TinkererData {
		private ItemStack itemStack;
		private TinkererReward reward;

		public TinkererData(ItemStack itemStack, TinkererReward reward) {
			this.itemStack = itemStack;
			this.reward = reward;
		}

		public ItemStack getItemStack() {
			return itemStack;
		}

		public void setItemStack(ItemStack itemStack) {
			this.itemStack = itemStack;
		}

		public TinkererReward getReward() {
			return reward;
		}

		public void setReward(TinkererReward reward) {
			this.reward = reward;
		}

	}

	private List<TinkererData> list = new ArrayList<TinkererData>();

	public TinkererMenu(CMenuView menuView, Player player) {
		super(menuView, player);
	}

	public TinkererAddReason addItem(ItemStack itemStack, CEItem ceItem) {
		if (list.size() >= settings.getSize()) {
			return TinkererAddReason.FULL_SLOT;
		}

		TinkererReward reward = settings.getReward(ceItem);
		if (reward == null) {
			return TinkererAddReason.NOT_SUPPORT_ITEM;
		}

		list.add(new TinkererData(itemStack, reward));
		updateMenu();
		return TinkererAddReason.SUCCESS;
	}

	public void updateMenu() {
		int size = settings.getSize();
		for (int i = 0; i < size; i++) {
			int tinkerSlot = settings.getTinkerSlot(i);
			int rewardSlot = settings.getRewardSlot(i);
			if (i < list.size()) {
				TinkererData data = list.get(i);

				menuView.setTemporaryItem(tinkerSlot, data.getItemStack());
				menuView.setTemporaryItem(rewardSlot, data.getReward().getItemStack());
			} else {
				menuView.removeTemporaryItem(tinkerSlot);
				menuView.removeTemporaryItem(rewardSlot);
			}
		}
	}

	public void returnItem(int slot) {
		int index = settings.getTinkerIndex(slot);
		if (index >= list.size()) {
			return;
		}

		TinkererData data = this.list.remove(index);
		InventoryUtils.addItem(player, Arrays.asList(data.getItemStack()));
		updateMenu();
	}

	public void removeTinkererBySlot(int slot) {
		removeTinkererByIndex(settings.getTinkerIndex(slot));
	}

	public void removeTinkererByIndex(int index) {
		if (index >= list.size()) {
			return;
		}
		list.remove(index);
		updateMenu();
	}

	public void returnItems() {
		List<ItemStack> itemStacks = new ArrayList<ItemStack>();
		for (TinkererData data : list) {
			itemStacks.add(data.getItemStack());
		}
		InventoryUtils.addItem(player, itemStacks);
	}

	public TinkererConfirmReason confirmTinkerer() {
		if (list.isEmpty()) {
			return TinkererConfirmReason.NOTHING;
		}

		for (TinkererData data : list) {
			data.getReward().getExecute().execute(player);
		}
		list.clear();
		updateMenu();
		return TinkererConfirmReason.SUCCESS;
	}

}
